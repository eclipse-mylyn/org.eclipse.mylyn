/*******************************************************************************
 * Copyright (c) 2004, 2008 Ken Sueda and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ken Sueda - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractRetrieveTitleFromUrlJob;
import org.eclipse.mylyn.internal.provisional.commons.ui.DatePicker;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ken Sueda
 * @author Wesley Coelho (Extended to allow URL input)
 * @author Mik Kersten
 */
public class TaskInputDialog extends Dialog {

	private String taskName = ""; //$NON-NLS-1$

	private String priority = "P3"; //$NON-NLS-1$

	private String taskURL = "http://"; //$NON-NLS-1$

	private Date reminderDate;

	private Text taskNameTextWidget;

	private Text issueURLTextWidget;

	private Button getDescButton;

	public TaskInputDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout gl = new GridLayout(5, false);
		composite.setLayout(gl);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH + 180);
		composite.setLayoutData(data);

		Label taskNameLabel = new Label(composite, SWT.WRAP);
		taskNameLabel.setText(Messages.TaskInputDialog_Description);
		taskNameLabel.setFont(parent.getFont());

		taskNameTextWidget = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData taskNameGD = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		taskNameGD.widthHint = 200;
		taskNameGD.horizontalSpan = 1;
		taskNameTextWidget.setLayoutData(taskNameGD);

		final Combo c = new Combo(composite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY
				| SWT.DROP_DOWN);
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

//		Label spacer = new Label(composite, SWT.NONE);
//		GridData spacerGD = new GridData();
//		spacerGD.horizontalSpan = 1;
////		spacerGD.widthHint = 5;
//		spacer.setLayoutData(spacerGD);
//		
//		Composite reminderComp = new Composite(composite, SWT.NONE);
//		GridLayout reminderCompGL = new GridLayout(3, false);
//		reminderCompGL.marginHeight = 0;
//		reminderCompGL.marginWidth = 0;		
//		reminderComp.setLayout(reminderCompGL);
//		GridData reminderCompGD = new GridData();
//		reminderCompGD.horizontalSpan = 1;		
//		reminderCompGD.horizontalAlignment = SWT.RIGHT;
//		reminderComp.setLayoutData(reminderCompGD);
//		Label reminderLabel = new Label(reminderComp, SWT.NONE);
		final DatePicker datePicker = new DatePicker(composite, SWT.BORDER, DatePicker.LABEL_CHOOSE, true,
				TasksUiPlugin.getDefault().getPreferenceStore().getInt(ITasksUiPreferenceConstants.PLANNING_ENDHOUR));
		datePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				if (datePicker.getDate() != null) {
					reminderDate = datePicker.getDate().getTime();
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}
		});

		Button removeReminder = new Button(composite, SWT.PUSH | SWT.CENTER);
		removeReminder.setText(Messages.TaskInputDialog_Clear);
		removeReminder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				datePicker.setDate(null);
				reminderDate = null;
			}
		});

//		
//		scheduledForDate.setLayout(new GridLayout());
//		GridData datePickerGD = new GridData();
//		datePickerGD.widthHint = 300;
//		scheduledForDate.setLayoutData(datePickerGD);

		Label urlLabel = new Label(composite, SWT.WRAP);
		urlLabel.setText(Messages.TaskInputDialog_Web_Link);
		urlLabel.setFont(parent.getFont());

		issueURLTextWidget = new Text(composite, SWT.SINGLE | SWT.BORDER);
		issueURLTextWidget.setText(getDefaultIssueUrl());
		GridData urlData = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		urlData.horizontalSpan = 3;
		urlData.grabExcessHorizontalSpace = true;
		issueURLTextWidget.setLayoutData(urlData);

		getDescButton = new Button(composite, SWT.PUSH);
		getDescButton.setText(Messages.TaskInputDialog_Get_Description);
		getDescButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		setButtonStatus();

		issueURLTextWidget.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				setButtonStatus();
			}

			public void keyReleased(KeyEvent e) {
				setButtonStatus();
			}
		});

		getDescButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				retrieveTaskDescription(issueURLTextWidget.getText());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		return composite;
	}

	/**
	 * Sets the Get Description button enabled or not depending on whether there is a URL specified
	 */
	protected void setButtonStatus() {
		String url = issueURLTextWidget.getText();

		if (url.length() > 10 && (url.startsWith("http://") || url.startsWith("https://"))) { //$NON-NLS-1$ //$NON-NLS-2$
//			String defaultPrefix = ContextCore.getPreferenceStore().getString(
//					TaskListPreferenceConstants.DEFAULT_URL_PREFIX);
//			if (url.equals(defaultPrefix)) {
//				getDescButton.setEnabled(false);
//			} else {
			getDescButton.setEnabled(true);
//			}
		} else {
			getDescButton.setEnabled(false);
		}
	}

	/**
	 * Returns the default URL text for the task by first checking the contents of the clipboard and then using the
	 * default prefix preference if that fails
	 */
	protected String getDefaultIssueUrl() {

		String clipboardText = getClipboardText();
		if ((clipboardText.startsWith("http://") || clipboardText.startsWith("https://") && clipboardText.length() > 10)) { //$NON-NLS-1$ //$NON-NLS-2$
			return clipboardText;
		} else {
			return taskURL;
		}
//		String defaultPrefix = ContextCore.getPreferenceStore().getString(
//				TaskListPreferenceConstants.DEFAULT_URL_PREFIX);
//		if (!defaultPrefix.equals("")) {
//			return defaultPrefix;
//		}
	}

	/**
	 * Attempts to set the task pageTitle to the title from the specified url
	 */
	protected void retrieveTaskDescription(final String url) {
		try {
			AbstractRetrieveTitleFromUrlJob job = new AbstractRetrieveTitleFromUrlJob(issueURLTextWidget.getText()) {
				@Override
				protected void titleRetrieved(final String pageTitle) {
					taskNameTextWidget.setText(pageTitle);
				}
			};
			job.schedule();
		} catch (RuntimeException e) {
			// FIXME which exception is caught here?
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not open task web page", e)); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the contents of the clipboard or "" if no text content was available
	 */
	protected String getClipboardText() {
		Clipboard clipboard = new Clipboard(Display.getDefault());
		TextTransfer transfer = TextTransfer.getInstance();
		String contents = (String) clipboard.getContents(transfer);
		if (contents != null) {
			return contents;
		} else {
			return ""; //$NON-NLS-1$
		}
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

	public String getIssueURL() {
		return taskURL;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			taskName = taskNameTextWidget.getText();
			taskURL = issueURLTextWidget.getText();
		} else {
			taskName = null;
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.TaskInputDialog_New_Task);
	}
}
