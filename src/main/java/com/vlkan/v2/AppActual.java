package com.vlkan.v2;

import static com.vlkan.v2.Log4j.log;

public class AppActual {

    public static void main(String[] args) {
        System.out.println("should log at line 9");
        log(null);
        System.out.println("nothing to see here");
        System.out.println("should log at line 12");
        log(null);
        f();
    }

    private static void f() {
        System.out.println("adding some indirection");
        System.out.println("should log at line 19");
        log(null);
    }

}
