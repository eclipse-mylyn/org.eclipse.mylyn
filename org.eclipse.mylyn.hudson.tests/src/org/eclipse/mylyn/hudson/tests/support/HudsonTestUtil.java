/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests.support;

import java.util.List;
import java.util.concurrent.Callable;

import junit.framework.Assert;

import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;

/**
 * @author Steffen Pingel
 */
public class HudsonTestUtil {

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
		while (System.currentTimeMillis() - startTime < POLL_TIMEOUT) {
			try {
				return callable.call();
			} catch (AssertionError e) {
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

}
