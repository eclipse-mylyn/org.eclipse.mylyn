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
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.core.BuildsCorePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class RefreshOperation extends AbstractBuildOperation {

	private final List<IBuildServer> servers;

	public RefreshOperation(List<IBuildServer> servers) {
		super("Refresh Builds");
		Assert.isNotNull(servers);
		this.servers = new ArrayList<IBuildServer>(servers.size());
		for (IBuildServer server : servers) {
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
		server.getLoader().getRealm().exec(new Runnable() {
			public void run() {
				ArrayList<IBuildPlan> oldPlans = new ArrayList<IBuildPlan>(server.getPlans());
				server.getPlans().clear();
				for (IBuildPlanData plan : result.get()) {
					server.getPlans().add((IBuildPlan) plan);
				}
				for (IBuildPlan plan : oldPlans) {
					IBuildPlan newPlan = server.getPlanById(plan.getId());
					if (newPlan != null) {
						((BuildPlan) newPlan).setSelected(plan.isSelected());
					}
				}
			}
		});
	}

}
