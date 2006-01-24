/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Contains utility methods for working with zip files
 * 
 * @author Wesley Coelho
 * @author Shawn Minto (Wrote methods that were moved here)
 */
public class ZipFileUtil {

	/**
	 * @param zipFile
	 *            Destination zipped file
	 * @param files
	 *            List of files to add to the zip file
	 */
	public static void createZipFile(File zipFile, List<File> files) throws FileNotFoundException, IOException {
		createZipFile(zipFile, files, null);
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
	public static void createZipFile(File zipFile, List<File> files, IProgressMonitor monitor)
			throws FileNotFoundException, IOException {
		if (zipFile.exists()) {
			zipFile.delete();
		}

		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));

		for (File file : files) {
			try {
				addZipEntry(zipOut, file);
				if (monitor != null) {
					monitor.worked(1);
				}
			} catch (Exception e) {
				MylarStatusHandler.log(e, "Could not add " + file.getName() + " to zip");
			}
		}

		// Complete the ZIP file
		zipOut.close();
	}

	/**
	 * @author Shawn Minto
	 */
	private static void addZipEntry(ZipOutputStream zipOut, File file) throws FileNotFoundException, IOException {

		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		// Compress the files
		FileInputStream in = new FileInputStream(file);

		// Add ZIP entry to output stream.
		zipOut.putNextEntry(new ZipEntry(file.getName()));

		// Transfer bytes from the file to the ZIP file
		int len;
		while ((len = in.read(buf)) > 0) {
			zipOut.write(buf, 0, len);
		}

		// Complete the entry
		zipOut.closeEntry();
		in.close();
	}

}
