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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.eclipse.mylar.internal.core.MylarContextManager;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryManager {

	public static final String PROPERTY_TIMEZONE = "timezone";
	
	public static final String PROPERTY_ENCODING = "encoding";
	
	public static final String PROPERTY_VERSION = "version";

	public static final String PROPERTY_DELIM = ":";

	public static final String PREF_REPOSITORIES = "org.eclipse.mylar.tasklist.repositories.";

	private Map<String, AbstractRepositoryConnector> repositoryConnectors = new HashMap<String, AbstractRepositoryConnector>();

	private Map<String, Set<TaskRepository>> repositoryMap = new HashMap<String, Set<TaskRepository>>();

	private Set<ITaskRepositoryListener> listeners = new HashSet<ITaskRepositoryListener>();

	public static final String MESSAGE_NO_REPOSITORY = "No repository available, please add one using the Task Repositories view.";

	public static final String PREFIX_LOCAL_OLD = "task-";

	public static final String PREFIX_LOCAL = "local-";

	public static final String PREFIX_REPOSITORY_OLD = "Bugzilla";

	public static final String MISSING_REPOSITORY_HANDLE = "norepository" + MylarContextManager.CONTEXT_HANDLE_DELIM;

	private static final String PREF_STORE_DELIM = ", ";

	public Collection<AbstractRepositoryConnector> getRepositoryConnectors() {
		return Collections.unmodifiableCollection(repositoryConnectors.values());
	}

	public AbstractRepositoryConnector getRepositoryConnector(String kind) {
		return repositoryConnectors.get(kind);
	}

	public void addRepositoryConnector(AbstractRepositoryConnector repositoryConnector) {
		if (!repositoryConnectors.values().contains(repositoryConnector)) {
			repositoryConnectors.put(repositoryConnector.getRepositoryType(), repositoryConnector);
		}
	}

	public void removeRepositoryConnector(AbstractRepositoryConnector repositoryConnector) {
		repositoryConnectors.remove(repositoryConnector);
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
		for (ITaskRepositoryListener listener : listeners) {
			listener.repositoryAdded(repository);
		}
	}

	public void removeRepository(TaskRepository repository) {
		Set<TaskRepository> repositories = repositoryMap.get(repository.getKind());
		if (repositories != null) {
			repository.flushAuthenticationCredentials();
			repositories.remove(repository);
		}
		saveRepositories();
		for (ITaskRepositoryListener listener : listeners) {
			listener.repositoryRemoved(repository);
		}
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
				if (repository.getUrl().equals(urlString)) {
					return repository;
				}
			}
		}
		return null;
	}
	
	/**
	 * @return	the first connector to accept the URL
	 */
	public AbstractRepositoryConnector getRepositoryForTaskUrl(String url) {
		for (AbstractRepositoryConnector connector : getRepositoryConnectors()) {
			if (connector.getRepositoryUrlFromTaskUrl(url) != null) {
				return connector;
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
		for (AbstractRepositoryConnector repositoryConnector : repositoryConnectors.values()) {
			if (repositoryMap.containsKey(repositoryConnector.getRepositoryType())) {
				repositories.addAll(repositoryMap.get(repositoryConnector.getRepositoryType()));
			}
		}
		return repositories;
	}

	public TaskRepository getRepositoryForActiveTask(String repositoryKind) {
		List<ITask> activeTasks = MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks();
		if (activeTasks.size() == 1) {
			ITask activeTask = activeTasks.get(0);
			if (activeTask instanceof AbstractRepositoryTask) {
				String repositoryUrl = AbstractRepositoryTask.getRepositoryUrl(activeTask.getHandleIdentifier());
				for (TaskRepository repository : getRepositories(repositoryKind)) {
					if (repository.getUrl().equals(repositoryUrl)) {
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
		for (AbstractRepositoryConnector repositoryConnector : repositoryConnectors.values()) {
			String read = MylarTaskListPlugin.getMylarCorePrefs().getString(PREF_REPOSITORIES + repositoryConnector.getRepositoryType());
			Set<TaskRepository> repositories = new HashSet<TaskRepository>();
			if (read != null) {
				StringTokenizer st = new StringTokenizer(read, PREF_STORE_DELIM);
				while (st.hasMoreTokens()) {
					String urlString = st.nextToken();

					repositoryMap.put(repositoryConnector.getRepositoryType(), repositories);						
					String prefIdVersion = urlString + PROPERTY_DELIM + PROPERTY_VERSION;
					String version = MylarTaskListPlugin.getMylarCorePrefs().getString(prefIdVersion);					
					
					String prefIdEncoding = urlString + PROPERTY_DELIM + PROPERTY_ENCODING;
					String encoding = MylarTaskListPlugin.getMylarCorePrefs().getString(prefIdEncoding);
					if(encoding.equals("")) {
						encoding = TaskRepository.DEFAULT_CHARACTER_ENCODING;
					}
					
					String prefIdTimeZoneId = urlString + PROPERTY_DELIM + PROPERTY_TIMEZONE;
					String timeZoneId = MylarTaskListPlugin.getMylarCorePrefs().getString(prefIdTimeZoneId);
					if(timeZoneId.equals("")) {
						timeZoneId = TimeZone.getDefault().getID();
					}
					
					repositories.add(new TaskRepository(repositoryConnector.getRepositoryType(), urlString, version, encoding, timeZoneId));
				}
			}
		}
		for (ITaskRepositoryListener listener : listeners) {
			listener.repositoriesRead();
		}
		return repositoryMap;
	}

	/**
	 * for testing purposes
	 */
	public void setVersion(TaskRepository repository, String version) {
		repository.setVersion(version);
		saveRepositories();
	}
	
	/**
	 * for testing purposes
	 */
	public void setEncoding(TaskRepository repository, String encoding) {
		repository.setCharacterEncoding(encoding);
		saveRepositories();
	}
	
	/**
	 * for testing purposes
	 */
	public void setTimeZoneId(TaskRepository repository, String timeZoneId) {
		repository.setTimeZoneId(timeZoneId);
		saveRepositories();
	}
	
	private void saveRepositories() {
		for (AbstractRepositoryConnector repositoryConnector : repositoryConnectors.values()) {
			if (repositoryMap.containsKey(repositoryConnector.getRepositoryType())) {
				String repositoriesToStore = "";
				for (TaskRepository repository : repositoryMap.get(repositoryConnector.getRepositoryType())) {
					repositoriesToStore += repository.getUrl() + PREF_STORE_DELIM;

					String prefIdVersion = repository.getUrl() + PROPERTY_DELIM + PROPERTY_VERSION;					
					MylarTaskListPlugin.getMylarCorePrefs().setValue(prefIdVersion, repository.getVersion());
					
					String prefIdEncoding = repository.getUrl() + PROPERTY_DELIM + PROPERTY_ENCODING;
					MylarTaskListPlugin.getMylarCorePrefs().setValue(prefIdEncoding, repository.getCharacterEncoding());
					
					String prefIdTimeZoneId = repository.getUrl() + PROPERTY_DELIM + PROPERTY_TIMEZONE;
					MylarTaskListPlugin.getMylarCorePrefs().setValue(prefIdTimeZoneId, repository.getTimeZoneId());
				}
				String prefId = PREF_REPOSITORIES + repositoryConnector.getRepositoryType();
				MylarTaskListPlugin.getMylarCorePrefs().setValue(prefId, repositoriesToStore);
			} 
		}
	}
	
	/**
	 * For testing.
	 */
	public void clearRepositories() {
		repositoryMap.clear();
		for (AbstractRepositoryConnector repositoryConnector : repositoryConnectors.values()) {
			String prefId = PREF_REPOSITORIES + repositoryConnector.getRepositoryType();
			MylarTaskListPlugin.getMylarCorePrefs().setValue(prefId, "");
		}
	}
}
