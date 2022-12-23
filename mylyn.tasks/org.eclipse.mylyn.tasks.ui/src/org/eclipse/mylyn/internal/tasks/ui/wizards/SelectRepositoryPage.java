/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Brock Janiczak - improvements
 *     Eugene Kuleshov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.GradientDrawer;
import org.eclipse.mylyn.internal.tasks.core.Category;
import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesViewSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 * @author Brock Janiczak
 * @author Steffen Pingel
 * @author Eugene Kuleshov
 */
public abstract class SelectRepositoryPage extends WizardSelectionPage {

	private TreeViewer viewer;

	protected MultiRepositoryAwareWizard wizard;

	private List<TaskRepository> repositories = new ArrayList<TaskRepository>();

	private final ITaskRepositoryFilter taskRepositoryFilter;

//	private TaskRepositoriesContentProvider contentProvider;

	class RepositoryContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}

		public Object[] getElements(Object inputElement) {
			return repositories.toArray();
		}

		public void dispose() {
			// ignore

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// ignore

		}

	}

	public SelectRepositoryPage(ITaskRepositoryFilter taskRepositoryFilter) {
		super(Messages.SelectRepositoryPage_Select_a_repository);

		setTitle(Messages.SelectRepositoryPage_Select_a_repository);
		setDescription(Messages.SelectRepositoryPage_Add_new_repositories_using_the_X_view);

		this.taskRepositoryFilter = taskRepositoryFilter;
		this.repositories = getTaskRepositories();
	}

	public List<TaskRepository> getTaskRepositories() {
		List<TaskRepository> repositories = new ArrayList<TaskRepository>();
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		for (AbstractRepositoryConnector connector : repositoryManager.getRepositoryConnectors()) {
			Set<TaskRepository> connectorRepositories = repositoryManager.getRepositories(connector.getConnectorKind());
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

		Tree tree = createTableViewer(container);
		viewer.setSorter(new TaskRepositoriesViewSorter());

		GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		tree.setLayoutData(gridData);

		Composite buttonContainer = new Composite(container, SWT.NULL);
		GridLayout buttonLayout = new GridLayout(2, false);
		buttonContainer.setLayout(buttonLayout);

		final AddRepositoryAction action = new AddRepositoryAction();
		action.setPromptToAddQuery(false);

		Button button = new Button(buttonContainer, SWT.NONE);
		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
		button.setImage(CommonImages.getImage(TasksUiImages.REPOSITORY_NEW));
		button.setText(AddRepositoryAction.TITLE);
		button.setEnabled(action.isEnabled());
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TaskRepository taskRepository = action.showWizard();
				if (taskRepository != null) {
					SelectRepositoryPage.this.repositories = getTaskRepositories();
					viewer.setInput(TasksUi.getRepositoryManager().getRepositoryConnectors());
					viewer.setSelection(new StructuredSelection(taskRepository));
				}
			}
		});

		final Command discoveryWizardCommand = TasksUiInternal.getConfiguredDiscoveryWizardCommand();
		if (discoveryWizardCommand != null && discoveryWizardCommand.isEnabled()) {
			Button discoveryButton = new Button(buttonContainer, SWT.PUSH);
			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(discoveryButton);
			discoveryButton.setText(Messages.SelectRepositoryConnectorPage_activateDiscovery);
			discoveryButton.setImage(CommonImages.getImage(CommonImages.DISCOVERY));
			discoveryButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
							.getService(IHandlerService.class);
					try {
						handlerService.executeCommand(discoveryWizardCommand.getId(), null);
					} catch (Exception e) {
						IStatus status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								NLS.bind(Messages.SelectRepositoryConnectorPage_discoveryProblemMessage,
										new Object[] { e.getMessage() }),
								e);
						TasksUiInternal.logAndDisplayStatus(
								Messages.SelectRepositoryConnectorPage_discoveryProblemTitle, status);
					}
				}
			});
		}

		Dialog.applyDialogFont(container);
		setControl(container);
	}

	protected Tree createTableViewer(Composite container) {
		viewer = new TreeViewer(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//		contentProvider = new TeamRepositoriesContentProvider();
		viewer.setContentProvider(new RepositoryContentProvider());
//		ViewerFilter[] filters = { new EmptyCategoriesFilter(contentProvider) };
//		viewer.setFilters(filters);
		// viewer.setLabelProvider(new TaskRepositoryLabelProvider());
		viewer.setLabelProvider(new DecoratingLabelProvider(new TaskRepositoryLabelProvider(),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		viewer.setInput(TasksUi.getRepositoryManager().getRepositoryConnectors());

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof TaskRepository) {
					setSelectedNode(new CustomWizardNode((TaskRepository) selection.getFirstElement()));
					setPageComplete(true);
				} else {
					setSelectedNode(null);
					setPageComplete(false);
				}
			}
		});

		TaskRepository selectedRepository = TasksUiUtil.getSelectedRepository(null);
		if (selectedRepository != null) {
			Category category = ((TaskRepositoryManager) TasksUi.getRepositoryManager())
					.getCategory(selectedRepository);
			Object[] path = { category, selectedRepository };
			viewer.setSelection(new TreeSelection(new TreePath(path)));
		} else {
			TaskRepository localRepository = TasksUi.getRepositoryManager()
					.getRepository(LocalRepositoryConnector.CONNECTOR_KIND, LocalRepositoryConnector.REPOSITORY_URL);
			viewer.setSelection(new StructuredSelection(localRepository));
		}

		final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

		new GradientDrawer(themeManager, getViewer()) {
			@Override
			protected boolean shouldApplyGradient(org.eclipse.swt.widgets.Event event) {
				return event.item.getData() instanceof Category;
			}
		};

		viewer.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				if (canFlipToNextPage()) {
					try {
						getContainer().showPage(getNextPage());
					} catch (RuntimeException e) {
						StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
								"Exception while opening the next wizard page", e)); //$NON-NLS-1$
					}
				} else if (canFinish()) {
					if (getWizard().performFinish()) {
						((WizardDialog) getContainer()).close();
					}
				}
			}
		});

		viewer.expandAll();
		viewer.getTree().showSelection();
		viewer.getTree().setFocus();
		return viewer.getTree();
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
				if (wizard != null) {
					wizard.setContainer(getContainer());
				}
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

			return this.repository.getConnectorKind().equals(that.repository.getConnectorKind())
					&& this.repository.getRepositoryUrl().equals(that.repository.getRepositoryUrl());
		}

		@Override
		public int hashCode() {
			return 31 * this.repository.getRepositoryUrl().hashCode() + this.repository.getConnectorKind().hashCode();
		}

	}

	/**
	 * Public for testing.
	 */
	public TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * Public for testing.
	 */
	public List<TaskRepository> getRepositories() {
		return repositories;
	}

}
