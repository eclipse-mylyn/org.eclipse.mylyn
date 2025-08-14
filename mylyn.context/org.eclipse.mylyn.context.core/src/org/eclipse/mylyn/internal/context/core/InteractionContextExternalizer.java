/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IContextContributor;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;

/**
 * @author Mik Kersten
 * @author Jevgeni Holodkov
 */
public class InteractionContextExternalizer {

	public static final String ELMNT_INTERACTION_HISTORY_OLD = "interactionEvent"; //$NON-NLS-1$

	public static final String ELMNT_INTERACTION_HISTORY = "InteractionHistory"; //$NON-NLS-1$

	public static final String ATR_STRUCTURE_KIND = "StructureKind"; //$NON-NLS-1$

	public static final String ATR_STRUCTURE_HANDLE = "StructureHandle"; //$NON-NLS-1$

	public static final String ATR_START_DATE = "StartDate"; //$NON-NLS-1$

	public static final String ATR_ORIGIN_ID = "OriginId"; //$NON-NLS-1$

	public static final String ATR_NAVIGATION = "Navigation"; //$NON-NLS-1$

	public static final String ATR_KIND = "Kind"; //$NON-NLS-1$

	public static final String ATR_INTEREST = "Interest"; //$NON-NLS-1$

	public static final String ATR_NUM_EVENTS = "NumEvents"; //$NON-NLS-1$

	public static final String ATR_CREATION_COUNT = "CreationCount"; //$NON-NLS-1$

	public static final String ATR_DELTA = "Delta"; //$NON-NLS-1$

	public static final String ATR_END_DATE = "EndDate"; //$NON-NLS-1$

	public static final String ATR_ID = "Id"; //$NON-NLS-1$

	public static final String ATR_VERSION = "Version"; //$NON-NLS-1$

	static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss.S z"; //$NON-NLS-1$

	static String getFirstContextHandle(File sourceFile) throws CoreException {
		try {
			try (ZipFile zipFile = new ZipFile(sourceFile)) {
				for (Enumeration<?> e = zipFile.entries(); e.hasMoreElements();) {
					ZipEntry entry = (ZipEntry) e.nextElement();
					String name = entry.getName();
					if (name.endsWith(InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD)) {
						try {
							String decodedName = URLDecoder.decode(name,
									InteractionContextManager.CONTEXT_FILENAME_ENCODING);
							if (decodedName.length() > InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD.length()) {
								return decodedName.substring(0, decodedName.length()
										- InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD.length());
							}
						} catch (IllegalArgumentException ignored) {
							// not a valid context entry
						}
					}
				}
				return null;
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN,
					"Could not get context handle from " + sourceFile, e)); //$NON-NLS-1$
		}
	}

	public void writeContextToXml(IInteractionContext context, File file) throws IOException {
		writeContextToXml(context, file, new SaxContextWriter());
	}

	/**
	 * Public for testing.
	 *
	 * @throws IOException
	 *             if writing of context fails
	 */
	public void writeContextToXml(IInteractionContext context, File file, IInteractionContextWriter writer)
			throws IOException {
		if (context.getInteractionHistory().isEmpty()) {
			return;
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(file);
				ZipOutputStream outputStream = new ZipOutputStream(fileOutputStream)) {
			writeContext(context, outputStream, writer);
		}
	}

	public void writeContext(IInteractionContext context, ZipOutputStream outputStream) throws IOException {
		writeContext(context, outputStream, new SaxContextWriter());
	}

	/**
	 * For testing
	 */
	public void writeContext(IInteractionContext context, ZipOutputStream outputStream,
			IInteractionContextWriter writer) throws IOException {
		String handleIdentifier = context.getHandleIdentifier();
		String encoded = URLEncoder.encode(handleIdentifier, InteractionContextManager.CONTEXT_FILENAME_ENCODING);
		ZipEntry zipEntry = new ZipEntry(encoded + InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD);
		outputStream.putNextEntry(zipEntry);
		outputStream.setMethod(ZipOutputStream.DEFLATED);

		writer.setOutputStream(outputStream);
		writer.writeContextToStream(context);
		outputStream.flush();
		outputStream.closeEntry();

		addAdditionalInformation(context, outputStream);
	}

	private void addAdditionalInformation(final IInteractionContext context, final ZipOutputStream outputStream)
			throws IOException {
		for (final IContextContributor contributor : getContextContributor()) {
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable e) {
					StatusHandler.log(
							new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Context contribution failed: " //$NON-NLS-1$
									+ contributor.getClass(), e));
				}

				@Override
				public void run() throws Exception {
					try (InputStream additionalContextInformation = contributor.getDataAsStream(context)) {
						if (additionalContextInformation != null) {
							String encoded = URLEncoder.encode(contributor.getIdentifier(),
									InteractionContextManager.CONTEXT_FILENAME_ENCODING);
							ZipEntry zipEntry = new ZipEntry(encoded);
							outputStream.putNextEntry(zipEntry);
							IOUtils.copy(additionalContextInformation, outputStream);
							outputStream.flush();
							outputStream.closeEntry();
						}
					}
				}
			});
		}
	}

	public InputStream getAdditionalInformation(File file, String contributorIdentifier) throws IOException {
		if (!file.exists()) {
			return null;
		}
		final ZipFile zipFile = new ZipFile(file);
		ZipEntry entry = findFileInZip(zipFile, contributorIdentifier);
		if (entry == null) {
			return null;
		}

		return new FilterInputStream(zipFile.getInputStream(entry)) {
			@Override
			public void close() throws IOException {
				super.close();
				zipFile.close();
			}
		};
	}

	private ZipEntry findFileInZip(ZipFile zipFile, String identifier) throws UnsupportedEncodingException {
		String encoded = URLEncoder.encode(identifier, InteractionContextManager.CONTEXT_FILENAME_ENCODING);
		for (Enumeration<?> e = zipFile.entries(); e.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			if (entry.getName().equals(encoded)) {
				return entry;
			}
		}
		return null;
	}

	private List<IContextContributor> getContextContributor() {
		return ContextCorePlugin.getDefault().getContextContributor();
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

				InteractionContext context = reader.readContext(handleIdentifier, fromFile);
				if (context == null) {
					String firstHandle = getFirstContextHandle(fromFile);
					if (firstHandle != null && !firstHandle.equals(handleIdentifier)) {
						context = reader.readContext(firstHandle, fromFile);
						if (context != null) {
							context.setHandleIdentifier(handleIdentifier);
						}
					}
				}
				return context;
			}
		} catch (Exception e) {
			// TODO: propagate exception instead?
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Could not read: " //$NON-NLS-1$
					+ fromFile.getAbsolutePath(), e));
		}
		return null;
	}
}
