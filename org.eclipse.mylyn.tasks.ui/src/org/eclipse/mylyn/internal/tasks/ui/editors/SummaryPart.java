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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonTextSupport;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
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

	protected boolean summaryChanged;

	private CCombo statusCombo;

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
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(EditorUtil.MAXIMUM_WIDTH, SWT.DEFAULT).grab(
				true, false).applyTo(borderComposite);

		summaryEditor = new RichTextEditor(getRepository(), SWT.SINGLE);
		summaryEditor.setSpellCheckingEnabled(true);
		summaryEditor.setReadOnly(!(getTask() instanceof LocalTask));
		summaryEditor.createControl(borderComposite, toolkit);
		if (textSupport != null) {
			textSupport.install(summaryEditor.getViewer(), true);
		}
		summaryEditor.getViewer().addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				if (!getTask().getSummary().equals(summaryEditor.getText())) {
					summaryChanged = true;
					markDirty();
				}
			}
		});
		summaryEditor.getViewer().getControl().setMenu(composite.getMenu());
		EditorUtil.setHeaderFontSizeAndStyle(summaryEditor.getControl());
	}

	@Override
	public Control createControl(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 5;
		composite.setLayout(layout);

		priorityEditor = new PriorityEditor() {
			@Override
			protected void valueChanged(String value) {
				markDirty();
				priorityEditor.select(value, PriorityLevel.fromString(value));
				priorityEditor.setToolTipText(value);
			};
		};
		Map<String, String> labelByValue = new LinkedHashMap<String, String>();
		for (PriorityLevel level : PriorityLevel.values()) {
			labelByValue.put(level.toString(), level.getDescription());
		}
		priorityEditor.setLabelByValue(labelByValue);
		priorityEditor.createControl(composite, toolkit);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(priorityEditor.getControl());

		createSummaryControl(composite, toolkit);

		createHeaderControls(composite, toolkit);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(headerComposite);

		toolkit.paintBordersFor(composite);
		return composite;
	}

	protected Composite createHeaderControls(Composite composite, FormToolkit toolkit) {
		headerComposite = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 1;
		layout.marginHeight = 1;
		layout.marginWidth = 0;
		layout.marginBottom = 10;
		headerComposite.setLayout(layout);

		createLabel(headerComposite, toolkit, Messages.TaskPlanningEditor_Status, 0);
		statusCombo = new CCombo(headerComposite, SWT.FLAT | SWT.READ_ONLY);
		toolkit.adapt(statusCombo, true, true);
		statusCombo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		statusCombo.add(Messages.TaskPlanningEditor_Complete);
		statusCombo.add(Messages.TaskPlanningEditor_Incomplete);
		if (getTask().isCompleted()) {
			statusCombo.select(0);
		} else {
			statusCombo.select(1);
		}
		statusCombo.setEnabled(getTask() instanceof LocalTask);
		statusCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				markDirty();
			}
		});

		// right align controls
		Composite spacer = toolkit.createComposite(headerComposite, SWT.NONE);
		GridDataFactory.fillDefaults().hint(0, 10).grab(true, false).applyTo(spacer);

		createLabel(headerComposite, toolkit, Messages.TaskPlanningEditor_Created, 0);
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
		return EditorUtil.getDateTimeFormat().format(date);
	}

	@Override
	public void refresh() {
		PriorityLevel level = PriorityLevel.fromString(getTask().getPriority());
		priorityEditor.select(level.toString(), level);
		if (getTask().isCompleted()) {
			statusCombo.select(0);
		} else {
			statusCombo.select(1);
		}
		if (!summaryChanged) {
			summaryEditor.setText(getTask().getSummary());
			if (!initialized) {
				initialized = true;
				if (LocalRepositoryConnector.DEFAULT_SUMMARY.equals(getTask().getSummary())) {
					summaryEditor.getViewer().setSelectedRange(0, summaryEditor.getText().length());
				}
			}
		}
		creationDateText.setText(getDateString(getTask().getCreationDate()));
		completionDateText.setText(getDateString(getTask().getCompletionDate()));
		super.refresh();
		if (summaryChanged) {
			markDirty();
		}
		((Composite) getControl()).layout();
	}

	@Override
	public void commit(boolean onSave) {
		PriorityLevel level = PriorityLevel.fromString(priorityEditor.getValue());
		if (level != null) {
			getTask().setPriority(level.toString());
		}
		getTask().setSummary(summaryEditor.getText());
		if (!getTask().isCompleted() && statusCombo.getSelectionIndex() == 0) {
			getTask().setCompletionDate(new Date());
		} else {
			getTask().setCompletionDate(null);
		}
		summaryChanged = false;
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

}
