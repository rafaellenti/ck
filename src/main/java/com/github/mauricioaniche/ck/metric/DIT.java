package com.github.mauricioaniche.ck.metric;

import com.github.mauricioaniche.ck.CKClassResult;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class DIT implements CKASTVisitor, ClassLevelMetric {
	int ditCounter = 1;

	@Override
	public void visit(TypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		if(binding!=null) calculate(binding);

	}

	private void calculate(ITypeBinding binding) {
		ITypeBinding father = binding.getSuperclass();
		if (father != null) {
			String fatherName = father.getQualifiedName();
			if (fatherName.endsWith("Object")) return;
			ditCounter++;

			calculate(father);
		}

	}

	@Override
	public void setResult(CKClassResult result) {
		result.setDit(ditCounter);
	}

}
