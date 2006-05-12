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

package org.eclipse.mylar.internal.tasklist.ui.views;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.eclipse.mylar.internal.tasklist.planner.ui.DateSelectionDialog;
import org.eclipse.mylar.internal.tasklist.planner.ui.ReminderCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Temporary date picker from patch posted to:
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=19945
 * 
 * see bug# 19945
 * 
 * TODO: remove this class when an SWT date picker is added
 * 
 * @author Bahadir Yagan
 * @author Mik Kersten
 */
public class DatePicker extends Composite {

	public static final String LABEL_CHOOSE = "<choose date>";

	private Text dateText = null;

	private Button pickButton = null;

	private Calendar date = null;

//	private Shell pickerShell = null;
//
//	private DatePickerPanel datePickerPanel = null;

	private List<SelectionListener> pickerListeners = new LinkedList<SelectionListener>();
	
	SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.ENGLISH);
	
	private String initialText = "Select Date";
	
	public DatePicker(Composite parent, int style, String initialText) {
		super(parent, style);
		this.initialText = initialText;
		initialize();
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

		setSize(new org.eclipse.swt.graphics.Point(120, 19));
		
		simpleDateFormat.applyPattern("MM/dd/yy h:mm aa");
		dateText = new Text(this, SWT.NONE);
		
		GridData dateTextGridData = new org.eclipse.swt.layout.GridData();
		dateTextGridData.widthHint = 95;	
		
		dateText.setLayoutData(dateTextGridData);
		dateText.setText(initialText);
		dateText.addFocusListener(new FocusListener() {
//			DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(currentDate));
			
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
		
		
		pickButton = new Button(this, SWT.ARROW | SWT.DOWN);
		GridData pickButtonGridData = new org.eclipse.swt.layout.GridData();
		pickButtonGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		pickButton.setLayoutData(pickButtonGridData);
		pickButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				Calendar newCalendar = GregorianCalendar.getInstance();
				if(date != null) {
					newCalendar.setTime(date.getTime());
				}
				DateSelectionDialog dialog = new DateSelectionDialog(new Shell(PlatformUI.getWorkbench().getDisplay()), newCalendar, ReminderCellEditor.REMINDER_DIALOG_TITLE);
				pickButton.setEnabled(false);
				dateText.setEnabled(false);
				
				int dialogResponse = dialog.open();				
				newCalendar.setTime(dialog.getDate());
				dateSelected(dialogResponse == DateSelectionDialog.CANCEL, newCalendar);
				
//				Display display = Display.getCurrent();
//				showDatePicker((display.getCursorLocation().x), (display.getCursorLocation().y));
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});
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

	public void setDate(Calendar date) {
		this.date = date;
		updateDateText();
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		this.layout();
		return new Point(this.getSize().x, this.getSize().y);
	}

//	private void showDatePicker(int x, int y) {
//		pickerShell = new Shell(SWT.APPLICATION_MODAL);//| SWT.ON_TOP
//		pickerShell.setText("Shell");
//		pickerShell.setLayout(new FillLayout());		
//		if (date == null) {
//			date = new GregorianCalendar();
//		}
////		datePickerPanel.setDate(date);
//		datePickerPanel = new DatePickerPanel(pickerShell, SWT.NONE, date);
//		datePickerPanel.addSelectionChangedListener(new ISelectionChangedListener() {
//
//			public void selectionChanged(SelectionChangedEvent event) {
//				if(!event.getSelection().isEmpty()) {
//					dateSelected(event.getSelection().isEmpty(), ((DateSelection)event.getSelection()).getDate());
//				} else {
//					dateSelected(false, null);
//				}
//			}});
//				
//		pickerShell.setSize(new Point(240, 180));
//		pickerShell.setLocation(new Point(x, y));
//
//		datePickerPanel.addKeyListener(new KeyListener() {
//			public void keyPressed(KeyEvent e) {
//				if (e.keyCode == SWT.ESC) {
//					dateSelected(true, null);	
//				}
//			}
//
//			public void keyReleased(KeyEvent e) {
//			}
//		});
//		
//		pickerShell.addFocusListener(new FocusListener() {
//
//			public void focusGained(FocusEvent e) {
//				System.err.println(" shell - Focus Gained!");
//				
//			}
//
//			public void focusLost(FocusEvent e) {
//				System.err.println("shell -  Focus Lost!");
//				
//			}});
//		
//		pickerShell.pack();
//		pickerShell.open();		
//	}

	/** Called when the user has selected a date */
	protected void dateSelected(boolean canceled, Calendar selectedDate) {
		
		if (!canceled) {
			if(selectedDate != null) {
				this.date = selectedDate;
			}
			updateDateText();
		}
		
		notifyPickerListeners();
		pickButton.setEnabled(true);
		dateText.setEnabled(true);
//		pickerShell.close();
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
			//DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(currentDate));
		} else {
			dateText.setEnabled(false);
			dateText.setText(initialText);
			dateText.setEnabled(true);
		}
		notifyPickerListeners();
	}

	public void setEnabled(boolean enabled) {
		dateText.setEnabled(enabled);
		pickButton.setEnabled(enabled);
		super.setEnabled(enabled);
	}
}
