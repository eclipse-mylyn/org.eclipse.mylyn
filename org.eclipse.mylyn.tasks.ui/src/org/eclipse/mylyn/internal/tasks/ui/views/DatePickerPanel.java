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

package org.eclipse.mylar.internal.tasks.ui.views;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Bahadir Yagan
 * @author Mik Kersten
 * @author Rob Elves
 */
public class DatePickerPanel extends Composite implements KeyListener, ISelectionProvider {

	// private Combo timeCombo = null;

	// private Combo monthCombo = null;

	private org.eclipse.swt.widgets.List timeList = null;

	private org.eclipse.swt.widgets.List monthList = null;

	private Spinner yearSpinner = null;

	private Composite headerComposite = null;

	private Composite calendarComposite = null;

	private Calendar initialDate = null;

	private Calendar date = null;

	private ISelection selection = null;

	private DateFormatSymbols dateFormatSymbols = null;

	private Label[] calendarLabels = null;

	private List<ISelectionChangedListener> selectionListeners = new ArrayList<ISelectionChangedListener>();

	public DatePickerPanel(Composite parent, int style, Calendar initialDate) {
		super(parent, style);
		this.initialDate = GregorianCalendar.getInstance();
		this.initialDate.setTime(initialDate.getTime());
		this.date = initialDate;
		initialize();
		updateCalendar();
	}

	private void initialize() {
		if (date == null) {
			date = GregorianCalendar.getInstance();
		}
		dateFormatSymbols = new DateFormatSymbols();
		calendarLabels = new Label[42];
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		// gridLayout.horizontalSpacing = 3;
		// gridLayout.verticalSpacing = 3;
		this.setLayout(gridLayout);
		// pickerPanel.setSize(new org.eclipse.swt.graphics.Point(400, 200));
		createMonthList(this);
		Composite middle = new Composite(this, SWT.NONE);
		GridLayout middleLayout = new GridLayout();
		middleLayout.verticalSpacing = 0;
		middle.setLayout(middleLayout);
		createYearSpinner(middle);
		createComposite(middle);
		createComposite1(middle);
		createCalendarData();
		createTimeList(this);

	}

	/**
	 * This method initializes the month combo
	 * 
	 */
	private void createTimeList(Composite composite) {
		timeList = new org.eclipse.swt.widgets.List(composite, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		Calendar tempCalendar = Calendar.getInstance();		
		tempCalendar.set(Calendar.MINUTE, 0);
		tempCalendar.set(Calendar.SECOND, 0);
		for(int x = 0; x < 24; x++) {
			tempCalendar.set(Calendar.HOUR_OF_DAY, x);
			timeList.add(dateFormat.format(tempCalendar.getTime()));
		}

		if (date == null) {
			timeList.select(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		} else {
			timeList.select(date.get(Calendar.HOUR_OF_DAY));
		}
		timeList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				date.set(Calendar.HOUR_OF_DAY, timeList.getSelectionIndex());
				date.set(Calendar.MINUTE, 0);
				setSelection(new DateSelection(date));
				notifyListeners(new SelectionChangedEvent(DatePickerPanel.this, getSelection()));
				// updateCalendar();
			}
		});
		GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 150).grab(false, true).applyTo(timeList);
		timeList.addKeyListener(this);
	}

	// /**
	// * This method initializes the month combo
	// *
	// */
	// private void createTimeCombo(Composite composite) {
	// timeCombo = new Combo(composite, SWT.READ_ONLY);
	// timeCombo.setItems(new String[] { "12:00 AM", "1:00 AM", "2:00 AM", "3:00
	// AM", "4:00 AM", "5:00 AM",
	// "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM",
	// "12:00 PM", "1:00 PM",
	// "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00
	// PM", "9:00 PM", "10:00 PM",
	// "11:00 PM" });
	// if(date == null) {
	// timeCombo.select(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
	// } else {
	// timeCombo.select(date.get(Calendar.HOUR_OF_DAY));
	// }
	// timeCombo.addModifyListener(new ModifyListener() {
	// public void modifyText(ModifyEvent arg0) {
	// date.set(Calendar.HOUR_OF_DAY, timeCombo.getSelectionIndex());
	// date.set(Calendar.MINUTE, 0);
	// setSelection(new DateSelection(date));
	// notifyListeners(new SelectionChangedEvent(DatePickerPanel.this,
	// getSelection()));
	// //updateCalendar();
	// }
	// });
	// timeCombo.addKeyListener(this);
	// }

	/**
	 * This method initializes the month combo
	 * 
	 */
	private void createMonthList(Composite composite) {
		monthList = new org.eclipse.swt.widgets.List(composite, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		for (String month : dateFormatSymbols.getMonths()) {
			if (month != null && !month.equals("")) {
				monthList.add(month);
			}
		}
		GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 50).grab(false, false).applyTo(monthList);
		monthList.select(date.get(Calendar.MONTH));
		monthList.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				date.set(Calendar.MONTH, monthList.getSelectionIndex());
				setSelection(new DateSelection(date));
				notifyListeners(new SelectionChangedEvent(DatePickerPanel.this, getSelection()));
				updateCalendar();
			}
		});
		monthList.addKeyListener(this);
	}

	private void createYearSpinner(Composite composite) {
		Composite yearComposite = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(yearComposite);
		yearComposite.setLayout(new GridLayout(2, false));
		org.eclipse.swt.widgets.Label yearLabel = new org.eclipse.swt.widgets.Label(yearComposite, SWT.NONE);
		yearLabel.setText("Year: ");
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.BEGINNING;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.grabExcessHorizontalSpace = false;
		gridData1.heightHint = -1;
		gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		yearSpinner = new Spinner(yearComposite, SWT.BORDER | SWT.READ_ONLY);
		yearSpinner.setMinimum(1900);
		yearSpinner.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		yearSpinner.setDigits(0);
		yearSpinner.setMaximum(3000);
		yearSpinner.setLayoutData(gridData1);
		yearSpinner.setSelection(date.get(Calendar.YEAR));
		yearSpinner.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent arg0) {
				date.set(Calendar.YEAR, yearSpinner.getSelection());
				setSelection(new DateSelection(date));
				notifyListeners(new SelectionChangedEvent(DatePickerPanel.this, getSelection()));
				updateCalendar();
			}

		});
		yearSpinner.addKeyListener(this);
	}

	private void createComposite(Composite composite) {
		String[] weekDays = dateFormatSymbols.getWeekdays();
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 7;
		gridLayout1.verticalSpacing = 0;
		gridLayout1.makeColumnsEqualWidth = true;
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		// gridData.horizontalSpan = 3;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		headerComposite = new Composite(composite, SWT.NONE);
		headerComposite.setLayoutData(gridData);
		headerComposite.setLayout(gridLayout1);
		GridData labelGridData = new org.eclipse.swt.layout.GridData();
		labelGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		labelGridData.grabExcessHorizontalSpace = true;
		labelGridData.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		for (int i = 1; i < 8; ++i) {
			Label headerLabel = new Label(headerComposite, SWT.BEGINNING);
			headerLabel.setText(weekDays[i].substring(0, 3));
			headerLabel.setLayoutData(labelGridData);
		}
	}

	/**
	 * This method initializes composite1
	 * 
	 */
	private void createComposite1(Composite composite) {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 7;
		gridLayout2.verticalSpacing = 0;
		gridLayout2.makeColumnsEqualWidth = true;
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		// gridData1.horizontalSpan = 3;
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.grabExcessHorizontalSpace = true;
		calendarComposite = new Composite(composite, SWT.BORDER);
		calendarComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		calendarComposite.setLayout(gridLayout2);
		calendarComposite.setLayoutData(gridData1);
	}

	private void createCalendarData() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		for (int i = 0; i < 42; ++i) {
			Label headerLabel = new Label(calendarComposite, SWT.CENTER);
			headerLabel.setText("99");
			headerLabel.setLayoutData(gridData);
			calendarLabels[i] = headerLabel;
			headerLabel.addMouseListener(new MouseListener() {

				public void mouseDoubleClick(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void mouseDown(MouseEvent arg0) {
					unSellectAll();
					Label label = (Label) arg0.getSource();
					if (!label.getText().equals("")) {
						label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION));
						label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
					}
				}

				public void mouseUp(MouseEvent arg0) {
					Label label = (Label) arg0.getSource();
					if (!label.getText().equals("")) {
						date.set(Calendar.YEAR, yearSpinner.getSelection());
						date.set(Calendar.MONTH, monthList.getSelectionIndex());
						date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(label.getText()));
						setSelection(new DateSelection(date));
						notifyListeners(new SelectionChangedEvent(DatePickerPanel.this, getSelection()));
					}

				}

			});
		}
	}

	private void updateCalendar() {
		unSellectAll();
		// Fill Labels
		Calendar cal = new GregorianCalendar(date.get(Calendar.YEAR), date.get(Calendar.MONTH), 1);
		int dayofWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

		for (int i = 0; i < dayofWeek; ++i) {
			calendarLabels[i].setText("");
		}

		for (int i = 1; i <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); ++i) {
			calendarLabels[i + dayofWeek - 1].setText("" + i);
		}

		for (int i = cal.getActualMaximum(Calendar.DAY_OF_MONTH) + dayofWeek; i < 42; ++i) {
			calendarLabels[i].setText("");
		}

		calendarLabels[date.get(Calendar.DAY_OF_MONTH) + dayofWeek - 1].setBackground(Display.getDefault()
				.getSystemColor(SWT.COLOR_LIST_SELECTION));
		calendarLabels[date.get(Calendar.DAY_OF_MONTH) + dayofWeek - 1].setForeground(Display.getDefault()
				.getSystemColor(SWT.COLOR_WHITE));

	}

	private void unSellectAll() {
		for (int i = 0; i < 42; ++i) {
			calendarLabels[i].setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
			calendarLabels[i].setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		}
	}

	public void setDate(Calendar date) {
		this.date = date;
		updateCalendar();
	}

	public void keyPressed(KeyEvent e) {
		if (e.keyCode == SWT.ESC) {
			SelectionChangedEvent changeEvent = new SelectionChangedEvent(this, new ISelection() {
				public boolean isEmpty() {
					return true;
				}
			});
			notifyListeners(changeEvent);
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	private void notifyListeners(SelectionChangedEvent event) {
		for (ISelectionChangedListener listener : selectionListeners) {
			listener.selectionChanged(event);
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}

	public ISelection getSelection() {
		return selection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection) {
		this.selection = selection;
	}

	public class DateSelection implements ISelection {
		private Calendar date;

		public DateSelection(Calendar calendar) {
			date = calendar;
		}

		public boolean isEmpty() {
			return date == null;
		}

		public Calendar getDate() {
			return date;
		}

	}

}
