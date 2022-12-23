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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.mylyn.commons.ui.ClipboardCopier;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class CopyCommentDetailsAction extends BaseSelectionListenerAction {

	public CopyCommentDetailsAction() {
		super(Messages.CopyCommentDetailsAction_Copy_User_ID);
		setToolTipText(Messages.CopyCommentDetailsAction_Copy_User_ID_Tooltip);
		setImageDescriptor(CommonImages.COPY);
	}

	@Override
	public void run() {
		ClipboardCopier.getDefault().copy(getStructuredSelection(), new ClipboardCopier.TextProvider() {
			public String getTextForElement(Object element) {
				if (element instanceof ITaskComment) {
					ITaskComment comment = (ITaskComment) element;
					IRepositoryPerson author = comment.getAuthor();
					if (author != null) {
						return author.getPersonId();
					}
				}
				return null;
			}
		});
	}

}
