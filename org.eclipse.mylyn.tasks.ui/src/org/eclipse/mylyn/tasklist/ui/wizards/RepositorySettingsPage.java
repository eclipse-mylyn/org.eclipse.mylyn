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
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.tasklist.repositories.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public class RepositorySettingsPage extends WizardPage {

	private static final String LABEL = "Repository Settings";

	private final String TITLE = "Enter repository settings";
	
	private final String LABEL_WARNING = "Example: https://bugs.eclipse.org/bugs (do not include index.cgi)";

	private static final String LABEL_SERVER = "Server: ";
	
	private static final String LABEL_USER = "User Name: ";

	private static final String LABEL_PASSWORD = "Password: ";
	
	private static final String URL_PREFIX_HTTPS = "https://";

	private static final String URL_PREFIX_HTTP = "http://";
	
	private StringFieldEditor serverUrlEditor;

	private StringFieldEditor userNameEditor;

	private RepositoryStringFieldEditor passwordEditor;
	
	private TaskRepository repository;
	
	public RepositorySettingsPage() {
		super(LABEL);
		super.setTitle(TITLE);
		super.setDescription(LABEL_WARNING);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		FillLayout layout = new FillLayout();
		container.setLayout(layout);

		serverUrlEditor = new StringFieldEditor("", LABEL_SERVER,
				StringFieldEditor.UNLIMITED, container) {

			@Override
			protected boolean doCheckState() {
				return isValidURl(getStringValue());
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
			serverUrlEditor.setStringValue(repository.getServerUrl().toExternalForm());
			userNameEditor.setStringValue(repository.getUserName());
			passwordEditor.setStringValue(repository.getPassword());
		}
		
		setControl(container);
	}
	
	public URL getServerUrl() {
		try {
			return new URL(serverUrlEditor.getStringValue());
		} catch (MalformedURLException e) {
			ErrorLogger.fail(e, "could not create url", true);
			return null;
		}
	}
	
	public String getUserName() {
		return userNameEditor.getStringValue();
	}
	
	public String getPassword() {
		return passwordEditor.getStringValue();
	}
	
	private boolean isValidURl(String name) {
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
	 * Hack private class to make StringFieldEditor.refreshValidState() a
	 * publicly acessible method.
	 * 
	 * @see org.eclipse.jface.preference.StringFieldEditor#refreshValidState()
	 */
	private static class RepositoryStringFieldEditor extends StringFieldEditor {
		public RepositoryStringFieldEditor(String name, String labelText, int style, Composite parent) {
			super(name, labelText, style, parent);
		}

		@Override
		public void refreshValidState() {
			super.refreshValidState();
		}

		@Override
		public Text getTextControl() {
			return super.getTextControl();
		}

	}

	@Override
	public boolean isPageComplete() {
		return isValidURl(serverUrlEditor.getStringValue());
	}

	public void setRepository(TaskRepository repository) {
		this.repository = repository;
	}
}
