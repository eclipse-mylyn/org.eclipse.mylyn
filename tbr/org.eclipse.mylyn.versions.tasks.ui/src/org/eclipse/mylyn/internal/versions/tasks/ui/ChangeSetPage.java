/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.versions.tasks.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * 
 * @author Kilian Matt
 *
 */
public class ChangeSetPage extends AbstractTaskEditorPage {

	public ChangeSetPage(TaskEditor editor) {
		super(editor, ((TaskEditorInput) editor.getEditorInput())
				.getTaskRepository().getConnectorKind());
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = new HashSet<TaskEditorPartDescriptor>();
		descriptors.add(new TaskEditorPartDescriptor("") {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new ChangesetPart();
			}
		});
		return descriptors;
	}
	@Override
	public void fillToolBar(IToolBarManager toolBarManager) {
		
	}
}
