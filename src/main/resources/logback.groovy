//
// Built on Sun May 13 22:51:53 CEST 2012 by logback-translator
// For more information on configuration files in Groovy
// please see http://logback.qos.ch/manual/groovy.html

// For assistance related to this tool or configuration files
// in general, please contact the logback user mailing list at
//    http://qos.ch/mailman/listinfo/logback-user

// For professional support please see
//   http://www.qos.ch/shop/products/professionalSupport

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbHelper
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbWarnFilter

import static ch.qos.logback.classic.Level.*

def API_KEY = TheMovieDbHelper.API_KEY

//println "Setting logback in groovy."

appender("SOCKET", de.huxhorn.lilith.logback.appender.ClassicMultiplexSocketAppender) {
    compressing = true
    reconnectionDelay = 10000
    includeCallerData = true
    remoteHosts = "localhost, 10.200.55.13"
}

appender("STDOUT", ConsoleAppender) {
    filter(TheMovieDbWarnFilter)
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{20} [%file:%line] - %replace(%msg){'${API_KEY}', '[API-KEY]'}%n"
    }
}

if (isDev() || isTestMode()) {
    root(INFO, ["STDOUT"])
    logger("pl.rafalmag", DEBUG, ["STDOUT"], additivity = false)
} else {
    root(WARN, ["STDOUT"])
    logger("pl.rafalmag", INFO, ["STDOUT"], additivity = false)
}


def isTestMode() {
    try {
        Class.forName("pl.rafalmag.subtitledownloader.opensubtitles.SessionTest");
        println 'test mode'
        return true
    } catch (ClassNotFoundException ignored) {
        return false
    }
}

def isDev() {
    if (InetAddress.getLocalHost().getHostName().toLowerCase().contains("rafal")) {
        println "dev mode"
        return true
    }
    return false
}