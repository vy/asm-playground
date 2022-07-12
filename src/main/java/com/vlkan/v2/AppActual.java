package com.vlkan.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppActual {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        LOGGER.info("should log at line 11");
        System.out.println("nothing to see here");
        LOGGER.info("should log at line 13");
        f();
    }

    private static void f() {
        System.out.println("adding some indirection");
        LOGGER.info("should log at line 19");
    }

}
