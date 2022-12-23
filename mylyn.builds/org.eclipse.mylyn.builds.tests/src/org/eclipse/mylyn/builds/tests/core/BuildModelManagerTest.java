/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.tests.core;

import java.io.File;
import java.util.Collections;

import junit.framework.TestCase;

import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.builds.internal.core.util.BuildModelManager;
import org.eclipse.mylyn.builds.tests.support.MockBuildLoader;

/**
 * @author Steffen Pingel
 */
public class BuildModelManagerTest extends TestCase {

	private BuildModelManager manager;

	private File file;

	@Override
	protected void setUp() throws Exception {
		file = File.createTempFile("builds", ".xmi");
		file.deleteOnExit();
		manager = new BuildModelManager(file, new MockBuildLoader());
	}

	@Override
	protected void tearDown() throws Exception {
		file.delete();
	}

	public void testSaveEmptyModel() throws Exception {
		manager.save();
	}

	public void testSaveDanglingPlan() throws Exception {
		IBuildPlan plan = BuildFactory.eINSTANCE.createBuildPlan();
		IBuild build = BuildFactory.eINSTANCE.createBuild();
		build.setPlan(plan);
		manager.getModel().getBuilds().add(build);
		manager.save();
		assertEquals(Collections.EMPTY_LIST, manager.getModel().getBuilds());
	}

	public void testSaveDanglingPlanWithServer() throws Exception {
		IBuildServer server = BuildFactory.eINSTANCE.createBuildServer();
		IBuildPlan plan1 = BuildFactory.eINSTANCE.createBuildPlan();
		plan1.setServer(server);
		IBuildPlan plan2 = BuildFactory.eINSTANCE.createBuildPlan();
		manager.getModel().getServers().add(server);
		manager.getModel().getPlans().add(plan1);
		manager.getModel().getPlans().add(plan2);
		manager.save();
		assertEquals(Collections.singletonList(server), manager.getModel().getServers());
		assertEquals(Collections.singletonList(plan1), manager.getModel().getPlans());
	}

}
