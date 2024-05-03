/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.tests.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.bugzilla.rest.tests.AbstractTckTest;
import org.eclipse.mylyn.bugzilla.rest.tests.TckFixture;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.tasks.core.RepositoryInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@FixtureDefinition(fixtureClass = TckFixture.class, fixtureType = "bugzillaREST")
@SuppressWarnings("nls")
@Ignore("No CI Server")
public class ValidationTest extends AbstractTckTest {

	public ValidationTest(TckFixture fixture) {
		super(fixture);
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testValidateCredentials() throws Exception {
		TaskRepository repository = fixture().createRepository();
		org.eclipse.mylyn.commons.net.AuthenticationCredentials mylynCreds = new org.eclipse.mylyn.commons.net.AuthenticationCredentials(
				"tests@mylyn.eclipse.org", "mylyntest");
		repository.setCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY, mylynCreds, true);
		RepositoryInfo info = fixture().connector().validateRepository(repository, monitor);
		assertNotNull(info);
		assertEquals(fixture().getVersion(), info.getVersion().toString());
	}

	@Test
	public void testInvalidateCredentials() throws CoreException {
		thrown.expect(CoreException.class);
		thrown.expectMessage("Unauthorized");
		TaskRepository repository = fixture().createRepository();
		org.eclipse.mylyn.commons.net.AuthenticationCredentials invalideCreds = new org.eclipse.mylyn.commons.net.AuthenticationCredentials(
				"invalidateCredentials", "invalidateCredentials");
		repository.setCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY, invalideCreds, true);
		fixture().connector().validateRepository(repository, monitor);
	}

}
