/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Calendar;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.DatePicker;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.ScheduleDatePicker;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public class TaskEditorPlanningPart extends AbstractTaskEditorPart {

	private static final String CLEAR = "Clear";

	private static final int DEFAULT_ESTIMATED_TIME = 1;

	private static final String NO_TIME_ELAPSED = "0 seconds";

	private static final String RESET = "Reset";

	private static final int CONTROL_WIDTH = 135;

	private DatePicker dueDatePicker;

	private Text elapsedTimeText;

	private Spinner estimatedTime;

	private ScheduleDatePicker scheduleDatePicker;

	private AbstractTask task;

	private final ITaskListChangeListener TASK_LIST_LISTENER = new TaskListChangeAdapter() {

		@Override
		public void containersChanged(Set<TaskContainerDelta> containers) {
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (taskContainerDelta.getElement() instanceof ITask) {
					final AbstractTask updateTask = (AbstractTask) taskContainerDelta.getElement();
					if (updateTask != null && task != null
							&& updateTask.getHandleIdentifier().equals(task.getHandleIdentifier())) {
						if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									updateFromTask(updateTask);
								}
							});
						}
					}
				}
			}
		}

	};

	private final ITaskActivityListener timingListener = new TaskActivityAdapter() {

		@Override
		public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
			if (task.equals(TaskEditorPlanningPart.this.task)) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (elapsedTimeText != null && !elapsedTimeText.isDisposed()) {
							updateElapsedTime();
						}
					}
				});

			}
		}
	};

	public TaskEditorPlanningPart() {
		setPartName("Personal Planning");
	}

	@Override
	public void commit(boolean onSave) {
		AbstractTask task = (AbstractTask) getTaskEditorPage().getTask();
		Assert.isNotNull(task);

		if (scheduleDatePicker != null && scheduleDatePicker.getScheduledDate() != null) {
			if (task.getScheduledForDate() == null
					|| (task.getScheduledForDate() != null && !scheduleDatePicker.getScheduledDate().equals(
							task.getScheduledForDate())) || (task).getScheduledForDate().isDay()) {
				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, scheduleDatePicker.getScheduledDate());
				(task).setReminded(false);
			}
		} else {
			TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, null);
			(task).setReminded(false);
		}

		if (estimatedTime != null) {
			task.setEstimatedTimeHours(estimatedTime.getSelection());
		}

		if (dueDatePicker != null && dueDatePicker.getDate() != null) {
			TasksUiPlugin.getTaskActivityManager().setDueDate(task, dueDatePicker.getDate().getTime());
		} else {
			TasksUiPlugin.getTaskActivityManager().setDueDate(task, null);
		}

		super.commit(onSave);
	}

	private void createActualTime(FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, "Active:");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setToolTipText("Time working on this task");

		Composite nameValueComp = createComposite(parent, 2, toolkit);

		elapsedTimeText = toolkit.createText(nameValueComp, null);
		updateElapsedTime();

		GridData td = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		td.grabExcessHorizontalSpace = true;
		elapsedTimeText.setLayoutData(td);
		elapsedTimeText.setEditable(false);

		ImageHyperlink resetActivityTimeButton = toolkit.createImageHyperlink(nameValueComp, SWT.NONE);
		resetActivityTimeButton.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		resetActivityTimeButton.setToolTipText(RESET);
		resetActivityTimeButton.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (MessageDialog.openConfirm(getControl().getShell(), "Confirm Activity Time Deletion",
						"Do you wish to reset your activity time on this task?\n\nThis will take immediate affect and can not be undone.")) {
					MonitorUi.getActivityContextManager().removeActivityTime(task.getHandleIdentifier(), 0l,
							System.currentTimeMillis());
				}
			}
		});
	}

	private void updateElapsedTime() {
		String elapsedTimeString = DateUtil.getFormattedDurationShort(TasksUiPlugin.getTaskActivityManager()
				.getElapsedTime(task));
		if (elapsedTimeString.equals("")) {
			elapsedTimeString = NO_TIME_ELAPSED;
		}
		elapsedTimeText.setText(elapsedTimeString);
	}

	private Composite createComposite(Composite parent, int col, FormToolkit toolkit) {
		Composite nameValueComp = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 3;
		nameValueComp.setLayout(layout);
		return nameValueComp;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		task = (AbstractTask) getTaskEditorPage().getTask();
		boolean hasDueDate = !getTaskEditorPage().getConnector().hasRepositoryDueDate(
				getTaskEditorPage().getTaskRepository(), task, getTaskData());

		Section section = createSection(parent, toolkit, true);
		Composite composite = getManagedForm().getToolkit().createComposite(section);
		composite.setLayout(new GridLayout((hasDueDate) ? 4 : 6, false));

		createScheduledDatePicker(toolkit, composite);

		// disable due date picker if it's a repository due date
		if (hasDueDate) {
			createDueDatePicker(toolkit, composite);
		}

		createEstimatedTime(toolkit, composite);

		createActualTime(toolkit, composite);

		TasksUiInternal.getTaskList().addChangeListener(TASK_LIST_LISTENER);
		TasksUiPlugin.getTaskActivityManager().addActivityListener(timingListener);

		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
	}

	private void createDueDatePicker(FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, "Due:");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		Composite composite = createComposite(parent, 2, toolkit);

		dueDatePicker = new DatePicker(composite, SWT.FLAT, DatePicker.LABEL_CHOOSE, true, 0);
		GridDataFactory.fillDefaults().hint(CONTROL_WIDTH, SWT.DEFAULT).applyTo(dueDatePicker);
		dueDatePicker.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		dueDatePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		if (task.getDueDate() != null) {
			Calendar calendar = TaskActivityUtil.getCalendar();
			calendar.setTime(task.getDueDate());
			dueDatePicker.setDate(calendar);
		}
		dueDatePicker.addPickerSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				markDirty();
			}
		});
		toolkit.adapt(dueDatePicker, false, false);
		toolkit.paintBordersFor(composite);

		ImageHyperlink clearDueDate = toolkit.createImageHyperlink(composite, SWT.NONE);
		clearDueDate.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		clearDueDate.setToolTipText(CLEAR);
		clearDueDate.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				dueDatePicker.setDate(null);
				markDirty();
			}
		});
	}

	private void createEstimatedTime(FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, "Estimated:");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		Composite composite = createComposite(parent, 2, toolkit);

		// Estimated time
		estimatedTime = new Spinner(composite, SWT.FLAT);
		estimatedTime.setDigits(0);
		estimatedTime.setMaximum(100);
		estimatedTime.setMinimum(0);
		estimatedTime.setIncrement(1);
		estimatedTime.setSelection(DEFAULT_ESTIMATED_TIME);
		estimatedTime.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

		ImageHyperlink clearEstimated = toolkit.createImageHyperlink(composite, SWT.NONE);
		clearEstimated.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		clearEstimated.setToolTipText("Clear");
		clearEstimated.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				estimatedTime.setSelection(0);
			}
		});
		toolkit.paintBordersFor(composite);
	}

	private void createScheduledDatePicker(FormToolkit toolkit, Composite parent) {
		Label label = toolkit.createLabel(parent, "Scheduled:");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		Composite composite = createComposite(parent, 2, toolkit);

		scheduleDatePicker = new ScheduleDatePicker(composite, task, SWT.FLAT);
		GridDataFactory.fillDefaults().hint(CONTROL_WIDTH, SWT.DEFAULT).applyTo(scheduleDatePicker);
		scheduleDatePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.adapt(scheduleDatePicker, false, false);
		toolkit.paintBordersFor(composite);

		scheduleDatePicker.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		scheduleDatePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}

			public void widgetSelected(SelectionEvent arg0) {
				markDirty();
			}
		});

		ImageHyperlink clearScheduledDate = toolkit.createImageHyperlink(composite, SWT.NONE);
		clearScheduledDate.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		clearScheduledDate.setToolTipText(CLEAR);
		clearScheduledDate.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				scheduleDatePicker.setScheduledDate(null);
				// XXX why is this set here?
				task.setReminded(false);
				markDirty();
			}
		});
	}

	@Override
	public void dispose() {
		TasksUiPlugin.getTaskActivityManager().removeActivityListener(timingListener);
		TasksUiInternal.getTaskList().removeChangeListener(TASK_LIST_LISTENER);
	}

	private void updateFromTask(AbstractTask updateTask) {
		if (scheduleDatePicker != null && !scheduleDatePicker.isDisposed()) {
			if (updateTask.getScheduledForDate() != null) {
				scheduleDatePicker.setScheduledDate(updateTask.getScheduledForDate());
			} else {
				scheduleDatePicker.setScheduledDate(null);
			}
		}
	}

}
