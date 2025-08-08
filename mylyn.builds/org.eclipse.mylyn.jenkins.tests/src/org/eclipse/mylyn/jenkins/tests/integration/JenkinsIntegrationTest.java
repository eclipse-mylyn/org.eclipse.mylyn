/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests.integration;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.mylyn.builds.core.IBooleanParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IParameterDefinition;
import org.eclipse.mylyn.builds.core.spi.BuildPlanRequest;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.jenkins.core.JenkinsCore;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsFixture;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsHarness;
import org.junit.Ignore;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
//FIXME: [#772] Nothing for Jenkins available at https://mylyn.frank-becker.de/mylyn_idx/service
@Ignore
public class JenkinsIntegrationTest extends TestCase {

	private JenkinsHarness harness;

	@Override
	protected void setUp() throws Exception {
		harness = JenkinsFixture.current().createHarness();
	}

	@Override
	protected void tearDown() throws Exception {
		harness.dispose();
	}

	public void testPlanParameters() throws Exception {
		RepositoryLocation location = harness.getFixture().location();
		BuildServerBehaviour behaviour = JenkinsCore.createConnector(null).getBehaviour(location);
		BuildPlanRequest request = new BuildPlanRequest(Collections.singletonList(harness.getPlanParameterized()));

		List<IBuildPlan> plans = behaviour.getPlans(request, null);
		assertEquals("Expected one plan, got: " + plans, 1, plans.size());

		IBuildPlan plan = plans.get(0);
		assertEquals(harness.getPlanParameterized(), plan.getName());

		List<IParameterDefinition> parameters = plan.getParameterDefinitions();
		IBooleanParameterDefinition booleanParameter = BuildFactory.eINSTANCE.createBooleanParameterDefinition();
		booleanParameter.setName("Boolean Parameter");
		booleanParameter.setDescription("Boolean Parameter Description.");
		booleanParameter.setDefaultValue(true);
		booleanParameter.setContainingBuildPlan(plan);
		assertEObjectsEquals(booleanParameter, parameters.get(0));
	}

	private void assertEObjectsEquals(Object o1, Object o2) {
		boolean equals = EcoreUtil.equals((EObject) o1, (EObject) o2);
		if (!equals) {
			// fail with meaningful message
			assertEquals(o1.toString(), o2.toString());
		}
	}

}
