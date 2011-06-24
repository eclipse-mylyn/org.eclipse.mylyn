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

import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Search for GitHub repositories wizard page.
 */
public class RepositorySearchWizardPage extends WizardPage {

	private Text searchForText;
	private Button searchButton;
	private List repoList;
	private ListViewer repoListViewer;

	/**
	 * 
	 */
	protected RepositorySearchWizardPage() {
		super(RepositorySearchWizardPage.class.getName());
		setPageComplete(false);
	}

	/**
	 * 
	 */
	public void createControl(Composite parent) {
		final Composite root = new Composite(parent, SWT.NONE);
		setControl(root);
		root.setLayout(new GridLayout(3, false));

		Label searchForLabel = new Label(root, SWT.NONE);
		searchForLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 3, 1));
		searchForLabel
				.setText(Messages.RepositorySearchWizardPage_SearchForRepositories);

		searchForText = new Text(root, SWT.BORDER);
		searchForText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));

		searchButton = new Button(root, SWT.NONE);
		searchButton.setText(Messages.RepositorySearchWizardPage_SearchButton);
		searchButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent selectionEvent) {

				Shell shell = root.getShell();
				Cursor prevCursor = shell.getCursor();
				Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
				shell.setCursor(busy);
				try {

					GitHubClient client = new GitHubClient(
							IGitHubConstants.HOST_API_V2, -1,
							IGitHubConstants.PROTOCOL_HTTPS);
					RepositoryService repositoryService = new RepositoryService(
							client);
					java.util.List<SearchRepository> repositories = repositoryService
							.searchRepositories(searchForText.getText());
					repoListViewer.setInput(repositories.toArray());
				} catch (IOException ioException) {
					repoListViewer.setInput(new Object[] {});
					GitHubUi.logError(ioException);
				} finally {
					shell.setCursor(prevCursor);
					busy.dispose();
				}

				setPageComplete(false);
			}
		});

		repoListViewer = new ListViewer(root, SWT.BORDER | SWT.V_SCROLL);
		repoList = repoListViewer.getList();
		repoList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
				1));
		repoListViewer.setContentProvider(new ArrayContentProvider());
		repoListViewer.setLabelProvider(new LabelProvider());
		repoListViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						setPageComplete(!repoListViewer.getSelection()
								.isEmpty());

					}
				});

	}
}
