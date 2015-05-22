/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui.editor;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Leo Dos Santos
 */
public class MockWikiEditorExtension extends AbstractTaskEditorExtension {

	@Deprecated
	@Override
	public SourceViewer createViewer(TaskRepository taskRepository, Composite parent, int style) {
		return null;
	}

	@Deprecated
	@Override
	public SourceViewer createEditor(TaskRepository taskRepository, Composite parent, int style) {
		// ignore
		return null;
	}

	@Override
	public String getEditorContextId() {
		// ignore
		return null;
	}

}
