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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IContextStore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class LocalContextStore implements IContextStore {

	private File contextDirectory;

	private Set<File> contextFiles = null;

	private final InteractionContextExternalizer externalizer = new InteractionContextExternalizer();

	private final IInteractionContextScaling commonContextScaling;

	private final List<IContextStoreListener> listeners = new ArrayList<IContextStoreListener>();

	public LocalContextStore(IInteractionContextScaling commonContextScaling) {
		this.commonContextScaling = commonContextScaling;
	}

	public synchronized void setContextDirectory(File directory) {
		this.contextDirectory = directory;
		for (IContextStoreListener listener : listeners) {
			listener.contextStoreMoved(directory);
		}
//		rootDirectory = new File(TasksUiPlugin.getDefault().getDataDirectory());
//		if (!rootDirectory.exists()) {
//			rootDirectory.mkdir();
//		}
//
//		contextDirectory = new File(rootDirectory, ITasksCoreConstants.CONTEXTS_DIRECTORY);
//		if (!contextDirectory.exists()) {
//			contextDirectory.mkdir();
//		}
	}

	public File getContextDirectory() {
		return contextDirectory;
	}

	/**
	 * @return false if the map could not be read for any reason
	 */
	public IInteractionContext loadContext(String handleIdentifier) {
		return loadContext(handleIdentifier, getFileForContext(handleIdentifier), commonContextScaling);
//		return importContext(handleIdentifier, getFileForContext(handleIdentifier));
	}

	public IInteractionContext importContext(String handleIdentifier, File fromFile) throws CoreException {
		InteractionContext context;
		String handleToImportFrom;
		handleToImportFrom = getFirstContextHandle(fromFile);
		context = (InteractionContext) loadContext(handleToImportFrom, fromFile, commonContextScaling);
		context.setHandleIdentifier(handleIdentifier);
		saveContext(context);
		return context;
	}

	private String getFirstContextHandle(File sourceFile) throws CoreException {
		try {
			ZipFile zipFile = new ZipFile(sourceFile);
			try {
				for (Enumeration<?> e = zipFile.entries(); e.hasMoreElements();) {
					ZipEntry entry = (ZipEntry) e.nextElement();
					String name = entry.getName();
					String decodedName = URLDecoder.decode(name, InteractionContextManager.CONTEXT_FILENAME_ENCODING);
					if (decodedName.length() > InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD.length()) {
						return decodedName.substring(0, decodedName.length()
								- InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD.length());
					}
				}
				return null;
			} finally {
				zipFile.close();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN,
					"Could not get context handle from " + sourceFile, e)); //$NON-NLS-1$
		}
	}

	public IInteractionContext loadContext(String handleIdentifier, File fromFile,
			IInteractionContextScaling contextScaling) {
		IInteractionContext loadedContext = externalizer.readContextFromXml(handleIdentifier, fromFile, contextScaling);
		if (loadedContext == null) {
			return new InteractionContext(handleIdentifier, contextScaling);
		} else {
			return loadedContext;
		}
	}

	// TODO: interaction activity capture should be locked or queued for the duration of this and other saves
	public void saveActiveContext() {
		// FIXME this should not reference the context manager
		IInteractionContext context = ContextCore.getContextManager().getActiveContext();
		if (context != null && context.getHandleIdentifier() != null) {
			saveContext(context);
		}
	}

	public void saveContext(IInteractionContext context, String fileName) {
		externalizer.writeContextToXml(context, getFileForContext(fileName));
	}

	public void saveContext(IInteractionContext context) {
		// FIXME this should not reference the context manager
		boolean wasPaused = ContextCore.getContextManager().isContextCapturePaused();
		try {
			// TODO: make this asynchronous by creating a copy
			if (!wasPaused) {
				// FIXME this should not reference the context manager
				ContextCore.getContextManager().setContextCapturePaused(true);
			}

			if (context instanceof InteractionContext) {
				((InteractionContext) context).collapse();
			}
			externalizer.writeContextToXml(context, getFileForContext(context.getHandleIdentifier()));

			if (context.getAllElements().size() == 0) {
				removeFromCache(context);
			} else {
				addToCache(context);
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "could not save context", t)); //$NON-NLS-1$
		} finally {
			if (!wasPaused) {
				// FIXME this should not reference the context manager
				ContextCore.getContextManager().setContextCapturePaused(false);
			}
		}
	}

	private boolean addToCache(IInteractionContext context) {
		initCache();
		return contextFiles.add(getFileForContext(context.getHandleIdentifier()));
	}

	private void initCache() {
		if (contextFiles == null) {
			contextFiles = new HashSet<File>();
			File[] files = contextDirectory.listFiles();
			for (File file : files) {
				contextFiles.add(file);
			}
		}
	}

	private boolean removeFromCache(IInteractionContext context) {
		if (contextFiles != null) {
			return contextFiles.remove(getFileForContext(context.getHandleIdentifier()));
		} else {
			return false;
		}
	}

	public IInteractionContext cloneContext(String sourceContextHandle, String destinationContextHandle) {

		InteractionContext readContext = (InteractionContext) externalizer.readContextFromXml(sourceContextHandle,
				getFileForContext(sourceContextHandle), commonContextScaling);

		if (readContext == null) {
			return new InteractionContext(destinationContextHandle, commonContextScaling);
		} else {
			readContext.setHandleIdentifier(destinationContextHandle);
			saveContext(readContext);
		}

//		IInteractionContext context = importContext(destinationContextHandle, getFileForContext(sourceContextHandle));
//		if (context != null) {
//			saveContext(context);
//		} else {
//			StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Could not copy context from: "
//					+ sourceContextHandle));
//		}
		return readContext;
	}

	public boolean hasContext(String handleIdentifier) {
		Assert.isNotNull(handleIdentifier);
		File file = getFileForContext(handleIdentifier);
		initCache();
		return contextFiles.contains(file);
	}

	public File getFileForContext(String handleIdentifier) {
		String encoded;
		try {
			encoded = URLEncoder.encode(handleIdentifier, InteractionContextManager.CONTEXT_FILENAME_ENCODING);
			File contextDirectory = getContextDirectory();
			File contextFile = new File(contextDirectory, encoded + InteractionContextManager.CONTEXT_FILE_EXTENSION);
			return contextFile;
		} catch (UnsupportedEncodingException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN,
					"Could not determine path for context", e)); //$NON-NLS-1$
		}
		return null;
	}

	public void deleteContext(String handleIdentifier) {
		try {
			File file = getFileForContext(handleIdentifier);
			if (file.exists()) {
				file.delete();
			}

			if (contextFiles != null) {
				contextFiles.remove(getFileForContext(handleIdentifier));
			}
		} catch (SecurityException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN,
					"Could not delete context file, insufficient permissions.", e)); //$NON-NLS-1$
		}
	}

	/**
	 * Can consider making this API, but it should not expose a zip stream.
	 */
	public void export(String handleIdentifier, ZipOutputStream outputStream) throws IOException {
		IInteractionContext context = loadContext(handleIdentifier);
		externalizer.writeContext(context, outputStream);
	}

	@Deprecated
	public void addListener(IContextStoreListener listener) {
		listeners.add(listener);
	}

	@Deprecated
	public void removeListener(IContextStoreListener listener) {
		listeners.remove(listener);
	}

}
