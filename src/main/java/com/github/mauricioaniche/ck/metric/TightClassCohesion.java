package com.github.mauricioaniche.ck.metric;

import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKMethodResult;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RunAfter(metrics={RFC.class, MethodLevelFieldUsageCount.class, MethodInvocationsLocal.class})
public class TightClassCohesion implements CKASTVisitor, ClassLevelMetric {
    private HashMap<String, Set<String>> accessedFields = new HashMap<>();

    private Set<ImmutablePair<String, String>> getDirectConnections(CKClassResult result){
        for (CKMethodResult method : result.getMethods()){
            accessedFields.put(method.getMethodName(), method.getFieldsAccessed());
        }

        HashMap<String, Set<String>> allAccessedFields = new HashMap<>();
        for (CKMethodResult method : result.getVisibleMethods()){
            Set<String> allLocalFields = collectAccessedFields(method);
            allLocalFields.addAll(method.getFieldsAccessed());
            allAccessedFields.put(method.getMethodName(), allLocalFields);
        }

        Set<ImmutablePair<String, String>> directConnections = new HashSet<>();
        for(String firstKey : allAccessedFields.keySet()){
            for(String secondKey : allAccessedFields.keySet()){
                Set<String> accessedFieldsFirst = Sets.newHashSet(allAccessedFields.get(firstKey));
                Set<String> accessedFieldsSecond = allAccessedFields.get(secondKey);
                accessedFieldsFirst.retainAll(accessedFieldsSecond);
                if(!firstKey.equals(secondKey) && accessedFieldsFirst.size() > 0){
                    directConnections.add(new ImmutablePair<String, String>(firstKey, secondKey));
                }
            }
        }
        return directConnections;
    }

    private Set<String> collectAccessedFields(CKMethodResult method){
        Set<String> allLocalInvocations = method.getMethodInvocationsIndirectLocal().keySet();

        Set<String> allLocalFields = new HashSet<>();
        for (String invocation : allLocalInvocations){
            Set<String> currentFields = accessedFields.get(invocation);
            if(currentFields != null)
                allLocalFields.addAll(currentFields);
        }

        return allLocalFields;
    }

    private Set<ImmutablePair<String, String>> getIndirectConnections(CKClassResult result, Set<ImmutablePair<String, String>> directConnections){
        HashMap<String, Set<String>> directConnectionsMap = new HashMap<>();
        for(CKMethodResult method : result.getMethods()){
            directConnectionsMap.put(method.getMethodName(), Sets.newHashSet(Sets.newHashSet(ArrayUtils.EMPTY_STRING_ARRAY)));
        }
        for(ImmutablePair<String, String> pair : directConnections){
            directConnectionsMap.get(pair.left).add(pair.right);
        }

        HashMap<String, Set<String>> indirectConnectionsMap = new HashMap<>();
        for (CKMethodResult method : result.getVisibleMethods()){
            Set<String> localConnections = extractConnections(method.getMethodName(), new HashSet<>(), directConnectionsMap);
            indirectConnectionsMap.put(method.getMethodName(), localConnections);
        }

        Set<ImmutablePair<String, String>> indirectConnections = new HashSet<>();
        for(String key : indirectConnectionsMap.keySet()){
            indirectConnections.addAll(indirectConnectionsMap.get(key).stream()
                    .filter(right -> !key.equals(right))
                    .map(right -> new ImmutablePair<String, String>(key, right))
                    .collect(Collectors.toSet()));
        }

        indirectConnections.removeAll(directConnections);
        return indirectConnections;
    }

    private Set<String> extractConnections(String currentConnection, Set<String> explored, HashMap<String, Set<String>> connections){
        explored.add(currentConnection);

        Set<String> nextConnections = connections.get(currentConnection).stream()
                .filter(connection -> !explored.contains(connection))
                .collect(Collectors.toSet());
        explored.addAll(nextConnections);
        for (String nextConnection : nextConnections){
            explored.addAll(extractConnections(nextConnection, explored, connections));
        }

        return explored;
    }

    public void setResult(CKClassResult result) {
        if(result.getVisibleMethods().size() < 1){
            result.setTightClassCohesion(-1);
            result.setLooseClassCohesion(-1);
        } else {
            float np = (float) result.getVisibleMethods().size() * (result.getVisibleMethods().size() - 1);

            Set<ImmutablePair<String, String>> directConnections = getDirectConnections(result);
            result.setTightClassCohesion(directConnections.size() / np);

            Set<ImmutablePair<String, String>> indirectConnections = getIndirectConnections(result, directConnections);
            result.setLooseClassCohesion((directConnections.size() + indirectConnections.size()) / np);
        }
    }
}