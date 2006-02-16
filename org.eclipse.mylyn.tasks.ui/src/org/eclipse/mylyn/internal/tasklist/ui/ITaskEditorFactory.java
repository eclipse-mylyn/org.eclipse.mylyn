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

package org.eclipse.mylar.internal.tasklist.ui;

import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.ui.editors.MylarTaskEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public interface ITaskEditorFactory {

	public IEditorPart createEditor(MylarTaskEditor parentEditor);

	public IEditorInput createEditorInput(ITask task);
	
	public boolean providesOutline();

	public String getTitle();

	public void notifyEditorActivationChange(IEditorPart editor);

	public boolean canCreateEditorFor(ITask task);
}
