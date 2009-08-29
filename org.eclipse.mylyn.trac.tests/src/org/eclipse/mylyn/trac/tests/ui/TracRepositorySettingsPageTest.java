/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.ui;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracRepositorySettingsPage.TracValidator;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TracRepositorySettingsPageTest extends TestCase {

	private TracRepositorySettingsPage page;

	private TracValidator validator;

	public TracRepositorySettingsPageTest() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		page = new TracRepositorySettingsPage(null);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		page.createControl(shell);
		page.setVisible(true);
	}

	protected void initialize(TracFixture fixture) throws Exception {
		// initialize page from test fixture
		TaskRepository repository = fixture.repository();
		page.setAnonymous(false);
		page.setUrl(repository.getRepositoryUrl());
		page.setUserId(repository.getCredentials(AuthenticationType.REPOSITORY).getUserName());
		page.setPassword(repository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
		page.setTracVersion(fixture.getAccessMode());

		validator = page.new TracValidator(page.createTaskRepository(), fixture.getAccessMode());
	}

	public void testValidateXmlRpc() throws Exception {
		initialize(TracFixture.TRAC_0_10_XML_RPC);

		validator.run(new NullProgressMonitor());
		assertNull(validator.getResult());
		assertNull(validator.getStatus());
	}

	public void testValidateWeb() throws Exception {
		initialize(TracFixture.TRAC_0_10_WEB);

		validator.run(new NullProgressMonitor());
		assertNull(validator.getResult());
		assertNull(validator.getStatus());
	}

	public void testValidateAutomaticUser() throws Exception {
		initialize(TracFixture.TRAC_0_10_XML_RPC);

		page.setTracVersion(null);
		validator = page.new TracValidator(page.createTaskRepository(), null);

		validator.run(new NullProgressMonitor());
		assertEquals(Version.XML_RPC, validator.getResult());
		assertNull(validator.getStatus());
	}

	public void testValidateAutomaticAnonymous() throws Exception {
		initialize(TracFixture.TRAC_0_10_XML_RPC);

		page.setUserId("");
		page.setPassword("");
		page.setTracVersion(null);
		validator = page.new TracValidator(page.createTaskRepository(), null);

		validator.run(new NullProgressMonitor());
		assertEquals(Version.TRAC_0_9, validator.getResult());
		assertNotNull(validator.getStatus());
	}

}
