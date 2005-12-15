/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.internal.planner.ui;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.tasklist.ui.views.DatePicker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mik Kersten
 * @author Ken Sueda (original prototype)
 */
public class TaskPlannerWizardPage extends WizardPage {

	private static final int DEFAULT_DAYS = 1;
	private static final String TITLE = "Mylar Task Planner";
	private static final String DESCRIPTION = 
		"Summarizes task activity and assists planning future tasks.";
	
	private long DAY = 24 * 3600 * 1000;
	
	private Date reportStartDate = null;
	
	private Text numDays;
	private int numDaysToReport = 0;
	
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
				try{
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
		
		Label label2 = new Label(container, SWT.NULL);
		label2.setText("Or provide report start date: ");
        final DatePicker datePicker = new DatePicker(container, SWT.NULL);	
//        datePicker.setDateText("<reminder>");
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
		
		setControl(container);
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
