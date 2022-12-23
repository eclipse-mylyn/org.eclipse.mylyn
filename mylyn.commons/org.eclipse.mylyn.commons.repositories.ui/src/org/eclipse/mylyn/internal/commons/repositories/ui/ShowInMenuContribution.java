/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.ui;

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
