package com.github.mauricioaniche.ck.metric;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.github.mauricioaniche.ck.CKClassResult;

public class NOC implements CKASTVisitor, ClassLevelMetric{
	private NOCExtras extras;
	
	public NOC() {
		this.extras = NOCExtras.getInstance();
	}
	
	@Override
	public void visit(TypeDeclaration node){
		ITypeBinding binding = node.resolveBinding();
		
		if(binding != null){
			ITypeBinding father = binding.getSuperclass();
			if(father != null){
				this.extras.plusOne(father.getQualifiedName());
			}
		} else {
			Type type = node.getSuperclassType();
			
			SimpleType castedFatherType = null;
			
			if(type instanceof SimpleType)
				castedFatherType = ((SimpleType) type);
			
			if(castedFatherType != null){
				this.extras.plusOne(castedFatherType.getName().getFullyQualifiedName());
			}
			List<Type> list = node.superInterfaceTypes();
			list = list.stream().filter(x -> (x instanceof SimpleType)).collect(Collectors.toList());
			list.stream().map(x -> (SimpleType) x).forEach(x -> this.extras.plusOne(x.getName().getFullyQualifiedName()));
		}
		
	}
	
	@Override
	public void setResult(CKClassResult result) {
		
	}

}
