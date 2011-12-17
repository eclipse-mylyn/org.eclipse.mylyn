/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.team;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ContributionItemFactory;

/**
 * @author Steffen Pingel
 */
public class ShowInMenuContribution extends ContributionItem {

	private IContributionItem item;

	public ShowInMenuContribution() {
	}

	public ShowInMenuContribution(String id) {
		super(id);
	}

	@Override
	public void fill(Menu menu, int index) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			if (item != null) {
				item.dispose();
			}
			item = ContributionItemFactory.VIEWS_SHOW_IN.create(window);
			item.fill(menu, index);
		}
	}

	@Override
	public void dispose() {
		if (item != null) {
			item.dispose();
			item = null;
		}
		super.dispose();
	}

}
