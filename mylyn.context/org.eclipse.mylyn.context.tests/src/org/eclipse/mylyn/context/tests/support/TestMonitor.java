/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @deprecated use {@link org.eclipse.mylyn.context.sdk.util.AbstractUserInteractionMonitor} instead
 * @author Mik Kersten
 */
@Deprecated
public class TestMonitor extends AbstractUserInteractionMonitor {

	List<IJavaElement> selections = new ArrayList<>();

	public TestMonitor() {
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
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection,
			boolean contributeToContext) {
		// don't need to do anything here

	}
}
