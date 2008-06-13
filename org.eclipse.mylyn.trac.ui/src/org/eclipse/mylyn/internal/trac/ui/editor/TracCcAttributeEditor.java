/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.internal.trac.core.TracAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Rob Elves
 */
public class TracCcAttributeEditor extends AbstractAttributeEditor {

	private List list;

	private TaskAttribute attrRemoveCc;

	public TracCcAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.MULTIPLE, ColumnSpan.SINGLE));
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		list = new List(parent, SWT.FLAT | SWT.MULTI | SWT.V_SCROLL);
		toolkit.adapt(list, true, true);
		list.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		list.setFont(JFaceResources.getDefaultFont());
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(list);

		TaskAttribute attrUserCC = getTaskAttribute();
		if (attrUserCC != null) {
			for (String value : attrUserCC.getValues()) {
				list.add(value);
			}
		}

		attrRemoveCc = getModel().getTaskData().getRoot().getMappedAttribute(TracAttributeMapper.REMOVE_CC);
		for (String item : attrRemoveCc.getValues()) {
			int i = list.indexOf(item);
			if (i != -1) {
				list.select(i);
			}
		}

		list.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (String cc : list.getItems()) {
					int index = list.indexOf(cc);
					if (list.isSelected(index)) {
						java.util.List<String> remove = attrRemoveCc.getValues();
						if (!remove.contains(cc)) {
							attrRemoveCc.addValue(cc);
						}
					} else {
						attrRemoveCc.removeValue(cc);
					}
				}
				getModel().attributeChanged(attrRemoveCc);
			}
		});

		list.showSelection();

		setControl(list);
	}
}
