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
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Mik Kersten
 * @author Brock Janiczak
 */
public abstract class SelectRepositoryPage extends WizardSelectionPage {

	private static final String DESCRIPTION = "Select a repository, or add a new one using the Task Repositories view.";

	private static final String TITLE = "Select a repository";

	private TableViewer viewer;

	protected MultiRepositoryAwareWizard wizard;

	private List<String> repositoryKinds = null;

	class RepositoryContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (repositoryKinds != null) {
				List<TaskRepository> repositories = new ArrayList<TaskRepository>();
				for (String repositoryKind : repositoryKinds) {
					repositories.addAll(MylarTaskListPlugin.getRepositoryManager().getRepositories(repositoryKind));
				}
				return repositories.toArray();
			} else {
				return MylarTaskListPlugin.getRepositoryManager().getAllRepositories().toArray();
			}
		}
	}

	public SelectRepositoryPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	public SelectRepositoryPage(List<String> repositoryKinds) {
		this();
		this.repositoryKinds = repositoryKinds;
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
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof TaskRepository) {
					setSelectedNode(new CustomWizardNode((TaskRepository) selection.getFirstElement()));
					setPageComplete(true);
				}
				setPageComplete(false);
			}
		});

		viewer.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				getContainer().showPage(getNextPage());
			}

		});
		viewer.getTable().setFocus();
//		TaskRepository defaultRepository = MylarTaskListPlugin.getRepositoryManager().getDefaultRepository(
//				repositoryKind);
//		if (defaultRepository != null) {
//			viewer.setSelection(new StructuredSelection(defaultRepository));
//		}

		setControl(container);
	}

	protected abstract IWizard createWizard(TaskRepository taskRepository);

	private class CustomWizardNode implements IWizardNode {

		private final TaskRepository repository;

		private IWizard wizard;

		public CustomWizardNode(TaskRepository repository) {
			this.repository = repository;
		}

		public void dispose() {
			if (wizard != null) {
				wizard.dispose();
			}
		}

		public Point getExtent() {
			return new Point(-1, -1);
		}

		public IWizard getWizard() {
			if (wizard == null) {
				wizard = SelectRepositoryPage.this.createWizard(repository);
			}

			return wizard;
		}

		public boolean isContentCreated() {
			return wizard != null;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CustomWizardNode)) {
				return false;
			}
			CustomWizardNode that = (CustomWizardNode) obj;
			if (this == that) {
				return true;
			}

			return this.repository.getKind().equals(that.repository.getKind())
					&& this.repository.getUrl().equals(that.repository.getUrl());
		}

		@Override
		public int hashCode() {
			return 31 * this.repository.getUrl().hashCode() + this.repository.getKind().hashCode();
		}

	}
}
