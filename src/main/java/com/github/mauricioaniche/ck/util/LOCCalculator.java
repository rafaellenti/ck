package com.github.mauricioaniche.ck.util;

import com.github.mauricioaniche.ck.CKMetricsNumbers;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LOCCalculator {

	private static Logger log = Logger.getLogger(LOCCalculator.class);
	private static final int COMMENT_END_OFFSET = 2;

	private static int ckZeroNumber = CKMetricsNumbers.ckZero;
	private static int ckOneNumber = CKMetricsNumbers.ckOne;
	
	public static int calculate(String sourceCode) {
		try {
			InputStream is = IOUtils.toInputStream(sourceCode);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			return getNumberOfLines(reader);
		} catch (IOException e) {
			log.error("Error when counting lines", e);
			return ckZeroNumber;
		}
	}

	private static int getNumberOfLines(BufferedReader bReader)
			throws IOException {
		int count = ckZeroNumber;
		boolean commentBegan = false;
		String line = null;

		while ((line = bReader.readLine()) != null) {
			line = line.trim();
			if ("".equals(line) || line.startsWith("//")) {
				continue;
			}
			if (commentBegan) {
				if (commentEnded(line)) {
					line = line.substring(line.indexOf("*/") + COMMENT_END_OFFSET).trim();
					commentBegan = false;
					if ("".equals(line) || line.startsWith("//")) {
						continue;
					}
				} else
					continue;
			}
			if (isSourceCodeLine(line)) {
				count++;
			}
			if (commentBegan(line)) {
				commentBegan = true;
			}
		}
		return count;
	}

	private static boolean commentBegan(String line) {
		int index = line.indexOf("/*");
		if (index < ckZeroNumber) {
			return false;
		}
		int quoteStartIndex = line.indexOf("\"");
		if (quoteStartIndex != -ckOneNumber && quoteStartIndex < index) {
			while (quoteStartIndex > -ckOneNumber) {
				line = line.substring(quoteStartIndex + ckOneNumber);
				int quoteEndIndex = line.indexOf("\"");
				line = line.substring(quoteEndIndex + ckOneNumber);
				quoteStartIndex = line.indexOf("\"");
			}
			return commentBegan(line);
		}
		return !commentEnded(line.substring(index + COMMENT_END_OFFSET));
	}

	private static boolean commentEnded(String line) {
		int index = line.indexOf("*/");
		if (index < ckZeroNumber) {
			return false;
		} else {
			String subString = line.substring(index + COMMENT_END_OFFSET).trim();
			if ("".equals(subString) || subString.startsWith("//")) {
				return true;
			}

			return !commentBegan(subString);
		}
	}

	private static boolean isSourceCodeLine(String line) {
    line = line.trim();
    if (line.isEmpty() || line.startsWith("//")) {
        return false;
    }
    if (line.length() == ckOneNumber || !line.startsWith("/*")) {
        return true;
    }

    while (!line.isEmpty()) {
			line = line.substring(COMMENT_END_OFFSET);  // Remove the opening /*
			int endCommentPosition = line.indexOf("*/");
			if (endCommentPosition < ckZeroNumber) {
				return false;
			}
			line = line.substring(endCommentPosition + COMMENT_END_OFFSET).trim();  // Remove the closing */ and trim
			if (line.isEmpty() || line.startsWith("//")) {
				return false;
			}
			if (!line.startsWith("/*")) {
				return true;
			}
    }

    return false;
	}

}
