/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.mylyn.builds.internal.core.util.BuildRunnableWithResult;
import org.eclipse.mylyn.builds.internal.core.util.BuildRunner;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;

/**
 * Manages refreshes for plans and builds. Each server has one associated session that may process several requests concurrently.
 *
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

	private boolean isStale(IBuildPlan oldPlan, BuildPlan newPlan, IOperationMonitor monitor) {
		if (oldPlan.getStatus() != newPlan.getStatus() || oldPlan.getState() != newPlan.getState()) {
			return true;
		}
		if (oldPlan.getLastBuild() != null && newPlan.getLastBuild() != null) {
			// only refresh if there is a new build or if build status has changed
			return isStale(oldPlan.getLastBuild(), newPlan.getLastBuild(), monitor);
		}
		return oldPlan.getLastBuild() == null && newPlan.getLastBuild() == null;
	}

	private boolean isStale(IBuild oldBuild, IBuild newBuild, IOperationMonitor monitor) {
		if (oldBuild.getBuildNumber() != newBuild.getBuildNumber()) {
			return true;
		}
		return false;
	}

	protected void markStale(RefreshRequest request, BuildPlan newPlan) {
		request.stalePlans.add(newPlan);
	}

	public void refresh(RefreshRequest request, IOperationMonitor monitor) throws CoreException {
		// initialize
		request.stalePlans = Collections.synchronizedList(new ArrayList<IBuildPlan>());

		// refresh selected or all plans
		refreshPlans(request, monitor);

		// force refresh of selected plans
		if (request.plansToRefresh != null) {
			request.stalePlans.clear();
			request.stalePlans.addAll(request.plansToRefresh);
		}

		// refresh last build of stale plans
		for (IBuildPlan plan : request.stalePlans) {
			GetBuildsRequest buildRequest = new GetBuildsRequest(plan, Kind.LAST);
			refreshBuilds(request, buildRequest, monitor);
		}
	}

	public void refreshBuilds(final RefreshRequest request, final GetBuildsRequest buildRequest,
			final IOperationMonitor monitor) throws CoreException {
		final List<IBuild> result = BuildRunner.run(new BuildRunnableWithResult<List<IBuild>>() {
			@Override
			public List<IBuild> run() throws CoreException {
				return server.getBehaviour().getBuilds(buildRequest, monitor);
			}
		});

		if (result == null) {
			// indicates that plan was never built
			return;
		}

		// merge builds into model
		final BuildServer original = server.getOriginal();
		original.getLoader().getRealm().syncExec(() -> {
			Date refreshDate = new Date();
			for (IBuildPlan modelPlan : request.getModel().getPlans()) {
				if (modelPlan.getServer() == original && modelPlan.getId().equals(buildRequest.getPlan().getId())) {
					updateLastBuild(request, modelPlan, result.get(0), refreshDate);
				}
			}
		});
	}

	private void updateLastBuild(final RefreshRequest request, IBuildPlan plan, IBuild build, Date refreshDate) {
		if (plan.getLastBuild() != null) {
			request.getModel().getBuilds().remove(plan.getLastBuild());
		}
		plan.setLastBuild(build);
		build.setPlan(plan);
		build.setServer(plan.getServer());
		build.setRefreshDate(refreshDate);
		request.getModel().getBuilds().add(build);
	}

	public void refreshPlans(final RefreshRequest request, final IOperationMonitor monitor) throws CoreException {
		final BuildServer original = server.getOriginal();

		// prepare
		final AtomicReference<List<String>> input = new AtomicReference<>();
		if (request.plansToRefresh != null) {
			// refresh selected plans
			List<String> planIds = new ArrayList<>();
			for (IBuildPlan plan : request.plansToRefresh) {
				planIds.add(plan.getId());
			}
			input.set(planIds);
		} else {
			// refresh all plans for server
			original.getLoader().getRealm().syncExec(() -> {
				List<String> planIds = new ArrayList<String>();
				for (IBuildPlan oldPlan : request.getModel().getPlans()) {
					if (oldPlan.getServer() == original) {
						planIds.add(oldPlan.getId());
					}
				}
				input.set(planIds);
			});
		}

		// execute
		final List<IBuildPlan> result = BuildRunner.run(new BuildRunnableWithResult<List<IBuildPlan>>() {
			@Override
			public List<IBuildPlan> run() throws CoreException {
				BuildPlanRequest planRequest = new BuildPlanRequest(input.get());
				List<IBuildPlan> result = server.getBehaviour().getPlans(planRequest, monitor);
				result.stream().forEach(p -> p.setServer(server.getOriginal()));
				return result;
			}
		});

		// handle result
		if (result == null) {
			throw new CoreException(
					new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, "Server did not provide any plans."));
		}
		original.getLoader().getRealm().syncExec(() -> {
			Date refreshDate = new Date();
			original.setRefreshDate(refreshDate);
			for (IBuildPlan oldPlan : request.getModel().getPlans()) {
				if (oldPlan.getServer() == original) {
					BuildPlan newPlan = getPlanById(result, oldPlan.getId());
					if (newPlan != null) {
						newPlan.setRefreshDate(refreshDate);
						update(request, oldPlan, newPlan, monitor);
					} else {
						((BuildPlan) oldPlan).setOperationStatus(
								new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, "The plan does not exist."));
					}
				}
			}
		});
	}

	protected void update(RefreshRequest request, IBuildPlan oldPlan, BuildPlan newPlan, IOperationMonitor monitor) {
		boolean stale = isStale(oldPlan, newPlan, monitor);
		if (stale || !monitor.hasFlag(OperationFlag.BACKGROUND)) {
			markStale(request, newPlan);
		}
		((BuildPlan) oldPlan).merge(newPlan);
		if (stale && newPlan.getLastBuild() != null) {
			updateLastBuild(request, oldPlan, newPlan.getLastBuild(), new Date());
		}
	}

}
