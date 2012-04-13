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

package org.eclipse.mylyn.gerrit.tests.support;

import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.tests.util.TestFixture;

/**
 * @author Steffen Pingel
 */
public class GerritFixture extends TestFixture {

	public static GerritFixture GERRIT_2_1_5 = new GerritFixture("http://localhost:8080/", "2.1.5", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static GerritFixture GERRIT_2_2_1 = new GerritFixture("http://review.mylyn.org/", "2.2.1", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static GerritFixture GERRIT_2_2_2 = new GerritFixture("http://mylyn.org/gerrit-2.2.2", "2.2.2", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static GerritFixture GERRIT_2_3 = new GerritFixture("http://mylyn.org/gerrit-2.3.0", "2.3.0", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static GerritFixture[] ALL = new GerritFixture[] { GERRIT_2_2_1, GERRIT_2_2_2, GERRIT_2_3 };

	public static GerritFixture DEFAULT = GERRIT_2_3;

	private static GerritFixture current;

	public GerritFixture(String url, String version, String description) {
		super(GerritConnector.CONNECTOR_KIND, url);
		setInfo(url, version, description);
	}

	public static GerritFixture current() {
		if (current == null) {
			DEFAULT.activate();
		}
		return current;
	}

	@Override
	protected GerritFixture activate() {
		current = this;
		setUpFramework();
		return this;
	}

	@Override
	protected GerritFixture getDefault() {
		return DEFAULT;
	}

	public GerritHarness harness() {
		return new GerritHarness(this);
	}

	public boolean canAuthenticate() {
		return this != GERRIT_2_2_1;
	}
}
