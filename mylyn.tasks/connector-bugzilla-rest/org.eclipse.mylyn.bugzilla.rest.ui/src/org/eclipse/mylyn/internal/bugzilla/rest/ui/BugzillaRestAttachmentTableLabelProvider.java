/*******************************************************************************
 * Copyright (c) 2017 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.bugzilla.rest.ui;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.mylyn.internal.bugzilla.rest.core.IBugzillaRestConstants;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentTableLabelProvider;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class BugzillaRestAttachmentTableLabelProvider extends AttachmentTableLabelProvider {

	@Override
	public StyledString buildTextFromEventIndex(int index, ITaskAttachment attachment) {
		if (index == 6) {
			return getAttachmentFlags(attachment);
		} else {
			return super.buildTextFromEventIndex(index, attachment);
		}
	}

	public static StyledString getAttachmentFlags(ITaskAttachment attachment) {
		StyledString text = new StyledString();

		for (TaskAttribute taskAttribute : attachment.getTaskAttribute().getAttributes().values()) {
			if (taskAttribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG)) {
				TaskAttribute stateAttribute = taskAttribute.getAttribute("state");
				if (text.length() > 0) {
					text.append("\n");
				}
				text.append(stateAttribute.getMetaData().getLabel());
				text.append(": ");
				text.append(stateAttribute.getValue());
				TaskAttribute requesteeAttribute = taskAttribute.getAttribute("requestee");
				if (!requesteeAttribute.getValue().isEmpty()) {
					text.append(" ");
					text.append(requesteeAttribute.getValue(), StyledString.COUNTER_STYLER);
				}

			}
		}
		return text;
	}

}
