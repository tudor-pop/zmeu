open module zmeu.api {
    exports io.zmeu.api;
    exports io.zmeu.api.schema;
    exports io.zmeu.api.resource;

    requires java.logging;
    requires javers.core;
    requires static lombok;
    requires org.pf4j;
    requires java.compiler;
}