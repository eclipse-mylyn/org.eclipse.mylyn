/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import org.eclipse.mylyn.internal.tasks.core.OrphanedTasksContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskArchive;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
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
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Adapted from org.eclipse.ui.internal.ide.dialogs.ResourceWorkingSetPage
 * 
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public class TaskWorkingSetPage extends WizardPage implements IWorkingSetPage {

	private static final String LABEL_TASKS = "Tasks";

	private final static int SIZING_SELECTION_WIDGET_WIDTH = 50;

	private final static int SIZING_SELECTION_WIDGET_HEIGHT = 200;

	private Text text;

	private CheckboxTreeViewer treeViewer;

	private IWorkingSet workingSet;

	private WorkingSetPageContentProvider workingSetPageContentProvider = new WorkingSetPageContentProvider();

	private boolean firstCheck = false;

	private final class WorkingSetPageContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof List) {
				List<IAdaptable> taskRepositoriesContainers = new ArrayList<IAdaptable>();
				List<IAdaptable> resourcesRepositoriesContainers = new ArrayList<IAdaptable>();

				for (AbstractTaskContainer category : TasksUiPlugin.getTaskListManager().getTaskList().getCategories()) {
					if (!(category instanceof TaskArchive)) {
						taskRepositoriesContainers.add(category);
					}
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
							TaskRepository taskRepository = TasksUiPlugin.getDefault().getRepositoryForResource(
									project, true);
							if (container.equals(taskRepository)) {
								mappedProjects.add(project);
							} else if (taskRepository == null) {
								unmappedProjects.add(project);
							}
						}
						if (!mappedProjects.isEmpty()) {
							resourcesRepositoriesContainers.add(new TaskRepositoryProjectMapping(
									(TaskRepository) container, mappedProjects));
						}
					}
				}
				resourcesRepositoriesContainers.addAll(unmappedProjects);
				return new Object[] { new ElementCategory(LABEL_TASKS, taskRepositoriesContainers),
						new ElementCategory("Resources", resourcesRepositoriesContainers) };
			} else if (parentElement instanceof TaskRepository) {
				List<IAdaptable> taskContainers = new ArrayList<IAdaptable>();
				for (AbstractTaskContainer element : TasksUiPlugin.getTaskListManager()
						.getTaskList()
						.getRepositoryQueries(((TaskRepository) parentElement).getUrl())) {
					if (element instanceof AbstractRepositoryQuery) {
						taskContainers.add(element);
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
			return null;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class TaskRepositoryProjectMapping extends PlatformObject {

		private TaskRepository taskRepository;

		private Set<IProject> projects;

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

		private String label;

		private List<IAdaptable> children;

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

		private TaskElementLabelProvider taskLabelProvider = new TaskElementLabelProvider(false);

		private TaskRepositoryLabelProvider taskRepositoryLabelProvider = new TaskRepositoryLabelProvider();

		private WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();

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
			} else if (e1 instanceof ElementCategory && ((ElementCategory) e1).getLabel(e1).equals(LABEL_TASKS)) {
				return -1;
			} else if (e2 instanceof ElementCategory && ((ElementCategory) e1).getLabel(e1).equals(LABEL_TASKS)) {
				return 1;
			} else {
				return super.compare(viewer, e1, e2);
			}
		}
	}

	public TaskWorkingSetPage() {
		super("taskWorkingSetPage", "Select Working Set Elements", null);
		setDescription("" + "When this Working Set is selected views will be filtered just to show only\n"
				+ "these elements if the Window Working Set is enabled in the view (default).");
		setImageDescriptor(TasksUiImages.BANNER_WORKING_SET);
	}

	public void finish() {
		Object[] elements = treeViewer.getCheckedElements();
		Set<IAdaptable> validElements = new HashSet<IAdaptable>();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof AbstractTaskContainer || elements[i] instanceof IProject) {
				validElements.add((IAdaptable) elements[i]);
			}
		}

		addUnmatchedCategories(validElements);

		if (workingSet == null) {
			IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
			workingSet = workingSetManager.createWorkingSet(getWorkingSetName(),
					validElements.toArray(new IAdaptable[validElements.size()]));
		} else {
			workingSet.setName(getWorkingSetName());
			workingSet.setElements(validElements.toArray(new IAdaptable[validElements.size()]));
		}
	}

	private void addUnmatchedCategories(Set<IAdaptable> validElements) {
		HashSet<OrphanedTasksContainer> orphanContainers = new HashSet<OrphanedTasksContainer>();
		for (IAdaptable element : validElements) {
			if (element instanceof AbstractRepositoryQuery) {
				AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
				if (query.getRepositoryUrl() != null) {
					OrphanedTasksContainer orphansContainer = TasksUiPlugin.getTaskListManager()
							.getTaskList()
							.getOrphanContainer(query.getRepositoryUrl());
					if (orphansContainer != null) {
						orphanContainers.add(orphansContainer);
					}
				}
			}
		}
		validElements.addAll(orphanContainers);
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
		label.setText(IDEWorkbenchMessages.ResourceWorkingSetPage_message);
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
		label.setText(IDEWorkbenchMessages.ResourceWorkingSetPage_label_tree);
		label.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER));

		treeViewer = new CheckboxTreeViewer(composite);
		treeViewer.setUseHashlookup(true);
		treeViewer.setContentProvider(workingSetPageContentProvider);

		treeViewer.setLabelProvider(new DecoratingLabelProvider(new AggregateLabelProvider(), PlatformUI.getWorkbench()
				.getDecoratorManager()
				.getLabelDecorator()));

//        tree.setLabelProvider(new TaskElementLabelProvider());
		treeViewer.setSorter(new CustomSorter());

		ArrayList<Object> containers = new ArrayList<Object>();
		for (TaskRepository repository : TasksUiPlugin.getRepositoryManager().getAllRepositories()) {
			containers.add(repository);
		}

//		for (AbstractTaskContainer element : TasksUiPlugin.getTaskListManager().getTaskList().getRootElements()) {
//			if (!(element instanceof TaskArchive)) {
//				containers.add(element);
//			}
//		}
		containers.addAll(Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects()));

		treeViewer.setInput(containers);
		treeViewer.expandAll();

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

//        tree.addTreeListener(new ITreeViewerListener() {
//            public void treeCollapsed(TreeExpansionEvent event) {
//            }
//
//            public void treeExpanded(TreeExpansionEvent event) {
//                final Object element = event.getElement();
//                if (tree.getGrayed(element) == false) {
//					BusyIndicator.showWhile(getShell().getDisplay(),
//                            new Runnable() {
//                                public void run() {
//                                    setSubtreeChecked((IContainer) element,
//                                            tree.getChecked(element), false);
//                                }
//                            });
//				}
//            }
//        });

		// Add select / deselect all buttons for bug 46669
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		Button selectAllButton = new Button(buttonComposite, SWT.PUSH);
		selectAllButton.setText(IDEWorkbenchMessages.ResourceWorkingSetPage_selectAll_label);
		selectAllButton.setToolTipText(IDEWorkbenchMessages.ResourceWorkingSetPage_selectAll_toolTip);
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				treeViewer.setCheckedElements(workingSetPageContentProvider.getElements(treeViewer.getInput()));
				validateInput();
			}
		});
		setButtonLayoutData(selectAllButton);

		Button deselectAllButton = new Button(buttonComposite, SWT.PUSH);
		deselectAllButton.setText(IDEWorkbenchMessages.ResourceWorkingSetPage_deselectAll_label);
		deselectAllButton.setToolTipText(IDEWorkbenchMessages.ResourceWorkingSetPage_deselectAll_toolTip);
		deselectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				treeViewer.setCheckedElements(new Object[0]);
				validateInput();
			}
		});
		setButtonLayoutData(deselectAllButton);

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
				if (element instanceof AbstractTaskContainer || element instanceof IProject) {
					treeViewer.setGrayed(element, false);
				} else if (element instanceof ElementCategory) {
					for (Object child : ((ElementCategory) element).getChildren(null)) {
						treeViewer.setChecked(child, event.getChecked());
					}
				} else if (element instanceof TaskRepository || element instanceof TaskRepositoryProjectMapping) {
					for (Object child : workingSetPageContentProvider.getChildren(element)) {
						treeViewer.setChecked(child, event.getChecked());
					}
				}

				validateInput();
			}
		});
	}

	protected void validateInput() {
		String errorMessage = null;
		String infoMessage = null;
		String newText = text.getText();

		if (!newText.equals(newText.trim())) {
			errorMessage = "The name must not have a leading or trailing whitespace.";
		} else if (firstCheck) {
			firstCheck = false;
			return;
		}
		if ("".equals(newText)) { //$NON-NLS-1$
			errorMessage = "The name must not be empty.";
		}
		if (errorMessage == null && (workingSet == null || !newText.equals(workingSet.getName()))) {
			IWorkingSet[] workingSets = PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets();
			for (int i = 0; i < workingSets.length; i++) {
				if (newText.equals(workingSets[i].getName())) {
					errorMessage = "A working set with the same name already exists.";
				}
			}
		}
		if (treeViewer.getCheckedElements().length == 0) {
			infoMessage = "No categories/queries selected.";
		}
		setMessage(infoMessage, INFORMATION);
		setErrorMessage(errorMessage);
		setPageComplete(errorMessage == null);
	}

}
