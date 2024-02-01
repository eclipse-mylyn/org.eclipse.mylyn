/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.CommonListenerList;
import org.eclipse.mylyn.commons.core.CommonListenerList.Notifier;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.storage.CommonStore;
import org.eclipse.mylyn.commons.core.storage.ICommonStorable;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.tasks.ui.TaskContextStoreEvent.Kind;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class TaskContextStore extends AbstractTaskContextStore {

	private static final String FOLDER_DATA = "data"; //$NON-NLS-1$

	private final CommonListenerList<TaskContextStoreListener> listeners;

	private File directory;

	private CommonStore taskStore;

	private final ContextStatePersistenceHandler stateHandler;

	public TaskContextStore() {
		listeners = new CommonListenerList<>(TasksUiPlugin.ID_PLUGIN);
		stateHandler = new ContextStatePersistenceHandler();
	}

	public void addListener(TaskContextStoreListener listener) {
		listeners.add(listener);
	}

	@Override
	public void clearContext(ITask task) {
		ContextCorePlugin.getContextManager().deleteContext(task.getHandleIdentifier());

		stateHandler.clear(task);

		final TaskContextStoreEvent event = new TaskContextStoreEvent(Kind.CLEAR, task);
		listeners.notify(new Notifier<TaskContextStoreListener>() {
			@Override
			public void run(TaskContextStoreListener listener) throws Exception {
				listener.taskContextChanged(event);
			}
		});
	}

	@Override
	public IAdaptable copyContext(ITask sourceTask, ITask targetTask) {
		IInteractionContext result = copyContextInternal(sourceTask, targetTask);

		stateHandler.copy(sourceTask, targetTask);

		final TaskContextStoreEvent event = new TaskContextStoreEvent(Kind.COPY, sourceTask, targetTask);
		listeners.notify(new Notifier<TaskContextStoreListener>() {
			@Override
			public void run(TaskContextStoreListener listener) throws Exception {
				listener.taskContextChanged(event);
			}
		});

		return asAdaptable(result);
	}

	@Override
	public void deleteContext(ITask task) {
		ICommonStorable storable = getStorable(task);
		try {
			storable.deleteAll();
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					"Unexpected error while deleting context state", e)); //$NON-NLS-1$
		} finally {
			storable.release();
		}

		ContextCorePlugin.getContextManager().deleteContext(task.getHandleIdentifier());
		stateHandler.clear(task);

		final TaskContextStoreEvent event = new TaskContextStoreEvent(Kind.DELETE, task);
		listeners.notify(new Notifier<TaskContextStoreListener>() {
			@Override
			public void run(TaskContextStoreListener listener) throws Exception {
				listener.taskContextChanged(event);
			}
		});
	}

	@Override
	public File getFileForContext(ITask task) {
		return ContextCorePlugin.getContextStore().getFileForContext(task.getHandleIdentifier());
	}

	public ICommonStorable getStorable(ITask task) {
		return getTaskStore().get(getPath(task));
	}

	@Override
	public boolean hasContext(ITask task) {
		return ContextCore.getContextStore().hasContext(task.getHandleIdentifier());
	}

	@Override
	public void mergeContext(ITask sourceTask, ITask targetTask) {
		ContextCorePlugin.getContextStore().merge(sourceTask.getHandleIdentifier(), targetTask.getHandleIdentifier());

		stateHandler.merge(sourceTask, targetTask);

		final TaskContextStoreEvent event = new TaskContextStoreEvent(Kind.MERGE, sourceTask, targetTask);
		listeners.notify(new Notifier<TaskContextStoreListener>() {
			@Override
			public void run(TaskContextStoreListener listener) throws Exception {
				listener.taskContextChanged(event);
			}
		});
	}

	@Override
	public IAdaptable moveContext(ITask sourceTask, ITask targetTask) {
		final IInteractionContext result = copyContextInternal(sourceTask, targetTask);

		// move task activity
		moveTaskActivity(Map.of(sourceTask.getHandleIdentifier(), targetTask.getHandleIdentifier()));

		moveContextInStore(sourceTask, targetTask);

		return asAdaptable(result);
	}

	@Override
	public void moveContext(Map<ITask, ITask> tasks) {
		Map<String, String> handles = new HashMap<>();
		for (ITask sourceTask : tasks.keySet()) {
			handles.put(sourceTask.getHandleIdentifier(), tasks.get(sourceTask).getHandleIdentifier());
			copyContextInternal(sourceTask, tasks.get(sourceTask));
		}

		moveTaskActivity(handles);

		for (ITask sourceTask : tasks.keySet()) {
			moveContextInStore(sourceTask, tasks.get(sourceTask));
		}
	}

	private void moveTaskActivity(Map<String, String> handles) {
		ChangeActivityHandleOperation operation = new ChangeActivityHandleOperation(handles);
		try {
			operation.run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
			StatusHandler.log(
					new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, "Failed to migrate activity to new task", e)); //$NON-NLS-1$
		} catch (InterruptedException e) {
			// ignore
		}
	}

	private void moveContextInStore(ITask sourceTask, ITask targetTask) {
		try {
			getTaskStore().move(getPath(sourceTask), getPath(targetTask));
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					"Failed to migrate context state to new task", e)); //$NON-NLS-1$
		}

		final TaskContextStoreEvent event = new TaskContextStoreEvent(Kind.MOVE, sourceTask, targetTask);
		listeners.notify(new Notifier<TaskContextStoreListener>() {
			@Override
			public void run(TaskContextStoreListener listener) throws Exception {
				listener.taskContextChanged(event);
			}
		});
	}

	@Override
	public void refactorRepositoryUrl(TaskRepository repository, String oldRepositoryUrl, String newRepositoryUrl) {
		refactorMetaContextHandles(oldRepositoryUrl, newRepositoryUrl);
		refactorContextFileNames(oldRepositoryUrl, newRepositoryUrl);
		if (repository != null) {
			refactorRepositoryLocation(repository, oldRepositoryUrl, newRepositoryUrl);
		}
	}

	public void removeListener(TaskContextStoreListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void saveActiveContext() {
		ContextCorePlugin.getContextStore().saveActiveContext();

		ITask task = TasksUi.getTaskActivityManager().getActiveTask();
		if (task != null) {
			stateHandler.saved(task);

			final TaskContextStoreEvent event = new TaskContextStoreEvent(Kind.SAVE, task);
			listeners.notify(new Notifier<TaskContextStoreListener>() {
				@Override
				public void run(TaskContextStoreListener listener) throws Exception {
					listener.taskContextChanged(event);
				}
			});
		}
	}

	@Override
	public synchronized void setDirectory(File directory) {
		this.directory = directory;
		if (taskStore != null) {
			taskStore.setLocation(directory);
		}

		File contextDirectory = new File(directory.getParent(), ITasksCoreConstants.CONTEXTS_DIRECTORY);
		if (!contextDirectory.exists()) {
			contextDirectory.mkdirs();
		}
		ContextCorePlugin.getContextStore().setContextDirectory(contextDirectory);
	}

	private IAdaptable asAdaptable(final IInteractionContext result) {
		return new IAdaptable() {
			public Object getAdapter(Class adapter) {
				if (adapter == IInteractionContext.class) {
					return result;
				}
				return null;
			}
		};
	}

	private IInteractionContext copyContextInternal(ITask sourceTask, ITask targetTask) {
		ContextCorePlugin.getContextStore().saveActiveContext();
		final IInteractionContext result = ContextCore.getContextStore()
				.cloneContext(sourceTask.getHandleIdentifier(), targetTask.getHandleIdentifier());
		return result;
	}

	private IPath getPath(ITask task) {
		IPath path = new Path(""); //$NON-NLS-1$
		path = path.append(task.getConnectorKind() + "-" + CoreUtil.asFileName(task.getRepositoryUrl())); //$NON-NLS-1$
		path = path.append(FOLDER_DATA);
		path = path.append(CoreUtil.asFileName(task.getTaskId()));
		return path;
	}

	private synchronized CommonStore getTaskStore() {
		if (taskStore == null) {
			taskStore = new CommonStore(directory);
		}
		return taskStore;
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

	private void refactorRepositoryLocation(TaskRepository repository, String oldRepositoryUrl,
			String newRepositoryUrl) {
		IPath oldPath = new Path(repository.getConnectorKind() + "-" + CoreUtil.asFileName(oldRepositoryUrl)) //$NON-NLS-1$
				.append(FOLDER_DATA);
		IPath newPath = new Path(repository.getConnectorKind() + "-" + CoreUtil.asFileName(newRepositoryUrl)) //$NON-NLS-1$
				.append(FOLDER_DATA);
		try {
			getTaskStore().move(oldPath, newPath);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					NLS.bind("Failed to migrate data store for repository {0}", newRepositoryUrl), e)); //$NON-NLS-1$
		}
	}

	public ContextStatePersistenceHandler getStateHandler() {
		return stateHandler;
	}

}
