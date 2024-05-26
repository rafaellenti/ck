package com.github.mauricioaniche.ck;

import com.github.mauricioaniche.ck.util.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Runner {

	public static void main(String[] args) throws IOException {

		if (args == null || args.length < 1) {
			System.out.println("Usage java -jar ck.jar <path to project> <use Jars=true|false> <max files per partition, 0=automatic selection> <print variables and fields metrics? True|False> <path to save the output files>");
			System.exit(1);
		}

		String path = args[0];

		boolean useJars = false;
		if(args.length >= 2)
			useJars = Boolean.parseBoolean(args[1]);

		int maxAtOnce = 0;
		if(args.length >= 3)
			maxAtOnce = Integer.parseInt(args[2]);

		boolean variablesAndFields = true;
		if(args.length >= 4)
			variablesAndFields = Boolean.parseBoolean(args[3]);
		
		String outputDir = "";
		if(args.length >= 5)
			outputDir = args[4];

    for (int i = 5; i < args.length; i++) {
      FileUtils.IGNORED_DIRECTORIES.add(args[i]);
    }

		ResultWriter writer = new ResultWriter(outputDir + "class.csv", outputDir + "method.csv", outputDir + "variable.csv", outputDir + "field.csv", variablesAndFields);
		
		Map<String, CKClassResult> results = new HashMap<>();
		
		new CK(useJars, maxAtOnce, variablesAndFields).calculate(path, new CKNotifier() {
			@Override
			public void notify(CKClassResult result) {
				
				results.put(result.getClassName(), result);
				
			}

			@Override
			public void notifyError(String sourceFilePath, Exception e) {
				System.err.println("Error in " + sourceFilePath);
				e.printStackTrace(System.err);
			}
		});
		
		for(Map.Entry<String, CKClassResult> entry : results.entrySet()){
			writer.printResult(entry.getValue());
		}
		
		writer.flushAndClose();
		System.out.println("Metrics extracted!!!");
	}
}
