package com.github.vdybysov.ydb.client;

import com.github.vdybysov.ydb.exception.YdbClientException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.jetbrains.annotations.NotNull;
import tech.ydb.proto.OperationProtos;
import tech.ydb.proto.StatusCodesProtos;
import tech.ydb.proto.ValueProtos;
import tech.ydb.proto.table.YdbTable;
import tech.ydb.proto.table.v1.TableServiceGrpc;

import java.util.List;
import java.util.Map;

public class YdbClient {

    public static final YdbTable.TransactionControl DEFAULT_TX_CONTROL = YdbTable.TransactionControl.newBuilder()
            .setCommitTx(true)
            .setBeginTx(
                    YdbTable.TransactionSettings.newBuilder()
                            .setSerializableReadWrite(YdbTable.SerializableModeSettings.newBuilder())
            )
            .build();

    private final TableServiceGrpc.TableServiceBlockingStub tableService;

    private String sessionId;

    YdbClient(TableServiceGrpc.TableServiceBlockingStub tableService) {
        this.tableService = tableService;
    }

    @NotNull
    private <Result extends Message> Result handleOperationResult(
            @NotNull OperationProtos.Operation operation,
            @NotNull Class<? extends Result> resultType
    ) throws YdbClientException {
        try {
            if (operation.getStatus() != StatusCodesProtos.StatusIds.StatusCode.SUCCESS) {
                throw new YdbClientException(operation.getStatus(), operation.getIssuesList());
            }
            return operation.getResult().unpack(resultType);
        } catch (InvalidProtocolBufferException e) {
            throw new YdbClientException(e);
        }
    }

    @NotNull
    private String acquireSessionId() throws YdbClientException {
        if (sessionId == null) {
            sessionId = handleOperationResult(
                    tableService
                            .createSession(YdbTable.CreateSessionRequest.newBuilder().build())
                            .getOperation(),
                    YdbTable.CreateSessionResult.class
            ).getSessionId();
        }
        return sessionId;
    }

    @NotNull
    public List<ValueProtos.ResultSet> executeQuery(
            @NotNull String yql,
            @NotNull Map<String, ValueProtos.TypedValue> params
    ) throws YdbClientException {
        YdbTable.ExecuteDataQueryRequest.Builder request = YdbTable.ExecuteDataQueryRequest.newBuilder()
                .setSessionId(acquireSessionId())
                .setTxControl(DEFAULT_TX_CONTROL)
                .setQuery(YdbTable.Query.newBuilder().setYqlText(yql))
                .putAllParameters(params);
        YdbTable.ExecuteDataQueryResponse response = tableService.executeDataQuery(request.build());
        return handleOperationResult(response.getOperation(), YdbTable.ExecuteQueryResult.class)
                .getResultSetsList();
    }
}
