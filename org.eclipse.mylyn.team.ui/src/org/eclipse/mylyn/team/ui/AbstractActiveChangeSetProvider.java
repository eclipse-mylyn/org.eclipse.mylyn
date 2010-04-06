/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenkecht - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.team.ui;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.team.ui.ContextChangeSet;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;

/**
 * Integrates an Eclipse Team repository with Mylyn.
 * 
 * @author Gunnar Wagenknecht
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractActiveChangeSetProvider {

	private static final String LABEL_NO_TASK = "<No Active Task>"; //$NON-NLS-1$

	private static final String HANDLE_NO_TASK = "org.eclipse.mylyn.team.ui.inactive.proxy"; //$NON-NLS-1$

	private final ITask noTaskActiveProxy = new LocalTask(HANDLE_NO_TASK, LABEL_NO_TASK);

	/**
	 * Return the change set collector that manages the active change set for the participant associated with this
	 * capability. A <code>null</code> is returned if active change sets are not supported. The default is to return
	 * <code>null</code>. This method must be overridden by subclasses that support active change sets. Note that
	 * {@link ActiveChangeSetManager} is an internal class of <code>org.eclipse.team.core</code>, but is required for
	 * change set support (bug 116084). The current implementation will only work if a subtype of
	 * {@link ActiveChangeSetManager} is returned. In the future, if a change set API becomes available, an additional
	 * extensibility mechanism will be provided.
	 * 
	 * @return the change set collector that manages the active change set for the participant associated with this
	 *         capability or <code>null</code> if active change sets are not supported.
	 * @since 3.0
	 */
	public ActiveChangeSetManager getActiveChangeSetManager() {
		return null;
	}

	/**
	 * Override if a custom change set class is needed, e.g. in order to support custom action and model mappings as is
	 * the case with the CVS change set implementation used by org.eclipse.mylyn.team.cvs.
	 * 
	 * @since 3.0
	 */
	public IContextChangeSet createChangeSet(ITask task) {
		return new ContextChangeSet(task, getActiveChangeSetManager());
	}

	/**
	 * Called upon deactivation to set the default context when no context is active.
	 * 
	 * @since 3.4
	 */
	public void activateDefaultChangeSet() {
		ActiveChangeSet noTaskSet = null;
		ActiveChangeSetManager manager = getActiveChangeSetManager();
		if (manager != null) {
			noTaskSet = manager.getSet(LABEL_NO_TASK);
			if (noTaskSet == null) {
				noTaskSet = (ActiveChangeSet) this.createChangeSet(noTaskActiveProxy);
				manager.add(noTaskSet);
			}

			manager.makeDefault(noTaskSet);
			noTaskSet.remove(noTaskSet.getResources());
			manager.remove(noTaskSet);
		}
	}

}
