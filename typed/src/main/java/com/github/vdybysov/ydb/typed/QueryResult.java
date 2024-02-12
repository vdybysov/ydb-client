package com.github.vdybysov.ydb.typed;

import com.github.vdybysov.ydb.typed.util.MappingUtil;
import lombok.RequiredArgsConstructor;
import tech.ydb.proto.ValueProtos;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class QueryResult<T> {

    private final List<ValueProtos.ResultSet> resultSets;
    private final Class<T> resultType;

    public Stream<T> stream() {
        return resultSets.stream()
                .findFirst()
                .map(rs -> MappingUtil.map(rs, resultType).stream())
                .orElseGet(Stream::empty);
    }
}
