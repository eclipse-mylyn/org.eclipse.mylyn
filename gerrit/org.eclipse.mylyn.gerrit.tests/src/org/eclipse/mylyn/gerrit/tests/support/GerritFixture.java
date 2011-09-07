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

	public static GerritFixture GERRIT_EGIT = new GerritFixture("http://egit.eclipse.org/r/", "2.1.5", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static GerritFixture GERRIT_MYLYN = new GerritFixture("http://review.mylyn.org/", "2.2.1", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static GerritFixture DEFAULT = GERRIT_MYLYN;

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

}
