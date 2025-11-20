/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     IBM Corporation - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.support;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.osgi.util.NLS;

/**
 * @deprecated use {@link org.eclipse.mylyn.commons.sdk.util.CommonTestUtil} instead
 */
@Deprecated
@SuppressWarnings("nls")
public class CommonTestUtil {

	private final static int MAX_RETRY = 5;

	/**
	 * Returns the given file path with its separator character changed from the given old separator to the given new separator.
	 *
	 * @param path
	 *            a file path
	 * @param oldSeparator
	 *            a path separator character
	 * @param newSeparator
	 *            a path separator character
	 * @return the file path with its separator character changed from the given old separator to the given new separator
	 */
	@Deprecated
	public static String changeSeparator(String path, char oldSeparator, char newSeparator) {
		return path.replace(oldSeparator, newSeparator);
	}

	/**
	 * Copies the given source file to the given destination file.
	 */
	@Deprecated
	public static void copy(File source, File dest) throws IOException {
		try (InputStream in = new FileInputStream(source);
				OutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
			transferData(in, out);
		}
	}

	/**
	 * Copies all files in the current data directory to the specified folder. Will overwrite.
	 */
	@Deprecated
	public static void copyFolder(File sourceFolder, File targetFolder) throws IOException {
		for (File currFile : sourceFolder.listFiles()) {
			if (currFile.isFile()) {
				File destFile = new File(targetFolder, currFile.getName());
				copy(currFile, destFile);
			} else if (currFile.isDirectory()) {
				File destDir = new File(targetFolder, currFile.getName());
				if (!destDir.exists()) {
					if (!destDir.mkdir()) {
						throw new IOException(
								"Unable to create destination context folder: " + destDir.getAbsolutePath());
					}
				}
				for (File file : currFile.listFiles()) {
					File destFile = new File(destDir, file.getName());
					if (destFile.exists()) {
						destFile.delete();
					}
					copy(file, destFile);
				}
			}
		}
	}

	@Deprecated
	public static File createTempFileInPlugin(Plugin plugin, IPath path) {
		IPath stateLocation = plugin.getStateLocation();
		stateLocation = stateLocation.append(path);
		return stateLocation.toFile();
	}

	@Deprecated
	public static void delete(File file) {
		if (file.exists()) {
			for (int i = 0; i < MAX_RETRY; i++) {
				if (file.delete()) {
					i = MAX_RETRY;
				} else {
					try {
						Thread.sleep(1000); // sleep a second
					} catch (InterruptedException e) {
						// don't need to catch this
					}
				}
			}
		}
	}

	@Deprecated
	public static void deleteFolder(File path) {
		if (path.isDirectory()) {
			for (File file : path.listFiles()) {
				file.delete();
			}
			path.delete();
		}
	}

	@Deprecated
	public static void deleteFolderRecursively(File path) {
		File[] files = path.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolderRecursively(file);
				} else {
					file.delete();
				}
			}
		}
	}

	@Deprecated
	public static InputStream getResource(Object source, String filename) throws IOException {
		Class<?> clazz = source instanceof Class<?> ? (Class<?>) source : source.getClass();
		ClassLoader classLoader = clazz.getClassLoader();
		InputStream in = classLoader.getResourceAsStream(filename);
		if (in == null) {
			File file = getFile(source, filename);
			if (file != null) {
				return new FileInputStream(file);
			}
		}
		if (in == null) {
			throw new IOException(NLS.bind("Failed to locate ''{0}'' for ''{1}''", filename, clazz.getName()));
		}
		return in;
	}

	@Deprecated
	public static File getFile(Object source, String filename) throws IOException {
		return org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.getFile(source, filename);
	}

	@Deprecated
	public static String read(File source) throws IOException {
		InputStream in = new FileInputStream(source);
		try (in) {
			StringBuilder sb = new StringBuilder();
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				sb.append(new String(buf, 0, len));
			}
			return sb.toString();
		}
	}

	/**
	 * Copies all bytes in the given source stream to the given destination stream. Neither streams are closed.
	 *
	 * @param source
	 *            the given source stream
	 * @param destination
	 *            the given destination stream
	 * @throws IOException
	 *             in case of error
	 */
	private static void transferData(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
	}

	/**
	 * Unzips the given zip file to the given destination directory extracting only those entries the pass through the given filter.
	 *
	 * @param zipFile
	 *            the zip file to unzip
	 * @param dstDir
	 *            the destination directory
	 * @throws IOException
	 *             in case of problem
	 */
	@Deprecated
	public static void unzip(ZipFile zipFile, File dstDir) throws IOException {
		unzip(zipFile, dstDir, dstDir, 0);
	}

	private static void unzip(ZipFile zipFile, File rootDstDir, File dstDir, int depth) throws IOException {

		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		try {
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				String entryName = entry.getName();
				File file = new File(dstDir, changeSeparator(entryName, '/', File.separatorChar));
				file.getParentFile().mkdirs();
				try (InputStream src = zipFile.getInputStream(entry);
						OutputStream dst = new BufferedOutputStream(new FileOutputStream(file))) {
					transferData(src, dst);
				}
			}
		} finally {
			try {
				zipFile.close();
			} catch (IOException e) {
				// don't need to catch this
			}
		}
	}

	@Deprecated
	public static void write(String fileName, StringBuffer content) throws IOException {
		try (Writer writer = new FileWriter(fileName)) {
			writer.write(content.toString());
		}
	}
}
