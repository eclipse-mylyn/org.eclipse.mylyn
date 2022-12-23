/*******************************************************************************
 * Copyright (c) 2010, 2011 Frank Becker and others. 
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.mylyn.commons.ui.ClipboardCopier;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Frank Becker
 */
public class CopyCommenterNameAction extends BaseSelectionListenerAction {

	public CopyCommenterNameAction() {
		super(Messages.CopyCommenterNameAction_Copy_User_Name);
		setToolTipText(Messages.CopyCommenterNameAction_Copy_User_Name_Tooltip);
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
						return author.getName();
					}
				}
				return null;
			}
		});
	}

}
