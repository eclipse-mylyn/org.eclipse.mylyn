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

package org.eclipse.mylar.provisional.tasklist;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryManager {

	public static final String PREF_REPOSITORIES = "org.eclipse.mylar.tasklist.repositories.";

	private Map<String, AbstractRepositoryClient> repositoryClients = new HashMap<String, AbstractRepositoryClient>();

	private Map<String, Set<TaskRepository>> repositoryMap = new HashMap<String, Set<TaskRepository>>();

	private Set<ITaskRepositoryListener> listeners = new HashSet<ITaskRepositoryListener>();

	public static final String MESSAGE_NO_REPOSITORY = "No repository available, please add one using the Task Repositories view.";

	public static final String PREFIX_LOCAL_OLD = "task-";

	public static final String PREFIX_LOCAL = "local-";

	public static final String PREFIX_REPOSITORY_OLD = "Bugzilla";

	public static final String MISSING_REPOSITORY_HANDLE = "norepository" + MylarContextManager.CONTEXT_HANDLE_DELIM;

	private static final String PREF_STORE_DELIM = ", ";

	public Collection<AbstractRepositoryClient> getRepositoryClients() {
		return Collections.unmodifiableCollection(repositoryClients.values());
	}

	public AbstractRepositoryClient getRepositoryClient(String kind) {
		return repositoryClients.get(kind);
	}

	public void addRepositoryClient(AbstractRepositoryClient repositoryClient) {
		if (!repositoryClients.values().contains(repositoryClient)) {
			repositoryClients.put(repositoryClient.getKind(), repositoryClient);
		}
	}

	public void removeRepositoryClient(AbstractRepositoryClient repositoryClient) {
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
		for (AbstractRepositoryClient repositoryClient : repositoryClients.values()) {
			if (repositoryMap.containsKey(repositoryClient.getKind())) {
				repositories.addAll(repositoryMap.get(repositoryClient.getKind()));
			}
		}
		return repositories;
	}

	public TaskRepository getRepositoryForActiveTask(String repositoryKind) {
		List<ITask> activeTasks = MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks();
		if (activeTasks.size() == 1) {
			ITask activeTask = activeTasks.get(0);
			if (!activeTask.isLocal()) {
				String repositoryUrl = AbstractRepositoryTask.getRepositoryUrl(activeTask.getHandleIdentifier());
				for (TaskRepository repository : getRepositories(repositoryKind)) {
					if (repository.getUrl().toExternalForm().equals(repositoryUrl)) {
						return repository;
					}
				}
			}
		}
		return null;
	}

	/**
	 * TODO: implement default support, this just returns first found
	 */
	public TaskRepository getDefaultRepository(String kind) {
		// HACK: returns first repository found
		if (repositoryMap.containsKey(kind)) {
			for (TaskRepository repository : repositoryMap.get(kind)) {
				return repository;
			}
		} else {
			Collection values = repositoryMap.values();
			if (!values.isEmpty()) {
				HashSet repoistorySet = (HashSet) values.iterator().next();
				return (TaskRepository) repoistorySet.iterator().next();
			}
		}
		return null;
	}

	public Map<String, Set<TaskRepository>> readRepositories() {
		for (AbstractRepositoryClient repositoryClient : repositoryClients.values()) {
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
		return repositoryMap;
	}

	private void saveRepositories() {
		for (AbstractRepositoryClient repositoryClient : repositoryClients.values()) {
			if (repositoryMap.containsKey(repositoryClient.getKind())) {
				String repositoriesToStore = "";
				for (TaskRepository repository : repositoryMap.get(repositoryClient.getKind())) {
					repositoriesToStore += repository.getUrl().toExternalForm() + PREF_STORE_DELIM;
				}
				String prefId = PREF_REPOSITORIES + repositoryClient.getKind();
				MylarTaskListPlugin.getPrefs().setValue(prefId, repositoriesToStore);
			} 
		}

		for (ITaskRepositoryListener listener : listeners) {
			listener.repositorySetUpdated();
		}
	}
	
	/**
	 * For testing.
	 */
	public void clearRepositories() {
		repositoryMap.clear();
		for (AbstractRepositoryClient repositoryClient : repositoryClients.values()) {
			String prefId = PREF_REPOSITORIES + repositoryClient.getKind();
			MylarTaskListPlugin.getPrefs().setValue(prefId, "");
		}
	}
}
