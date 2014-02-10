/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

public class RepositorySettingsPageTest {

	public static class TestRepositorySettingsPage extends AbstractRepositorySettingsPage {

		public TestRepositorySettingsPage() {
			super("Title", "Description", new TaskRepository("mock", "url"), MockRepositoryConnector.getDefault());
			setNeedsProxy(true);
		}

		@Override
		protected Validator getValidator(TaskRepository repository) {
			// ignore
			return null;
		}

		@Override
		public String getConnectorKind() {
			return MockRepositoryConnector.CONNECTOR_KIND;
		}

		@Override
		protected void createAdditionalControls(Composite parent) {
			// ignore
		}
	}

	@Test
	public void proxyPortTriggersValidation() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage();
		IWizardContainer container = applyWizardContainer(page);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		verify(container, times(1)).updateButtons();

		page.proxyPortEditor.setStringValue("123");
		verify(container, times(2)).updateButtons();
	}

	private IWizardContainer applyWizardContainer(TestRepositorySettingsPage page) {
		IWizard wizard = mock(IWizard.class);
		IWizardContainer container = mock(IWizardContainer.class);
		when(wizard.getContainer()).thenReturn(container);
		page.setWizard(wizard);
		return container;
	}
}
