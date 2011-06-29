/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.Activator;
import org.eclipse.egit.core.RepositoryUtil;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * {@link IImportWizard} for cloning GitHub repositories.
 */
public class RepositoryImportWizard extends Wizard implements IImportWizard {

	/**
	 * 
	 */
	private RepositorySearchWizardPage repositorySearchWizardPage = new RepositorySearchWizardPage();

	/**
	 * {@inheritDoc}
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		addPage(repositorySearchWizardPage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		try {
			SearchRepository repository = repositorySearchWizardPage
					.getRepository();
			String repoName = repository.toString();
			URIish uri = new URIish("git://github.com/" + repoName //$NON-NLS-1$
					+ Constants.DOT_GIT);
			boolean allSelected = true;
			Collection<Ref> selectedBranches = Collections.emptyList();
			IPreferenceStore preferenceStore = org.eclipse.egit.ui.Activator
					.getDefault().getPreferenceStore();
			int timeout = preferenceStore
					.getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT);
			String defaultRepoDir = preferenceStore
					.getString(UIPreferences.DEFAULT_REPO_DIR);
			final CloneOperation cloneOperation = new CloneOperation(uri,
					allSelected, selectedBranches, new File(defaultRepoDir
							+ File.separator + repoName), Constants.R_HEADS
							+ Constants.MASTER, Constants.DEFAULT_REMOTE_NAME,
					timeout);
			Job job = new Job(Messages.RepositoryImportWizard_CloningRepository) {
				protected IStatus run(IProgressMonitor monitor) {
					try {
						monitor.beginTask(
								Messages.RepositoryImportWizard_CloningRepository,
								100);
						cloneOperation.run(monitor);
						monitor.worked(90);
						RepositoryUtil repositoryUtil = Activator.getDefault()
								.getRepositoryUtil();
						monitor.worked(99);
						repositoryUtil.addConfiguredRepository(cloneOperation
								.getGitDir());
						monitor.done();
					} catch (InvocationTargetException invocationTargetException) {
						GitHubUi.logError(invocationTargetException);
					} catch (InterruptedException interruptedException) {
						GitHubUi.logError(interruptedException);
					}
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			IWorkbenchSiteProgressService workbenchSiteProgressService = (IWorkbenchSiteProgressService) PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActivePart().getSite()
					.getService(IWorkbenchSiteProgressService.class);
			workbenchSiteProgressService.schedule(job);
		} catch (URISyntaxException uriSyntaxException) {
			GitHubUi.logError(uriSyntaxException);
			return false;
		}
		return true;
	}

}
