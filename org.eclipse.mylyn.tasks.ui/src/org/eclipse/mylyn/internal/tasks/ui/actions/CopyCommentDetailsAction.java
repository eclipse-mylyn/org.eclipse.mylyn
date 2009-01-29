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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.util.ClipboardCopier;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class CopyCommentDetailsAction extends BaseSelectionListenerAction {

	private final ClipboardCopier copier;

	public CopyCommentDetailsAction() {
		super(Messages.CopyCommentDetailsAction_Copy_User_ID);
		setToolTipText(Messages.CopyCommentDetailsAction_Copy_User_ID_Tooltip);
		setImageDescriptor(CommonImages.COPY);
		copier = new ClipboardCopier() {
			@Override
			protected String getTextForElement(Object element) {
				if (element instanceof ITaskComment) {
					ITaskComment comment = (ITaskComment) element;
					IRepositoryPerson author = comment.getAuthor();
					if (author != null) {
						return author.getPersonId();
					}
				}
				return null;
			}
		};
	}

	@Override
	public void run() {
		copier.copy(getStructuredSelection());
	}

	public void dispose() {
		copier.dispose();

	}

}
