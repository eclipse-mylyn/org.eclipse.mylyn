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
/*
 * Created on Feb 9, 2005
 */
package org.eclipse.mylyn.context.tests.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 */
public class TestMonitor extends AbstractUserInteractionMonitor {

	List<IJavaElement> selections = new ArrayList<IJavaElement>();

	public TestMonitor() {
		super();
	}

	public void handleElementSelection(IJavaElement selected) {
		selections.add(selected);
	}

	protected void handleUnknownSelection(Object selectedObject) {
		// don't need to do anything here
	}

	protected void handleSelection(File file) {
		// don't need to do anything here
	}

	public List<IJavaElement> getSelections() {
		return selections;
	}

	public void handleReferenceNavigation(IJavaElement from, IJavaElement to) {
		// don't need to do anything here
	}

	public void handleImplementorNavigation(IJavaElement from, IJavaElement to) {
		// don't need to do anything here
	}

	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection, boolean contributeToContext) {
		// don't need to do anything here

	}
}
