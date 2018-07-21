/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * @author Steffen Pingel
 */
public class TaskEditorNewCommentPart extends TaskEditorRichTextPart {

	public TaskEditorNewCommentPart() {
		setPartName(Messages.TaskEditorNewCommentPart_New_Comment);
		setSectionStyle(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setExpandVertically(true);
	}

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		setAttribute(getModel().getTaskData().getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW));
	}

}
