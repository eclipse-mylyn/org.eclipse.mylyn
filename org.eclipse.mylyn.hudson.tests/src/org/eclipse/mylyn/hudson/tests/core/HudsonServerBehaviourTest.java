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

import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
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
}
