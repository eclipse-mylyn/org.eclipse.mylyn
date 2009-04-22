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

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.ImportExportUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Steffen Pingel
 */
public class ExportAction implements IViewActionDelegate {

	private ISelection selection;

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		if (selection.isEmpty() || !(selection instanceof StructuredSelection)) {
			MessageDialog.openError(WorkbenchUtil.getShell(), Messages.ExportAction_Dialog_Title, Messages.ExportAction_Nothing_selected);
			return;
		}

		FileDialog dialog = new FileDialog(WorkbenchUtil.getShell(), SWT.PRIMARY_MODAL | SWT.SAVE);
		dialog.setText(Messages.ExportAction_Dialog_Title);
		ImportExportUtil.configureFilter(dialog);
		dialog.setFileName(ITasksCoreConstants.EXPORT_FILE_NAME + ITasksCoreConstants.FILE_EXTENSION);
		String path = dialog.open();
		if (path != null) {
			File file = new File(path);
			// Prompt the user to confirm if save operation will cause an overwrite
			if (file.exists()) {
				if (!MessageDialog.openConfirm(WorkbenchUtil.getShell(), Messages.ExportAction_Dialog_Title, NLS.bind(
						Messages.ExportAction_X_exists_Do_you_wish_to_overwrite, file.getPath()))) {
					return;
				}
			}
			try {
				ImportExportUtil.export(file, (IStructuredSelection) selection);
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Problems encountered during export", e)); //$NON-NLS-1$
				TasksUiInternal.displayStatus(Messages.ExportAction_Dialog_Title, new MultiStatus(ITasksCoreConstants.ID_PLUGIN, 0,
						new IStatus[] { e.getStatus() },
						Messages.ExportAction_Problems_encountered, e));
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
