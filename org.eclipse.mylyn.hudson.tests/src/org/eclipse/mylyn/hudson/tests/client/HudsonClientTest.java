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

import java.util.List;
import java.util.concurrent.Callable;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.util.ProgressUtil;
import org.eclipse.mylyn.hudson.tests.support.HudsonFixture;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonException;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelBallColor;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.eclipse.mylyn.tests.util.TestUtil;
import org.eclipse.mylyn.tests.util.TestUtil.Credentials;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

/**
 * Test cases for {@link RestfulHudsonClient}.
 * 
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class HudsonClientTest extends TestCase {

	private static final String PLAN_SUCCEEDING = "test-succeeding";

	private static final String PLAN_FAILING = "test-failing";

	private static final long POLL_TIMEOUT = 30 * 1000;

	private static final long POLL_INTERVAL = 2 * 1000;

	RestfulHudsonClient client;

	HudsonFixture fixture;

	@Override
	protected void setUp() throws Exception {
		fixture = HudsonFixture.current();
	}

	public void testValidate() throws Exception {
		// standard connect
		client = fixture.connect();
		assertEquals(Status.OK_STATUS, client.validate(ProgressUtil.convert(null)));

		// invalid url
		client = fixture.connect("http://non.existant/repository");
		try {
			client.validate(ProgressUtil.convert(null));
			fail("Expected HudsonException");
		} catch (HudsonException e) {
		}

		// non Hudson url
		client = fixture.connect("http://mylyn.eclipse.org/");
		try {
			client.validate(ProgressUtil.convert(null));
			fail("Expected HudsonException");
		} catch (HudsonException e) {
		}
	}

	public void testGetJobs() throws Exception {
		client = fixture.connect();
		List<HudsonModelJob> jobs = client.getJobs(null, null);
		assertContains(jobs, PLAN_FAILING);
		assertContains(jobs, PLAN_SUCCEEDING);
		assertHealthReport(jobs);
	}

	private void assertContains(List<HudsonModelJob> jobs, String name) {
		for (HudsonModelJob job : jobs) {
			if (job.getName().equals(name)) {
				return;
			}
		}
		fail("Expected '" + name + "' in " + jobs);
	}

	private void assertHealthReport(List<HudsonModelJob> jobs) {
		for (HudsonModelJob job : jobs) {
			if (!job.getHealthReport().isEmpty()) {
				return;
			}
		}
		fail("Expected attribute 'healthReport' in " + jobs);
	}

	public void testRunBuild() throws Exception {
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);

		client = fixture.connect(HudsonFixture.HUDSON_TEST_URL, credentials.username, credentials.password);

		// failing build
		client.runBuild(getJob(PLAN_FAILING), ProgressUtil.convert(null));
		poll(new Callable<Object>() {
			public Object call() throws Exception {
				assertEquals(getJob(PLAN_FAILING).getColor(), HudsonModelBallColor.RED_ANIME);
				return null;
			}
		});
//		assertEquals(getJob(PLAN_SUCCEEDING).getColor(), HudsonModelBallColor.RED);

		// succeeding build
		client.runBuild(getJob(PLAN_SUCCEEDING), ProgressUtil.convert(null));
		poll(new Callable<Object>() {
			public Object call() throws Exception {
				assertEquals(getJob(PLAN_SUCCEEDING).getColor(), HudsonModelBallColor.BLUE_ANIME);
				return null;
			}
		});
//		assertEquals(getJob(PLAN_SUCCEEDING).getColor(), HudsonModelBallColor.BLUE);
	}

	private <T> T poll(Callable<T> callable) throws Exception {
		AssertionFailedError lastException = null;
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < POLL_TIMEOUT) {
			try {
				return callable.call();
			} catch (AssertionFailedError e) {
				lastException = e;
			}
			Thread.sleep(POLL_INTERVAL);
		}
		if (lastException != null) {
			throw lastException;
		}

		// try one more time
		return callable.call();
	}

	private HudsonModelJob getJob(String name) throws HudsonException {
		for (HudsonModelJob job : client.getJobs(null, null)) {
			if (job.getName().equals(name)) {
				return job;
			}
		}
		return null;
	}

}
