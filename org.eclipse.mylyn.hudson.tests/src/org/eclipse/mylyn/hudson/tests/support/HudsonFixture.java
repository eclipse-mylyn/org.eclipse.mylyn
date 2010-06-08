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

package org.eclipse.mylyn.hudson.tests.support;

import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.hudson.core.HudsonCorePlugin;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.tests.util.TestFixture;

/**
 * Initializes Hudson repositories to a defined state. This is done once per test run, since cleaning and initializing
 * the repository for each test method would take too long.
 * 
 * @author Markus Knittig
 */
public class HudsonFixture extends TestFixture {

	private static HudsonFixture current;

	private final static String HUDSON_TEST_URL = "http://mylyn.eclipse.org/hudson";

	private static final HudsonFixture DEFAULT = new HudsonFixture(HUDSON_TEST_URL, "1.339", "REST");

	/**
	 * Standard configurations for running all test against.
	 */
	public static final HudsonFixture[] ALL = new HudsonFixture[] { DEFAULT };

	public static HudsonFixture current() {
		return current(DEFAULT);
	}

	public static HudsonFixture current(HudsonFixture fixture) {
		if (current == null) {
			fixture.activate();
		}
		return current;
	}

	public HudsonFixture() {
		super(HudsonCorePlugin.CONNECTOR_KIND, HUDSON_TEST_URL);
	}

	public HudsonFixture(String url, String version, String info) {
		super(HudsonCorePlugin.CONNECTOR_KIND, url);
		setInfo("Hudson", version, info);
	}

	@Override
	protected TestFixture activate() {
		current = this;
		setUpFramework();
		return this;
	}

	public RestfulHudsonClient connect() {
		return connect(getRepositoryUrl());
	}

	public RestfulHudsonClient connect(String url) {
		return new RestfulHudsonClient(new WebLocation(url));
	}

	@Override
	protected TestFixture getDefault() {
		return DEFAULT;
	}

}
