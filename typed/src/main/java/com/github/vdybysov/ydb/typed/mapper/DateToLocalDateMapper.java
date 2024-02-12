package com.github.vdybysov.ydb.typed.mapper;

import org.jetbrains.annotations.NotNull;
import tech.ydb.proto.ValueProtos;

import java.time.LocalDate;

public class DateToLocalDateMapper implements IMapper<LocalDate> {

    @Override
    public @NotNull LocalDate map(ValueProtos.Value value) {
        return LocalDate.ofEpochDay(value.getUint32Value());
    }

    @Override
    public ValueProtos.@NotNull TypedValue unmap(LocalDate value) {
        return unmapNullable(
                ValueProtos.Type.PrimitiveTypeId.DATE,
                value,
                builder -> builder.setUint32Value((int) value.toEpochDay())
        );
    }
}
