package com.github.vdybysov.ydb.typed.mapper;

import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;
import tech.ydb.proto.ValueProtos;

import java.nio.charset.Charset;

public class BytesToStringMapper implements IMapper<String> {
    @Override
    public @NotNull String map(ValueProtos.Value value) {
        return value.getBytesValue().toStringUtf8();
    }

    @Override
    public ValueProtos.@NotNull TypedValue unmap(String value) {
        return unmapNullable(
                ValueProtos.Type.PrimitiveTypeId.STRING,
                value,
                builder -> builder.setBytesValue(ByteString.copyFrom(value, Charset.defaultCharset()))
        );
    }
}
