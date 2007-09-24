/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on May 22, 2005
 */
package org.eclipse.mylyn.internal.context.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.mylyn.context.core.IInteractionContextReader;
import org.eclipse.mylyn.context.core.IInteractionContextWriter;
import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * @author Mik Kersten
 * @author Jevgeni Holodkov
 */
public class InteractionContextExternalizer {

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

	public void writeContextToXml(InteractionContext context, File file) {
		writeContextToXml(context, file, new SaxContextWriter());
	}

	/**
	 * For testing
	 */
	public void writeContextToXml(InteractionContext context, File file, IInteractionContextWriter writer) {
		if (context.getInteractionHistory().isEmpty())
			return;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file));
			writeContext(context, outputStream, writer);
			outputStream.close();
		} catch (IOException e) {
			StatusHandler.fail(e, "Could not write: " + file.getAbsolutePath(), true);
		}
	}

	public void writeContext(InteractionContext context, ZipOutputStream outputStream) throws IOException {
		writeContext(context, outputStream, new SaxContextWriter());
	}

	/**
	 * For testing
	 */
	public void writeContext(InteractionContext context, ZipOutputStream outputStream, IInteractionContextWriter writer)
			throws IOException {
		String handleIdentifier = context.getHandleIdentifier();
		String encoded = URLEncoder.encode(handleIdentifier, InteractionContextManager.CONTEXT_FILENAME_ENCODING);
		ZipEntry zipEntry = new ZipEntry(encoded + InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD);
		outputStream.putNextEntry(zipEntry);
		outputStream.setMethod(ZipOutputStream.DEFLATED);

		writer.setOutputStream(outputStream);
		writer.writeContextToStream(context);
		outputStream.flush();
		outputStream.closeEntry();
	}

	public InteractionContext readContextFromXML(String handleIdentifier, File file) {
		return readContextFromXML(handleIdentifier, file, new SaxContextReader());
	}

	/**
	 * For testing
	 */
	public InteractionContext readContextFromXML(String handleIdentifier, File file, IInteractionContextReader reader) {
		try {
			if (!file.exists()) {
				return null;
			} else {
				return reader.readContext(handleIdentifier, file);
			}
		} catch (Exception e) {
			StatusHandler.fail(e, "Could not read: " + file.getAbsolutePath(), true);
		}
		return null;
	}

//	public IInteractionContextReader getReader() {
//		return reader;
//	}
//
//	public void setReader(IInteractionContextReader reader) {
//		this.reader = reader;
//	}
//
//	public IInteractionContextWriter getWriter() {
//		return writer;
//	}
//
//	public void setWriter(IInteractionContextWriter writer) {
//		this.writer = writer;
//	}

}
