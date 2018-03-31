/*******************************************************************************
 * Copyright (c) 2011-2015 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.eclipse.mylyn.docs.epub.core.EPUB;

/**
 * Various EPUB file related utilities.
 *
 * @author Torkild U. Resheim
 */
public class EPUBFileUtil {

	static final int BUFFERSIZE = 2048;

	private static TikaConfig tika;

	/**
	 * Copies the contents of <i>source</i> to the new <i>destination</i> file. If the destination file already exists,
	 * it will not be overwritten.
	 *
	 * @param source
	 *            the source file
	 * @param destination
	 *            the destination file
	 * @return <code>true</code> if the file was copied
	 * @throws IOException
	 */
	public static boolean copy(File source, File destination) throws IOException {
		if (destination.exists()) {
			return false;
		}
		destination.getParentFile().mkdirs();
		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(source);
			to = new FileOutputStream(destination);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytesRead);
			}
		} finally {
			if (from != null) {
				try {
					from.close();
				} catch (IOException e) {
				}
			}
			if (to != null) {
				try {
					to.close();
				} catch (IOException e) {
				}
			}
		}
		destination.setLastModified(source.lastModified());
		return true;
	}

	/**
	 * Attempts to figure out the MIME-type for the file.
	 *
	 * @param file
	 *            the file to determine MIME-type for
	 * @return the MIME-type or <code>application/octet-stream</code>
	 */
	public static String getMimeType(File file) {
		try {
			if (tika == null) {
				tika = new TikaConfig();
			}
			Metadata metadata = new Metadata();
			metadata.set(TikaMetadataKeys.RESOURCE_NAME_KEY, file.getName());
			MediaType detect = tika.getDetector().detect(TikaInputStream.get(file), metadata);
			return detect.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (TikaException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a path segment list.
	 *
	 * @param root
	 *            the root folder
	 * @param file
	 *            the destination file
	 * @param segments
	 * @see #getRelativePath(File, File)
	 */
	private static void getPathSegments(File root, File file, ArrayList<String> segments) {
		if (root.equals(file) || file == null) {
			return;
		}
		segments.add(0, file.getName());
		getPathSegments(root, file.getParentFile(), segments);
	}

	/**
	 * Determines the <i>root</i> relative path of <i>file</i> in a platform independent manner. The returned string is
	 * a path starting from but excluding <i>root</i> using the '/' character as a directory separator. If the
	 * <i>file</i> argument is a folder a trailing directory separator is added. if the <i>root</i> argument is a file,
	 * it's parent folder will be used.
	 * <p>
	 * Note that if <i>file</i> is <b>not relative</b> to root, it's absolute path will be returned.
	 * </p>
	 *
	 * @param root
	 *            the root directory or file
	 * @param file
	 *            the root contained file or directory
	 * @return the platform independent, relative path or an absolute path
	 */
	public static String getRelativePath(File root, File file) {
		ArrayList<String> segments = new ArrayList<String>();
		if (root.isFile()) {
			root = root.getParentFile();
		}
		getPathSegments(root, file, segments);
		StringBuilder path = new StringBuilder();
		for (int p = 0; p < segments.size(); p++) {
			if (p > 0) {
				path.append('/');
			}
			path.append(segments.get(p));
		}
		if (file.isDirectory()) {
			path.append('/');
		}
		return path.toString();
	}

	/**
	 * Unpacks the given <i>epubfile</i> to the <i>destination</i> directory. This method will also validate the first
	 * item contained in the EPUB (see {@link #writeEPUBHeader(ZipOutputStream)}).
	 * <p>
	 * If the destination folder does not already exist it will be created. Additionally the modification time stamp of
	 * this folder will be set to the same as the originating EPUB file.
	 * </p>
	 * TODO: Actually validate the mimetype file
	 *
	 * @param epubfile
	 *            the EPUB file
	 * @param destination
	 *            the destination folder
	 * @throws FileNotFoundException
	 *             when EPUB file does not exist
	 * @throws IOException
	 *             if the operation was unsuccessful
	 */
	public static void unzip(File epubfile, File destination) throws IOException {
		if (!destination.exists()) {
			if (!destination.mkdirs()) {
				throw new IOException("Could not create directory for EPUB contents"); //$NON-NLS-1$
			}
		}
		ZipInputStream in = new ZipInputStream(new FileInputStream(epubfile));
		byte[] buf = new byte[BUFFERSIZE];
		ZipEntry entry = null;
		while ((entry = in.getNextEntry()) != null) {
			// for each entry to be extracted
			String entryName = entry.getName();
			File newFile = new File(destination.getAbsolutePath() + File.separator + entryName);
			if (entry.isDirectory()) {
				newFile.mkdirs();
				if (entry.getTime() > 0) {
					newFile.setLastModified(entry.getTime());
				}
				continue;
			} else {
				newFile.getParentFile().mkdirs();
			}
			int n;
			FileOutputStream fileoutputstream = new FileOutputStream(newFile);
			while ((n = in.read(buf, 0, BUFFERSIZE)) > -1) {
				fileoutputstream.write(buf, 0, n);
			}
			fileoutputstream.close();
			in.closeEntry();
			// Update the file modification time
			if (entry.getTime() > 0) {
				newFile.setLastModified(entry.getTime());
			}
		} // iterate over contents
		in.close();
		destination.setLastModified(epubfile.lastModified());
	}

	/**
	 * A correctly formatted EPUB file must contain an uncompressed entry named <b>mimetype</b> that is placed at the
	 * beginning. The contents of this file must be the ASCII-encoded string <b>application/epub+zip</b>. This method
	 * will create this file.
	 *
	 * @param zos
	 *            the zip output stream to write to.
	 * @throws IOException
	 */
	public static void writeEPUBHeader(ZipOutputStream zos) throws IOException {
		byte[] bytes = EPUB.MIMETYPE_EPUB.getBytes("ASCII"); //$NON-NLS-1$
		ZipEntry mimetype = new ZipEntry("mimetype"); //$NON-NLS-1$
		mimetype.setMethod(ZipOutputStream.STORED);
		mimetype.setSize(bytes.length);
		mimetype.setCompressedSize(bytes.length);
		CRC32 crc = new CRC32();
		crc.update(bytes);
		mimetype.setCrc(crc.getValue());
		zos.putNextEntry(mimetype);
		zos.write(bytes);
		zos.closeEntry();
	}

	/**
	 * Recursively compresses contents of the given folder into a zip-file. If a file already exists in the given
	 * location an exception will be thrown.
	 *
	 * @param destination
	 *            the destination file
	 * @param folder
	 *            the source folder
	 * @param uncompressed
	 *            a list of files to keep uncompressed
	 * @throws ZipException
	 * @throws IOException
	 */
	public static void zip(File destination, File folder) throws ZipException, IOException {
		if (destination.exists()) {
			throw new IOException("A file already exists at " + destination.getAbsolutePath()); //$NON-NLS-1$
		}
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destination));
		writeEPUBHeader(out);
		zip(folder, folder, out);
		out.close();
	}

	/**
	 * Adds a folder recursively to the output stream.
	 *
	 * @param folder
	 *            the root folder
	 * @param out
	 *            the output stream
	 * @throws IOException
	 */
	private static void zip(File root, File folder, ZipOutputStream out) throws IOException {
		// Files first in order to make sure "metadata" is placed first in the
		// zip file. We need that in order to support EPUB properly. Also do
		// not add a mimetype file – it has already been added to the stream.
		File[] files = folder.listFiles(new java.io.FileFilter() {
			public boolean accept(File pathname) {
				return !pathname.isDirectory() && !pathname.getName().equals("mimetype"); //$NON-NLS-1$
			}
		});
		byte[] tmpBuf = new byte[BUFFERSIZE];

		for (File file : files) {
			FileInputStream in = new FileInputStream(file.getAbsolutePath());
			ZipEntry zipEntry = new ZipEntry(getRelativePath(root, file));
			zipEntry.setTime(file.lastModified());
			out.putNextEntry(zipEntry);
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}
			out.closeEntry();
			in.close();
		}
		File[] dirs = folder.listFiles(new java.io.FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for (File dir : dirs) {
			ZipEntry f = new ZipEntry(getRelativePath(root, dir));
			f.setTime(dir.lastModified());
			out.putNextEntry(f);
			zip(root, dir, out);
		}
	}

}
