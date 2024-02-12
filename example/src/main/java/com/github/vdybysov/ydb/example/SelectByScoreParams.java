package com.github.vdybysov.ydb.example;

import com.github.vdybysov.ydb.typed.annotation.Mapper;
import com.github.vdybysov.ydb.typed.mapper.FloatMapper;

public record SelectByScoreParams(
        @Mapper(FloatMapper.class) float score
) {
}
