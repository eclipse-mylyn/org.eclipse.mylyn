/*******************************************************************************
 * Copyright (c) 2012, 2014 SAP and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sascha Scholz (SAP) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.egit;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.ui.internal.provisional.wizards.GitRepositoryInfo;
import org.eclipse.egit.ui.internal.provisional.wizards.IRepositorySearchResult;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.mylyn.commons.workbench.SubstringPatternFilter;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;

import com.google.gerrit.reviewdb.AccountGeneralPreferences.DownloadScheme;
import com.google.gerrit.reviewdb.Project;

/**
 * @author Sascha Scholz
 */
public class GerritRepositorySearchPage extends WizardPage implements IRepositorySearchResult {

	private Combo cloneUriCombo;

	private Button refreshButton;

	public GerritRepositorySearchPage() {
		super(GerritRepositorySearchPage.class.getName());
		setTitle(Messages.GerritRepositorySearchPage_Source_Git_Repository);
		setMessage(Messages.GerritRepositorySearchPage_Select_Gerrit_project);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(container);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(container);

		Composite composite = new Composite(container, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(composite);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);

		FilteredTree tree = new FilteredTree(composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL,
				new SubstringPatternFilter(), true);
		final TreeViewer tv = tree.getViewer();
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tree);
		tv.setContentProvider(new GerritRepositorySearchPageContentProvider());
		tv.setLabelProvider(new GerritRepositorySearchPageLabelProvider());
		tv.setInput(this);
		tv.addSelectionChangedListener(event -> {
			ITreeSelection selection = (ITreeSelection) event.getSelection();
			GerritRepositorySearchPage.this.selectionChanged(tv, selection);
		});
		tv.addDoubleClickListener(event -> {
			ITreeSelection selection = (ITreeSelection) event.getSelection();
			GerritRepositorySearchPage.this.doubleClick(tv, selection);
		});
		tv.getTree().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.keyCode == SWT.F5) {
					refreshConfigIfTaskRepositoryIsSelected(tv);
				}
			}
		});

		final Composite buttonsComposite = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(buttonsComposite);
		buttonsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		buttonsComposite.setLayout(new GridLayout(2, false));

		Button addButton = new Button(buttonsComposite, SWT.NONE);
		addButton.setText(Messages.GerritRepositorySearchPage_Add);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = getShell();
				if (shell != null && !shell.isDisposed()) {
					AddRepositoryAction addRepositoryAction = new AddRepositoryAction();
					addRepositoryAction.setPromptToAddQuery(false);
					TaskRepository addedRepository = addRepositoryAction.showWizard(shell,
							GerritConnector.CONNECTOR_KIND);
					if (addedRepository != null) {
						tv.refresh();
						refreshConfig(tv, addedRepository);
						tv.expandToLevel(addedRepository, AbstractTreeViewer.ALL_LEVELS);
					}
				}
			}
		});

		refreshButton = new Button(buttonsComposite, SWT.NONE);
		refreshButton.setText(Messages.GerritRepositorySearchPage_Refresh);
		refreshButton.setEnabled(false);
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshConfigIfTaskRepositoryIsSelected(tv);
			}
		});

		final Group repositoryGroup = new Group(composite, SWT.NONE);
		repositoryGroup.setText(Messages.GerritRepositorySearchPage_Git_Repository);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(repositoryGroup);
		repositoryGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		repositoryGroup.setLayout(new GridLayout(2, false));

		new Label(repositoryGroup, SWT.NULL).setText(Messages.GerritRepositorySearchPage_URI);
		cloneUriCombo = new Combo(repositoryGroup, SWT.DROP_DOWN);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(cloneUriCombo);

		clearCloneUris();
		setControl(container);
	}

	private void selectionChanged(final TreeViewer tv, ITreeSelection selection) {
		clearError();
		if (selection.size() == 1) {
			refreshButton.setEnabled(true);
			if (selection.getFirstElement() instanceof Project) {
				TreePath path = selection.getPaths()[0];
				TaskRepository repository = (TaskRepository) path.getSegment(0);
				Project project = (Project) path.getSegment(1);
				try {
					GerritConfiguration config = GerritCorePlugin.getGerritClient(repository).getConfiguration();
					setCloneUris(GerritUtil.getCloneUris(config, repository, project));
				} catch (URISyntaxException e) {
					String message = NLS.bind(Messages.GerritRepositorySearchPage_Unable_to_compute_clone_URI,
							project.getName());
					showError(e, message);
					clearCloneUris();
				}
			} else if (selection.getFirstElement() instanceof TaskRepository) {
				clearCloneUris();
			}
		} else {
			refreshButton.setEnabled(false);
			clearCloneUris();
		}
	}

	private void showError(Exception e, String message) {
		GerritCorePlugin.logError(message, e);
		setErrorMessage(message);
	}

	private void clearError() {
		setErrorMessage(null);
	}

	private void setCloneUris(HashMap<DownloadScheme, String> uriMap) {
		cloneUriCombo.removeAll();
		addCloneUriIfNotNull(uriMap, DownloadScheme.SSH);
		addCloneUriIfNotNull(uriMap, DownloadScheme.HTTP);
		addCloneUriIfNotNull(uriMap, DownloadScheme.ANON_HTTP);
		addCloneUriIfNotNull(uriMap, DownloadScheme.ANON_GIT);
		if (cloneUriCombo.getItemCount() > 0) {
			cloneUriCombo.select(0);
			cloneUriCombo.setEnabled(true);
			setPageComplete(true);
		} else {
			clearCloneUris();
			showError(null, Messages.GerritRepositorySearchPage_No_download_scheme);
		}
	}

	private void addCloneUriIfNotNull(HashMap<DownloadScheme, String> uriMap, DownloadScheme scheme) {
		String uri = uriMap.get(scheme);
		if (uri != null) {
			cloneUriCombo.add(uri);
		}
	}

	private void clearCloneUris() {
		cloneUriCombo.removeAll();
		cloneUriCombo.setText(""); //$NON-NLS-1$
		cloneUriCombo.setEnabled(false);
		setPageComplete(false);
	}

	private void doubleClick(TreeViewer tv, ITreeSelection selection) {
		clearError();
		GerritRepositorySearchPage.this.selectionChanged(tv, selection);
		if (selection.size() == 1 && selection.getFirstElement() instanceof TaskRepository) {
			Object element = selection.getFirstElement();
			if (tv.getExpandedState(element)) {
				tv.collapseToLevel(element, 1);
			} else {
				tv.expandToLevel(element, 1);
			}
		}
		if (isPageComplete()) {
			getContainer().showPage(getNextPage());
		}
	}

	private void refreshConfig(TreeViewer tv, final TaskRepository repository) {
		clearError();
		try {
			final GerritClient client = GerritCorePlugin.getGerritClient(repository);
			getContainer().run(true, true, monitor -> {
				monitor.beginTask(
						NLS.bind(Messages.GerritRepositorySearchPage_Refreshing_X, repository.getRepositoryLabel()),
						IProgressMonitor.UNKNOWN);
				try {
					client.refreshConfig(monitor);
				} catch (GerritException e) {
					throw new InvocationTargetException(e, e.getMessage());
				}
				monitor.done();
			});
			tv.refresh(repository);
		} catch (InvocationTargetException e) {
			showError(e, e.getMessage());
		} catch (InterruptedException e) {
		}
	}

	private void refreshConfigIfTaskRepositoryIsSelected(final TreeViewer tv) {
		ITreeSelection selection = (ITreeSelection) tv.getSelection();
		Object element = selection.getFirstElement();
		if (element instanceof TaskRepository) {
			refreshConfig(tv, (TaskRepository) element);
		} else if (element instanceof Project) {
			TreePath path = selection.getPaths()[0];
			refreshConfig(tv, (TaskRepository) path.getSegment(0));
		}
	}

	@Override
	public GitRepositoryInfo getGitRepositoryInfo() {
		GitRepositoryInfo gitRepositoryInfo = new GitRepositoryInfo(cloneUriCombo.getText());
		addFetchReviewNotesRefSpec(gitRepositoryInfo);
		addPushToGerritRefSpec(gitRepositoryInfo);
		addCreateGerritChangeIdConfig(gitRepositoryInfo);
		return gitRepositoryInfo;
	}

	private void addFetchReviewNotesRefSpec(GitRepositoryInfo gitRepositoryInfo) {
		String notesRef = Constants.R_NOTES + "*"; //$NON-NLS-1$
		gitRepositoryInfo.addFetchRefSpec("+" + notesRef + ":" + notesRef); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void addPushToGerritRefSpec(GitRepositoryInfo gitRepositoryInfo) {
		gitRepositoryInfo.addPushInfo("HEAD:refs/for/master", null); //$NON-NLS-1$
	}

	private void addCreateGerritChangeIdConfig(GitRepositoryInfo gitRepositoryInfo) {
		gitRepositoryInfo.addRepositoryConfigProperty(ConfigConstants.CONFIG_GERRIT_SECTION, null,
				ConfigConstants.CONFIG_KEY_CREATECHANGEID, ConfigConstants.CONFIG_KEY_TRUE);
	}
}
