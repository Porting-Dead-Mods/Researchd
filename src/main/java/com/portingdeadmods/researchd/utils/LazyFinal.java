package com.portingdeadmods.researchd.utils;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


/**
 * A thread-safe write-once lazy container that starts with null value
 * and can be initialized exactly once.
 *
 * @param <T> the type of the lazy value
 */
public class LazyFinal<T> implements Supplier<T> {
    private volatile @Nullable T cachedValue = null;
    private volatile boolean initialized = false;
    private final Object lock = new Object();

    /**
     * Creates a new LazyFinal instance initialized to null
     */
    public LazyFinal() {
        // Starts with null by default
    }

    /**
     * Factory method to create a new LazyFinal instance
     */
    public static <T> LazyFinal<T> create() {
        return new LazyFinal<>();
    }

    /**
     * Initializes the lazy value exactly once.
     * Subsequent calls will throw IllegalStateException.
     *
     * @param value the value to initialize with (can be null if you want to explicitly set null)
     * @throws IllegalStateException if already initialized
     */
    public void initialize(T value) {
        if (initialized) {
            throw new IllegalStateException("WriteOnceLazy has already been initialized");
        }

        synchronized (lock) {
            if (initialized) {
                throw new IllegalStateException("WriteOnceLazy has already been initialized");
            }
            this.cachedValue = value;
            this.initialized = true;
        }
    }

    /**
     * Initializes the lazy value using a supplier exactly once.
     * Subsequent calls will throw IllegalStateException.
     *
     * @param supplier the supplier to get the value from
     * @throws IllegalStateException if already initialized
     * @throws IllegalArgumentException if supplier returns null
     */
    public void initialize(Supplier<T> supplier) {
        if (initialized) {
            throw new IllegalStateException("WriteOnceLazy has already been initialized");
        }

        synchronized (lock) {
            if (initialized) {
                throw new IllegalStateException("WriteOnceLazy has already been initialized");
            }

            T value = supplier.get();
            if (value == null) {
                throw new IllegalArgumentException("Supplier returned null, which is not allowed");
            }

            this.cachedValue = value;
            this.initialized = true;
        }
    }

    /**
     * Gets the cached value.
     *
     * @return the cached value, or null if not yet initialized
     */
    @Override
    public @Nullable T get() {
        return cachedValue;
    }

    /**
     * Gets the cached value, throwing an exception if not initialized.
     *
     * @return the cached value
     * @throws IllegalStateException if not yet initialized
     */
    public T getOrThrow() {
        if (!initialized) {
            throw new IllegalStateException("LazyFinal has not been initialized yet");
        }
        return cachedValue;
    }

    /**
     * Checks if this lazy container has been initialized.
     *
     * @return true if initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Gets the value if initialized, otherwise returns the default value.
     *
     * @param defaultValue the value to return if not initialized
     * @return the cached value if initialized, otherwise the default value
     */
    public T getOrDefault(T defaultValue) {
        return initialized ? cachedValue : defaultValue;
    }

    @Override
    public String toString() {
        return "LazyFinal{" +
                "initialized=" + initialized +
                ", value=" + (initialized ? String.valueOf(cachedValue) : "uninitialized") +
                '}';
    }
}
