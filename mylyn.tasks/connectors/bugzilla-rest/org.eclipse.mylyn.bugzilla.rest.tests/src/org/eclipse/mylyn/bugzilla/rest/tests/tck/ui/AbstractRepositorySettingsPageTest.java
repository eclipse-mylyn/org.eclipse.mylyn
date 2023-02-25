/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.tests.tck.ui;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.bugzilla.rest.tests.AbstractTckTest;
import org.eclipse.mylyn.bugzilla.rest.tests.TckFixture;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.RunOnlyWhenProperty;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.ui.BugzillaRestRepositoryConnectorUi;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@FixtureDefinition(fixtureClass = TckFixture.class, fixtureType = "bugzillaREST")
@RunOnlyWhenProperty(property = "default", value = "1")
public class AbstractRepositorySettingsPageTest extends AbstractTckTest {
	private WizardDialog dialog;

	public AbstractRepositorySettingsPageTest(TckFixture fixture) {
		super(fixture);
	}

	@Before
	public void setup() {
	}

	@After
	public void tearDown() throws Exception {
		if (dialog != null) {
			dialog.close();
		}
	}

	@Test
	public void testApplyTo() {
		TaskRepository repository = fixture().createRepository();
		BugzillaRestConnector connector = new BugzillaRestConnector();
		BugzillaRestRepositoryConnectorUi connectorUi = new BugzillaRestRepositoryConnectorUi(connector);
		EditRepositoryWizard wizard = new EditRepositoryWizard(repository, connectorUi);
		dialog = new WizardDialog(new Shell(), wizard);
		dialog.create();
		ITaskRepositoryPage page = wizard.getSettingsPage();
		// we need this because save removes the static import of org.junit.Assert
		org.junit.Assert.assertNull("repository.getCategory() should be null", repository.getCategory());
		page.applyTo(repository);
		org.junit.Assert.assertEquals(TaskRepository.CATEGORY_BUGS, repository.getCategory());
	}

}
