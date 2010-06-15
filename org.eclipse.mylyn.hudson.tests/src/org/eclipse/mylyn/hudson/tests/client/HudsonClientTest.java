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

package org.eclipse.mylyn.hudson.tests.client;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.util.ProgressUtil;
import org.eclipse.mylyn.hudson.tests.support.HudsonFixture;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonException;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;

/**
 * Test cases for {@link RestfulHudsonClient}.
 * 
 * @author Markus Knittig
 */
public class HudsonClientTest extends TestCase {

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

	public void testGetPlans() throws Exception {
		client = fixture.connect();
		List<HudsonModelJob> plans = client.getJobs(null);
		assertEquals(plans.get(0).getName(), "failing");
	}

}
