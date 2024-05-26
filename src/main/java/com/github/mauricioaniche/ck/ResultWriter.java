package com.github.mauricioaniche.ck;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ResultWriter {

    private static final String[] CLASS_HEADER = {
            "file",
            "class",
            "type",

            "cbo",
            "cboModified",
            "fanin",
            "fanout",
            "wmc",
            "dit",
            "noc",
            "rfc",
            "lcom",
            "lcom*",
            "tcc",
            "lcc",

            "totalMethodsQty",
            "staticMethodsQty",
            "publicMethodsQty",
            "privateMethodsQty",
            "protectedMethodsQty",
            "defaultMethodsQty",
            "visibleMethodsQty",
            "abstractMethodsQty",
            "finalMethodsQty",
            "synchronizedMethodsQty",

            "totalFieldsQty",
            "staticFieldsQty",
            "publicFieldsQty",
            "privateFieldsQty",
            "protectedFieldsQty",
            "defaultFieldsQty",
            "finalFieldsQty",
            "synchronizedFieldsQty",

            "nosi",
            "loc",
            "returnQty",
            "loopQty",
            "comparisonsQty",
            "tryCatchQty",
            "parenthesizedExpsQty",
            "stringLiteralsQty",
            "numbersQty",
            "assignmentsQty",
            "mathOperationsQty",
            "variablesQty",
            "maxNestedBlocksQty",
            "anonymousClassesQty",
            "innerClassesQty",
            "lambdasQty",
            "uniqueWordsQty",
            "modifiers",
            "logStatementsQty"};
    private static final String[] METHOD_HEADER = { 
            "file", 
            "class", 
            "method", 
            "constructor", 
            "line", 
            "cbo", 
            "cboModified",
            "fanin",
            "fanout",
            "wmc", 
            "rfc", 
            "loc",
            "returnsQty", 
            "variablesQty", 
            "parametersQty", 
            "methodsInvokedQty", 
            "methodsInvokedLocalQty", 
            "methodsInvokedIndirectLocalQty", 
            "loopQty", 
            "comparisonsQty", 
            "tryCatchQty",
            "parenthesizedExpsQty", 
            "stringLiteralsQty", 
            "numbersQty", 
            "assignmentsQty", 
            "mathOperationsQty",
            "maxNestedBlocksQty", 
            "anonymousClassesQty", 
            "innerClassesQty", 
            "lambdasQty", 
            "uniqueWordsQty", 
            "modifiers", 
            "logStatementsQty", 
            "hasJavaDoc" };
                          
    private static final String[] VAR_FIELD_HEADER = { "file", "class", "method", "variable", "usage" };
    private final boolean variablesAndFields;

    private CSVPrinter classPrinter;
    private CSVPrinter methodPrinter;
    private CSVPrinter variablePrinter;
    private CSVPrinter fieldPrinter;

    public ResultWriter(String classFile, String methodFile, String variableFile, String fieldFile, boolean variablesAndFields) throws IOException {
        FileWriter classOut = new FileWriter(classFile);
        this.classPrinter = new CSVPrinter(classOut, CSVFormat.DEFAULT.withHeader(CLASS_HEADER));
        FileWriter methodOut = new FileWriter(methodFile);
        this.methodPrinter = new CSVPrinter(methodOut, CSVFormat.DEFAULT.withHeader(METHOD_HEADER));

        this.variablesAndFields = variablesAndFields;
        if(variablesAndFields) {
            FileWriter variableOut = new FileWriter(variableFile);
            this.variablePrinter = new CSVPrinter(variableOut, CSVFormat.DEFAULT.withHeader(VAR_FIELD_HEADER));
            FileWriter fieldOut = new FileWriter(fieldFile);
            this.fieldPrinter = new CSVPrinter(fieldOut, CSVFormat.DEFAULT.withHeader(VAR_FIELD_HEADER));
        }
    }

    public void printResult(CKClassResult result) throws IOException {

        this.classPrinter.printRecord(
                result.getFile(),
                result.getClassName(),
                result.getType(),

                result.getCbo(),
                result.getCboModified(),
                result.getFanin(),
                result.getFanout(),
                result.getWmc(),
                result.getDit(),
                result.getNoc(),
                result.getRfc(),
                result.getLcom(),
                result.getLcomNormalized(),
                result.getTightClassCohesion(),
                result.getLooseClassCohesion(),

                result.getNumberOfMethods(),
                result.getNumberOfStaticMethods(),
                result.getNumberOfPublicMethods(),
                result.getNumberOfPrivateMethods(),
                result.getNumberOfProtectedMethods(),
                result.getNumberOfDefaultMethods(),
                result.getVisibleMethods().size(),
                result.getNumberOfAbstractMethods(),
                result.getNumberOfFinalMethods(),
                result.getNumberOfSynchronizedMethods(),

                result.getNumberOfFields(),
                result.getNumberOfStaticFields(),
                result.getNumberOfPublicFields(),
                result.getNumberOfPrivateFields(),
                result.getNumberOfProtectedFields(),
                result.getNumberOfDefaultFields(),
                result.getNumberOfFinalFields(),
                result.getNumberOfSynchronizedFields(),

                result.getNosi(),
                result.getLoc(),
                result.getReturnQty(),
                result.getLoopQty(),
                result.getComparisonsQty(),
                result.getTryCatchQty(),
                result.getParenthesizedExpsQty(),
                result.getStringLiteralsQty(),
                result.getNumbersQty(),
                result.getAssignmentsQty(),
                result.getMathOperationsQty(),
                result.getVariablesQty(),
                result.getMaxNestedBlocks(),
                result.getAnonymousClassesQty(),
                result.getInnerClassesQty(),
                result.getLambdasQty(),
                result.getUniqueWordsQty(),
                result.getModifiers(),
                result.getNumberOfLogStatements());

        for (CKMethodResult method : result.getMethods()) {
            this.methodPrinter.printRecord(
                    result.getFile(), 
                    result.getClassName(), 
                    method.getMethodName(),
                    method.isConstructor(),
                    method.getStartLine(), 
                    method.getCbo(), 
                    method.getCboModified(), 
                    method.getFanin(), 
                    method.getFanout(), 
                    method.getWmc(), 
                    method.getRfc(), 
                    method.getLoc(),
                    method.getReturnQty(), 
                    method.getVariablesQty(), 
                    method.getParametersQty(),
                    method.getMethodInvocations().size(), 
                    method.getMethodInvocationsLocal().size(), 
                    method.getMethodInvocationsIndirectLocal().size(),
                    method.getLoopQty(), 
                    method.getComparisonsQty(), 
                    method.getTryCatchQty(),
                    method.getParenthesizedExpsQty(), 
                    method.getStringLiteralsQty(), 
                    method.getNumbersQty(),
                    method.getAssignmentsQty(), 
                    method.getMathOperationsQty(), 
                    method.getMaxNestedBlocks(),
                    method.getAnonymousClassesQty(), 
                    method.getInnerClassesQty(), 
                    method.getLambdasQty(),
                    method.getUniqueWordsQty(), 
                    method.getModifiers(), 
                    method.getLogStatementsQty(), 
                    method.getHasJavadoc());

            if(variablesAndFields) {
                for (Map.Entry<String, Integer> entry : method.getVariablesUsage().entrySet()) {
                    this.variablePrinter.printRecord(result.getFile(), result.getClassName(), method.getMethodName(),
                            entry.getKey(), entry.getValue());
                }

                for (Map.Entry<String, Integer> entry : method.getFieldUsage().entrySet()) {
                    this.fieldPrinter.printRecord(result.getFile(), result.getClassName(), method.getMethodName(),
                            entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public void flushAndClose() throws IOException {
        this.classPrinter.flush();
        this.classPrinter.close();
        this.methodPrinter.flush();
        this.methodPrinter.close();
        if(variablesAndFields) {
            this.variablePrinter.flush();
            this.variablePrinter.close();
            this.fieldPrinter.flush();
            this.fieldPrinter.close();
        }
    }
}
