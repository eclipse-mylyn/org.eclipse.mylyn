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
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;

public abstract class AbstractReplyToCommentAction extends Action {

	private static final String LABEL_REPLY = "Reply";

	private final int commentNum;

	private final AbstractTaskEditorPage editor;

	public AbstractReplyToCommentAction(AbstractTaskEditorPage editor, int commentNum) {
		this.editor = editor;
		this.commentNum = commentNum;
		setImageDescriptor(TasksUiImages.REPLY);
		setToolTipText(LABEL_REPLY);
	}

	protected abstract String getReplyText();

	@Override
	public void run() {
		StringBuilder sb = new StringBuilder();
		sb.append(" (In reply to comment #" + commentNum + ")\n");
		String text = getText();
		if (text != null) {
			CommentQuoter quoter = new CommentQuoter();
			sb.append(quoter.quote(text));
		}
		editor.appendTextToNewComment(sb.toString());
	}

}
