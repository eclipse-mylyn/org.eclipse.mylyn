/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Extend to add tabs to task editors.
 * 
 * @author Mik Kersten
 * @since 2.0
 * @deprecated use {@link AbstractTaskEditorPageFactory} instead
 */
@Deprecated
public abstract class AbstractTaskEditorFactory {

	public abstract IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput);

	/**
	 * @since 3.0
	 */
	public abstract IEditorInput createEditorInput(ITask task);

	public abstract String getTitle();

	/**
	 * @since 3.0
	 */
	public abstract boolean canCreateEditorFor(ITask task);

	public abstract boolean canCreateEditorFor(IEditorInput input);

	public boolean providesOutline() {
		return false;
	}

	/**
	 * @return A higher integer for high priority, low integer for low priority. Higher priority editors will be placed
	 * 	earlier in the tab list.
	 */
	public int getTabOrderPriority() {
		return 1;
	}

}
