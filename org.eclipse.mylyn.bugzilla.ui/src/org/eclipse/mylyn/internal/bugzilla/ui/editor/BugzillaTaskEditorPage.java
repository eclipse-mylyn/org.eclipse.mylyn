/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;

/**
 * @author Rob Elves
 */
public class BugzillaTaskEditorPage extends AbstractTaskEditorPage {

	public BugzillaTaskEditorPage(TaskEditor editor) {
		super(editor, BugzillaCorePlugin.CONNECTOR_KIND);
	}

//	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
//		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();
//		
//		descriptors.add(new TaskEditorPartDescriptor(ID_PART_ACTIONS) {
//			@Override
//			public AbstractTaskEditorPart createPart() {
//				return new TaskEditorActionPart();
//			}
//		}.setPath(PATH_ACTIONS));
//		
//		
//		return descriptors;
//	}

}
