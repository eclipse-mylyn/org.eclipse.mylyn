/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.core.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.internal.builds.core.BuildModel;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.core.BuildsCorePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class RefreshPlansOperation {

	private final BuildModel model;

	public RefreshPlansOperation(BuildModel model) {
		Assert.isNotNull(model);
		this.model = model;
	}

	public void execute() {
		model.getScheduler().schedule(getJobs());
	}

	public List<BuildJob> getJobs() {
		final AtomicReference<List<BuildServer>> serversReference = new AtomicReference<List<BuildServer>>();
		model.getLoader().getRealm().syncExec(new Runnable() {
			public void run() {
				ArrayList<BuildServer> servers = new ArrayList<BuildServer>(model.getServers().size());
				for (IBuildServer server : model.getServers()) {
					if (server.getLocation().isOffline()) {
						continue;
					}
					servers.add((BuildServer) server);
					serversReference.set(servers);
				}
			}
		});

		List<BuildJob> jobs = new ArrayList<BuildJob>(serversReference.get().size());
		for (final BuildServer server : serversReference.get()) {
			BuildJob job = new BuildJob(NLS.bind("Refreshing Builds ({0})", server.getLabel())) {
				@Override
				protected IStatus doExecute(IOperationMonitor progress) {
					try {
						RefreshRequest request = new RefreshRequest(model);
						server.getRefreshSession().refresh(request, progress.newChild(1));
					} catch (CoreException e) {
						setStatus(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, NLS.bind(
								"Refresh of server ''{0}'' failed", server.getLabel()), e));
					} catch (OperationCanceledException e) {
						return Status.CANCEL_STATUS;
					}
					return Status.OK_STATUS;
				}
			};
			jobs.add(job);
		}
		return jobs;
	}

	public IStatus syncExec(IOperationMonitor progress) {
		List<BuildJob> jobs = getJobs();
		MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Refreshing of builds failed", null);
		progress.beginTask("Refreshing builds", jobs.size());
		for (BuildJob job : jobs) {
			IStatus status = job.run(progress.newChild(1));
			if (status.getSeverity() == IStatus.CANCEL) {
				return Status.CANCEL_STATUS;
			} else if (!status.isOK()) {
				result.add(status);
			}
		}
		return result;
	}

}
