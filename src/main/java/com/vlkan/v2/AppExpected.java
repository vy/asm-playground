package com.vlkan.v2;

import static com.vlkan.v2.Log4j.log;

public class AppExpected {

    public static void main(String[] args) {
        System.out.println("should log at line 9");
        log(Log4jLocationRegistry.get(0));
        System.out.println("nothing to see here");
        System.out.println("should log at line 12");
        log(Log4jLocationRegistry.get(1));
        f();
    }

    private static void f() {
        System.out.println("adding some indirection");
        System.out.println("should log at line 19");
        log(Log4jLocationRegistry.get(2));
    }

    static {
        Log4jLocationRegistry.init(3);
    }

}
