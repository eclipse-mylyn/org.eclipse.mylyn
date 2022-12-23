/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.forms.DatePicker;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Rob Elves
 */
public class ScheduleDatePicker extends Composite {

	private Text scheduledDateText;

	private Button pickButton;

	private final List<SelectionListener> pickerListeners = new LinkedList<SelectionListener>();

	private final String initialText = DatePicker.LABEL_CHOOSE;

	private final List<IRepositoryElement> tasks;

	private final ScheduleTaskMenuContributor contributor;

	private DateRange scheduledDate;

	private final boolean isFloating = false;

	private ImageHyperlink clearControl;

	public ScheduleDatePicker(Composite parent, AbstractTask task, int style) {
		super(parent, style);
		if (task != null) {
			if (task.getScheduledForDate() != null) {
				this.scheduledDate = task.getScheduledForDate();
			}
		}

		initialize((style & SWT.FLAT) > 0 ? SWT.FLAT : 0);
		contributor = new ScheduleTaskMenuContributor() {

			@Override
			protected DateRange getScheduledForDate(AbstractTask singleTaskSelection) {
				return ScheduleDatePicker.this.scheduledDate;
			}

			@Override
			protected void setScheduledDate(DateRange dateRange) {
				if (dateRange != null) {
					scheduledDate = dateRange;
				} else {
					scheduledDate = null;
				}
				updateDateText();
				notifyPickerListeners();
			}
		};
		tasks = new ArrayList<IRepositoryElement>();
		tasks.add(task);
	}

	private void initialize(int style) {

		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		this.setLayout(gridLayout);

		scheduledDateText = new Text(this, style);
		scheduledDateText.setEditable(false);
		GridData dateTextGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		dateTextGridData.heightHint = 5;
		dateTextGridData.grabExcessHorizontalSpace = true;
		dateTextGridData.verticalAlignment = SWT.FILL;

		scheduledDateText.setLayoutData(dateTextGridData);
		scheduledDateText.setText(initialText);

		clearControl = new ImageHyperlink(this, SWT.NONE);
		clearControl.setImage(CommonImages.getImage(CommonImages.FIND_CLEAR_DISABLED));
		clearControl.setHoverImage(CommonImages.getImage(CommonImages.FIND_CLEAR));
		clearControl.setToolTipText(Messages.ScheduleDatePicker_Clear);
		clearControl.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {

				setScheduledDate(null);
				for (IRepositoryElement task : tasks) {
					if (task instanceof AbstractTask) {
						// XXX why is this set here?
						((AbstractTask) task).setReminded(false);
					}
				}

				notifyPickerListeners();

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

				MenuManager menuManager = contributor.getSubMenuManager(tasks);
				Menu menu = menuManager.createContextMenu(pickButton);
				pickButton.setMenu(menu);
				menu.setVisible(true);
				Point location = pickButton.toDisplay(pickButton.getLocation());
				Rectangle bounds = pickButton.getBounds();

				menu.setLocation(location.x - pickButton.getBounds().x, location.y + bounds.height + 2);
			}
		});

		updateDateText();
		pack();
	}

	private void updateClearControlVisibility() {
		if (clearControl != null && clearControl.getLayoutData() instanceof GridData) {
			GridData gd = (GridData) clearControl.getLayoutData();
			gd.exclude = scheduledDate == null;
			clearControl.getParent().layout();
		}
	}

	public void addPickerSelectionListener(SelectionListener listener) {
		pickerListeners.add(listener);
	}

	@Override
	public void setForeground(Color color) {
		pickButton.setForeground(color);
		scheduledDateText.setForeground(color);
		super.setForeground(color);
	}

	@Override
	public void setBackground(Color backgroundColor) {
		pickButton.setBackground(backgroundColor);
		scheduledDateText.setBackground(backgroundColor);
		super.setBackground(backgroundColor);
	}

	private void notifyPickerListeners() {
		for (SelectionListener listener : pickerListeners) {
			listener.widgetSelected(null);
		}
	}

	private void updateDateText() {
		if (scheduledDate != null) {
			scheduledDateText.setText(scheduledDate.toString());
		} else {
			scheduledDateText.setEnabled(false);
			scheduledDateText.setText(DatePicker.LABEL_CHOOSE);
			scheduledDateText.setEnabled(true);
		}
		updateClearControlVisibility();
	}

	@Override
	public void setEnabled(boolean enabled) {
		scheduledDateText.setEnabled(enabled);
		pickButton.setEnabled(enabled);
		clearControl.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	public DateRange getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(DateRange date) {
		scheduledDate = date;
		updateDateText();
	}

	public boolean isFloatingDate() {
		return isFloating;
	}

}
