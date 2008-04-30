/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

/**
 * @author Bahadir Yagan
 * @author Mik Kersten
 * @author Rob Elves
 */
public class DatePickerPanel extends Composite implements KeyListener, ISelectionProvider {

	private org.eclipse.swt.widgets.List timeList = null;

	private ISelection selection = null;

	private Calendar date = null;

	private DateTime calendar = null;

	private final List<ISelectionChangedListener> selectionListeners = new ArrayList<ISelectionChangedListener>();

	private int hourOfDay = 0;

	public DatePickerPanel(Composite parent, int style, Calendar initialDate, boolean includeTime, int hourOfDay) {
		super(parent, style);
		this.date = initialDate;
		this.hourOfDay = hourOfDay;
		initialize(includeTime);
		setDate(date);
		//this.setBackground()
	}

	private void initialize(boolean includeTime) {
		if (date == null) {
			date = Calendar.getInstance();
			date.set(Calendar.HOUR_OF_DAY, hourOfDay);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MILLISECOND, 0);
		}

		GridLayout gridLayout = new GridLayout();
		if (includeTime) {
			gridLayout.numColumns = 2;
		} else {
			gridLayout.numColumns = 2;
		}
		this.setLayout(gridLayout);

		calendar = new DateTime(this, SWT.CALENDAR);
		calendar.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				date.set(Calendar.YEAR, calendar.getYear());
				date.set(Calendar.MONTH, calendar.getMonth());
				date.set(Calendar.DAY_OF_MONTH, calendar.getDay());
				setSelection(new DateSelection(date));
				notifyListeners(new SelectionChangedEvent(DatePickerPanel.this, getSelection()));
			}
		});

		if (includeTime) {
			createTimeList(this);
		}
	}

	/**
	 * This method initializes the month combo
	 * 
	 */
	private void createTimeList(Composite composite) {

		DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.set(Calendar.MINUTE, 0);
		tempCalendar.set(Calendar.SECOND, 0);
		String[] times = new String[24];
		for (int x = 0; x < 24; x++) {
			tempCalendar.set(Calendar.HOUR_OF_DAY, x);
			String timeString = dateFormat.format(tempCalendar.getTime());
			times[x] = timeString;
		}

		ListViewer listViewer = new ListViewer(composite);

		listViewer.setContentProvider(new ArrayContentProvider());
		listViewer.setInput(times);

		timeList = listViewer.getList();

		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				date.set(Calendar.HOUR_OF_DAY, timeList.getSelectionIndex());
				date.set(Calendar.MINUTE, 0);
				setSelection(new DateSelection(date));
				notifyListeners(new SelectionChangedEvent(DatePickerPanel.this, getSelection()));
			}
		});

		GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 150).grab(false, true).applyTo(timeList);
		if (date != null) {
			listViewer.setSelection(new StructuredSelection(times[date.get(Calendar.HOUR_OF_DAY)]), true);
		} else {
			listViewer.setSelection(new StructuredSelection(times[8]), true);
		}
		timeList.addKeyListener(this);
	}

	public void setDate(Calendar date) {
		this.date = date;
		calendar.setYear(date.get(Calendar.YEAR));
		calendar.setMonth(date.get(Calendar.MONTH));
		calendar.setDay(date.get(Calendar.DAY_OF_MONTH));
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
		private final Calendar date;

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
