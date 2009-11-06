/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;

/**
 * Contains utility methods for working with zip files
 * 
 * @author Wesley Coelho
 * @author Shawn Minto (Wrote methods that were moved here)
 */
public class ZipFileUtil {

	/**
	 * Only unzips files in zip file not directories
	 * 
	 * @param zipped
	 *            file
	 * @param destPath
	 *            Destination path
	 * @return Files that were unzipped
	 */
	public static List<File> unzipFiles(File zippedfile, String destPath, IProgressMonitor monitor)
			throws FileNotFoundException, IOException {
		ZipFile zipFile = new ZipFile(zippedfile);
		try {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			List<File> outputFiles = new ArrayList<File>();
			File destinationFile = new File(destPath);
			if (!destinationFile.exists()) {
				destinationFile.mkdirs();
			}
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File outputFile = new File(destinationFile, entry.getName());
				if (entry.isDirectory() && !outputFile.exists()) {
					outputFile.mkdirs();
					continue;
				}

				if (!outputFile.getParentFile().exists()) {
					outputFile.getParentFile().mkdirs();
				}

				InputStream inputStream = new BufferedInputStream(zipFile.getInputStream(entry));
				try {
					OutputStream outStream = new BufferedOutputStream(new FileOutputStream(outputFile));
					try {
						copyStream(inputStream, outStream);
					} finally {
						outStream.close();
					}
				} finally {
					inputStream.close();
				}

				outputFiles.add(outputFile);
				if (monitor != null) {
					monitor.worked(1);
				}
			}
			return outputFiles;
		} finally {
			zipFile.close();
		}
	}

	private static void copyStream(InputStream in, OutputStream out) throws IOException {
		Assert.isNotNull(in);
		Assert.isNotNull(out);

		byte[] buffer = new byte[4096];
		int readCount;
		while ((readCount = in.read(buffer)) != -1) {
			out.write(buffer, 0, readCount);
		}
		out.flush();
	}

	/**
	 * @param zipFile
	 *            Destination zipped file
	 * @param files
	 *            List of files to add to the zip file
	 */
	public static void createZipFile(File zipFile, List<File> files) throws FileNotFoundException, IOException {
		createZipFile(zipFile, files, null, null);
	}

	public static void createZipFile(File zipFile, List<File> files, IProgressMonitor monitor)
			throws FileNotFoundException, IOException {
		createZipFile(zipFile, files, null, monitor);
	}

	/**
	 * @param zipFile
	 *            Destination zipped file
	 * @param files
	 *            List of files to add to the zip file
	 * @param progressMonitor
	 *            will report worked(1) to the monitor for each file zipped
	 * @author Shawn Minto
	 * @author Wesley Coelho
	 */
	public static void createZipFile(File zipFile, List<File> files, String rootPath, IProgressMonitor monitor)
			throws FileNotFoundException, IOException {
		if (rootPath == null) {
			rootPath = ""; //$NON-NLS-1$
		} else if (!rootPath.endsWith("\\") || !rootPath.endsWith("/")) { //$NON-NLS-1$ //$NON-NLS-2$
			rootPath += "/"; //$NON-NLS-1$
		}

		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
		try {
			for (File file : files) {
				try {
					addZipEntry(zipOut, rootPath, file);
					if (monitor != null) {
						monitor.worked(1);
					}
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, ICommonsCoreConstants.ID_PLUGIN, "Could not add " //$NON-NLS-1$
							+ file.getName() + " to zip", e)); //$NON-NLS-1$
				}
			}
		} finally {
			zipOut.close();
		}
	}

	/**
	 * @author Shawn Minto
	 */
	private static void addZipEntry(ZipOutputStream zipOut, String rootPath, File file) throws FileNotFoundException,
			IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File child : file.listFiles()) {
					addZipEntry(zipOut, rootPath, child);
				}
			} else {
				// Add ZIP entry to output stream.m
				String path = ""; //$NON-NLS-1$
				if (!rootPath.equals("")) { //$NON-NLS-1$
					rootPath = rootPath.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
					path = file.getAbsolutePath().replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
					path = path.substring(rootPath.length());
				} else {
					path = file.getName();
				}

				zipOut.putNextEntry(new ZipEntry(path));
				InputStream in = new BufferedInputStream(new FileInputStream(file));
				try {
					copyStream(in, zipOut);
				} finally {
					in.close();
				}

				// Complete the entry
				zipOut.closeEntry();
			}
		}
	}

}
