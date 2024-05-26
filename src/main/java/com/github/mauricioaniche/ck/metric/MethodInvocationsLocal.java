package com.github.mauricioaniche.ck.metric;

import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKMethodResult;
import java.util.*;
import java.util.stream.Collectors;

@RunAfter(metrics={RFC.class, MethodLevelFieldUsageCount.class})
public class MethodInvocationsLocal implements CKASTVisitor, ClassLevelMetric {
    private Map<String, Set<String>> invocations(String invokedMethod, Map<String, Set<String>> explored, HashMap<String, Set<String>> invocations){
        Set<String> exploredKeys = explored.keySet();
        Set<String> nextInvocations = invocations.get(invokedMethod).stream()
                .filter(invoked -> !exploredKeys.contains(invoked) && !invoked.equals(invokedMethod))
                .collect(Collectors.toSet());
        if(nextInvocations.size() > 0){
            explored.put(invokedMethod, nextInvocations);

            for (String nextInvocation : nextInvocations){
                explored = invocations(nextInvocation, explored, invocations);
            }
        }

        return explored;
    }

    private HashMap<String, Map<String, Set<String>>> invocationsIndirect(Set<CKMethodResult> methods, HashMap<String, Set<String>> methodInvocationsLocal){
        HashMap<String, Map<String, Set<String>>> methodInvocationsIndirectLocal = new HashMap<>();

        for (CKMethodResult method : methods){
            Map<String, Set<String>> localInvocations =  invocations(method.getQualifiedMethodName(), new HashMap(), methodInvocationsLocal);
            methodInvocationsIndirectLocal.put(method.getQualifiedMethodName(), localInvocations);
        }
        return methodInvocationsIndirectLocal;
    }

    private HashMap<String, Set<String>> extractLocalInvocations(Set<CKMethodResult> methods){
        HashMap<String, Set<String>> methodInvocationsLocal = new HashMap<>();

        Set<String> methodNames = methods.stream().map(CKMethodResult::getQualifiedMethodName).collect(Collectors.toSet());
        for (CKMethodResult method : methods){
            Set<String> invokedLocal =  method.getMethodInvocations().stream()
                    .filter(methodNames::contains)
                    .collect(Collectors.toSet());
            methodInvocationsLocal.put(method.getQualifiedMethodName(), invokedLocal);
        }
        return methodInvocationsLocal;
    }

    public void setResult(CKClassResult result) {
        Set<CKMethodResult> methods = result.getMethods();
        HashMap<String, Set<String>> methodInvocationsLocal = extractLocalInvocations(methods);
        for (CKMethodResult method : methods){
            method.setMethodInvocationLocal(methodInvocationsLocal.get(method.getQualifiedMethodName()));
        }

        HashMap<String, Map<String, Set<String>>> methodInvocationsIndirectLocal = invocationsIndirect(methods, methodInvocationsLocal);
        for (CKMethodResult method : methods){
            method.setMethodInvocationsIndirectLocal(methodInvocationsIndirectLocal.get(method.getQualifiedMethodName()));
        }
    }
}