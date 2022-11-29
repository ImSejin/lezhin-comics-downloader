package io.github.imsejin.dl.lezhin.process.framework;

import io.github.imsejin.common.util.CollectionUtils;
import io.github.imsejin.common.util.ReflectionUtils;
import io.github.imsejin.dl.lezhin.exception.ProcessorCreationFailureException;
import io.github.imsejin.dl.lezhin.process.Processor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public final class ProcessorCreator {

    private final Set<Object> beans = new HashSet<>();

    public ProcessorCreator(Object... beans) {
        for (Object bean : beans) {
            if (bean != null) {
                this.beans.add(bean);
            }
        }
    }

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

    private Constructor<?> resolveConstructor(Class<? extends Processor> processorType) {
        // Resolves a constructor that has less number of parameters first.
        List<Constructor<?>> constructors = Arrays.stream(processorType.getDeclaredConstructors())
                .sorted(comparing(Constructor::getParameterCount)).collect(toList());

        constructor_scope:
        for (Constructor<?> constructor : constructors) {
            // Resolves a default constructor.
            if (constructor.getParameterCount() == 0) {
                return constructor;
            }

            param_scope:
            for (Class<?> paramType : constructor.getParameterTypes()) {
                for (Object bean : this.beans) {
                    if (paramType.isAssignableFrom(bean.getClass())) {
                        continue param_scope;
                    }
                }

                // Failed to resolve this constructor and tries to resolve the next constructor.
                continue constructor_scope;
            }

            // The parameters of this constructor are assignable from all beans.
            return constructor;
        }

        throw new ProcessorCreationFailureException("There is no matched bean for parameter of the processor: (processorType=%s, beans=%s)",
                processorType, this.beans.stream().map(Object::getClass).collect(toList()));
    }

    private Processor createProcessor(Constructor<?> constructor) {
        if (constructor.getParameterCount() == 0) {
            return (Processor) ReflectionUtils.instantiate(constructor);
        }

        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] arguments = new Object[paramTypes.length];

        Map<Class<?>, Object> beanTypeMap = this.beans.stream().collect(toMap(Object::getClass, it -> it));

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            // Picks a bean strictly.
            Object bean = beanTypeMap.get(paramType);

            // If no matched bean, picks a bean leniently.
            if (bean == null) {
                bean = this.beans.stream().filter(it -> paramType.isAssignableFrom(it.getClass()))
                        .findFirst().orElseThrow();
            }

            arguments[i] = bean;
        }

        return (Processor) ReflectionUtils.instantiate(constructor, arguments);
    }

}
