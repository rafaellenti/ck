package com.github.mauricioaniche.ck.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileUtils {
  public static final List<String> IGNORED_DIRECTORIES = new ArrayList<>();

  static {
    IGNORED_DIRECTORIES.add(String.format("%c.git%c", File.separatorChar, File.separatorChar));
  }

	public static String[] getAllDirs(String path) {
		try {
			return Files.walk(Paths.get(path))
					.filter(Files::isDirectory)
          .filter(FileUtils::isHiddenDir)
					.filter(x -> !isIgnoredDir(x.toAbsolutePath().toString(), IGNORED_DIRECTORIES))
					.map(x -> x.toAbsolutePath().toString())
					.toArray(String[]::new);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String[] getAllJavaFiles(String path) {
		return getAllFiles(path, "java");
	}

	public static String[] getAllJars(String path) {
		return getAllFiles(path, "jar");
	}

	private static String[] getAllFiles(String path, String ending){
		try {
			return Files.walk(Paths.get(path))
					.filter(Files::isRegularFile)
					.filter(x -> !isIgnoredDir(x.toAbsolutePath().toString(), IGNORED_DIRECTORIES))
					.filter(x -> x.toAbsolutePath().toString().toLowerCase().endsWith(ending))
					.map(x -> x.toAbsolutePath().toString())
					.toArray(String[]::new);
		} catch(Exception e) {
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