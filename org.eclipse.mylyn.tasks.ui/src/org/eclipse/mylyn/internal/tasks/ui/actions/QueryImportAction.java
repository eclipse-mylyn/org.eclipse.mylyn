/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
		dialog.setFilterExtensions(new String[] { "*" + ITasksCoreConstants.FILE_EXTENSION });

		String path = dialog.open();
		if (path != null) {
			File file = new File(path);
			if (file.isFile()) {
				try {
					List<RepositoryQuery> queries = TasksUiPlugin.getTaskListManager().getTaskListWriter().readQueries(
							file);
					Set<TaskRepository> repositories = TasksUiPlugin.getTaskListManager()
							.getTaskListWriter()
							.readRepositories(file);
					if (queries.size() > 0) {
						importQueries(queries, repositories, shell);
					} else {
						MessageDialog.openError(shell, "Query Import Error",
								"The specified file is not an exported query. Please, check that you have provided the correct file.");
					}
				} catch (IOException e) {
					MessageDialog.openError(shell, "Query Import Error",
							"The specified file is not an exported query. Please, check that you have provided the correct file.");
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
		String message = "The following queries were imported successfully: ";
		for (RepositoryQuery imported : queries) {
			if (!badQueries.contains(imported)) {
				message += "\n" + imported.getHandleIdentifier();
			}
		}

		if (badQueries.size() > 0) {
			message += "\n\n These queries were not imported, since their repository was not found: ";
			for (RepositoryQuery bad : badQueries) {
				message += "\n" + bad.getHandleIdentifier();
			}
		}

		MessageDialog.openInformation(shell, "Query Import Completed", message);
	}

	/**
	 * Imports Queries to the TaskList and synchronize them with the repository. If the imported query have the name
	 * that overlaps with the existing one, the the suffix [x] is added, where x is a number starting from 1.
	 * 
	 * @param queries
	 * 		to insert
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
	 * 	beginning, then the query's own identifier is returned. If there were, then the suffix [x] is applied the
	 * 	query's identifier, where x is a number.
	 * @since 2.1
	 */
	public static String resolveIdentifiersConflict(RepositoryQuery query) {
		String patternStr = "\\[(\\d+)\\]$"; // all string that end with [x], where x is a number
		Pattern pattern = Pattern.compile(patternStr);

		// resolve name conflict
		Set<RepositoryQuery> existingQueries = TasksUiPlugin.getTaskListManager().getTaskList().getQueries();
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
				handle = matcher.replaceAll("[" + index + "]");
			} else {
				handle += "[1]";
			}
		}

		return handle;
	}

}
