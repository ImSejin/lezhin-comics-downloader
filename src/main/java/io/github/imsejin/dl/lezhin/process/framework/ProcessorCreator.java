package io.github.imsejin.dl.lezhin.process.framework;

import io.github.imsejin.common.util.CollectionUtils;
import io.github.imsejin.common.util.ReflectionUtils;
import io.github.imsejin.dl.lezhin.process.Processor;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public final class ProcessorCreator {

    private static final List<Field> FIELDS = Arrays.stream(ProcessorCreator.class.getDeclaredFields())
            .filter(it -> !Modifier.isStatic(it.getModifiers()))
            .collect(toUnmodifiableList());

    private final Path basePath;

    private final Locale locale;

    public List<Processor> create(List<Class<? extends Processor>> processorTypes) {
        if (CollectionUtils.isNullOrEmpty(processorTypes)) {
            return Collections.emptyList();
        }

        List<Processor> processors = new ArrayList<>();

        for (Class<? extends Processor> processorType : processorTypes) {
            Constructor<?> constructor = resolveConstructor(processorType);
            Processor processor = createProcessor(constructor);

            processors.add(processor);
        }

        return Collections.unmodifiableList(processors);
    }

    // -------------------------------------------------------------------------------------------------

    private static Constructor<?> resolveConstructor(Class<? extends Processor> processorType) {
        List<Constructor<?>> constructors = Arrays.stream(processorType.getDeclaredConstructors())
                .sorted(comparing(Constructor::getParameterCount)).collect(toList());

        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return constructor;
            }

            // TODO: validate parameters of constructor.
//            List<Class<?>> paramTypes = List.of(constructor.getParameterTypes());
//            List<Field> resolvableFields = FIELDS.stream().filter(it -> paramTypes.contains(it.getType())).collect(toList());
        }

        return constructors.get(0);
    }

    private Processor createProcessor(Constructor<?> constructor) {
        if (constructor.getParameterCount() == 0) {
            return (Processor) ReflectionUtils.instantiate(constructor);
        }

        List<Object> arguments = new ArrayList<>();
        Class<?>[] paramTypes = constructor.getParameterTypes();

        for (Class<?> paramType : paramTypes) {
            Field field = FIELDS.stream().filter(it -> paramType == it.getType()).findFirst().orElseThrow();
            Object fieldValue = ReflectionUtils.getFieldValue(this, field);

            arguments.add(fieldValue);
        }

        return (Processor) ReflectionUtils.instantiate(constructor, arguments.toArray());
    }

}
