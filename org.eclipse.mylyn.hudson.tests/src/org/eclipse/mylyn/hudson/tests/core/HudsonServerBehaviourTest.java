/*******************************************************************************
 * Copyright (c) 2010, 2016 Markus Knittig and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Markus Knittig - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.hudson.core.HudsonServerBehaviour;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBallColor;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelHealthReport;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.junit.Assert;

import junit.framework.TestCase;

/**
 * @author Markus Knittig
 */
public class HudsonServerBehaviourTest extends TestCase {

	public void testParseJobHealthNoReport() throws Exception {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		String baseUrl = "http://test.org/jenkins/";
		repositoryLocation.setUrl(baseUrl);
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(repositoryLocation, new HudsonConfigurationCache());
		HudsonModelJob job = new HudsonModelJob();
		job.setUrl(behaviour.getLocation().getUrl() + "/job/test-job");

		job.setColor(HudsonModelBallColor.YELLOW);
		assertEquals(-1, behaviour.parseJob(job).getHealth());
	}

	public void testParseJobHealth() throws Exception {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		String baseUrl = "http://test.org/jenkins/";
		repositoryLocation.setUrl(baseUrl);
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(repositoryLocation, new HudsonConfigurationCache());
		HudsonModelJob job = new HudsonModelJob();
		job.setUrl(behaviour.getLocation().getUrl() + "/job/test-job");
		job.setColor(HudsonModelBallColor.YELLOW);
		HudsonModelHealthReport healthReport = new HudsonModelHealthReport();
		healthReport.setScore(80);
		job.getHealthReport().add(healthReport);
		assertEquals(80, behaviour.parseJob(job).getHealth());
	}

	public void testParseJobNoColor() throws Exception {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		String baseUrl = "http://test.org/jenkins/";
		repositoryLocation.setUrl(baseUrl);
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(repositoryLocation, new HudsonConfigurationCache());
		HudsonModelJob job = new HudsonModelJob();
		job.setUrl(behaviour.getLocation().getUrl() + "/job/test-job");

		IBuildPlan buildPlan = behaviour.parseJob(job);

		assertNull(buildPlan.getState());
		assertNull(buildPlan.getStatus());
	}

	public void testParseJobRunningColor() throws Exception {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		String baseUrl = "http://test.org/jenkins/";
		repositoryLocation.setUrl(baseUrl);
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(repositoryLocation, new HudsonConfigurationCache());
		HudsonModelJob job = new HudsonModelJob();
		job.setUrl(behaviour.getLocation().getUrl() + "/job/test-job");

		for (HudsonModelBallColor color : getRunningColors()) {
			job.setColor(color);
			IBuildPlan buildPlan = behaviour.parseJob(job);

			assertEquals(BuildState.RUNNING, buildPlan.getState());
		}
	}

	public void testParseJobStoppedColor() throws Exception {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		String baseUrl = "http://test.org/jenkins/";
		repositoryLocation.setUrl(baseUrl);
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(repositoryLocation, new HudsonConfigurationCache());
		HudsonModelJob job = new HudsonModelJob();
		job.setUrl(behaviour.getLocation().getUrl() + "/job/test-job");

		for (HudsonModelBallColor color : getStoppedColors()) {
			job.setColor(color);
			IBuildPlan buildPlan = behaviour.parseJob(job);

			assertEquals(BuildState.STOPPED, buildPlan.getState());
		}
	}

	public void testParseJobNestedJob() throws Exception {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		String baseUrl = "http://test.org/jenkins/";
		repositoryLocation.setUrl(baseUrl);
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(repositoryLocation, new HudsonConfigurationCache());

		HudsonModelJob nestedJob = new HudsonModelJob();
		String nestedJobUrl = baseUrl + "/test-folder/job/test-nested-one/";
		nestedJob.setName("test-nested-one");
		nestedJob.setUrl(nestedJobUrl);
		IBuildPlan buildPlan = behaviour.parseJob(nestedJob);
		Assert.assertEquals(nestedJobUrl, buildPlan.getId());
	}

	public void testParseJobTopLevelJob() throws Exception {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		String baseUrl = "http://test.org/jenkins/";
		repositoryLocation.setUrl(baseUrl);
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(repositoryLocation, new HudsonConfigurationCache());

		HudsonModelJob topLevelJob = new HudsonModelJob();
		String jobName = "test-succeeding";
		topLevelJob.setName(jobName);
		topLevelJob.setUrl(baseUrl + "job/test-succeeding/");
		IBuildPlan buildPlan = behaviour.parseJob(topLevelJob);
		Assert.assertEquals(jobName, buildPlan.getId());
	}

	private Set<HudsonModelBallColor> getRunningColors() {
		Set<HudsonModelBallColor> result = new HashSet<HudsonModelBallColor>();
		for (HudsonModelBallColor color : HudsonModelBallColor.values()) {
			if (color.value().endsWith("_anime")) {
				result.add(color);
			}
		}
		return result;
	}

	private Set<HudsonModelBallColor> getStoppedColors() {
		Set<HudsonModelBallColor> result = new HashSet<HudsonModelBallColor>();
		for (HudsonModelBallColor color : HudsonModelBallColor.values()) {
			if (!color.value().endsWith("_anime")) {
				result.add(color);
			}
		}
		return result;
	}

}
