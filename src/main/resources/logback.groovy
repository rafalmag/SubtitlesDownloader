import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import org.slf4j.bridge.SLF4JBridgeHandler
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbService
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbWarnFilter

import java.util.logging.LogManager

import static ch.qos.logback.classic.Level.*

def API_KEY = TheMovieDbService.API_KEY

// WARN: for changes to take effect - rebuild IDEA project

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
// jul -> slf4j
LogManager.getLogManager().reset();
SLF4JBridgeHandler.install();
// suppress javafx info about "index exceeds maxCellCount. Check size calculations for class"
logger("javafx.scene.control", WARN)

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
    def hostName = InetAddress.getLocalHost().getHostName().toLowerCase()
    def devMachines = ["rafal", "dell"]
    if (devMachines.find { it -> hostName.contains(it) }) {
        println "dev mode"
        return true
    }
    return false
}