/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.github.ui.internal.GitHubUi;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Create Gist handler class.
 */
public class CreateGistHandler extends AbstractHandler {

	/**
	 * PUBLIC_GIST
	 */
	public static final String PUBLIC_GIST = "publicGist"; //$NON-NLS-1$

	/**
	 * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
	 */
	public boolean isEnabled() {
		return !GistConnectorUi.getRepositories().isEmpty();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorInput input = HandlerUtil.getActiveEditorInput(event);
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection == null || selection.isEmpty())
			selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection instanceof ITextSelection
				&& input instanceof IFileEditorInput) {
			ITextSelection text = (ITextSelection) selection;
			IFile file = ((IFileEditorInput) input).getFile();
			createGistJob(file.getName(), file.getFileExtension(),
					text.getText(),
					Boolean.parseBoolean(event.getParameter(PUBLIC_GIST)));
		} else if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object obj = structuredSelection.getFirstElement();
			if (obj instanceof IFile)
				createGistJob((IFile) obj,
						Boolean.parseBoolean(event.getParameter(PUBLIC_GIST)));
		}
		return null;
	}

	private void createGistJob(String name, String extension, String contents,
			boolean isPublic) {
		Set<TaskRepository> repositories = GistConnectorUi.getRepositories();

		// only use the first repository, in the future provide a
		// selection if multiple exist
		TaskRepository repository = repositories.iterator().next();
		GitHubClient client = new GitHubClient();
		AuthenticationCredentials credentials = repository
				.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null)
			client.setCredentials(credentials.getUserName(),
					credentials.getPassword());
		GistService service = new GistService(client);
		CreateGistJob job = new CreateGistJob(Messages.CreateGistHandler_CreateGistJobName, name, contents,
				service, isPublic);
		job.setSystem(true);
		job.schedule();
	}

	private void createGistJob(IFile file, boolean isPublic) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(file.getContents()));
			String line;
			StringBuilder result = new StringBuilder();
			while ((line = br.readLine()) != null)
				result.append(line).append('\n');

			String contents = result.toString();
			createGistJob(file.getName(), file.getFileExtension(), contents,
					isPublic);
		} catch (CoreException e) {
			GitHubUi.logError(e);
		} catch (IOException e) {
			GitHubUi.logError(e);
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					GitHubUi.logError(e);
				}
		}
	}

}
