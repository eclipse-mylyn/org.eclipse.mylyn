/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.commons.ui.Messages;
import org.eclipse.mylyn.internal.provisional.commons.ui.dialogs.AbstractInPlaceDialog;
import org.eclipse.mylyn.internal.provisional.commons.ui.dialogs.IInPlaceDialogListener;
import org.eclipse.mylyn.internal.provisional.commons.ui.dialogs.InPlaceDateSelectionDialog;
import org.eclipse.mylyn.internal.provisional.commons.ui.dialogs.InPlaceDialogEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * Temporary date picker from patch posted to: https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=19945 see bug# 19945
 * TODO: remove this class when an SWT date picker is added
 * 
 * @author Bahadir Yagan
 * @author Mik Kersten
 * @since 1.0
 */
public class DatePicker extends Composite {

	public final static String TITLE_DIALOG = Messages.DatePicker_Choose_Date;

	public static final String LABEL_CHOOSE = Messages.DatePicker_Choose_Date;

	private Text dateText;

	private Button pickButton;

	private Calendar date;

	private final List<SelectionListener> pickerListeners = new LinkedList<SelectionListener>();

	private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

	private String initialText = LABEL_CHOOSE;

	private final boolean includeTimeOfday;

	private final int hourOfDay = 0;

	private int selectedHourOfDay = 0;

	private ImageHyperlink clearControl;

	public DatePicker(Composite parent, int style, String initialText, boolean includeHours, int selectedHourOfDay) {
		super(parent, style);
		this.initialText = initialText;
		this.includeTimeOfday = includeHours;
		this.selectedHourOfDay = selectedHourOfDay;
		initialize((style & SWT.FLAT) != 0 ? SWT.FLAT : 0);
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDatePattern(String pattern) {
		this.dateFormat = new SimpleDateFormat(pattern);
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	private void initialize(int style) {
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		this.setLayout(gridLayout);

		dateText = new Text(this, style);
		GridData dateTextGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		dateTextGridData.grabExcessHorizontalSpace = true;
		dateTextGridData.verticalAlignment = SWT.FILL;

		dateText.setLayoutData(dateTextGridData);
		dateText.setText(initialText);
		dateText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// key listener used because setting of date picker text causes
				// modify listener to fire which results in perpetual dirty
				// editor
				notifyPickerListeners();
			}
		});

		dateText.addFocusListener(new FocusAdapter() {
			Calendar calendar = Calendar.getInstance();

			@Override
			public void focusLost(FocusEvent e) {
				Date reminderDate;
				try {
					reminderDate = dateFormat.parse(dateText.getText());
					calendar.setTime(reminderDate);
					date = calendar;
					updateDateText();
				} catch (ParseException e1) {
					updateDateText();
				}

			}
		});

		clearControl = new ImageHyperlink(this, SWT.NONE);
		clearControl.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR_DISABLED));
		clearControl.setHoverImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		clearControl.setToolTipText(Messages.DatePicker_Clear);
		clearControl.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				dateSelected(false, null);
			}

		});
		clearControl.setBackground(clearControl.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData clearButtonGridData = new GridData();
		clearButtonGridData.horizontalIndent = 3;
		clearControl.setLayoutData(clearButtonGridData);

		pickButton = new Button(this, style | SWT.ARROW | SWT.DOWN);
		GridData pickButtonGridData = new GridData(SWT.RIGHT, SWT.FILL, false, true);
		pickButtonGridData.verticalIndent = 0;
		pickButtonGridData.horizontalIndent = 3;
		pickButton.setLayoutData(pickButtonGridData);
		pickButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				final Calendar newCalendar = Calendar.getInstance();
				newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				newCalendar.set(Calendar.MINUTE, 0);
				newCalendar.set(Calendar.SECOND, 0);
				newCalendar.set(Calendar.MILLISECOND, 0);
				if (date != null) {
					newCalendar.setTime(date.getTime());
				}
				Shell shell = pickButton.getShell();
				if (shell == null) {
					//fall back
					if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
						shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					} else {
						shell = new Shell(PlatformUI.getWorkbench().getDisplay());
					}
				}

				final InPlaceDateSelectionDialog dialog = new InPlaceDateSelectionDialog(shell, pickButton,
						newCalendar, DatePicker.TITLE_DIALOG, includeTimeOfday, selectedHourOfDay);
				dialog.addEventListener(new IInPlaceDialogListener() {

					public void buttonPressed(InPlaceDialogEvent event) {
						Calendar selectedCalendar = newCalendar;

						if (event.getReturnCode() == AbstractInPlaceDialog.ID_CLEAR) {
							dateSelected(event.getReturnCode() == Window.CANCEL, null);
						} else if (event.getReturnCode() == Window.OK) {
							if (dialog.getDate() != null) {
								if (selectedCalendar == null) {
									selectedCalendar = Calendar.getInstance();
								}
								selectedCalendar.setTime(dialog.getDate());
							} else {
								selectedCalendar = null;
							}
							dateSelected(event.getReturnCode() == Window.CANCEL, selectedCalendar);
						}

					}
				});
				dialog.open();
			}
		});
		updateClearControlVisibility();
		pack();
	}

	public void addPickerSelectionListener(SelectionListener listener) {
		pickerListeners.add(listener);
	}

	/**
	 * must check for null return value
	 * 
	 * @return Calendar
	 */
	public Calendar getDate() {
		return date;
	}

	@Override
	public void setBackground(Color backgroundColor) {
		dateText.setBackground(backgroundColor);
		pickButton.setBackground(backgroundColor);
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
	//				
	// }
	//
	// public void focusLost(FocusEvent e) {
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

	}

	private void notifyPickerListeners() {
		for (SelectionListener listener : pickerListeners) {
			listener.widgetSelected(null);
		}
	}

	private void updateDateText() {
		if (date != null) {
			Date currentDate = new Date(date.getTimeInMillis());
			dateText.setText(dateFormat.format(currentDate));
		} else {
			dateText.setEnabled(false);
			dateText.setText(LABEL_CHOOSE);
			dateText.setEnabled(true);
		}

		updateClearControlVisibility();
	}

	private void updateClearControlVisibility() {
		if (clearControl != null && clearControl.getLayoutData() instanceof GridData) {
			GridData gd = (GridData) clearControl.getLayoutData();
			gd.exclude = date == null;
			clearControl.getParent().layout();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		dateText.setEnabled(enabled);
		pickButton.setEnabled(enabled);
		clearControl.setEnabled(enabled);
		super.setEnabled(enabled);
	}

}
