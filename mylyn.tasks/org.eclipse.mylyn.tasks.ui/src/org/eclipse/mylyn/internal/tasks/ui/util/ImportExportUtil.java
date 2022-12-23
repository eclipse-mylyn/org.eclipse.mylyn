/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.File;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.ui.PlatformUiUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TransferList;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Steffen Pingel
 */
public class ImportExportUtil {

	public static void configureFilter(FileDialog dialog) {
		dialog.setFilterExtensions(
				PlatformUiUtil.getFilterExtensions(new String[] { "*" + ITasksCoreConstants.FILE_EXTENSION })); //$NON-NLS-1$
		dialog.setFilterNames(new String[] {
				NLS.bind(Messages.ImportExportUtil_Tasks_and_queries_Filter0, ITasksCoreConstants.FILE_EXTENSION) });
	}

	public static void export(File file, IStructuredSelection selection) throws CoreException {
		// extract queries and tasks from selection
		TransferList list = new TransferList();
		for (Iterator<?> it = selection.iterator(); it.hasNext();) {
			Object element = it.next();
			if (element instanceof TaskCategory) {
				list.addCategory((TaskCategory) element);
			} else if (element instanceof RepositoryQuery) {
				list.addQuery((RepositoryQuery) element);
			} else if (element instanceof ITask) {
				list.addTask((AbstractTask) element);
			}
		}

		TaskListExternalizer externalizer = TasksUiPlugin.getDefault().createTaskListExternalizer();
		externalizer.writeTaskList(list, file);
	}

}
