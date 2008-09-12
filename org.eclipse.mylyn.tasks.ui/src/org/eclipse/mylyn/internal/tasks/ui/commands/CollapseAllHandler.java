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

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.AbstractTreeViewer;

/**
 * @author Steffen Pingel
 */
// TODO e3.4 replace with org.eclipse.ui.handlers.CollapseAllHandler
public class CollapseAllHandler extends AbstractHandler {

	public static final String ID_COMMAND = "org.eclipse.ui.navigate.collapseAll"; //$NON-NLS-1$

	private final AbstractTreeViewer treeViewer;

	public CollapseAllHandler(AbstractTreeViewer treeViewer) {
		Assert.isNotNull(treeViewer);
		this.treeViewer = treeViewer;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		treeViewer.collapseAll();
		return null;
	}

}
