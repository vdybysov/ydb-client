package com.github.vdybysov.ydb.client;

import com.google.common.base.Splitter;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.jetbrains.annotations.NotNull;
import tech.ydb.proto.table.v1.TableServiceGrpc;

import java.net.URI;
import java.net.URISyntaxException;

public class YdbClientBuilder {

    private static final Metadata.Key<String> METADATA_KEY_DATABASE = Metadata.Key.of(
            "x-ydb-database",
            Metadata.ASCII_STRING_MARSHALLER
    );

    private static final Metadata.Key<String> METADATA_KEY_AUTH_TICKET = Metadata.Key.of(
            "x-ydb-auth-ticket",
            Metadata.ASCII_STRING_MARSHALLER
    );

    private static final Metadata.Key<String> METADATA_KEY_SDK_BUILD_INFO = Metadata.Key.of(
            "x-ydb-sdk-build-info",
            Metadata.ASCII_STRING_MARSHALLER
    );

    private static final String METADATA_VALUE_SDK_BUILD_INFO = "ydb-java-sdk/2.1.11";

    public static YdbClient forConnectionString(@NotNull String connectionString, @NotNull String iamToken) {
        URI uri;
        try {
            uri = new URI(connectionString);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String database = Splitter.on("&").withKeyValueSeparator("=").split(uri.getQuery()).get("database");
        if (database == null || database.isEmpty()) {
            throw new IllegalArgumentException("database is not defined");
        }
        Metadata metadata = createMetadata(database, iamToken);
        Channel channel = createChannel(uri, metadata);
        TableServiceGrpc.TableServiceBlockingStub tableService = TableServiceGrpc.newBlockingStub(channel);
        return new YdbClient(tableService);
    }

    private static Metadata createMetadata(@NotNull String database, @NotNull String iamToken) {
        Metadata metadata = new Metadata();
        metadata.put(METADATA_KEY_DATABASE, database);
        metadata.put(METADATA_KEY_AUTH_TICKET, iamToken);
        metadata.put(METADATA_KEY_SDK_BUILD_INFO, METADATA_VALUE_SDK_BUILD_INFO);
        return metadata;
    }

    private static Channel createChannel(@NotNull URI uri, @NotNull Metadata metadata) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(uri.getHost(), uri.getPort())
                .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata));
        if ("grpcs".equals(uri.getScheme())) {
            channelBuilder = channelBuilder.useTransportSecurity();
        }
        return channelBuilder.build();
    }
}
