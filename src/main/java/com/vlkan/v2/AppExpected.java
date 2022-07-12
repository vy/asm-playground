package com.vlkan.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppExpected {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        LOGGER.atInfo().withLocation(Log4jLocationRegistry.get(0)).log("should log at line 11");
        System.out.println("nothing to see here");
        LOGGER.atInfo().withLocation(Log4jLocationRegistry.get(1)).log("should log at line 13");
        f();
    }

    private static void f() {
        System.out.println("adding some indirection");
        LOGGER.atInfo().withLocation(Log4jLocationRegistry.get(2)).log("should log at line 19");
    }

    static {
        Log4jLocationRegistry.init(3);
    }

}
