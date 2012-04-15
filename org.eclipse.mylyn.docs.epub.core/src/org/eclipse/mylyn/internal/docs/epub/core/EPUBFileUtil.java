/*******************************************************************************
 * Copyright (c) 2011,2012 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.docs.epub.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.mylyn.docs.epub.core.OPSPublication;

/**
 * Various EPUB file related utilities.
 * 
 * @author Torkild U. Resheim
 */
public class EPUBFileUtil {

	static final int BUFFERSIZE = 2048;

	/**
	 * Copies the contents of <i>source</i> to the new <i>destination</i> file.
	 * 
	 * @param source
	 *            the source file
	 * @param destination
	 *            the destination file
	 * @throws IOException
	 */
	public static void copy(File source, File destination) throws IOException {
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
	}

	/**
	 * Attempts to figure out the MIME-type for the file.
	 * 
	 * @param file
	 *            the file to determine MIME-type for
	 * @return the MIME-type or <code>null</code>
	 */
	public static String getMimeType(File file) {
		String name = file.getName().toLowerCase();
		// These are not (correctly) detected by mechanism below
		if (name.endsWith("xhtml")) { //$NON-NLS-1$
			return "application/xhtml+xml"; //$NON-NLS-1$
		}
		if (name.endsWith(".otf")) { //$NON-NLS-1$
			return "font/opentype"; //$NON-NLS-1$
		}
		if (name.endsWith(".ttf")) { //$NON-NLS-1$
			return "font/truetype"; //$NON-NLS-1$
		}
		if (name.endsWith(".svg")) { //$NON-NLS-1$
			return "image/svg+xml"; //$NON-NLS-1$
		}
		if (name.endsWith(".css")) { //$NON-NLS-1$
			return "text/css"; //$NON-NLS-1$
		}
		try {
			// Use URLConnection or content type detection
			String mimeType_name = URLConnection.guessContentTypeFromName(file.getName());
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			String mimeType_content = URLConnection.guessContentTypeFromStream(is);
			is.close();
			// Handle situations where we have file name that indicates we have
			// plain HTML, but the contents say XML. Hence we are probably
			// looking at XHTML (see bug 360701).
			if (mimeType_name != null && mimeType_content != null) {
				if (mimeType_name.equals("text/html") && mimeType_content.equals("application/xml")) { //$NON-NLS-1$ //$NON-NLS-2$
					return "application/xhtml+xml"; //$NON-NLS-1$
				}
			}
			// We trust name over content
			return mimeType_name == null ? mimeType_content : mimeType_name;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
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
	 * If the destination folder does not already exist it will be created. Additionally the modification timestamp of
	 * this folder will be set to the same as the originating EPUB file.
	 * </p>
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
		boolean checkFirstItem = true;
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
			if (checkFirstItem) {
				if (!entryName.equals("mimetype")) { //$NON-NLS-1$
					throw new IOException("Invalid EPUB file. First item must be \"mimetype\""); //$NON-NLS-1$
				}
				String type = new String(buf);
				if (!type.trim().equals(OPSPublication.MIMETYPE_EPUB)) {
					throw new IOException("Invalid EPUB file. Expected \"" + OPSPublication.MIMETYPE_EPUB + "\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				checkFirstItem = false;
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
		byte[] bytes = OPSPublication.MIMETYPE_EPUB.getBytes("ASCII"); //$NON-NLS-1$
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
		// zip file. We need that in order to support EPUB properly.
		File[] files = folder.listFiles(new java.io.FileFilter() {
			public boolean accept(File pathname) {
				return !pathname.isDirectory();
			}
		});
		byte[] tmpBuf = new byte[BUFFERSIZE];

		for (File file : files) {
			FileInputStream in = new FileInputStream(file.getAbsolutePath());
			ZipEntry zipEntry = new ZipEntry(getRelativePath(root, file));
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
			out.putNextEntry(new ZipEntry(getRelativePath(root, dir)));
			zip(root, dir, out);
		}
	}

}
