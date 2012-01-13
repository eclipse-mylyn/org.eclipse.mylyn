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

package org.eclipse.mylyn.builds.tests.support;

import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildFactory;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.Build;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;

/**
 * @author Steffen Pingel
 */
public class BuildHarness {

	private final BuildModel model;

	private final MockOperationService operationService;

	private final MockBuildLoader loader;

	private BuildServer server;

	public BuildHarness() {
		model = (BuildModel) IBuildFactory.INSTANCE.createBuildModel();
		loader = new MockBuildLoader();
		operationService = new MockOperationService(loader.getRealm());
	}

	public void dispose() {
	}

	public BuildModel getModel() {
		return model;
	}

	public MockOperationService getOperationService() {
		return operationService;
	}

	public BuildServer createServer() {
		server = (BuildServer) IBuildFactory.INSTANCE.createBuildServer();
		server.setConnectorKind(MockBuildConnector.KIND);
		server.setLoader(loader);

		RepositoryLocation location = new RepositoryLocation();
		location.setCredentialsStore(new InMemoryCredentialsStore());
		location.setUrl("http://ci.mylyn.org/");
		server.setLocation(location);

		getModel().getServers().add(server);

		return server;
	}

	public IBuild createBuild() {
		Build build = (Build) IBuildFactory.INSTANCE.createBuild();
		return build;
	}

	public List<IBuildPlan> createPlans() {
		IBuildPlan failingPlan = IBuildFactory.INSTANCE.createBuildPlan();
		failingPlan.setId("1");
		failingPlan.setName("Failing Build Plan");
		failingPlan.setState(BuildState.RUNNING);
		failingPlan.setStatus(BuildStatus.FAILED);
		failingPlan.setHealth(15);

		IBuildPlan childPlan1 = IBuildFactory.INSTANCE.createBuildPlan();
		childPlan1.setId("1.1");
		childPlan1.setName("Stopped Child Build Plan");
		childPlan1.setState(BuildState.STOPPED);
		childPlan1.setStatus(BuildStatus.FAILED);
		//failingPlan.getChildren().add(childPlan1);

		IBuildPlan childPlan2 = IBuildFactory.INSTANCE.createBuildPlan();
		childPlan2.setId("1.2");
		childPlan2.setName("Running Child Build Plan");
		childPlan2.setState(BuildState.STOPPED);
		childPlan2.setStatus(BuildStatus.FAILED);
		childPlan2.setHealth(55);
		//failingPlan.getChildren().add(childPlan2);

		IBuildPlan succeedingPlan = IBuildFactory.INSTANCE.createBuildPlan();
		succeedingPlan.setId("2");
		succeedingPlan.setName("Succeeding Build Plan");
		succeedingPlan.setState(BuildState.STOPPED);
		succeedingPlan.setStatus(BuildStatus.SUCCESS);
		succeedingPlan.setInfo("12 tests passing");
		succeedingPlan.setHealth(89);

		return Arrays.asList(failingPlan, childPlan1, childPlan2, succeedingPlan);
	}

	public IBuildPlan createBuildPlan() {
		IBuildPlan plan = IBuildFactory.INSTANCE.createBuildPlan();
		plan.setId("plan");

		plan.setServer(getServer());
		getModel().getPlans().add(plan);

		return plan;
	}

	public IBuildServer getServer() {
		return server;
	}

}
