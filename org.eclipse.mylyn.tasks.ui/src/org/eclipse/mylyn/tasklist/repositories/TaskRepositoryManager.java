/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.repositories;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryManager {
	
	public static final String PREF_REPOSITORIES = "org.eclipse.mylar.tasklist.repositories.urls";
	
	private List<ITaskRepositoryClient> repositoryClients = new ArrayList<ITaskRepositoryClient>();

	private List<TaskRepository> repositories = new ArrayList<TaskRepository>();
	
	private static final String STORE_DELIM = ", ";
	
	public List<ITaskRepositoryClient> getRepositoryClients() {
		return repositoryClients;
	}
	
	public void addRepositoryClient(ITaskRepositoryClient repositoryClient) {
		if (!repositoryClients.contains(repositoryClient)) {
			repositoryClients.add(repositoryClient);
		}
		readRepositories();
	}
	
	public void removeRepositoryClient(ITaskRepositoryClient repositoryClient) {
		repositoryClients.remove(repositoryClient);
	}

	public void addRepository(TaskRepository repository) {
		repositories.add(repository);
		saveRepositories();
	}

	public List<TaskRepository> readRepositories() {
		repositories.clear();
		String read = MylarTaskListPlugin.getPrefs().getString(PREF_REPOSITORIES);
		if (read != null) {
			StringTokenizer st = new StringTokenizer(read, STORE_DELIM);
			while (st.hasMoreTokens()) {
				String urlString = st.nextToken();
				try {
					URL url = new URL(urlString);
					repositories.add(new TaskRepository(url));
				} catch (MalformedURLException e) {
					ErrorLogger.fail(e, "could not restore URL: " + urlString, false);
				}
			}
		}
		return repositories;
	}
	
	private void saveRepositories() {
		String store = "";
		for (TaskRepository repository : repositories) {
			store += repository.getServerUrl().toExternalForm() + STORE_DELIM;
		}
		
		MylarTaskListPlugin.getPrefs().setValue(PREF_REPOSITORIES, store);
	}

	public List<TaskRepository> getRepositories() {
		return repositories;
	}

	public void setRepositories(List<TaskRepository> repositories) {
		this.repositories = repositories;
	}
}
