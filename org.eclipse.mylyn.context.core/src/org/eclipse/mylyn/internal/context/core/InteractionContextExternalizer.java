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

package org.eclipse.mylyn.internal.context.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;

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

	static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss.S z";

	public void writeContextToXml(IInteractionContext context, File file) {
		writeContextToXml(context, file, new SaxContextWriter());
	}

	/**
	 * For testing
	 */
	public void writeContextToXml(IInteractionContext context, File file, IInteractionContextWriter writer) {
		if (context.getInteractionHistory().isEmpty()) {
			return;
		}

		FileOutputStream fileOutputStream = null;
		ZipOutputStream outputStream = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fileOutputStream = new FileOutputStream(file);
			outputStream = new ZipOutputStream(fileOutputStream);
			writeContext(context, outputStream, writer);

		} catch (IOException e) {
			// TODO: propagate exception?
			StatusHandler.fail(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Could not write: "
					+ file.getAbsolutePath(), e));
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Unable to write context "
						+ context.getHandleIdentifier(), e));
			}
		}
	}

	public void writeContext(IInteractionContext context, ZipOutputStream outputStream) throws IOException {
		writeContext(context, outputStream, new SaxContextWriter());
	}

	/**
	 * For testing
	 */
	public void writeContext(IInteractionContext context, ZipOutputStream outputStream, IInteractionContextWriter writer)
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

	public IInteractionContext readContextFromXml(String handleIdentifier, File fromFile,
			IInteractionContextScaling scaling) {
		return readContextFromXml(handleIdentifier, fromFile, new SaxContextReader(), scaling);
	}

	/**
	 * Public for testing
	 */
	public IInteractionContext readContextFromXml(String handleIdentifier, File fromFile,
			IInteractionContextReader reader, IInteractionContextScaling scaling) {
		try {
			if (!fromFile.exists()) {
				return null;
			} else {
				if (reader instanceof SaxContextReader) {
					((SaxContextReader) reader).setContextScaling(scaling);
				}

				return reader.readContext(handleIdentifier, fromFile);
			}
		} catch (Exception e) {
			// TODO: propagate exception instead?
			StatusHandler.fail(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Could not read: "
					+ fromFile.getAbsolutePath(), e));
		}
		return null;
	}

}
