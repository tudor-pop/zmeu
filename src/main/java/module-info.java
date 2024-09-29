module zmeu.io {
    requires info.picocli;
    requires java.net.http;
    requires javers.core;
    requires javers.persistence.sql;
    requires static lombok;
    requires org.apache.commons.lang3;
    requires org.apache.logging.log4j.core;
    requires org.fusesource.jansi;
    requires org.jetbrains.annotations;
    requires org.slf4j;
    requires org.pf4j;
    requires zmeu.api;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;
    requires java.sql;
    requires jdk.jdi;
}