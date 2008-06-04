/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ccvs;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.team.ui.AbstractActiveChangeSetProvider;
import org.eclipse.mylyn.team.ui.IContextChangeSet;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;

/**
 * CVS change set integration for Mylyn.
 * 
 * @author Mik Kersten
 */
public class CvsActiveChangeSetProvider extends AbstractActiveChangeSetProvider {

	@Override
	public ActiveChangeSetManager getActiveChangeSetManager() {
		return CVSUIPlugin.getPlugin().getChangeSetManager();
	}

	@Override
	public IContextChangeSet createChangeSet(ITask task) {
		return new CvsContextChangeSet(task, getActiveChangeSetManager());
	}
}
