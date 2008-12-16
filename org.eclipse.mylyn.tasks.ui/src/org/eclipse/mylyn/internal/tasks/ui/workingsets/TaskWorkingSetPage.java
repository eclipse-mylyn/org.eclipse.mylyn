/*******************************************************************************
 * Copyright (c) 2004, 2008 Eugene Kuleshov and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetPage;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Adapted from org.eclipse.ui.internal.ide.dialogs.ResourceWorkingSetPage
 * 
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public class TaskWorkingSetPage extends WizardPage implements IWorkingSetPage {

	private final static int SIZING_SELECTION_WIDGET_WIDTH = 50;

	private final static int SIZING_SELECTION_WIDGET_HEIGHT = 200;

	private Text text;

	private CheckboxTreeViewer treeViewer;

	private IWorkingSet workingSet;

	private final WorkingSetPageContentProvider workingSetPageContentProvider = new WorkingSetPageContentProvider();

	private boolean firstCheck = false;

	private final class WorkingSetPageContentProvider implements ITreeContentProvider {

		private ElementCategory tasksContainer;

		private ElementCategory resourcesContainer;

		private final Map<IRepositoryQuery, TaskRepository> queryMap = new HashMap<IRepositoryQuery, TaskRepository>();

		private final Map<IProject, TaskRepositoryProjectMapping> projectMap = new HashMap<IProject, TaskRepositoryProjectMapping>();

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof List) {
				List<IAdaptable> taskRepositoriesContainers = new ArrayList<IAdaptable>();
				List<IAdaptable> resourcesRepositoriesContainers = new ArrayList<IAdaptable>();

				for (AbstractTaskContainer category : TasksUiInternal.getTaskList().getCategories()) {
					taskRepositoriesContainers.add(category);
				}

				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				Set<IProject> unmappedProjects = new HashSet<IProject>();
				for (Object container : (List<?>) parentElement) {
					if (container instanceof TaskRepository) {
						// NOTE: looking down, high complexity
						if (hasChildren(container)) {
							taskRepositoriesContainers.add((TaskRepository) container);
						}

						// NOTE: O(n^2) complexity, could fix
						Set<IProject> mappedProjects = new HashSet<IProject>();

						for (IProject project : projects) {
							TaskRepository taskRepository = TasksUiPlugin.getDefault()
									.getRepositoryForResource(project);
							if (container.equals(taskRepository)) {
								mappedProjects.add(project);
							} else if (taskRepository == null) {
								unmappedProjects.add(project);
							}
						}
						if (!mappedProjects.isEmpty()) {
							TaskRepositoryProjectMapping projectMapping = new TaskRepositoryProjectMapping(
									(TaskRepository) container, mappedProjects);
							resourcesRepositoriesContainers.add(projectMapping);
							for (IProject mappedProject : mappedProjects) {
								projectMap.put(mappedProject, projectMapping);
							}
						}
					}
				}
				resourcesRepositoriesContainers.addAll(unmappedProjects);
				tasksContainer = new ElementCategory(Messages.TaskWorkingSetPage_Tasks, taskRepositoriesContainers);
				resourcesContainer = new ElementCategory(Messages.TaskWorkingSetPage_Resources,
						resourcesRepositoriesContainers);
				return new Object[] { tasksContainer, resourcesContainer };
			} else if (parentElement instanceof TaskRepository) {
				List<IAdaptable> taskContainers = new ArrayList<IAdaptable>();
				for (AbstractTaskContainer element : TasksUiPlugin.getTaskList().getRepositoryQueries(
						((TaskRepository) parentElement).getRepositoryUrl())) {
					if (element instanceof IRepositoryQuery) {
						taskContainers.add(element);
						queryMap.put((IRepositoryQuery) element, (TaskRepository) parentElement);
					}
				}

				return taskContainers.toArray();
			} else if (parentElement instanceof TaskRepositoryProjectMapping) {
				return ((TaskRepositoryProjectMapping) parentElement).getProjects().toArray();
			} else if (parentElement instanceof ElementCategory) {
				return ((ElementCategory) parentElement).getChildren(parentElement);
			} else {
				return new Object[0];
			}
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		public Object[] getElements(Object element) {
			return getChildren(element);
		}

		public Object getParent(Object element) {
			if (element instanceof AbstractTaskCategory || element instanceof TaskRepository) {
				return tasksContainer;
			} else if (element instanceof IRepositoryQuery) {
				return queryMap.get(element);
			} else if (element instanceof TaskRepositoryProjectMapping) {
				return resourcesContainer;
			} else if (element instanceof IProject) {
				Object repository = projectMap.get(element);
				if (repository != null) {
					return repository;
				} else {
					return resourcesContainer;
				}
			} else {
				return null;
			}
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class TaskRepositoryProjectMapping extends PlatformObject {

		private final TaskRepository taskRepository;

		private final Set<IProject> projects;

		public TaskRepositoryProjectMapping(TaskRepository taskRepository, Set<IProject> mappedProjects) {
			this.taskRepository = taskRepository;
			this.projects = mappedProjects;
		}

		public Set<IProject> getProjects() {
			return projects;
		}

		public TaskRepository getTaskRepository() {
			return taskRepository;
		}
	}

	class ElementCategory extends PlatformObject implements IWorkbenchAdapter {

		private final String label;

		private final List<IAdaptable> children;

		public ElementCategory(String label, List<IAdaptable> children) {
			this.label = label;
			this.children = children;
		}

		public Object[] getChildren(Object o) {
			return children.toArray();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_OBJ_WORKING_SETS);
		}

		public String getLabel(Object o) {
			return label;
		}

		public Object getParent(Object o) {
			return null;
		}

	}

	class AggregateLabelProvider implements ILabelProvider {

		private final TaskElementLabelProvider taskLabelProvider = new TaskElementLabelProvider(false);

		private final TaskRepositoryLabelProvider taskRepositoryLabelProvider = new TaskRepositoryLabelProvider();

		private final WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();

		public Image getImage(Object element) {
			if (element instanceof AbstractTaskContainer) {
				return taskLabelProvider.getImage(element);
			} else if (element instanceof TaskRepository) {
				return taskRepositoryLabelProvider.getImage(element);
			} else if (element instanceof TaskRepositoryProjectMapping) {
				return getImage(((TaskRepositoryProjectMapping) element).getTaskRepository());
			} else {
				return workbenchLabelProvider.getImage(element);
			}
		}

		public String getText(Object element) {
			if (element instanceof AbstractTaskContainer) {
				return taskLabelProvider.getText(element);
			} else if (element instanceof TaskRepository) {
				return taskRepositoryLabelProvider.getText(element);
			} else if (element instanceof TaskRepositoryProjectMapping) {
				return getText(((TaskRepositoryProjectMapping) element).getTaskRepository());
			} else {
				return workbenchLabelProvider.getText(element);
			}
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}

	class CustomSorter extends ViewerSorter {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 instanceof TaskRepository || e1 instanceof TaskRepositoryProjectMapping) {
				return -1;
			} else if (e2 instanceof TaskRepository || e2 instanceof TaskRepositoryProjectMapping) {
				return 1;
			} else if (e1 instanceof ElementCategory
					&& ((ElementCategory) e1).getLabel(e1).equals(Messages.TaskWorkingSetPage_Tasks)) {
				return -1;
			} else if (e2 instanceof ElementCategory
					&& ((ElementCategory) e1).getLabel(e1).equals(Messages.TaskWorkingSetPage_Tasks)) {
				return 1;
			} else {
				return super.compare(viewer, e1, e2);
			}
		}
	}

	public TaskWorkingSetPage() {
		super("taskWorkingSetPage", Messages.TaskWorkingSetPage_Select_Working_Set_Elements, null); //$NON-NLS-1$
		setDescription(Messages.TaskWorkingSetPage_Page_Description);
		setImageDescriptor(TasksUiImages.BANNER_WORKING_SET);
	}

	public void finish() {
		Object[] elements = treeViewer.getCheckedElements();
		Set<IAdaptable> validElements = new HashSet<IAdaptable>();
		for (Object element : elements) {
			if (element instanceof AbstractTaskContainer || element instanceof IProject) {
				validElements.add((IAdaptable) element);
			}
		}

		addSpecialContainers(validElements);

		if (workingSet == null) {
			IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
			workingSet = workingSetManager.createWorkingSet(getWorkingSetName(),
					validElements.toArray(new IAdaptable[validElements.size()]));
		} else {
			workingSet.setName(getWorkingSetName());
			workingSet.setElements(validElements.toArray(new IAdaptable[validElements.size()]));
		}
	}

	private void addSpecialContainers(Set<IAdaptable> validElements) {
		HashSet<AbstractTaskContainer> specialContainers = new HashSet<AbstractTaskContainer>();
		for (IAdaptable element : validElements) {
			if (element instanceof IRepositoryQuery) {
				IRepositoryQuery query = (IRepositoryQuery) element;
				if (query.getRepositoryUrl() != null) {
					// Add Unmatched
					AbstractTaskContainer orphansContainer = TasksUiPlugin.getTaskList().getUnmatchedContainer(
							query.getRepositoryUrl());
					if (orphansContainer != null) {
						specialContainers.add(orphansContainer);
					}

					// Add Unsubmitted
					AbstractTaskContainer unsubmittedContainer = TasksUiPlugin.getTaskList().getUnsubmittedContainer(
							query.getRepositoryUrl());
					if (unsubmittedContainer != null) {
						specialContainers.add(unsubmittedContainer);
					}
				}
			}
		}
		validElements.addAll(specialContainers);
	}

	public IWorkingSet getSelection() {
		return workingSet;
	}

	public void setSelection(IWorkingSet workingSet) {
		this.workingSet = workingSet;
		if (getShell() != null && text != null) {
			firstCheck = true;
			initializeCheckedState();
			text.setText(workingSet.getName());
		}
	}

	private String getWorkingSetName() {
		return text.getText();
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);

		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IIDEHelpContextIds.WORKING_SET_RESOURCE_PAGE);
		Label label = new Label(composite, SWT.WRAP);
		label.setText(""); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER));

		text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		});
		text.setFocus();
		// text.setBackground(FieldAssistColors.getRequiredFieldBackgroundColor(text));

		label = new Label(composite, SWT.WRAP);
		label.setText(""); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER));

		treeViewer = new CheckboxTreeViewer(composite);
		treeViewer.setUseHashlookup(true);
		treeViewer.setContentProvider(workingSetPageContentProvider);

		treeViewer.setLabelProvider(new DecoratingLabelProvider(new AggregateLabelProvider(), PlatformUI.getWorkbench()
				.getDecoratorManager()
				.getLabelDecorator()));
		treeViewer.setSorter(new CustomSorter());

		ArrayList<Object> containers = new ArrayList<Object>();
		for (TaskRepository repository : TasksUi.getRepositoryManager().getAllRepositories()) {
			containers.add(repository);
		}

		containers.addAll(Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects()));

		treeViewer.setInput(containers);

		// tree.setComparator(new ResourceComparator(ResourceComparator.NAME));

		GridData data = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
		data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
		data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
		treeViewer.getControl().setLayoutData(data);

		treeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				handleCheckStateChange(event);
			}
		});

		// Add select / deselect all buttons for bug 46669
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		Button selectAllButton = new Button(buttonComposite, SWT.PUSH);
		selectAllButton.setText(Messages.TaskWorkingSetPage_Select_All);
		selectAllButton.setToolTipText(""); //$NON-NLS-1$
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				treeViewer.setCheckedElements(workingSetPageContentProvider.getElements(treeViewer.getInput()));
				validateInput();
			}
		});
		setButtonLayoutData(selectAllButton);

		Button deselectAllButton = new Button(buttonComposite, SWT.PUSH);
		deselectAllButton.setText(Messages.TaskWorkingSetPage_Deselect_All);
		deselectAllButton.setToolTipText(""); //$NON-NLS-1$
		deselectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				treeViewer.setCheckedElements(new Object[0]);
				validateInput();
			}
		});
		setButtonLayoutData(deselectAllButton);

		if (workingSet != null) {
			for (Object object : workingSet.getElements()) {
				treeViewer.expandToLevel(object, 1);
			}
		} else {
			treeViewer.expandToLevel(2);
		}
		initializeCheckedState();

		if (workingSet != null) {
			text.setText(workingSet.getName());
		}

		setPageComplete(false);
		Dialog.applyDialogFont(composite);
	}

	private void initializeCheckedState() {
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				Object[] items = null;
				if (workingSet != null) {
					items = workingSet.getElements();
					if (items != null) {
						// see bug 191342
						treeViewer.setCheckedElements(new Object[] {});
						for (Object item : items) {
							if (item != null) {
								treeViewer.setChecked(item, true);
							}
						}
					}
				}
			}
		});
	}

	protected void handleCheckStateChange(final CheckStateChangedEvent event) {
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				IAdaptable element = (IAdaptable) event.getElement();
				handleCheckStateChangeHelper(event, element);
				validateInput();
			}

			private void handleCheckStateChangeHelper(final CheckStateChangedEvent event, IAdaptable element) {
				if (element instanceof AbstractTaskContainer || element instanceof IProject) {
					treeViewer.setGrayed(element, false);
				} else if (element instanceof ElementCategory) {
					for (Object child : ((ElementCategory) element).getChildren(null)) {
						treeViewer.setChecked(child, event.getChecked());
						if (child instanceof IAdaptable) {
							handleCheckStateChangeHelper(event, (IAdaptable) child);
						}
					}
				} else if (element instanceof TaskRepository || element instanceof TaskRepositoryProjectMapping) {
					for (Object child : workingSetPageContentProvider.getChildren(element)) {
						treeViewer.setChecked(child, event.getChecked());
					}
				}
			}
		});
	}

	protected void validateInput() {
		String errorMessage = null;
		String infoMessage = null;
		String newText = text.getText();

		if (!newText.equals(newText.trim())) {
			errorMessage = Messages.TaskWorkingSetPage_The_name_must_not_have_a_leading_or_trailing_whitespace;
		} else if (firstCheck) {
			firstCheck = false;
			return;
		}
		if ("".equals(newText)) { //$NON-NLS-1$
			errorMessage = Messages.TaskWorkingSetPage_The_name_must_not_be_empty;
		}
		if (errorMessage == null && (workingSet == null || !newText.equals(workingSet.getName()))) {
			IWorkingSet[] workingSets = PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets();
			for (IWorkingSet workingSet2 : workingSets) {
				if (newText.equals(workingSet2.getName())) {
					errorMessage = Messages.TaskWorkingSetPage_A_working_set_with_the_same_name_already_exists;
				}
			}
		}
		if (treeViewer.getCheckedElements().length == 0) {
			infoMessage = Messages.TaskWorkingSetPage_No_categories_queries_selected;
		}
		setMessage(infoMessage, INFORMATION);
		setErrorMessage(errorMessage);
		setPageComplete(errorMessage == null);
	}

}
