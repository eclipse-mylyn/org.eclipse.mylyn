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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
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
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryFilter;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Brock Janiczak
 * @author Steffen Pingel
 * @author Eugene Kuleshov
 */
public abstract class SelectRepositoryPage extends WizardSelectionPage {

	private static final String DESCRIPTION = "Add new repositories using the " + TasksUiPlugin.LABEL_VIEW_REPOSITORIES
			+ " view.\n" + "If a repository is missing it does not support the requested operation.";

	private static final String TITLE = "Select a repository";

	private TableViewer viewer;

	protected MultiRepositoryAwareWizard wizard;

	private List<TaskRepository> repositories = new ArrayList<TaskRepository>();

	private final TaskRepositoryFilter taskRepositoryFilter;

	class RepositoryContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return repositories.toArray();
		}
	}

	public SelectRepositoryPage(TaskRepositoryFilter taskRepositoryFilter) {
		super(TITLE);

		setTitle(TITLE);
		setDescription(DESCRIPTION);

		this.taskRepositoryFilter = taskRepositoryFilter;
		this.repositories = getTaskRepositories();
	}

	public List<TaskRepository> getTaskRepositories() {
		List<TaskRepository> repositories = new ArrayList<TaskRepository>();
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		for (AbstractRepositoryConnector connector : repositoryManager.getRepositoryConnectors()) {
			Set<TaskRepository> connectorRepositories = repositoryManager.getRepositories(connector.getRepositoryType());
			for (TaskRepository repository : connectorRepositories) {
				if (taskRepositoryFilter.accept(repository, connector)) {
					repositories.add(repository);
				}
			}
		}
		return repositories;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, true);
		container.setLayout(layout);

		Table table = createTableViewer(container);
		viewer.setSorter(new TaskRepositoriesSorter());

		GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		table.setLayoutData(gridData);

		final AddRepositoryAction action = new AddRepositoryAction();

		Button button = new Button(container, SWT.NONE);
		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
		button.setText(AddRepositoryAction.TITLE);
		button.setEnabled(action.isEnabled());
		button.addSelectionListener(new SelectionAdapter() {
			@Override
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
		// viewer.setLabelProvider(new TaskRepositoryLabelProvider());
		viewer.setLabelProvider(new DecoratingLabelProvider(new TaskRepositoryLabelProvider(),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		viewer.setInput(TasksUiPlugin.getRepositoryManager().getRepositoryConnectors());

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof TaskRepository) {
					setSelectedNode(new CustomWizardNode((TaskRepository) selection.getFirstElement()));
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});

		viewer.setSelection(new StructuredSelection(new Object[] { TasksUiUtil.getSelectedRepository(viewer) }));

		viewer.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				getContainer().showPage(getNextPage());
			}

		});
		viewer.getTable().showSelection();
		viewer.getTable().setFocus();
		return viewer.getTable();
	}

	protected abstract IWizard createWizard(TaskRepository taskRepository);

	@Override
	public boolean canFlipToNextPage() {
		return getSelectedNode() != null && getNextPage() != null;
	}

	public boolean canFinish() {
		return getSelectedNode() != null && getNextPage() == null;
	}

	public boolean performFinish() {
		if (getSelectedNode() == null || getNextPage() != null) {
			// finish event will get forwarded to nested wizard
			// by container
			return false;
		}

		return getSelectedNode().getWizard().performFinish();
	}

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
				wizard.setContainer(getContainer());
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
