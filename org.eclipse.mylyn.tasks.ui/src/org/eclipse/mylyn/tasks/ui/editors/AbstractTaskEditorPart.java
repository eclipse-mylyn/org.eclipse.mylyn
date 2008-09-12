/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
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
 * @since 3.0
 */
public abstract class AbstractTaskEditorPart extends AbstractFormPart {

	// the default font of some controls, e.g. radio buttons, is too big; set this font explicitly on the control 
	protected static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	private Control control;

	private String partName;

	private String partId;

	private AbstractTaskEditorPage taskEditorPage;

	private boolean expandVertically;

	public AbstractTaskEditorPart() {
	}

	protected AbstractAttributeEditor createAttributeEditor(TaskAttribute attribute) {
		if (attribute == null) {
			return null;
		}

		String type = attribute.getMetaData().getType();
		if (type != null) {
			AttributeEditorFactory attributeEditorFactory = getTaskEditorPage().getAttributeEditorFactory();
			AbstractAttributeEditor editor = attributeEditorFactory.createEditor(type, attribute);
			if (editor instanceof RichTextAttributeEditor) {
				boolean spellChecking = getTaskEditorPage().getAttributeEditorToolkit().hasSpellChecking(attribute);
				((RichTextAttributeEditor) editor).setSpellCheckingEnabled(spellChecking);
			}
			return editor;
		}
		return null;
	}

	public abstract void createControl(Composite parent, FormToolkit toolkit);

	protected Section createSection(Composite parent, FormToolkit toolkit, int style) {
		Section section = toolkit.createSection(parent, style);
		section.setText(getPartName());
		return section;
	}

	protected Section createSection(Composite parent, FormToolkit toolkit, boolean expandedState) {
		int style = ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE;
		if (expandedState) {
			style |= ExpandableComposite.EXPANDED;
		}
		return createSection(parent, toolkit, style);
	}

	protected void fillToolBar(ToolBarManager toolBarManager) {
	}

	public Control getControl() {
		return control;
	}

	public TaskDataModel getModel() {
		return getTaskEditorPage().getModel();
	}

	public String getPartId() {
		return partId;
	}

	public String getPartName() {
		return partName;
	}

	public TaskData getTaskData() {
		return getTaskEditorPage().getModel().getTaskData();
	}

	public AbstractTaskEditorPage getTaskEditorPage() {
		return taskEditorPage;
	}

	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		this.taskEditorPage = taskEditorPage;
	}

	public void setControl(Control control) {
		this.control = control;
	}

	void setPartId(String partId) {
		this.partId = partId;
	}

	protected void setPartName(String partName) {
		this.partName = partName;
	}

	protected void setSection(FormToolkit toolkit, Section section) {
		if (section.getTextClient() == null) {
			ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
			fillToolBar(toolBarManager);

			// TODO toolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

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

	protected boolean setSelection(ISelection selection) {
		return false;
	}

	public boolean getExpandVertically() {
		return expandVertically;
	}

	public void setExpandVertically(boolean expandVertically) {
		this.expandVertically = expandVertically;
	}

}
