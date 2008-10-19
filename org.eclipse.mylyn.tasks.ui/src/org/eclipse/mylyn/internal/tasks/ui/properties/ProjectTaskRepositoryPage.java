/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.internal.dialogs.DialogUtil;

/**
 * A property page that support per project configuration of an associated task repository.
 * 
 * @author Rob Elves
 * @see Adapted from org.eclipse.ui.internal.ide.dialogs.ProjectReferencePage
 */
public class ProjectTaskRepositoryPage extends PropertyPage {

	private static final int REPOSITORY_LIST_MULTIPLIER = 30;

	private IProject project;

	private boolean modified = false;

	private CheckboxTableViewer listViewer;

	public ProjectTaskRepositoryPage() {
	}

	@Override
	protected Control createContents(Composite parent) {

		Font font = parent.getFont();

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setFont(font);

		initialize();

		Label description = createDescriptionLabel(composite);
		description.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		listViewer = CheckboxTableViewer.newCheckList(composite, SWT.TOP | SWT.BORDER);
		listViewer.getTable().setFont(font);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;

		// Only set a height hint if it will not result in a cut off dialog
		if (DialogUtil.inRegularFontMode(parent)) {
			data.heightHint = getDefaultFontHeight(listViewer.getTable(), REPOSITORY_LIST_MULTIPLIER);
		}
		listViewer.getTable().setLayoutData(data);
		listViewer.getTable().setFont(font);

		listViewer.setLabelProvider(new DecoratingLabelProvider(new TaskRepositoryLabelProvider(),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		listViewer.setContentProvider(new IStructuredContentProvider() {
			public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object parent) {
				return TasksUi.getRepositoryManager().getAllRepositories().toArray();
			}

		});

		listViewer.setSorter(new TaskRepositoriesSorter());
		listViewer.setInput(project.getWorkspace());

		listViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					// only allow single selection
					listViewer.setAllChecked(false);
					listViewer.setChecked(event.getElement(), event.getChecked());
				}
				modified = true;
			}
		});
		updateLinkedRepository();

		// TODO this code was copied from SelectRepositoryPage
		final AddRepositoryAction action = new AddRepositoryAction();
		action.setPromptToAddQuery(false);

		Button button = new Button(composite, SWT.NONE);
		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
		button.setText(AddRepositoryAction.TITLE);
		button.setEnabled(action.isEnabled());
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TaskRepository taskRepository = action.showWizard();
				if (taskRepository != null) {
					listViewer.setInput(project.getWorkspace());
					listViewer.setSelection(new StructuredSelection(taskRepository));
					updateLinkedRepository();
				}
			}
		});

		return composite;
	}

	void updateLinkedRepository() {
		TaskRepository repository = TasksUiPlugin.getDefault().getRepositoryForResource(project);
		if (repository != null) {
			listViewer.setCheckedElements(new Object[] { repository });
		}
		listViewer.getControl().setEnabled(TasksUiPlugin.getDefault().canSetRepositoryForResource(project));
	}

	private static int getDefaultFontHeight(Control control, int lines) {
		FontData[] viewerFontData = control.getFont().getFontData();
		int fontHeight = 10;

		// If we have no font data use our guess
		if (viewerFontData.length > 0) {
			fontHeight = viewerFontData[0].getHeight();
		}
		return lines * fontHeight;

	}

	private void initialize() {
		project = (IProject) getElement().getAdapter(IResource.class);
		noDefaultAndApplyButton();
		setDescription("Select a task repository to associate with this project below:");
	}

	@Override
	public boolean performOk() {
		if (!modified) {
			return true;
		}
		if (listViewer.getCheckedElements().length > 0) {
			TaskRepository selectedRepository = (TaskRepository) listViewer.getCheckedElements()[0];
			TasksUiPlugin.getDefault().setRepositoryForResource(project, selectedRepository);
		} else {
			TasksUiPlugin.getDefault().setRepositoryForResource(project, null);
		}
		return true;
	}
}
