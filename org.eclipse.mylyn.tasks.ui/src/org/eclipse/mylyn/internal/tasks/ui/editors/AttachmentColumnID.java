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
import org.eclipse.mylyn.internal.tasks.ui.views.TaskKeyComparator;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.swt.SWT;

public class AttachmentColumnID extends AttachmentColumnDefinition {
	TaskKeyComparator keyComparator = new TaskKeyComparator();

	public AttachmentColumnID(int index) {
		super(index, 0, "ID", SWT.LEFT, false, SWT.NONE);
	}

	@Override
	public String getColumnText(ITaskAttachment attachment, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		return getAttachmentId(attachment);
	}

	@Override
	public int compare(TableViewer viewer, ITaskAttachment attachment1, ITaskAttachment attachment2, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		String key1 = getAttachmentId(attachment1);
		String key2 = getAttachmentId(attachment2);
		return keyComparator.compare2(key1, key2);
	}

	static String getAttachmentId(ITaskAttachment attachment) {
		TaskAttribute attribute = attachment.getTaskAttribute();
		if (attribute != null) {
			return attribute.getValue();
		}
		return ""; //$NON-NLS-1$
	}

}
