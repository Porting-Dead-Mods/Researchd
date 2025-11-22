package com.portingdeadmods.researchd.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class NullableFieldCodec<A> extends MapCodec<A> {
    private final String name;
    private final Codec<A> elementCodec;
    private final boolean lenient;

    public static <A> NullableFieldCodec<A> of(Codec<A> elementCodec, String name) {
        return new NullableFieldCodec<>(name, elementCodec, false);
    }

    private NullableFieldCodec(final String name, final Codec<A> elementCodec, final boolean lenient) {
        this.name = name;
        this.elementCodec = elementCodec;
        this.lenient = lenient;
    }

    @Override
    public <T> DataResult<A> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        final T value = input.get(name);
        if (value == null) {
            return DataResult.success(null);
        }
        final DataResult<A> parsed = elementCodec.parse(ops, value);
        if (parsed.isError() && lenient) {
            return DataResult.success(null);
        }
        return parsed.map(Function.identity()).setPartial(parsed.resultOrPartial().orElse(null));
    }

    @Override
    public <T> RecordBuilder<T> encode(final A input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        if (input != null) {
            return prefix.add(name, elementCodec.encodeStart(ops, input));
        }
        return prefix;
    }

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return Stream.of(ops.createString(name));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NullableFieldCodec<?> that)) return false;
        return lenient == that.lenient && Objects.equals(name, that.name) && Objects.equals(elementCodec, that.elementCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, elementCodec, lenient);
    }

    @Override
    public String toString() {
        return "NullableFieldCodec[" + name + ": " + elementCodec + ']';
    }
}
