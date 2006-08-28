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

package org.eclipse.mylar.internal.tasks.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.mylar.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * @author Mik Kersten
 * @author Brock Janiczak
 * @author Steffen Pingel
 * @author Eugene Kuleshov
 */
public abstract class SelectRepositoryPage extends WizardSelectionPage {

	private static final String DESCRIPTION = "Add new repositories using the " + TaskRepositoriesView.NAME
			+ " view.\n" + "If a repository is missing it does not support the requested operation.";

	private static final String TITLE = "Select a repository";

	private TableViewer viewer;

	protected MultiRepositoryAwareWizard wizard;

	private TaskRepository[] repositories = null;

	private IStructuredSelection selection;

	private final TaskRepositoryFilter taskRepositoryFilter;

	class RepositoryContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return repositories;
		}
	}

	public SelectRepositoryPage(TaskRepositoryFilter taskRepositoryFilter) {
		super(TITLE);
		
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		
		this.taskRepositoryFilter = taskRepositoryFilter;
		this.repositories = getTaskRepositories();
	}

	private TaskRepository[] getTaskRepositories() {
		List<TaskRepository> repositories = new ArrayList<TaskRepository>();
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		for (AbstractRepositoryConnector connector : repositoryManager.getRepositoryConnectors()) {
			Set<TaskRepository> connectorRepositories = repositoryManager.getRepositories(connector.getRepositoryType());
			for (TaskRepository repository : connectorRepositories) {
				if(taskRepositoryFilter.accept(repository, connector)) {
					repositories.add(repository);
				}
			}
		}
		return repositories.toArray(new TaskRepository[repositories.size()]);
	}
	
	
	public SelectRepositoryPage setSelection(IStructuredSelection selection) {
		this.selection = selection;
		return this;
	}

	public IStructuredSelection getSelection() {
		return this.selection;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, true);
		container.setLayout(layout);

		Table table = createTableViewer(container);
		GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		table.setLayoutData(gridData);

		// TaskRepository defaultRepository =
		// MylarTaskListPlugin.getRepositoryManager().getDefaultRepository(
		// repositoryKind);
		// if (defaultRepository != null) {
		// viewer.setSelection(new StructuredSelection(defaultRepository));
		// }

		final AddRepositoryAction action = new AddRepositoryAction();
		
		Button button = new Button(container, SWT.NONE);
		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
		button.setText(AddRepositoryAction.TITLE);
		button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					action.run();
					SelectRepositoryPage.this.repositories = getTaskRepositories();
					viewer.setInput(TasksUiPlugin.getRepositoryManager().getRepositoryConnectors());
				}
			});

		setControl(container);
	}

	protected Table createTableViewer(Composite container) {
		viewer = new TableViewer(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new RepositoryContentProvider());
		viewer.setLabelProvider(new TaskRepositoryLabelProvider());
		viewer.setInput(TasksUiPlugin.getRepositoryManager().getRepositoryConnectors());

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

		viewer.setSelection(new StructuredSelection(new Object[] { getSelectedRepository() }));

		viewer.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				getContainer().showPage(getNextPage());
			}

		});
		viewer.getTable().showSelection();
		viewer.getTable().setFocus();
		return viewer.getTable();
	}

	protected TaskRepository getSelectedRepository() {
		if (selection == null) {
			return (TaskRepository) viewer.getElementAt(0);
		}

		Object element = selection.getFirstElement();
		if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			return getRepository(query.getRepositoryUrl(), query.getRepositoryKind());

		} else if (element instanceof AbstractQueryHit) {
			AbstractQueryHit queryHit = (AbstractQueryHit) element;
			if (queryHit.getParent() != null) {
				return getRepository(queryHit.getRepositoryUrl(), queryHit.getParent().getRepositoryKind());
			} else {
				return TasksUiPlugin.getRepositoryManager().getRepository(queryHit.getRepositoryUrl());
			}
		} else if (element instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask task = (AbstractRepositoryTask) element;
			return getRepository(task.getRepositoryUrl(), task.getRepositoryKind());
		}

		// TODO handle project (when link from projects to repositories is
		// implemented)

		return null;
	}

	private TaskRepository getRepository(String repositoryUrl, String repositoryKind) {
		return TasksUiPlugin.getRepositoryManager().getRepository(repositoryKind, repositoryUrl);
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
