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
package org.eclipse.mylyn.github.ui.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.GitHubCredentials;
import org.eclipse.mylyn.github.internal.GitHubService;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

public class CreateGistHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IStructuredSelection menuSelection = (IStructuredSelection) HandlerUtil
		.getActiveMenuSelection(event);
		if(selection != null && menuSelection == null) {
			if(selection instanceof ITextSelection) {
				ITextSelection text = (ITextSelection) selection;
				IEditorInput input = editor.getEditorInput();
				if(input instanceof IFileEditorInput) {
					// only use the first repository, in the future provide a selection if multiple exist
					IFileEditorInput fileInput = (IFileEditorInput) input;
					IFile file = fileInput.getFile();
					createGistJob(file.getName(), file.getFileExtension(), text.getText());
				}
			}
		}
		if(menuSelection != null) {
			Object obj = menuSelection.getFirstElement();
			if(obj instanceof IFile) {
				IFile file = (IFile) obj;
				createGistJob(file);
			}
		}
		return null;
	}

	private void createGistJob(String name, String extension, String contents) {
		Set<TaskRepository> repositories = TasksUi.getRepositoryManager().getRepositories(GitHub.CONNECTOR_KIND);
		TaskRepository repository = repositories.iterator().next();
		GitHubService service = new GitHubService();
		GitHubCredentials credentials = GitHubCredentials.create(repository);
		CreateGistJob job = new CreateGistJob("Creating Gist", name, extension, contents, credentials, service);
		job.setSystem(true);
		job.schedule();
	}

	private void createGistJob(IFile file) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()));
			String line;
			StringBuffer result = new StringBuffer();
			while ((line = br.readLine()) != null) {
			result.append(line);
			result.append('\n');
			}
			String contents = result.toString();
			createGistJob(file.getName(), file.getFileExtension(), contents);
		} catch (CoreException e) {
			GitHubUi.logError(e);
		} catch (IOException e) {
			GitHubUi.logError(e);
		}
	}

}
