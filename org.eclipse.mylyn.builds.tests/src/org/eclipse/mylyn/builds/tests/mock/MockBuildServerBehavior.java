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

package org.eclipse.mylyn.builds.tests.mock;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;

/**
 * @author Steffen Pingel
 */
public class MockBuildServerBehavior extends BuildServerBehaviour {

	public MockBuildServerBehavior(IBuildServer server) {
		super(server);
	}

	@Override
	public List<IBuildPlan> getPlans(IOperationMonitor monitor) {
		IBuildPlanWorkingCopy failingPlan = getServer().createBuildPlan();
		failingPlan.setId("1");
		failingPlan.setName("Failing Build Plan");
		failingPlan.setState(BuildState.RUNNING);
		failingPlan.setStatus("Failed");
		failingPlan.setHealth(15);

		IBuildPlanWorkingCopy childPlan1 = getServer().createBuildPlan();
		childPlan1.setId("1.1");
		childPlan1.setName("Stopped Child Build Plan");
		childPlan1.setState(BuildState.STOPPED);
		childPlan1.setStatus("Failed");
		failingPlan.getChildren().add(childPlan1);

		IBuildPlanWorkingCopy childPlan2 = getServer().createBuildPlan();
		childPlan2.setId("1.2");
		childPlan2.setName("Running Child Build Plan");
		childPlan2.setState(BuildState.STOPPED);
		childPlan2.setStatus("Unknown");
		childPlan2.setHealth(55);
		failingPlan.getChildren().add(childPlan2);

		IBuildPlanWorkingCopy succeedingPlan = getServer().createBuildPlan();
		succeedingPlan.setId("2");
		succeedingPlan.setName("Succeeding Build Plan");
		succeedingPlan.setState(BuildState.STOPPED);
		succeedingPlan.setStatus("Success");
		succeedingPlan.setInfo("12 tests passing");
		succeedingPlan.setHealth(89);

		return Arrays.asList((IBuildPlan) failingPlan, childPlan1, childPlan2, succeedingPlan);
	}

	@Override
	public IStatus validate(IOperationMonitor monitor) throws CoreException {
		return Status.OK_STATUS;
	}

}
