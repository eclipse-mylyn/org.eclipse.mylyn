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
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanData;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.builds.core.BuildModel;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.core.BuildsCorePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class RefreshPlansOperation extends AbstractBuildOperation {

	private final List<IBuildServer> servers;

	private final BuildModel model;

	public RefreshPlansOperation(BuildModel model) {
		super("Refresh Builds");
		this.model = model;
		Assert.isNotNull(model);
		this.servers = new ArrayList<IBuildServer>(model.getServers().size());
		for (IBuildServer server : model.getServers()) {
			if (server.getLocation().isOffline()) {
				continue;
			}
			this.servers.add(((BuildServer) server).createWorkingCopy());
		}
	}

	@Override
	protected IStatus doExecute(IOperationMonitor progress) {
		MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Refreshing of builds failed", null);
		progress.beginTask("Refreshing builds", servers.size());
		for (IBuildServer server : servers) {
			try {
				doRefresh((BuildServer) server, progress.newChild(1));
			} catch (CoreException e) {
				result.add(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, NLS.bind(
						"Refresh of server ''{0}'' failed", server.getName()), e));
			} catch (OperationCanceledException e) {
				return Status.CANCEL_STATUS;
			}
		}
		setStatus(result);
		return Status.OK_STATUS;
	}

	public void doRefresh(final BuildServer server, final IOperationMonitor monitor) throws CoreException {
		final AtomicReference<List<IBuildPlanData>> result = new AtomicReference<List<IBuildPlanData>>();
		SafeRunner.run(new ISafeRunnable() {
			public void run() throws Exception {
				result.set(server.getBehaviour().getPlans(monitor));
			}

			public void handleException(Throwable e) {
				StatusHandler.log(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
						"Unexpected error during invocation in server behavior", e));
			}
		});
		if (result.get() == null) {
			throw new CoreException(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
					"Server did not provide any plans."));
		}
		final BuildServer original = server.getOriginal();
		original.getLoader().getRealm().exec(new Runnable() {
			public void run() {
				for (IBuildPlan oldPlan : model.getPlans()) {
					if (oldPlan.getServer() == original) {
						BuildPlan newPlan = getPlanById(result.get(), oldPlan.getId());
						if (newPlan != null) {
							((BuildPlan) oldPlan).merge(newPlan);
						} else {
							((BuildPlan) oldPlan).setOperationStatus(new Status(IStatus.ERROR,
									BuildsCorePlugin.ID_PLUGIN, "The plan does not exist."));
						}
					}
				}
			}
		});
	}

	public BuildPlan getPlanById(List<IBuildPlanData> plans, String id) {
		if (id != null) {
			for (IBuildPlanData plan : plans) {
				if (id.equals(plan.getId())) {
					return (BuildPlan) plan;
				}
			}
		}
		return null;
	}

}
