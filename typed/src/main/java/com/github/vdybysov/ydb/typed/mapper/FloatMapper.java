package com.github.vdybysov.ydb.typed.mapper;

import org.jetbrains.annotations.NotNull;
import tech.ydb.proto.ValueProtos;

public class FloatMapper implements IMapper<Float> {

    @Override
    @NotNull
    public Float map(ValueProtos.Value value) {
        return value.getFloatValue();
    }

    @Override
    @NotNull
    public ValueProtos.TypedValue unmap(Float value) {
        return unmapNullable(
                ValueProtos.Type.PrimitiveTypeId.FLOAT,
                value,
                builder -> builder.setFloatValue(value)
        );
    }
}
