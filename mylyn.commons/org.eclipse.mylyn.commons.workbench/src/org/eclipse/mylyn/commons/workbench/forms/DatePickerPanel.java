/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Bahadir Yagan - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.forms;

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
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.internal.commons.workbench.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Bahadir Yagan
 * @author Mik Kersten
 * @author Rob Elves
 * @since 3.7
 */
public class DatePickerPanel extends Composite implements KeyListener, ISelectionProvider {

	private org.eclipse.swt.widgets.List timeList = null;

	private ISelection selection = null;

	private Calendar date = null;

	private DateTime calendar = null;

	private final List<ISelectionChangedListener> selectionListeners = new ArrayList<>();

	private int hourOfDay = 0;

	/**
	 * @Since 3.3
	 */
	public DatePickerPanel(Composite parent, int style, Calendar initialDate, boolean includeTime, int hourOfDay) {
		this(parent, style, initialDate, includeTime, hourOfDay, -1);
	}

	public DatePickerPanel(Composite parent, int style, Calendar initialDate, boolean includeTime, int hourOfDay,
			int marginSize) {
		super(parent, style);
		date = initialDate;
		this.hourOfDay = hourOfDay;
		initialize(includeTime, marginSize);
		setDate(date);
		//this.setBackground()
	}

	private void initialize(boolean includeTime, int marginSize) {
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
			gridLayout.numColumns = 1;
		}
		if (marginSize != -1) {
			gridLayout.marginWidth = marginSize;
		}
		setLayout(gridLayout);

		calendar = new DateTime(this, SWT.CALENDAR);
		calendar.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				date.set(Calendar.YEAR, calendar.getYear());
				date.set(Calendar.MONTH, calendar.getMonth());
				date.set(Calendar.DAY_OF_MONTH, calendar.getDay());
				setSelection(new DateSelection(date, true));
				notifyListeners(new SelectionChangedEvent(DatePickerPanel.this, getSelection()));
			}

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

		Hyperlink todayLink = new Hyperlink(this, SWT.NONE);
		todayLink.setText(Messages.DatePickerPanel_Today);
		todayLink.setUnderlined(true);
		todayLink.setForeground(CommonColors.HYPERLINK_WIDGET);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).align(SWT.CENTER, SWT.TOP).applyTo(todayLink);
		todayLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				Calendar today = Calendar.getInstance();
				ISelection selection = getSelection();
				if (selection instanceof DateSelection && !selection.isEmpty()) {
					Calendar selectedDate = ((DateSelection) selection).getDate();
					if (selectedDate != null) {
						today.set(Calendar.HOUR_OF_DAY, selectedDate.get(Calendar.HOUR_OF_DAY));
						today.set(Calendar.MINUTE, selectedDate.get(Calendar.MINUTE));
						today.set(Calendar.SECOND, selectedDate.get(Calendar.SECOND));
						today.set(Calendar.MILLISECOND, selectedDate.get(Calendar.MILLISECOND));
					}
				}
				setDate(today, true);
			}
		});
	}

	/**
	 * This method initializes the month combo
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

		listViewer.addSelectionChangedListener(event -> {
			date.set(Calendar.HOUR_OF_DAY, timeList.getSelectionIndex());
			date.set(Calendar.MINUTE, 0);
			setSelection(new DateSelection(date));
			notifyListeners(new SelectionChangedEvent(DatePickerPanel.this, getSelection()));
		});

		listViewer.addOpenListener(event -> {
			date.set(Calendar.HOUR_OF_DAY, timeList.getSelectionIndex());
			date.set(Calendar.MINUTE, 0);
			setSelection(new DateSelection(date, true));
			notifyListeners(new SelectionChangedEvent(DatePickerPanel.this, getSelection()));
		});

		GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 50).applyTo(timeList);
		if (date != null) {
			listViewer.setSelection(new StructuredSelection(times[date.get(Calendar.HOUR_OF_DAY)]), true);
		} else {
			listViewer.setSelection(new StructuredSelection(times[8]), true);
		}
		timeList.addKeyListener(this);
	}

	public void setDate(Calendar date) {
		setDate(date, false);
	}

	/**
	 * @since 3.3
	 */
	public void setDate(Calendar date, boolean notifyListeners) {
		this.date = date;
		calendar.setYear(date.get(Calendar.YEAR));
		calendar.setMonth(date.get(Calendar.MONTH));
		calendar.setDay(date.get(Calendar.DAY_OF_MONTH));
		if (notifyListeners) {
			setSelection(new DateSelection(date, false));
			notifyListeners(new SelectionChangedEvent(DatePickerPanel.this, getSelection()));
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode == SWT.ESC) {
			SelectionChangedEvent changeEvent = new SelectionChangedEvent(this, () -> true);
			notifyListeners(changeEvent);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	private void notifyListeners(SelectionChangedEvent event) {
		for (ISelectionChangedListener listener : selectionListeners) {
			listener.selectionChanged(event);
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return selection;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		this.selection = selection;
	}

	public class DateSelection implements ISelection {
		private final Calendar date;

		private final boolean isDefaultSelection;

		public DateSelection(Calendar calendar) {
			this(calendar, false);
		}

		public DateSelection(Calendar calendar, boolean isDefaultSelection) {
			date = calendar;
			this.isDefaultSelection = isDefaultSelection;

		}

		@Override
		public boolean isEmpty() {
			return date == null;
		}

		public boolean isDefaultSelection() {
			return isDefaultSelection;
		}

		public Calendar getDate() {
			return date;
		}

	}

}
