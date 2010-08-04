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
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.builds.ui.BuildsUiUtil;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Steffen Pingel
 */
public class NewBuildServerMenuAction extends Action implements IMenuCreator {

	private Menu menu;

	private MenuManager manager;

	public NewBuildServerMenuAction() {
		setMenuCreator(this);
		setToolTipText("New Build Server Location");
		setImageDescriptor(TasksUiImages.REPOSITORY_NEW);
	}

	public void dispose() {
		if (menu != null) {
			menu.dispose();
			menu = null;
		}
		if (manager != null) {
			manager.dispose();
			manager = null;
		}
	}

	@Override
	public void run() {
		new NewBuildServerAction().run();
	}

	public Menu getMenu(Control parent) {
		initMenuManager();
		menu = manager.createContextMenu(parent);
		return menu;
	}

	public Menu getMenu(Menu parent) {
		initMenuManager();
		menu = new Menu(parent);
		manager.fill(menu, 0);
		return menu;
	}

	private void initMenuManager() {
		dispose();

		manager = new MenuManager();
		addActions(manager);
	}

	private void addActions(IMenuManager manager) { // add repository action
		NewBuildServerAction action = new NewBuildServerAction();
		manager.add(action);

		// open repository configuration actions
		boolean separatorAdded = false;
		for (final IBuildServer server : BuildsUi.getModel().getServers()) {
			if (!separatorAdded) {
				manager.add(new Separator());
				separatorAdded = true;
			}
			Action openAction = new Action() {
				@Override
				public void run() {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							BuildsUiUtil.openPropertiesDialog(server);
						}
					});
				}
			};
			openAction.setText(NLS.bind("Properties for {0}", server.getLocation().getLabel()));
			manager.add(openAction);
		}
	}

}
