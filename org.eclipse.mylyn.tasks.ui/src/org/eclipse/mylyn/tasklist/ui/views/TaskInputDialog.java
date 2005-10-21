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

package org.eclipse.mylar.tasklist.ui.views;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.mylar.tasklist.contribution.DatePicker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ken Sueda
 * @author Wesley Coelho (Extended to allow URL input)
 */
public class TaskInputDialog extends Dialog {

	private String taskName = "";
	private String priority = "P3";
	private String url = "http://";
	private Date reminderDate = null;
	private Text taskNameTextWidget = null;
	private Text issueURLTextWidget = null;

	public TaskInputDialog(Shell parentShell) {
		super(parentShell);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout gl = new GridLayout(4, false);
		composite.setLayout(gl);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		composite.setLayoutData(data);

		Label taskNameLabel = new Label(composite, SWT.WRAP);
		taskNameLabel.setText("Task Name:");
		taskNameLabel.setFont(parent.getFont());

		taskNameTextWidget = new Text(composite, SWT.SINGLE | SWT.BORDER);
		taskNameTextWidget.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));

		final Combo c = new Combo(composite, SWT.NO_BACKGROUND | SWT.MULTI
				| SWT.V_SCROLL | SWT.READ_ONLY | SWT.DROP_DOWN);
		c.setItems(TaskListView.PRIORITY_LEVELS);
		c.setText(priority);
		c.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				priority = c.getText();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
			        
        final DatePicker datePicker = new DatePicker(composite, SWT.NULL);	
        datePicker.setDateText("<reminder>");
		datePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				reminderDate = datePicker.getDate().getTime();
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}
		});
		
		
		Label urlLabel = new Label(composite, SWT.WRAP);
		urlLabel.setText("Issue URL:");
		urlLabel.setFont(parent.getFont());

		issueURLTextWidget = new Text(composite, SWT.SINGLE | SWT.BORDER);
		issueURLTextWidget.setText(url);
		GridData urlData = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		urlData.horizontalSpan = 3;
		issueURLTextWidget.setLayoutData(urlData);
		
		
		return composite;
	}

	public String getSelectedPriority() {
		return priority;
	}

	public String getTaskname() {
		return taskName;
	}
	
	public Date getReminderDate() {
		return reminderDate;
	}

	public String getIssueURL(){
		return url;
	}
	
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			taskName = taskNameTextWidget.getText();
			url = issueURLTextWidget.getText();
		} else {
			taskName = null;
		}
		super.buttonPressed(buttonId);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Enter Task Information");
	}

}
