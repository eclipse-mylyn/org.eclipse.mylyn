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

package org.eclipse.mylar.provisional.ui;

import java.util.List;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public interface IMylarUiBridge {

	public abstract void open(IMylarElement element);

//	public abstract void setContextCapturePaused(boolean paused);

	public abstract void restoreEditor(IMylarElement document);

	public abstract void close(IMylarElement element);

	public abstract boolean acceptsEditor(IEditorPart editorPart);

	public abstract IMylarElement getElement(IEditorInput input);
	
	/**
	 * Note that a single editor part can correspond to multipe outlines (e.g.
	 * the PDE manifest editor).
	 * 
	 * @return	an empty list if none
	 */
	public abstract List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart);

	public abstract Object getObjectForTextSelection(TextSelection selection, IEditorPart editor);

}
