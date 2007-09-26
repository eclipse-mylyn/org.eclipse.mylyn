/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Action to export a task to an external file.
 * @author Jevgeni Holodkov
 */
public class TaskExportAction extends Action implements IViewActionDelegate {

	protected ISelection selection;
	
	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		run(getSelectedTasks(selection));
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		List<AbstractTask> tasks = getSelectedTasks(selection);
		action.setEnabled(true);
		if (tasks.size() > 0) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
		}
	}
	
	protected List<AbstractTask> getSelectedTasks(ISelection newSelection) {
		List<AbstractTask> selectedQueries = new ArrayList<AbstractTask>();
		if (selection instanceof StructuredSelection) {
			List<?> selectedObjects = ((StructuredSelection) selection).toList();
			for (Object selectedObject : selectedObjects) {
				if (selectedObject instanceof AbstractTask) {
					selectedQueries.add((AbstractTask) selectedObject);
				}
			}
		}
		return selectedQueries;
	}
	
	public void run(List<AbstractTask> tasks) {
		Map<AbstractTask, File> taskFiles = new HashMap<AbstractTask, File>();

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		if(tasks.size() == 1) {
			// open FileDialog
			FileDialog dialog = new FileDialog(shell, SWT.PRIMARY_MODAL | SWT.SAVE);
			dialog.setFilterExtensions(new String[] { "*" + ITasksUiConstants.FILE_EXTENSION });

			AbstractTask task = tasks.get(0);
			dialog.setFileName(encodeName(task) + ITasksUiConstants.FILE_EXTENSION);
			String path = dialog.open();

			if (path != null) {
				File file = new File(path);
				if (file.isDirectory()) {
					MessageDialog.openError(shell, "Task Export Error",
							"Could not export task because specified location is a folder");
					return;
				}

				taskFiles.put(task, file);
			}
		} else {
			// open DirectoryDialog
			DirectoryDialog dialog = new DirectoryDialog(shell, SWT.PRIMARY_MODAL | SWT.SAVE);
			String path = dialog.open();
			for (AbstractTask task : tasks) {
				File file = new File(path, encodeName(task) + ITasksUiConstants.FILE_EXTENSION);
				taskFiles.put(task, file);
			}
		}
		
		for(Entry<AbstractTask, File> entry : taskFiles.entrySet()) {
			AbstractTask task = entry.getKey();
			File file = entry.getValue();
			
			// Prompt the user to confirm if save operation will cause an overwrite
			if (file.exists()) {
				if (!MessageDialog.openQuestion(shell, "Confirm File Replace", "The file " + file.getPath()
						+ " already exists. Do you want to overwrite it?")) {
					continue;
				}
			}
			
			TasksUiPlugin.getTaskListManager().getTaskListWriter().writeTask(task, file);
		}
		 return;
	}

	private String encodeName(AbstractTask task) {
		String encodedName = null;
		try {
			encodedName = URLEncoder.encode(task.getHandleIdentifier(), ITasksUiConstants.FILENAME_ENCODING);
		} catch (UnsupportedEncodingException e) {
			StatusHandler.fail(e, "Could not determine name for the selected task", false);
		}
		
		return encodedName;
	}

}
