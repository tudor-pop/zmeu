package io.zmeu.api;

import java.util.logging.Logger;

public class Plugin extends org.pf4j.Plugin {
    private static final Logger logger = Logger.getLogger(Plugin.class.getName());


    public void start() {
        logger.info(this.getClass().getName() + " started");
    }

    public void stop() {
        logger.info(this.getClass().getName() + " stopped");
    }

    public void delete() {
        super.delete();
    }
}
