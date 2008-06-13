/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.net.Proxy;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.ui.TracConnectorUi;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracRepositorySettingsPage.TracValidator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TracRepositorySettingsPageTest extends AbstractTracClientTest {

	private TracConnectorUi connector;

	private TracRepositorySettingsPage page;

	private TracValidator validator;

	public TracRepositorySettingsPageTest() {
		super(null);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		connector = (TracConnectorUi) TasksUiPlugin.getConnectorUi(TracCorePlugin.CONNECTOR_KIND);
		page = new TracRepositorySettingsPage(null);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		page.createControl(shell);
		page.setVisible(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		// TestFixture.cleanupRepository1();
	}

	@Override
	public ITracClient connect(String url, String username, String password, Proxy proxy, Version version)
			throws Exception {
		page.setAnonymous(false);
		page.setUrl(url);
		page.setUserId(username);
		page.setPassword(password);
		page.setTracVersion(version);
		validator = page.new TracValidator(page.createTaskRepository(), version);
		return null;
	}

	public void testValidateXmlRpc() throws Exception {
		version = Version.XML_RPC;
		connect010();

		validator.run(new NullProgressMonitor());
		assertNull(validator.getResult());
		assertNull(validator.getStatus());
	}

	public void testValidateWeb() throws Exception {
		version = Version.TRAC_0_9;
		connect010();

		validator.run(new NullProgressMonitor());
		assertNull(validator.getResult());
		assertNull(validator.getStatus());
	}

	public void testValidateAutomaticUser() throws Exception {
		version = null;
		connect010();

		validator.run(new NullProgressMonitor());
		assertEquals(Version.XML_RPC, validator.getResult());
		assertNull(validator.getStatus());
	}

	public void testValidateAutomaticAnonymous() throws Exception {
		version = null;
		connect(TracTestConstants.TEST_TRAC_010_URL, "", "");

		validator.run(new NullProgressMonitor());
		assertEquals(Version.TRAC_0_9, validator.getResult());
		assertNotNull(validator.getStatus());
	}

}
