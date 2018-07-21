/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.storage.ICommonStorable;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.state.ContextStateManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/**
 * Manages the persistence for {@link ContextStateManager}.
 * 
 * @author Steffen Pingel
 */
public class ContextStatePersistenceHandler {

	public static final String FILE_NAME = "context-state.xml"; //$NON-NLS-1$

	public ContextStatePersistenceHandler() {
	}

	public void activated(IInteractionContext context) {
		if (!isOnUiThread()) {
			return;
		}

		getStateManager().saveDefaultState();

		ICommonStorable storable = getStorable(context.getHandleIdentifier());
		if (storable != null) {
			try {
				InputStream in = null;
				if (storable.exists(FILE_NAME)) {
					in = storable.read(FILE_NAME, null);
				}
				try {
					getStateManager().restoreState(context, in);
				} finally {
					if (in != null) {
						in.close();
					}
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, NLS.bind(
						"Unexpected error restoring the context state for {0}", context.getHandleIdentifier()), e)); //$NON-NLS-1$
			} finally {
				storable.release();
			}
		} else {
//			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, NLS.bind(
//					"Context restore failed: No corresponding task for {0} found.", context.getHandleIdentifier()))); //$NON-NLS-1$			
		}
	}

	public void clear(ITask task) {
		if (!isOnUiThread()) {
			return;
		}

		ICommonStorable storable = getStorable(task);
		try {
			storable.delete(FILE_NAME);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, NLS.bind(
					"Unexpected error deleting the context state for {0}", task.getHandleIdentifier()), e)); //$NON-NLS-1$
		} finally {
			storable.release();
		}
	}

	public void clear(String contextHandle, boolean activeContext) {
		// bug 255588: use the handle since the context is null when it is cleared
		getStateManager().clearState(contextHandle, activeContext);
	}

	public void copy(ITask sourceTask, ITask targetTask) {
		if (!isOnUiThread()) {
			return;
		}

		copyState(sourceTask, targetTask, true);
	}

	public void deactivated(IInteractionContext context) {
		if (!isOnUiThread()) {
			return;
		}

		save(context, true);
		getStateManager().restoreDefaultState();
	}

	public void merge(ITask sourceTask, ITask targetTask) {
		if (!isOnUiThread()) {
			return;
		}

		copyState(sourceTask, targetTask, false);
	}

	public void save(IInteractionContext context) {
		save(context, false);
	}

	public void save(IInteractionContext context, boolean allowModifications) {
		if (!isOnUiThread()) {
			return;
		}

		ICommonStorable storable = getStorable(context.getHandleIdentifier());
		if (storable != null) {
			try {
				OutputStream out = storable.write(FILE_NAME, null);
				try {
					getStateManager().saveState(context, out, allowModifications);
				} finally {
					out.close();
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, NLS.bind(
						"Unexpected error saving the context state for {0}", context.getHandleIdentifier()), e)); //$NON-NLS-1$
			} finally {
				storable.release();
			}
		} else {
//			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, NLS.bind(
//					"Context save failed: No corresponding task for {0} found.", context.getHandleIdentifier()))); //$NON-NLS-1$			
		}
	}

	public void saved(ITask task) {
		if (!isOnUiThread()) {
			return;
		}

		IInteractionContext context = ContextCore.getContextManager().getActiveContext();
		if (context != null && task.getHandleIdentifier().equals(context.getHandleIdentifier())) {
			save(context);
		}
	}

	private void copyState(ITask sourceTask, ITask targetTask, boolean overwrite) {
		ICommonStorable targetStorable = getStorable(targetTask);
		try {
			if (overwrite || !targetStorable.exists(FILE_NAME)) {
				ICommonStorable sourceStorable = getStorable(sourceTask);
				try {
					copyStateFile(sourceStorable, targetStorable, FILE_NAME);
				} finally {
					sourceStorable.release();
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, NLS.bind(
					"Unexpected error saving the context state for {0}", targetTask.getHandleIdentifier()), e)); //$NON-NLS-1$
		} finally {
			targetStorable.release();
		}
	}

	private void copyStateFile(ICommonStorable sourceStorable, ICommonStorable targetStorable, String handle)
			throws CoreException, IOException {
		BufferedInputStream in = new BufferedInputStream(sourceStorable.read(handle, null));
		try {
			BufferedOutputStream out = new BufferedOutputStream(targetStorable.write(handle, null));
			try {
				byte[] buffer = new byte[4096];
				while (true) {
					int count = in.read(buffer);
					if (count == -1) {
						return;
					}
					out.write(buffer, 0, count);
				}
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}

	private ContextStateManager getStateManager() {
		return ContextUiPlugin.getDefault().getStateManager();
	}

	private ICommonStorable getStorable(ITask task) {
		return ((TaskContextStore) TasksUiPlugin.getContextStore()).getStorable(task);
	}

	private ICommonStorable getStorable(String contextHandle) {
		ITask task = TasksUi.getRepositoryModel().getTask(contextHandle);
		return (task != null) ? getStorable(task) : null;
	}

	private boolean isOnUiThread() {
		return Display.getCurrent() != null;
	}

}
