/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

/**
 * @author Steffen Pingel
 */
public class ContextUiStartupHandler implements IContextUiStartup {

	public ContextUiStartupHandler() {
		// ignore
	}

	public void lazyStartup() {
		ExternalizationManager externalizationManager = TasksUiPlugin.getExternalizationManager();
		ActiveContextExternalizationParticipant activeContextExternalizationParticipant = new ActiveContextExternalizationParticipant(
				externalizationManager);
		externalizationManager.addParticipant(activeContextExternalizationParticipant);
		activeContextExternalizationParticipant.registerListeners();
	}

}
