/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.tests.support.MockScmConnector;
import org.junit.Before;
import org.junit.Test;

public class ScmRepositoryTest {
	private static final String REPO_URL = "http://git.eclipse.org/c/org.eclipse.mylyn.versions.git";

	private static final String REPO_NAME = "NAME";

	private ScmConnector connector;

	@Before
	public void prepare() {
		connector = new MockScmConnector();
	}

	@Test
	public void differentConnectorAreNotEqual() {
		ScmRepository scmRepository = new ScmRepository(connector, REPO_NAME, REPO_URL);
		scmRepository.setConnector(connector);
		ScmRepository other = new ScmRepository(new MockScmConnector(), REPO_NAME, REPO_URL);
		other.setConnector(new MockScmConnector());
		assertFalse(scmRepository.equals(other));
	}

	@Test
	public void sameFieldsButDifferentClassIsNotEqual() {
		ScmRepository scmRepository = new ScmRepository(connector, REPO_NAME, REPO_URL);
		ScmRepository other = new ScmRepository(connector, REPO_NAME, REPO_URL) {
		};
		assertFalse(scmRepository.equals(other));
	}

	@Test
	public void differentNameIsNotEqual() {
		ScmRepository scmRepository = new ScmRepository(connector, REPO_NAME, REPO_URL);
		ScmRepository other = new ScmRepository(connector, "NAME2", REPO_URL);
		assertFalse(scmRepository.equals(other));
	}

	@Test
	public void differentUrlIsNotEqual() {
		ScmRepository scmRepository = new ScmRepository(connector, REPO_NAME, REPO_URL);
		ScmRepository other = new ScmRepository(connector, REPO_NAME,
				"http://git.eclipse.org/c/org.eclipse.mylyn.reviews.git");
		assertFalse(scmRepository.equals(other));
	}

	@Test
	public void sameFieldsAndClassIsEqual() {
		ScmRepository scmRepository = new ScmRepository(connector, REPO_NAME, REPO_URL);
		ScmRepository other = new ScmRepository(connector, REPO_NAME, REPO_URL);
		assertTrue(scmRepository.equals(other));
	}
}
