/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizer;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryMigrator;
import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.spi.RepositoryConnectorContributor;
import org.eclipse.mylyn.tasks.core.spi.RepositoryConnectorDescriptor;
import org.eclipse.osgi.util.NLS;

public class RepositoryConnectorExtensionReader {

	public static final String ELMNT_MIGRATOR = "taskListMigrator"; //$NON-NLS-1$

	public static final String ELMNT_REPOSITORY_MIGRATOR = "repositoryMigrator"; //$NON-NLS-1$

	public static final String ELMNT_REPOSITORY_CONNECTOR = "connectorCore"; //$NON-NLS-1$

	public static final String ATTR_ID = "id"; //$NON-NLS-1$

	public static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static final String EXTENSION_CONTRIBUTORS = ITasksCoreConstants.ID_PLUGIN
			+ ".repositoryConnectorContributor"; //$NON-NLS-1$

	private static class ConnectorFactory {

		private AbstractRepositoryConnector connector;

		private AbstractTaskListMigrator taskListMigrator;

		private AbstractRepositoryMigrator repositoryMigrator;

		private final RepositoryConnectorDescriptor descriptor;

		private final String pluginId;

		public ConnectorFactory(RepositoryConnectorDescriptor descriptor, String pluginId) {
			Assert.isNotNull(pluginId);
			Assert.isNotNull(pluginId);
			this.descriptor = descriptor;
			this.pluginId = pluginId;
		}

		public String getConnectorKind() {
			return (getConnector() != null) ? getConnector().getConnectorKind() : null;
		}

		public AbstractRepositoryConnector getConnector() {
			return connector;
		}

		public String getPluginId() {
			return pluginId;
		}

		public AbstractTaskListMigrator getTaskListMigrator() {
			return taskListMigrator;
		}

		public AbstractRepositoryMigrator getRepositoryMigrator() {
			return repositoryMigrator;
		}

		public IStatus createConnector() {
			Assert.isTrue(connector == null);
			try {
				connector = descriptor.createRepositoryConnector();
				if (connector != null) {
					return Status.OK_STATUS;
				} else {
					return new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, NLS.bind(
							"Could not load connector core contributed by ''{0}''", getPluginId())); //$NON-NLS-1$
				}
			} catch (Throwable e) {
				return new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, NLS.bind(
						"Could not load connector core contributed by ''{0}''", getPluginId()), e); //$NON-NLS-1$
			}
		}

		public IStatus createTaskListMigrator() {
			try {
				taskListMigrator = descriptor.createTaskListMigrator();
				if (taskListMigrator != null) {
					return Status.OK_STATUS;
				} else {
					return Status.CANCEL_STATUS;
				}
			} catch (Throwable e) {
				return new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, NLS.bind(
						"Could not load task list migrator extension contributed by ''{0}''", getPluginId()), e); //$NON-NLS-1$
			}
		}

		public IStatus createRepositoryMigrator() {
			Assert.isTrue(repositoryMigrator == null);
			try {
				repositoryMigrator = descriptor.createRepositoryMigrator();
				if (repositoryMigrator != null) {
					return Status.OK_STATUS;
				} else {
					return Status.CANCEL_STATUS;
				}
			} catch (Throwable e) {
				return new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, NLS.bind(
						"Could not load repository migrator extension contributed by ''{0}''", getPluginId()), e); //$NON-NLS-1$
			}
		}

	}

	private static class ExtensionPointBasedConnectorDescriptor extends RepositoryConnectorDescriptor {

		IConfigurationElement element;

		IConfigurationElement taskListMigratorElement;

		IConfigurationElement repositoryMigratorElement;

		public ExtensionPointBasedConnectorDescriptor(IConfigurationElement element) {
			this.element = element;
		}

		@Override
		public AbstractRepositoryConnector createRepositoryConnector() {
			try {
				return (AbstractRepositoryConnector) element.createExecutableExtension(ATTR_CLASS);
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public AbstractTaskListMigrator createTaskListMigrator() {
			if (taskListMigratorElement == null) {
				return null;
			}
			try {
				return (AbstractTaskListMigrator) taskListMigratorElement.createExecutableExtension(ATTR_CLASS);
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public AbstractRepositoryMigrator createRepositoryMigrator() {
			if (repositoryMigratorElement == null) {
				return null;
			}
			try {
				return (AbstractRepositoryMigrator) repositoryMigratorElement.createExecutableExtension(ATTR_CLASS);
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}

		public String getPluginId() {
			return element.getContributor().getName();
		}

		public String getId() {
			return element.getAttribute(ATTR_ID);
		}

	}

	/**
	 * Plug-in ids of connector extensions that failed to load.
	 */
	private final Set<String> disabledContributors = new HashSet<String>();

	private final TaskListExternalizer taskListExternalizer;

	private final TaskRepositoryManager repositoryManager;

	private final List<ConnectorFactory> factories = new ArrayList<ConnectorFactory>();

	private MultiStatus result;

	public RepositoryConnectorExtensionReader(TaskListExternalizer taskListExternalizer,
			TaskRepositoryManager repositoryManager) {
		this.taskListExternalizer = taskListExternalizer;
		this.repositoryManager = repositoryManager;
	}

	public void registerConnectors(IExtensionPoint repositoriesExtensionPoint) {
		if (result != null) {
			throw new IllegalStateException("registerConnectors may only be invoked once"); //$NON-NLS-1$
		}

		result = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, 0, "Repository connectors failed to load.", null); //$NON-NLS-1$

		Map<String, List<ConnectorFactory>> factoryById = readFromRepositoriesExtensionPoint(repositoriesExtensionPoint);
		checkForConflicts(factoryById);

		readFromContributorsExtensionPoint();

		Map<String, List<ConnectorFactory>> factoryByConnectorKind = createConnectorInstances();
		checkForConflicts(factoryByConnectorKind);

		registerConnectorInstances();

		if (!result.isOK()) {
			StatusHandler.log(result);
		}
	}

	private void readFromContributorsExtensionPoint() {
		IExtensionPoint repositoriesExtensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
				EXTENSION_CONTRIBUTORS);
		IExtension[] extensions = repositoriesExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				addDescriptorsFromContributor(element);
			}
		}
	}

	private void addDescriptorsFromContributor(final IConfigurationElement element) {
		SafeRunner.run(new ISafeRunnable() {

			@Override
			public void run() throws Exception {
				RepositoryConnectorContributor contributor = (RepositoryConnectorContributor) element.createExecutableExtension(ATTR_CLASS);
				Collection<RepositoryConnectorDescriptor> descriptors = contributor.getDescriptors();
				if (descriptors == null) {
					result.add(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, NLS.bind(
							"Could not load connectors contributed by ''{0}''", element.getContributor().getName()))); //$NON-NLS-1$
					return;
				}
				for (RepositoryConnectorDescriptor descriptor : descriptors) {
					if (descriptor != null) {
						factories.add(new ConnectorFactory(descriptor, element.getContributor().getName()));
					}
				}
			}

			@Override
			public void handleException(Throwable exception) {
				// ignore

			}
		});
	}

	public Map<String, List<ConnectorFactory>> createConnectorInstances() {
		Map<String, List<ConnectorFactory>> factoryByConnectorKind = new LinkedHashMap<String, List<ConnectorFactory>>();
		for (ConnectorFactory descriptor : factories) {
			IStatus status = descriptor.createConnector();
			if (status.isOK() && descriptor.getConnector() != null) {
				add(factoryByConnectorKind, descriptor.getConnectorKind(), descriptor);
			} else {
				result.add(status);
			}
		}
		return factoryByConnectorKind;
	}

	private void registerConnectorInstances() {
		List<AbstractTaskListMigrator> taskListmigrators = new ArrayList<AbstractTaskListMigrator>();
		List<AbstractRepositoryMigrator> repositoryMigrators = new ArrayList<AbstractRepositoryMigrator>();

		for (ConnectorFactory descriptor : factories) {
			if (descriptor.getConnector() != null) {
				repositoryManager.addRepositoryConnector(descriptor.getConnector());

				IStatus status = descriptor.createTaskListMigrator();
				if (status.isOK() && descriptor.getTaskListMigrator() != null) {
					taskListmigrators.add(descriptor.getTaskListMigrator());
				} else if (status.getSeverity() == IStatus.CANCEL) {
					// ignore
				} else {
					result.add(status);
				}

				status = descriptor.createRepositoryMigrator();
				if (status.isOK() && descriptor.getRepositoryMigrator() != null) {
					repositoryMigrators.add(descriptor.getRepositoryMigrator());
				} else if (status.getSeverity() == IStatus.CANCEL) {
					// ignore
				} else {
					result.add(status);
				}
			}
		}

		repositoryManager.initialize(repositoryMigrators);
		taskListExternalizer.initialize(taskListmigrators);
	}

	private Map<String, List<ConnectorFactory>> readFromRepositoriesExtensionPoint(
			IExtensionPoint repositoriesExtensionPoint) {
		// read core and migrator extensions to check for id conflicts
		Map<String, List<ConnectorFactory>> factoryById = new LinkedHashMap<String, List<ConnectorFactory>>();

		IExtension[] repositoryExtensions = repositoriesExtensionPoint.getExtensions();
		for (IExtension repositoryExtension : repositoryExtensions) {
			IConfigurationElement[] elements = repositoryExtension.getConfigurationElements();
			ExtensionPointBasedConnectorDescriptor descriptor = null;
			IConfigurationElement tasklistMigratorElement = null;
			IConfigurationElement repositoryMigratorElement = null;
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELMNT_REPOSITORY_CONNECTOR)) {
					descriptor = new ExtensionPointBasedConnectorDescriptor(element);
				} else if (element.getName().equals(ELMNT_MIGRATOR)) {
					tasklistMigratorElement = element;
				} else if (element.getName().equals(ELMNT_REPOSITORY_MIGRATOR)) {
					repositoryMigratorElement = element;
				}
			}
			if (descriptor != null) {
				descriptor.taskListMigratorElement = tasklistMigratorElement;
				descriptor.repositoryMigratorElement = repositoryMigratorElement;
				ConnectorFactory factory = new ConnectorFactory(descriptor, descriptor.getPluginId());
				factories.add(factory);
				if (descriptor.getId() != null) {
					add(factoryById, descriptor.getId(), factory);
				}
			}
		}
		return factoryById;
	}

	private void checkForConflicts(Map<String, List<ConnectorFactory>> descriptorById) {
		for (Map.Entry<String, List<ConnectorFactory>> entry : descriptorById.entrySet()) {
			if (entry.getValue().size() > 1) {
				MultiStatus status = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, 0, NLS.bind(
						"Connector ''{0}'' registered by multiple extensions.", entry.getKey()), null); //$NON-NLS-1$
				for (ConnectorFactory factory : entry.getValue()) {
					status.add(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, NLS.bind(
							"All extensions contributed by ''{0}'' have been disabled.", factory.getPluginId()), null)); //$NON-NLS-1$
					disabledContributors.add(factory.getPluginId());
					factories.remove(factory);
				}
				result.add(status);
			}
		}
	}

	private void add(Map<String, List<ConnectorFactory>> descriptorById, String id, ConnectorFactory descriptor) {
		List<ConnectorFactory> list = descriptorById.get(id);
		if (list == null) {
			list = new ArrayList<ConnectorFactory>();
			descriptorById.put(id, list);
		}
		list.add(descriptor);
	}

	public Set<String> getDisabledContributors() {
		return new HashSet<String>(disabledContributors);
	}

	public IStatus getResult() {
		return result;
	}

}
