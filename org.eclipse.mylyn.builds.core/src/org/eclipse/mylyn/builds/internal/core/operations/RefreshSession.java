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

package org.eclipse.mylyn.builds.internal.core.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildPlanRequest;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest.Kind;
import org.eclipse.mylyn.builds.internal.core.BuildPlan;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;

/**
 * @author Steffen Pingel
 */
public class RefreshSession {

	private final BuildServer server;

	public RefreshSession(IBuildServer server) {
		this.server = ((BuildServer) server).createWorkingCopy();
	}

	public BuildPlan getPlanById(List<IBuildPlan> plans, String id) {
		if (id != null) {
			for (IBuildPlan plan : plans) {
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
			GetBuildsRequest buildRequest = new GetBuildsRequest(Kind.LAST, plan);
			refreshBuilds(request, buildRequest, monitor);
		}
	}

	public void refreshBuilds(final RefreshRequest request, final GetBuildsRequest buildRequest,
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
			// indicates that plan was never built
			return;
		}

		// merge builds into model
		final BuildServer original = server.getOriginal();
		original.getLoader().getRealm().syncExec(new Runnable() {
			public void run() {
				Date refreshDate = new Date();
				for (IBuildPlan modelPlan : request.getModel().getPlans()) {
					if (modelPlan.getServer() == original && modelPlan.getId().equals(buildRequest.getPlan().getId())) {
						if (modelPlan.getLastBuild() != null) {
							request.getModel().getBuilds().remove(modelPlan.getLastBuild());
						}
						IBuild build = result.get().get(0);
						modelPlan.setLastBuild(build);
						build.setPlan(modelPlan);
						build.setServer(original);
						build.setRefreshDate(refreshDate);
						request.getModel().getBuilds().add(build);
					}
				}
			}
		});
	}

	public void refreshPlans(final RefreshRequest request, final IOperationMonitor monitor) throws CoreException {
		final BuildServer original = server.getOriginal();

		// prepare
		final AtomicReference<List<String>> input = new AtomicReference<List<String>>();
		original.getLoader().getRealm().syncExec(new Runnable() {
			public void run() {
				List<String> planIds = new ArrayList<String>();
				for (IBuildPlan oldPlan : request.getModel().getPlans()) {
					if (oldPlan.getServer() == original) {
						planIds.add(oldPlan.getId());
					}
				}
				input.set(planIds);
			}
		});

		// execute
		final AtomicReference<List<IBuildPlan>> result = new AtomicReference<List<IBuildPlan>>();
		SafeRunner.run(new ISafeRunnable() {
			public void handleException(Throwable e) {
				StatusHandler.log(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
						"Unexpected error during invocation in server behavior", e));
			}

			public void run() throws Exception {
				BuildPlanRequest planRequest = new BuildPlanRequest(input.get());
				result.set(server.getBehaviour().getPlans(planRequest, monitor));
			}
		});

		// handle result
		if (result.get() == null) {
			throw new CoreException(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
					"Server did not provide any plans."));
		}
		original.getLoader().getRealm().syncExec(new Runnable() {
			public void run() {
				Date refreshDate = new Date();
				original.setRefreshDate(refreshDate);
				for (IBuildPlan oldPlan : request.getModel().getPlans()) {
					if (oldPlan.getServer() == original) {
						BuildPlan newPlan = getPlanById(result.get(), oldPlan.getId());
						if (newPlan != null) {
							newPlan.setRefreshDate(refreshDate);
							update(request, oldPlan, newPlan);
						} else {
							((BuildPlan) oldPlan).setOperationStatus(new Status(IStatus.ERROR,
									BuildsCorePlugin.ID_PLUGIN, "The plan does not exist."));
						}
					}
				}
			}
		});
	}

	protected void update(RefreshRequest request, IBuildPlan oldPlan, BuildPlan newPlan) {
		if (isStale(oldPlan, newPlan)) {
			((BuildPlan) oldPlan).merge(newPlan);
			markStale(request, newPlan);
		}
	}

}
