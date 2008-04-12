/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Mik Kersten
 */
// API 3.0 rename to SelectConnectorPage
public class SelectRepositoryClientPage extends WizardPage {

	private static final String DESCRIPTION = "You can connect to an existing account using one of the installed connectors.";

	private static final String TITLE = "Select a task repository type";

	private TableViewer viewer;

	private final AbstractRepositoryClientWizard wizard;

	static class RepositoryContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			List<AbstractRepositoryConnector> userManagedRepositories = new ArrayList<AbstractRepositoryConnector>();
			for (AbstractRepositoryConnector connector : TasksUi.getRepositoryManager().getRepositoryConnectors()) {
				if (connector.isUserManaged()) {
					userManagedRepositories.add(connector);
				}
			}

			return userManagedRepositories.toArray();
		}
	}

	public SelectRepositoryClientPage(AbstractRepositoryClientWizard wizard) {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		this.wizard = wizard;
		super.setWizard(wizard);
	}

	@Override
	public boolean canFlipToNextPage() {
		return wizard.getRepositoryConnector() != null;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		FillLayout layout = new FillLayout();
		container.setLayout(layout);

		viewer = new TableViewer(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new RepositoryContentProvider());
		viewer.setSorter(new TaskRepositoriesSorter());
		viewer.setLabelProvider(new TaskRepositoryLabelProvider());
		viewer.setInput(TasksUi.getRepositoryManager().getRepositoryConnectors());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof AbstractRepositoryConnector) {
					wizard.setRepositoryConnector((AbstractRepositoryConnector) selection.getFirstElement());
					SelectRepositoryClientPage.this.setPageComplete(true);
					wizard.getContainer().updateButtons();
				}
			}

		});

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				getContainer().showPage(getNextPage());
			}
		});

		setControl(container);
	}

	@Override
	public IWizardPage getNextPage() {
		if (isPageComplete()) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(wizard.getRepositoryConnector()
					.getConnectorKind());

			AbstractRepositorySettingsPage nextPage = connectorUi.getSettingsPage();
			wizard.setRepositorySettingsPage(nextPage);
			nextPage.setWizard(wizard);
			return nextPage;
		} else {
			return super.getNextPage();
		}
	}
}
