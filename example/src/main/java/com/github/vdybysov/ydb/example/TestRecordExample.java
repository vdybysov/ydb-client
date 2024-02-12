package com.github.vdybysov.ydb.example;

import com.github.vdybysov.ydb.client.YdbClientBuilder;
import com.github.vdybysov.ydb.exception.YdbClientException;
import com.github.vdybysov.ydb.typed.TypedYdbClient;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class TestRecordExample {

    private final TypedYdbClient client;

    public static void main(String[] args) throws YdbClientException {
        String connectionString = Objects.requireNonNull(
                System.getenv("YDB_CONNECTION_STRING"),
                "YDB_CONNECTION_STRING must be defined in env."
        );
        String iamToken = Objects.requireNonNull(
                System.getenv("YDB_IAM_TOKEN"),
                "YDB_IAM_TOKEN must be defined in env."
        );
        TypedYdbClient client = new TypedYdbClient(YdbClientBuilder.forConnectionString(connectionString, iamToken));
        TestRecordExample example = new TestRecordExample(client);
        example.selectAll();
        example.selectByScore();
        example.insert();
    }

    private void selectAll() throws YdbClientException {
        List<TestRecord> result = client.executeQuery(
                "select * from test_table", null, TestRecord.class
        ).stream().toList();
        System.out.println(result);
    }

    private void selectByScore() throws YdbClientException {
        List<TestRecord> result = client.executeQuery(
                "declare $score as Float; select * from test_table where score > $score",
                new SelectByScoreParams(1),
                TestRecord.class
        ).stream().toList();
        System.out.println(result);
    }

    private void insert() throws YdbClientException {
        client.executeQuery(
                """
                        declare $id as String;
                        declare $name as Utf8;
                        declare $score as Float;
                        declare $date as Date;
                        declare $datetime as Datetime;
                        declare $timestamp as Timestamp;
                        insert into test_table (id, name, score, date, datetime, timestamp)
                        values ($id, $name, $score, $date, $datetime, $timestamp)
                        """.stripIndent(),
                new TestRecord(
                        "TestId",
                        "TestName",
                        1.5f,
                        LocalDate.now(),
                        Instant.now(),
                        Instant.now()
                )
        );
    }
}
