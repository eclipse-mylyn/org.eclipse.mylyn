/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.internal.runtime.PlatformActivator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class GerritTaskEditorTest {

	private ITask task;

	private TasksUiLogListener listener;

	@Before
	public void setUp() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "url");
		TasksUi.getRepositoryManager().addRepository(repository);
		task = TasksUi.getRepositoryModel().createTask(repository, "1");
		addLogListener();
	}

	@After
	public void tearDown() {
		removeLogListener();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	@Test
	public void testOpenEditor() {
		TasksUiUtil.openTask(task);

		for (IStatus status : listener.getStatuses()) {
			assertFalse(status.getMessage().contains("Could not create editor via factory"));
		}
	}

	private void addLogListener() {
		if (InternalPlatform.getDefault() != null && PlatformActivator.getContext() != null) {
			ILog log = InternalPlatform.getDefault().getLog(PlatformActivator.getContext().getBundle());
			if (log != null) {
				listener = new TasksUiLogListener();
				log.addLogListener(listener);
			}
		}
	}

	private void removeLogListener() {
		if (listener != null) {
			if (InternalPlatform.getDefault() != null && PlatformActivator.getContext() != null) {
				ILog log = InternalPlatform.getDefault().getLog(PlatformActivator.getContext().getBundle());
				if (log != null) {
					log.removeLogListener(listener);
				}
			}
		}
		listener = null;
	}

	private static class TasksUiLogListener implements ILogListener {
		List<IStatus> statuses = new ArrayList<>();

		@Override
		public void logging(IStatus status, String plugin) {
			if ("org.eclipse.core.runtime".equals(plugin) && TasksUiPlugin.ID_PLUGIN.equals(status.getPlugin())) {
				statuses.add(status);
			}
		}

		private Collection<IStatus> getStatuses() {
			return Collections.unmodifiableList(statuses);
		}
	}
}
