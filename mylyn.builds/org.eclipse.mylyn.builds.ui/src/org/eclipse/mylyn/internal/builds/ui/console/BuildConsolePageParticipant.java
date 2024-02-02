/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.console;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.actions.CloseConsoleAction;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * @author Steffen Pingel
 */
public class BuildConsolePageParticipant implements IConsolePageParticipant {

	private CloseConsoleAction closeAction;

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	@Override
	public void init(IPageBookViewPage page, IConsole console) {
		closeAction = new CloseConsoleAction(console);

		IToolBarManager manager = page.getSite().getActionBars().getToolBarManager();
		manager.appendToGroup(IConsoleConstants.LAUNCH_GROUP, closeAction);
	}

	@Override
	public void dispose() {
		// ignore
	}

	@Override
	public void activated() {
		// ignore
	}

	@Override
	public void deactivated() {
		// ignore
	}

}
