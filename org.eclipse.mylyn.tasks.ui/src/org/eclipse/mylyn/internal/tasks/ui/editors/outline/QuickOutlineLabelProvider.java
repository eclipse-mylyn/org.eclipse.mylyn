/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
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

package org.eclipse.mylyn.internal.tasks.ui.editors.outline;

import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorOutlineNode;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorOutlineNodeLabelProvider;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.swt.graphics.Image;

/**
 * @author Frank Becker
 */
public class QuickOutlineLabelProvider extends TaskEditorOutlineNodeLabelProvider {

	@Override
	public String getText(Object element) {
		String result = ""; //$NON-NLS-1$
		if (element instanceof TaskData) {
			TaskData node = (TaskData) element;
			result = node.getTaskId();
		} else if (element instanceof TaskAttribute) {
			TaskAttribute node = (TaskAttribute) element;
			TaskAttributeMetaData meta = node.getMetaData();
			if (meta != null) {
				String lable = meta.getLabel();
				if (lable != null) {
					result = lable + " (" + node.getId() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					if (TaskAttribute.TYPE_ATTACHMENT.equals(meta.getType())) {
						result = "Attachment: " + node.getValue(); //$NON-NLS-1$
					} else if (TaskAttribute.TYPE_COMMENT.equals(meta.getType())) {
						result = "Comment: " + node.getValue(); //$NON-NLS-1$
					} else {
						result = "<" + node.getId() + ">"; //$NON-NLS-1$//$NON-NLS-2$
					}
				}
			}
		} else if (element instanceof TaskEditorOutlineNode) {
			result = super.getText(element);
		} else if (element instanceof String) {
			result = (String) element;
		}
		return result;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof TaskEditorOutlineNode) {
			return super.getImage(element);
		} else {
			return null;
		}
	}

}
