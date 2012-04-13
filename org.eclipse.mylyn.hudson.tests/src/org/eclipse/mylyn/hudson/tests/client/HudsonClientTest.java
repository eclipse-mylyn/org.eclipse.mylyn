/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests.client;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.hudson.tests.support.HudsonFixture;
import org.eclipse.mylyn.hudson.tests.support.HudsonHarness;
import org.eclipse.mylyn.hudson.tests.support.HudsonTestUtil;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonException;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonResourceNotFoundException;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonServerInfo;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient.BuildId;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBallColor;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBuild;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;

/**
 * Test cases for {@link RestfulHudsonClient}.
 * 
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class HudsonClientTest extends TestCase {

	private HudsonHarness harness;

	@Override
	protected void setUp() throws Exception {
		harness = HudsonFixture.current().createHarness();
	}

	@Override
	protected void tearDown() throws Exception {
		harness.dispose();
	}

	public void testValidateValidUrl() throws Exception {
		// standard connect
		RestfulHudsonClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		HudsonServerInfo info = client.validate(null);
		assertEquals(harness.getFixture().getType(), info.getType());
		assertEquals(harness.getFixture().getVersion(), info.getVersion());
	}

	public void testValidateValidUrlAuthenticate() throws Exception {
		if (!HudsonFixture.current().canAuthenticate()) {
			// ignore
			return;
		}
		// standard connect
		RestfulHudsonClient client = harness.connect();
		HudsonServerInfo info = client.validate(null);
		assertEquals(harness.getFixture().getType(), info.getType());
		assertEquals(harness.getFixture().getVersion(), info.getVersion());
	}

	public void testValidateExpiredCookie() throws Exception {
		if (!HudsonFixture.current().canAuthenticate()) {
			// ignore
			return;
		}
		RestfulHudsonClient client = harness.connect();
		client.validate(null);
		// clear cookies
		client.reset();
		// TODO try an operation that requires authentication 
		HudsonServerInfo info = client.validate(null);
		assertEquals(harness.getFixture().getType(), info.getType());
		assertEquals(harness.getFixture().getVersion(), info.getVersion());
	}

	public void testGetJobs() throws Exception {
		RestfulHudsonClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(null, null);
		HudsonTestUtil.assertContains(jobs, harness.getPlanFailing());
		HudsonTestUtil.assertContains(jobs, harness.getPlanSucceeding());
		HudsonTestUtil.assertHealthReport(jobs);
	}

	public void testGetJobsWithWhitespaces() throws Exception {
		RestfulHudsonClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(Collections.singletonList(harness.getPlanWhitespace()), null);
		assertEquals(1, jobs.size());

		HudsonModelJob job = jobs.get(0);
		assertEquals(HudsonModelBallColor.BLUE, job.getColor());

		HudsonModelBuild build = client.getBuild(job, job.getLastBuild(), null);
		assertNotNull(build);
	}

	public void testGetJobDisabled() throws Exception {
		RestfulHudsonClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(Collections.singletonList(harness.getPlanDisabled()), null);
		assertEquals(1, jobs.size());

		HudsonModelJob job = jobs.get(0);
		assertEquals(HudsonModelBallColor.DISABLED, job.getColor());

		try {
			HudsonModelBuild build = client.getBuild(job, BuildId.LAST.getBuild(), null);
			fail("Expected HudsonResourceNotFoundException, since " + harness.getPlanDisabled()
					+ " was never built, got: " + build);
		} catch (HudsonResourceNotFoundException e) {
			// expected
		}
	}

	public void testGetJobGit() throws Exception {
		RestfulHudsonClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(Collections.singletonList(harness.getPlanGit()), null);
		assertEquals(1, jobs.size());

		HudsonModelJob job = jobs.get(0);
		HudsonModelBuild build = client.getBuild(job, BuildId.LAST.getBuild(), null);
		assertNotNull(build.getAction());
	}

	public void testRunBuildFailing() throws Exception {
		if (!HudsonFixture.current().canAuthenticate()) {
			// ignore
			return;
		}

		final String jobName = harness.getPlanFailing();
		RestfulHudsonClient client = harness.connect();
		client.runBuild(harness.getJob(jobName), null, null);
		HudsonTestUtil.poll(new Callable<Object>() {
			public Object call() throws Exception {
				assertEquals(HudsonModelBallColor.RED_ANIME, harness.getJob(jobName).getColor());
				return null;
			}
		});
	}

	public void testRunBuildSucceeding() throws Exception {
		if (!HudsonFixture.current().canAuthenticate()) {
			// ignore
			return;
		}

		final String jobName = harness.getPlanSucceeding();
		RestfulHudsonClient client = harness.connect();
		client.runBuild(harness.getJob(jobName), null, null);
		HudsonTestUtil.poll(new Callable<Object>() {
			public Object call() throws Exception {
				assertEquals(HudsonModelBallColor.BLUE_ANIME, harness.getJob(jobName).getColor());
				return null;
			}
		});
	}

	public void testRunBuildGuest() throws Exception {
		final String jobName = harness.getPlanSucceeding();
		RestfulHudsonClient client = harness.connect(PrivilegeLevel.GUEST);
		try {
			client.runBuild(harness.getJob(jobName), null, null);
			fail("Expected HudsonException");
		} catch (HudsonException expected) {
			// ignore
		}
	}

}
