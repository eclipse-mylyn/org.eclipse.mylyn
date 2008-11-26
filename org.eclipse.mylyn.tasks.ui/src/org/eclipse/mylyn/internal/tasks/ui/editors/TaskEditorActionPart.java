/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fix for bug 254806
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public class TaskEditorActionPart extends AbstractTaskEditorPart {

	private static final String KEY_OPERATION = "operation";

	public class SelectButtonListener implements ModifyListener, VerifyListener, SelectionListener, FocusListener,
			TextChangeListener {

		private final Button button;

		public SelectButtonListener(Button button) {
			this.button = button;
		}

		public void modifyText(ModifyEvent e) {
			selected();
		}

		public void verifyText(VerifyEvent e) {
			selected();
		}

		public void widgetSelected(SelectionEvent e) {
			selected();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			selected();
		}

		public void focusGained(FocusEvent event) {
			selected();
		}

		public void focusLost(FocusEvent e) {
		}

		public void textChanged(TextChangedEvent event) {
			selected();
		}

		public void textSet(TextChangedEvent event) {
			selected();
		}

		public void textChanging(TextChangingEvent event) {
		}

		private void selected() {
			setSelectedRadionButton(button, true);
		}

	}

	private static final int DEFAULT_FIELD_WIDTH = 150;

	private static final String LABEL_BUTTON_SUBMIT = "Submit";

	private static final int RADIO_OPTION_WIDTH = 120;

	private static final String KEY_ASSOCIATED_EDITOR = "associatedEditor";

	private List<Button> operationButtons;

	private Button submitButton;

	private Button attachContextButton;

//	private boolean needsAttachContext = true;

	private Button addToCategory;

	private CCombo categoryChooser;

	private AbstractTaskCategory category;

	private TaskAttribute selectedOperationAttribute;

	public TaskEditorActionPart() {
		setPartName("Actions");
	}

	protected void addAttachContextButton(Composite buttonComposite, FormToolkit toolkit) {
		attachContextButton = toolkit.createButton(buttonComposite, "Attach Context", SWT.CHECK);
		attachContextButton.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_ATTACH));
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
		submitButton.setImage(CommonImages.getImage(TasksUiImages.REPOSITORY_SUBMIT));
		submitButton.setLayoutData(submitButtonData);
		submitButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				getTaskEditorPage().doSubmit();
			}
		});

		setSubmitEnabled(true);

		toolkit.createLabel(buttonComposite, "    ");

		if (!getTaskData().isNew()) {
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
		toolkit.adapt(categoryChooser, false, false);
		categoryChooser.setFont(TEXT_FONT);
		ITaskList taskList = TasksUiInternal.getTaskList();
		final List<AbstractTaskCategory> categories = new ArrayList<AbstractTaskCategory>(taskList.getCategories());
		Collections.sort(categories, new Comparator<AbstractTaskContainer>() {

			public int compare(AbstractTaskContainer c1, AbstractTaskContainer c2) {
				if (c1.equals(TasksUiPlugin.getTaskList().getDefaultCategory())) {
					return -1;
				} else if (c2.equals(TasksUiPlugin.getTaskList().getDefaultCategory())) {
					return 1;
				} else {
					return c1.getSummary().compareToIgnoreCase(c2.getSummary());
				}
			}

		});
		for (IRepositoryElement category : categories) {
			categoryChooser.add(category.getSummary());
		}
		categoryChooser.select(0);
		categoryChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (categoryChooser.getSelectionIndex() != -1) {
					category = categories.get(categoryChooser.getSelectionIndex());
				}
			}
		});
		categoryChooser.setEnabled(false);

		addToCategory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				markDirty();
				if (!addToCategory.getSelection()) {
					category = null;
				} else if (categoryChooser.getSelectionIndex() != -1) {
					category = categories.get(categoryChooser.getSelectionIndex());
				}
				categoryChooser.setEnabled(addToCategory.getSelection());
			}
		});

		GridDataFactory.fillDefaults().hint(DEFAULT_FIELD_WIDTH, SWT.DEFAULT).span(3, SWT.DEFAULT).applyTo(
				categoryChooser);
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

		if (getTaskEditorPage().needsAddToCategory()) {
			createCategoryChooser(buttonComposite, toolkit);
		}

		selectedOperationAttribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		if (selectedOperationAttribute != null
				&& TaskAttribute.TYPE_OPERATION.equals(selectedOperationAttribute.getMetaData().getType())) {
			TaskOperation selectedOperation = getTaskData().getAttributeMapper().getTaskOperation(
					selectedOperationAttribute);
			createRadioButtons(buttonComposite, toolkit, selectedOperation);
		}

		createActionButtons(buttonComposite, toolkit);

		toolkit.paintBordersFor(buttonComposite);
		section.setClient(buttonComposite);
		setSection(toolkit, section);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute, Button button) {
		AbstractAttributeEditor editor = createAttributeEditor(attribute);
		if (editor != null) {
			editor.createControl(composite, toolkit);
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.horizontalSpan = 3;
			Control editorControl = editor.getControl();
			if (editorControl instanceof CCombo) {
				// XXX combo boxes are too tall by default and wider than other controls
				gd.heightHint = 20;
				gd.widthHint = RADIO_OPTION_WIDTH;
			} else {
				gd.widthHint = RADIO_OPTION_WIDTH - 5;
			}
			editorControl.setLayoutData(gd);

			// the following listeners are hooked up so that changes to something in the actions area
			// will cause the corresponding radio button to become selected.  Note that we can't just use
			// a focus listener due to bug 254806
			if (editorControl instanceof CCombo) {
				((CCombo) editorControl).addSelectionListener(new SelectButtonListener(button));
			} else if (editorControl instanceof Text) {
				((Text) editorControl).addModifyListener(new SelectButtonListener(button));
				((Text) editorControl).addVerifyListener(new SelectButtonListener(button));
			} else if (editorControl instanceof StyledText) {
				((StyledText) editorControl).getContent().addTextChangeListener(new SelectButtonListener(button));
			} else {
				// last resort
				editorControl.addFocusListener(new SelectButtonListener(button));
			}

			button.setData(KEY_ASSOCIATED_EDITOR, editor);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
		}
	}

	private void createRadioButtons(Composite buttonComposite, FormToolkit toolkit, TaskOperation selectedOperation) {
		List<TaskOperation> operations = getTaskData().getAttributeMapper().getTaskOperations(
				selectedOperationAttribute);
		if (operations.size() > 0) {
			operationButtons = new ArrayList<Button>();
			Button selectedButton = null;
			for (TaskOperation operation : operations) {
				Button button = toolkit.createButton(buttonComposite, operation.getLabel(), SWT.RADIO);
				button.setFont(TEXT_FONT);
				button.setData(KEY_OPERATION, operation);
				GridData radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				TaskAttribute associatedAttribute = getTaskData().getAttributeMapper().getAssoctiatedAttribute(
						operation);
				if (associatedAttribute != null) {
					radioData.horizontalSpan = 1;
					addAttribute(buttonComposite, toolkit, associatedAttribute, button);
				} else {
					radioData.horizontalSpan = 4;
				}
				button.setLayoutData(radioData);
				button.addSelectionListener(new SelectButtonListener(button));
				operationButtons.add(button);
				if (operation.equals(selectedOperation)) {
					selectedButton = button;
				}
			}
			// do this last to ensure only a single button is selected
			if (selectedButton == null && !operationButtons.isEmpty()) {
				selectedButton = operationButtons.get(0);
			}
			setSelectedRadionButton(selectedButton, false);
		}
	}

	public boolean getAttachContext() {
		if (attachContextButton == null || attachContextButton.isDisposed()) {
			return false;
		} else {
			return attachContextButton.getSelection();
		}
	}

//	boolean needsAttachContext() {
//		return needsAttachContext;
//	}
//
//	void setNeedsAttachContext(boolean attachContextEnabled) {
//		this.needsAttachContext = attachContextEnabled;
//	}

	public void setSubmitEnabled(boolean enabled) {
		if (submitButton != null && !submitButton.isDisposed()) {
			submitButton.setEnabled(enabled);
			if (enabled) {
				submitButton.setToolTipText("Submit to " + getTaskEditorPage().getTaskRepository().getRepositoryUrl());
			}
		}
	}

	private void setSelectedRadionButton(Button selectedButton, boolean updateModel) {
		// avoid changes to the model if the button is already selected
		if (selectedButton.getSelection()) {
			return;
		}

		selectedButton.setSelection(true);
		for (Button button : operationButtons) {
			if (button != selectedButton) {
				button.setSelection(false);
			}
		}

		if (updateModel) {
			TaskOperation taskOperation = (TaskOperation) selectedButton.getData(KEY_OPERATION);
			getTaskData().getAttributeMapper().setTaskOperation(selectedOperationAttribute, taskOperation);
			getModel().attributeChanged(selectedOperationAttribute);

			AbstractAttributeEditor editor = (AbstractAttributeEditor) selectedButton.getData(KEY_ASSOCIATED_EDITOR);
			if (editor instanceof SingleSelectionAttributeEditor) {
				((SingleSelectionAttributeEditor) editor).selectDefaultValue();
			}
		}
	}

}
