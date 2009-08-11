/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Jakub Jurkiewicz
 * @author Mik Kersten
 */
public class TaskSelectionDialogWithRandom extends TaskSelectionDialog {

	private static final int RANDOM_ID = IDialogConstants.CLIENT_ID + 1;

	private Button randomTaskButton;

	private boolean activateTask = false;

	public TaskSelectionDialogWithRandom(Shell parent) {
		super(parent);
	}

	@Override
	protected void createAdditionalButtons(Composite parent) {

		randomTaskButton = createButton(parent, RANDOM_ID, Messages.TaskSelectionDialog_Random_Task, false);
		randomTaskButton.setToolTipText(Messages.TaskSelectionDialogWithRandom_Feeling_Lazy_Tooltip);
		randomTaskButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent se) {

				try {
					Set<ITask> selectedTasks = new HashSet<ITask>();
					Set<ITask> allScheduled = ((TaskActivityManager) TasksUi.getTaskActivityManager()).getAllScheduledTasks();
					if (!allScheduled.isEmpty()) {
						selectedTasks.addAll(allScheduled);
						// XXX bug 280939 make sure all scheduled tasks actually exist 
						selectedTasks.retainAll(TasksUiPlugin.getTaskList().getAllTasks());
					}
					if (selectedTasks.isEmpty()) {
						selectedTasks.addAll(TasksUiPlugin.getTaskList().getAllTasks());
					}

					Set<ITask> potentialTasks = new HashSet<ITask>();
					addLowEnergyTasks(selectedTasks, potentialTasks, PriorityLevel.P5);
					addLowEnergyTasks(selectedTasks, potentialTasks, PriorityLevel.P4);

					if (potentialTasks.isEmpty()) {
						addLowEnergyTasks(selectedTasks, potentialTasks, PriorityLevel.P3);
					}
					if (potentialTasks.isEmpty()) {
						addLowEnergyTasks(selectedTasks, potentialTasks, PriorityLevel.P2);
					}

					int randomTaskIndex = new Random().nextInt(potentialTasks.size());
					ITask randomTask = potentialTasks.toArray(new ITask[potentialTasks.size()])[randomTaskIndex];

					if (activateTask) {
						TasksUi.getTaskActivityManager().activateTask(randomTask);
					}
					TasksUiInternal.refreshAndOpenTaskListElement(randomTask);
					close();
				} catch (Exception e) {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							Messages.TaskSelectionDialogWithRandom_Feeling_Lazy_Error_Title,
							Messages.TaskSelectionDialogWithRandom_Feeling_Lazy_Error);
				}
			}

			private void addLowEnergyTasks(Set<ITask> selectedTasks, Set<ITask> potentialTasks,
					PriorityLevel priorityLevel) {
				for (ITask task : selectedTasks) {
					if (task.getSynchronizationState().isSynchronized() && !task.isCompleted()) {
						if (priorityLevel.toString().equals(task.getPriority())) {
							potentialTasks.add(task);
						}
					}
				}
			}
		});
	}

	public boolean isActivateTask() {
		return activateTask;
	}

	public void setActivateTask(boolean activateTask) {
		this.activateTask = activateTask;
	}
}
