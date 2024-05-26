package com.github.mauricioaniche.ck.metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.util.JDTUtils;

public class LCOMNormalized implements CKASTVisitor, ClassLevelMetric {

	HashMap<String, TreeSet<String>> declaredFields;
	ArrayList<String> methods;
	ArrayList<Integer> flags;
	
	public LCOMNormalized() {
		this.declaredFields = new HashMap<>();
		this.methods = new ArrayList<String>();
		this.flags = new ArrayList<Integer>();
	}
	
	public void visit(FieldDeclaration node) {
		
		for(Object o : node.fragments()) {
			VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
			declaredFields.put(vdf.getName().toString(), new TreeSet<String>());
		}
		
	}
	
	public void visit(SimpleName node) {
		String name = node.getFullyQualifiedName();
		if(declaredFields.containsKey(name)) {
			acessed(name);
		}
		
	}

	private void acessed(String name) {
		if(!methods.isEmpty()){
			this.declaredFields.get(name).add(this.methods.get(this.methods.size() - 1));
		}
	}
	
	public void visit(MethodDeclaration node) {
		
		String currentMethodName = JDTUtils.getMethodFullName(node);
		
		if(!this.methods.isEmpty()){
			if(this.methods.get(this.methods.size() - 1).equals(currentMethodName)){
				if(this.flags.get(this.flags.size() - 1) == 0){
					this.flags.set((this.flags.size() - 1), 1);
				}else{
					this.methods.add(currentMethodName);
					this.flags.add(0);
				}
			} else{
				this.methods.add(currentMethodName);
				this.flags.add(0);
			}
		} else {
			this.methods.add(currentMethodName);
			this.flags.add(0);
		}
		
	}
	
	@Override
	public void setResult(CKClassResult result) {
		float sum = 0;
		float lcomNormalized = 0;
		
		int numberOfAttributes = this.declaredFields.size();
		int numberOfMethods = this.methods.size();
		
		for (String key : this.declaredFields.keySet())
			sum = sum + (((float) (numberOfMethods - this.declaredFields.get(key).size())) / numberOfMethods);
		
		if(numberOfAttributes > 0)
			lcomNormalized = (((float) 1) * sum) / numberOfAttributes;
		
		result.setLcomNormalized(lcomNormalized);
		
	}	
	
}
