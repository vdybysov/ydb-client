package com.github.vdybysov.ydb.typed;

import com.github.vdybysov.ydb.client.YdbClient;
import com.github.vdybysov.ydb.exception.YdbClientException;
import com.github.vdybysov.ydb.typed.util.MappingUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.ydb.proto.ValueProtos;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TypedYdbClient {

    private final YdbClient client;

    @NotNull
    private List<ValueProtos.ResultSet> executeQueryWithMappedParams(
            @NotNull String yql,
            @Nullable Object params
    ) throws YdbClientException {
        Map<String, ValueProtos.TypedValue> unmappedParams = params == null
                ? Collections.emptyMap()
                : MappingUtil.unmap(params);
        return client.executeQuery(yql, unmappedParams);
    }

    public void executeQuery(@NotNull String yql, @Nullable Object params) throws YdbClientException {
        executeQueryWithMappedParams(yql, params);
    }

    @NotNull
    public <Result> QueryResult<Result> executeQuery(
            @NotNull String yql,
            @Nullable Object params,
            @NotNull Class<Result> resultType
    ) throws YdbClientException {
        List<ValueProtos.ResultSet> result = executeQueryWithMappedParams(yql, params);
        return new QueryResult<>(result, resultType);
    }
}
