package com.github.vdybysov.ydb.example;

import com.github.vdybysov.ydb.typed.annotation.Mapper;
import com.github.vdybysov.ydb.typed.annotation.Name;
import com.github.vdybysov.ydb.typed.mapper.BytesToStringMapper;
import com.github.vdybysov.ydb.typed.mapper.DateToLocalDateMapper;
import com.github.vdybysov.ydb.typed.mapper.DatetimeToInstantMapper;
import com.github.vdybysov.ydb.typed.mapper.FloatMapper;
import com.github.vdybysov.ydb.typed.mapper.TextToStringMapper;
import com.github.vdybysov.ydb.typed.mapper.TimestampToInstantMapper;

import java.time.Instant;
import java.time.LocalDate;

public record TestRecord(
        @Name("id") @Mapper(BytesToStringMapper.class) String id,
        @Name("name") @Mapper(TextToStringMapper.class) String name,
        @Name("score") @Mapper(FloatMapper.class) Float score,
        @Name("date") @Mapper(DateToLocalDateMapper.class) LocalDate date,
        @Name("datetime") @Mapper(DatetimeToInstantMapper.class) Instant datetime,
        @Name("timestamp") @Mapper(TimestampToInstantMapper.class) Instant timestamp
) {
}
