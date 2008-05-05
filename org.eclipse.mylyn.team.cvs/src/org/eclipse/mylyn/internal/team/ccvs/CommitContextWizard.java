/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ccvs;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.ui.wizards.CommitWizard;

/**
 * @author Mik Kersten
 */
public class CommitContextWizard extends CommitWizard {

	public CommitContextWizard(IResource[] resources, ITask task) throws CVSException {
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
