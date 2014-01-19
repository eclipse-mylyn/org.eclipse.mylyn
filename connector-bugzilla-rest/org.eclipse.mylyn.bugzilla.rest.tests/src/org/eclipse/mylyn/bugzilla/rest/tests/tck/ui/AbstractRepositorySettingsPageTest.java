/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.tests.tck.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.bugzilla.rest.tests.AbstractTckTest;
import org.eclipse.mylyn.bugzilla.rest.tests.TckFixture;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.RunOnlyWhenProperty;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Test;

@FixtureDefinition(fixtureClass = TckFixture.class, fixtureType = "bugzillaREST")
@RunOnlyWhenProperty(property = "default", value = "1")
public class AbstractRepositorySettingsPageTest extends AbstractTckTest {
	private WizardDialog dialog;

	public AbstractRepositorySettingsPageTest(TckFixture fixture) {
		super(fixture);
	}

	@After
	public void tearDown() throws Exception {
		if (dialog != null) {
			dialog.close();
		}
	}

	@Test
	public void testApplyTo() {
		TaskRepository repository = fixture().repository();
		EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
		dialog = new WizardDialog(new Shell(), wizard);
		dialog.create();
		ITaskRepositoryPage page = wizard.getSettingsPage();
		assertNull("repository.getCategory() should be null", repository.getCategory());
		page.applyTo(repository);
		assertEquals(TaskRepository.CATEGORY_BUGS, repository.getCategory());
	}

}
