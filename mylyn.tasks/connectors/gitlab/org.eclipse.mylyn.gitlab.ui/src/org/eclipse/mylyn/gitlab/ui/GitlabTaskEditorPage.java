/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
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

package org.eclipse.mylyn.gitlab.ui;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

public class GitlabTaskEditorPage extends AbstractTaskEditorPage {

	public GitlabTaskEditorPage(TaskEditor editor) {
		this(editor, GitlabCoreActivator.CONNECTOR_KIND);
	}
	
	
	public GitlabTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, connectorKind);
		setNeedsPrivateSection(false);
		setNeedsSubmitButton(true);
	}

	@Override
	protected AttributeEditorFactory createAttributeEditorFactory() {
		// TODO Auto-generated method stub
		return super.createAttributeEditorFactory();
	}
	
	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		// TODO Auto-generated method stub
		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();
		// remove unnecessary default editor parts
		ArrayList<TaskEditorPartDescriptor> descriptorsToRemove = new ArrayList<TaskEditorPartDescriptor>(2);
		boolean hasCommentPart = false;
		for (TaskEditorPartDescriptor taskEditorPartDescriptor : descriptors) {
			if (taskEditorPartDescriptor.getId().equals(ID_PART_COMMENTS)) {
				hasCommentPart = hasCommentPart || taskEditorPartDescriptor.getId().equals(ID_PART_COMMENTS);
				descriptorsToRemove.add(taskEditorPartDescriptor);
				continue;
			}
		}
		descriptors.removeAll(descriptorsToRemove);
		if (hasCommentPart) {
			descriptors.add(new TaskEditorPartDescriptor(ID_PART_COMMENTS) {
				@Override
				public AbstractTaskEditorPart createPart() {
					return new GitlabTaskEditorCommentPart();
				}
			}.setPath(PATH_COMMENTS));
		}
		return descriptors;
	}
}
