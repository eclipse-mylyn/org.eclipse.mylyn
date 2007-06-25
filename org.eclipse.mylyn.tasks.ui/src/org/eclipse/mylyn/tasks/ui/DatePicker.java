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

package org.eclipse.mylyn.tasks.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.ui.planner.DateSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Temporary date picker from patch posted to:
 * https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=19945
 * 
 * see bug# 19945
 * 
 * TODO: remove this class when an SWT date picker is added
 * 
 * @author Bahadir Yagan
 * @author Mik Kersten
 * @since 1.0
 */
public class DatePicker extends Composite {

	public final static String TITLE_DIALOG = "Choose Date";

	public static final String LABEL_CHOOSE = "<choose date>";

	private Text dateText = null;

	private Button pickButton = null;

	private Calendar date = null;

	private List<SelectionListener> pickerListeners = new LinkedList<SelectionListener>();

	private SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
			DateFormat.SHORT);

	private String initialText = "Select Date";

	public DatePicker(Composite parent, int style, String initialText) {
		super(parent, style);
		this.initialText = initialText;
		initialize();
	}

	public void setDatePattern(String pattern) {
		simpleDateFormat.applyPattern(pattern);
	}

	private void initialize() {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.makeColumnsEqualWidth = false;
		this.setLayout(gridLayout);

		dateText = new Text(this, SWT.FLAT);
		GridData dateTextGridData = new org.eclipse.swt.layout.GridData();
		dateTextGridData.widthHint = 135;
		dateTextGridData.horizontalAlignment = GridData.FILL;

		dateText.setLayoutData(dateTextGridData);
		dateText.setText(initialText);
		dateText.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				// key listener used because setting of date picker text causes
				// modify listener to fire which results in perpetual dirty
				// editor
				notifyPickerListeners();
			}

			public void keyReleased(KeyEvent e) {
				// ignore

			}
		});

		dateText.addFocusListener(new FocusListener() {

			Calendar calendar = Calendar.getInstance();

			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				Date reminderDate;
				try {
					reminderDate = simpleDateFormat.parse(dateText.getText());
					calendar.setTime(reminderDate);
					date = calendar;
					updateDateText();
				} catch (ParseException e1) {
					updateDateText();
				}

			}
		});

		pickButton = new Button(this, SWT.FLAT | SWT.ARROW | SWT.DOWN);
		GridData pickButtonGridData = new org.eclipse.swt.layout.GridData();
		pickButtonGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		pickButton.setLayoutData(pickButtonGridData);
		pickButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				Calendar newCalendar = GregorianCalendar.getInstance();
				newCalendar.set(Calendar.HOUR_OF_DAY, TasksUiPlugin.getTaskListManager().getStartHour());
				if(date != null) {
					newCalendar.setTime(date.getTime());
				}

				Shell shell = null;
				if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
					shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				} else {
					shell = new Shell(PlatformUI.getWorkbench().getDisplay());
				}
				DateSelectionDialog dialog = new DateSelectionDialog(shell, newCalendar, DatePicker.TITLE_DIALOG);
				pickButton.setEnabled(false);
				dateText.setEnabled(false);

				int dialogResponse = dialog.open();
				if(dialog.getDate()!=null) {
					newCalendar.setTime(dialog.getDate());
				} else {
					newCalendar = null;
				}
				dateSelected(dialogResponse == Window.CANCEL, newCalendar);

				// Display display = Display.getCurrent();
				// showDatePicker((display.getCursorLocation().x),
				// (display.getCursorLocation().y));
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

		pack();
	}

	public void addPickerSelectionListener(SelectionListener listener) {
		pickerListeners.add(listener);
	}

	/**
	 * must check for null return value
	 */
	public Calendar getDate() {
		return date;
	}

	@Override
	public void setBackground(Color backgroundColor) {
		dateText.setBackground(backgroundColor);
		super.setBackground(backgroundColor);
	}
	
	public void setDate(Calendar date) {
		this.date = date;
		updateDateText();
	}

	// private void showDatePicker(int x, int y) {
	// pickerShell = new Shell(SWT.APPLICATION_MODAL);//| SWT.ON_TOP
	// pickerShell.setText("Shell");
	// pickerShell.setLayout(new FillLayout());
	// if (date == null) {
	// date = new GregorianCalendar();
	// }
	// // datePickerPanel.setDate(date);
	// datePickerPanel = new DatePickerPanel(pickerShell, SWT.NONE, date);
	// datePickerPanel.addSelectionChangedListener(new
	// ISelectionChangedListener() {
	//
	// public void selectionChanged(SelectionChangedEvent event) {
	// if(!event.getSelection().isEmpty()) {
	// dateSelected(event.getSelection().isEmpty(),
	// ((DateSelection)event.getSelection()).getDate());
	// } else {
	// dateSelected(false, null);
	// }
	// }});
	//				
	// pickerShell.setSize(new Point(240, 180));
	// pickerShell.setLocation(new Point(x, y));
	//
	// datePickerPanel.addKeyListener(new KeyListener() {
	// public void keyPressed(KeyEvent e) {
	// if (e.keyCode == SWT.ESC) {
	// dateSelected(true, null);
	// }
	// }
	//
	// public void keyReleased(KeyEvent e) {
	// }
	// });
	//		
	// pickerShell.addFocusListener(new FocusListener() {
	//
	// public void focusGained(FocusEvent e) {
	// System.err.println(" shell - Focus Gained!");
	//				
	// }
	//
	// public void focusLost(FocusEvent e) {
	// System.err.println("shell - Focus Lost!");
	//				
	// }});
	//		
	// pickerShell.pack();
	// pickerShell.open();
	// }

	/** Called when the user has selected a date */
	protected void dateSelected(boolean canceled, Calendar selectedDate) {

		if (!canceled) {
			this.date = selectedDate != null ? selectedDate : null;
			updateDateText();
			notifyPickerListeners();
		}

		
		pickButton.setEnabled(true);
		dateText.setEnabled(true);
	}

	private void notifyPickerListeners() {
		for (SelectionListener listener : pickerListeners) {
			listener.widgetSelected(null);
		}
	}

	private void updateDateText() {
		if (date != null) {
			Date currentDate = new Date(date.getTimeInMillis());
			dateText.setText(simpleDateFormat.format(currentDate));
		} else {
			dateText.setEnabled(false);
			dateText.setText(LABEL_CHOOSE);
			dateText.setEnabled(true);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		dateText.setEnabled(enabled);
		pickButton.setEnabled(enabled);
		super.setEnabled(enabled);
	}

}
