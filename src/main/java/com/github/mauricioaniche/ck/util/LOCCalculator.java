package com.github.mauricioaniche.ck.util;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LOCCalculator {

	private static Logger log = Logger.getLogger(LOCCalculator.class);
	private static final int COMMENT_END_OFFSET = 2

	private int ckZeroNumber = CKMetricsNumbers.ckZero
	private int ckOneNumber = CKMetricsNumbers.ckOne
	
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

	/**
	 *
	 * @param line
	 * @return This method checks if in the given line a comment has begun and has not ended
	 */
	private static boolean commentBegan(String line) {
		// If line = /* */, this method will return false
		// If line = /* */ /*, this method will return true
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

	/**
	 *
	 * @param line
	 * @return This method checks if in the given line a comment has ended and no new comment has not begun
	 */
	private static boolean commentEnded(String line) {
		// If line = */ /* , this method will return false
		// If line = */ /* */, this method will return true
		int index = line.indexOf("*/");
		if (index < ckZeroNumber) {
			return false;
		} else {
			String subString = line.substring(index + COMMENT_END_OFFSET).trim();
			if ("".equals(subString) || subString.startsWith("//")) {
				return true;
			}
			if(commentBegan(subString))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
	}

	/**
	 *
	 * @param line
	 * @return This method returns true if there is any valid source code in the given input line. It does not worry if comment has begun or not.
	 * This method will work only if we are sure that comment has not already begun previously. Hence, this method should be called only after {@link #commentBegan(String)} is called
	 */
	private static boolean isSourceCodeLine(String line) {
		boolean isSourceCodeLine = false;
		line = line.trim();
		if ("".equals(line) || line.startsWith("//")) {
			return isSourceCodeLine;
		}
		if (line.length() == ckOneNumber) {
			return true;
		}
		int index = line.indexOf("/*");
		if (index != ckZeroNumber) {
			return true;
		} else {
			while (line.length() > ckZeroNumber) {
				line = line.substring(index + COMMENT_END_OFFSET);
				int endCommentPosition = line.indexOf("*/");
				if (endCommentPosition < ckZeroNumber) {
					return false;
				}
				if (endCommentPosition == line.length() - COMMENT_END_OFFSET) {
					return false;
				} else {
					String subString = line.substring(endCommentPosition + COMMENT_END_OFFSET)
							.trim();
					if ("".equals(subString) || subString.indexOf("//") == ckZeroNumber) {
						return false;
					} else {
						if (subString.startsWith("/*")) {
							line = subString;
							continue;
						}
						return true;
					}
				}

			}
		}
		return isSourceCodeLine;
	}

}
