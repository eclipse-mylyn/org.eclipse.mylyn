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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
import org.eclipse.mylyn.internal.jenkins.core.client.RestfulJenkinsClient;
import org.eclipse.mylyn.internal.jenkins.core.client.RestfulJenkinsClient.BuildId;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsFixture;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsHarness;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
/**
 * Test cases for {@link RestfulJenkinsClient}.
 *
 * @author Markus Knittig
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
@EnabledIf("org.eclipse.mylyn.jenkins.tests.SuiteSetup#isFixtureActive")
public class JenkinsClientTest {

	private JenkinsHarness harness;

	@BeforeEach
	public void setUp() throws Exception {
		harness = JenkinsFixture.current().createHarness();
	}

	@AfterEach
	protected void tearDown() throws Exception {
		harness.dispose();
	}

	@Test
	public void testValidateValidUrl() throws Exception {
		// standard connect
		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		JenkinsServerInfo info = client.validate(null);
		assertEquals(harness.getFixture().getType(), info.getType());
	}

	@Test
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

	@Test
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

	@Test
	public void testGetJobs() throws Exception {
		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		harness.ensureHasRun(harness.getPlanFailing());
		harness.ensureHasRun(harness.getPlanSucceeding());

		List<HudsonModelJob> jobs = client.getJobs(null, null);

		assertThat(jobs, hasItem(hasProperty("name", is(harness.getPlanFailing()))));
		assertThat(jobs, hasItem(hasProperty("name", is(harness.getPlanSucceeding()))));

		assertTrue(jobs.stream().anyMatch(job -> !job.getHealthReport().isEmpty()),
				"Expected attribute 'healthReport' in " + jobs);
	}

	@Test
	public void testGetManyJobs() throws Exception {
		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		harness.ensureHasRun(harness.getPlanSucceeding());

		List<String> jobIDs = Collections.nCopies(1000, harness.getPlanSucceeding());
		List<HudsonModelJob> jobs = client.getJobs(jobIDs, null);
		assertThat(jobs, hasItem(hasProperty("name", is(harness.getPlanSucceeding()))));

		assertTrue(jobs.stream().anyMatch(job -> !job.getHealthReport().isEmpty()),
				"Expected attribute 'healthReport' in " + jobs);
	}

	@Test
	public void testGetNestedJobs() throws Exception {
		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(null, null);
		assertThat(jobs, not(hasItem(hasProperty("name", is(harness.getPlanFolder())))));
		assertThat(jobs, hasItem(hasProperty("name", is(harness.getPlanNestedOne()))));
		assertThat(jobs, not(hasItem(hasProperty("name", is(harness.getPlanSubFolder())))));
		assertThat(jobs, hasItem(hasProperty("name", is(harness.getPlanNestedTwo()))));
	}

	@Test
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

	@Test
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

	@Test
	public void testGetJobGit() throws Exception {
		harness.ensureHasRun(harness.getPlanGit());

		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(Collections.singletonList(harness.getPlanGit()), null);
		assertEquals(1, jobs.size());

		HudsonModelJob job = jobs.get(0);
		HudsonModelBuild build = client.getBuild(job, BuildId.LAST.getBuild(), null);
		assertNotNull(build.getAction());
	}

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testRunNestedJob() throws Exception {
		if (!JenkinsFixture.current().canAuthenticate()) {
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
