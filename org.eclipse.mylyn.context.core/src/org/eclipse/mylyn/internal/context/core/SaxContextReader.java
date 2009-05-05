/*******************************************************************************
 * Copyright (c) 2004, 2008 Brock Janiczak and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brock Janiczak - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Jevgeni Holodkov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Brock Janiczak
 * @author Mik Kersten (refactoring)
 */
public class SaxContextReader implements IInteractionContextReader {

	private IInteractionContextScaling contextScaling;

	public SaxContextReader() {
	}

	public void setContextScaling(IInteractionContextScaling contextScaling) {
		this.contextScaling = contextScaling;
	}

	/**
	 * Reads the first entry in the zip file if an entry matching the handleIdentifier is not found.
	 */
	public InteractionContext readContext(String handleIdentifier, File file) {
		if (!file.exists()) {
			return null;
		}
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
				try {
					// search for context entry
					String encoded = URLEncoder.encode(handleIdentifier,
							InteractionContextManager.CONTEXT_FILENAME_ENCODING);
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

					SaxContextContentHandler contentHandler = new SaxContextContentHandler(handleIdentifier,
							contextScaling);
					XMLReader reader = XMLReaderFactory.createXMLReader();
					reader.setContentHandler(contentHandler);
					reader.parse(new InputSource(zipInputStream));
					return contentHandler.getContext();
				} finally {
					zipInputStream.close();
				}
			} finally {
				fileInputStream.close();
			}
		} catch (Exception e) {
			File saveFile = new File(file.getAbsolutePath() + "-save"); //$NON-NLS-1$
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN,
					"Error loading context, backup saved to \"" + saveFile + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
			file.renameTo(saveFile);
			return null;
		}
	}
}
