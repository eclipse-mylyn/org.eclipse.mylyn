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

package org.eclipse.mylar.internal.bugs;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTaskSelection;
import org.eclipse.mylar.provisional.core.AbstractUserInteractionMonitor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 */
public class BugzillaEditingMonitor extends AbstractUserInteractionMonitor {

	public BugzillaEditingMonitor() {
		super();
	}

	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection, boolean contributeToContext) {
		if (!(part instanceof AbstractRepositoryTaskEditor) && !(part instanceof BugzillaTaskEditor))
			return;

		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object object = ss.getFirstElement();
			if (object instanceof RepositoryTaskSelection)
				super.handleElementSelection(part, object, contributeToContext);
		}
	}

}
