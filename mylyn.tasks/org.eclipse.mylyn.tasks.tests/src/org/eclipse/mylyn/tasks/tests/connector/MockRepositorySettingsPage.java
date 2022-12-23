/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class MockRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private String brand;

	public MockRepositorySettingsPage(TaskRepository taskRepository) {
		super("title", "summary", taskRepository);
	}

	public MockRepositorySettingsPage(TaskRepository taskRepository, AbstractRepositoryConnector connector) {
		super("title", "summary", taskRepository, connector);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// ignore
	}

	@Override
	public boolean isValidUrl(String url) {
		return super.isValidUrl(url);
	}

	@Override
	protected void validateSettings() {
		// ignore
	}

	public Button getAnonymousButton() {
		return anonymousButton;
	}

	public StringFieldEditor getUserNameEditor() {
		return repositoryUserNameEditor;
	}

	public StringFieldEditor getPasswordEditor() {
		return repositoryPasswordEditor;
	}

	public Composite getParent() {
		return compositeContainer;
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

	public StringFieldEditor getRepositoryUserNameEditor() {
		return repositoryUserNameEditor;
	}

	public StringFieldEditor getRepositoryPasswordEditor() {
		return repositoryPasswordEditor;
	}

	@Override
	public void setBrand(String brand) {
		super.setBrand(brand);
		this.brand = brand;
	}

	public String getBrand() {
		return brand;
	}

}
