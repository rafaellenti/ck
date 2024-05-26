package com.github.mauricioaniche.ck.util;

import com.github.mauricioaniche.ck.metric.ClassLevelMetric;
import com.github.mauricioaniche.ck.metric.MethodLevelMetric;
import com.github.mauricioaniche.ck.metric.RunAfter;
import com.github.mauricioaniche.ck.metric.VariableOrFieldMetric;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

public class MetricsFinder {

	private static final String RUNTIME_EXCEPTION = "Could not instantiate a method level metric. Something is really wrong";
	private static final String METHOD_EXCEPTION = "Could not find method level metrics. Something is really wrong";
	private static final String CLASS_EXCEPTION = "Could not find class level metrics. Something is really wrong";

	private static final String GIT_LOCATION = "com.github.mauricioaniche.ck.metric";

	private static List<Class<? extends MethodLevelMetric>> methodLevelClasses = null;
	private static List<Class<? extends ClassLevelMetric>> classLevelClasses = null;
	private DependencySorter sorter;

	public MetricsFinder(DependencySorter sorter) {
		this.sorter = sorter;
	}

	public MetricsFinder() {
		this(new DependencySorter());
	}

	public List<MethodLevelMetric> allMethodLevelMetrics(boolean variablesAndFields) {
		if(methodLevelClasses == null)
			loadMethodLevelClasses(variablesAndFields);

		try {
			ArrayList<MethodLevelMetric> metrics = new ArrayList<>();
			for (Class<? extends MethodLevelMetric> aClass : methodLevelClasses) {
				metrics.add(aClass.getDeclaredConstructor().newInstance());
			}

			return metrics;
		} catch(Exception e) {
			throw new RuntimeException(RUNTIME_EXCEPTION, e);
		}
	}

	public List<ClassLevelMetric> allClassLevelMetrics() {

		if(classLevelClasses == null)
			loadClassLevelClasses();

		try {
			ArrayList<ClassLevelMetric> metrics = new ArrayList<>();
			for (Class<? extends ClassLevelMetric> aClass : classLevelClasses) {
				metrics.add(aClass.getDeclaredConstructor().newInstance());
			}

			return metrics;
		} catch(Exception e) {
			throw new RuntimeException(RUNTIME_EXCEPTION, e);
		}
	}

	private void loadMethodLevelClasses(boolean variablesAndFields) {
		try {
			Reflections reflections = new Reflections(GIT_LOCATION);

			methodLevelClasses = sorter.sort(reflections.getSubTypesOf(MethodLevelMetric.class).stream()
					.filter(x -> variablesAndFields || !Arrays.asList(x.getInterfaces()).contains(VariableOrFieldMetric.class))
					.collect(Collectors.toList()));

		} catch(Exception e) {
			throw new RuntimeException(METHOD_EXCEPTION, e);
		}
	}

	private void loadClassLevelClasses() {
		try {
			Reflections reflections = new Reflections(GIT_LOCATION);
			classLevelClasses = sorter.sort(new ArrayList<>(reflections.getSubTypesOf(ClassLevelMetric.class)));
		} catch(Exception e) {
			throw new RuntimeException(CLASS_EXCEPTION, e);
		}
	}


}
