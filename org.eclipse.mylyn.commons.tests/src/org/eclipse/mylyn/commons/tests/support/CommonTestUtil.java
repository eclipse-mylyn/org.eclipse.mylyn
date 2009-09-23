/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;

@SuppressWarnings("restriction")
public class CommonTestUtil {

	private final static int MAX_RETRY = 5;

	/**
	 * Returns the given file path with its separator character changed from the given old separator to the given new
	 * separator.
	 * 
	 * @param path
	 *            a file path
	 * @param oldSeparator
	 *            a path separator character
	 * @param newSeparator
	 *            a path separator character
	 * @return the file path with its separator character changed from the given old separator to the given new
	 *         separator
	 */
	public static String changeSeparator(String path, char oldSeparator, char newSeparator) {
		return path.replace(oldSeparator, newSeparator);
	}

	/**
	 * Copies the given source file to the given destination file.
	 */
	public static void copy(File source, File dest) throws IOException {
		InputStream in = new FileInputStream(source);
		try {
			OutputStream out = new FileOutputStream(dest);
			try {
				transferData(in, out);
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}

	/**
	 * Copies all files in the current data directory to the specified folder. Will overwrite.
	 */
	public static void copyFolder(File sourceFolder, File targetFolder) throws IOException {
		for (File currFile : sourceFolder.listFiles()) {
			if (currFile.isFile()) {
				File destFile = new File(targetFolder, currFile.getName());
				copy(currFile, destFile);
			} else if (currFile.isDirectory()) {
				File destDir = new File(targetFolder, currFile.getName());
				if (!destDir.exists()) {
					if (!destDir.mkdir()) {
						throw new IOException("Unable to create destination context folder: "
								+ destDir.getAbsolutePath());
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

	public static File createTempFileInPlugin(Plugin plugin, IPath path) {
		IPath stateLocation = plugin.getStateLocation();
		stateLocation = stateLocation.append(path);
		return stateLocation.toFile();
	}

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

	public static void deleteFolder(File path) {
		if (path.isDirectory()) {
			for (File file : path.listFiles()) {
				file.delete();
			}
			path.delete();
		}
	}

	public static File getFile(Object source, String filename) throws IOException {
		Class<?> clazz = (source instanceof Class<?>) ? (Class<?>) source : source.getClass();
		if (Platform.isRunning()) {
			ClassLoader classLoader = clazz.getClassLoader();
			if (classLoader instanceof BundleClassLoader) {
				URL url = ((BundleClassLoader) classLoader).getResource(filename);
				URL localURL = FileLocator.toFileURL(url);
				return new File(localURL.getFile());
			}
		} else {
			URL localURL = clazz.getResource("");
			String path = localURL.getFile();
			int i = path.indexOf("!");
			if (i != -1) {
				int j = path.lastIndexOf(File.separatorChar, i);
				if (j != -1) {
					path = path.substring(0, j) + File.separator;
				} else {
					throw new AssertionFailedError("Unable to determine location for '" + filename + "' at '" + path
							+ "'");
				}
				// class file is nested in jar, use jar path as base
				if (path.startsWith("file:")) {
					path = path.substring(5);
				}
				return new File(filename);
			} else {
				// remove all package segments from name
				String directory = clazz.getName().replaceAll("[^.]", "");
				directory = directory.replaceAll(".", "../");
				if (localURL.getFile().contains("/bin/")) {
					// account for bin/ when running from Eclipse workspace
					directory += "../";
				}
				filename = localURL.getFile() + (directory + filename).replaceAll("/", File.separator);
				return new File(filename).getCanonicalFile();
			}
		}
		throw new AssertionFailedError("Could not locate " + filename);
	}

	public static String read(File source) throws IOException {
		InputStream in = new FileInputStream(source);
		try {
			StringBuilder sb = new StringBuilder();
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				sb.append(new String(buf, 0, len));
			}
			return sb.toString();
		} finally {
			in.close();
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
	 * Unzips the given zip file to the given destination directory extracting only those entries the pass through the
	 * given filter.
	 * 
	 * @param zipFile
	 *            the zip file to unzip
	 * @param dstDir
	 *            the destination directory
	 * @throws IOException
	 *             in case of problem
	 */
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
				InputStream src = null;
				OutputStream dst = null;
				try {
					src = zipFile.getInputStream(entry);
					dst = new FileOutputStream(file);
					transferData(src, dst);
				} finally {
					if (dst != null) {
						try {
							dst.close();
						} catch (IOException e) {
							// don't need to catch this
						}
					}
					if (src != null) {
						try {
							src.close();
						} catch (IOException e) {
							// don't need to catch this
						}
					}
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

	public static void write(String fileName, StringBuffer content) throws IOException {
		Writer writer = new FileWriter(fileName);
		try {
			writer.write(content.toString());
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// don't need to catch this
			}
		}
	}

}
