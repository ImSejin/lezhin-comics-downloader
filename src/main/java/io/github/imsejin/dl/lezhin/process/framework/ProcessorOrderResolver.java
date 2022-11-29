package io.github.imsejin.dl.lezhin.process.framework;

import io.github.imsejin.common.model.graph.DirectedGraph;
import io.github.imsejin.common.model.graph.Graph;
import io.github.imsejin.common.model.graph.traverse.DepthFirstIterator;
import io.github.imsejin.common.util.ClassUtils;
import io.github.imsejin.common.util.CollectionUtils;
import io.github.imsejin.common.util.StreamUtils;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.exception.InvalidProcessSpecificationException;
import io.github.imsejin.dl.lezhin.exception.ProcessorNotSpecifyException;
import io.github.imsejin.dl.lezhin.process.Processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableList;

public final class ProcessorOrderResolver {

    public static List<Class<? extends Processor>> resolve(Set<Class<? extends Processor>> processorTypes) {
        if (CollectionUtils.isNullOrEmpty(processorTypes)) {
            return Collections.emptyList();
        }

        validate(processorTypes);

        Graph<Class<? extends Processor>> graph = createDependencyGraph(processorTypes);

        // Validates a dependency graph.
        if (graph.getVertexSize() != graph.getPathLength() + 1) {
            throw new InvalidProcessSpecificationException("Not linear dependency graph of process specification: " + graph);
        }

        Class<? extends Processor> startingProcessorType = processorTypes.stream()
                .filter(it -> it.getAnnotation(ProcessSpecification.class).dependsOn() == ProcessSpecification.INDEPENDENT)
                .findFirst().orElseThrow();
        Iterator<Class<? extends Processor>> iterator = new DepthFirstIterator<>(graph, startingProcessorType);

        return StreamUtils.toStream(iterator).collect(toUnmodifiableList());
    }

    // -------------------------------------------------------------------------------------------------

    private static void validate(Set<Class<? extends Processor>> processorTypes) {
        Collection<Class<? extends Processor>> rootSet = new ArrayList<>();

        for (Class<? extends Processor> processorType : processorTypes) {
            ProcessSpecification spec = processorType.getAnnotation(ProcessSpecification.class);
            if (spec == null) {
                throw new ProcessorNotSpecifyException("There is a processor that doesn't specify its specification; " +
                        "Annotate @ProcessSpecification on %s", processorType);
            }

            Class<?> dependentType = spec.dependsOn();
            if (dependentType == ProcessSpecification.INDEPENDENT) {
                rootSet.add(processorType);
                continue;
            }

            if (dependentType == Processor.class
                    || ClassUtils.isAbstractClass(dependentType)
                    || !Processor.class.isAssignableFrom(dependentType)) {
                throw new InvalidProcessSpecificationException("@ProcessSpecification.dependsOn must be a implementation of Processor: " +
                        "@ProcessSpecification(dependsOn = %s.class) %s", dependentType.getName(), processorType.getName());
            }

            if (dependentType == processorType) {
                throw new InvalidProcessSpecificationException("There is a self-referential @ProcessSpecification.dependsOn: %s", processorType);
            }
        }

        if (rootSet.isEmpty()) {
            throw new InvalidProcessSpecificationException("There is no @ProcessSpecification as a starting process; " +
                    "Must be only one @ProcessSpecification whose dependsOn is %s", ProcessSpecification.INDEPENDENT);
        }

        if (rootSet.size() > 1) {
            throw new InvalidProcessSpecificationException("There are two or more @ProcessSpecification as a starting process; " +
                    "Must be only one @ProcessSpecification whose dependsOn is %s: %s", ProcessSpecification.INDEPENDENT, rootSet);
        }
    }

    @SuppressWarnings("unchecked")
    private static Graph<Class<? extends Processor>> createDependencyGraph(Set<Class<? extends Processor>> processorTypes) {
        Graph<Class<? extends Processor>> graph = new DirectedGraph<>();
        processorTypes.forEach(graph::addVertex);

        for (Class<? extends Processor> processorType : processorTypes) {
            ProcessSpecification spec = processorType.getAnnotation(ProcessSpecification.class);

            Class<?> dependentType = spec.dependsOn();
            if (dependentType == ProcessSpecification.INDEPENDENT) {
                continue;
            }

            graph.addEdge((Class<? extends Processor>) dependentType, processorType);
        }

        return graph;
    }

}
