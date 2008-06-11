/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * @author Steffen Pingel
 */
public class TaskEditorNewCommentPart extends TaskEditorRichTextPart {

	public TaskEditorNewCommentPart() {
		setPartName("New Comment");
		setSectionStyle(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setExpandVertically(true);
	}

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		setAttribute(getModel().getTaskData().getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW));
	}

	@Override
	protected void fillToolBar(ToolBarManager barManager) {

		Action collapseAllAction = new Action("") {
			@Override
			public void run() {
				setChecked(!isChecked());
			}
		};
		collapseAllAction.setImageDescriptor(CommonImages.PART_MAXIMIZE);
		collapseAllAction.setToolTipText("Maximize");
		barManager.add(collapseAllAction);
	}

}
