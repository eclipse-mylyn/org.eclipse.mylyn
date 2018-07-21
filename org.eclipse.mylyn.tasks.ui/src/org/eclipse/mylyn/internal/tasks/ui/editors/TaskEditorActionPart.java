/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fix for bug 254806, bug 267135
 *     Benjamin Muskalla - fix for bug 310798
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskContainerComparator;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public class TaskEditorActionPart extends AbstractTaskEditorPart {

	private static final String KEY_OPERATION = "operation"; //$NON-NLS-1$

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

	private static final int RADIO_OPTION_WIDTH = 120;

	private static final String KEY_ASSOCIATED_EDITOR = "associatedEditor"; //$NON-NLS-1$

	private List<Button> operationButtons;

	private Button submitButton;

	private Button attachContextButton;

	private Button addToCategory;

	private CCombo categoryChooser;

	private AbstractTaskCategory category;

	private TaskAttribute selectedOperationAttribute;

	public TaskEditorActionPart() {
		setPartName(Messages.TaskEditorActionPart_Actions);
	}

	protected void addAttachContextButton(Composite buttonComposite, FormToolkit toolkit) {
		attachContextButton = toolkit.createButton(buttonComposite, Messages.TaskEditorActionPart_Attach_Context,
				SWT.CHECK);
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
		//if (!getTaskEditorPage().needsSubmitButton()) {
		submitButton = toolkit.createButton(buttonComposite, Messages.TaskEditorActionPart_Submit, SWT.NONE);
		submitButton.setImage(CommonImages.getImage(TasksUiImages.REPOSITORY_SUBMIT));
		submitButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				getTaskEditorPage().doSubmit();
			}
		});
		Point minSize = submitButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		GridData submitButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		submitButtonData.widthHint = Math.max(100, minSize.x);
		submitButton.setLayoutData(submitButtonData);

		setSubmitEnabled(true);

		toolkit.createLabel(buttonComposite, "    "); //$NON-NLS-1$
		//}

		createAttachContextButton(buttonComposite, toolkit);
	}

	private void createAttachContextButton(Composite buttonComposite, FormToolkit toolkit) {
		AbstractTaskEditorPage taskEditorPage = getTaskEditorPage();
		AbstractRepositoryConnector connector = taskEditorPage.getConnector();
		AbstractTaskAttachmentHandler taskAttachmentHandler = connector.getTaskAttachmentHandler();
		boolean canPostContent = false;
		if (taskAttachmentHandler != null) {
			TaskRepository taskRepository = taskEditorPage.getTaskRepository();
			ITask task = taskEditorPage.getTask();
			canPostContent = taskAttachmentHandler.canPostContent(taskRepository, task);
		}
		if ((!getTaskData().isNew() && canPostContent)) {
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
		addToCategory = getManagedForm().getToolkit().createButton(buttonComposite,
				Messages.TaskEditorActionPart_Add_to_Category, SWT.CHECK);
		categoryChooser = new CCombo(buttonComposite, SWT.FLAT | SWT.READ_ONLY);
		categoryChooser.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		categoryChooser.setLayoutData(GridDataFactory.swtDefaults().hint(150, SWT.DEFAULT).create());
		toolkit.adapt(categoryChooser, false, false);
		categoryChooser.setFont(TEXT_FONT);
		ITaskList taskList = TasksUiInternal.getTaskList();
		final List<AbstractTaskCategory> categories = new ArrayList<AbstractTaskCategory>(taskList.getCategories());
		Collections.sort(categories, new TaskContainerComparator());
		AbstractTaskCategory selectedCategory = TasksUiInternal.getSelectedCategory(TaskListView.getFromActivePerspective());
		int i = 0;
		int selectedIndex = 0;
		for (IRepositoryElement category : categories) {
			categoryChooser.add(category.getSummary());
			if (category.equals(selectedCategory)) {
				selectedIndex = i;
			}
			i++;
		}
		categoryChooser.select(selectedIndex);
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

		GridDataFactory.fillDefaults()
				.hint(DEFAULT_FIELD_WIDTH, SWT.DEFAULT)
				.span(3, SWT.DEFAULT)
				.applyTo(categoryChooser);
	}

	public AbstractTaskCategory getCategory() {
		return category;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);

		Composite buttonComposite = toolkit.createComposite(section);
		GridLayout buttonLayout = EditorUtil.createSectionClientLayout();
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

		createOperationAttributes(buttonComposite, toolkit);

		createActionButtons(buttonComposite, toolkit);

		toolkit.paintBordersFor(buttonComposite);
		section.setClient(buttonComposite);
		setSection(toolkit, section);
	}

	private void createOperationAttributes(Composite buttonComposite, FormToolkit toolkit) {
		Composite parent = null;
		for (TaskAttribute taskAttribute : getTaskData().getRoot().getAttributes().values()) {
			if (TaskAttribute.KIND_OPERATION.equals(taskAttribute.getMetaData().getKind())) {
				if (parent == null) {
					parent = toolkit.createComposite(buttonComposite);
					parent.setLayout(new GridLayout(2, false));
					GridDataFactory.fillDefaults().span(4, 1).grab(true, false).applyTo(parent);
					toolkit.paintBordersFor(parent);
				}

				addAttribute(parent, toolkit, taskAttribute);
			}
		}
	}

	private void addAttribute(Composite attributesComposite, FormToolkit toolkit, TaskAttribute taskAttribute) {
		AbstractAttributeEditor attributeEditor = createAttributeEditor(taskAttribute);
		if (attributeEditor.hasLabel() && attributeEditor.getLabel().length() != 0) {
			attributeEditor.createLabelControl(attributesComposite, toolkit);
			Label label = attributeEditor.getLabelControl();
			String text = label.getText();
			label.setText(text);
			GridData gd = GridDataFactory.fillDefaults()
					.align(SWT.LEFT, SWT.CENTER)
					.hint(SWT.DEFAULT, SWT.DEFAULT)
					.create();
			label.setLayoutData(gd);
		}

		attributeEditor.createControl(attributesComposite, toolkit);
		getTaskEditorPage().getAttributeEditorToolkit().adapt(attributeEditor);
		GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gd.minimumWidth = DEFAULT_FIELD_WIDTH;
		if (attributeEditor.getLabelControl() == null) {
			gd.horizontalSpan = 2;
		}
		attributeEditor.getControl().setLayoutData(gd);

	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute, Button button) {
		AbstractAttributeEditor editor = createAttributeEditor(attribute);
		if (editor != null) {
			editor.createControl(composite, toolkit);
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.horizontalSpan = 3;
			Control editorControl = editor.getControl();
			if (editorControl instanceof CCombo) {
				if (!Platform.OS_MACOSX.equals(Platform.getOS())) {
					// XXX on some platforms combo boxes are too tall by default and wider than other controls
					// bug 267135 only do this for non-mac platforms, since the default CCombo height on Carbon and Cocoa is perfect
					gd.heightHint = 20;
				}
				gd.widthHint = RADIO_OPTION_WIDTH;
			} else {
				gd.widthHint = RADIO_OPTION_WIDTH - 5;
			}
			editorControl.setLayoutData(gd);

			if (editor instanceof PersonAttributeEditor) {
				editorControl = ((PersonAttributeEditor) editor).getText();
			}

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
				//button.setEnabled(!operation.getTaskAttribute().getMetaData().isReadOnly());
				button.setEnabled(!operation.getTaskAttribute().getMetaData().isDisabled());
				button.setToolTipText(operation.getTaskAttribute()
						.getMetaData()
						.getValue(TaskAttribute.META_DESCRIPTION));

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
				submitButton.setToolTipText(MessageFormat.format(Messages.TaskEditorActionPart_Submit_to_X,
						getTaskEditorPage().getTaskRepository().getRepositoryUrl()));
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

	/**
	 * @since 3.5
	 */
	public void refreshOperations() {
		if (operationButtons != null) {
			for (Button button : operationButtons) {
				TaskOperation taskOperation = (TaskOperation) button.getData(KEY_OPERATION);
				button.setEnabled(!taskOperation.getTaskAttribute().getMetaData().isDisabled());
				button.setToolTipText(taskOperation.getTaskAttribute()
						.getMetaData()
						.getValue(TaskAttribute.META_DESCRIPTION));
			}
		}
	}
}
