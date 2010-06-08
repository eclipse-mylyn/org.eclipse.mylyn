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

import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.util.ProgressUtil;
import org.eclipse.mylyn.hudson.tests.support.HudsonFixture;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;

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
		assertEquals(Status.CANCEL_STATUS, client.validate(ProgressUtil.convert(null)));

		// non Hudson url
		client = fixture.connect("http://mylyn.eclipse.org/");
		assertEquals(Status.CANCEL_STATUS, client.validate(ProgressUtil.convert(null)));
	}

}
