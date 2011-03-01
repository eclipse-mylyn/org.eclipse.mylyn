/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationParticipant;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class SynchronizationManger {

	private final Map<String, List<SynchronizationParticipant>> participantsByConnectorKind = new HashMap<String, List<SynchronizationParticipant>>();

	private final RepositoryModel model;

	public SynchronizationManger(RepositoryModel model) {
		Assert.isNotNull(model);
		this.model = model;
	}

	private List<SynchronizationParticipant> loadParticipants(String connectorKind) {
		Assert.isNotNull(connectorKind);
		MultiStatus status = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, 0,
				"Synchronization participants failed to load.", null); //$NON-NLS-1$

		List<SynchronizationParticipant> result = new ArrayList<SynchronizationParticipant>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint connectorsExtensionPoint = registry.getExtensionPoint(ITasksCoreConstants.ID_PLUGIN
				+ ".synchronizationParticipants"); //$NON-NLS-1$
		IExtension[] extensions = connectorsExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				String value = element.getAttribute("connectorKind"); //$NON-NLS-1$
				if (value == null || connectorKind.equals(value)) {
					try {
						Object object = element.createExecutableExtension("class"); //$NON-NLS-1$
						if (object instanceof SynchronizationParticipant) {
							result.add((SynchronizationParticipant) object);
						} else {
							status.add(new Status(
									IStatus.ERROR,
									ITasksCoreConstants.ID_PLUGIN,
									NLS.bind(
											"Connector core ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
											object.getClass().getCanonicalName(), element.getContributor().getName())));
						}
					} catch (Throwable e) {
						status.add(new Status(
								IStatus.ERROR,
								ITasksCoreConstants.ID_PLUGIN,
								NLS.bind(
										"Connector core failed to load for extension contributed by {0}", element.getContributor().getName()), e)); //$NON-NLS-1$
					}
				}
			}
		}

		if (!status.isOK()) {
			StatusHandler.log(status);
		}

		return result;
	}

	public synchronized boolean hasParticipants(String connectorKind) {
		return getParticipants(connectorKind) != null;
	}

	public synchronized List<SynchronizationParticipant> getParticipants(String connectorKind) {
		List<SynchronizationParticipant> participants = participantsByConnectorKind.get(connectorKind);
		if (participants == null) {
			participants = loadParticipants(connectorKind);
			participantsByConnectorKind.put(connectorKind, participants);
		}
		return participants;
	}

	public TaskDataDiff processUpdate(TaskData newTaskData, TaskData oldTaskData, IProgressMonitor monitor) {
		TaskDataDiff diff = new TaskDataDiff(model, newTaskData, oldTaskData);
		for (SynchronizationParticipant participant : getParticipants(newTaskData.getConnectorKind())) {
			participant.processUpdate(diff, monitor);
		}
		return diff;
	}

}
