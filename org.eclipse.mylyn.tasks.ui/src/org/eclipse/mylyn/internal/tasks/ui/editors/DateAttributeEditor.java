/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.ui.DatePicker;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Steffen Pingel
 * @author Robert Elves
 */
public class DateAttributeEditor extends AbstractAttributeEditor {

	public DateAttributeEditor(AbstractRepositoryTaskEditor editor, RepositoryTaskAttribute taskAttribute) {
		super(editor, taskAttribute);
	}

	@Override
	public void createControl(Composite composite, FormToolkit toolkit) {
		Composite dateWithClear = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		dateWithClear.setLayout(layout);

		String value = "";
		Date date = getValue();
		if (date != null) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
			value = f.format(date);
		}

		final DatePicker deadlinePicker = new DatePicker(dateWithClear, /* SWT.NONE */SWT.BORDER, value);
		deadlinePicker.setEnabled(!getTaskAttribute().isReadOnly());
		deadlinePicker.setFont(TEXT_FONT);
		deadlinePicker.setDatePattern("yyyy-MM-dd");
		decorate(deadlinePicker);
		deadlinePicker.addPickerSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				Calendar cal = deadlinePicker.getDate();
				if (cal != null) {
					setValue(cal.getTime());
					// TODO goes dirty even if user presses cancel
					// markDirty(true);
				} else {
					setValue(null);
					deadlinePicker.setDate(null);
				}
			}
		});

		ImageHyperlink clearDeadlineDate = toolkit.createImageHyperlink(dateWithClear, SWT.NONE);
		clearDeadlineDate.setImage(TasksUiImages.getImage(TasksUiImages.REMOVE));
		clearDeadlineDate.setToolTipText("Clear");
		clearDeadlineDate.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				setValue(null);
				deadlinePicker.setDate(null);
			}

		});

		setControl(dateWithClear);
	}

	public Date getValue() {
		return getAttributeMapper().getDateValue(getTaskAttribute());
	}

	public void setValue(Date date) {
		getAttributeMapper().setDateValue(getTaskAttribute(), date);
		attributeChanged();
	}
}
