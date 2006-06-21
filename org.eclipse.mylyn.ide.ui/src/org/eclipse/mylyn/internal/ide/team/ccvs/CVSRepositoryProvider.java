/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *     Gunnar Wagenknecht - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.ide.team.ccvs;

import org.eclipse.mylar.provisional.ide.team.TeamRepositoryProvider;
import org.eclipse.team.internal.ccvs.core.mapping.CVSActiveChangeSetCollector;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;

/**
 * CVS integration for Mylar.
 */
public class CVSRepositoryProvider extends TeamRepositoryProvider {

	@Override
	public ActiveChangeSetManager getActiveChangeSetManager() {
		return (CVSActiveChangeSetCollector) CVSUIPlugin.getPlugin()
				.getChangeSetManager();
	}
}
