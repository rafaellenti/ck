package com.github.mauricioaniche.ck.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {
  private static final String DOT_GIT = String.format("%c.git%c", File.separatorChar, File.separatorChar);
  private static final String JAVA_EXTENSION = "java";
  private static final String JAR_EXTENSION = "jar";

  public static final List<String> IGNORED_DIRECTORIES = new ArrayList<>();

  static {
    IGNORED_DIRECTORIES.add(DOT_GIT);
  }

	public static String[] getAllDirs(String path) {
		try (Stream<Path> paths = Files.walk(Paths.get(path))) {
			return paths
					.filter(Files::isDirectory)
					.filter(FileUtils::isHiddenDir)
					.filter(x -> !isIgnoredDir(x.toAbsolutePath().toString(), IGNORED_DIRECTORIES))
					.map(x -> x.toAbsolutePath().toString())
					.toArray(String[]::new);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String[] getAllJavaFiles(String path) {
		return getAllFiles(path, JAVA_EXTENSION);
	}

	public static String[] getAllJars(String path) {
		return getAllFiles(path, JAR_EXTENSION);
	}

	private static String[] getAllFiles(String path, String ending) {
		try (Stream<Path> paths = Files.walk(Paths.get(path))) {
			return paths
					.filter(Files::isRegularFile)
					.filter(x -> !isIgnoredDir(x.toAbsolutePath().toString(), IGNORED_DIRECTORIES))
					.filter(x -> x.toAbsolutePath().toString().toLowerCase().endsWith(ending))
					.map(x -> x.toAbsolutePath().toString())
					.toArray(String[]::new);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	

  public static boolean isHiddenDir(Path path) {
    try {
      return Files.isHidden(path);
    } catch (IOException exception) {
      exception.printStackTrace();
      return false;
    }
  }

  public static boolean isIgnoredDir(String path, Collection<String> blocked) {
    for (String ignoredDirectory : blocked) {
      if (path.contains(ignoredDirectory)) {
        return true;
      }
    }
    return false;
  }
}