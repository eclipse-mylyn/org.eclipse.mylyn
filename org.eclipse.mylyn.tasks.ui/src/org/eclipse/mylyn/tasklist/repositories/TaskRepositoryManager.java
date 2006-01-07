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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryManager {
	
	public static final String PREF_REPOSITORIES = "org.eclipse.mylar.tasklist.repositories.";
	
	private List<ITaskRepositoryClient> repositoryClients = new ArrayList<ITaskRepositoryClient>();

	private Map<String, Set<TaskRepository>> repositoryMap = new HashMap<String, Set<TaskRepository>>();
	
	private Set<ITaskRepositoryListener> listeners = new HashSet<ITaskRepositoryListener>();
	
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
		Set<TaskRepository> repositories = repositoryMap.get(repository.getKind());
		if (repositories == null) {
			repositories = new HashSet<TaskRepository>();
			repositoryMap.put(repository.getKind(), repositories);
		}
		repositories.add(repository);
		saveRepositories();
	}

	public void removeRepository(TaskRepository repository) {
		Set<TaskRepository> repositories = repositoryMap.get(repository.getKind());
		if (repositories != null) {
			repositories.remove(repository);
		}
		saveRepositories();
	}
	
	public void addListener(ITaskRepositoryListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ITaskRepositoryListener listener) {
		listeners.remove(listener);
	}
	
	public Map<String, Set<TaskRepository>> readRepositories() {
		repositoryMap.clear();
		for (ITaskRepositoryClient repositoryClient : repositoryClients) {
			String read = MylarTaskListPlugin.getPrefs().getString(PREF_REPOSITORIES + repositoryClient.getKind());
			Set<TaskRepository> repositories  = new HashSet<TaskRepository>();
			if (read != null) {
				StringTokenizer st = new StringTokenizer(read, STORE_DELIM);
				while (st.hasMoreTokens()) {
					String urlString = st.nextToken();
					try {
						URL url = new URL(urlString);
						repositoryMap.put(repositoryClient.getKind(), repositories);
						repositories.add(new TaskRepository(url, repositoryClient.getKind()));
					} catch (MalformedURLException e) {
						MylarStatusHandler.fail(e, "could not restore URL: " + urlString, false);
					}
				}
			}
		}
		for (ITaskRepositoryListener listener : listeners) {
			listener.repositorySetUpdated();
		}
		return repositoryMap;
	}
	
	private void saveRepositories() {
		String store = "";
		for (ITaskRepositoryClient repositoryClient : repositoryClients) {
			if (repositoryMap.containsKey(repositoryClient.getKind())) {
				for (TaskRepository repository : repositoryMap.get(repositoryClient.getKind())) {
					store += repository.getServerUrl().toExternalForm() + STORE_DELIM;
				}
				String prefId = PREF_REPOSITORIES + repositoryClient.getKind();
				MylarTaskListPlugin.getPrefs().setValue(prefId, store);
			}
		}
		
		System.err.println(">>> " + repositoryMap);
		
		for (ITaskRepositoryListener listener : listeners) {
			listener.repositorySetUpdated();
		}
	}

	public List<TaskRepository> getAllRepositories() {
		List<TaskRepository> repositories = new ArrayList<TaskRepository>();
		for (ITaskRepositoryClient repositoryClient : repositoryClients) {
			if (repositoryMap.containsKey(repositoryClient.getKind())) {
				repositories.addAll(repositoryMap.get(repositoryClient.getKind()));
			}
		}
		return repositories;
	}

	public void clearRepositories() {
		repositoryMap.clear();
		saveRepositories();
	}
}
