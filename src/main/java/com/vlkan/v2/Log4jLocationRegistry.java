package com.vlkan.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum Log4jLocationRegistry {;

    private static List<LazyLocation> LOCATIONS = new ArrayList<>();

    public static void init(int size) {
        LOCATIONS = IntStream
                .range(0, size)
                .mapToObj(ignored -> new LazyLocation())
                .collect(Collectors.toList());
    }

    public static StackTraceElement get(int index) {
        LazyLocation lazyLocation = LOCATIONS.get(index);
        return lazyLocation.get();
    }

}
