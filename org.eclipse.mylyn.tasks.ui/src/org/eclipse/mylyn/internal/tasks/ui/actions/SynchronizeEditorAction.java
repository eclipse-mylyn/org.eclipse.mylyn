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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Rob Elves
 */
public class SynchronizeEditorAction extends BaseSelectionListenerAction {

	private static final String LABEL = "Synchronize";

	private static final String TOOLTIP = "Synchronize Incoming Changes";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.synchronize.editor";

	public SynchronizeEditorAction() {
		super(LABEL);
		setToolTipText(TOOLTIP);
		setId(ID);
		setImageDescriptor(CommonImages.REFRESH_SMALL);
		// setAccelerator(SWT.MOD1 + 'r');
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		if (getStructuredSelection() != null) {
			for (Iterator it = getStructuredSelection().iterator(); it.hasNext();) {
				runWithSelection(it.next());
			}
		}
	}

	private void runWithSelection(final Object selectedObject) {
		final TaskEditor editor;
		final ITask task;
		if (selectedObject instanceof TaskEditor) {
			editor = (TaskEditor) selectedObject;
			task = editor.getTaskEditorInput().getTask();
		} else {
			return;
		}

		if (task != null) {
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					task.getConnectorKind());
			if (connector != null) {
				TasksUiInternal.synchronizeTask(connector, task, true, new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								try {
									if (selectedObject instanceof TaskEditor) {
										TaskEditor editor = (TaskEditor) selectedObject;
										editor.refreshPages();
									}
								} finally {
									if (editor != null) {
										editor.showBusy(false);
									}
								}
							}
						});
					}
				});
			}
			if (editor != null) {
				editor.showBusy(true);
			}
		}

	}
}
