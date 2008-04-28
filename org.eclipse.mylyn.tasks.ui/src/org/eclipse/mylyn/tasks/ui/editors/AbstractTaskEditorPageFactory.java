/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @since 3.0
 * @author Steffen Pingel
 */
public abstract class AbstractTaskEditorPageFactory {

	public static final int PRIORITY_ADDITIONS = 100;

	public static final int PRIORITY_CONTEXT = 20;

	public static final int PRIORITY_PLANNING = 10;

	public static final int PRIORITY_TASK = 30;

	private String id;

	public abstract boolean canCreatePageFor(TaskEditorInput input);

	public abstract FormPage createPage(TaskEditor parentEditor);

	public String[] getConflictingIds(TaskEditorInput input) {
		return null;
	}

	public String getId() {
		return id;
	}

	// TODO EDITOR life cycle of image?
	public abstract Image getPageImage();

	public abstract String getPageText();

	public int getPriority() {
		return PRIORITY_ADDITIONS;
	}

	public void setId(String id) {
		this.id = id;
	}

}
