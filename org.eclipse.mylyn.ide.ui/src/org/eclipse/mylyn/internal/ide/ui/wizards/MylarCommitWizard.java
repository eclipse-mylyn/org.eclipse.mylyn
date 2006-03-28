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

package org.eclipse.mylar.internal.ide.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.ui.wizards.CommitWizard;

/**
 * @author Mik Kersten
 */
public class MylarCommitWizard extends CommitWizard {

	public MylarCommitWizard(IResource[] resources, ITask task) throws CVSException {
		super(resources);
	}

	@Override
	public void dispose() {
		try {
			super.dispose();
		} catch (Exception e) {
			// ignore, see bug 132888
		}
	}
}
