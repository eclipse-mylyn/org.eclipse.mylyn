/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.hudson.core.HudsonServerBehaviour;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBallColor;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelHealthReport;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;

/**
 * @author Markus Knittig
 */
public class HudsonServerBehaviourTest extends TestCase {

	public void testParseJobHealthNoReport() throws Exception {
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(new RepositoryLocation(),
				new HudsonConfigurationCache());
		HudsonModelJob job = new HudsonModelJob();
		job.setColor(HudsonModelBallColor.YELLOW);
		assertEquals(-1, behaviour.parseJob(job).getHealth());
	}

	public void testParseJobHealth() throws Exception {
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(new RepositoryLocation(),
				new HudsonConfigurationCache());
		HudsonModelJob job = new HudsonModelJob();
		job.setColor(HudsonModelBallColor.YELLOW);
		HudsonModelHealthReport healthReport = new HudsonModelHealthReport();
		healthReport.setScore(80);
		job.getHealthReport().add(healthReport);
		assertEquals(80, behaviour.parseJob(job).getHealth());
	}

	public void testBuildElementFromUrl() throws Exception {
		IBuildServer server = BuildFactory.eINSTANCE.createBuildServer();
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(new RepositoryLocation("http://server"),
				new HudsonConfigurationCache());
		IBuildElement element = behaviour.getBuildElementFromUrl(server, "http://server/job/my-plan/3/");
		assertNotNull(element);
		assertTrue("Expected IBuild, got " + element.getClass(), element instanceof IBuild);
		IBuild build = (IBuild) element;
		assertEquals("3", build.getId());
		assertEquals("my-plan", build.getPlan().getId());
	}

}
