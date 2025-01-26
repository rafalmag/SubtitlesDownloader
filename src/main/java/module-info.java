open module subtitlesdownloader.main {

    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires com.google.guice;
    requires org.slf4j;
    requires com.google.common;
    requires akka.actor;
    requires org.apache.commons.lang3;
    requires xmlrpc.common;
    requires xmlrpc.client;
    requires themoviedbapi;
    requires fx.guice;
    requires jsr305;
    requires com.google.gson;
    requires java.datatransfer;
    requires java.desktop;
    requires org.apache.commons.codec;
    requires org.apache.commons.io;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires jakarta.inject;
    requires akka.stream;
    requires scala.library;

    exports pl.rafalmag.subtitledownloader;

}