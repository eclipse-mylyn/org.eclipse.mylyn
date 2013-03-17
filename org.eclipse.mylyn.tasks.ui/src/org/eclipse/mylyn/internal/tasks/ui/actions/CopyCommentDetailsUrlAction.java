/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.mylyn.commons.ui.ClipboardCopier;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

public class CopyCommentDetailsUrlAction extends BaseSelectionListenerAction {

	public CopyCommentDetailsUrlAction() {
		super(Messages.CopyCommentDetailsURL_Copy_Comment_URL);
		setToolTipText(Messages.CopyCommentDetailsURL_Copy_Comment_URL_Tooltip);
		setImageDescriptor(CommonImages.COPY);
	}

	@Override
	public void run() {
		ClipboardCopier.getDefault().copy(getStructuredSelection(), new ClipboardCopier.TextProvider() {
			public String getTextForElement(Object element) {
				if (element instanceof ITaskComment) {
					ITaskComment comment = (ITaskComment) element;
					return comment.getUrl();
				}
				return null;
			}
		});
	}

}
