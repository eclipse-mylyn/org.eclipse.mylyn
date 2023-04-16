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
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests.client;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.hudson.tests.support.HudsonFixture;
import org.eclipse.mylyn.hudson.tests.support.HudsonHarness;
import org.eclipse.mylyn.hudson.tests.support.HudsonTestUtil;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonException;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonResourceNotFoundException;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonServerInfo;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonServerInfo.Type;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient.BuildId;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBallColor;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBuild;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelRun;

import junit.framework.TestCase;

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
	}

	public void testGetJobs() throws Exception {
		RestfulHudsonClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		harness.ensureHasRun(harness.getPlanFailing());
		harness.ensureHasRun(harness.getPlanSucceeding());

		List<HudsonModelJob> jobs = client.getJobs(null, null);
		HudsonTestUtil.assertContains(jobs, harness.getPlanFailing());
		HudsonTestUtil.assertContains(jobs, harness.getPlanSucceeding());
		HudsonTestUtil.assertHealthReport(jobs);
	}

	public void testGetManyJobs() throws Exception {
		RestfulHudsonClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		harness.ensureHasRun(harness.getPlanSucceeding());

		List<String> jobIDs = Collections.nCopies(1000, harness.getPlanSucceeding());
		List<HudsonModelJob> jobs = client.getJobs(jobIDs, null);
		HudsonTestUtil.assertContains(jobs, harness.getPlanSucceeding());
		HudsonTestUtil.assertHealthReport(jobs);
	}

	public void testGetNestedJobs() throws Exception {
		if (harness.getFixture().getType().equals(Type.HUDSON)) {
			/* HUDSON does not support nested jobs */
			return;
		}

		RestfulHudsonClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(null, null);
		HudsonTestUtil.assertContainsNot(jobs, harness.getPlanFolder());
		HudsonTestUtil.assertContains(jobs, harness.getPlanNestedOne());
		HudsonTestUtil.assertContainsNot(jobs, harness.getPlanSubFolder());
		HudsonTestUtil.assertContains(jobs, harness.getPlanNestedTwo());
	}

	public void testGetJobsWithWhitespaces() throws Exception {
		harness.ensureHasRun(harness.getPlanWhitespace());

		final RestfulHudsonClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		List<HudsonModelJob> jobs = client.getJobs(Collections.singletonList(harness.getPlanWhitespace()), null);
		assertEquals(1, jobs.size());
		HudsonModelJob job = jobs.get(0);
		assertEquals(harness.getSuccessColor(), job.getColor());

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
		harness.ensureHasRun(harness.getPlanGit());

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
		ensureHasRunOnce(client, jobName, HudsonModelBallColor.RED);

		runBuild(client, jobName);
		HudsonTestUtil.poll(new Callable<Object>() {
			public Object call() throws Exception {
				assertEquals(HudsonModelBallColor.RED_ANIME, harness.getJob(jobName).getColor());
				return null;
			}
		});
	}

	private void ensureHasRunOnce(RestfulHudsonClient client, final String jobName,
			final HudsonModelBallColor expectedColor) throws Exception {
		if (!expectedColor.equals(harness.getJob(jobName).getColor())) {
			client.runBuild(harness.getJob(jobName), null, null);
			HudsonTestUtil.poll(new Callable<Object>() {
				public Object call() throws Exception {
					assertEquals(expectedColor, harness.getJob(jobName).getColor());
					return null;
				}
			});
		}
	}

	public void testRunBuildSucceeding() throws Exception {
		if (!HudsonFixture.current().canAuthenticate()) {
			// ignore
			return;
		}

		final String jobName = harness.getPlanSucceeding();
		RestfulHudsonClient client = harness.connect();
		ensureHasRunOnce(client, jobName, harness.getSuccessColor());

		runBuild(client, jobName);
		HudsonTestUtil.poll(new Callable<Object>() {
			public Object call() throws Exception {
				assertEquals(harness.getSuccessAnimeColor(), harness.getJob(jobName).getColor());
				return null;
			}
		});
	}

	private void runBuild(RestfulHudsonClient client, final String jobName) throws HudsonException {
		try {
			client.runBuild(harness.getJob(jobName), null, null);
		} catch (HudsonException e) {
			if (e.getMessage().contains("Bad Gateway")) {
				client.runBuild(harness.getJob(jobName), null, null);
			}
		}
	}

	public void testAbortBuild() throws Exception {
		if (!HudsonFixture.current().canAuthenticate()) {
			// ignore
			return;
		}

		final String jobName = harness.getPlanSucceeding();
		RestfulHudsonClient client = harness.connect();

		runBuild(client, jobName);
		HudsonTestUtil.poll(new Callable<Object>() {
			public Object call() throws Exception {
				HudsonModelRun run = harness.getJob(jobName).getLastBuild();
				HudsonModelBuild build = harness.getBuild(jobName, run.getNumber());
				assertTrue(build.isBuilding());
				return null;
			}
		});
		abortBuild(client, jobName);
		HudsonTestUtil.poll(new Callable<Object>() {
			public Object call() throws Exception {
				assertEquals(harness.getAbortedColor(), harness.getJob(jobName).getColor());
				return null;
			}
		});
	}

	private void abortBuild(RestfulHudsonClient client, final String jobName) throws HudsonException {
		HudsonModelJob job = harness.getJob(jobName);
		HudsonModelBuild build = new HudsonModelBuild();
		build.setNumber(job.getLastBuild().getNumber());
		try {
			client.abortBuild(job, build, null);
		} catch (HudsonException e) {
			if (e.getMessage().contains("Bad Gateway")) {
				client.abortBuild(job, build, null);
			}
		}
	}

	// FIXME GNL: Have to disable UIHarness to make the rest of the hudson tests pass.
//	public void testRunBuildGuest() throws Exception {
//		final String jobName = harness.getPlanSucceeding();
//		RestfulHudsonClient client = harness.connect(PrivilegeLevel.GUEST);
//		try {
//			client.runBuild(harness.getJob(jobName), null, null);
//			fail("Expected HudsonException");
//		} catch (HudsonException expected) {
//			// ignore
//		}
//	}

	public void testRunNestedJob() throws Exception {
		if (!HudsonFixture.current().canAuthenticate() || harness.getFixture().getType().equals(Type.HUDSON)) {
			// ignore
			return;
		}

		final String jobName = harness.getPlanNestedOne();
		RestfulHudsonClient client = harness.connect();
		ensureHasRunOnce(client, jobName, harness.getSuccessAnimeColor());

		client.runBuild(harness.getJob(jobName), null, null);
		HudsonTestUtil.poll(new Callable<Object>() {
			public Object call() throws Exception {
				assertEquals(harness.getSuccessAnimeColor(), harness.getJob(jobName).getColor());
				return null;
			}
		});
	}
}
