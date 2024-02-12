package com.github.vdybysov.ydb.typed.annotation;

import com.github.vdybysov.ydb.typed.mapper.IMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper {
    Class<? extends IMapper<?>> value();
}
