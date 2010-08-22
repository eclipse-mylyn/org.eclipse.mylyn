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

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.mylyn.internal.builds.ui.view.BuildContentProvider.Presentation;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Steffen Pingel
 */
public class PresentationMenuAction extends Action implements IMenuCreator {

	private final BuildsView view;

	public PresentationMenuAction(BuildsView view) {
		this.view = view;
		setMenuCreator(this);
		setText("Top Level Elements");
	}

	private void addActions(Menu menu) { // add repository action

		Presentation selectedPresentation = view.getContentProvider().getPresentation();
		System.err.println(selectedPresentation);
		for (final Presentation presentation : Presentation.values()) {
			Action action = new Action() {
				@Override
				public void run() {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							view.getContentProvider().setPresentation(presentation);
						}
					});
				}
			};
			action.setText(presentation.toString());
			action.setChecked(presentation == selectedPresentation);
			ActionContributionItem item = new ActionContributionItem(action);
			item.fill(menu, -1);
		}
	}

	public void dispose() {
		// ignore

	}

	public Menu getMenu(Control parent) {
		return null;
	}

	public Menu getMenu(Menu parent) {
		final Menu menu = new Menu(parent);
		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				for (MenuItem item : menu.getItems()) {
					item.dispose();
				}
				addActions(menu);
			}
		});
		return menu;
	}

}
