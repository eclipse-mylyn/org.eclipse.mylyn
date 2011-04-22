/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.RepositoryUtil;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.egit.core.op.CloneOperation.PostCloneTask;
import org.eclipse.egit.core.op.ConnectProviderOperation;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.gist.GistAttribute;
import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * Clone Gist handler class.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CloneGistHandler extends AbstractHandler {

	/**
	 * ID
	 */
	public static final String ID = "org.eclipse.mylyn.github.ui.command.cloneGist"; //$NON-NLS-1$

	private File getParentDirectory() {
		String destinationDir = Activator.getDefault().getPreferenceStore()
				.getString(UIPreferences.DEFAULT_REPO_DIR);
		File parentDir = new File(destinationDir);
		if (!parentDir.exists() || !parentDir.isDirectory())
			parentDir = ResourcesPlugin.getWorkspace().getRoot()
					.getRawLocation().toFile();
		return parentDir;
	}

	private void createProject(final File workDir, final String name,
			final Repository repository, IProgressMonitor monitor)
			throws CoreException {
		IProjectDescription description = null;
		String projectName = null;
		File projectFile = new File(workDir, ".project"); //$NON-NLS-1$
		if (projectFile.exists()) {
			description = ResourcesPlugin.getWorkspace()
					.loadProjectDescription(
							Path.fromOSString(projectFile.getAbsolutePath()));
			projectName = description.getName();
		} else {
			description = ResourcesPlugin.getWorkspace().newProjectDescription(
					name);
			description
					.setLocation(Path.fromOSString(workDir.getAbsolutePath()));
			projectName = name;
		}

		monitor.setTaskName(Messages.CloneGistHandler_TaskCreatingProject);
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		project.create(description, monitor);
		project.open(IResource.BACKGROUND_REFRESH, monitor);

		monitor.setTaskName(Messages.CloneGistHandler_TaskConnectingProject);
		ConnectProviderOperation cpo = new ConnectProviderOperation(project,
				repository.getDirectory());
		cpo.execute(monitor);
	}

	private CloneOperation createCloneOperation(TaskData data, String name)
			throws IOException, URISyntaxException {
		String pullUrl = data.getRoot()
				.getAttribute(GistAttribute.CLONE_URL.getId()).getValue();
		URIish uri = new URIish(pullUrl);
		int timeout = Activator.getDefault().getPreferenceStore()
				.getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT);
		final File workDir = new File(getParentDirectory(), name);

		if (getRepoUtil().getConfiguredRepositories().contains(
				new File(workDir, Constants.DOT_GIT).getAbsolutePath()))
			throw new IOException(MessageFormat.format(
					Messages.CloneGistHandler_ErrorRepoExists, name));

		return new CloneOperation(uri, true, null, workDir, Constants.R_HEADS
				+ Constants.MASTER, Constants.DEFAULT_REMOTE_NAME, timeout);
	}

	private void updateCredentials(TaskData data, CloneOperation operation) {
		TaskRepository repository = TasksUi.getRepositoryManager()
				.getRepository(GistConnector.KIND, data.getRepositoryUrl());
		AuthenticationCredentials credentials = repository
				.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null) {
			UsernamePasswordCredentialsProvider provider = new UsernamePasswordCredentialsProvider(
					credentials.getUserName(), credentials.getPassword()
							.toCharArray());
			operation.setCredentialsProvider(provider);
		}
	}

	private RepositoryUtil getRepoUtil() {
		return org.eclipse.egit.core.Activator.getDefault().getRepositoryUtil();
	}

	private Job createCloneJob(final ExecutionEvent event, final TaskData data) {
		Job job = new Job(Messages.CloneGistHandler_TaskCloning) {

			protected IStatus run(IProgressMonitor monitor) {
				try {
					final String name = "gist-" + data.getTaskId(); //$NON-NLS-1$

					CloneOperation operation = createCloneOperation(data, name);
					updateCredentials(data, operation);

					operation.addPostCloneTask(new PostCloneTask() {

						public void execute(Repository repository,
								IProgressMonitor monitor) throws CoreException {
							if (monitor.isCanceled())
								return;
							monitor.setTaskName(Messages.CloneGistHandler_TaskRegisteringRepository);
							getRepoUtil().addConfiguredRepository(
									repository.getDirectory());
						}
					});

					operation.addPostCloneTask(new PostCloneTask() {

						public void execute(final Repository repository,
								IProgressMonitor monitor) throws CoreException {
							IWorkspaceRunnable runnable = new IWorkspaceRunnable() {

								public void run(IProgressMonitor monitor)
										throws CoreException {
									if (monitor.isCanceled())
										return;
									createProject(repository.getDirectory()
											.getParentFile(), name, repository,
											monitor);
								}
							};
							ResourcesPlugin.getWorkspace().run(runnable,
									monitor);
						}
					});

					operation.run(monitor);
				} catch (Exception e) {
					displayError(event, e);
					Activator.logError("Error cloning gist", e); //$NON-NLS-1$
				}
				return Status.OK_STATUS;
			}
		};
		return job;
	}

	private void displayError(final ExecutionEvent event, Exception exception) {
		final Throwable cause = exception.getCause() != null ? exception
				.getCause() : exception;
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				ErrorDialog.openError(HandlerUtil.getActiveShell(event),
						Messages.CloneGistHandler_ErrorTitle,
						Messages.CloneGistHandler_ErrorMessage, Activator
								.createErrorStatus(cause.getLocalizedMessage(),
										cause));
			}
		});
	}

	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchSite activeSite = HandlerUtil.getActiveSite(event);
		IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) activeSite
				.getService(IWorkbenchSiteProgressService.class);

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection == null || selection.isEmpty())
			selection = HandlerUtil.getActiveMenuSelection(event);

		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			Object first = ((IStructuredSelection) selection).getFirstElement();
			if (first instanceof TaskData)
				service.schedule(createCloneJob(event, (TaskData) first));
		}
		return null;
	}
}
