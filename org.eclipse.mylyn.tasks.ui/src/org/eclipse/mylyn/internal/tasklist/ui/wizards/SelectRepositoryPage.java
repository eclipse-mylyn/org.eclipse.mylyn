/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylar.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Mik Kersten
 */
public class SelectRepositoryPage extends WizardPage {

	private static final String DESCRIPTION = "Select a repository, or add a new one using the Task Repositories view.";

	private static final String TITLE = "Select a repository";

	private TableViewer viewer;
	
	protected AbstractRepositoryWizard wizard;
	
	private String repositoryKind = null;
	
	class RepositoryContentProvider implements IStructuredContentProvider {
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (repositoryKind != null) {
				return MylarTaskListPlugin.getRepositoryManager().getRepositories(repositoryKind).toArray();
			} else {
				return MylarTaskListPlugin.getRepositoryManager().getAllRepositories().toArray();
			}
		}
	}
	
	public SelectRepositoryPage(AbstractRepositoryWizard wizard) {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		this.wizard = wizard;
		super.setWizard(wizard);
	}
	
	public SelectRepositoryPage(AbstractRepositoryWizard wizard, String repositoryKind) {
		this(wizard);
		this.repositoryKind = repositoryKind;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return wizard.getRepository() != null;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		FillLayout layout = new FillLayout();
		container.setLayout(layout);
		
		viewer = new TableViewer(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new RepositoryContentProvider());
		viewer.setLabelProvider(new TaskRepositoryLabelProvider());
		viewer.setInput(MylarTaskListPlugin.getRepositoryManager().getRepositoryClients());
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				if (selection.getFirstElement() instanceof TaskRepository) {
					wizard.setRepository((TaskRepository)selection.getFirstElement());
					SelectRepositoryPage.this.setPageComplete(true);
					try {
						wizard.getContainer().updateButtons();
					} catch (NullPointerException npe) {
						// ignore, back button couldn't be updated
						// TODO: remove this catch block
					}
				}
			}
		}); 
		viewer.getTable().setFocus();
		TaskRepository defaultRepository = MylarTaskListPlugin.getRepositoryManager().getDefaultRepository(repositoryKind);
		if (defaultRepository != null) {
			 viewer.setSelection(new StructuredSelection(defaultRepository));
		}
		
		setControl(container);
	}
}
