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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.mylar.core.internal.MylarContextManager;
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

	public static final String PREFIX_LOCAL_OLD = "task-";
	
	public static final String PREFIX_LOCAL = "local-";
	
	public static final String HANDLE_DELIM = "-";


	public static final String PREFIX_REPOSITORY_OLD = "Bugzilla";
	public static final String MISSING_REPOSITORY_HANDLE = PREFIX_REPOSITORY_OLD
			+ MylarContextManager.CONTEXT_HANDLE_DELIM;

	private static final String PREF_STORE_DELIM = ", ";

	public List<ITaskRepositoryClient> getRepositoryClients() {
		return repositoryClients;
	}

	public void addRepositoryClient(ITaskRepositoryClient repositoryClient) {
		if (!repositoryClients.contains(repositoryClient)) {
			repositoryClients.add(repositoryClient);
		}
	}

	public void removeRepositoryClient(ITaskRepositoryClient repositoryClient) {
		repositoryClients.remove(repositoryClient);
	}

	public void addRepository(TaskRepository repository) {
		Set<TaskRepository> repositories;
		if (!repositoryMap.containsKey(repository.getKind())) {
			repositories = new HashSet<TaskRepository>();
			repositoryMap.put(repository.getKind(), repositories);
		} else {
			repositories = repositoryMap.get(repository.getKind());
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

	public TaskRepository getRepository(String kind, String urlString) {
		if (repositoryMap.containsKey(kind)) {
			for (TaskRepository repository : repositoryMap.get(kind)) {
				if (repository.getUrl().toExternalForm().equals(urlString)) {
					return repository;
				}
			}
		}
		return null;
	}

	public Set<TaskRepository> getRepositories(String kind) {
		if (repositoryMap.containsKey(kind)) {
			return repositoryMap.get(kind);
		} else {
			return Collections.emptySet();
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

	@Deprecated
	public TaskRepository getDefaultRepository(String kind) {
		// HACK: returns first repository found
		if (repositoryMap.containsKey(kind)) {
			for (TaskRepository repository : repositoryMap.get(kind)) {
				return repository;
			}
		}
		return null;
	}

	public Map<String, Set<TaskRepository>> readRepositories() {
		for (ITaskRepositoryClient repositoryClient : repositoryClients) {
			String read = MylarTaskListPlugin.getPrefs().getString(PREF_REPOSITORIES + repositoryClient.getKind());
			Set<TaskRepository> repositories = new HashSet<TaskRepository>();
			if (read != null) {
				StringTokenizer st = new StringTokenizer(read, PREF_STORE_DELIM);
				while (st.hasMoreTokens()) {
					String urlString = st.nextToken();
					try {
						URL url = new URL(urlString);
						repositoryMap.put(repositoryClient.getKind(), repositories);
						repositories.add(new TaskRepository(repositoryClient.getKind(), url));
					} catch (MalformedURLException e) {
						MylarStatusHandler.fail(e, "could not restore URL: " + urlString, false);
					}
				}
			}
		}
		for (ITaskRepositoryListener listener : listeners) {
			listener.repositorySetUpdated();
		}
		System.err.println("> read repositories: " + repositoryMap);
		return repositoryMap;
	}

	private void saveRepositories() {
		String store = "";
		for (ITaskRepositoryClient repositoryClient : repositoryClients) {
			if (repositoryMap.containsKey(repositoryClient.getKind())) {
				for (TaskRepository repository : repositoryMap.get(repositoryClient.getKind())) {
					store += repository.getUrl().toExternalForm() + PREF_STORE_DELIM;
				}
				String prefId = PREF_REPOSITORIES + repositoryClient.getKind();
				MylarTaskListPlugin.getPrefs().setValue(prefId, store);
			}
		}

		for (ITaskRepositoryListener listener : listeners) {
			listener.repositorySetUpdated();
		}
	}

	public void clearRepositories() {
		repositoryMap.clear();
		saveRepositories();
	}

	public static String getTaskId(String taskHandle) {
		int index = taskHandle.lastIndexOf(HANDLE_DELIM);
		if (index != -1) {
			String id = taskHandle.substring(index + 1);
			return id;
		}
		return null;
	}

	public static String getRepositoryUrl(String contextHandle) {
		int index = contextHandle.lastIndexOf(HANDLE_DELIM);
		String url = null;
		if (index != -1) {
			url = contextHandle.substring(0, index);
		}
		if (url != null && url.equals(TaskRepositoryManager.PREFIX_REPOSITORY_OLD)) {
			String repositoryKind = TaskRepositoryManager.PREFIX_REPOSITORY_OLD.toLowerCase();
			TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getDefaultRepository(repositoryKind);
			if (repository != null) {
				url = repository.getUrl().toExternalForm();
			}
		}
		return url;
	}

	public static int getTaskIdAsInt(String taskHandle) {
		String idString = getTaskId(taskHandle);
		if (idString != null) {
			return Integer.parseInt(idString);
		} else {
			return -1;
		}
	}

	public static String getHandle(String repositoryUrl, String taskId) {
		if (repositoryUrl == null) {
			return MISSING_REPOSITORY_HANDLE + taskId;
		} else {
			return repositoryUrl + MylarContextManager.CONTEXT_HANDLE_DELIM + taskId;
		}
	}

	public static String getHandle(String repositoryUrl, int taskId) {
		return getHandle(repositoryUrl, "" + taskId);
	}
}
