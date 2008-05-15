/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Provides facilities for managing the lifecycle of and access to task repositories.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Jevgeni Holodkov
 * @since 3.0
 */
public class TaskRepositoryManager implements ITaskRepositoryManager {

	public static final String OLD_REPOSITORIES_FILE = "repositories.xml";

	public static final String DEFAULT_REPOSITORIES_FILE = "repositories.xml.zip";

	public static final String PREF_REPOSITORIES = "org.eclipse.mylyn.tasklist.repositories.";

	private final Map<String, AbstractRepositoryConnector> repositoryConnectors = new HashMap<String, AbstractRepositoryConnector>();

	private final Map<String, Set<TaskRepository>> repositoryMap = new HashMap<String, Set<TaskRepository>>();

	private final Set<ITaskRepositoryListener> listeners = new HashSet<ITaskRepositoryListener>();

	private final Set<TaskRepository> orphanedRepositories = new HashSet<TaskRepository>();

	public static final String MESSAGE_NO_REPOSITORY = "No repository available, please add one using the Task Repositories view.";

	public static final String PREFIX_LOCAL = "local-";

	private final TaskRepositoriesExternalizer externalizer = new TaskRepositoriesExternalizer();

	public TaskRepositoryManager() {
	}

	public Collection<AbstractRepositoryConnector> getRepositoryConnectors() {
		return Collections.unmodifiableCollection(repositoryConnectors.values());
	}

	public AbstractRepositoryConnector getRepositoryConnector(String connectorKind) {
		return repositoryConnectors.get(connectorKind);
	}

	public void addRepositoryConnector(AbstractRepositoryConnector repositoryConnector) {
		if (!repositoryConnectors.values().contains(repositoryConnector)) {
			repositoryConnectors.put(repositoryConnector.getConnectorKind(), repositoryConnector);
		}
	}

	public boolean hasUserManagedRepositoryConnectors() {
		for (AbstractRepositoryConnector connector : repositoryConnectors.values()) {
			if (connector.isUserManaged()) {
				return true;
			}
		}
		return false;
	}

	public void addRepository(TaskRepository repository) {
		Set<TaskRepository> repositories;
		if (!repositoryMap.containsKey(repository.getConnectorKind())) {
			repositories = new HashSet<TaskRepository>();
			repositoryMap.put(repository.getConnectorKind(), repositories);
		} else {
			repositories = repositoryMap.get(repository.getConnectorKind());
		}
		repositories.add(repository);
		for (ITaskRepositoryListener listener : listeners) {
			listener.repositoryAdded(repository);
		}
	}

	public void removeRepository(TaskRepository repository, String repositoryFilePath) {
		Set<TaskRepository> repositories = repositoryMap.get(repository.getConnectorKind());
		if (repositories != null) {
			repository.flushAuthenticationCredentials();
			repositories.remove(repository);
		}
		saveRepositories(repositoryFilePath);
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

	/* Public for testing. */
	public static String stripSlashes(String url) {
		Assert.isNotNull(url);
		StringBuilder sb = new StringBuilder(url.trim());
		while (sb.length() > 0 && sb.charAt(sb.length() - 1) == '/') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public TaskRepository getRepository(String kind, String urlString) {
		Assert.isNotNull(kind);
		Assert.isNotNull(urlString);
		urlString = stripSlashes(urlString);
		if (repositoryMap.containsKey(kind)) {
			for (TaskRepository repository : repositoryMap.get(kind)) {
				if (stripSlashes(repository.getRepositoryUrl()).equals(urlString)) {
					return repository;
				}
			}
		}
		return null;
	}

	/**
	 * @return first repository that matches the given url
	 */
	public TaskRepository getRepository(String urlString) {
		Assert.isNotNull(urlString);
		urlString = stripSlashes(urlString);
		for (String kind : repositoryMap.keySet()) {
			for (TaskRepository repository : repositoryMap.get(kind)) {
				if (stripSlashes(repository.getRepositoryUrl()).equals(urlString)) {
					return repository;
				}
			}
		}
		return null;
	}

	/**
	 * @return the first connector to accept the URL
	 */
	public AbstractRepositoryConnector getConnectorForRepositoryTaskUrl(String url) {
		Assert.isNotNull(url);
		for (AbstractRepositoryConnector connector : getRepositoryConnectors()) {
			if (connector.getRepositoryUrlFromTaskUrl(url) != null) {
				for (TaskRepository repository : getRepositories(connector.getConnectorKind())) {
					if (url.startsWith(repository.getRepositoryUrl())) {
						return connector;
					}
				}
			}
		}
		return null;
	}

	public Set<TaskRepository> getRepositories(String kind) {
		Assert.isNotNull(kind);
		if (repositoryMap.containsKey(kind)) {
			return repositoryMap.get(kind);
		} else {
			return Collections.emptySet();
		}
	}

	public List<TaskRepository> getAllRepositories() {
		List<TaskRepository> repositories = new ArrayList<TaskRepository>();
		for (AbstractRepositoryConnector repositoryConnector : repositoryConnectors.values()) {
			if (repositoryMap.containsKey(repositoryConnector.getConnectorKind())) {
				repositories.addAll(repositoryMap.get(repositoryConnector.getConnectorKind()));
			}
		}
		return repositories;
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
			Collection<Set<TaskRepository>> values = repositoryMap.values();
			if (!values.isEmpty()) {
				Set<TaskRepository> repoistorySet = values.iterator().next();
				return repoistorySet.iterator().next();
			}
		}
		return null;
	}

	protected Map<String, Set<TaskRepository>> readRepositories(String repositoriesFilePath) {

		repositoryMap.clear();
		orphanedRepositories.clear();

		loadRepositories(repositoriesFilePath);

		for (ITaskRepositoryListener listener : listeners) {
			try {
				listener.repositoriesRead();
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Repository listener failed", t));
			}
		}
		return repositoryMap;
	}

	private void loadRepositories(String repositoriesFilePath) {
		boolean migration = false;
		// String dataDirectory =
		// TasksUiPlugin.getDefault().getDataDirectory();
		File repositoriesFile = new File(repositoriesFilePath);

		// Will only load repositories for which a connector exists
		for (AbstractRepositoryConnector repositoryConnector : repositoryConnectors.values()) {
			repositoryMap.put(repositoryConnector.getConnectorKind(), new HashSet<TaskRepository>());
		}
		if (repositoriesFile.exists()) {
			Set<TaskRepository> repositories = externalizer.readRepositoriesFromXML(repositoriesFile);
			if (repositories != null && repositories.size() > 0) {
				for (TaskRepository repository : repositories) {

					if (removeHttpAuthMigration(repository)) {
						migration = true;
					}

					if (migrateAnonymousRepository(repository)) {
						migration = true;
					}

					if (repositoryMap.containsKey(repository.getConnectorKind())) {
						repositoryMap.get(repository.getConnectorKind()).add(repository);
					} else {
						orphanedRepositories.add(repository);
					}
				}
			}
			if (migration) {
				saveRepositories(repositoriesFilePath);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private boolean removeHttpAuthMigration(TaskRepository repository) {
		String httpusername = repository.getProperty(TaskRepository.AUTH_HTTP_USERNAME);
		String httppassword = repository.getProperty(TaskRepository.AUTH_HTTP_PASSWORD);
		if (httpusername != null && httppassword != null) {
			repository.removeProperty(TaskRepository.AUTH_HTTP_USERNAME);
			repository.removeProperty(TaskRepository.AUTH_HTTP_PASSWORD);
			if (httpusername.length() > 0 && httppassword.length() > 0) {
				repository.setHttpAuthenticationCredentials(httpusername, httppassword);
			}
			return true;
		}
		return false;
	}

	// Migration 2.0M1 - 2.0M2
	private boolean migrateAnonymousRepository(TaskRepository repository) {
		if (repository.getProperty(TaskRepository.ANONYMOUS_LOGIN) == null) {
			if ((repository.getUserName() == null || repository.getPassword() == null)
					|| ("".equals(repository.getUserName()) && "".equals(repository.getPassword()))) {
				repository.setAnonymous(true);
			} else {
				repository.setAnonymous(false);
			}
			return true;
		}
		return false;
	}

	@Deprecated
	public void setSynchronizationTime(TaskRepository repository, String syncTime, String repositoriesFilePath) {
		repository.setSynchronizationTimeStamp(syncTime);
		saveRepositories(repositoriesFilePath);
	}

	protected synchronized boolean saveRepositories(String destinationPath) {
//		if (!Platform.isRunning()) {// || TasksUiPlugin.getDefault() == null) {
//			return false;
//		}
		Set<TaskRepository> repositoriesToWrite = new HashSet<TaskRepository>(getAllRepositories());
		// if for some reason a repository is added/changed to equal one in the
		// orphaned set the orphan is discarded
		for (TaskRepository repository : orphanedRepositories) {
			if (!repositoriesToWrite.contains(repository)) {
				repositoriesToWrite.add(repository);
			}
		}

		try {
			File repositoriesFile = new File(destinationPath);
			externalizer.writeRepositoriesToXML(repositoriesToWrite, repositoriesFile);
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Could not save repositories", t));
			return false;
		}
		return true;
	}

	/**
	 * For testing.
	 */
	public void clearRepositories(String repositoriesFilePath) {
		repositoryMap.clear();
		orphanedRepositories.clear();
		saveRepositories(repositoriesFilePath);
	}

	public void notifyRepositorySettingsChanged(TaskRepository repository) {
		for (ITaskRepositoryListener listener : listeners) {
			listener.repositorySettingsChanged(repository);
		}
	}

	public void insertRepositories(Set<TaskRepository> repositories, String repositoryFilePath) {
		for (TaskRepository repository : repositories) {
			if (getRepository(repository.getConnectorKind(), repository.getRepositoryUrl()) == null) {
				addRepository(repository);
			}
		}
	}

	public boolean isOwnedByUser(ITask task) {
		if (task instanceof LocalTask) {
			return true;
		}

		ITask repositoryTask = task;
		TaskRepository repository = getRepository(repositoryTask.getConnectorKind(), repositoryTask.getRepositoryUrl());
		if (repository != null && repositoryTask.getOwner() != null) {
			return repositoryTask.getOwner().equals(repository.getUserName());
		}

		return false;
	}

	/**
	 * @param repository
	 * 		with new url
	 * @param oldUrl
	 * 		previous url for this repository
	 */
	public void notifyRepositoryUrlChanged(TaskRepository repository, String oldUrl) {
		for (ITaskRepositoryListener listener : listeners) {
			listener.repositoryUrlChanged(repository, oldUrl);
		}
	}

}
