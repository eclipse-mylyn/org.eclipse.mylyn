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

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Mik Kersten
 */
public class InterestDecrementAction extends RemoveFromContextAction implements IViewActionDelegate {

	public InterestDecrementAction(CommonViewer commonViewer, InterestFilter interestFilter) {
		super(commonViewer, interestFilter);
	}

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		// ignore
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			super.selectionChanged((IStructuredSelection)selection);
		}
	}
}
