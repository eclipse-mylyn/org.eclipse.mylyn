/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.mylyn.github.internal.GitHubTaskAttributes;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttributePart;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;

/**
 * GitHub issue task editor attribute part that display labels and milestone
 * attribute editors.
 */
public class IssueAttributePart extends TaskEditorAttributePart {

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart#createAttributeEditor(org.eclipse.mylyn.tasks.core.data.TaskAttribute)
	 */
	protected AbstractAttributeEditor createAttributeEditor(
			TaskAttribute attribute) {
		if (GitHubTaskAttributes.LABELS.getId().equals(attribute.getId())) {
			return new IssueLabelAttributeEditor(getModel(), attribute);
		} else if (GitHubTaskAttributes.MILESTONE.getId().equals(
				attribute.getId())) {
			return super.createAttributeEditor(attribute);
		}
		return null;
	}

}