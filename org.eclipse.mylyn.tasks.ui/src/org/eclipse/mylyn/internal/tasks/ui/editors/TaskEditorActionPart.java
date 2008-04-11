/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.RepositoryOperation;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class TaskEditorActionPart extends AbstractTaskEditorPart {

	/**
	 * Class to handle the selection change of the radio buttons.
	 */
	private class RadioButtonListener implements SelectionListener, ModifyListener {

		public void modifyText(ModifyEvent e) {
			Button selected = null;
			for (Button element : radios) {
				if (element.getSelection()) {
					selected = element;
				}
			}
			// determine the operation to do to the bug
			for (int i = 0; i < radios.length; i++) {
				if (radios[i] != e.widget && radios[i] != selected) {
					radios[i].setSelection(false);
				}

				if (e.widget == radios[i]) {
					RepositoryOperation o = getTaskData().getOperation(radios[i].getText());
					getTaskData().setSelectedOperation(o);
					getTaskEditorPage().getAttributeManager().operationChanged(o);
				} else if (e.widget == radioOptions[i]) {
					RepositoryOperation o = getTaskData().getOperation(radios[i].getText());
					o.setInputValue(((Text) radioOptions[i]).getText());

					if (getTaskData().getSelectedOperation() != null) {
						getTaskData().getSelectedOperation().setChecked(false);
					}
					o.setChecked(true);

					getTaskData().setSelectedOperation(o);
					radios[i].setSelection(true);
					if (selected != null && selected != radios[i]) {
						selected.setSelection(false);
					}
					getTaskEditorPage().getAttributeManager().operationChanged(o);
				}
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			Button selected = null;
			for (Button element : radios) {
				if (element.getSelection()) {
					selected = element;
				}
			}
			// determine the operation to do to the bug
			for (int i = 0; i < radios.length; i++) {
				if (radios[i] != e.widget && radios[i] != selected) {
					radios[i].setSelection(false);
				}

				if (e.widget == radios[i]) {
					RepositoryOperation o = getTaskData().getOperation(radios[i].getText());
					getTaskData().setSelectedOperation(o);
					getTaskEditorPage().getAttributeManager().operationChanged(o);
				} else if (e.widget == radioOptions[i]) {
					RepositoryOperation o = getTaskData().getOperation(radios[i].getText());
					o.setOptionSelection(((CCombo) radioOptions[i]).getItem(((CCombo) radioOptions[i]).getSelectionIndex()));

					if (getTaskData().getSelectedOperation() != null) {
						getTaskData().getSelectedOperation().setChecked(false);
					}
					o.setChecked(true);

					getTaskData().setSelectedOperation(o);
					radios[i].setSelection(true);
					if (selected != null && selected != radios[i]) {
						selected.setSelection(false);
					}
					getTaskEditorPage().getAttributeManager().operationChanged(o);
				}
			}
		}
	}

	private static final int DEFAULT_FIELD_WIDTH = 150;

	private static final String LABEL_BUTTON_SUBMIT = "Submit";

	private static final int RADIO_OPTION_WIDTH = 120;

	private Control[] radioOptions;

	private Button[] radios;

	private Button submitButton;

	private Button attachContextButton;

	private boolean needsAttachContext = true;

	private boolean needsAddToCategory;

	private Button addToCategory;

	private CCombo categoryChooser;

	private AbstractTaskCategory category;

	public TaskEditorActionPart(AbstractTaskEditorPage taskEditorPage) {
		super(taskEditorPage);
	}

	protected void addAttachContextButton(Composite buttonComposite, AbstractTask task, FormToolkit toolkit) {
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

		AbstractTask task = TasksUi.getTaskListManager().getTaskList().getTask(getTaskRepository().getRepositoryUrl(),
				getTaskData().getTaskId());
		if (needsAttachContext && task != null) {
			addAttachContextButton(buttonComposite, task, toolkit);
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
		TaskList taskList = TasksUi.getTaskListManager().getTaskList();
		List<AbstractTaskCategory> categories = new ArrayList<AbstractTaskCategory>(taskList.getCategories());
		Collections.sort(categories, new Comparator<AbstractTaskContainer>() {

			public int compare(AbstractTaskContainer c1, AbstractTaskContainer c2) {
				if (c1.equals(TasksUi.getTaskListManager().getTaskList().getDefaultCategory())) {
					return -1;
				} else if (c2.equals(TasksUi.getTaskListManager().getTaskList().getDefaultCategory())) {
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
		Composite buttonComposite = toolkit.createComposite(parent);
		GridLayout buttonLayout = new GridLayout();
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(buttonComposite);
		buttonLayout.numColumns = 4;
		buttonComposite.setLayout(buttonLayout);

		if (needsAddToCategory) {
			createCategoryChooser(buttonComposite, toolkit);
		}

		createRadioButtons(buttonComposite, toolkit);
		createActionButtons(buttonComposite, toolkit);

		setControl(buttonComposite);
	}

	private void createRadioButtons(Composite buttonComposite, FormToolkit toolkit) {
		int i = 0;
		Button selected = null;
		radios = new Button[getTaskData().getOperations().size()];
		radioOptions = new Control[getTaskData().getOperations().size()];
		for (RepositoryOperation o : getTaskData().getOperations()) {
			radios[i] = toolkit.createButton(buttonComposite, "", SWT.RADIO);
			radios[i].setFont(TEXT_FONT);
			GridData radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			if (!o.hasOptions() && !o.isInput()) {
				radioData.horizontalSpan = 4;
			} else {
				radioData.horizontalSpan = 1;
			}
			radioData.heightHint = 20;
			String opName = o.getOperationName();
			opName = opName.replaceAll("</.*>", "");
			opName = opName.replaceAll("<.*>", "");
			radios[i].setText(opName);
			radios[i].setLayoutData(radioData);
			// radios[i].setBackground(background);
			radios[i].addSelectionListener(new RadioButtonListener());

			if (o.hasOptions()) {
				radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				radioData.horizontalSpan = 3;
				radioData.heightHint = 20;
				radioData.widthHint = RADIO_OPTION_WIDTH;
				radioOptions[i] = new CCombo(buttonComposite, SWT.FLAT | SWT.READ_ONLY);
				radioOptions[i].setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				toolkit.adapt(radioOptions[i], true, true);
				radioOptions[i].setFont(TEXT_FONT);
				radioOptions[i].setLayoutData(radioData);

				Object[] a = o.getOptionNames().toArray();
				Arrays.sort(a);
				for (int j = 0; j < a.length; j++) {
					if (a[j] != null) {
						((CCombo) radioOptions[i]).add((String) a[j]);
						if (((String) a[j]).equals(o.getOptionSelection())) {
							((CCombo) radioOptions[i]).select(j);
						}
					}
				}
				((CCombo) radioOptions[i]).addSelectionListener(new RadioButtonListener());
			} else if (o.isInput()) {
				radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				radioData.horizontalSpan = 3;
				radioData.widthHint = RADIO_OPTION_WIDTH - 10;

				String assignmentValue = "";
				// NOTE: removed this because we now have content assit
// if (opName.equals(REASSIGN_BUG_TO)) {
// assignmentValue = repository.getUserName();
// }
				radioOptions[i] = toolkit.createText(buttonComposite, assignmentValue);
				radioOptions[i].setFont(TEXT_FONT);
				radioOptions[i].setLayoutData(radioData);
				// radioOptions[i].setBackground(background);
				((Text) radioOptions[i]).setText(o.getInputValue());
				((Text) radioOptions[i]).addModifyListener(new RadioButtonListener());

				// FIXME EDITOR use attributes instead of operations
//				if (getTaskEditorPage().getAttributeEditorToolkit().hasContentAssist(o)) {
//					ContentAssistCommandAdapter adapter = applyContentAssist((Text) radioOptions[i],
//							getTaskEditorPage().getAttributeEditorToolkit().createContentProposalProvider(o));
//					ILabelProvider propsalLabelProvider = getTaskEditorPage().getAttributeEditorToolkit()
//							.createProposalLabelProvider(o);
//					if (propsalLabelProvider != null) {
//						adapter.setLabelProvider(propsalLabelProvider);
//					}
//					adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
//				}
			}

			if (i == 0 || o.isChecked()) {
				if (selected != null) {
					selected.setSelection(false);
				}
				selected = radios[i];
				radios[i].setSelection(true);
				if (o.hasOptions() && o.getOptionSelection() != null) {
					int j = 0;
					for (String s : ((CCombo) radioOptions[i]).getItems()) {
						if (s.compareTo(o.getOptionSelection()) == 0) {
							((CCombo) radioOptions[i]).select(j);
						}
						j++;
					}
				}
				getTaskData().setSelectedOperation(o);
			}

			i++;
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
				submitButton.setToolTipText("Submit to " + getTaskRepository().getRepositoryUrl());
			}
		}
	}

}
