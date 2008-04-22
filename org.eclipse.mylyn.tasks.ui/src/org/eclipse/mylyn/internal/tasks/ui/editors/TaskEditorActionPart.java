/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.data.AbstractAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class TaskEditorActionPart extends AbstractTaskEditorPart {

	private class RadioButtonListener extends SelectionAdapter {

		public void modifyText(ModifyEvent e) {
			for (Button button : operationButtons) {
				if (button != e.widget) {
					button.setSelection(false);
				}
			}

			TaskAttribute operation = (TaskAttribute) e.widget.getData();
			getTaskData().getAttributeMapper().setTaskOperation(getTaskData(), operation);
		}

	}

	private class FocusListener extends FocusAdapter {

		private final Button button;

		public FocusListener(Button button) {
			this.button = button;
		}

		@Override
		public void focusGained(FocusEvent e) {
			this.button.setSelection(true);
			for (Button button : operationButtons) {
				if (button != this.button) {
					button.setSelection(false);
				}
			}
		}

	}

	private static final int DEFAULT_FIELD_WIDTH = 150;

	private static final String LABEL_BUTTON_SUBMIT = "Submit";

	private static final int RADIO_OPTION_WIDTH = 120;

	private List<Button> operationButtons;

	private Button submitButton;

	private Button attachContextButton;

	private boolean needsAttachContext = true;

	private boolean needsAddToCategory;

	private Button addToCategory;

	private CCombo categoryChooser;

	private AbstractTaskCategory category;

	public TaskEditorActionPart() {
		setPartName("Actions");
	}

	protected void addAttachContextButton(Composite buttonComposite, FormToolkit toolkit) {
		attachContextButton = toolkit.createButton(buttonComposite, "Attach Context", SWT.CHECK);
		attachContextButton.setImage(TasksUiImages.getImage(TasksUiImages.CONTEXT_ATTACH));
	}

	/**
	 * Adds buttons to this composite. Subclasses can override this method to provide different/additional buttons.
	 * 
	 * @param buttonComposite
	 *            Composite to add the buttons to.
	 * @param toolkit
	 */
	private void createActionButtons(Composite buttonComposite, FormToolkit toolkit) {
		submitButton = toolkit.createButton(buttonComposite, LABEL_BUTTON_SUBMIT, SWT.NONE);
		GridData submitButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		submitButtonData.widthHint = 100;
		submitButton.setImage(TasksUiImages.getImage(TasksUiImages.REPOSITORY_SUBMIT));
		submitButton.setLayoutData(submitButtonData);
		submitButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				getTaskEditorPage().submitToRepository();
			}
		});

		setSubmitEnabled(true);

		toolkit.createLabel(buttonComposite, "    ");

		if (needsAttachContext) {
			addAttachContextButton(buttonComposite, toolkit);
		}
	}

	/**
	 * Creates the button layout. This displays options and buttons at the bottom of the editor to allow actions to be
	 * performed on the bug.
	 * 
	 * @param toolkit
	 */
	private void createCategoryChooser(Composite buttonComposite, FormToolkit toolkit) {
		addToCategory = getManagedForm().getToolkit().createButton(buttonComposite, "Add to Category", SWT.CHECK);
		categoryChooser = new CCombo(buttonComposite, SWT.FLAT | SWT.READ_ONLY);
		categoryChooser.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		categoryChooser.setLayoutData(GridDataFactory.swtDefaults().hint(150, SWT.DEFAULT).create());
		toolkit.adapt(categoryChooser, true, true);
		categoryChooser.setFont(TEXT_FONT);
		ITaskList taskList = TasksUi.getTaskListManager().getTaskList();
		List<AbstractTaskCategory> categories = new ArrayList<AbstractTaskCategory>(taskList.getCategories());
		Collections.sort(categories, new Comparator<AbstractTaskContainer>() {

			public int compare(AbstractTaskContainer c1, AbstractTaskContainer c2) {
				if (c1.equals(TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory())) {
					return -1;
				} else if (c2.equals(TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory())) {
					return 1;
				} else {
					return c1.getSummary().compareToIgnoreCase(c2.getSummary());
				}
			}

		});

		for (AbstractTaskContainer category : categories) {
			categoryChooser.add(category.getSummary());
		}

		categoryChooser.select(0);
		categoryChooser.setEnabled(false);
		categoryChooser.setData(categories);
		addToCategory.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				categoryChooser.setEnabled(addToCategory.getSelection());
			}

		});

		GridDataFactory.fillDefaults().hint(DEFAULT_FIELD_WIDTH, SWT.DEFAULT).span(3, SWT.DEFAULT).applyTo(
				categoryChooser);
	}

	@Override
	public void commit(boolean onSave) {
		if (needsAddToCategory()) {
			category = getCategoryInternal();
		}

		super.commit(onSave);
	}

	public AbstractTaskCategory getCategory() {
		return category;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);

		Composite buttonComposite = toolkit.createComposite(section);
		GridLayout buttonLayout = new GridLayout();
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(buttonComposite);
		buttonLayout.numColumns = 4;
		buttonComposite.setLayout(buttonLayout);

		if (needsAddToCategory) {
			createCategoryChooser(buttonComposite, toolkit);
		}

		createRadioButtons(buttonComposite, toolkit);
		createActionButtons(buttonComposite, toolkit);

		section.setClient(buttonComposite);
		setSection(toolkit, section);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute, Button button) {
		AbstractAttributeMapper attributeMapper = getTaskData().getAttributeMapper();
		AttributeEditorFactory attributeEditorFactory = getTaskEditorPage().getAttributeEditorFactory();

		String type = attributeMapper.getType(attribute);
		if (type != null) {
			AbstractAttributeEditor editor = attributeEditorFactory.createEditor(type, attribute);
			editor.createControl(composite, toolkit);
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.horizontalSpan = 3;
			gd.widthHint = RADIO_OPTION_WIDTH;
			editor.getControl().setLayoutData(gd);
			editor.getControl().addFocusListener(new FocusListener(button));
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
		}
	}

	private void createRadioButtons(Composite buttonComposite, FormToolkit toolkit) {
		List<TaskAttribute> attributes = getTaskData().getAttributeMapper().getAttributesByType(getTaskData(),
				TaskAttribute.TYPE_OPERATION);
		if (attributes != null) {
			operationButtons = new ArrayList<Button>();
			for (TaskAttribute attribute : attributes) {
				TaskOperation operation = getTaskData().getAttributeMapper().getTaskOperation(attribute);
				if (operation != null) {
					Button button = toolkit.createButton(buttonComposite, operation.getLabel(), SWT.RADIO);
					button.setData(attribute);
					GridData radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
					TaskAttribute associatedAttribute = getTaskData().getAttributeMapper().getAssoctiatedAttribute(
							attribute);
					if (associatedAttribute != null) {
						radioData.horizontalSpan = 1;
						addAttribute(buttonComposite, toolkit, associatedAttribute, button);
					} else {
						radioData.horizontalSpan = 4;
					}
					button.setLayoutData(radioData);
					button.addSelectionListener(new RadioButtonListener());
					operationButtons.add(button);
				}
			}
		}
		toolkit.paintBordersFor(buttonComposite);
	}

	boolean getAttachContext() {
		if (attachContextButton == null || attachContextButton.isDisposed()) {
			return false;
		} else {
			return attachContextButton.getSelection();
		}
	}

	/**
	 * Returns the {@link AbstractTaskContainer category} the new task belongs to
	 * 
	 * @return {@link AbstractTaskContainer category} where the new task must be added to, or null if it must not be
	 *         added to the task list
	 */
	@SuppressWarnings("unchecked")
	private AbstractTaskCategory getCategoryInternal() {
		int index = categoryChooser.getSelectionIndex();
		if (addToCategory.getSelection() && index != -1) {
			return ((List<AbstractTaskCategory>) categoryChooser.getData()).get(index);
		}
		return null;
	}

	boolean needsAddToCategory() {
		return needsAddToCategory;
	}

	boolean needsAttachContext() {
		return needsAttachContext;
	}

	public void setNeedsAddToCategory(boolean needsAddToCategory) {
		this.needsAddToCategory = needsAddToCategory;
	}

	void setNeedsAttachContext(boolean attachContextEnabled) {
		this.needsAttachContext = attachContextEnabled;
	}

	void setSubmitEnabled(boolean enabled) {
		if (submitButton != null && !submitButton.isDisposed()) {
			submitButton.setEnabled(enabled);
			if (enabled) {
				submitButton.setToolTipText("Submit to " + getTaskEditorPage().getTaskRepository().getRepositoryUrl());
			}
		}
	}

}
