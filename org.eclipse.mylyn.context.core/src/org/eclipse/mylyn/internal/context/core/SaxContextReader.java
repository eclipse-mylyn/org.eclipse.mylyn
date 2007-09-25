/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.mylyn.context.core.IInteractionContextReader;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Brock Janiczak
 * @author Mik Kersten (minor refactoring)
 * @author Jevgeni Holodkov
 */
public class SaxContextReader implements IInteractionContextReader {

	public InteractionContext readContext(String handleIdentifier, File file) {
		if (!file.exists())
			return null;
		FileInputStream fileInputStream = null;
		ZipInputStream zipInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			zipInputStream = new ZipInputStream(fileInputStream);

			// search for context entry
			String encoded = URLEncoder.encode(handleIdentifier, InteractionContextManager.CONTEXT_FILENAME_ENCODING);
			String contextFileName = encoded + InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD;
			ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null) {
				if (contextFileName.equals(entry.getName())) {
					break;
				}
				entry = zipInputStream.getNextEntry();
			}

			if (entry == null) {
				return null;
			}

			SaxContextContentHandler contentHandler = new SaxContextContentHandler(handleIdentifier);
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(contentHandler);
			reader.parse(new InputSource(zipInputStream));
			return contentHandler.getContext();
		} catch (Throwable t) {
			closeStream(zipInputStream);
			closeStream(fileInputStream);
			file.renameTo(new File(file.getAbsolutePath() + "-save"));
			return null;
		} finally {
			closeStream(zipInputStream);
			closeStream(fileInputStream);
		}
	}

	private static final void closeStream(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				StatusHandler.fail(e, "Failed to close context input stream.", false);
			}
		}
	}

}
