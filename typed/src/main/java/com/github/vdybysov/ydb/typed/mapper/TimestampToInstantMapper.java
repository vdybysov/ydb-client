package com.github.vdybysov.ydb.typed.mapper;

import org.jetbrains.annotations.NotNull;
import tech.ydb.proto.ValueProtos;

import java.time.Instant;

public class TimestampToInstantMapper implements IMapper<Instant> {

    @Override
    public @NotNull Instant map(ValueProtos.Value value) {
        return Instant.ofEpochMilli(value.getUint64Value() / 1000);
    }

    @Override
    public ValueProtos.@NotNull TypedValue unmap(Instant value) {
        return unmapNullable(
                ValueProtos.Type.PrimitiveTypeId.TIMESTAMP,
                value,
                builder -> builder.setUint64Value(value.toEpochMilli() * 1000)
        );
    }
}
