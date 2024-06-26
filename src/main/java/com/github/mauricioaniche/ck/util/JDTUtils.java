package com.github.mauricioaniche.ck.util;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JDTUtils {

	private static final String EMPTY_STRING = "";
    private static final String FORMAT_SIGNATURE = "%d%s%s%s";
    private static final String SQUARE_BRACKET_OPEN = "[";
    private static final String SQUARE_BRACKET_CLOSE = "]";
    private static final String COMMA = ",";
    private static final String VARARGS_SUFFIX = "[]";
	private static final String SLASH = "/";


	public static int getStartLine(CompilationUnit cu, MethodDeclaration node) {
		return node.getBody() != null ?
				cu.getLineNumber(node.getBody().getStartPosition()) :
				cu.getLineNumber(node.getStartPosition());
	}

	public static int getStartLine(CompilationUnit cu, Initializer node) {
		return node.getBody() != null ?
				cu.getLineNumber(node.getBody().getStartPosition()) :
				cu.getLineNumber(node.getStartPosition());
	}

	public static String getMethodFullName(IMethodBinding binding) {
		String methodName = binding.getName();
		return methodName + SLASH + getMethodSignature(binding);
	}

	public static String getMethodFullName(MethodDeclaration node) {
		if(node.resolveBinding() != null){
			return getMethodFullName(node.resolveBinding());
		}
		String methodName = node.getName().getFullyQualifiedName();
		return methodName + SLASH + getMethodSignature(node);
	}

	public static String getQualifiedMethodFullName(IMethodBinding binding){
		String methodName = binding.getName();
		if(binding.getDeclaringClass() != null){
			methodName = binding.getDeclaringClass().getQualifiedName() + "." + binding.getName();
		}
		return methodName + SLASH + getMethodSignature(binding);
	}

	public static String getQualifiedMethodFullName(MethodDeclaration node) {
		if(node.resolveBinding() != null){
			return getQualifiedMethodFullName(node.resolveBinding());
		}
		String methodName = node.getName().getFullyQualifiedName();
		return methodName + SLASH + getMethodSignature(node);
	}

	public static String getQualifiedMethodFullName(MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		if(binding != null){
			return getQualifiedMethodFullName(binding);
		} else {
			return node.getName().getFullyQualifiedName() + SLASH + getMethodSignature(node.arguments(), node.typeArguments());
		}
	}

	public static String getQualifiedMethodFullName(SuperMethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		if(binding != null){
			return getQualifiedMethodFullName(binding);
		} else if(node.getQualifier() != null){
			return node.getQualifier().getFullyQualifiedName() + getMethodSignature(node.arguments(), node.typeArguments());
		}
		return node.getName().getFullyQualifiedName() + SLASH + getMethodSignature(node.arguments(), node.typeArguments());
	}

	public static String getMethodSignature(IMethodBinding node){
		int parameterCount = node.getParameterTypes()==null ? 0 : node.getParameterTypes().length;
		List<String> parameterTypes = new ArrayList<>();

		if(parameterCount > 0) {
			for(ITypeBinding binding : node.getParameterTypes()) {

				String v = binding.getQualifiedName();

				parameterTypes.add(v);
			}
		}
		return formatSignature(parameterTypes);
	}

	public static String getMethodSignature(MethodDeclaration node){
		int parameterCount = node.parameters()==null ? 0 : node.parameters().size();
		List<String> parameterTypes = new ArrayList<>();

		if(parameterCount > 0) {
			for(Object p0 : node.parameters()) {
				SingleVariableDeclaration parameter = (SingleVariableDeclaration) p0;

				ITypeBinding binding = parameter.getType().resolveBinding();

				String v;
				if(binding == null || binding.isRecovered())
					v = parameter.getType().toString();
				else
					v = binding.getQualifiedName();

				if(parameter.isVarargs()) v+=VARARGS_SUFFIX;

				parameterTypes.add(v);
			}
		}

		return formatSignature(parameterTypes);
	}

	private static String getMethodSignature(List<?> arguments, List<?> typeArguments) {
		int argumentCount = arguments != null ? arguments.size() : 0;
		List<String> parameterTypes = typeArguments.stream().map(object -> object.toString()).collect(Collectors.toList());
		return formatSignature(parameterTypes);
	}

	private static String formatSignature(List<String> parameters){
		int parameterCount = parameters.size();
		return String.format(FORMAT_SIGNATURE,
				parameterCount,
				(parameterCount > 0 ? SQUARE_BRACKET_OPEN : EMPTY_STRING),
				(parameterCount > 0 ? String.join(COMMA, parameters) : EMPTY_STRING),
				(parameterCount > 0 ? SQUARE_BRACKET_CLOSE : EMPTY_STRING)
		);
	}

	public static List<String> getVariableName(List<VariableDeclarationFragment> fragments){
		if (fragments != null)
			return fragments.stream().map(fragment -> fragment.getName().getIdentifier()).collect(Collectors.toList());
		return Collections.emptyList();
	}
}