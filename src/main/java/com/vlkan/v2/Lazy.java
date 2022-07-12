package com.vlkan.v2;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

class Lazy<V> implements Supplier<V> {

    private final Supplier<V> supplier;

    private volatile V value;

    Lazy(Supplier<V> supplier) {
        this.supplier = requireNonNull(supplier, "supplier");
    }

    @Override
    public V get() {
        V localValue = value;
        if (localValue == null) {
            synchronized (this) {
                localValue = value;
                if (localValue == null) {
                    localValue = value = requireNonNull(supplier.get(), "null values are not supported");
                }
            }
        }
        return localValue;
    }

}
