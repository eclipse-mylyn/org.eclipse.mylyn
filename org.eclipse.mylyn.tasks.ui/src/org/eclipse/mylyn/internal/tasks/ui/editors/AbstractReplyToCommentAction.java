/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.tasks.core.CommentQuoter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractReplyToCommentAction extends Action {

	private static final String LABEL_REPLY = "Reply";

	private final AbstractTaskEditorPage editor;

	private final ITaskComment taskComment;

	public AbstractReplyToCommentAction(AbstractTaskEditorPage editor, ITaskComment taskComment) {
		this.editor = editor;
		this.taskComment = taskComment;
		setImageDescriptor(TasksUiImages.COMMENT_REPLY);
		setToolTipText(LABEL_REPLY);
	}

	protected abstract String getReplyText();

	@Override
	public void run() {
		reply(editor, taskComment, getReplyText());
	}

	public static void reply(AbstractTaskEditorPage editor, ITaskComment taskComment, String text) {
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(editor.getConnectorKind());
		String reference = connectorUi.getReplyText(editor.getTaskRepository(), editor.getTask(), taskComment, false);
		StringBuilder sb = new StringBuilder();
		sb.append(reference);
		sb.append("\n");
		if (text != null) {
			CommentQuoter quoter = new CommentQuoter();
			sb.append(quoter.quote(text));
		}
		editor.appendTextToNewComment(sb.toString());
	}
}
