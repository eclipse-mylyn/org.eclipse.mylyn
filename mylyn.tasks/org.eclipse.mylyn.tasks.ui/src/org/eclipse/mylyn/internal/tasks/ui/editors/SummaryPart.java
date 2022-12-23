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
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.mylyn.commons.workbench.editors.CommonTextSupport;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class SummaryPart extends AbstractLocalEditorPart {

	private Composite headerComposite;

	private CommonTextSupport textSupport;

	private RichTextEditor summaryEditor;

	private Button statusCompleteButton;

	private Button statusIncompleteButton;

	private Text creationDateText;

	private Text completionDateText;

	private PriorityEditor priorityEditor;

	private boolean initialized;

	public SummaryPart() {
		super(Messages.SummaryPart_Section_Title);
	}

	private Label createLabel(Composite composite, FormToolkit toolkit, String label, int indent) {
		Label labelControl = toolkit.createLabel(composite, label);
		labelControl.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		GridDataFactory.defaultsFor(labelControl).indent(indent, 0).applyTo(labelControl);
		return labelControl;
	}

	private void createSummaryControl(Composite composite, final FormToolkit toolkit) {
		Composite borderComposite = EditorUtil.createBorder(composite, toolkit);
		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.hint(EditorUtil.MAXIMUM_WIDTH, SWT.DEFAULT)
				.grab(true, false)
				.applyTo(borderComposite);

		summaryEditor = new RichTextEditor(getRepository(), SWT.SINGLE, null, null, getTask());
		summaryEditor.setSpellCheckingEnabled(true);
		summaryEditor.setReadOnly(!isSummaryEditable());
		summaryEditor.createControl(borderComposite, toolkit);
		if (textSupport != null) {
			textSupport.install(summaryEditor.getViewer(), true);
		}
		summaryEditor.getViewer().addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				if (!getTask().getSummary().equals(summaryEditor.getText())) {
					markDirty(summaryEditor.getControl());
				}
			}
		});
		summaryEditor.getViewer().getControl().setMenu(composite.getMenu());
		EditorUtil.setHeaderFontSizeAndStyle(summaryEditor.getControl());
	}

	@Override
	public Control createControl(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = EditorUtil.createSectionClientLayout();
		layout.numColumns = 2;
		layout.marginTop = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 3;
		composite.setLayout(layout);

		priorityEditor = new PriorityEditor() {
			@Override
			protected void valueChanged(String value) {
				priorityEditor.select(value, PriorityLevel.fromString(value));
				priorityEditor.setToolTipText(value);
				markDirty(priorityEditor.getControl());
			};
		};
		Map<String, String> labelByValue = new LinkedHashMap<String, String>();
		for (PriorityLevel level : PriorityLevel.values()) {
			labelByValue.put(level.toString(), level.getDescription());
		}
		priorityEditor.setLabelByValue(labelByValue);
		priorityEditor.createControl(composite, toolkit);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).span(1, 2).applyTo(priorityEditor.getControl());

		createSummaryControl(composite, toolkit);

		createHeaderControls(composite, toolkit);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(headerComposite);

		toolkit.paintBordersFor(composite);
		return composite;
	}

	protected Composite createHeaderControls(Composite composite, FormToolkit toolkit) {
		headerComposite = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		headerComposite.setLayout(layout);

		createLabel(headerComposite, toolkit, Messages.TaskPlanningEditor_Status, 0);
		statusIncompleteButton = toolkit.createButton(headerComposite, Messages.TaskPlanningEditor_Incomplete,
				SWT.RADIO);
		statusIncompleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (statusIncompleteButton.getSelection()) {
					statusCompleteButton.setSelection(false);
					markDirty(statusCompleteButton);
				}
			}
		});
		statusCompleteButton = toolkit.createButton(headerComposite, Messages.TaskPlanningEditor_Complete, SWT.RADIO);
		statusCompleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (statusCompleteButton.getSelection()) {
					statusIncompleteButton.setSelection(false);
					markDirty(statusCompleteButton);
				}
			}
		});

		// right align controls
//		Composite spacer = toolkit.createComposite(headerComposite, SWT.NONE);
//		GridDataFactory.fillDefaults().hint(0, 10).grab(true, false).applyTo(spacer);

		createLabel(headerComposite, toolkit, getCreatedDateLabel(), EditorUtil.HEADER_COLUMN_MARGIN);
		// do not use toolkit.createText() to avoid border on Windows
		creationDateText = new Text(headerComposite, SWT.FLAT | SWT.READ_ONLY);
		toolkit.adapt(creationDateText, false, false);
		creationDateText.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);

		createLabel(headerComposite, toolkit, Messages.TaskPlanningEditor_Completed, EditorUtil.HEADER_COLUMN_MARGIN);
		// do not use toolkit.createText() to avoid border on Windows
		completionDateText = new Text(headerComposite, SWT.FLAT | SWT.READ_ONLY);
		toolkit.adapt(completionDateText, false, false);
		completionDateText.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);

		// ensure layout does not wrap
		layout.numColumns = headerComposite.getChildren().length;
		toolkit.paintBordersFor(headerComposite);
		return headerComposite;
	}

	@Override
	public void setFocus() {
		if (summaryEditor != null) {
			summaryEditor.getControl().setFocus();
		}
	}

	private String getDateString(Date date) {
		if (date == null) {
			return "-"; //$NON-NLS-1$
		}
		return EditorUtil.getDateFormat().format(date);
	}

	private String getDateTimeString(Date date) {
		if (date == null) {
			return "-"; //$NON-NLS-1$
		}
		return EditorUtil.getDateTimeFormat().format(date);
	}

	@Override
	public void refresh(boolean discardChanges) {
		if (shouldRefresh(priorityEditor.getControl(), discardChanges)) {
			PriorityLevel level = PriorityLevel.fromString(getTask().getPriority());
			priorityEditor.select(level.toString(), level);
		}
		if (shouldRefresh(statusCompleteButton, discardChanges)) {
			statusIncompleteButton.setSelection(!getTask().isCompleted());
			statusCompleteButton.setSelection(getTask().isCompleted());
		}
		if (shouldRefresh(summaryEditor.getControl(), discardChanges)) {
			summaryEditor.setText(getTask().getSummary());
			if (!initialized) {
				initialized = true;
				if (LocalRepositoryConnector.DEFAULT_SUMMARY.equals(getTask().getSummary())) {
					summaryEditor.getViewer().setSelectedRange(0, summaryEditor.getText().length());
				}
			}
		}
		creationDateText.setText(getDateString(getTask().getCreationDate()));
		updateToolTip(creationDateText, getTask().getCreationDate());
		completionDateText.setText(getDateString(getTask().getCompletionDate()));
		updateToolTip(completionDateText, getTask().getCompletionDate());
		// re-layout date fields
		headerComposite.layout(true);
	}

	private void updateToolTip(Text text, Date date) {
		if (date != null) {
			text.setToolTipText(getDateTimeString(date));
		} else {
			text.setToolTipText(null);
		}
	}

	@Override
	public void commit(boolean onSave) {
		PriorityLevel level = PriorityLevel.fromString(priorityEditor.getValue());
		if (level != null) {
			getTask().setPriority(level.toString());
		}
		clearState(priorityEditor.getControl());
		getTask().setSummary(summaryEditor.getText());
		clearState(summaryEditor.getControl());
		if (statusCompleteButton.getSelection()) {
			if (!getTask().isCompleted()) {
				getTask().setCompletionDate(new Date());
			}
		} else {
			if (getTask().isCompleted()) {
				getTask().setCompletionDate(null);
			}
		}
		clearState(statusCompleteButton);
		super.commit(onSave);
	}

	public void setTextSupport(CommonTextSupport textSupport) {
		this.textSupport = textSupport;
	}

	public CommonTextSupport getTextSupport() {
		return textSupport;
	}

	public void setSummary(String value) {
		if (!summaryEditor.getControl().isDisposed()) {
			summaryEditor.setText(value);
		}
	}

	protected String getCreatedDateLabel() {
		return Messages.TaskPlanningEditor_Created;
	}

	protected boolean isSummaryEditable() {
		return getTask() instanceof LocalTask;
	}

}
