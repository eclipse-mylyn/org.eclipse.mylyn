/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.swt.graphics.Color;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractAttributeEditorManager {

	private final RepositoryTaskEditorInput input;

	public AbstractAttributeEditorManager(RepositoryTaskEditorInput input) {
		this.input = input;
	}

	public abstract boolean attributeChanged(RepositoryTaskAttribute attribute);

	public abstract Color getColorIncoming();

	public boolean hasOutgoingChanges(RepositoryTaskAttribute taskAttribute) {
		return input.getOldEdits().contains(taskAttribute);
	}

	public boolean hasIncomingChanges(RepositoryTaskAttribute taskAttribute) {
		RepositoryTaskData oldTaskData = input.getOldTaskData();
		if (oldTaskData == null) {
			return false;
		}

		if (hasOutgoingChanges(taskAttribute)) {
			return false;
		}

		RepositoryTaskAttribute oldAttribute = oldTaskData.getAttribute(taskAttribute.getId());
		if (oldAttribute == null) {
			return true;
		}
		if (oldAttribute.getValue() != null && !oldAttribute.getValue().equals(taskAttribute.getValue())) {
			return true;
		} else if (oldAttribute.getValues() != null && !oldAttribute.getValues().equals(taskAttribute.getValues())) {
			return true;
		}
		return false;
	}


	public abstract void addTextViewer(SourceViewer viewer);

	public abstract TaskRepository getTaskRepository();

	public abstract void configureContextMenuManager(MenuManager menuManager);

}
