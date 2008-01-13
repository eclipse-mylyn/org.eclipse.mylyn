/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class TextAttributeEditor extends AbstractAttributeEditor {

	public TextAttributeEditor(AbstractAttributeEditorManager manager, RepositoryTaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		final Text text;
		if (getTaskAttribute().isReadOnly()) {
			text = new Text(parent, SWT.FLAT | SWT.READ_ONLY);
			toolkit.adapt(text, true, true);
			text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			text.setText(getValue());
		} else {
			text = toolkit.createText(parent, getValue(), SWT.FLAT);

			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setValue(text.getText());
				}
			});
		}

		decorate(text);
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
