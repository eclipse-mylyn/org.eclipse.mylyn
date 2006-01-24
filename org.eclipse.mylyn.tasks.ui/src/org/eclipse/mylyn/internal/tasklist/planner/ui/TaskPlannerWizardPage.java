/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.planner.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskListManager;
import org.eclipse.mylar.internal.tasklist.ui.views.DatePicker;
import org.eclipse.mylar.tasklist.ITaskCategory;
import org.eclipse.mylar.tasklist.IRepositoryQuery;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mik Kersten
 * @author Ken Sueda (original prototype)
 * @author Rob Elves (categories)
 */
public class TaskPlannerWizardPage extends WizardPage {

	private static final int DEFAULT_DAYS = 1;

	private static final String TITLE = "Mylar Task Planner";

	private static final String DESCRIPTION = "Summarizes task activity and assists planning future tasks.";

	private static final String LIST_LABEL = "Choose specific categories:";

	private long DAY = 24 * 3600 * 1000;

	protected String[] columnNames = new String[] { "", "Description" };

	private Date reportStartDate = null;

	private Text numDays;

	private int numDaysToReport = 0;

	private Label filtersLabel;

	private Table filtersTable;

	public TaskPlannerWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		GridData gd = new GridData();
		gd.widthHint = 50;

		Label label = new Label(container, SWT.NULL);
		label.setText("Specify number of days to report on: ");
		numDays = new Text(container, SWT.BORDER);
		numDays.setLayoutData(gd);
		numDays.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					numDaysToReport = Integer.parseInt(numDays.getText());
					setErrorMessage(null);
				} catch (Exception ex) {
					setErrorMessage("Must be integer");
					numDaysToReport = 0;
				}
			}
		});
		numDays.setText("" + DEFAULT_DAYS);
		numDays.setFocus();
		numDaysToReport = DEFAULT_DAYS;

		Label label2 = new Label(container, SWT.NULL);
		label2.setText("Or provide report start date: ");
		final DatePicker datePicker = new DatePicker(container, SWT.NULL);
		datePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				if (datePicker.getDate() != null) {
					reportStartDate = datePicker.getDate().getTime();
					numDays.setEnabled(false);
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}
		});

		addCategorySelection(container);

		setControl(container);
	}

	/**
	 * Selection of specific category to report on in the Task Planner
	 * 
	 * @param composite
	 *            container to add categories combo box to
	 */
	private void addCategorySelection(Composite composite) {

		createFilterTable(composite, true);
		TaskListManager manager = MylarTaskListPlugin.getTaskListManager();
		if (manager == null) {
			filtersTable.setEnabled(false);
			return;
		}
		// populate categories
		for (ITaskCategory category : manager.getTaskList().getTaskCategories()) {
			if (category.isArchive())
				continue;
			TableItem item = new TableItem(filtersTable, SWT.NONE);
			item.setImage(category.getIcon());
			item.setText(category.getDescription());
			item.setChecked(false);
			item.setData(category);
		}
		// populate qeries
		for (IRepositoryQuery query : manager.getTaskList().getQueries()) {
			TableItem item = new TableItem(filtersTable, SWT.NONE);
			item.setImage(query.getIcon());
			item.setText(query.getDescription());
			item.setChecked(false);
			item.setData(query);
		}
		for (int i = 0; i < columnNames.length; i++) {
			filtersTable.getColumn(i).pack();
		}
	}

	private void createFilterTable(Composite composite, boolean enabled) {

		Font font = composite.getFont();

		this.filtersLabel = new Label(composite, SWT.NONE);
		this.filtersLabel.setText(LIST_LABEL);
		this.filtersLabel.setEnabled(enabled);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		// gridData.horizontalSpan = 2;

		this.filtersLabel.setLayoutData(gridData);
		this.filtersLabel.setFont(font);

		this.filtersTable = new Table(composite, SWT.BORDER | SWT.MULTI | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL);
		this.filtersTable.setEnabled(enabled);
		GridData data = new GridData();
		// Set heightHint with a small value so the list size will be defined by
		// the space available in the dialog instead of resizing the dialog to
		// fit all the items in the list.
		data.heightHint = filtersTable.getItemHeight();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		this.filtersTable.setLayoutData(data);
		this.filtersTable.setFont(font);

		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(filtersTable, SWT.NONE);
			column.setText(columnNames[i]);
		}

	}

	public java.util.List<Object> getSelectedFilters() {
		java.util.List<Object> result = new ArrayList<Object>();
		TableItem[] items = filtersTable.getItems();
		for (TableItem item : items) {
			if (item.getChecked()) {
				result.add(item.getData());
			}
		}
		return result;
	}

	public Date getReportStartDate() {
		if (reportStartDate != null) {
			return reportStartDate;
		} else {
			long today = new Date().getTime();
			long lastDay = numDaysToReport * DAY;

			int offsetToday = Calendar.getInstance().get(Calendar.HOUR) * 60 * 60 * 1000
					+ Calendar.getInstance().get(Calendar.MINUTE) * 60 * 1000
					+ Calendar.getInstance().get(Calendar.SECOND) * 1000;
			return new Date(today - offsetToday - lastDay);
		}
	}

}
