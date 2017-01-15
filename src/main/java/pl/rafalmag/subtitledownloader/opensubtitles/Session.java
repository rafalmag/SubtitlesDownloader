package pl.rafalmag.subtitledownloader.opensubtitles;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderProperties;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.LoginAndPassword;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.MovieEntity;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SubtitleLanguage;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.TitleUtils;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Login and logout internally handled by class.
 */
@ThreadSafe
@Singleton
public class Session {

    /**
     * See restrictions here:
     * {@see http://trac.opensubtitles.org/projects/opensubtitles/wiki/DevReadFirst}
     */
    private static final String USER_AGENT = "SubtitlesDownloader v1.3";

    @InjectLogger
    private Logger LOG;

    @Inject
    private SubtitlesDownloaderProperties subtitlesDownloaderProperties;

    private final XmlRpcClient client;

    // write by login and logout
    // however also used by bunch of other methods,
    // race conditions between logout and those methods are not handled!
    @GuardedBy("lock")
    private volatile String token;

    private final Lock lock = new ReentrantLock();

    public Session() {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL("https://api.opensubtitles.org:443/xml-rpc"));
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        }
        client = new XmlRpcClient();
        client.setConfig(config);
    }

    public void login() throws SessionException {
        LoginAndPassword loginAndPassword = subtitlesDownloaderProperties.getLoginAndPassword();
        login(loginAndPassword);
    }

    public void login(LoginAndPassword loginAndPassword) throws SessionException {
        //TODO language hardcoded!
        login(loginAndPassword.getLogin(), loginAndPassword.getPasswordMd5(), "en", USER_AGENT);
    }

    public void login(String userName, String password, String language, String userAgent) throws SessionException {
        boolean lockSuccess;
        try {
            lockSuccess = lock.tryLock(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Thread interrupted while trying to get lock before login", e);
        }
        if (lockSuccess) {
            try {
                loginInternals(userName, password, language, userAgent);
            } finally {
                lock.unlock();
            }
        } else {
            throw new SessionException("Login failed, because could not acquire lock within 10 sec");
        }
    }

    private void loginInternals(String userName, String password, String language, String userAgent) throws SessionException {
        if (token != null) {
            logout();
        }
        Object[] params = new Object[]{userName, password, language, userAgent};
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> execute = (Map<String, Object>) client.execute("LogIn", params);
            LOG.debug("login as '{}' response: {}", userName, execute);
            String status = (String) execute.get("status");
            if (status.contains("401 Unauthorized")) {
                throw new SessionException("Bad login or password");
            }
            if (!status.contains("OK")) {
                throw new SessionException("could not login because of wrong status: " + status);
            }
            token = (String) execute.get("token");
        } catch (XmlRpcException e) {
            throw new SessionException("could not login because of " + e.getMessage(), e);
        }
    }

    public boolean logout() {
        if (token == null) {
            return true;
        }
        boolean lockSuccess;
        try {
            lockSuccess = lock.tryLock(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Thread interrupted while trying to get lock before logout", e);
        }
        if (lockSuccess) {
            try {
                return logoutInternals();
            } finally {
                lock.unlock();
            }
        } else {
            throw new IllegalStateException("Logout failed, because could not acquire lock within 10 sec");
        }
    }

    private boolean logoutInternals() {
        Object[] params = new Object[]{token};
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> execute = (Map<String, Object>) client.execute("LogOut", params);
            LOG.debug("logout response: {}", execute);
            String status = (String) execute.get("status");
            if (!status.contains("OK")) {
                LOG.error("could not logout because of wrong status " + status);
                return false;
            }
            token = null;
            return true;
        } catch (XmlRpcException e) {
            LOG.error("could not logout because of " + e.getMessage(), e);
            return false;
        }
    }

    public List<SearchSubtitlesResult> searchSubtitlesBy(String movieHash, Long movieByteSize)
            throws SubtitlesDownloaderException {
        checkLogin();
        Object[] params = new Object[]{
                token,
                new Object[]{ImmutableMap.of("sublanguageid", getLanguage(), "moviehash", movieHash, "moviebytesize",
                        movieByteSize.toString())}};
        return searchSubtitles(params, "hash");
    }

    private void checkLogin() throws SessionException {
        if (token == null) {
            login();
        }
    }

    private String getLanguage() {
        return subtitlesDownloaderProperties.getSubtitlesLanguage().getId();
    }

    public List<SearchSubtitlesResult> searchSubtitlesBy(int imdbId)
            throws SubtitlesDownloaderException {
        checkLogin();
        Object[] params = new Object[]{token,
                new Object[]{ImmutableMap.of("sublanguageid", getLanguage(), "imdbid", Integer.toString(imdbId))}};
        return searchSubtitles(params, "imdb");
    }

    public List<SearchSubtitlesResult> searchSubtitlesBy(String title) throws SubtitlesDownloaderException {
        checkLogin();
        Object[] params = new Object[]{token,
                new Object[]{ImmutableMap.of("sublanguageid", getLanguage(), "query", title)}};
        return searchSubtitles(params, "title");
    }

    // TODO search subtitles by "tag" - filename
    // TODO search movie by "GuessMovieFromString"->"BestGuess" ?

    public List<SubtitleLanguage> getSubLanguages() throws SubtitlesDownloaderException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) client.execute("GetSubLanguages", new Object[]{});
            Object[] languages = (Object[]) response.get("data");
            return Arrays.stream(languages).map(o -> {
                @SuppressWarnings("unchecked")
                Map<String, String> languageEntry = (Map<String, String>) o;
                return new SubtitleLanguage(languageEntry.get("SubLanguageID"),
                        languageEntry.get("LanguageName"), languageEntry.get("ISO639"));
            }).collect(Collectors.toList());
        } catch (XmlRpcException e) {
            throw new SubtitlesDownloaderException("could not invoke GetSubLanguages because of " + e.getMessage(), e);
        }
    }

    // TODO searchSubtitlesByTag - filename

    private List<SearchSubtitlesResult> searchSubtitles(Object[] params, String source) throws SubtitlesDownloaderException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> execute = (Map<String, Object>) client.execute("SearchSubtitles", params);
            LOG.debug("SearchSubtitles response: " + execute);
            String status = (String) execute.get("status");
            if (!status.contains("OK")) {
                throw new SubtitlesDownloaderException("could not SearchSubtitles because of wrong status " + status);
            }

            Object dataRaw = execute.get("data");
            // can be false if nothing found...
            if (dataRaw.equals(false)) {
                return Collections.emptyList();
            }
            Object[] data = (Object[]) dataRaw;
            List<SearchSubtitlesResult> result = Lists.newArrayListWithCapacity(data.length);
            for (Object entry : data) {
                @SuppressWarnings("unchecked")
                Map<String, Object> entryMap = (Map<String, Object>) entry;
                result.add(new SearchSubtitlesResult(entryMap, source));
            }
            return result;
        } catch (XmlRpcException e) {
            throw new SubtitlesDownloaderException("could not invoke SearchSubtitles because of " + e.getMessage(), e);
        }
    }

    // TODO
    // this can be also easily implemented
    // http://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#SearchMoviesOnIMDB

    public List<MovieEntity> checkMovieHash2(String hashCode) throws SubtitlesDownloaderException {
        checkLogin();
        Object[] params = new Object[]{token, new Object[]{hashCode}};
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) client.execute("CheckMovieHash2", params);
            LOG.debug("CheckMovieHash2 response: " + response);
            String status = (String) response.get("status");
            if (!status.contains("OK")) {
                throw new SubtitlesDownloaderException("could not CheckMovieHash2 because of wrong status " + status);
            }
            return parseData(response);
        } catch (XmlRpcException e) {
            throw new SubtitlesDownloaderException("could not invoke CheckMovieHash2 because of " + e.getMessage(), e);
        }
    }

    private List<MovieEntity> parseData(Map<String, Object> response) {
        List<MovieEntity> result;
        Object data = response.get("data");
        if (data instanceof Object[]) {
            Object[] records = (Object[]) data;
            result = getRecords(records);
        } else if (data instanceof HashMap<?, ?>) {
            result = Lists.newArrayList();
            @SuppressWarnings("unchecked")
            Map<String, Object> recordsMap = (Map<String, Object>) data;
            for (Object records : recordsMap.values()) {
                result.addAll(getRecords((Object[]) records));
            }
        } else {
            throw new IllegalStateException("Unsupported data object type " + data);
        }
        return result;
    }

    private List<MovieEntity> getRecords(Object[] records) {
        List<MovieEntity> result = Lists.newArrayListWithCapacity(records.length);
        for (Object recordObject : records) {
            @SuppressWarnings("unchecked")
            Map<String, Object> record = (Map<String, Object>) recordObject;
            result.add(new MovieEntity(record));
        }
        return result;
    }

    public Optional<Movie> guessMovieFromFileName(String fileName) throws SubtitlesDownloaderException {
        checkLogin();
        Object[] params = new Object[]{token, new Object[]{fileName}};
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> execute = (Map<String, Object>) client.execute("GuessMovieFromString", params);
            LOG.debug("GuessMovieFromString response: " + execute);
            String status = (String) execute.get("status");
            if (!status.contains("OK")) {
                throw new SubtitlesDownloaderException("could not GuessMovieFromString because of wrong status " + status);
            }

            Object dataRaw = execute.get("data");
            // can be false if nothing found...
            if (dataRaw.equals(false)) {
                return Optional.empty();
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataRaw;
            @SuppressWarnings("unchecked")
            Map<String, Object> dataForFileName = (Map<String, Object>) data.get(fileName);
            @SuppressWarnings("unchecked")
            Map<String, Object> bestGuess = (Map<String, Object>) dataForFileName.get("BestGuess");
            String title = (String) bestGuess.get("MovieName");
            int year = parseYear((String) bestGuess.get("MovieYear"));
            int imdbId = parseIMDB(bestGuess.get("IDMovieIMDB"));
            return Optional.of(new Movie(title, year, imdbId));
        } catch (XmlRpcException e) {
            throw new SubtitlesDownloaderException("could not invoke SearchSubtitles because of " + e.getMessage(), e);
        }
    }

    private Integer parseIMDB(Object imdbId) {
        if (imdbId == null) {
            LOG.debug("imdbId is null");
            return -1;
        }
        if (imdbId instanceof Integer) {
            return (Integer) imdbId;
        } else if (imdbId instanceof String) {
            return TitleUtils.getImdbFromString((String) imdbId);
        } else {
            throw new IllegalArgumentException("Not supported type: " + imdbId.getClass() + " with value " + imdbId);
        }
    }

    private int parseYear(String yearString) {
        try {
            return Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            LOG.warn("Could not parse year from " + yearString + ", because of " + e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Based on http://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#TryUploadSubtitles
     *
     * @param subtitleMd5Hash
     * @param subtitleFileName
     * @param movieHash
     * @param movieSizeByte
     * @param movieFileName
     * @return true if subtitles already in db
     * @throws SubtitlesDownloaderException
     */
    public boolean tryUploadSubtitles(String subtitleMd5Hash,
                                      String subtitleFileName,
                                      String movieHash,
                                      Long movieSizeByte,
                                      String movieFileName) throws SubtitlesDownloaderException {
        checkLogin();
        Object[] params = new Object[]{token,
                new Object[]{ImmutableMap.of("cd1", ImmutableMap.builder()
                        .put("subhash", subtitleMd5Hash)
                        .put("subfilename", subtitleFileName)
                        .put("moviehash", movieHash)
                        .put("moviebytesize", movieSizeByte.toString())
                        .put("moviefilename", movieFileName)
                        .build())}};

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) client.execute("TryUploadSubtitles", params);
            LOG.debug("TryUploadSubtitles response: " + response);
            String status = (String) response.get("status");
            if (!status.contains("OK")) {
                throw new SubtitlesDownloaderException("could not TryUploadSubtitles because of wrong status " + status);
            }
            boolean alreadyInDb = "1".equals(((String) response.get("alreadyindb")).trim());
            logInsertToOpenSubtitlesDb(response, alreadyInDb);
            return alreadyInDb;
        } catch (XmlRpcException e) {
            throw new SubtitlesDownloaderException("could not invoke CheckMovieHash2 because of " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void logInsertToOpenSubtitlesDb(Map<String, Object> response, boolean alreadyInDb) {
        try {
            if (alreadyInDb) {
                if ("0".equals(((Map<String, Object>) response.get("data")).get("MoviefilenameWasAlreadyInDb"))) {
                    LOG.info("New moviefilename was inserted to OpenSubtitles database!");
                }
                if ("0".equals(((Map<String, Object>) response.get("data")).get("HashWasAlreadyInDb"))) {
                    LOG.info("New MovieHash was inserted to OpenSubtitles database!");
                }
            }
        } catch (Exception e) {
            LOG.error("Something went wrong when tried to parse tryUploadSubtitles response, because of " + e.getMessage(), e);
        }
    }

    // http://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#UploadSubtitles
    public void uploadSubtitles(String idMovieImdb,
                                String movieReleaseName,
                                String subtitleLanguageId,
                                String subtitleMd5Hash,
                                String subtitleFileName,
                                String movieHash,
                                Long movieSizeByte,
                                String movieFileName,
                                String subtitleContent) throws SubtitlesDownloaderException {
        checkLogin();
        // ( $token,
        // array(
        // 'baseinfo' => array (
        //      'idmovieimdb' => $idmovieimdb,
        //      'moviereleasename' => $scene_releasename,
        //      'movieaka' => $aka_in_subtitle_language,
        //      'sublanguageid' => $sublanguageid,
        //      'subauthorcomment' => $author_comment,
        //      'hearingimpaired' => $hearing_impaired,
        //      'highdefinition' => $high_definition,
        //      'automatictranslation' => $automatic_translation,
        //      'subtranslator' => $who_translated_subtitles,
        //      'foreignpartsonly' => $foreign_parts_only),
        // 'cd1' => array(
        //      'subhash' => $md5subhash,
        //      'subfilename' => $subfilename,
        //      'moviehash' => $moviehash,
        //      'moviebytesize' => $moviebytesize,
        //      'movietimems' => $movietimems,
        //      'moviefps' => $moviefps,
        //      'movieframes' => $movieframes,
        //      'moviefilename' => $moviefilename,
        //      'subcontent' => $subtitlecontent ),
        // 'cd2' => array (...) ) )
        Object[] params = new Object[]{token,
                new Object[]{
                        ImmutableMap.of("baseinfo", ImmutableMap.builder()
                                .put("idmovieimdb", idMovieImdb)
                                .put("moviereleasename", movieReleaseName)
                                .put("sublanguageid", subtitleLanguageId)
                                .build()),
                        ImmutableMap.of("cd1", ImmutableMap.builder()
                                .put("subhash", subtitleMd5Hash)
                                .put("subfilename", subtitleFileName)
                                .put("moviehash", movieHash)
                                .put("moviebytesize", movieSizeByte.toString())
                                .put("moviefilename", movieFileName)
                                .put("subcontent", subtitleContent)
                                .build())
                }};
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) client.execute("UploadSubtitles", params);
            LOG.debug("UploadSubtitles response: " + response);
            String status = (String) response.get("status");
            if (!status.contains("OK")) {
                throw new SubtitlesDownloaderException("could not UploadSubtitles because of wrong status " + status);
            }
        } catch (XmlRpcException e) {
            throw new SubtitlesDownloaderException("could not invoke UploadSubtitles because of " + e.getMessage(), e);
        }
    }
}
