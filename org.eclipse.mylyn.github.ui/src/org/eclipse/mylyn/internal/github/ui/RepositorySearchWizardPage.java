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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.github.core.Language;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.util.UrlUtils;
import org.eclipse.egit.ui.internal.UIIcons;
import org.eclipse.egit.ui.internal.provisional.wizards.GitRepositoryInfo;
import org.eclipse.egit.ui.internal.provisional.wizards.IRepositorySearchResult;
import org.eclipse.egit.ui.internal.provisional.wizards.NoRepositoryInfoException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GitHubException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Search for GitHub repositories wizard page.
 */
@SuppressWarnings("restriction")
public class RepositorySearchWizardPage extends WizardPage implements
		IRepositorySearchResult {

	private SearchRepository[] repositories = null;

	private final RepositoryService repositoryService;

	private Text searchText;

	/**
	 *
	 */
	public RepositorySearchWizardPage() {
		super("repoSearchPage", Messages.RepositorySearchWizardPage_Title, null); //$NON-NLS-1$
		setDescription(Messages.RepositorySearchWizardPage_Description);
		setPageComplete(false);

		repositoryService = new RepositoryService();
		GitHub.configureClient(repositoryService.getClient());
	}

	/**
	 * Get selected repositories
	 *
	 * @return repositories
	 */
	protected SearchRepository[] getRepositories() {
		return repositories;
	}

	/**
	 *
	 */
	public void createControl(Composite parent) {
		final Composite root = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(root);

		Composite rowOne = new Composite(root, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(rowOne);

		Label searchForLabel = new Label(rowOne, SWT.NONE);
		searchForLabel
				.setText(Messages.RepositorySearchWizardPage_SearchForRepositories);

		final Combo languageCombo = new Combo(rowOne, SWT.READ_ONLY
				| SWT.DROP_DOWN);
		languageCombo.add(Messages.RepositorySearchWizardPage_AnyLanguage);

		for (Language language : Language.values())
			languageCombo.add(language.getValue());

		languageCombo.select(0);

		Composite rowTwo = new Composite(root, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(rowTwo);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(rowTwo);

		searchText = new Text(rowTwo, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(searchText);

		final Button searchButton = new Button(rowTwo, SWT.NONE);
		searchButton.setText(Messages.RepositorySearchWizardPage_SearchButton);
		searchButton.setEnabled(false);

		final TableViewer repoListViewer = new TableViewer(root);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(repoListViewer.getControl());
		repoListViewer.setContentProvider(new ArrayContentProvider());

		repoListViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(
				new IStyledLabelProvider() {

					private Image repoImage = UIIcons.REPOSITORY.createImage();

					public void removeListener(ILabelProviderListener listener) {
					}

					public boolean isLabelProperty(Object element,
							String property) {
						return false;
					}

					public void dispose() {
						repoImage.dispose();
					}

					public void addListener(ILabelProviderListener listener) {
					}

					public StyledString getStyledText(Object element) {
						StyledString styled = new StyledString();
						SearchRepository repo = (SearchRepository) element;
						styled.append(repo.getOwner() + "/" + repo.getName()); //$NON-NLS-1$
						String language = repo.getLanguage();
						if (language != null && language.length() > 0)
							styled.append(" (" + language + ")", //$NON-NLS-1$ //$NON-NLS-2$
									StyledString.QUALIFIER_STYLER);

						int forks = repo.getForks();
						if (forks != 1)
							styled.append(MessageFormat.format(
									Messages.RepositorySearchWizardPage_Forks,
									forks), StyledString.COUNTER_STYLER);
						else
							styled.append(
									Messages.RepositorySearchWizardPage_Fork,
									StyledString.COUNTER_STYLER);

						int watchers = repo.getWatchers();
						if (watchers != 1)
							styled.append(
									MessageFormat
											.format(Messages.RepositorySearchWizardPage_Watchers,
													watchers),
									StyledString.COUNTER_STYLER);
						else
							styled.append(
									Messages.RepositorySearchWizardPage_Watcher,
									StyledString.COUNTER_STYLER);

						return styled;
					}

					public Image getImage(Object element) {
						return repoImage;
					}
				}));

		repoListViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						validate(repoListViewer);
					}
				});

		searchText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				searchButton
						.setEnabled(searchText.getText().trim().length() != 0);
			}
		});

		searchButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent selectionEvent) {
				String language = null;
				if (languageCombo.getSelectionIndex() > 0)
					language = languageCombo.getText();
				search(language, searchText.getText().trim(), repoListViewer);
			}
		});

		setControl(root);
	}

	private void validate(TableViewer viewer) {
		ISelection selection = viewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object[] selected = ((IStructuredSelection) selection).toArray();
			repositories = new SearchRepository[selected.length];
			System.arraycopy(selected, 0, repositories, 0, selected.length);
		}
		setPageComplete(!selection.isEmpty());
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			searchText.setFocus();
	}

	private void search(final String language, final String text,
			final TableViewer viewer) {
		viewer.setSelection(StructuredSelection.EMPTY);
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask(
							MessageFormat
									.format(Messages.RepositorySearchWizardPage_Searching,
											text), 10);
					try {
						StringBuilder query = new StringBuilder();
						for (String term : text.split("\n")) //$NON-NLS-1$
							query.append(UrlUtils.encode(term.trim())).append(
									'+');
						query.deleteCharAt(query.length() - 1);
						final List<SearchRepository> repositories = repositoryService
								.searchRepositories(query.toString(), language);
						PlatformUI.getWorkbench().getDisplay()
								.syncExec(new Runnable() {

									public void run() {
										if (viewer.getControl().isDisposed())
											return;
										setMessage(
												MessageFormat
														.format(Messages.RepositorySearchWizardPage_Found,
																repositories
																		.size()),
												INFORMATION);
										viewer.setInput(repositories);
										validate(viewer);
									}
								});
					} catch (IOException e) {
						throw new InvocationTargetException(GitHubException
								.wrap(e));
					}
				}
			});
			setErrorMessage(null);
		} catch (InvocationTargetException e) {
			viewer.setInput(Collections.emptyList());
			Throwable cause = e.getCause();
			if (cause == null)
				cause = e;
			setErrorMessage(MessageFormat.format(
					Messages.RepositorySearchWizardPage_Error,
					cause.getLocalizedMessage()));
		} catch (InterruptedException e) {
			GitHubUi.logError(e);
		}
	}

	public GitRepositoryInfo getGitRepositoryInfo()
			throws NoRepositoryInfoException {
		String cloneUrl = null;
		try {
			Repository fullRepo = repositoryService
					.getRepository(repositories[0]);
			cloneUrl = fullRepo.getCloneUrl();
		} catch (IOException e) {
			throw new NoRepositoryInfoException(e.getMessage(), e);
		}
		return new GitRepositoryInfo(cloneUrl);
	}
}
