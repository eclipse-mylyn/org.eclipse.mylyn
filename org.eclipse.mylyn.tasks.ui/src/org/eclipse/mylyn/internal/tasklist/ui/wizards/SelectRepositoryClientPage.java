/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Mik Kersten
 */
public class SelectRepositoryClientPage extends WizardPage {

	private static final String DESCRIPTION = "You can connect to an existing account using one of the installed connectors.";

	private static final String TITLE = "Select a task repository type";

	private TableViewer viewer;

	private AbstractRepositoryClientWizard wizard;

	class RepositoryContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return MylarTaskListPlugin.getRepositoryManager().getRepositoryConnectors().toArray();
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
		return wizard.getRepositoryClient() != null;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		FillLayout layout = new FillLayout();
		container.setLayout(layout);

		viewer = new TableViewer(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new RepositoryContentProvider());
		viewer.setLabelProvider(new TaskRepositoryLabelProvider());
		viewer.setInput(MylarTaskListPlugin.getRepositoryManager().getRepositoryConnectors());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof AbstractRepositoryConnector) {
					wizard.setRepositoryClient((AbstractRepositoryConnector) selection.getFirstElement());
					SelectRepositoryClientPage.this.setPageComplete(true);
					wizard.getContainer().updateButtons();
				}
			}

		});
		setControl(container);
	}

	@Override
	public IWizardPage getNextPage() {
		if (isPageComplete()) {
			AbstractRepositorySettingsPage nextPage = wizard.getRepositoryClient().getSettingsPage();
			wizard.setRepositorySettingsPage(nextPage);
			nextPage.setWizard(wizard);
			return nextPage;
		} else {
			return super.getNextPage();
		}
	}
}
