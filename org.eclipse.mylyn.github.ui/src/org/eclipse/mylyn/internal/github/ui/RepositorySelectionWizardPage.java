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
package org.eclipse.mylyn.internal.github.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.ui.internal.components.FilteredCheckboxTree;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GitHubException;
import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Repository selection wizard page.
 */
public class RepositorySelectionWizardPage extends WizardPage {

	private static class RepositoryStyledLabelProvider implements
			IStyledLabelProvider, ILabelProvider {
		private final WorkbenchLabelProvider wrapped = new WorkbenchLabelProvider();

		public String getText(Object element) {
			return wrapped.getText(element);
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
			wrapped.dispose();
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

		public StyledString getStyledText(Object element) {
			// TODO Replace with use of IWorkbenchAdapter3 when 3.6 is no longer
			// supported
			if (element instanceof RepositoryAdapter)
				return ((RepositoryAdapter) element).getStyledText(element);
			if (element instanceof OrganizationAdapter)
				return ((OrganizationAdapter) element).getStyledText(element);

			return new StyledString(wrapped.getText(element));
		}

		public Image getImage(Object element) {
			return wrapped.getImage(element);
		}

	}

	private static class RepositoryLabelProvider extends
			DelegatingStyledCellLabelProvider implements ILabelProvider {

		private final RepositoryStyledLabelProvider wrapped;

		public RepositoryLabelProvider() {
			super(new RepositoryStyledLabelProvider());
			wrapped = (RepositoryStyledLabelProvider) getStyledStringProvider();
		}

		public String getText(Object element) {
			return wrapped.getText(element);
		}

		public void dispose() {
			super.dispose();
			wrapped.dispose();
		}
	}

	private static class RepositoryAdapter extends WorkbenchAdapter {

		private final Repository repo;

		RepositoryAdapter(Repository repo) {
			this.repo = repo;
		}

		public String getLabel(Object object) {
			String label = this.repo.generateId();
			return label != null ? label : "";
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return TasksUiImages.REPOSITORY;
		}

	}

	private static class OrganizationAdapter extends WorkbenchAdapter {

		private final User org;

		private final RepositoryAdapter[] repos;

		OrganizationAdapter(User org, List<Repository> repos) {
			this.org = org;
			this.repos = new RepositoryAdapter[repos.size()];
			final int length = this.repos.length;
			for (int i = 0; i < length; i++)
				this.repos[i] = new RepositoryAdapter(repos.get(i));
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return GitHubImages.DESC_GITHUB_ORG;
		}

		public Object[] getChildren(Object object) {
			return repos;
		}

		public StyledString getStyledText(Object object) {
			StyledString styled = new StyledString(getLabel(object));
			styled.append(MessageFormat.format(" ({0})", repos.length),
					StyledString.COUNTER_STYLER);
			return styled;
		}

		public String getLabel(Object object) {
			return org.getLogin();
		}

	}

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
	public Repository[] getRepositories() {
		Object[] checked = tree.getCheckboxTreeViewer()
				.getCheckedLeafElements();
		Repository[] repos = new Repository[checked.length];
		for (int i = 0; i < repos.length; i++)
			repos[i] = ((RepositoryAdapter) checked[i]).repo;
		return repos;
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
		viewer.setLabelProvider(new RepositoryLabelProvider());
		viewer.setSorter(new ViewerSorter() {

			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof OrganizationAdapter)
					if (e2 instanceof OrganizationAdapter)
						return ((OrganizationAdapter) e1)
								.getLabel(e1)
								.compareToIgnoreCase(
										((OrganizationAdapter) e2).getLabel(e2));
					else if (e2 instanceof RepositoryAdapter)
						return 1;
				if (e1 instanceof RepositoryAdapter)
					if (e2 instanceof RepositoryAdapter)
						return ((RepositoryAdapter) e1).getLabel(e1)
								.compareToIgnoreCase(
										((RepositoryAdapter) e2).getLabel(e2));
					else if (e2 instanceof OrganizationAdapter)
						return -1;
				return super.compare(viewer, e1, e2);
			}

		});
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
				for (Object leaf : tree.getCheckboxTreeViewer()
						.getCheckedLeafElements())
					tree.getCheckboxTreeViewer().setChecked(leaf, true);
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
				tree.getCheckboxTreeViewer().setCheckedElements(new Object[0]);
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

	private void updateInput(final List<Object> repos) {
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

	private void removeExisting(List<Repository> repos, List<String> existing) {
		Iterator<Repository> iter = repos.iterator();
		while (iter.hasNext()) {
			String id = iter.next().generateId();
			if (id == null || existing.contains(id))
				iter.remove();
		}
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
					GitHubClient client = GitHub
							.configureClient(new GitHubClient());
					client.setCredentials(user, password);
					RepositoryService service = new RepositoryService(client);
					OrganizationService orgs = new OrganizationService(client);
					repoCount = 0;
					List<Object> repos = new ArrayList<Object>();
					List<String> existing = new ArrayList<String>();
					for (TaskRepository repo : TasksUi.getRepositoryManager()
							.getRepositories(GitHub.CONNECTOR_KIND)) {
						String id = GitHub.getRepository(
								repo.getRepositoryUrl()).generateId();
						if (id != null)
							existing.add(id);
					}
					try {
						monitor.beginTask("", 2); //$NON-NLS-1$
						monitor.setTaskName(Messages.RepositorySelectionWizardPage_TaskFetchingRepositories);
						List<Repository> userRepos = service.getRepositories();
						removeExisting(userRepos, existing);
						repoCount += userRepos.size();
						for (Repository repo : userRepos)
							repos.add(new RepositoryAdapter(repo));
						monitor.worked(1);
						monitor.setTaskName(Messages.RepositorySelectionWizardPage_TaskFetchingOrganizationRepositories);
						for (User org : orgs.getOrganizations()) {
							List<Repository> orgRepos = service
									.getOrgRepositories(org.getLogin());
							removeExisting(orgRepos, existing);
							repoCount += orgRepos.size();
							repos.add(new OrganizationAdapter(org, orgRepos));
						}
						updateInput(repos);
					} catch (IOException e) {
						throw new InvocationTargetException(GitHubException
								.wrap(e));
					}
				}
			});
			setErrorMessage(null);
		} catch (InvocationTargetException e) {
			updateInput(Collections.emptyList());
			Throwable cause = e.getCause();
			if (cause == null)
				cause = e;
			setErrorMessage(MessageFormat.format(
					Messages.RepositorySelectionWizardPage_ErrorLoading,
					cause.getLocalizedMessage()));
		} catch (InterruptedException ignored) {
			// Ignored
		}
	}
}
