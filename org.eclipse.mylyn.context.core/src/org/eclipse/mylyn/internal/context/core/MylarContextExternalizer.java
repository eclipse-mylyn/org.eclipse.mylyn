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
/*
 * Created on May 22, 2005
 */
package org.eclipse.mylar.internal.context.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.mylar.context.core.IContextReader;
import org.eclipse.mylar.context.core.IContextWriter;
import org.eclipse.mylar.core.MylarStatusHandler;

/**
 * @author Mik Kersten
 */
public class MylarContextExternalizer {

	private IContextReader reader = new SaxContextReader();

	private IContextWriter writer = new SaxContextWriter();
	
	public static final String ELMNT_INTERACTION_HISTORY_OLD = "interactionEvent";

	public static final String ELMNT_INTERACTION_HISTORY = "InteractionHistory";

	public static final String ATR_STRUCTURE_KIND = "StructureKind";

	public static final String ATR_STRUCTURE_HANDLE = "StructureHandle";

	public static final String ATR_START_DATE = "StartDate";

	public static final String ATR_ORIGIN_ID = "OriginId";

	public static final String ATR_NAVIGATION = "Navigation";

	public static final String ATR_KIND = "Kind";

	public static final String ATR_INTEREST = "Interest";

	public static final String ATR_DELTA = "Delta";

	public static final String ATR_END_DATE = "EndDate";

	public static final String ATR_ID = "Id";

	public static final String ATR_VERSION = "Version";

	private static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss.S z";

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.ENGLISH);

	public void writeContextToXml(MylarContext context, File file) {
		if (context.getInteractionHistory().isEmpty())
			return;
		try {
			 if (!file.exists()) {
				file.createNewFile();
			 }
			String handleIdentifier = context.getHandleIdentifier();
			String encoded = URLEncoder.encode(handleIdentifier, ContextManager.CONTEXT_FILENAME_ENCODING);		
			ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file));
			ZipEntry zipEntry = new ZipEntry(encoded + ContextManager.CONTEXT_FILE_EXTENSION_OLD);
			outputStream.putNextEntry(zipEntry);
			outputStream.setMethod(ZipOutputStream.DEFLATED);

			// OutputStream stream = new FileOutputStream(file);
			writer.setOutputStream(outputStream);
			writer.writeContextToStream(context);
			outputStream.closeEntry();
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			MylarStatusHandler.fail(e, "Could not write: " + file.getAbsolutePath(), true);
		}
	}

	public MylarContext readContextFromXML(String handleIdentifier, File file) {
		try {
			if (!file.exists()) {
				return null;
			} else {
				return reader.readContext(handleIdentifier, file);
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not read: " + file.getAbsolutePath(), true);
		}
		return null;
	}

	public IContextReader getReader() {
		return reader;
	}

	public void setReader(IContextReader reader) {
		this.reader = reader;
	}

	public IContextWriter getWriter() {
		return writer;
	}

	public void setWriter(IContextWriter writer) {
		this.writer = writer;
	}
}
