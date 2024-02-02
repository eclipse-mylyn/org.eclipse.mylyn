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
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

	/**
	 * Cache of available context files.
	 */
	private Set<File> contextFiles;

	private final InteractionContextExternalizer externalizer = new InteractionContextExternalizer();

	private final IInteractionContextScaling commonContextScaling;

	private final List<IContextStoreListener> listeners = new ArrayList<>();

	public LocalContextStore(IInteractionContextScaling commonContextScaling) {
		this.commonContextScaling = commonContextScaling;
	}

	public synchronized void setContextDirectory(File directory) {
		contextDirectory = directory;
		contextFiles = null;
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
	}

	public InputStream getAdditionalContextInformation(IInteractionContext context, String identifier)
			throws IOException {
		File fileForContext = getFileForContext(context.getHandleIdentifier());
		return externalizer.getAdditionalInformation(fileForContext, identifier);
	}

	@Override
	public IInteractionContext importContext(String handleIdentifier, File fromFile) throws CoreException {
		InteractionContext context;
		String handleToImportFrom;
		handleToImportFrom = InteractionContextExternalizer.getFirstContextHandle(fromFile);
		context = (InteractionContext) loadContext(handleToImportFrom, fromFile, commonContextScaling);
		context.setHandleIdentifier(handleIdentifier);
		saveContext(context);
		return context;
	}

	/**
	 * @return The loaded context, or a newly created one.
	 */
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
		try {
			externalizer.writeContextToXml(context, getFileForContext(fileName));
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Failed to write context " //$NON-NLS-1$
					+ context.getHandleIdentifier(), e));
		}
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
			synchronized (context) {
				IInteractionContext contextToSave = context;
				if (context instanceof InteractionContext) {
					contextToSave = ((InteractionContext) context).createCollapsedWritableCopy();
				}

				externalizer.writeContextToXml(contextToSave, getFileForContext(contextToSave.getHandleIdentifier()));
			}

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
			contextFiles = new HashSet<>();
			File[] files = contextDirectory.listFiles();
			Collections.addAll(contextFiles, files);
		}
	}

	private boolean removeFromCache(IInteractionContext context) {
		if (contextFiles != null) {
			return contextFiles.remove(getFileForContext(context.getHandleIdentifier()));
		} else {
			return false;
		}
	}

	public void merge(String sourceTaskHandle, String targetTaskHandle) {
		IInteractionContext sourceContext = loadContext(sourceTaskHandle);
		IInteractionContext targetContext = loadContext(targetTaskHandle);
		if (targetContext instanceof InteractionContext) {
			((InteractionContext) targetContext).addEvents(sourceContext);
			ContextCorePlugin.getContextStore().saveContext(targetContext);
		} else {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Cannot merge contexts of " //$NON-NLS-1$
					+ sourceTaskHandle + " and " + targetTaskHandle, new Exception().fillInStackTrace())); //$NON-NLS-1$
		}
	}

	@Override
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

	@Override
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
			StatusHandler.log(
					new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Could not determine path for context", e)); //$NON-NLS-1$
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
