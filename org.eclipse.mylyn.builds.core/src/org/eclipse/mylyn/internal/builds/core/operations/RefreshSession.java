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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.BuildRequest;
import org.eclipse.mylyn.builds.core.BuildRequest.Kind;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanData;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.builds.core.Build;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.core.BuildsCorePlugin;

/**
 * @author Steffen Pingel
 */
public class RefreshSession {

	private final BuildServer server;

	public RefreshSession(IBuildServer server) {
		this.server = ((BuildServer) server).createWorkingCopy();
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

	private boolean isStale(IBuildPlan oldPlan, BuildPlan newPlan) {
		return true;
	}

	protected void markStale(RefreshRequest request, BuildPlan newPlan) {
		request.stalePlans.add(newPlan);
	}

	public void refresh(RefreshRequest request, IOperationMonitor monitor) throws CoreException {
		request.stalePlans = Collections.synchronizedList(new ArrayList<IBuildPlan>());
		refreshPlans(request, monitor);
		for (IBuildPlan plan : request.stalePlans) {
			BuildRequest buildRequest = new BuildRequest(Kind.LAST, plan);
			refreshBuilds(request, buildRequest, monitor);
		}
	}

	public void refreshBuilds(final RefreshRequest request, final BuildRequest buildRequest,
			final IOperationMonitor monitor) throws CoreException {
		final AtomicReference<List<IBuild>> result = new AtomicReference<List<IBuild>>();
		SafeRunner.run(new ISafeRunnable() {
			public void handleException(Throwable e) {
				StatusHandler.log(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
						"Unexpected error during invocation in server behavior", e));
			}

			public void run() throws Exception {
				result.set(server.getBehaviour().getBuilds(buildRequest, monitor));
			}
		});
		if (result.get() == null) {
			throw new CoreException(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
					"Server did not provide a valid build."));
		}
		final BuildServer original = server.getOriginal();
		original.getLoader().getRealm().exec(new Runnable() {
			public void run() {
				for (IBuildPlan modelPlan : request.getModel().getPlans()) {
					if (modelPlan.getServer() == original && modelPlan.getId().equals(buildRequest.getPlan().getId())) {
						if (modelPlan.getLastBuild() != null) {
							request.getModel().getBuilds().remove(modelPlan.getLastBuild());
						}
						IBuild build = result.get().get(0);
						((BuildPlan) modelPlan).setLastBuild(build);
						((Build) build).setPlan(modelPlan);
						((Build) build).setServer(original);
						request.getModel().getBuilds().add(build);
					}
				}
			}
		});
	}

	public void refreshPlans(final RefreshRequest request, final IOperationMonitor monitor) throws CoreException {
		final AtomicReference<List<IBuildPlanData>> result = new AtomicReference<List<IBuildPlanData>>();
		SafeRunner.run(new ISafeRunnable() {
			public void handleException(Throwable e) {
				StatusHandler.log(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
						"Unexpected error during invocation in server behavior", e));
			}

			public void run() throws Exception {
				result.set(server.getBehaviour().getPlans(monitor));
			}
		});
		if (result.get() == null) {
			throw new CoreException(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
					"Server did not provide any plans."));
		}
		final BuildServer original = server.getOriginal();
		original.getLoader().getRealm().exec(new Runnable() {
			public void run() {
				for (IBuildPlan oldPlan : request.getModel().getPlans()) {
					if (oldPlan.getServer() == original) {
						BuildPlan newPlan = getPlanById(result.get(), oldPlan.getId());
						if (newPlan != null) {
							update(request, oldPlan, newPlan);
						} else {
							((BuildPlan) oldPlan).setOperationStatus(new Status(IStatus.ERROR,
									BuildsCorePlugin.ID_PLUGIN, "The plan does not exist."));
						}
					}
				}
			}
		});

		// FIXME exec does not block, copy all
		for (IBuildPlanData plan : result.get()) {
			markStale(request, (BuildPlan) plan);
		}
	}

	protected void update(RefreshRequest request, IBuildPlan oldPlan, BuildPlan newPlan) {
		if (isStale(oldPlan, newPlan)) {
			((BuildPlan) oldPlan).merge(newPlan);
		}
	}

}
