/*******************************************************************************
 * Copyright (c) 2011, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.tasks.core.data.ITaskAttributeDiff;
import org.eclipse.mylyn.tasks.core.data.ITaskDataDiff;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationParticipant;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class SynchronizationManger {

	public static class DefaultParticipant extends SynchronizationParticipant {

		private final Set<String> attributeIds;

		public DefaultParticipant(List<String> attributeIds, String connectorKind) {
			Assert.isNotNull(attributeIds);
			this.attributeIds = new HashSet<String>(attributeIds);
			String id = DefaultParticipant.class.getName();
			if (connectorKind != null) {
				id += "." + connectorKind; //$NON-NLS-1$
			}
			setId(id);
		}

		@Override
		public void processUpdate(ITaskDataDiff diff, IProgressMonitor monitor) {
			if (diff.getChangedAttributes().size() > 0) {
				for (Iterator<ITaskAttributeDiff> it = diff.getChangedAttributes().iterator(); it.hasNext();) {
					ITaskAttributeDiff attributeDiff = it.next();
					if (attributeIds.contains(attributeDiff.getAttributeId())) {
						it.remove();
					}
				}
			}
		}

	}

	private final Map<String, List<SynchronizationParticipant>> participantsByConnectorKind = new HashMap<String, List<SynchronizationParticipant>>();

	private List<SynchronizationParticipant> defaultParticipants;

	private final RepositoryModel model;

	public SynchronizationManger(RepositoryModel model) {
		Assert.isNotNull(model);
		this.model = model;
	}

	private List<SynchronizationParticipant> loadParticipants(String connectorKind) {
		MultiStatus status = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, 0,
				"Synchronization participants failed to load.", null); //$NON-NLS-1$

		List<SynchronizationParticipant> participants = new ArrayList<SynchronizationParticipant>(1);
		List<String> attributeIds = new ArrayList<String>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint connectorsExtensionPoint = registry
				.getExtensionPoint(ITasksCoreConstants.ID_PLUGIN + ".synchronizationParticipants"); //$NON-NLS-1$
		IExtension[] extensions = connectorsExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if ("participant".equals(element.getName())) { //$NON-NLS-1$
					String value = element.getAttribute("connectorKind"); //$NON-NLS-1$
					if (value != null && value.equals(connectorKind) || value == connectorKind) {
						try {
							Object object = element.createExecutableExtension("class"); //$NON-NLS-1$
							if (object instanceof SynchronizationParticipant) {
								SynchronizationParticipant participant = (SynchronizationParticipant) object;
								participant.setId(element.getAttribute("id")); //$NON-NLS-1$
								participants.add(participant);
							} else {
								status.add(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, NLS.bind(
										"Connector core ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
										object.getClass().getCanonicalName(), element.getContributor().getName())));
							}
						} catch (Throwable e) {
							status.add(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
									NLS.bind("Connector core failed to load for extension contributed by {0}", //$NON-NLS-1$
											element.getContributor().getName()),
									e));
						}
					}
				} else if ("suppressIncoming".equals(element.getName())) { //$NON-NLS-1$
					String value = element.getAttribute("connectorKind"); //$NON-NLS-1$
					if (value != null && value.equals(connectorKind) || value == connectorKind) {
						String attributeId = element.getAttribute("attributeId"); //$NON-NLS-1$
						if (attributeId != null) {
							attributeIds.add(attributeId);
						}
					}
				}
			}
		}

		if (attributeIds.size() > 0) {
			participants.add(new DefaultParticipant(attributeIds, connectorKind));
		}

		if (!status.isOK()) {
			StatusHandler.log(status);
		}

		return participants;
	}

	public synchronized boolean hasParticipants(String connectorKind) {
		return getDefaultParticipants().size() > 0 || getParticipants(connectorKind).size() > 0;
	}

	public synchronized List<SynchronizationParticipant> getParticipants(String connectorKind) {
		List<SynchronizationParticipant> participants = participantsByConnectorKind.get(connectorKind);
		if (participants == null) {
			participants = loadParticipants(connectorKind);
			participantsByConnectorKind.put(connectorKind, participants);
		}
		return participants;
	}

	public synchronized List<SynchronizationParticipant> getDefaultParticipants() {
		if (defaultParticipants == null) {
			defaultParticipants = loadParticipants(null);
		}
		return defaultParticipants;
	}

	public synchronized List<SynchronizationParticipant> getIgnoredIncomings(String connectorKind) {
		List<SynchronizationParticipant> participants = participantsByConnectorKind.get(connectorKind);
		if (participants == null) {
			participants = loadParticipants(connectorKind);
			participantsByConnectorKind.put(connectorKind, participants);
		}
		return participants;
	}

	public TaskDataDiff createDiff(TaskData newTaskData, TaskData oldTaskData, final IProgressMonitor monitor) {
		final TaskDataDiff diff = new TaskDataDiff(model, newTaskData, oldTaskData);
		for (final SynchronizationParticipant participant : getDefaultParticipants()) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// handled by framework
				}

				public void run() throws Exception {
					participant.processUpdate(diff, monitor);
				}
			});
		}
		for (final SynchronizationParticipant participant : getParticipants(newTaskData.getConnectorKind())) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// handled by framework
				}

				public void run() throws Exception {
					participant.processUpdate(diff, monitor);
				}
			});
		}
		return diff;
	}
}
