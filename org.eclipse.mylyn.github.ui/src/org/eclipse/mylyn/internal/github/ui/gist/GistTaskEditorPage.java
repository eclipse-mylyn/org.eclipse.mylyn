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
package org.eclipse.mylyn.internal.github.ui.gist;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.mylyn.github.ui.internal.IssueSummaryPart;
import org.eclipse.mylyn.internal.github.core.gist.GistAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * Gist task editor page class
 */
public class GistTaskEditorPage extends AbstractTaskEditorPage {

	/**
	 * @param editor
	 * @param connectorKind
	 */
	public GistTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, connectorKind);
		setNeedsPrivateSection(true);
		setNeedsSubmitButton(true);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage#createPartDescriptors()
	 */
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> partDescriptors = super
				.createPartDescriptors();
		Iterator<TaskEditorPartDescriptor> descriptorIt = partDescriptors
				.iterator();
		while (descriptorIt.hasNext()) {
			TaskEditorPartDescriptor partDescriptor = descriptorIt.next();
			String id = partDescriptor.getId();
			if (id.equals(ID_PART_ATTRIBUTES) || id.equals(ID_PART_SUMMARY)
					|| id.equals(ID_PART_ATTACHMENTS))
				descriptorIt.remove();
		}
		if (!getModel().getTaskData().isNew()) {
			partDescriptors.add(new TaskEditorPartDescriptor(ID_PART_SUMMARY) {

				public AbstractTaskEditorPart createPart() {
					return new IssueSummaryPart(GistAttribute.AUTHOR_GRAVATAR
							.getId(), null);
				}
			}.setPath(PATH_HEADER));
			partDescriptors.add(new TaskEditorPartDescriptor(
					ID_PART_ATTACHMENTS) {

				public AbstractTaskEditorPart createPart() {
					return new GistAttachmentPart();
				}
			}.setPath(PATH_ATTACHMENTS));
		}

		return partDescriptors;
	}
}
