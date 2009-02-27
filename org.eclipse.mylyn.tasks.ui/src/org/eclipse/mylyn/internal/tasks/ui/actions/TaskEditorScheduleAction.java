/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Collections;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.ScheduleTaskMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Mik Kersten
 */
public class TaskEditorScheduleAction extends Action implements IMenuCreator {

	private final ITask task;

	private MenuManager menuManager;

	private final ScheduleTaskMenuContributor scheduleMenuContributor = new ScheduleTaskMenuContributor();

	public TaskEditorScheduleAction(ITask task) {
		this.task = task;
		this.setImageDescriptor(CommonImages.SCHEDULE_DAY);
		setMenuCreator(this);
	}

	@Override
	public void run() {
		TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) task,
				TaskActivityUtil.getCurrentWeek().getToday());
	}

	public Menu getMenu(Control parent) {
		if (menuManager != null) {
			menuManager.dispose();
		}
		menuManager = scheduleMenuContributor.getSubMenuManager(Collections.singletonList((IRepositoryElement) task));
		menuManager.createContextMenu(parent);
		return menuManager.getMenu();
	}

	public Menu getMenu(Menu parent) {
		if (menuManager != null) {
			return menuManager.getMenu();
		}
		return null;
	}

	public void dispose() {
		if (menuManager != null) {
			menuManager.dispose();
		}
	}

}