package com.github.vdybysov.ydb.typed.mapper;

import org.jetbrains.annotations.NotNull;
import tech.ydb.proto.ValueProtos;

public class TextToStringMapper implements IMapper<String> {
    @Override
    public @NotNull String map(ValueProtos.Value value) {
        return value.getTextValue();
    }

    @Override
    public ValueProtos.@NotNull TypedValue unmap(String value) {
        return unmapNullable(
                ValueProtos.Type.PrimitiveTypeId.UTF8,
                value,
                builder -> builder.setTextValue(value)
        );
    }
}
