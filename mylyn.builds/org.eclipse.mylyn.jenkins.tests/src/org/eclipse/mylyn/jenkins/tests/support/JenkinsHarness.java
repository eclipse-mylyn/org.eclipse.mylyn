/*******************************************************************************
 * Copyright (c) 2011, 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.jenkins.tests.support;

import static org.junit.Assert.assertNotNull;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBallColor;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBuild;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsException;
import org.eclipse.mylyn.internal.jenkins.core.client.RestfulJenkinsClient;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class JenkinsHarness {

	private static final String PLAN_DISABLED = "test-disabled";

	private static final String PLAN_FAILING = "test-failing";

	private static final String PLAN_PARAMETERIZED = "test-parameterized";

	private static final String PLAN_SUCCEEDING = "test-succeeding";

	private static final String PLAN_WHITESPACE = "test-white space";

	private static final String PLAN_GIT = "test-git";

	private static final String PLAN_FOLDER = "test-folder";

	private static final String PLAN_SUB_FOLDER = "test-sub-folder";

	private static final String PLAN_NESTED_ONE = "test-nested-one";

	private static final String PLAN_NESTED_TWO = "test-nested-two";

	private RestfulJenkinsClient client;

	private final JenkinsFixture fixture;

	public JenkinsHarness(JenkinsFixture fixture) {
		this.fixture = fixture;
	}

	public RestfulJenkinsClient connect() throws Exception {
		return connect(PrivilegeLevel.USER);
	}

	public RestfulJenkinsClient connect(PrivilegeLevel level) throws Exception {
		client = JenkinsFixture.connect(fixture.location(level));
		return client;
	}

	public RestfulJenkinsClient privilegedClient() throws Exception {
		return JenkinsFixture.connect(fixture.location(PrivilegeLevel.USER));
	}

	public void dispose() {
	}

	public JenkinsFixture getFixture() {
		return fixture;
	}

	public HudsonModelJob getJob(String name) throws JenkinsException {
		return getJob(client, name);
	}

	private HudsonModelJob getJob(RestfulJenkinsClient client, String name) throws JenkinsException {
		for (HudsonModelJob job : client.getJobs(null, null)) {
			if (job.getName().equals(name)) {
				return job;
			}
		}
		return null;
	}

	public HudsonModelBuild getBuild(String jobName, int buildNumber) throws JenkinsException {
		HudsonModelJob job = getJob(client, jobName);
		if (job == null) {
			return null;
		}
		HudsonModelBuild build = new HudsonModelBuild();
		build.setNumber(buildNumber);
		return getBuild(client, job, build);
	}

	private HudsonModelBuild getBuild(RestfulJenkinsClient client, HudsonModelJob job, HudsonModelBuild build)
			throws JenkinsException {
		return client.getBuild(job, build, null);
	}

	public String getPlanDisabled() {
		return PLAN_DISABLED;
	}

	public String getPlanFailing() {
		return PLAN_FAILING;
	}

	public String getPlanParameterized() {
		return PLAN_PARAMETERIZED;
	}

	public String getPlanSucceeding() {
		return PLAN_SUCCEEDING;
	}

	public String getPlanWhitespace() {
		return PLAN_WHITESPACE;
	}

	public String getPlanGit() {
		return PLAN_GIT;
	}

	public String getPlanFolder() {
		return PLAN_FOLDER;
	}

	public String getPlanSubFolder() {
		return PLAN_SUB_FOLDER;
	}

	public String getPlanNestedOne() {
		return PLAN_NESTED_ONE;
	}

	public String getPlanNestedTwo() {
		return PLAN_NESTED_TWO;
	}

	public HudsonModelJob ensureHasRun(final String plan) throws Exception {
		final RestfulJenkinsClient privilegedClient = privilegedClient();
		HudsonModelJob job = getJob(privilegedClient, plan);
		if (job.getLastCompletedBuild() == null) {
			privilegedClient.runBuild(job, null, null);
			job = JenkinsTestUtil.poll(() -> {
				HudsonModelJob job1 = getJob(privilegedClient, plan);
				assertNotNull(job1.getLastCompletedBuild());
				return job1;
			});
		}
		return job;
	}

	public HudsonModelBallColor getSuccessColor() {
		if (getFixture().isHudson() && getFixture().getVersion().compareTo("3.0.1") >= 0) {
			return HudsonModelBallColor.GREEN;
		} else {
			return HudsonModelBallColor.BLUE;
		}
	}

	public HudsonModelBallColor getSuccessAnimeColor() {
		if (getFixture().isHudson() && getFixture().getVersion().compareTo("3.0.1") >= 0) {
			return HudsonModelBallColor.GREEN_ANIME;
		} else {
			return HudsonModelBallColor.BLUE_ANIME;
		}
	}

	public HudsonModelBallColor getAbortedColor() {
		return HudsonModelBallColor.ABORTED;
	}

}
