/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracRepositorySettingsPage.TracValidator;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.trac.tests.support.TracFixture;

/**
 * @author Steffen Pingel
 */
public class TracRepositorySettingsPageTest extends TestCase {

	// make protected methods visible
	private static class MyTracRepositorySettingsPage extends TracRepositorySettingsPage {

		public MyTracRepositorySettingsPage(TaskRepository taskRepository) {
			super(taskRepository);
		}

		@Override
		protected void applyValidatorResult(Validator validator) {
			// see AbstractRespositorySettingsPage.validate()
			if (validator.getStatus() == null) {
				validator.setStatus(Status.OK_STATUS);
			}
			super.applyValidatorResult(validator);
		}

	}

	private MyTracRepositorySettingsPage page;

	private TracValidator validator;

	private WizardDialog dialog;

	public TracRepositorySettingsPageTest() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		page = new MyTracRepositorySettingsPage(null);

		// stub wizard and dialog
		Wizard wizard = new Wizard() {
			@Override
			public boolean performFinish() {
				return true;
			}
		};
		wizard.addPage(page);
		dialog = new WizardDialog(null, wizard);
		dialog.create();
//		page.createControl(dialog.getShell());
//		page.setVisible(true);
	}

	@Override
	protected void tearDown() throws Exception {
		if (dialog != null) {
			dialog.close();
		}
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

		page.applyValidatorResult(validator);
		assertEquals(Version.XML_RPC, page.getTracVersion());
		assertEquals("Authentication credentials are valid.", page.getMessage());
	}

	public void testValidateWeb() throws Exception {
		initialize(TracFixture.TRAC_0_10_WEB);

		validator.run(new NullProgressMonitor());
		assertNull(validator.getResult());
		assertNull(validator.getStatus());

		page.applyValidatorResult(validator);
		assertEquals(Version.TRAC_0_9, page.getTracVersion());
		assertEquals("Authentication credentials are valid.", page.getMessage());
	}

	public void testValidateAutomaticUser() throws Exception {
		initialize(TracFixture.TRAC_0_10_XML_RPC);

		page.setTracVersion(null);
		validator = page.new TracValidator(page.createTaskRepository(), null);

		validator.run(new NullProgressMonitor());
		assertEquals(Version.XML_RPC, validator.getResult());
		assertNull(validator.getStatus());

		page.applyValidatorResult(validator);
		assertEquals(Version.XML_RPC, page.getTracVersion());
		assertEquals("Authentication credentials are valid.", page.getMessage());
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

		page.applyValidatorResult(validator);
		assertEquals(Version.TRAC_0_9, page.getTracVersion());
		assertEquals(
				"Authentication credentials are valid. Note: Insufficient permissions for XML-RPC access, falling back to web access.",
				page.getMessage());
	}

	public void testValidateInvalid() throws Exception {
		initialize(TracFixture.TRAC_INVALID);

		page.setTracVersion(null);
		validator = page.new TracValidator(page.createTaskRepository(), null);

		try {
			validator.run(new NullProgressMonitor());
			fail("Expected CoreException");
		} catch (CoreException e) {
			validator.setStatus(e.getStatus());
		}

		page.applyValidatorResult(validator);
		assertNull(page.getTracVersion());
		assertEquals(IMessageProvider.ERROR, page.getMessageType());
	}
}
