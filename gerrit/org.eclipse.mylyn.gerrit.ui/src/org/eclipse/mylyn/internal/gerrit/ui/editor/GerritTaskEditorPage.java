/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui.editor;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * @author Mikael Kober
 * @author Thomas Westling
 */
public class GerritTaskEditorPage extends AbstractTaskEditorPage {

	public GerritTaskEditorPage(TaskEditor editor) {
		super(editor, GerritConnector.CONNECTOR_KIND);
		setNeedsPrivateSection(true);
		setNeedsSubmitButton(false);
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();
		for (Iterator<TaskEditorPartDescriptor> it = descriptors.iterator(); it.hasNext();) {
			TaskEditorPartDescriptor descriptor = it.next();
			if (PATH_ACTIONS.equals(descriptor.getPath())) {
				it.remove();
			}
		}
		descriptors.add(new TaskEditorPartDescriptor("review") { //$NON-NLS-1$
			@Override
			public AbstractTaskEditorPart createPart() {
				return new ReviewSection();
			}
		});
		return descriptors;
	}

}
