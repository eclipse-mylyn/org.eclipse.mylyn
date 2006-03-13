/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.ui.editors.CategoryEditorInput;
import org.eclipse.mylar.internal.tasklist.ui.editors.TaskEditorInput;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart; 
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;

/**
 * @author Mik Kersten
 */
public class TaskListUiUtil {

	/**
	 * TODO: move
	 */
	public static Image getImageForPriority(Task.PriorityLevel priorityLevel) {
		if (priorityLevel == null) {
			return null;
		}
		switch (priorityLevel) {
		case P1: 
			return TaskListImages.getImage(TaskListImages.PRIORITY_1);
		case P2:
			return TaskListImages.getImage(TaskListImages.PRIORITY_2);
		case P3:
			return TaskListImages.getImage(TaskListImages.PRIORITY_3);
		case P4:
			return TaskListImages.getImage(TaskListImages.PRIORITY_4);
		case P5:
			return TaskListImages.getImage(TaskListImages.PRIORITY_5);
		default:
			return null;
		}
	}
	
	public static void closeEditorInActivePage(ITask task) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page == null) {
			return;
		}
		IEditorInput input = new TaskEditorInput(task);
		IEditorPart editor = page.findEditor(input);
		if (editor != null) {
			page.closeEditor(editor, false);
		}
	}

	public static void openEditor(final ITask task) {
		openEditor(task, true);
	}

	/**
	 * Set asyncExec false for testing purposes.
	 */
	public static void openEditor(final ITask task, boolean asyncExec) {

		final IEditorInput editorInput = new TaskEditorInput(task);

		if (asyncExec) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					openEditor(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID, page);
				}
			});
		} else {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			openEditor(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID, page);
		}
	}

	public static IEditorPart openEditor(IEditorInput input, String editorId, IWorkbenchPage page) {
		try {
			return page.openEditor(input, editorId);
		} catch (PartInitException e) {
			MylarStatusHandler.fail(e, "Open for editor failed: " + input + ", id: " + editorId, true);
		}
		return null;
	}

	public static void openEditor(AbstractTaskContainer category) {
		final IEditorInput input = new CategoryEditorInput(category);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				openEditor(input, TaskListPreferenceConstants.CATEGORY_EDITOR_ID, page);
			}
		});
	}

	public static void openUrl(String title, String tooltip, String url) {
		try {
			IWebBrowser browser = null;
			int flags = 0;
			if (WorkbenchBrowserSupport.getInstance().isInternalWebBrowserAvailable()) {
				flags = WorkbenchBrowserSupport.AS_EDITOR | WorkbenchBrowserSupport.LOCATION_BAR
						| WorkbenchBrowserSupport.NAVIGATION_BAR;

			} else {
				flags = WorkbenchBrowserSupport.AS_EXTERNAL | WorkbenchBrowserSupport.LOCATION_BAR
						| WorkbenchBrowserSupport.NAVIGATION_BAR;
			}
			browser = WorkbenchBrowserSupport.getInstance().createBrowser(flags, MylarTaskListPlugin.PLUGIN_ID + title,
					title, tooltip);
			browser.openURL(new URL(url));
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Browser init error",
					"Browser could not be initiated");
		} catch (MalformedURLException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "URL not found", "URL Could not be opened");
		}
	}
}
