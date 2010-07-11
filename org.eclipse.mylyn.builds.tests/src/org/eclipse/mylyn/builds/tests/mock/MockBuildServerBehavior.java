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
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanData;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;

/**
 * @author Steffen Pingel
 */
public class MockBuildServerBehavior extends BuildServerBehaviour {

	public MockBuildServerBehavior() {
	}

	@Override
	public List<IBuildPlanData> getPlans(IOperationMonitor monitor) {
		IBuildPlanWorkingCopy failingPlan = createBuildPlan();
		failingPlan.setId("1");
		failingPlan.setName("Failing Build Plan");
		failingPlan.setState(BuildState.RUNNING);
		failingPlan.setStatus(BuildStatus.FAILED);
		failingPlan.setHealth(15);

		IBuildPlanWorkingCopy childPlan1 = createBuildPlan();
		childPlan1.setId("1.1");
		childPlan1.setName("Stopped Child Build Plan");
		childPlan1.setState(BuildState.STOPPED);
		childPlan1.setStatus(BuildStatus.FAILED);
		//failingPlan.getChildren().add(childPlan1);

		IBuildPlanWorkingCopy childPlan2 = createBuildPlan();
		childPlan2.setId("1.2");
		childPlan2.setName("Running Child Build Plan");
		childPlan2.setState(BuildState.STOPPED);
		childPlan2.setStatus(BuildStatus.FAILED);
		childPlan2.setHealth(55);
		//failingPlan.getChildren().add(childPlan2);

		IBuildPlanWorkingCopy succeedingPlan = createBuildPlan();
		succeedingPlan.setId("2");
		succeedingPlan.setName("Succeeding Build Plan");
		succeedingPlan.setState(BuildState.STOPPED);
		succeedingPlan.setStatus(BuildStatus.SUCCESS);
		succeedingPlan.setInfo("12 tests passing");
		succeedingPlan.setHealth(89);

		return Arrays.asList((IBuildPlan) failingPlan, childPlan1, childPlan2, succeedingPlan);
	}

	@Override
	public IStatus validate(IOperationMonitor monitor) throws CoreException {
		return Status.OK_STATUS;
	}

	@Override
	public void runBuild(IBuildPlanData plan, IOperationMonitor monitor) throws CoreException {
	}

}
