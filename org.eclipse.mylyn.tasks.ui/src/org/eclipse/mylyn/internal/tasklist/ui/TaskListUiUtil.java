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
import org.eclipse.mylar.internal.tasklist.IQueryHit;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.ITaskCategory;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
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

	public static void openEditor(final IQueryHit hit) {
		ITask task = hit.getOrCreateCorrespondingTask();
		if (task != null) {
			openEditor(task);
		} else {
			MessageDialog.openInformation(null, MylarTaskListPlugin.TITLE_DIALOG,
					"Could not create task for query hit: " + hit);
		}
	}

	public static void openEditor(final ITask task) {
		openEditor(task, true);
	}

	/**
	 * Set asyncExec true for testing purposes.
	 */
	public static void openEditor(final ITask task, boolean asyncExec) {

		final IEditorInput editorInput = new TaskEditorInput(task);
		if (!asyncExec) {
			openEditorInActivePage(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID);
		} else {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					openEditorInActivePage(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID);
				}
			});
			
//			OpenTaskEditorJob openTaskEditorJob = new OpenTaskEditorJob("Opening Task", editorInput);
//			openTaskEditorJob.schedule();
		}
	}

//	private static class OpenTaskEditorJob extends Job {
//
//		private IEditorInput editorInput;
//
//		public OpenTaskEditorJob(String name, IEditorInput editorInput) {
//			super(name);
//			this.editorInput = editorInput;
//		}
//
//		@Override
//		protected IStatus run(IProgressMonitor monitor) {
//			try {
//				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//					public void run() {
//						openEditorInActivePage(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID);
//					}
//				});
//				return new Status(IStatus.OK, MylarPlugin.PLUGIN_ID, IStatus.OK, "", null);
//			} catch (Exception e) {
//				MylarStatusHandler.fail(e, "Could not open task editor", true);
//			}
//			return Status.CANCEL_STATUS;
//		}
//	}

	public static IEditorPart openEditorInActivePage(IEditorInput input, String editorId) {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			return page.openEditor(input, editorId);
		} catch (PartInitException e) {
			MylarStatusHandler.fail(e, "Open for editor failed: " + input + ", id: " + editorId, true);
		}
		return null;
	}

	public static void openEditor(ITaskCategory category) {
		final IEditorInput input = new CategoryEditorInput(category);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				openEditorInActivePage(input, TaskListPreferenceConstants.CATEGORY_EDITOR_ID);
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
