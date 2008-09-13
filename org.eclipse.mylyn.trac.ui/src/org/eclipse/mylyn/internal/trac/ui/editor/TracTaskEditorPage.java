/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.editor;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.mylyn.internal.trac.core.TracAttribute;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * @author Steffen Pingel
 */
public class TracTaskEditorPage extends AbstractTaskEditorPage {

	private TracRenderingEngine renderingEngine;

	public TracTaskEditorPage(TaskEditor editor) {
		super(editor, TracCorePlugin.CONNECTOR_KIND);
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();
		// remove unnecessary default editor parts
		for (Iterator<TaskEditorPartDescriptor> it = descriptors.iterator(); it.hasNext();) {
			TaskEditorPartDescriptor taskEditorPartDescriptor = it.next();
			if (taskEditorPartDescriptor.getId().equals(ID_PART_PEOPLE)) {
				it.remove();
			}
		}
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_PEOPLE) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new TracPeoplePart();
			}
		}.setPath(PATH_PEOPLE));
		return descriptors;
	}

	@Override
	protected void createParts() {
		if (renderingEngine == null) {
			renderingEngine = new TracRenderingEngine();
		}
		getAttributeEditorToolkit().setRenderingEngine(renderingEngine);
		super.createParts();
	}

	@Override
	protected AttributeEditorFactory createAttributeEditorFactory() {
		AttributeEditorFactory factory = new AttributeEditorFactory(getModel(), getTaskRepository()) {
			@Override
			public AbstractAttributeEditor createEditor(String type, TaskAttribute taskAttribute) {
				if (TracAttribute.CC.getTracKey().equals(taskAttribute.getId())) {
					return new TracCcAttributeEditor(getModel(), taskAttribute);
				}
				return super.createEditor(type, taskAttribute);
			}
		};
		return factory;
	}

}
