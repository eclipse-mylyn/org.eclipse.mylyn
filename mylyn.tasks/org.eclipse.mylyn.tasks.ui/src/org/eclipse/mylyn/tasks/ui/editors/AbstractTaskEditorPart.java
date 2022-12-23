/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.ui.editors;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.Messages;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
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

	private MaximizePartAction maximizePartAction;

	private final Set<String> attributeIds = new HashSet<>();

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
			attributeIds.add(attribute.getId());
			return editor;
		}
		return null;
	}

	public abstract void createControl(Composite parent, FormToolkit toolkit);

	protected Section createSection(Composite parent, FormToolkit toolkit, int style) {
		Section section = toolkit.createSection(parent, style);
		section.setText(LegacyActionTools.escapeMnemonics(getPartName()));
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
		getModel().addModelListener(new TaskDataModelListener() {

			@Override
			public void attributeChanged(TaskDataModelEvent event) {
				if (attributeIds.contains(event.getTaskAttribute().getId())) {
					markDirty();
				}
			}
		});
	}

	@Override
	public void markDirty() {
		if (!isDirty()) {
			super.markDirty();
		}
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
				rowLayout.marginLeft = 0;
				rowLayout.marginRight = 0;
				rowLayout.marginTop = 0;
				rowLayout.marginBottom = 0;
				rowLayout.center = true;
				toolbarComposite.setLayout(rowLayout);

				toolBarManager.createControl(toolbarComposite);
				section.clientVerticalSpacing = 0;
				section.descriptionVerticalSpacing = 0;
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

	/**
	 * Returns an action for maximizing the part.
	 *
	 * @since 3.5
	 */
	protected Action getMaximizePartAction() {
		if (maximizePartAction == null) {
			maximizePartAction = new MaximizePartAction();
		}
		return maximizePartAction;
	}

	/**
	 * Returns the control that determines the size of the part.
	 *
	 * @see #getMaximizePartAction()
	 * @since 3.5
	 */
	protected Control getLayoutControl() {
		return getControl();
	}

	private class MaximizePartAction extends Action {

		private static final String COMMAND_ID = "org.eclipse.mylyn.tasks.ui.command.maximizePart"; //$NON-NLS-1$

		private static final int SECTION_HEADER_HEIGHT = 50;

		private int originalHeight = -2;

		public MaximizePartAction() {
			super(Messages.TaskEditorRichTextPart_Maximize, SWT.TOGGLE);
			setImageDescriptor(CommonImages.PART_MAXIMIZE);
			setToolTipText(Messages.TaskEditorRichTextPart_Maximize);
			setActionDefinitionId(COMMAND_ID);
			setChecked(false);
		}

		@Override
		public void run() {
			if (getControl() instanceof Section && !((Section) getControl()).isExpanded()) {
				CommonFormUtil.setExpanded((Section) getControl(), true);
			}
			Control control = getLayoutControl();
			if (control == null || !(control.getLayoutData() instanceof GridData)) {
				return;
			}
			GridData gd = (GridData) control.getLayoutData();

			// initialize originalHeight on first invocation
			if (originalHeight == -2) {
				originalHeight = gd.heightHint;
			}

			int heightHint;
			if (isChecked()) {
				heightHint = getManagedForm().getForm().getClientArea().height - SECTION_HEADER_HEIGHT;
			} else {
				heightHint = originalHeight;
			}

			// ignore when not necessary
			if (gd.heightHint == heightHint) {
				return;
			}
			gd.heightHint = heightHint;
			gd.minimumHeight = heightHint;
			if (gd.widthHint == -1) {
				gd.widthHint = 300;// needs to be set or else heightHint is ignored
			}

			getTaskEditorPage().reflow();
			CommonFormUtil.ensureVisible(control);
		}
	}

}
