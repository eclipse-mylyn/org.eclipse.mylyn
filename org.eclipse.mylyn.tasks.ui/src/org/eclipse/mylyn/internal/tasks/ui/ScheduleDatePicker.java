/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.provisional.workbench.ui.DatePicker;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

/**
 * @author Rob Elves
 */
public class ScheduleDatePicker extends Composite {

	private Text scheduledDateText = null;

	private Button pickButton = null;

	private final List<SelectionListener> pickerListeners = new LinkedList<SelectionListener>();

	private final SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM);

	private final String initialText = DatePicker.LABEL_CHOOSE;

	private final List<AbstractTaskContainer> tasks;

	private final ScheduleTaskMenuContributor contributor;

	private Date scheduledDate;

	private boolean isFloating = false;

	private ScheduledTaskContainer scheduledContainer;

	public ScheduleDatePicker(Composite parent, AbstractTask task, int style) {
		super(parent, style);
		if (task != null) {
			if (task.getScheduledForDate() != null) {
				Calendar cal = TaskActivityUtil.getCalendar();
				cal.setTime(task.getScheduledForDate());
				scheduledContainer = TasksUiPlugin.getTaskActivityManager().getActivityContainer(cal, isFloating);
				this.scheduledDate = task.getScheduledForDate();
			}
			this.isFloating = task.internalIsFloatingScheduledDate();
		}

		initialize((style & SWT.FLAT) > 0 ? SWT.FLAT : 0);
		contributor = new ScheduleTaskMenuContributor() {

			@Override
			protected Date getScheduledForDate(AbstractTask singleTaskSelection) {
				return ScheduleDatePicker.this.scheduledDate;
			}

			@Override
			protected boolean isFloating(AbstractTask task) {
				return ScheduleDatePicker.this.isFloating;
			}

			@Override
			protected void setScheduledDate(ScheduledTaskContainer container) {
				if (container != null) {
					scheduledContainer = container;
					ScheduleDatePicker.this.scheduledDate = container.getStart().getTime();
					ScheduleDatePicker.this.isFloating = container.isCaptureFloating();
				} else {
					scheduledContainer = null;
					ScheduleDatePicker.this.scheduledDate = null;
					ScheduleDatePicker.this.isFloating = false;
				}
				updateDateText();
				notifyPickerListeners();
			}
		};
		tasks = new ArrayList<AbstractTaskContainer>();
		tasks.add(task);
	}

	public void setDatePattern(String pattern) {
		simpleDateFormat.applyPattern(pattern);
	}

	private void initialize(int style) {

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		this.setLayout(gridLayout);

		scheduledDateText = new Text(this, style);
		scheduledDateText.setEditable(false);
		GridData dateTextGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		dateTextGridData.widthHint = 135;
		dateTextGridData.verticalIndent = 0;

		scheduledDateText.setLayoutData(dateTextGridData);
		scheduledDateText.setText(initialText);

		pickButton = new Button(this, style | SWT.ARROW | SWT.DOWN);
		GridData pickButtonGridData = new GridData(SWT.RIGHT, SWT.FILL, false, true);
		pickButtonGridData.verticalIndent = 0;
		pickButton.setLayoutData(pickButtonGridData);
		pickButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {

				MenuManager menuManager = contributor.getSubMenuManager(tasks);
				Menu menu = menuManager.createContextMenu(pickButton);
				pickButton.setMenu(menu);
				menu.setVisible(true);
			}
		});

		updateDateText();
		pack();
	}

	public void addPickerSelectionListener(SelectionListener listener) {
		pickerListeners.add(listener);
	}

	@Override
	public void setBackground(Color backgroundColor) {
		scheduledDateText.setBackground(backgroundColor);
		super.setBackground(backgroundColor);
	}

	private void notifyPickerListeners() {
		for (SelectionListener listener : pickerListeners) {
			listener.widgetSelected(null);
		}
	}

	private void updateDateText() {
		if (scheduledDate != null) {
			if (scheduledContainer != null && !scheduledContainer.getSummary().equals("")) {
				scheduledDateText.setText(scheduledContainer.getSummary());
			} else {
				scheduledDateText.setText(simpleDateFormat.format(scheduledDate));
			}
		} else {
			scheduledDateText.setEnabled(false);
			scheduledDateText.setText(DatePicker.LABEL_CHOOSE);
			scheduledDateText.setEnabled(true);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		scheduledDateText.setEnabled(enabled);
		pickButton.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date date, boolean isFloating) {
		if (date != null) {
			Calendar cal = TaskActivityUtil.getCalendar();
			cal.setTime(date);
			scheduledContainer = TasksUiPlugin.getTaskActivityManager().getActivityContainer(cal, isFloating);
			this.isFloating = isFloating;
		} else {
			scheduledContainer = null;
		}
		scheduledDate = date;
		updateDateText();
	}

	public boolean isFloatingDate() {
		return isFloating;
	}

}
