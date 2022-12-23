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

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylyn.internal.tasks.ui.actions.SaveAttachmentsAction;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TasksUiMenus {

	public static void fillTaskAttachmentMenu(IMenuManager manager) {
		final Action saveAction = new SaveAttachmentsAction(Messages.TasksUiMenus_Save_);

		final Action copyURLToClipAction = new Action(Messages.TasksUiMenus_Copy_URL) {
			@Override
			public void run() {
				ITaskAttachment attachment = AttachmentUtil.getSelectedAttachment();
				if (attachment != null) {
					Clipboard clip = new Clipboard(PlatformUI.getWorkbench().getDisplay());
					clip.setContents(new Object[] { attachment.getUrl() },
							new Transfer[] { TextTransfer.getInstance() });
					clip.dispose();
				}
			}

			@Override
			public boolean isEnabled() {
				ITaskAttachment attachment = AttachmentUtil.getSelectedAttachment();
				if (attachment != null) {
					return attachment.getUrl() != null;
				}
				return super.isEnabled();
			}
		};

		final Action copyToClipAction = new Action(Messages.TasksUiMenus_Copy_Contents) {
			@Override
			public void run() {
				ITaskAttachment attachment = AttachmentUtil.getSelectedAttachment();
				if (attachment != null) {
					CopyAttachmentToClipboardJob job = new CopyAttachmentToClipboardJob(attachment);
					job.setUser(true);
					job.schedule();
				}
			}
		};

		manager.add(new Separator("group.open")); //$NON-NLS-1$
		manager.add(new Separator("group.save")); //$NON-NLS-1$
		manager.add(saveAction);
		manager.add(copyURLToClipAction);
		manager.add(copyToClipAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
}
