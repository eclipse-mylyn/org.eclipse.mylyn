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

package org.eclipse.mylyn.internal.tasks.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylyn.internal.provisional.commons.ui.DatePicker;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
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

	private final List<IRepositoryElement> tasks;

	private final ScheduleTaskMenuContributor contributor;

	private DateRange scheduledDate;

	private final boolean isFloating = false;

	public ScheduleDatePicker(Composite parent, AbstractTask task, int style) {
		super(parent, style);
		if (task != null) {
			if (task.getScheduledForDate() != null) {
				this.scheduledDate = task.getScheduledForDate();
			}
		}

		initialize((style & SWT.FLAT) > 0 ? SWT.FLAT : 0);
		contributor = new ScheduleTaskMenuContributor() {

			@Override
			protected DateRange getScheduledForDate(AbstractTask singleTaskSelection) {
				return ScheduleDatePicker.this.scheduledDate;
			}

			@Override
			protected void setScheduledDate(DateRange dateRange) {
				if (dateRange != null) {
					scheduledDate = dateRange;
				} else {
					scheduledDate = null;
				}
				updateDateText();
				notifyPickerListeners();
			}
		};
		tasks = new ArrayList<IRepositoryElement>();
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
		dateTextGridData.grabExcessHorizontalSpace = true;
		dateTextGridData.widthHint = SWT.FILL;
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
	public void setForeground(Color color) {
		pickButton.setForeground(color);
		scheduledDateText.setForeground(color);
		super.setForeground(color);
	}

	@Override
	public void setBackground(Color backgroundColor) {
		pickButton.setBackground(backgroundColor);
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
			scheduledDateText.setText(scheduledDate.toString());
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

	public DateRange getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(DateRange date) {
		scheduledDate = date;
		updateDateText();
	}

	public boolean isFloatingDate() {
		return isFloating;
	}

}
