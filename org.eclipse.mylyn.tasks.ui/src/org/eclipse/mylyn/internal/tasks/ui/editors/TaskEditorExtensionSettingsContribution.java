/*******************************************************************************
 * Copyright (c) 2004, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.SortedSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorExtensions.RegisteredTaskEditorExtension;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractTaskRepositoryPageContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * A contribution that adds a section for 'Editor' on the task repository settings page.
 * 
 * @author David Green
 */
public class TaskEditorExtensionSettingsContribution extends AbstractTaskRepositoryPageContribution {

	private static final String LABEL_NONE = Messages.TaskEditorExtensionSettingsContribution_Plain_Text;

	private static final String LABEL_DEFAULT_SUFFIX = Messages.TaskEditorExtensionSettingsContribution__default_;

	private static final String DATA_EDITOR_EXTENSION = "editorExtension"; //$NON-NLS-1$

	private final SelectionListener listener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			selectedExtensionId = (String) ((Widget) e.getSource()).getData(DATA_EDITOR_EXTENSION);
			fireValidationRequired();
		}
	};

	private String selectedExtensionId;

	public TaskEditorExtensionSettingsContribution() {
		super(Messages.TaskEditorExtensionSettingsContribution_Editor,
				Messages.TaskEditorExtensionSettingsContribution_Select_the_capabilities_of_the_task_editor);
	}

	@Override
	public void applyTo(TaskRepository repository) {
		TaskEditorExtensions.setTaskEditorExtensionId(repository, selectedExtensionId == null ? "none" //$NON-NLS-1$
				: selectedExtensionId);
	}

	@Override
	public boolean canFlipToNextPage() {
		return true;
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}

	@Override
	public Control createControl(Composite parentControl) {
		Composite parent = new Composite(parentControl, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		parent.setLayout(layout);

		String defaultExtensionId = TaskEditorExtensions.getDefaultTaskEditorExtensionId(getConnectorKind());
		selectedExtensionId = getRepository() == null ? defaultExtensionId
				: TaskEditorExtensions.getTaskEditorExtensionId(getRepository());

		// configure a 'Plain Text' (none) button
		Button noneButton = new Button(parent, SWT.RADIO);
		String noneTitle = LABEL_NONE;
		boolean isDefault = defaultExtensionId == null || defaultExtensionId.length() == 0;
		if (isDefault) {
			noneTitle += LABEL_DEFAULT_SUFFIX;
		}
		noneButton.setText(noneTitle);
		noneButton.addSelectionListener(listener);

		boolean foundSelection = false;

		// now add selection buttons for all registered extensions
		SortedSet<RegisteredTaskEditorExtension> allEditorExtensions = TaskEditorExtensions.getTaskEditorExtensions();
		for (RegisteredTaskEditorExtension editorExtension : allEditorExtensions) {
			String name = editorExtension.getName();
			isDefault = editorExtension.getId().equals(defaultExtensionId);
			if (isDefault) {
				name += LABEL_DEFAULT_SUFFIX;
			}
			Button button = new Button(parent, SWT.RADIO);
			button.setText(name);

			if (editorExtension.getId().equals(selectedExtensionId)) {
				foundSelection = true;
				button.setSelection(true);
			}
			button.setText(name);
			button.setData(DATA_EDITOR_EXTENSION, editorExtension.getId());
			button.addSelectionListener(listener);
		}
		if (!foundSelection) {
			noneButton.setSelection(true);
		}
		return parent;
	}

	@Override
	public IStatus validate() {
		// nothing to validate
		return null;
	}

	/**
	 * only enabled when there are installed/registered task editor extensions.
	 */
	@Override
	public boolean isEnabled() {
		return !TaskEditorExtensions.getTaskEditorExtensions().isEmpty();
	}
}
