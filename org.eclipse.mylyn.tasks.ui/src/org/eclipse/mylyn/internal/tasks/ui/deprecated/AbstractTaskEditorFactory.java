/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.deprecated;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
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
	 *         earlier in the tab list.
	 */
	public int getTabOrderPriority() {
		return 1;
	}

}
