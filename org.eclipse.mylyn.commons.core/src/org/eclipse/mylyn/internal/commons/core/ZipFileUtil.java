/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
// FIXME add try/finally blocks that properly close streams
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

			InputStream inputStream = zipFile.getInputStream(entry);
			FileOutputStream outStream = new FileOutputStream(outputFile);
			copyByteStream(inputStream, outStream);

			outputFiles.add(outputFile);
			if (monitor != null) {
				monitor.worked(1);
			}
		}
		return outputFiles;
	}

	public static List<File> extactEntries(File zippedFile, List<ZipEntry> entries, String destPath)
			throws FileNotFoundException, IOException {
		ZipFile zipFile = new ZipFile(zippedFile);
		List<File> outputFiles = new ArrayList<File>();
		for (ZipEntry entry : entries) {
			if (entry.isDirectory()) {
				// Assume directories are stored parents first then children.		          
				(new File(entry.getName())).mkdir();
				continue;
			}
			InputStream inputStream = zipFile.getInputStream(entry);
			File outputFile = new File(destPath + File.separator + entry.getName());
			FileOutputStream outStream = new FileOutputStream(outputFile);
			copyByteStream(inputStream, outStream);
			outputFiles.add(outputFile);
		}
		return outputFiles;
	}

	public static void copyByteStream(InputStream in, OutputStream out) throws IOException {
		if (in != null && out != null) {
			BufferedInputStream inBuffered = new BufferedInputStream(in);

			int bufferSize = 1000;
			byte[] buffer = new byte[bufferSize];

			int readCount;

			BufferedOutputStream fout = new BufferedOutputStream(out);

			while ((readCount = inBuffered.read(buffer)) != -1) {
				if (readCount < bufferSize) {
					fout.write(buffer, 0, readCount);
				} else {
					fout.write(buffer);
				}
			}
			fout.flush();
			fout.close();
			in.close();
		}
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
		if (zipFile.exists()) {
			zipFile.delete();
		}
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

				// Create a buffer for reading the files
				byte[] buf = new byte[1024];

				// Transfer bytes from the file to the ZIP file
				// and compress the files
				FileInputStream in = new FileInputStream(file);
				int len;
				while ((len = in.read(buf)) > 0) {
					zipOut.write(buf, 0, len);
				}

				// Complete the entry
				zipOut.closeEntry();
				in.close();
			}
		}
	}

}
