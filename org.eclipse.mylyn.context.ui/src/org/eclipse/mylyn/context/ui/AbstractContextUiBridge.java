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

package org.eclipse.mylyn.context.ui;

import java.util.List;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Extend to bridge between a tool's UI and the generic facilities invoked by the Context UI.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractContextUiBridge {

	public abstract void open(IInteractionElement element);

	public abstract void close(IInteractionElement element);

	public abstract boolean acceptsEditor(IEditorPart editorPart);

	public abstract IInteractionElement getElement(IEditorInput input);

	/**
	 * Note that a single editor part can correspond to multipe outlines (e.g. the PDE manifest editor).
	 * 
	 * @return an empty list if none
	 */
	public abstract List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart);

	public abstract Object getObjectForTextSelection(TextSelection selection, IEditorPart editor);

	public abstract String getContentType();

}
