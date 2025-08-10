/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 * @deprecated use {@link org.eclipse.mylyn.commons.workbench.TaskBarManager} instead
 */
@Deprecated
public class TaskBarManager {

	private static final String TASK_BAR_MENU_MANAGER_KEY = "org.eclipse.mylyn.commons.TaskBarMenuManager.instance"; //$NON-NLS-1$

	public static MenuManager getTaskBarMenuManager() {
		Widget /* TaskItem */ taskItem = getApplicationTaskItem();
		if (taskItem != null) {
			MenuManager taskBarMenuManager = getTaskBarMenuManager(taskItem);
			return taskBarMenuManager;
		}
		return null;
	}

	private static Widget /* TaskItem */ getApplicationTaskItem() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null && workbench.getDisplay() != null && !workbench.isClosing()) {

			try {
				Class<?> taskBarClass = Class.forName("org.eclipse.swt.widgets.TaskBar"); //$NON-NLS-1$
				Method getSystemTaskBarMethod = Display.class.getMethod("getSystemTaskBar"); //$NON-NLS-1$
				Object systemTaskBar = getSystemTaskBarMethod.invoke(workbench.getDisplay());
				if (systemTaskBar != null && systemTaskBar.getClass().equals(taskBarClass)) {

					Method getItemMethod = taskBarClass.getMethod("getItem", Shell.class); //$NON-NLS-1$
					Object taskItem = getItemMethod.invoke(systemTaskBar, new Object[] { null });
					if (taskItem instanceof Widget) {
						return (Widget) taskItem;
					}
				}
			} catch (Throwable t) {
				// ignore since class probably doesn't exist
			}

		}
		return null;
	}

	private static TaskBarMenuManager getTaskBarMenuManager(Widget /* TaskItem */ taskItem) {
		Assert.isNotNull(taskItem);
		TaskBarMenuManager taskBarMenuManager;
		Object data = taskItem.getData(TASK_BAR_MENU_MANAGER_KEY);
		if (data instanceof TaskBarMenuManager) {
			taskBarMenuManager = (TaskBarMenuManager) data;
		} else {
			taskBarMenuManager = new TaskBarMenuManager(taskItem);
			taskItem.setData(TASK_BAR_MENU_MANAGER_KEY, taskBarMenuManager);
		}
		return taskBarMenuManager;
	}

	private static final class TaskBarMenuManager extends MenuManager {

		private final Widget taskItem;

		public TaskBarMenuManager(Widget taskItem) {
			this.taskItem = taskItem;
		}

		@Override
		protected void update(boolean force, boolean recursive) {
			// force Menu creation
			Menu menu = getMenu();
			if (menu == null) {
				Shell shell = getShell();
				if (shell != null) {
					menu = createContextMenu(shell);
				}
			}
			super.update(force, recursive);
			if (menu != null && menu.getItemCount() == 0) {
				// clear the menu on the TaskItem if there are no items
				menu = null;
			}
			if (taskItem != null && !taskItem.isDisposed()) {
				setMenuOnTaskItem(taskItem, menu);
			}
		}

		private void setMenuOnTaskItem(Widget taskItem, Menu menu) {
			try {
				Class<?> taskItemClass = Class.forName("org.eclipse.swt.widgets.TaskItem"); //$NON-NLS-1$
				if (taskItem.getClass().equals(taskItemClass)) {
					Method setMenuMethod = taskItemClass.getMethod("setMenu", Menu.class); //$NON-NLS-1$
					setMenuMethod.invoke(taskItem, menu);
				}
			} catch (Throwable t) {
				// ignore since class probably doesn't exist
			}
		}

		/**
		 * From @see org.eclipse.mylyn.commons.workbench.WorkbenchUtil#getNonModalShell()
		 * <p>
		 * Get the active non modal shell. If there isn't one return null.
		 * <p>
		 * <b>Note: Applied from patch on bug 99472.</b>
		 *
		 * @return Shell
		 */
		private static Shell getShell() {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window == null) {
				IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
				if (windows.length > 0) {
					return windows[0].getShell();
				}
			} else {
				return window.getShell();
			}

			return null;
		}
	}
}
