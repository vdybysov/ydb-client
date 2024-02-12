package com.github.vdybysov.ydb.typed.util;

import com.github.vdybysov.ydb.typed.annotation.Mapper;
import com.github.vdybysov.ydb.typed.annotation.Name;
import com.github.vdybysov.ydb.typed.mapper.IMapper;
import tech.ydb.proto.ValueProtos;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

public class MappingUtil {

    public static Map<String, ValueProtos.TypedValue> unmap(Object obj) {
        Map<String, ValueProtos.TypedValue> params = new HashMap<>();
        for (RecordComponent recordComponent : obj.getClass().getRecordComponents()) {
            try {
                params.put(
                        String.format("$%s", recordComponent.getName()),
                        unmapField(obj, getPropertyInfo(obj.getClass(), recordComponent))
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return params;
    }

    private static <T> ValueProtos.TypedValue unmapField(Object obj, PropertyInfo<T> propertyInfo) {
        return propertyInfo.mapper().unmap(propertyInfo.accessor().apply(obj));
    }

    public static <T> List<T> map(ValueProtos.ResultSet resultSet, Class<T> resultType) {
        List<T> result = new ArrayList<>(resultSet.getRowsCount());
        for (ValueProtos.Value row : resultSet.getRowsList()) {
            try {
                result.add(mapRow(resultSet.getColumnsList(), row, resultType));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private static <T> T mapRow(List<ValueProtos.Column> columns, ValueProtos.Value row, Class<T> type) throws Exception {
        //noinspection unchecked
        Constructor<T> constructor = (Constructor<T>) type.getDeclaredConstructors()[0];
        List<Object> args = new LinkedList<>();
        for (RecordComponent recordComponent : type.getRecordComponents()) {
            PropertyInfo<?> propertyInfo = getPropertyInfo(type, recordComponent);
            int columnIdx = IntStream.range(0, columns.size())
                    .filter(idx -> columns.get(idx).getName().equals(propertyInfo.name()))
                    .findFirst()
                    .orElseThrow();
            ValueProtos.Value value = row.getItems(columnIdx);
            args.add(propertyInfo.mapper().mapNullable(value));
        }
        return constructor.newInstance(args.toArray());
    }

    @SuppressWarnings("unchecked")
    private static <T> PropertyInfo<T> getPropertyInfo(Class<?> type, RecordComponent recordComponent) throws Exception {
        Field field = type.getDeclaredField(recordComponent.getName());
        Mapper mapperAnnotation = field.getDeclaredAnnotation(Mapper.class);
        Name nameAnnotation = field.getDeclaredAnnotation(Name.class);
        return new PropertyInfo<>(
                nameAnnotation == null ? recordComponent.getName() : nameAnnotation.value(),
                obj -> {
                    try {
                        return ((T) recordComponent.getAccessor().invoke(obj));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                },
                (IMapper<T>) mapperAnnotation.value().getDeclaredConstructor().newInstance()
        );
    }

    private record PropertyInfo<T>(
            String name,
            Function<Object, T> accessor,
            IMapper<T> mapper
    ) {
    }
}
