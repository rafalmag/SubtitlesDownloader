//
// Built on Sun May 13 22:51:53 CEST 2012 by logback-translator
// For more information on configuration files in Groovy
// please see http://logback.qos.ch/manual/groovy.html

// For assistance related to this tool or configuration files
// in general, please contact the logback user mailing list at
//    http://qos.ch/mailman/listinfo/logback-user

// For professional support please see
//   http://www.qos.ch/shop/products/professionalSupport

import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbHelper;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.*

def API_KEY = TheMovieDbHelper.API_KEY

//println "Setting logback in groovy."

appender("STDOUT", ConsoleAppender) {
	encoder(PatternLayoutEncoder) { pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{20} [%file:%line] - %replace(%msg){'${API_KEY}', '[API-KEY]'}%n" }
}

def testMode = isTestMode()
//println "isTestMode="+testMode;
if(testMode){
	root(DEBUG, ["STDOUT"])
	logger("pl.rafalmag", DEBUG, ["STDOUT"])
}else{
	root(WARN, ["STDOUT"])
	logger("pl.rafalmag", INFO, ["STDOUT"])
}


def isTestMode(){
	try{
		Class.forName("pl.rafalmag.subtitledownloader.opensubtitles.SessionTest");
		true
	}catch(ClassNotFoundException e){
		false
	}
}
