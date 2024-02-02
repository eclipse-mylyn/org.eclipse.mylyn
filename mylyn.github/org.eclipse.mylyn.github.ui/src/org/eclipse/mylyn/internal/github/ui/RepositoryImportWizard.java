/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.RepositoryUtil;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.egit.core.settings.GitSettings;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GitHubException;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * {@link IImportWizard} for cloning GitHub repositories.
 */
@SuppressWarnings("restriction")
public class RepositoryImportWizard extends Wizard implements IImportWizard {

	private final RepositorySearchWizardPage repositorySearchWizardPage = new RepositorySearchWizardPage();

	/**
	 * Create repository import wizard
	 */
	public RepositoryImportWizard() {
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(
				WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_WIZBAN_IMPORT_WIZ));
		setWindowTitle(Messages.RepositorySearchWizardPage_Title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// empty
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		addPage(repositorySearchWizardPage);
	}

	private CloneOperation createCloneOperation(SearchRepository repo, RepositoryService service)
			throws IOException, URISyntaxException {
		Repository fullRepo = service.getRepository(repo);
		URIish uri = new URIish(fullRepo.getCloneUrl());

		String defaultRepoDir = RepositoryUtil.getDefaultRepositoryDir();
		File directory = new File(new File(defaultRepoDir, repo.getOwner()), repo.getName());

		int timeout = GitSettings.getRemoteConnectionTimeout();

		return new CloneOperation(uri, true, null, directory, Constants.R_HEADS + Constants.MASTER,
				Constants.DEFAULT_REMOTE_NAME, timeout);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		final SearchRepository[] repositories = repositorySearchWizardPage.getRepositories();
		String name = MessageFormat.format(
				Messages.RepositoryImportWizard_CloningRepositories, Integer.valueOf(repositories.length));
		Job job = new Job(name) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					SubMonitor progress = SubMonitor.convert(monitor, name, repositories.length * 3);
					GitHubClient client = GitHub.configureClient(new GitHubClient());
					RepositoryService service = new RepositoryService(client);
					for (SearchRepository repo : repositories) {
						try {
							final String id = repo.getId();
							progress.subTask(MessageFormat.format(
									Messages.RepositoryImportWizard_CreatingOperation, id));
							CloneOperation op = createCloneOperation(repo, service);
							progress.worked(1);

							monitor.setTaskName(MessageFormat.format(
									Messages.RepositoryImportWizard_Cloning, id));
							op.run(progress.newChild(1));

							monitor.setTaskName(MessageFormat.format(
									Messages.RepositoryImportWizard_Registering, id));
							RepositoryUtil.INSTANCE.addConfiguredRepository(op.getGitDir());
							progress.worked(1);
						} catch (IOException e) {
							GitHubUi.logError(GitHubException.wrap(e));
						} catch (InvocationTargetException | InterruptedException | URISyntaxException e) {
							GitHubUi.logError(e);
						}
					}
					return Status.OK_STATUS;
				} finally {
					if (monitor != null) {
						monitor.done();
					}
				}
			}
		};
		IWorkbenchSiteProgressService progress = PlatformUI.getWorkbench()
				.getService(IWorkbenchSiteProgressService.class);
		if (progress != null) {
			progress.schedule(job);
		} else {
			job.schedule();
		}
		return true;
	}
}
