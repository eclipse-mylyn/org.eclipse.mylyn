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
package org.eclipse.mylyn.github.ui.internal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.ui.internal.FilteredCheckboxTree;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchContentProvider;

/**
 * Repository selection wizard page.
 */
public class RepositorySelectionWizardPage extends WizardPage {

	private Button addGistRepoButton;
	private Label selectedLabel;
	private FilteredCheckboxTree tree;
	private int repoCount = 0;
	private String user;
	private String password;

	/**
	 * Create repository selection wizard page
	 */
	public RepositorySelectionWizardPage() {
		super(
				"repositoriesPage", Messages.RepositorySelectionWizardPage_Title, null); //$NON-NLS-1$
		setDescription(Messages.RepositorySelectionWizardPage_Description);
	}

	/** @param user */
	public void setUser(String user) {
		this.user = user;
	}

	/** @param password */
	public void setPassword(String password) {
		this.password = password;
	}

	/** @return true to create giste repository, false otherwise */
	public boolean createGistRepository() {
		return this.addGistRepoButton.getSelection()
				&& this.addGistRepoButton.isVisible();
	}

	/** @return array of selected repositories */
	public Object[] getRepositories() {
		return this.tree.getCheckboxTreeViewer().getCheckedLeafElements();
	}

	/** @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite) */
	public void createControl(Composite parent) {
		Composite displayArea = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(displayArea);

		Label repoLabel = new Label(displayArea, SWT.NONE);
		repoLabel.setText(Messages.RepositorySelectionWizardPage_LabelRepos);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
				.applyTo(repoLabel);

		tree = new FilteredCheckboxTree(displayArea, null, SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER, new PatternFilter());
		CheckboxTreeViewer viewer = tree.getCheckboxTreeViewer();
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setSorter(new ViewerSorter());
		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				updateSelectionLabel();
			}
		});
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tree);

		ToolBar toolbar = new ToolBar(displayArea, SWT.FLAT | SWT.VERTICAL);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(toolbar);
		ToolItem checkItem = new ToolItem(toolbar, SWT.PUSH);
		checkItem.setImage(GitHubImages.get(GitHubImages.GITHUB_CHECKALL_OBJ));
		checkItem
				.setToolTipText(Messages.RepositorySelectionWizardPage_TooltipCheckAll);
		checkItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				tree.getCheckboxTreeViewer().setAllChecked(true);
				updateSelectionLabel();
			}
		});
		ToolItem uncheckItem = new ToolItem(toolbar, SWT.PUSH);
		uncheckItem.setImage(GitHubImages
				.get(GitHubImages.GITHUB_UNCHECKALL_OBJ));
		uncheckItem
				.setToolTipText(Messages.RepositorySelectionWizardPage_TooltipUncheckAll);
		uncheckItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				tree.getCheckboxTreeViewer().setAllChecked(false);
				updateSelectionLabel();
			}
		});

		selectedLabel = new Label(displayArea, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
				.applyTo(selectedLabel);

		addGistRepoButton = new Button(displayArea, SWT.CHECK);
		addGistRepoButton
				.setText(Messages.RepositorySelectionWizardPage_LabelAddGist);
		addGistRepoButton.setSelection(true);
		GridDataFactory.swtDefaults().span(2, 1).applyTo(addGistRepoButton);
		addGistRepoButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}

		});

		setControl(displayArea);
		setPageComplete(false);
	}

	private void updateSelectionLabel() {
		selectedLabel.setText(MessageFormat.format(
				Messages.RepositorySelectionWizardPage_LabelSelectionCount,
				tree.getCheckboxTreeViewer().getCheckedLeafCount(), repoCount));
		selectedLabel.getParent().layout(true, true);
		validatePage();
	}

	private void validatePage() {
		setPageComplete(getRepositories().length > 0 || createGistRepository());
		if (isPageComplete())
			setErrorMessage(null);
	}

	private void updateInput(final List<Repository> repos) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				if (getControl().isDisposed())
					return;
				tree.getCheckboxTreeViewer().setCheckedElements(new Object[0]);
				tree.getViewer().setInput(new WorkbenchAdapter() {

					public Object[] getChildren(Object object) {
						return repos.toArray();
					}

				});
				updateSelectionLabel();
			}
		});
	}

	/** @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean) */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible)
			return;
		addGistRepoButton.setVisible(TasksUi.getRepositoryManager()
				.getRepositories(GistConnector.KIND).isEmpty());
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					HttpHost httpHost = new HttpHost(IGitHubConstants.HOST_API_V2, -1,
							IGitHubConstants.PROTOCOL_HTTPS);
					GitHubClient client = new GitHubClient(httpHost);
					client.setCredentials(user, password);
					RepositoryService service = new RepositoryService(client);
					repoCount = 0;
					List<Repository> repos = new ArrayList<Repository>();
					try {
						monitor.beginTask("", 2); //$NON-NLS-1$
						monitor.setTaskName(Messages.RepositorySelectionWizardPage_TaskFetchingRepositories);
						repos.addAll(service.getRepositories(user));
						monitor.worked(1);
						monitor.setTaskName(Messages.RepositorySelectionWizardPage_TaskFetchingOrganizationRepositories);
						repos.addAll(service.getOrganizationRepositories());
						monitor.worked(1);
						for (TaskRepository repo : TasksUi
								.getRepositoryManager().getRepositories(
										GitHub.CONNECTOR_KIND)) {
							repos.remove(GitHub.getRepository(repo
									.getRepositoryUrl()));
						}
						repoCount = repos.size();
						updateInput(repos);
					} catch (IOException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InvocationTargetException e) {
			updateInput(Collections.<Repository> emptyList());
			setErrorMessage(MessageFormat.format(
					Messages.RepositorySelectionWizardPage_ErrorLoading, e
							.getTargetException().getLocalizedMessage()));
		} catch (InterruptedException ignored) {
			// Ignored
		}
	}
}
