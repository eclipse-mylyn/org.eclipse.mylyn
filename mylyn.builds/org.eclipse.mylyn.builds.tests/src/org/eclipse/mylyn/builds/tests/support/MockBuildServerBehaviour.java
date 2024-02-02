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

package org.eclipse.mylyn.builds.tests.support;

import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.spi.BuildPlanRequest;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.core.spi.BuildServerConfiguration;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.core.spi.RunBuildRequest;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;

/**
 * @author Steffen Pingel
 */
public class MockBuildServerBehaviour extends BuildServerBehaviour {

	private List<IBuild> builds;

	private final BuildServer server;

	private List<IBuildPlan> plans;

	public MockBuildServerBehaviour() {
		this(null);
	}

	public MockBuildServerBehaviour(BuildServer server) {
		this.server = server;

		IBuildPlan plan = createBuildPlan();
		plan.setId("1");
		plans = Collections.singletonList(plan);

		IBuild build = createBuild();
		build.setId("1");
		build.setPlan(plan);
		builds = Collections.singletonList(build);
	}

	@Override
	public List<IBuild> getBuilds(GetBuildsRequest request, IOperationMonitor monitor) throws CoreException {
		return builds;
	}

	@Override
	public BuildServerConfiguration getConfiguration() {
		// ignore
		return null;
	}

	@Override
	public Reader getConsole(IBuild build, IOperationMonitor monitor) throws CoreException {
		// ignore
		return null;
	}

	@Override
	public List<IBuildPlan> getPlans(BuildPlanRequest request, IOperationMonitor monitor) throws CoreException {
		return plans;
	}

	public BuildServer getServer() {
		return server;
	}

	@Override
	public BuildServerConfiguration refreshConfiguration(IOperationMonitor monitor) throws CoreException {
		// ignore
		return null;
	}

	@Override
	public void runBuild(RunBuildRequest request, IOperationMonitor monitor) throws CoreException {
	}

	@Override
	public void abortBuild(IBuild build, IOperationMonitor monitor) throws CoreException {
	}

	public void setBuilds(List<IBuild> builds) {
		this.builds = builds;
	}

	public void setPlans(List<IBuildPlan> plans) {
		this.plans = plans;
	}

	@Override
	public IStatus validate(IOperationMonitor monitor) throws CoreException {
		return Status.OK_STATUS;
	}

}
