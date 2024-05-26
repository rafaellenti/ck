package com.github.mauricioaniche.ck;

import com.github.mauricioaniche.ck.util.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Runner {

	private static int ckZeroNumber = CKMetricsNumbers.ckZero;
	private static int ckOneNumber = CKMetricsNumbers.ckOne;
	private static int ckThree = CKMetricsNumbers.ckThree;
	private static int ckFour = CKMetricsNumbers.ckFour;

	public static final int JARS_LENGTH = 2;
	public static final int FILES_PARTITION_LENGTH = 3;
	public static final int FIELDS_RESULT_LENGTH = 4;
	public static final int OUTPUT_LENGTH = 5;

	public static void main(String[] args) throws IOException {

		if (args == null || args.length < ckOneNumber) {
			System.out.println("Usage java -jar ck.jar <path to project> <use Jars=true|false> <max files per partition, 0=automatic selection> <print variables and fields metrics? True|False> <path to save the output files>");
			System.exit(ckOneNumber);
		}

		String path = args[ckZeroNumber];

		// use jars?
		boolean useJars = false;
		if(args.length >= JARS_LENGTH)
			useJars = Boolean.parseBoolean(args[ckOneNumber]);

		// number of files per partition?
		int maxAtOnce = ckZeroNumber;
		if(args.length >= FILES_PARTITION_LENGTH)
			maxAtOnce = Integer.parseInt(args[JARS_LENGTH]);

		// variables and field results?
		boolean variablesAndFields = true;
		if(args.length >= FIELDS_RESULT_LENGTH)
			variablesAndFields = Boolean.parseBoolean(args[ckThree]);
		
		// path where the output csv files will be exported
		String outputDir = "";
		if(args.length >= OUTPUT_LENGTH)
			outputDir = args[ckFour];

    // load possible additional ignored directories
    //noinspection ManualArrayToCollectionCopy
    for (int i = OUTPUT_LENGTH; i < args.length; i++) {
      FileUtils.IGNORED_DIRECTORIES.add(args[i]);
    }

		ResultWriter writer = new ResultWriter(outputDir + "class.csv", outputDir + "method.csv", outputDir + "variable.csv", outputDir + "field.csv", variablesAndFields);
		
		Map<String, CKClassResult> results = new HashMap<>();
		
		new CK(useJars, maxAtOnce, variablesAndFields).calculate(path, new CKNotifier() {
			@Override
			public void notify(CKClassResult result) {
				
				// Store the metrics values from each component of the project in a HashMap
				results.put(result.getClassName(), result);
				
			}

			@Override
			public void notifyError(String sourceFilePath, Exception e) {
				System.err.println("Error in " + sourceFilePath);
				e.printStackTrace(System.err);
			}
		});
		
		// Write the metrics value of each component in the csv files
		for(Map.Entry<String, CKClassResult> entry : results.entrySet()){
			writer.printResult(entry.getValue());
		}
		
		writer.flushAndClose();
		System.out.println("Metrics extracted!!!");
	}
}
