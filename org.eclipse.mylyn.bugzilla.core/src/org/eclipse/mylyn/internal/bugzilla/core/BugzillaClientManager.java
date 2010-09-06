/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryChangeEvent;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta.Type;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;

/**
 * @author Steffen Pingel
 * @author Robert Elves (adaption for Bugzilla)
 */
public class BugzillaClientManager implements IRepositoryListener, IRepositoryChangeListener {

	private final Map<String, BugzillaClient> clientByUrl = new HashMap<String, BugzillaClient>();

	private final BugzillaRepositoryConnector connector;

	private JobChangeAdapter repositoryConfigurationUpdateJobChangeAdapter;

	public BugzillaClientManager(BugzillaRepositoryConnector connector) {
		this.connector = connector;
	}

	public BugzillaClient getClient(TaskRepository taskRepository, IProgressMonitor monitor) throws CoreException {
		BugzillaClient client;
		synchronized (clientByUrl) {
			client = clientByUrl.get(taskRepository.getRepositoryUrl());
			if (client == null) {
				String language = taskRepository.getProperty(IBugzillaConstants.BUGZILLA_LANGUAGE_SETTING);
				if (language == null || language.equals("")) { //$NON-NLS-1$
					language = IBugzillaConstants.DEFAULT_LANG;
				}
				try {
					client = createClient(taskRepository);
				} catch (MalformedURLException e) {
					throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
							"Malformed Repository Url", e)); //$NON-NLS-1$
				}
				clientByUrl.put(taskRepository.getRepositoryUrl(), client);
			}
			RepositoryConfiguration config = connector.getRepositoryConfiguration(taskRepository.getUrl());
			client.setRepositoryConfiguration(config);
		}
		return client;
	}

	protected BugzillaClient createClient(TaskRepository taskRepository) throws MalformedURLException {
		return BugzillaClientFactory.createClient(taskRepository, connector);
	}

	public void repositoryAdded(TaskRepository repository) {
		// make sure there is no stale client still in the cache, bug #149939
		removeClient(repository);
	}

	public void repositoryRemoved(TaskRepository repository) {
		removeClient(repository);
	}

	private void removeClient(TaskRepository repository) {
		synchronized (clientByUrl) {
			clientByUrl.remove(repository.getRepositoryUrl());
		}
	}

	public void repositorySettingsChanged(TaskRepository repository) {
		removeClient(repository);
	}

	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
		// ignore
	}

	@SuppressWarnings("restriction")
	public void repositoryChanged(TaskRepositoryChangeEvent event) {
		Type type = event.getDelta().getType();
		if (type == TaskRepositoryDelta.Type.PROPERTY) {
			Object key = event.getDelta().getKey();
			if (IBugzillaConstants.BUGZILLA_USE_XMLRPC.equals(key)
					|| IBugzillaConstants.BUGZILLA_DESCRIPTOR_FILE.equals(key)) {
				final TaskRepository repository = event.getRepository();
				TaskJob updateJob = new TaskJob("Refreshing repository configuration") {
					private IStatus error;

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor = SubMonitor.convert(monitor);
						monitor.beginTask("Receiving_configuration", IProgressMonitor.UNKNOWN);
						try {
							try {
								connector.updateRepositoryConfiguration(repository, null, monitor);
							} catch (CoreException e) {
								error = e.getStatus();
							}
						} finally {
							monitor.done();
						}
						return Status.OK_STATUS;
					}

					@Override
					public boolean belongsTo(Object family) {
						return family == repository;
					}

					@Override
					public IStatus getStatus() {
						return error;
					}
				};
				updateJob.setPriority(Job.INTERACTIVE);
				updateJob.addJobChangeListener(repositoryConfigurationUpdateJobChangeAdapter);
				updateJob.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						synchronized (repository) {
							repository.setUpdating(false);
						}
					}
				});
				updateJob.schedule();
			}
		}
	}

	public void setRepositoryConfigurationUpdateJobChangeAdapter(
			JobChangeAdapter repositoryConfigurationUpdateJobChangeAdapter) {
		this.repositoryConfigurationUpdateJobChangeAdapter = repositoryConfigurationUpdateJobChangeAdapter;
	}

}
