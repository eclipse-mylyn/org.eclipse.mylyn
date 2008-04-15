/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.planner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskListManager;
import org.eclipse.mylyn.tasks.ui.DatePicker;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mik Kersten
 * @author Ken Sueda (original prototype)
 * @author Rob Elves
 */
public class TaskActivityWizardPage extends WizardPage {

	private static final int DEFAULT_DAYS = 1;

	private static final String TITLE = "Mylyn Task Activity Report";

	private static final String DESCRIPTION = "Summarizes task activity and assists planning future tasks.";

	private final long DAY = 24 * 3600 * 1000;

	protected String[] columnNames = new String[] { "", "Description" };

	private Date reportStartDate = null;

	private Date reportEndDate = null;

	private Button daysRadioButton = null;

	private Button dateRadioButton = null;

	private Text numDays;

	private int numDaysToReport = 0;

	private Table filtersTable;

	private final TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	public TaskActivityWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.FILL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		createReportPeriodGroup(container);

		Label spacer = new Label(container, SWT.NONE);
		spacer.setText(" ");

		createCategorySelectionGroup(container);

		setControl(container);
		numDays.setFocus();
	}

	private void createReportPeriodGroup(Composite parent) {
		Group reportPeriodGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		reportPeriodGroup.setLayout(layout);
		reportPeriodGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		reportPeriodGroup.setText("Report Period");
		reportPeriodGroup.setFont(parent.getFont());

		daysRadioButton = new Button(reportPeriodGroup, SWT.RADIO | SWT.LEFT | SWT.NO_FOCUS);
		daysRadioButton.setText("Number of days prior: ");
		daysRadioButton.setSelection(true);

		numDays = new Text(reportPeriodGroup, SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = 50;
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
		numDaysToReport = DEFAULT_DAYS;

		Label spacer = new Label(reportPeriodGroup, SWT.NONE);
		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(spacer);

		dateRadioButton = new Button(reportPeriodGroup, SWT.RADIO | SWT.LEFT | SWT.NO_FOCUS);
		dateRadioButton.setText("Date range        Start:");

		final DatePicker datePicker = new DatePicker(reportPeriodGroup, SWT.BORDER, "<start date>");
		datePicker.setEnabled(false);
		Calendar startCal = Calendar.getInstance();
		TaskActivityUtil.snapStartOfDay(startCal);
		reportStartDate = startCal.getTime();
		datePicker.setDate(startCal);
		datePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				if (datePicker.getDate() != null) {
					reportStartDate = datePicker.getDate().getTime();
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}
		});

		new Label(reportPeriodGroup, SWT.NONE).setText("End:");

		final DatePicker endDatePicker = new DatePicker(reportPeriodGroup, SWT.BORDER, "<end date>");
		endDatePicker.setEnabled(false);
		Calendar endCal = Calendar.getInstance();
		TaskActivityUtil.snapEndOfWorkDay(endCal);
		reportEndDate = endCal.getTime();
		endDatePicker.setDate(endCal);
		endDatePicker.addPickerSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (datePicker.getDate() != null) {
					reportEndDate = endDatePicker.getDate().getTime();
				}
			}
		});

		SelectionListener radioListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				numDays.setEnabled(daysRadioButton.getSelection());
				datePicker.setEnabled(dateRadioButton.getSelection());
				endDatePicker.setEnabled(dateRadioButton.getSelection());
				if (daysRadioButton.getSelection()) {
					numDays.setFocus();
				}
				if (dateRadioButton.getSelection()) {
					datePicker.setFocus();
				}
			}

		};

		daysRadioButton.addSelectionListener(radioListener);
		dateRadioButton.addSelectionListener(radioListener);

	}

	/**
	 * Selection of specific category to report on in the Task Planner
	 * 
	 * @param composite
	 *            container to add categories combo box to
	 */
	private void createCategorySelectionGroup(Composite composite) {

		Group categorySelectionGroup = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		categorySelectionGroup.setLayout(layout);
		categorySelectionGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL));
		categorySelectionGroup.setText("Category Selection");
		categorySelectionGroup.setFont(composite.getFont());

		createFilterTable(categorySelectionGroup, true);
		ITaskListManager manager = TasksUi.getTaskListManager();
		if (manager == null) {
			filtersTable.setEnabled(false);
			return;
		}

		List<AbstractTaskContainer> containers = new ArrayList<AbstractTaskContainer>();
		containers.addAll(manager.getTaskList().getCategories());
		containers.addAll(manager.getTaskList().getQueries());
		//containers.add(manager.getTaskList().getArchiveContainer());
		Collections.sort(containers, new Comparator<AbstractTaskContainer>() {

			public int compare(AbstractTaskContainer c1, AbstractTaskContainer c2) {
				if (c1.equals(TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory())) {
					return -1;
				} else if (c2.equals(TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory())) {
					return 1;
				} else {
					return c1.getSummary().compareToIgnoreCase(c2.getSummary());
				}
			}

		});

		// populate table
		for (AbstractTaskContainer container : containers) {
			TableItem item = new TableItem(filtersTable, SWT.NONE);
			item.setImage(labelProvider.getImage(container));
			item.setText(container.getSummary());
			item.setData(container);
		}

		for (int i = 0; i < columnNames.length; i++) {
			filtersTable.getColumn(i).pack();
		}

		createButtonsGroup(categorySelectionGroup);

		// default to all categories selected
		setChecked(true);
	}

	/**
	 * 
	 * Creates the buttons for selecting all or none of the categories.
	 * 
	 * @param parent
	 *            parent composite
	 */
	private final void createButtonsGroup(Composite parent) {

		Font font = parent.getFont();
		new Label(parent, SWT.NONE); // Blank cell on left

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setFont(parent.getFont());

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

		Button selectButton = new Button(buttonComposite, SWT.NONE);
		selectButton.setText("Select All");

		selectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setChecked(true);
			}
		});

		selectButton.setFont(font);
		setButtonLayoutData(selectButton);

		Button deselectButton = new Button(buttonComposite, SWT.NONE);
		deselectButton.setText("Deselect All");

		deselectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setChecked(false);
			}
		});

		deselectButton.setFont(font);
		setButtonLayoutData(deselectButton);

	}

	private void setChecked(boolean checked) {
		for (TableItem item : filtersTable.getItems()) {
			item.setChecked(checked);
		}
	}

	private void createFilterTable(Composite composite, boolean enabled) {

		Font font = composite.getFont();
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

		for (String columnName : columnNames) {
			TableColumn column = new TableColumn(filtersTable, SWT.NONE);
			column.setText(columnName);
		}

	}

	public Set<AbstractTaskContainer> getSelectedContainers() {
		Set<AbstractTaskContainer> result = new HashSet<AbstractTaskContainer>();
		TableItem[] items = filtersTable.getItems();
		for (TableItem item : items) {
			if (item.getChecked() && item.getData() instanceof AbstractTaskContainer) {
				result.add((AbstractTaskContainer) item.getData());
			}
		}
		return result;
	}

	public Date getReportStartDate() {
		if (dateRadioButton.getSelection() && reportStartDate != null) {
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

	public Date getReportEndDate() {
		if (dateRadioButton.getSelection() && reportEndDate != null) {
			return reportEndDate;
		} else {
			return new Date();
		}
	}

}
