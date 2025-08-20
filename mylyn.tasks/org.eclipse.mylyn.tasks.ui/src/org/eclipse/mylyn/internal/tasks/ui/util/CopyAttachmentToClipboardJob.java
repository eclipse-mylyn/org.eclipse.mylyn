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

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.ByteArrayOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class CopyAttachmentToClipboardJob extends Job {

	private final ITaskAttachment attachment;

	public CopyAttachmentToClipboardJob(ITaskAttachment attachment) {
		super(Messages.CopyAttachmentToClipboardJob_Copying_Attachment_to_Clipboard);
		this.attachment = attachment;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			AttachmentUtil.downloadAttachment(attachment, out, monitor);
		} catch (final CoreException e) {
			TasksUiInternal.asyncDisplayStatus(Messages.CopyAttachmentToClipboardJob_Copy_Attachment_to_Clipboard,
					e.getStatus());
			return Status.OK_STATUS;
		}

		String contents = new String(out.toByteArray());
		contents = contents.replaceAll("\r\n|\n", System.lineSeparator()); //$NON-NLS-1$
		copyToClipboard(contents);

		return Status.OK_STATUS;
	}

	private void copyToClipboard(final String contents) {
		PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
			Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
			clipboard.setContents(new Object[] { contents }, new Transfer[] { TextTransfer.getInstance() });
			clipboard.dispose();
		});
	}

}
