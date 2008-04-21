/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.tasks.core.data.AttributeManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
// TODO EDITOR add API for section toolbars
public abstract class AbstractTaskEditorPart extends AbstractFormPart {

	// XXX why is this required?
	protected static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	private TaskData taskData;

	private Control control;

	private AbstractTaskEditorPage taskEditorPage;

	private String partName;

	public AbstractTaskEditorPart() {
	}

	protected Section createSection(Composite parent, FormToolkit toolkit, boolean expandedState) {
		int style = ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE;
		if (expandedState) {
			style |= ExpandableComposite.EXPANDED;
		}
		Section section = toolkit.createSection(parent, style);
		section.setText(getPartName());
		return section;
	}

	public abstract void createControl(Composite parent, FormToolkit toolkit);

	public Control getControl() {
		return control;
	}

	public AttributeManager getAttributeManager() {
		return getTaskEditorPage().getAttributeManager();
	}

	public TaskData getTaskData() {
		return getTaskEditorPage().getAttributeManager().getTaskData();
	}

	public AbstractTaskEditorPage getTaskEditorPage() {
		return taskEditorPage;
	}

	public void setControl(Control control) {
		this.control = control;
	}

	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		this.taskEditorPage = taskEditorPage;
	}

	protected void fillToolBar(ToolBarManager toolBarManager) {
	}

	public String getPartName() {
		return partName;
	}

	protected void setPartName(String partName) {
		this.partName = partName;
	}

	protected void setSection(FormToolkit toolkit, Section section) {
		if (section.getTextClient() == null) {
			ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
			fillToolBar(toolBarManager);

			// TODO EDITOR toolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

			if (toolBarManager.getSize() > 0) {
				Composite toolbarComposite = toolkit.createComposite(section);
				toolbarComposite.setBackground(null);
				RowLayout rowLayout = new RowLayout();
				rowLayout.marginTop = 0;
				rowLayout.marginBottom = 0;
				toolbarComposite.setLayout(rowLayout);

				toolBarManager.createControl(toolbarComposite);
				section.setTextClient(toolbarComposite);
			}
		}
		setControl(section);
	}

}
