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
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests.support;

import java.util.List;
import java.util.concurrent.Callable;

import junit.framework.Assert;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsException;

/**
 * @author Steffen Pingel
 */
public class JenkinsTestUtil {

	private static final long POLL_TIMEOUT = 60 * 1000;

	private static final long POLL_INTERVAL = 3 * 1000;

	public static void assertContains(List<HudsonModelJob> jobs, String name) {
		for (HudsonModelJob job : jobs) {
			if (job.getName().equals(name)) {
				return;
			}
		}
		Assert.fail("Expected '" + name + "' in " + jobs);
	}

	public static void assertContainsNot(List<HudsonModelJob> jobs, String name) {
		for (HudsonModelJob job : jobs) {
			if (job.getName().equals(name)) {
				Assert.fail("Not expected '" + name + "' in " + jobs);
			}
		}

	}

	public static void assertHealthReport(List<HudsonModelJob> jobs) {
		for (HudsonModelJob job : jobs) {
			if (!job.getHealthReport().isEmpty()) {
				return;
			}
		}
		Assert.fail("Expected attribute 'healthReport' in " + jobs);
	}

	public static <T> T poll(Callable<T> callable) throws Exception {
		AssertionError lastException = null;
		long startTime = System.currentTimeMillis();
		int badGatewayCounter = 3;
		while (System.currentTimeMillis() - startTime < POLL_TIMEOUT) {
			try {
				return callable.call();
			} catch (AssertionError e) {
				lastException = e;
			} catch (JenkinsException e) {
				if (e.getMessage().contains("Bad Gateway") && badGatewayCounter-- > 0) {// log and try again
					StatusHandler.log(new Status(IStatus.ERROR, "org.eclipse.mylyn.jenkins.tests",
							"Bad Gateway #" + badGatewayCounter, e));
				} else {
					throw e;
				}
			}
			Thread.sleep(POLL_INTERVAL);
		}
		if (lastException != null) {
			throw lastException;
		}

		// try one more time
		return callable.call();
	}

}
