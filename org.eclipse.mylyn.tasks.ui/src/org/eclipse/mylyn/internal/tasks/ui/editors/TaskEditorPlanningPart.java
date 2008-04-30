/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Calendar;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.DatePicker;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public class TaskEditorPlanningPart extends AbstractTaskEditorPart {

	private static final int DEFAULT_ESTIMATED_TIME = 1;

	private DatePicker scheduledForDate;

	private Spinner estimatedTime;

	public TaskEditorPlanningPart() {
		setPartName("Personal Planning");
	}

	@Override
	public void commit(boolean onSave) {
		AbstractTask task = getTaskEditorPage().getTask();
		Assert.isNotNull(task);

		Calendar selectedDate = null;
		if (scheduledForDate != null) {
			selectedDate = scheduledForDate.getDate();
		}
		if (selectedDate != null) {
			TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, selectedDate.getTime());
		}

		if (estimatedTime != null) {
			task.setEstimatedTimeHours(estimatedTime.getSelection());
		}

		super.commit(onSave);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);

		Composite sectionClient = getManagedForm().getToolkit().createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 7;
		layout.makeColumnsEqualWidth = false;
		sectionClient.setLayout(layout);
		GridData clientDataLayout = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		sectionClient.setLayoutData(clientDataLayout);

		// Reminder
		getManagedForm().getToolkit().createLabel(sectionClient, "Scheduled for:");
		// label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		scheduledForDate = new DatePicker(sectionClient, SWT.FLAT, DatePicker.LABEL_CHOOSE, true,
				TasksUiPlugin.getDefault().getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR));
		scheduledForDate.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		scheduledForDate.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		Calendar newTaskSchedule = Calendar.getInstance();
		int scheduledEndHour = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
				TasksUiPreferenceConstants.PLANNING_ENDHOUR);
		// If past scheduledEndHour set for following day
		if (newTaskSchedule.get(Calendar.HOUR_OF_DAY) >= scheduledEndHour) {
			TaskActivityUtil.snapForwardNumDays(newTaskSchedule, 1);
		} else {
			TaskActivityUtil.snapEndOfWorkDay(newTaskSchedule);
		}
		scheduledForDate.setDate(newTaskSchedule);
//		Button removeReminder = getManagedForm().getToolkit().createButton(sectionClient, "Clear",
//				SWT.PUSH | SWT.CENTER);
//		removeReminder.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				scheduledForDate.setDate(null);
//			}
//		});

		ImageHyperlink clearReminder = getManagedForm().getToolkit().createImageHyperlink(sectionClient, SWT.NONE);
		clearReminder.setImage(CommonImages.getImage(CommonImages.REMOVE));
		clearReminder.setToolTipText("Clear");
		clearReminder.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				scheduledForDate.setDate(null);
			}
		});

		// 1 Blank column after Reminder clear button
		Label dummy = getManagedForm().getToolkit().createLabel(sectionClient, "");
		GridData dummyLabelDataLayout = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		dummyLabelDataLayout.horizontalSpan = 1;
		dummyLabelDataLayout.widthHint = 30;
		dummy.setLayoutData(dummyLabelDataLayout);

		// Estimated time
		getManagedForm().getToolkit().createLabel(sectionClient, "Estimated hours:");
		// label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		// estimatedTime = new Spinner(sectionClient, SWT.FLAT);
		estimatedTime = new Spinner(sectionClient, SWT.FLAT);
		estimatedTime.setDigits(0);
		estimatedTime.setMaximum(100);
		estimatedTime.setMinimum(0);
		estimatedTime.setIncrement(1);
		estimatedTime.setSelection(DEFAULT_ESTIMATED_TIME);
		estimatedTime.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		GridData estimatedDataLayout = new GridData();
		estimatedDataLayout.widthHint = 30;
		estimatedTime.setLayoutData(estimatedDataLayout);
		// getManagedForm().getToolkit().createLabel(sectionClient, "hours ");
		// label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		ImageHyperlink clearEstimated = getManagedForm().getToolkit().createImageHyperlink(sectionClient, SWT.NONE);
		clearEstimated.setImage(CommonImages.getImage(CommonImages.REMOVE));
		clearEstimated.setToolTipText("Clear");
		clearEstimated.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				estimatedTime.setSelection(0);
			}
		});

		getManagedForm().getToolkit().paintBordersFor(sectionClient);
		section.setClient(sectionClient);
		setSection(toolkit, section);
	}
}
