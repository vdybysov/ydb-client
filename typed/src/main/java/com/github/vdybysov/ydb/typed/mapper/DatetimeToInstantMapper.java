package com.github.vdybysov.ydb.typed.mapper;

import org.jetbrains.annotations.NotNull;
import tech.ydb.proto.ValueProtos;

import java.time.Instant;

public class DatetimeToInstantMapper implements IMapper<Instant> {

    @Override
    public @NotNull Instant map(ValueProtos.Value value) {
        return Instant.ofEpochSecond(value.getUint32Value());
    }

    @Override
    public ValueProtos.@NotNull TypedValue unmap(Instant value) {
        return unmapNullable(
                ValueProtos.Type.PrimitiveTypeId.DATETIME,
                value,
                builder -> builder.setUint32Value((int) value.getEpochSecond())
        );
    }
}
