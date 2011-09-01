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

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentColumnDefinition;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.swt.SWT;

public class AttachmentColumnFlags extends AttachmentColumnDefinition {
	public AttachmentColumnFlags(int index) {
		super(index, 100, "Flag", SWT.LEFT, false, SWT.NONE);
	}

	@Override
	public String getColumnText(ITaskAttachment attachment, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		return getAttachmentFlags(attachment);
	}

	@Override
	public int compare(TableViewer viewer, ITaskAttachment attachment1, ITaskAttachment attachment2, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		String key1 = getAttachmentFlags(attachment1);
		String key2 = getAttachmentFlags(attachment2);
		return compare(key1, key2);
	}

	static String getAttachmentFlags(ITaskAttachment attachment) {
		TaskAttribute attribute = attachment.getTaskAttribute();
		String result = ""; //$NON-NLS-1$
		for (TaskAttribute attachmentAttribute : attribute.getAttributes().values()) {
			String atribID = attachmentAttribute.getId();
			if (!atribID.startsWith(BugzillaAttribute.KIND_FLAG)) {
				continue;
			}
			TaskAttribute state = attachmentAttribute.getAttribute("state"); //$NON-NLS-1$
			if (state != null) {
				if (" ".equals(state.getValue())) { //$NON-NLS-1$
					continue;
				}
				if (!"".equals(result)) { //$NON-NLS-1$
					result += ", "; //$NON-NLS-1$
				}
				result += state.getMetaData().getLabel() + state.getValue();
			}
		}

		return result;
	}

}
