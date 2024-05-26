package com.github.mauricioaniche.ck.metric;

import com.github.mauricioaniche.ck.CKMethodResult;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class JavadocLines implements CKASTVisitor, MethodLevelMetric {

    int methodsVisited = 0;
    boolean hasJavadoc = false;

    public void visit(MethodDeclaration node) {
        methodsVisited++;
    }

    public void visit(Javadoc node) {
        if(methodsVisited == 1) {
            this.hasJavadoc = true;
        }
    }

    @Override
    public void setResult(CKMethodResult result) {
        result.setHasJavadoc(hasJavadoc);
    }
}
