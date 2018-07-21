/*******************************************************************************
 * Copyright (c) 2017 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.rest.ui;

import java.util.Arrays;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.ui.TableColumnDescriptor;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentTableLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttachmentPart;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.swt.SWT;

public class BugzillaRestTaskEditorAttachmentPart extends TaskEditorAttachmentPart {

	@Override
	protected TableColumnDescriptor[] createColumnDescriptors() {
		TableColumnDescriptor[] defined = super.createColumnDescriptors();

		TableColumnDescriptor[] result = Arrays.copyOf(defined, defined.length + 1);
		result[defined.length] = new TableColumnDescriptor(100, "Flags", SWT.LEFT, false, SWT.DOWN, true);
		return result;
	}

	@Override
	protected AttachmentTableLabelProvider createTableProvider() {
		return new BugzillaRestAttachmentTableLabelProvider();
	}

	@Override
	protected int compareColumn(ITaskAttachment attachment1, ITaskAttachment attachment2, int propertyIndex) {
		if (propertyIndex == 6) {
			StyledString flags1 = BugzillaRestAttachmentTableLabelProvider.getAttachmentFlags(attachment1);
			StyledString flags2 = BugzillaRestAttachmentTableLabelProvider.getAttachmentFlags(attachment2);
			String flags1String = flags1 != null ? flags1.getString() : null;
			String flags2String = flags1 != null ? flags2.getString() : null;
			return CoreUtil.compare(flags1String, flags2String);
		} else {
			return super.compareColumn(attachment1, attachment2, propertyIndex);
		}
	}

}
