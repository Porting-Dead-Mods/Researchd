package com.portingdeadmods.researchd.utils;

import javax.annotation.Nullable;

// TODO: Move to pdl
public sealed interface Result<T, E> permits Result.Ok, Result.Err {
    record Ok<T, E>(T value) implements Result<T, E> {}
    record Err<T, E>(E error) implements Result<T, E> {}

    default @Nullable E error() {
        return null;
    }

    static <T, E extends Exception> Result<T, E> ok(T val) {
        return new Ok<>(val);
    }

    static <T, E extends Exception> Result<T, E> err(E exception) {
        return new Err<>(exception);
    }

    static <T> Result<T, Exception> err(String message) {
        return new Err<>(new Exception(message));
    }

}
