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

package org.eclipse.mylar.tasklist.contribution;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 * Temporary date picker from patch posted to: 
 * 	  https://bugs.eclipse.org/bugs/show_bug.cgi?id=19945
 * 
 * see bug# 19945
 * 
 * TODO: remove this class when an SWT date picker is added
 * 
 * @author Bahadir Yagan
 */
public class DatePicker extends Composite {

	private Text dateText = null;

	private Button pickButton = null;

	private Calendar date = null;

	private Shell pickerShell = null;

	private DatePickerPanel datePickerPanel = null;

	public DatePicker(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 2;
		gridLayout.makeColumnsEqualWidth = false;
		this.setLayout(gridLayout);
		setSize(new org.eclipse.swt.graphics.Point(95, 28));
		dateText = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		dateText.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		dateText.setLayoutData(gridData1);
		pickButton = new Button(this, SWT.ARROW | SWT.DOWN);
		pickButton.setLayoutData(gridData);
		pickButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				Display display = Display.getCurrent();
				showDatePicker(display.getCursorLocation().x, 
						       display.getCursorLocation().y);
				updateDateText();
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

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
	
	private void showDatePicker(int x, int y) {
		Display display = Display.getCurrent();
		pickerShell = new Shell(SWT.APPLICATION_MODAL | SWT.ON_TOP);
		pickerShell.setText("Shell");
		pickerShell.setLayout(new FillLayout());
		datePickerPanel = new DatePickerPanel(pickerShell, SWT.NONE);
		if (date == null) {
			date = new GregorianCalendar();
		}
		datePickerPanel.setDate(date);
		pickerShell.setSize(new Point(225, 180));
		pickerShell.setLocation(new Point(x, y));
		pickerShell.open();

		while (!pickerShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		this.date = datePickerPanel.getDate();
		pickerShell.dispose();
	}

	private void updateDateText() {
		dateText.setText(date.get(Calendar.DAY_OF_MONTH) + "/"
				+ date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR));
	}
} // @jve:decl-index=0:visual-constraint="10,10"

class DatePickerPanel extends Composite {

	private Combo monthCombo = null;

	private Spinner yearSpinner = null;

	private Composite headerComposite = null;

	private Composite calendarComposite = null;

	private Calendar date = null;

	private DateFormatSymbols dateFormatSymbols = null;

	private Label[] calendarLabels = null;

	public DatePickerPanel(Composite parent, int style) {
		super(parent, style);
		initialize();
		updateCalendar();
	}

	private void initialize() {
		date = new GregorianCalendar();
		dateFormatSymbols = new DateFormatSymbols();
		calendarLabels = new Label[42];
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 3;
		gridLayout.verticalSpacing = 3;
		this.setLayout(gridLayout);
		setSize(new org.eclipse.swt.graphics.Point(277, 200));
		createCombo();
		createYearSpinner();
		createComposite();
		createComposite1();
		createCalendarData();
	}

	/**
	 * This method initializes combo
	 * 
	 */
	private void createCombo() {
		monthCombo = new Combo(this, SWT.READ_ONLY);
		monthCombo.setItems(dateFormatSymbols.getMonths());
		monthCombo.remove(12);
		monthCombo.select(date.get(Calendar.MONTH));
		monthCombo.setVisibleItemCount(12);
		monthCombo.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent arg0) {
				date.set(Calendar.MONTH, monthCombo.getSelectionIndex());
				updateCalendar();
			}

		});
	}

	private void createYearSpinner() {
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.grabExcessHorizontalSpace = false;
		gridData1.heightHint = -1;
		gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		yearSpinner = new Spinner(this, SWT.BORDER | SWT.READ_ONLY);
		yearSpinner.setMinimum(1900);
		yearSpinner.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		yearSpinner.setDigits(0);
		yearSpinner.setMaximum(3000);
		yearSpinner.setLayoutData(gridData1);
		yearSpinner.setSelection(date.get(Calendar.YEAR));
		yearSpinner.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent arg0) {
				date.set(Calendar.YEAR, yearSpinner.getSelection());
				updateCalendar();
			}

		});
	}

	/**
	 * This method initializes composite
	 * 
	 */
	private void createComposite() {
		String[] weekDays = dateFormatSymbols.getWeekdays();
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 7;
		gridLayout1.makeColumnsEqualWidth = true;
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalSpan = 2;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		headerComposite = new Composite(this, SWT.NONE);
		headerComposite.setLayoutData(gridData);
		headerComposite.setLayout(gridLayout1);
		GridData labelGridData = new org.eclipse.swt.layout.GridData();
		labelGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		labelGridData.grabExcessHorizontalSpace = true;
		labelGridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		for (int i = 1; i < 8; ++i) {
			Label headerLabel = new Label(headerComposite, SWT.CENTER);
			headerLabel.setText(weekDays[i].substring(0, 3));
			headerLabel.setLayoutData(labelGridData);
		}
	}

	/**
	 * This method initializes composite1
	 * 
	 */
	private void createComposite1() {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 7;
		gridLayout2.makeColumnsEqualWidth = true;
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		gridData1.horizontalSpan = 2;
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.grabExcessHorizontalSpace = true;
		calendarComposite = new Composite(this, SWT.BORDER);
		calendarComposite.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
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
						label.setBackground(Display.getDefault()
								.getSystemColor(SWT.COLOR_LIST_SELECTION));
						label.setForeground(Display.getDefault()
								.getSystemColor(SWT.COLOR_WHITE));
					}
				}

				public void mouseUp(MouseEvent arg0) {
					Label label = (Label) arg0.getSource();
					if (!label.getText().equals("")) {
						date.set(Calendar.YEAR, yearSpinner.getSelection());
						date
								.set(Calendar.MONTH, monthCombo
										.getSelectionIndex());
						date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(label
								.getText()));
						DatePickerPanel.this.getShell().close();
					}
				}

			});
		}
	}

	private void updateCalendar() {
		unSellectAll();
		// Fill Labels
		Calendar cal = new GregorianCalendar(date.get(Calendar.YEAR), date
				.get(Calendar.MONTH), 1);
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

		calendarLabels[date.get(Calendar.DAY_OF_MONTH) + dayofWeek]
				.setBackground(Display.getDefault().getSystemColor(
						SWT.COLOR_LIST_SELECTION));
		calendarLabels[date.get(Calendar.DAY_OF_MONTH) + dayofWeek]
				.setForeground(Display.getDefault().getSystemColor(
						SWT.COLOR_WHITE));

	}

	private void unSellectAll() {
		for (int i = 0; i < 42; ++i) {
			calendarLabels[i].setForeground(Display.getDefault()
					.getSystemColor(SWT.COLOR_BLACK));
			calendarLabels[i].setBackground(Display.getCurrent()
					.getSystemColor(SWT.COLOR_WHITE));
		}
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
		updateCalendar();
	}

} 