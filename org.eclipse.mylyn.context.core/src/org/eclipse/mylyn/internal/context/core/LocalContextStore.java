/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IContextStore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
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
		return importContext(handleIdentifier, getFileForContext(handleIdentifier));
	}

	public IInteractionContext importContext(String handleIdentifier, File file) {
		return loadContext(handleIdentifier, file, commonContextScaling);
	}

	/**
	 * Creates a file for specified context and activates it
	 */

	public boolean importContext(IInteractionContext context) {
		if (context instanceof InteractionContext) {
			externalizer.writeContextToXml(context, getFileForContext(context.getHandleIdentifier()));
			return true;
		} else {
			return false;
		}
//		contextFiles.add(getFileForContext(context.getHandleIdentifier()));
//		activeContext.getContextMap().put(context.getHandleIdentifier(), context);
//		if (!activationHistorySuppressed) {
//			processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
//					IInteractionContextManager.ACTIVITY_STRUCTUREKIND_ACTIVATION, context.getHandleIdentifier(),
//					IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
//					IInteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 1f));
//		}
	}

	private IInteractionContext loadContext(String handleIdentifier, File file,
			IInteractionContextScaling contextScaling) {
		IInteractionContext loadedContext = externalizer.readContextFromXml(handleIdentifier, file, contextScaling);
		if (loadedContext == null) {
			return new InteractionContext(handleIdentifier, contextScaling);
		} else {
			return loadedContext;
		}
	}

	/**
	 * Only saves if active.
	 */

	public void saveContext(String handleIdentifier) {
		// FIXME this should not reference the context manager
		IInteractionContext context = ContextCore.getContextManager().getActiveContext();
		if (context != null && context.getHandleIdentifier() != null
				&& context.getHandleIdentifier().equals(handleIdentifier)) {
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
			// XXX: make this asynchronous by creating a copy

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
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "could not save context", t));
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

	/**
	 * Consider deleting
	 * 
	 * @param sourceContextFile
	 * @param targetcontextHandle
	 * @return
	 */
	@Deprecated
	public boolean copyContext(File sourceContextFile, String targetcontextHandle) {
		if (sourceContextFile.exists()
				&& sourceContextFile.getName().endsWith(IInteractionContextManager.CONTEXT_FILE_EXTENSION)) {
			// FIXME this should not reference the ContextCore
			IInteractionContext context = externalizer.readContextFromXml("temp", sourceContextFile,
					ContextCore.getCommonContextScaling());
			if (context == null) {
				return false;
			}
		}

		File targetContextFile = getFileForContext(targetcontextHandle);
		targetContextFile.delete();
		try {
			// FIXME this implementation is broken: it does not refactor the context handle
			copy(sourceContextFile, targetContextFile);
			contextFiles.add(targetContextFile);
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Cold not transfer context: "
					+ targetcontextHandle, e));
			return false;
		}
		return true;
	}

	public IInteractionContext cloneContext(String sourceContextHandle, String destinationContextHandle) {
		IInteractionContext context = importContext(destinationContextHandle, getFileForContext(sourceContextHandle));
		if (context != null) {
//			source.setHandleIdentifier(destinationContextHandle);
			saveContext(context);
		}
		return context;
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
			encoded = URLEncoder.encode(handleIdentifier, IInteractionContextManager.CONTEXT_FILENAME_ENCODING);
			File contextDirectory = getContextDirectory();
			File contextFile = new File(contextDirectory, encoded + IInteractionContextManager.CONTEXT_FILE_EXTENSION);
			return contextFile;
		} catch (UnsupportedEncodingException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID,
					"Could not determine path for context", e));
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
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID,
					"Could not delete context file, insufficient permissions.", e));
		}
	}

	@Deprecated
	private void copy(File src, File dest) throws IOException {
		InputStream in = new FileInputStream(src);
		try {
			OutputStream out = new FileOutputStream(dest);
			try {
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) == -1) {
					out.write(buf, 0, len);
				}
			} finally {
				out.close();
			}
		} finally {
			in.close();
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
