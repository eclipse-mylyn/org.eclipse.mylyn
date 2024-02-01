/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.mylyn.commons.core.storage.ICommonStorable;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.mylyn.internal.context.ui.state.ContextState;
import org.eclipse.mylyn.internal.context.ui.state.ContextStateManager;
import org.eclipse.mylyn.internal.context.ui.state.EditorStateParticipant;
import org.eclipse.mylyn.internal.context.ui.state.PerspectiveStateParticipant;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Steffen Pingel
 */
public class ContextMementoMigrator {

	/**
	 * Only intended for tests and migration.
	 */
	@Deprecated
	public static final String PREFIX_TASK_TO_PERSPECTIVE = "org.eclipse.mylyn.ui.perspectives.task."; //$NON-NLS-1$

	/**
	 * Only intended for tests and migration.
	 */
	@Deprecated
	public static final String EDITOR_MEMENTO_PREFS_PREFIX = "editors.task."; //$NON-NLS-1$

	private final ContextStateManager stateManager;

	private boolean deleteOldDataEnabled;

	public ContextMementoMigrator(ContextStateManager stateManager) {
		this.stateManager = stateManager;

	}

	public void setDeleteOldDataEnabled(boolean deleteOldDataEnabled) {
		this.deleteOldDataEnabled = deleteOldDataEnabled;
	}

	public boolean isDeleteOldDataEnabled() {
		return deleteOldDataEnabled;
	}

	/**
	 * Migrates editor mementos and perspective ids from the preferences to a memento based store in the file-system.
	 * <p>
	 * <b>Public for testing.</b>
	 * 
	 * @since 3.4
	 */
	public IStatus migrateContextMementos(SubMonitor monitor) {
		MultiStatus status = new MultiStatus(ContextUiPlugin.ID_PLUGIN, 0,
				"Errors migrating saved editors and perspective settings", null); //$NON-NLS-1$

		ScopedPreferenceStore perspectivePreferenceStore = new ScopedPreferenceStore(new InstanceScope(),
				"org.eclipse.mylyn.context.ui"); //$NON-NLS-1$
		ScopedPreferenceStore editorPreferenceStore = new ScopedPreferenceStore(new InstanceScope(),
				"org.eclipse.mylyn.resources.ui"); //$NON-NLS-1$

		// migrate editor mementos first
		IEclipsePreferences[] perspectiveNodes = perspectivePreferenceStore.getPreferenceNodes(false);
		IEclipsePreferences[] editorNodes = editorPreferenceStore.getPreferenceNodes(false);

		monitor.beginTask(Messages.ContextMementoMigrator_0, 100);

		if (editorNodes.length > 0) {
			String[] keys;
			try {
				keys = editorNodes[0].keys();
				SubMonitor progress = monitor.newChild(80);
				progress.setWorkRemaining(keys.length);
				for (String key : keys) {
					if (key.startsWith(EDITOR_MEMENTO_PREFS_PREFIX)) {
						String contextHandle = key.substring(EDITOR_MEMENTO_PREFS_PREFIX.length());
						String mementoString = editorPreferenceStore.getString(key);
						if (mementoString != null && !mementoString.trim().equals("")) { //$NON-NLS-1$
							try {
								IMemento oldMemento = XMLMemento.createReadRoot(new StringReader(mementoString));
								InteractionContext context = new InteractionContext(contextHandle,
										ContextCore.getCommonContextScaling());
								ContextState state = stateManager.createMemento(context, contextHandle);

								// migrate editors
								IMemento newMemnto = state.createMemento(EditorStateParticipant.MEMENTO_EDITORS);
								newMemnto.putMemento(oldMemento);

								// migrate perspective
								String perspectiveId = perspectivePreferenceStore
										.getString(PREFIX_TASK_TO_PERSPECTIVE + contextHandle);
								if (perspectiveId != null && perspectiveId.length() > 0) {
									IMemento perspectiveMemento = state
											.createMemento(PerspectiveStateParticipant.MEMENTO_PERSPECTIVE);
									perspectiveMemento.putString(PerspectiveStateParticipant.KEY_ACTIVE_ID,
											perspectiveId);
								}

								ITask task = TasksUi.getRepositoryModel().getTask(contextHandle);
								if (task != null) {
									write(state, task);
								}
							} catch (Exception e) {
								status.add(new Status(IStatus.WARNING, ContextUiPlugin.ID_PLUGIN,
										NLS.bind("Migration of editor memento failed for {0}", contextHandle), e)); //$NON-NLS-1$
							}
						}

						if (isDeleteOldDataEnabled()) {
							editorNodes[0].remove(key);
							if (perspectiveNodes.length > 0) {
								perspectiveNodes[0].remove(PREFIX_TASK_TO_PERSPECTIVE + contextHandle);
							}
						}
					}
					progress.worked(1);
				}
				progress.done();
			} catch (BackingStoreException e) {
				status.add(
						new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Reading of editor mementos failed", e)); //$NON-NLS-1$
			}
		}

		// migrate remaining perspective mementos
		if (perspectiveNodes.length > 0) {
			try {
				String[] keys = perspectiveNodes[0].keys();
				SubMonitor progress = monitor.newChild(20);
				progress.setWorkRemaining(keys.length);
				for (String key : keys) {
					if (key.startsWith(PREFIX_TASK_TO_PERSPECTIVE)
							&& !key.equals(IContextUiPreferenceContstants.PERSPECTIVE_NO_ACTIVE_TASK)) {
						String contextHandle = key.substring(PREFIX_TASK_TO_PERSPECTIVE.length());
						String perspectiveId = perspectivePreferenceStore.getString(key);
						if (perspectiveId != null && perspectiveId.length() > 0) {
							try {
								InteractionContext context = new InteractionContext(contextHandle,
										ContextCore.getCommonContextScaling());
								ContextState state = stateManager.createMemento(context, contextHandle);

								// migrate perspective
								IMemento perspectiveMemento = state
										.createMemento(PerspectiveStateParticipant.MEMENTO_PERSPECTIVE);
								perspectiveMemento.putString(PerspectiveStateParticipant.KEY_ACTIVE_ID, perspectiveId);

								ITask task = TasksUi.getRepositoryModel().getTask(contextHandle);
								if (task != null) {
									write(state, task);
								}
							} catch (Exception e) {
								status.add(new Status(IStatus.WARNING, ContextUiPlugin.ID_PLUGIN,
										NLS.bind("Migration of editor memento failed for {0}", contextHandle), e)); //$NON-NLS-1$
							}
						}

						if (isDeleteOldDataEnabled()) {
							perspectiveNodes[0].remove(key);
						}
					}
					progress.worked(1);
				}
				progress.done();
			} catch (BackingStoreException e) {
				status.add(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
						"Reading of perspective mementos failed", e)); //$NON-NLS-1$
			}
		}

		try {
			editorPreferenceStore.save();
		} catch (IOException e) {
			status.add(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Saving of preference store failed", e)); //$NON-NLS-1$
		}
		try {
			perspectivePreferenceStore.save();
		} catch (IOException e) {
			status.add(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Saving of preference store failed", e)); //$NON-NLS-1$
		}

		return status;
	}

	private void write(ContextState state, ITask task) throws IOException, CoreException {
		ICommonStorable storable = ((TaskContextStore) TasksUiPlugin.getContextStore()).getStorable(task);
		try {
			if (!storable.exists("context-state.xml")) { //$NON-NLS-1$
				OutputStream out = storable.write("context-state.xml", null); //$NON-NLS-1$
				try (out) {
					stateManager.write(out, state);
				}
			}
		} finally {
			storable.release();
		}
	}

}
