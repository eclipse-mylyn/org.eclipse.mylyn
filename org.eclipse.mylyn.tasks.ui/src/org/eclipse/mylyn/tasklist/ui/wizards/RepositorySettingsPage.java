/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.ui.wizards;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public abstract class RepositorySettingsPage extends WizardPage {

	protected static final String LABEL_SERVER = "Server: ";
	
	protected static final String LABEL_USER = "User Name: ";

	protected static final String LABEL_PASSWORD = "Password: ";
	
	protected static final String URL_PREFIX_HTTPS = "https://";

	protected static final String URL_PREFIX_HTTP = "http://";
	
	protected StringFieldEditor serverUrlEditor;
 
	protected StringFieldEditor userNameEditor;

	protected RepositoryStringFieldEditor passwordEditor;
	
	protected TaskRepository repository;
		
	public RepositorySettingsPage(String title, String description) {
		super(title);
		super.setTitle(title);
		super.setDescription(description);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		FillLayout layout = new FillLayout();
		container.setLayout(layout);

		serverUrlEditor = new StringFieldEditor("", LABEL_SERVER,
				StringFieldEditor.UNLIMITED, container) {

			@Override
			protected boolean doCheckState() {
				return isValidUrl(getStringValue());
			}

			@Override
			protected void valueChanged() {
				super.valueChanged();
				getWizard().getContainer().updateButtons();
			}
		};
		serverUrlEditor.setErrorMessage("Server path must be a valid http(s):// url");
		
		userNameEditor = new StringFieldEditor("", LABEL_USER, StringFieldEditor.UNLIMITED, container);
		passwordEditor = new RepositoryStringFieldEditor("", LABEL_PASSWORD, StringFieldEditor.UNLIMITED, container);
		passwordEditor.getTextControl().setEchoChar('*');
		
		if (repository != null) {
			serverUrlEditor.setStringValue(repository.getUrl().toExternalForm());
			userNameEditor.setStringValue(repository.getUserName());
			passwordEditor.setStringValue(repository.getPassword());
		}
		
		createAdditionalControls(container);
		setControl(container);
	}
	
	protected abstract void createAdditionalControls(Composite parent);

	public URL getServerUrl() {
		try {
			return new URL(serverUrlEditor.getStringValue());
		} catch (MalformedURLException e) {
			MylarStatusHandler.fail(e, "could not create url", true);
			return null;
		}
	}
	
	public String getUserName() {
		return userNameEditor.getStringValue();
	}
	
	public String getPassword() {
		return passwordEditor.getStringValue();
	}
	
	private boolean isValidUrl(String name) {
		if (name.startsWith(URL_PREFIX_HTTPS) || name.startsWith(URL_PREFIX_HTTP)) {
			try {
				new URL(name);
			} catch (MalformedURLException e) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void init(IWorkbench workbench) {
		// ignore
	}

	/**
	 * Exposes StringFieldEditor.refreshValidState() 
	 * TODO: is there a better way?
	 */
	private static class RepositoryStringFieldEditor extends StringFieldEditor {
		public RepositoryStringFieldEditor(String name, String labelText, int style, Composite parent) {
			super(name, labelText, style, parent);
		}

		@Override
		public void refreshValidState() {
			try {
				super.refreshValidState();
			} catch (Exception e) {
				MylarStatusHandler.log(e, "problem refreshing password field");
			}
		}

		@Override
		public Text getTextControl() {
			return super.getTextControl();
		}

	}

	@Override
	public boolean isPageComplete() {
		return isValidUrl(serverUrlEditor.getStringValue());
	}

	public void setRepository(TaskRepository repository) {
		this.repository = repository;
	}

	public TaskRepository getRepository() {
		return repository;
	}
}
