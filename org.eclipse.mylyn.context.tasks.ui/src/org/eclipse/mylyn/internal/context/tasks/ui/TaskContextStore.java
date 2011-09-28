/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;

/**
 * @author Steffen Pingel
 */
public class TaskContextStore extends AbstractTaskContextStore {

	@Override
	public IAdaptable cloneContext(ITask sourceTask, ITask targetTask) {
		ContextCorePlugin.getContextStore().saveActiveContext();
		final IInteractionContext result = ContextCore.getContextStore().cloneContext(sourceTask.getHandleIdentifier(),
				targetTask.getHandleIdentifier());

		// migrate editor memento
		ContextUiPlugin.getEditorManager().copyEditorMemento(sourceTask.getHandleIdentifier(),
				targetTask.getHandleIdentifier());

		// migrate task activity
		ChangeActivityHandleOperation operation = new ChangeActivityHandleOperation(sourceTask.getHandleIdentifier(),
				targetTask.getHandleIdentifier());
		try {
			operation.run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					"Failed to migrate activity to new task", e.getCause())); //$NON-NLS-1$
		} catch (InterruptedException e) {
			// ignore
		}
		return new IAdaptable() {
			public Object getAdapter(Class adapter) {
				if (adapter == IInteractionContext.class) {
					return result;
				}
				return null;
			}
		};
	}

	@Override
	public void deleteContext(ITask task) {
		ContextCorePlugin.getContextManager().deleteContext(task.getHandleIdentifier());
	}

	@Override
	public File getFileForContext(ITask task) {
		return ContextCorePlugin.getContextStore().getFileForContext(task.getHandleIdentifier());
	}

	@Override
	public boolean hasContext(ITask task) {
		return ContextCore.getContextStore().hasContext(task.getHandleIdentifier());
	}

	@Override
	public void refactorRepositoryUrl(String oldRepositoryUrl, String newRepositoryUrl) {
		refactorMetaContextHandles(oldRepositoryUrl, newRepositoryUrl);
		refactorContextFileNames(oldRepositoryUrl, newRepositoryUrl);
	}

	@Override
	public void saveActiveContext() {
		ContextCorePlugin.getContextStore().saveActiveContext();
	}

	@Override
	public void setContextDirectory(File contextStoreDir) {
		ContextCorePlugin.getContextStore().setContextDirectory(contextStoreDir);
	}

	@SuppressWarnings("restriction")
	private void refactorContextFileNames(String oldUrl, String newUrl) {
		File dataDir = new File(TasksUiPlugin.getDefault().getDataDirectory(), ITasksCoreConstants.CONTEXTS_DIRECTORY);
		if (dataDir.exists() && dataDir.isDirectory()) {
			File[] files = dataDir.listFiles();
			if (files != null) {
				for (File file : dataDir.listFiles()) {
					int dotIndex = file.getName().lastIndexOf(".xml"); //$NON-NLS-1$
					if (dotIndex != -1) {
						String storedHandle;
						try {
							storedHandle = URLDecoder.decode(file.getName().substring(0, dotIndex),
									InteractionContextManager.CONTEXT_FILENAME_ENCODING);
							int delimIndex = storedHandle.lastIndexOf(RepositoryTaskHandleUtil.HANDLE_DELIM);
							if (delimIndex != -1) {
								String storedUrl = storedHandle.substring(0, delimIndex);
								if (oldUrl.equals(storedUrl)) {
									String id = RepositoryTaskHandleUtil.getTaskId(storedHandle);
									String newHandle = RepositoryTaskHandleUtil.getHandle(newUrl, id);
									File newFile = ContextCorePlugin.getContextStore().getFileForContext(newHandle);
									file.renameTo(newFile);
								}
							}
						} catch (Exception e) {
							StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
									"Could not move context file: " + file.getName(), e)); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("restriction")
	private void refactorMetaContextHandles(String oldRepositoryUrl, String newRepositoryUrl) {
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		ContextCorePlugin.getContextManager().resetActivityMetaContext();
		InteractionContext newMetaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		for (InteractionEvent event : metaContext.getInteractionHistory()) {
			if (event.getStructureHandle() != null) {
				String storedUrl = RepositoryTaskHandleUtil.getRepositoryUrl(event.getStructureHandle());
				if (storedUrl != null) {
					if (oldRepositoryUrl.equals(storedUrl)) {
						String taskId = RepositoryTaskHandleUtil.getTaskId(event.getStructureHandle());
						if (taskId != null) {
							String newHandle = RepositoryTaskHandleUtil.getHandle(newRepositoryUrl, taskId);
							event = new InteractionEvent(event.getKind(), event.getStructureKind(), newHandle,
									event.getOriginId(), event.getNavigation(), event.getDelta(),
									event.getInterestContribution(), event.getDate(), event.getEndDate());
						}
					}
				}
			}
			newMetaContext.parseEvent(event);
		}
	}

	@Override
	public void mergeContext(ITask sourceTask, ITask targetTask) {
		ContextCorePlugin.getContextStore().merge(sourceTask.getHandleIdentifier(), targetTask.getHandleIdentifier());
		boolean shouldCopyEditorMemento = !ContextUiPlugin.getEditorManager().hasEditorMemento(
				targetTask.getHandleIdentifier());
		if (shouldCopyEditorMemento) {
			// copy editor memento
			ContextUiPlugin.getEditorManager().copyEditorMemento(sourceTask.getHandleIdentifier(),
					targetTask.getHandleIdentifier());
		}
	}

}
