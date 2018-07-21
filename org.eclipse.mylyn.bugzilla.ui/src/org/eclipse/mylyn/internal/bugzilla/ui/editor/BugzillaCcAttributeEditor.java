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

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
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
public class BugzillaCcAttributeEditor extends AbstractAttributeEditor {

	private List list;

	private TaskAttribute attrRemoveCc;

	protected boolean suppressRefresh;

	public BugzillaCcAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
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
		list.setToolTipText(getDescription());

		populateFromAttribute();

		attrRemoveCc = getModel().getTaskData().getRoot().getMappedAttribute(BugzillaAttribute.REMOVECC.getKey());

		if (attrRemoveCc == null) {
			attrRemoveCc = BugzillaTaskDataHandler.createAttribute(getModel().getTaskData(), BugzillaAttribute.REMOVECC);
		}

		selectValuesToRemove();

		list.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					suppressRefresh = true;
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
				} finally {
					suppressRefresh = false;
				}
			}
		});

		list.showSelection();

		setControl(list);
	}

	private void populateFromAttribute() {
		TaskAttribute attrUserCC = getTaskAttribute();
		if (attrUserCC != null) {
			for (String value : attrUserCC.getValues()) {
				list.add(value);
			}
		}
	}

	private void selectValuesToRemove() {
		for (String item : attrRemoveCc.getValues()) {
			int i = list.indexOf(item);
			if (i != -1) {
				list.select(i);
			}
		}
	}

	@Override
	public void refresh() {
		if (list != null && !list.isDisposed()) {
			list.removeAll();
			populateFromAttribute();
			selectValuesToRemove();
		}
	}

	@Override
	public boolean shouldAutoRefresh() {
		return !suppressRefresh;
	}
}
