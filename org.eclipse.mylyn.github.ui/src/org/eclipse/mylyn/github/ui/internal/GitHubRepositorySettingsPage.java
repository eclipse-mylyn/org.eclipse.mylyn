/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.github.ui.internal;

import java.util.regex.Matcher;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.GitHubCredentials;
import org.eclipse.mylyn.github.internal.GitHubService;
import org.eclipse.mylyn.github.internal.GitHubServiceException;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * GitHub connector specific extensions.
 */
public class GitHubRepositorySettingsPage extends
		AbstractRepositorySettingsPage {

	static final String URL = "http://github.com";

	/**
	 * Populate taskRepository with repository settings.
	 * 
	 * @param taskRepository
	 *            - Object to populate
	 */
	public GitHubRepositorySettingsPage(final TaskRepository taskRepository) {
		super("GitHub Repository Settings", "", taskRepository);
		this.setHttpAuth(false);
		this.setNeedsAdvanced(false);
		this.setNeedsAnonymousLogin(true);
		this.setNeedsTimeZone(false);
		this.setNeedsHttpAuth(false);
	}

	@Override
	public String getConnectorKind() {
		return GitHub.CONNECTOR_KIND;
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// Set the URL now, because serverURL is definitely instantiated .
		if (serverUrlCombo != null && (serverUrlCombo.getText() == null || serverUrlCombo.getText().trim().length() == 0)) {
			String fullUrlText = URL+"/user/project";
			serverUrlCombo.setText(fullUrlText);
			// select the user/project part of the URL so that the user can just start
			// typing to replace the text.
			serverUrlCombo.setSelection(new Point(URL.length()+1,fullUrlText.length()));
		}

		// Specify that you need the GitHub User Name
		if (repositoryUserNameEditor != null) {
			String text = repositoryUserNameEditor.getLabelText();
			repositoryUserNameEditor.setLabelText("GitHub " + text);
		}

		this.setAnonymous(false);
	}

	@Override
	protected Validator getValidator(final TaskRepository repository) {
		Validator validator = new Validator() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				int totalWork = 1000;
				monitor.beginTask("Validating settings", totalWork);
				try {
					
					String urlText = repository.getUrl();
					Matcher urlMatcher = GitHub.URL_PATTERN.matcher(urlText==null?"":urlText);
					if (!urlMatcher.matches()) {
						setStatus(GitHubUi.createErrorStatus("Server URL must be in the form http://github.com/user/project or\nhttp://www.github.org/user/project"));
						return;
					}
					monitor.worked(100);
					
					String user = urlMatcher.group(1);
					String repo = urlMatcher.group(2);
					AuthenticationCredentials auth = repository.getCredentials(AuthenticationType.REPOSITORY);
					
					GitHubService service = new GitHubService();
	
					monitor.subTask("Contacting server...");
					try {
						// verify the credentials
						if (auth == null) {
							setStatus(GitHubUi.createErrorStatus("Credentials are required.  Please specify username and API Token."));
							return;
						}
						monitor.worked(250);
						
						GitHubCredentials credentials = new GitHubCredentials(auth.getUserName(), auth.getPassword());
						if (!service.verifyCredentials(credentials)) {
							setStatus(GitHubUi.createErrorStatus("Invalid credentials.  Please check your GitHub User ID and API Token.\nYou can find your API Token on your GitHub account settings page."));
							return;	
						}
						monitor.worked(250);
						
						// verify the repo
						service.searchIssues(user, repo, new String("open"),"", credentials);
						monitor.worked(400);
					} catch (GitHubServiceException e) {
						setStatus(GitHubUi.createErrorStatus("Repository Test failed:"+ e.getMessage()));
						return;
					}
					
					setStatus(new Status(IStatus.OK,GitHubUi.BUNDLE_ID, "Success!"));
				} finally {
					monitor.done();
				}
			}
		};
		return validator;
	}

	@Override
	protected boolean isValidUrl(final String url) {
		if (url.contains("github")) {
			return true;
		}
		return false;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage#applyTo(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	public void applyTo(TaskRepository repository) {
		repository.setProperty(IRepositoryConstants.PROPERTY_CATEGORY,
				IRepositoryConstants.CATEGORY_BUGS);
		super.applyTo(repository);
	}

}
