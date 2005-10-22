/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * TODO: move to UI
 * 
 * @author Mik Kersten
 */
public interface IContextEditorFactory {

	public IEditorPart createEditor();
	
	public IEditorInput createEditorInput(IMylarContext context);

	public String getTitle();
}
