/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.mylyn.internal.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.internal.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class LongTextAttributeEditor extends AbstractAttributeEditor {

	public LongTextAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.MULTIPLE, ColumnSpan.MULTIPLE));
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		TextViewer viewer = new TextViewer(parent, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		viewer.setDocument(new Document(getValue()));

		final StyledText text = viewer.getTextWidget();
		toolkit.adapt(text, true, true);

		if (isReadOnly()) {
			viewer.setEditable(false);
		} else {
			viewer.setEditable(true);
			text.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setValue(text.getText());
				}
			});
		}

		setControl(text);
	}

	public String getValue() {
		return getAttributeMapper().getValue(getTaskAttribute());
	}

	public void setValue(String text) {
		getAttributeMapper().setValue(getTaskAttribute(), text);
		attributeChanged();
	}

}
