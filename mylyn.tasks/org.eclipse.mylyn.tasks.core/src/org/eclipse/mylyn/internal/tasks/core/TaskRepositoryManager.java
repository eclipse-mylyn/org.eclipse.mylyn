/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jevgeni Holodkov - improvements
 *     Atlassian - improvements for bug 319397
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta.Type;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryMigrator;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Provides facilities for managing the life-cycle of and access to task repositories.
 *
 * @author Mik Kersten
 * @author Rob Elves
 * @author Jevgeni Holodkov
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskRepositoryManager implements IRepositoryManager {

	public static final String OLD_REPOSITORIES_FILE = "repositories.xml"; //$NON-NLS-1$

	public static final String DEFAULT_REPOSITORIES_FILE = "repositories.xml.zip"; //$NON-NLS-1$

	public static final String PREF_REPOSITORIES = "org.eclipse.mylyn.tasklist.repositories."; //$NON-NLS-1$

	private final Map<String, AbstractRepositoryConnector> repositoryConnectors = new HashMap<String, AbstractRepositoryConnector>();

	// connector kinds to corresponding repositories
	private final Map<String, Set<TaskRepository>> repositoryMap = new HashMap<String, Set<TaskRepository>>();

	private final Set<IRepositoryListener> listeners = new CopyOnWriteArraySet<IRepositoryListener>();

	private final Set<TaskRepository> orphanedRepositories = new HashSet<TaskRepository>();

	public static final String PREFIX_LOCAL = "local-"; //$NON-NLS-1$

	private static final Map<String, Category> repositoryCategories = new HashMap<String, Category>();

	private final PropertyChangeListener PROPERTY_CHANGE_LISTENER = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			TaskRepositoryManager.this.notifyRepositorySettingsChanged((TaskRepository) evt.getSource(),
					new TaskRepositoryDelta(Type.PROPERTY, evt.getPropertyName()));
		}
	};

	private final TaskRepositoriesExternalizer externalizer = new TaskRepositoriesExternalizer();

	private List<AbstractRepositoryMigrator> migrators;

	public TaskRepositoryManager() {
		this.migrators = Collections.emptyList();
		Category catTasks = new Category(TaskRepository.CATEGORY_TASKS, "Tasks", 0); //$NON-NLS-1$
		repositoryCategories.put(catTasks.getId(), catTasks);
		Category catBugs = new Category(TaskRepository.CATEGORY_BUGS, "Bugs", 100); //$NON-NLS-1$
		repositoryCategories.put(catBugs.getId(), catBugs);
		Category catBuild = new Category(TaskRepository.CATEGORY_BUILD, "Builds", 200); //$NON-NLS-1$
		repositoryCategories.put(catBuild.getId(), catBuild);
		Category catReview = new Category(TaskRepository.CATEGORY_REVIEW, "Reviews", 300); //$NON-NLS-1$
		repositoryCategories.put(catReview.getId(), catReview);
		Category catOther = new Category(IRepositoryConstants.CATEGORY_OTHER, "Other", 400); //$NON-NLS-1$
		repositoryCategories.put(catOther.getId(), catOther);
	}

	public synchronized Collection<AbstractRepositoryConnector> getRepositoryConnectors() {
		return new ArrayList<AbstractRepositoryConnector>(repositoryConnectors.values());
	}

	public synchronized AbstractRepositoryConnector getRepositoryConnector(String connectorKind) {
		return repositoryConnectors.get(connectorKind);
	}

	public synchronized AbstractRepositoryConnector removeRepositoryConnector(String connectorKind) {
		return repositoryConnectors.remove(connectorKind);
	}

	public synchronized void addRepositoryConnector(AbstractRepositoryConnector repositoryConnector) {
		if (!repositoryConnectors.values().contains(repositoryConnector)) {
			repositoryConnectors.put(repositoryConnector.getConnectorKind(), repositoryConnector);
		}
	}

	public synchronized boolean hasUserManagedRepositoryConnectors() {
		for (AbstractRepositoryConnector connector : repositoryConnectors.values()) {
			if (connector.isUserManaged()) {
				return true;
			}
		}
		return false;
	}

	public void addRepository(final TaskRepository repository) {
		synchronized (this) {
			Set<TaskRepository> repositories;
			repositories = repositoryMap.get(repository.getConnectorKind());
			if (repositories == null) {
				repositories = new HashSet<TaskRepository>();
				repositoryMap.put(repository.getConnectorKind(), repositories);
			}

			if (!repositories.add(repository)) {
				// TODO 4.0 return false to indicate that remove was unsuccessful
				return;
			}

			repository.addChangeListener(PROPERTY_CHANGE_LISTENER);
		}

		for (final IRepositoryListener listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				public void run() throws Exception {
					listener.repositoryAdded(repository);
				}
			});
		}
	}

	@Deprecated
	public void removeRepository(final TaskRepository repository, String repositoryFilePath) {
		removeRepository(repository);
	}

	public void removeRepository(final TaskRepository repository) {
		synchronized (this) {
			Set<TaskRepository> repositories = repositoryMap.get(repository.getConnectorKind());
			if (repositories == null || !repositories.remove(repository)) {
				// TODO 4.0 return false to indicate that remove was unsuccessful
				return;
			}
			repository.flushAuthenticationCredentials();
			repository.removeChangeListener(PROPERTY_CHANGE_LISTENER);
		}
		for (final IRepositoryListener listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				public void run() throws Exception {
					listener.repositoryRemoved(repository);
				}
			});
		}
	}

	public void addListener(IRepositoryListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IRepositoryListener listener) {
		listeners.remove(listener);
	}

	/* Public for testing. */
	public static String stripSlashes(String url) {
		Assert.isNotNull(url);
		StringBuilder sb = new StringBuilder(url.trim());
		while (sb.length() > 0 && sb.charAt(sb.length() - 1) == '/') {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	public Category getCategory(String id) {
		Category category = repositoryCategories.get(IRepositoryConstants.CATEGORY_OTHER);
		Category cat = repositoryCategories.get(id);
		if (cat != null) {
			category = cat;
		}
		return category;
	}

	public Collection<Category> getCategories() {
		return Collections.unmodifiableCollection(repositoryCategories.values());
	}

	public TaskRepository getRepository(String kind, String urlString) {
		Assert.isNotNull(kind);
		Assert.isNotNull(urlString);
		urlString = stripSlashes(urlString);
		synchronized (this) {
			if (repositoryMap.containsKey(kind)) {
				for (TaskRepository repository : repositoryMap.get(kind)) {
					if (stripSlashes(repository.getRepositoryUrl()).equals(urlString)) {
						return repository;
					}
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
		synchronized (this) {
			for (String kind : repositoryMap.keySet()) {
				for (TaskRepository repository : repositoryMap.get(kind)) {
					if (stripSlashes(repository.getRepositoryUrl()).equals(urlString)) {
						return repository;
					}
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
			String repositoryUrl = connector.getRepositoryUrlFromTaskUrl(url);
			if (repositoryUrl != null) {
				for (TaskRepository repository : getRepositories(connector.getConnectorKind())) {
					if (repositoryUrl.startsWith(repository.getRepositoryUrl())) {
						return connector;
					}
				}
			}
		}
		return null;
	}

	public Set<TaskRepository> getRepositories(String connectorKind) {
		Assert.isNotNull(connectorKind);
		Set<TaskRepository> result;
		synchronized (this) {
			result = repositoryMap.get(connectorKind);
		}
		if (result == null) {
			return Collections.emptySet();
		}
		return new HashSet<TaskRepository>(result);
	}

	public List<TaskRepository> getAllRepositories() {
		List<TaskRepository> repositories = new ArrayList<TaskRepository>();
		synchronized (this) {
			for (AbstractRepositoryConnector repositoryConnector : repositoryConnectors.values()) {
				if (repositoryMap.containsKey(repositoryConnector.getConnectorKind())) {
					repositories.addAll(repositoryMap.get(repositoryConnector.getConnectorKind()));
				}
			}
		}
		return repositories;
	}

	@Deprecated
	public synchronized TaskRepository getDefaultRepository(String kind) {
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

	Map<String, Set<TaskRepository>> readRepositories(String repositoriesFilePath) {

		repositoryMap.clear();
		orphanedRepositories.clear();

		loadRepositories(repositoriesFilePath);

//		for (IRepositoryListener listener : listeners) {
//			try {
//				listener.repositoriesRead();
//			} catch (Throwable t) {
//				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
//						"Repository listener failed", t));
//			}
//		}
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
					if (applyMigrators(repository)) {
						migration = true;
					}
					if (repositoryMap.containsKey(repository.getConnectorKind())) {
						repositoryMap.get(repository.getConnectorKind()).add(repository);

						repository.addChangeListener(PROPERTY_CHANGE_LISTENER);

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

	public boolean applyMigrators(final TaskRepository repository) {
		final boolean[] result = new boolean[1];
		for (AbstractRepositoryMigrator migrator : migrators) {
			if (migrator.getConnectorKind().equals(repository.getConnectorKind())) {

				final AbstractRepositoryMigrator finalRepositoryMigrator = migrator;
				result[0] = false;
				SafeRunner.run(new ISafeRunnable() {

					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
								"Repository migration failed for repository \"" + repository.getUrl() + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
					}

					public void run() throws Exception {
						if (finalRepositoryMigrator.migrateRepository(repository)) {
							result[0] = true;
						}
					}

				});
				break;
			}
		}
		return result[0];
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
			StatusHandler
					.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Could not save repositories", t)); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	@Deprecated
	public void clearRepositories(String repositoriesFilePath) {
		clearRepositories();
	}

	/**
	 * For testing.
	 */
	public void clearRepositories() {
		List<TaskRepository> repositories = getAllRepositories();
		for (TaskRepository repository : repositories) {
			removeRepository(repository);
		}
		synchronized (this) {
			repositoryMap.clear();
			orphanedRepositories.clear();
		}
	}

	/*
	 * only used for testing
	 */
	public void notifyRepositorySettingsChanged(final TaskRepository repository) {
		notifyRepositorySettingsChanged(repository, new TaskRepositoryDelta(Type.ALL));
	}

	public void notifyRepositorySettingsChanged(final TaskRepository repository, TaskRepositoryDelta delta) {
		final TaskRepositoryChangeEvent event = new TaskRepositoryChangeEvent(this, repository, delta);
		for (final IRepositoryListener listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				public void run() throws Exception {
					if (listener instanceof IRepositoryChangeListener) {
						((IRepositoryChangeListener) listener).repositoryChanged(event);
					}
					listener.repositorySettingsChanged(repository);
				}
			});
		}
	}

	@Deprecated
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

		AbstractRepositoryConnector connector = getRepositoryConnector(task.getConnectorKind());
		if (connector != null) {
			TaskRepository repository = getRepository(task.getConnectorKind(), task.getRepositoryUrl());
			if (repository != null) {
				return connector.isOwnedByUser(repository, task);
			}
		}
		return false;
	}

	/**
	 * @param repository
	 *            with new url
	 * @param oldUrl
	 *            previous url for this repository
	 */
	public void notifyRepositoryUrlChanged(final TaskRepository repository, final String oldUrl) {
		for (final IRepositoryListener listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				public void run() throws Exception {
					listener.repositoryUrlChanged(repository, oldUrl);
				}
			});
		}
	}

	public Category getCategory(TaskRepository repository) {
		return getCategory(repository.getCategory());
	}

	public void initialize(List<AbstractRepositoryMigrator> repositoryMigrators) {
		this.migrators = repositoryMigrators;

	}
}
