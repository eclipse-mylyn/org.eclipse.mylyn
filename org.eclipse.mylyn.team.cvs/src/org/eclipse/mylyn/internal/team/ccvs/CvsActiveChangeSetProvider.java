/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
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
