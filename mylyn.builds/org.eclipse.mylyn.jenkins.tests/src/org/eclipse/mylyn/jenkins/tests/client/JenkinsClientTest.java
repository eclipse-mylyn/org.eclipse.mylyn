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
 *     Tasktop Technologies - improvements
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests.client;

import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBallColor;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBuild;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelRun;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsException;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsResourceNotFoundException;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsServerInfo;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsServerInfo.Type;
import org.eclipse.mylyn.internal.jenkins.core.client.RestfulJenkinsClient;
import org.eclipse.mylyn.internal.jenkins.core.client.RestfulJenkinsClient.BuildId;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsFixture;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsHarness;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsTestUtil;

import junit.framework.TestCase;

/**
 * Test cases for {@link RestfulJenkinsClient}.
 *
 * @author Markus Knittig
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class JenkinsClientTest extends TestCase {

	private JenkinsHarness harness;

	@Override
	protected void setUp() throws Exception {
		harness = JenkinsFixture.current().createHarness();
	}

	@Override
	protected void tearDown() throws Exception {
		harness.dispose();
	}

	public void testValidateValidUrl() throws Exception {
		// standard connect
		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		JenkinsServerInfo info = client.validate(null);
		assertEquals(harness.getFixture().getType(), info.getType());
	}

	public void testValidateValidUrlAuthenticate() throws Exception {
		if (!JenkinsFixture.current().canAuthenticate()) {
			// ignore
			return;
		}
		// standard connect
		RestfulJenkinsClient client = harness.connect();
		JenkinsServerInfo info = client.validate(null);
		assertEquals(harness.getFixture().getType(), info.getType());
	}

	public void testValidateExpiredCookie() throws Exception {
		if (!JenkinsFixture.current().canAuthenticate()) {
			// ignore
			return;
		}
		RestfulJenkinsClient client = harness.connect();
		client.validate(null);
		// clear cookies
		client.reset();
		// TODO try an operation that requires authentication
		JenkinsServerInfo info = client.validate(null);
		assertEquals(harness.getFixture().getType(), info.getType());
	}

	public void testGetJobs() throws Exception {
		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		harness.ensureHasRun(harness.getPlanFailing());
		harness.ensureHasRun(harness.getPlanSucceeding());

		List<HudsonModelJob> jobs = client.getJobs(null, null);
		JenkinsTestUtil.assertContains(jobs, harness.getPlanFailing());
		JenkinsTestUtil.assertContains(jobs, harness.getPlanSucceeding());
		JenkinsTestUtil.assertHealthReport(jobs);
	}

	public void testGetManyJobs() throws Exception {
		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		harness.ensureHasRun(harness.getPlanSucceeding());

		List<String> jobIDs = Collections.nCopies(1000, harness.getPlanSucceeding());
		List<HudsonModelJob> jobs = client.getJobs(jobIDs, null);
		JenkinsTestUtil.assertContains(jobs, harness.getPlanSucceeding());
		JenkinsTestUtil.assertHealthReport(jobs);
	}

	public void testGetNestedJobs() throws Exception {
		if (harness.getFixture().getType().equals(Type.HUDSON)) {
			/* HUDSON does not support nested jobs */
			return;
		}

		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(null, null);
		JenkinsTestUtil.assertContainsNot(jobs, harness.getPlanFolder());
		JenkinsTestUtil.assertContains(jobs, harness.getPlanNestedOne());
		JenkinsTestUtil.assertContainsNot(jobs, harness.getPlanSubFolder());
		JenkinsTestUtil.assertContains(jobs, harness.getPlanNestedTwo());
	}

	public void testGetJobsWithWhitespaces() throws Exception {
		harness.ensureHasRun(harness.getPlanWhitespace());

		final RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(Collections.singletonList(harness.getPlanWhitespace()), null);
		assertEquals(1, jobs.size());
		HudsonModelJob job = jobs.get(0);
		assertEquals(harness.getSuccessColor(), job.getColor());

		HudsonModelBuild build = client.getBuild(job, job.getLastBuild(), null);
		assertNotNull(build);
	}

	public void testGetJobDisabled() throws Exception {
		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(Collections.singletonList(harness.getPlanDisabled()), null);
		assertEquals(1, jobs.size());

		HudsonModelJob job = jobs.get(0);
		assertEquals(HudsonModelBallColor.DISABLED, job.getColor());

		try {
			HudsonModelBuild build = client.getBuild(job, BuildId.LAST.getBuild(), null);
			fail("Expected JenkinsResourceNotFoundException, since " + harness.getPlanDisabled()
			+ " was never built, got: " + build);
		} catch (JenkinsResourceNotFoundException e) {
			// expected
		}
	}

	public void testGetJobGit() throws Exception {
		harness.ensureHasRun(harness.getPlanGit());

		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(Collections.singletonList(harness.getPlanGit()), null);
		assertEquals(1, jobs.size());

		HudsonModelJob job = jobs.get(0);
		HudsonModelBuild build = client.getBuild(job, BuildId.LAST.getBuild(), null);
		assertNotNull(build.getAction());
	}

	public void testRunBuildFailing() throws Exception {
		if (!JenkinsFixture.current().canAuthenticate()) {
			// ignore
			return;
		}

		final String jobName = harness.getPlanFailing();
		RestfulJenkinsClient client = harness.connect();
		ensureHasRunOnce(client, jobName, HudsonModelBallColor.RED);

		runBuild(client, jobName);
		JenkinsTestUtil.poll(() -> {
			assertEquals(HudsonModelBallColor.RED_ANIME, harness.getJob(jobName).getColor());
			return null;
		});
	}

	private void ensureHasRunOnce(RestfulJenkinsClient client, final String jobName,
			final HudsonModelBallColor expectedColor) throws Exception {
		if (!expectedColor.equals(harness.getJob(jobName).getColor())) {
			client.runBuild(harness.getJob(jobName), null, null);
			JenkinsTestUtil.poll(() -> {
				assertEquals(expectedColor, harness.getJob(jobName).getColor());
				return null;
			});
		}
	}

	public void testRunBuildSucceeding() throws Exception {
		if (!JenkinsFixture.current().canAuthenticate()) {
			// ignore
			return;
		}

		final String jobName = harness.getPlanSucceeding();
		RestfulJenkinsClient client = harness.connect();
		ensureHasRunOnce(client, jobName, harness.getSuccessColor());

		runBuild(client, jobName);
		JenkinsTestUtil.poll(() -> {
			assertEquals(harness.getSuccessAnimeColor(), harness.getJob(jobName).getColor());
			return null;
		});
	}

	private void runBuild(RestfulJenkinsClient client, final String jobName) throws JenkinsException {
		try {
			client.runBuild(harness.getJob(jobName), null, null);
		} catch (JenkinsException e) {
			if (e.getMessage().contains("Bad Gateway")) {
				client.runBuild(harness.getJob(jobName), null, null);
			}
		}
	}

	public void testAbortBuild() throws Exception {
		if (!JenkinsFixture.current().canAuthenticate()) {
			// ignore
			return;
		}

		final String jobName = harness.getPlanSucceeding();
		RestfulJenkinsClient client = harness.connect();

		runBuild(client, jobName);
		JenkinsTestUtil.poll(() -> {
			HudsonModelRun run = harness.getJob(jobName).getLastBuild();
			HudsonModelBuild build = harness.getBuild(jobName, run.getNumber());
			assertTrue(build.isBuilding());
			return null;
		});
		abortBuild(client, jobName);
		JenkinsTestUtil.poll(() -> {
			assertEquals(harness.getAbortedColor(), harness.getJob(jobName).getColor());
			return null;
		});
	}

	private void abortBuild(RestfulJenkinsClient client, final String jobName) throws JenkinsException {
		HudsonModelJob job = harness.getJob(jobName);
		HudsonModelBuild build = new HudsonModelBuild();
		build.setNumber(job.getLastBuild().getNumber());
		try {
			client.abortBuild(job, build, null);
		} catch (JenkinsException e) {
			if (e.getMessage().contains("Bad Gateway")) {
				client.abortBuild(job, build, null);
			}
		}
	}

	// FIXME GNL: Have to disable UIHarness to make the rest of the hudson tests pass.
//	public void testRunBuildGuest() throws Exception {
//		final String jobName = harness.getPlanSucceeding();
//		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.GUEST);
//		try {
//			client.runBuild(harness.getJob(jobName), null, null);
//			fail("Expected JenkinsException");
//		} catch (JenkinsException expected) {
//			// ignore
//		}
//	}

	public void testRunNestedJob() throws Exception {
		if (!JenkinsFixture.current().canAuthenticate() || harness.getFixture().getType().equals(Type.HUDSON)) {
			// ignore
			return;
		}

		final String jobName = harness.getPlanNestedOne();
		RestfulJenkinsClient client = harness.connect();
		ensureHasRunOnce(client, jobName, harness.getSuccessAnimeColor());

		client.runBuild(harness.getJob(jobName), null, null);
		JenkinsTestUtil.poll(() -> {
			assertEquals(harness.getSuccessAnimeColor(), harness.getJob(jobName).getColor());
			return null;
		});
	}
}
