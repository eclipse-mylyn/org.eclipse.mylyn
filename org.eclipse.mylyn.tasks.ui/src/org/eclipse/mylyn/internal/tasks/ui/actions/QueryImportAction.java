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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.PlatformUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Makes able to select an exported query file and import it back to the system.
 * 
 * @author Jevgeni Holodkov
 */
public class QueryImportAction extends Action implements IViewActionDelegate {

	protected ISelection selection;

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog dialog = new FileDialog(shell);
		dialog.setText(Messages.QueryImportAction_Import_Mylyn_Query);
		dialog.setFilterExtensions(PlatformUtil.getFilterExtensions(ITasksCoreConstants.FILE_EXTENSION));
		dialog.setFilterNames(new String[] { Messages.QueryImportAction_Mylyn_Queries
				+ " (*" + ITasksCoreConstants.FILE_EXTENSION + ")" }); //$NON-NLS-1$ //$NON-NLS-2$

		String path = dialog.open();
		if (path != null) {
			File file = new File(path);
			if (file.isFile()) {
				try {
					List<RepositoryQuery> queries = TasksUiPlugin.getTaskListWriter().readQueries(file);
					Set<TaskRepository> repositories = TasksUiPlugin.getTaskListWriter().readRepositories(file);
					if (queries.size() > 0) {
						importQueries(queries, repositories, shell);
					} else {
						MessageDialog.openError(shell, Messages.QueryImportAction_Query_Import_Error,
								Messages.QueryImportAction_The_specified_file_is_not_an_exported_query);
					}
				} catch (IOException e) {
					MessageDialog.openError(shell, Messages.QueryImportAction_Query_Import_Error,
							Messages.QueryImportAction_The_specified_file_is_not_an_exported_query);
				}
			}
		}
	}

	/**
	 * @param queries
	 * @param repositories
	 * @param shell
	 */
	public void importQueries(List<RepositoryQuery> queries, Set<TaskRepository> repositories, Shell shell) {
		TasksUiPlugin.getRepositoryManager().insertRepositories(repositories,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
		List<RepositoryQuery> badQueries = insertQueries(queries);

		// notify user about importing
		String message = Messages.QueryImportAction_The_following_queries_were_imported_successfully;
		for (RepositoryQuery imported : queries) {
			if (!badQueries.contains(imported)) {
				message += "\n" + imported.getHandleIdentifier(); //$NON-NLS-1$
			}
		}

		if (badQueries.size() > 0) {
			message += "\n\n " + Messages.QueryImportAction_These_queries_were_not_imported; //$NON-NLS-1$
			for (RepositoryQuery bad : badQueries) {
				message += "\n" + bad.getHandleIdentifier(); //$NON-NLS-1$
			}
		}

		MessageDialog.openInformation(shell, Messages.QueryImportAction_Query_Import_Completed, message);
	}

	/**
	 * Imports Queries to the TaskList and synchronize them with the repository. If the imported query have the name
	 * that overlaps with the existing one, the the suffix [x] is added, where x is a number starting from 1.
	 * 
	 * @param queries
	 *            to insert
	 * @return the list queries, which were not inserted since because the related repository was not found.
	 */
	public List<RepositoryQuery> insertQueries(List<RepositoryQuery> queries) {
		List<RepositoryQuery> badQueries = new ArrayList<RepositoryQuery>();

		for (RepositoryQuery query : queries) {

			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(query.getConnectorKind(),
					query.getRepositoryUrl());
			if (repository == null) {
				badQueries.add(query);
				continue;
			}

			String handle = resolveIdentifiersConflict(query);
			query.setHandleIdentifier(handle);

			// add query
			TasksUiInternal.getTaskList().addQuery(query);

			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			if (connector != null) {
				TasksUiInternal.synchronizeQuery(connector, query, null, true);
			}

		}

		return badQueries;
	}

	/**
	 * Utility method that checks, if there is already a query with the same identifier.
	 * 
	 * @param query
	 * @return a handle, that is not in conflict with any existed one in the system. If there were no conflict in the
	 *         beginning, then the query's own identifier is returned. If there were, then the suffix [x] is applied the
	 *         query's identifier, where x is a number.
	 * @since 2.1
	 */
	public static String resolveIdentifiersConflict(RepositoryQuery query) {
		String patternStr = "\\[(\\d+)\\]$"; // all string that end with [x], where x is a number //$NON-NLS-1$
		Pattern pattern = Pattern.compile(patternStr);

		// resolve name conflict
		Set<RepositoryQuery> existingQueries = TasksUiPlugin.getTaskList().getQueries();
		Map<String, RepositoryQuery> queryMap = new HashMap<String, RepositoryQuery>();
		for (RepositoryQuery existingQuery : existingQueries) {
			queryMap.put(existingQuery.getHandleIdentifier(), existingQuery);
		}

		// suggest a new handle if needed
		String handle = query.getHandleIdentifier();

		while (queryMap.get(handle) != null) {
			Matcher matcher = pattern.matcher(handle);
			boolean matchFound = matcher.find();
			if (matchFound) {
				// increment index
				int index = Integer.parseInt(matcher.group(1));
				index++;
				handle = matcher.replaceAll("[" + index + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				handle += "[1]"; //$NON-NLS-1$
			}
		}

		return handle;
	}

}
