package com.github.vdybysov.ydb.exception;

import tech.ydb.proto.StatusCodesProtos;
import tech.ydb.proto.YdbIssueMessage;

import java.util.List;
import java.util.stream.Collectors;

public class YdbClientException extends Exception {
    public YdbClientException(
            StatusCodesProtos.StatusIds.StatusCode statusCode,
            List<YdbIssueMessage.IssueMessage> issueMessages
    ) {
        super(
                String.format(
                        "%s: %s",
                        statusCode.name(),
                        issueMessages.stream()
                                .map(YdbIssueMessage.IssueMessage::toString)
                                .collect(Collectors.joining(", "))
                )
        );
    }

    public YdbClientException(Throwable cause) {
        super(cause);
    }
}
