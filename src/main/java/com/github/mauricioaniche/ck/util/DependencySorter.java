package com.github.mauricioaniche.ck.util;

import com.github.mauricioaniche.ck.metric.RunAfter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DependencySorter {

    public <T> List<Class<? extends T>> sort(List<Class<? extends T>> toSort) {
        Stack<Integer> sortedStack = new Stack<>();

        boolean[][] adjacencyMatrix = deriveAdjacencyMatrix(toSort);

        boolean visited[] = new boolean[toSort.size()];

        for (int i = 0; i < toSort.size(); i++)
            if (visited[i] == false)
                topologicalSort(i, visited, adjacencyMatrix, sortedStack);

        return sortedStack.stream().map(i -> toSort.get(i)).collect(Collectors.toList());
    }

    private void topologicalSort(int v, boolean[] visited, boolean[][] adjacencyMatrix, Stack<Integer> sortedStack) {
        visited[v] = true;

        IntStream.range(0, adjacencyMatrix[v].length)
                .filter(i -> adjacencyMatrix[v][i])
                .filter(i -> !visited[i])
                .forEach(i -> topologicalSort(i, visited, adjacencyMatrix, sortedStack));

        sortedStack.push(v);
    }

    private <T> boolean[][] deriveAdjacencyMatrix(List<Class<? extends T>> toSort) {

        final boolean[][] adjacencyMatrix = new boolean[toSort.size()][toSort.size()];

        IntStream.range(0, toSort.size())
                .filter(i -> toSort.get(i).getAnnotation(RunAfter.class) != null)
                .mapToObj(i -> Pair.of(i,toSort.get(i).getAnnotation(RunAfter.class).metrics()))

                .flatMap(p -> Arrays.stream(p.getValue())
                        .map(d -> Pair.of(p.getKey(),toSort.indexOf(d)))
                        .filter(d -> d.getValue() != -1))
                .forEach(p -> adjacencyMatrix[p.getKey()][p.getValue()] = true);

        return adjacencyMatrix;
    }

}
