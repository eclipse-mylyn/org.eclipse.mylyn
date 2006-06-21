/*******************************************************************************
 * Copyright (c) 2006 Gunnar Wagenknecht, Truition and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.provisional.ide.team;

import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;

/**
 * Integrates an Eclipt Team repository with Mylar.
 */
public abstract class TeamRepositoryProvider {

	/**
	 * Return the change set collector that manages the active change set for
	 * the participant associated with this capability. A <code>null</code> is
	 * returned if active change sets are not supported. The default is to
	 * return <code>null</code>. This method must be overridden by subclasses
	 * that support active change sets.
	 * 
	 * @return the change set collector that manages the active change set for
	 *         the participant associated with this capability or
	 *         <code>null</code> if active change sets are not supported.
	 */
	public ActiveChangeSetManager getActiveChangeSetManager() {
		return null;
	}
}
