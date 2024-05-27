package com.github.mauricioaniche.ck.util;

import java.util.*;
import java.util.stream.Collectors;

public class WordCounter {

	private static final Set<String> javaKeywords = new HashSet<>(Set.of(
		"abstract", "continue", "for", "new", "switch",
		"assert***", "default", "goto*", "package", "synchronized",
		"boolean", "do", "if", "private", "this",
		"break", "double", "implements", "protected", "throw",
		"byte", "else", "import", "public", "throws",
		"case", "enum****", "instanceof", "return", "transient",
		"catch", "extends", "int", "short", "try",
		"char", "final", "interface", "static", "void",
		"class", "finally", "long", "strictfp**", "volatile",
		"const*", "float", "native", "super", "while",
		"String"
	));

	public static Set<String> wordsIn(String fullString) {
		String[] cleanString = fullString
				.replace("\t", " ")
				.replace("\n", " ")
				.replace("\r", " ")
				.replace("(", " ")
				.replace(")", " ")
				.replace("{", " ")
				.replace("}", " ")
				.replace("=", " ")
				.replace(">", " ")
				.replace(">", " ")
				.replace("&", " ")
				.replace("|", " ")
				.replace("!", " ")
				.replace("+", " ")
				.replace("*", " ")
				.replace("/", " ")
				.replace("-", " ")
				.replace(";", " ")
				.split(" ");

		List<String> strings = Arrays.stream(cleanString).filter(word -> !javaKeywords.contains(word))
				.filter(word -> !word.isEmpty())
				.filter(word -> word.matches("\\w*"))
				.filter(word -> !word.matches("[0-9]*"))
				.collect(Collectors.toList());

		HashSet<String> words = new HashSet<>();
		for(String string : strings) {
			words.addAll(breakString(string));
		}

		return words;

	}

	private static Collection<? extends String> breakString(String string) {

		if(string.length() == 1)
			return Arrays.asList(string);

		int current = 0;
		List<String> words = new ArrayList<>();

		for(int i = 1; i < string.length(); i++) {
			if(string.charAt(i) == '_' || Character.isUpperCase(string.charAt(i))) {
				String wordToAdd = string.substring(current, i);
				words.add(wordToAdd);
				current = i + (string.charAt(i) == '_' ? 1 : 0);
			}
		}
		String remainingWord = string.substring(current);
		words.add(remainingWord);
		return words;
	}


	public static String removeSpacesAndIdentation(String toString) {
		return toString
				.trim()
				.replace("\r", " ")
				.replace("\t", " ")
				.replace("\n", " ")
				.replaceAll(" +", " ");
	}

}
