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
 *     Eike Stepper - fixes for bug 323568
 *******************************************************************************/

package org.eclipse.mylyn.builds.core.spi;

import java.io.Reader;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildCause;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildReference;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeArtifact;
import org.eclipse.mylyn.builds.core.IChangeSet;
import org.eclipse.mylyn.builds.core.IHealthReport;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestResult;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.IUser;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;

/**
 * @author Steffen Pingel
 */
public abstract class BuildServerBehaviour {

	public BuildServerBehaviour() {
	}

	protected IArtifact createArtifact() {
		return BuildFactory.eINSTANCE.createArtifact();
	}

	protected IBuild createBuild() {
		return BuildFactory.eINSTANCE.createBuild();
	}

	protected IBuildCause createBuildCause() {
		return BuildFactory.eINSTANCE.createBuildCause();
	}

	protected IBuildPlan createBuildPlan() {
		return BuildFactory.eINSTANCE.createBuildPlan();
	}

	protected IBuildReference createBuildReference() {
		return BuildFactory.eINSTANCE.createBuildReference();
	}

	protected IChange createChange() {
		return BuildFactory.eINSTANCE.createChange();
	}

	protected IChangeArtifact createChangeArtifact() {
		return BuildFactory.eINSTANCE.createChangeArtifact();
	}

	protected IChangeSet createChangeSet() {
		return BuildFactory.eINSTANCE.createChangeSet();
	}

	protected IHealthReport createHealthReport() {
		return BuildFactory.eINSTANCE.createHealthReport();
	}

	protected ITestCase createTestCase() {
		return BuildFactory.eINSTANCE.createTestCase();
	}

	protected ITestResult createTestResult() {
		return BuildFactory.eINSTANCE.createTestResult();
	}

	protected ITestSuite createTestSuite() {
		return BuildFactory.eINSTANCE.createTestSuite();
	}

	protected IUser createUser() {
		return BuildFactory.eINSTANCE.createUser();
	}

	public abstract List<IBuild> getBuilds(GetBuildsRequest request, IOperationMonitor monitor) throws CoreException;

	public abstract BuildServerConfiguration getConfiguration();

	public abstract Reader getConsole(IBuild build, IOperationMonitor monitor) throws CoreException;

	public abstract List<IBuildPlan> getPlans(BuildPlanRequest request, IOperationMonitor monitor) throws CoreException;

	public abstract BuildServerConfiguration refreshConfiguration(IOperationMonitor monitor) throws CoreException;

	public abstract void runBuild(RunBuildRequest request, IOperationMonitor monitor) throws CoreException;

	public void abortBuild(IBuild build, IOperationMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public abstract IStatus validate(IOperationMonitor monitor) throws CoreException;

}
