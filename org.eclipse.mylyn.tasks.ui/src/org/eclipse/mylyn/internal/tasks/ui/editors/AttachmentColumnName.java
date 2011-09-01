/*******************************************************************************
 * Copyright (c) 2011 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImageManger;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public class AttachmentColumnName extends AttachmentColumnDefinition {
	private final CommonImageManger imageManager = new CommonImageManger();

	public AttachmentColumnName(int index) {
		super(index, 130, "Name", SWT.LEFT, false, SWT.NONE);
	}

	@Override
	public Image getColumnImage(ITaskAttachment attachment, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		if (AttachmentUtil.isContext(attachment)) {
			return imageManager.getImage(TasksUiImages.CONTEXT_TRANSFER);
		} else if (attachment.isPatch()) {
			return imageManager.getImage(TasksUiImages.TASK_ATTACHMENT_PATCH);
		} else {
			return imageManager.getFileImage(attachment.getFileName());
		}
	}

	@Override
	public String getColumnText(ITaskAttachment attachment, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		if (AttachmentUtil.isContext(attachment)) {
			return "Task Context";
		} else if (attachment.isPatch()) {
			return "Patch";
		} else {
			return " " + attachment.getFileName(); //$NON-NLS-1$
		}
	}

	@Override
	public int compare(TableViewer viewer, ITaskAttachment attachment1, ITaskAttachment attachment2, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		return compare(attachment1.getFileName(), attachment2.getFileName());
	}
}
