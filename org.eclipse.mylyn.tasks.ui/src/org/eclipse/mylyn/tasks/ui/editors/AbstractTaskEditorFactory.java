/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Extend to add tabs to task editors.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractTaskEditorFactory {

	public abstract IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput);

	public abstract IEditorInput createEditorInput(AbstractTask task);

	public abstract String getTitle();

	public abstract boolean canCreateEditorFor(AbstractTask task);

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
