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

package org.eclipse.mylyn.internal.context.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

import org.eclipse.mylyn.context.core.IInteractionContextReader;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Brock Janiczak
 * @author Mik Kersten (minor refactoring)
 */
public class SaxContextReader implements IInteractionContextReader {

	public InteractionContext readContext(String handleIdentifier, File file) {
		if (!file.exists())
			return null;
		ZipInputStream inputStream = null;
		try {
			inputStream = new ZipInputStream(new FileInputStream(file));
			inputStream.getNextEntry();			
			SaxContextContentHandler contentHandler = new SaxContextContentHandler(handleIdentifier);
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(contentHandler);
			reader.parse(new InputSource(inputStream));
			return contentHandler.getContext();
		} catch (Throwable t) {
			file.renameTo(new File(file.getAbsolutePath() + "-save"));
			return null;
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					StatusManager.fail(e, "Failed to close context input stream.", false);
				}
			}
		}
	}
}
