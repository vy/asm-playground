package com.vlkan.v2;

import java.util.function.Supplier;

final class LazyLocation extends Lazy<StackTraceElement> {

    private static final Supplier<StackTraceElement> LOCATION_SUPPLIER =
            () -> {
                StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
                // Skip the first element pointing to this method.
                return stackTraceElements[3];
            };

    LazyLocation() {
        super(LOCATION_SUPPLIER);
    }

    private LazyLocation(Supplier<StackTraceElement> supplier) {
        super(supplier);
    }

}
