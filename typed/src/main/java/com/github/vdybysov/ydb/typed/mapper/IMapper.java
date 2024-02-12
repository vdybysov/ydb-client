package com.github.vdybysov.ydb.typed.mapper;

import com.google.protobuf.NullValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.ydb.proto.ValueProtos;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IMapper<T> {

    static ValueProtos.TypedValue primitiveValue(
            ValueProtos.Type.PrimitiveTypeId typeId,
            Function<ValueProtos.Value.Builder, ValueProtos.Value.Builder> valueSetter
    ) {
        return ValueProtos.TypedValue.newBuilder()
                .setType(ValueProtos.Type.newBuilder().setTypeId(typeId))
                .setValue(valueSetter.apply(ValueProtos.Value.newBuilder()))
                .build();
    }

    @Nullable
    default T mapNullable(ValueProtos.Value value) {
        if (value.hasNullFlagValue()) {
            return null;
        }
        return map(value);
    }

    @NotNull
    default ValueProtos.TypedValue unmapNullable(
            @NotNull ValueProtos.Type.PrimitiveTypeId typeId,
            @Nullable T value,
            @NotNull Consumer<ValueProtos.Value.Builder> valueSetter
    ) {
        ValueProtos.TypedValue.Builder builder = ValueProtos.TypedValue.newBuilder()
                .setType(ValueProtos.Type.newBuilder().setTypeId(typeId));
        ValueProtos.Value.Builder unmappedValue = ValueProtos.Value.newBuilder();
        if (value == null) {
            unmappedValue.setNullFlagValue(NullValue.NULL_VALUE);
        } else {
            valueSetter.accept(unmappedValue);
        }
        return builder.setValue(unmappedValue).build();
    }

    @NotNull
    T map(ValueProtos.Value value);

    @NotNull
    ValueProtos.TypedValue unmap(T value);
}
