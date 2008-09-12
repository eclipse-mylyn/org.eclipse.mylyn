/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jevgeni Holodkov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Makes able to export selected query to the file system.
 * 
 * @author Jevgeni Holodkov
 */
public class QueryExportAction extends Action implements IViewActionDelegate {

	protected ISelection selection;

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		run(getSelectedQueries(selection));
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		List<RepositoryQuery> selectedQueries = getSelectedQueries(selection);
		action.setEnabled(true);
		if (selectedQueries.size() > 0) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
		}
	}

	@SuppressWarnings("unchecked")
	protected List<RepositoryQuery> getSelectedQueries(ISelection newSelection) {
		List<RepositoryQuery> selectedQueries = new ArrayList<RepositoryQuery>();
		if (selection instanceof StructuredSelection) {
			List selectedObjects = ((StructuredSelection) selection).toList();
			for (Object selectedObject : selectedObjects) {
				if (selectedObject instanceof IRepositoryQuery) {
					selectedQueries.add((RepositoryQuery) selectedObject);
				}
			}
		}
		return selectedQueries;
	}

	public void run(List<RepositoryQuery> queries) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog dialog = new FileDialog(shell, SWT.PRIMARY_MODAL | SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*" + ITasksCoreConstants.FILE_EXTENSION });
		if (queries.size() == 1) {
			dialog.setFileName(queries.get(0).getHandleIdentifier() + ITasksCoreConstants.FILE_EXTENSION);
		} else {
			String fomratString = "yyyy-MM-dd";
			SimpleDateFormat format = new SimpleDateFormat(fomratString, Locale.ENGLISH);
			String date = format.format(new Date());
			dialog.setFileName(date + "-exported-queries" + ITasksCoreConstants.FILE_EXTENSION);
		}

		String path = dialog.open();
		if (path != null) {
			File file = new File(path);
			if (file.isDirectory()) {
				MessageDialog.openError(shell, "Query Export Error",
						"Could not export query because specified location is a folder");
				return;
			}

			// Prompt the user to confirm if save operation will cause an overwrite
			if (file.exists()) {
				if (!MessageDialog.openConfirm(shell, "Confirm File Replace", "The file " + file.getPath()
						+ " already exists. Do you want to overwrite it?")) {
					return;
				}
			}

			TasksUiPlugin.getTaskListManager().getTaskListWriter().writeQueries(queries, file);
		}
		return;
	}
}
