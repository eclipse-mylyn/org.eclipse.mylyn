/*******************************************************************************
 * Copyright (c) 2009 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.ui.tasklist.Messages;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Frank Becker
 */
public class BugzillaAttachmentDialog extends SelectionDialog {

	private final FormToolkit toolkit;

	private Composite scrollComposite;

	private ExpandableComposite flagExpandComposite = null;

	private final TaskAttribute attachment;

	private final Shell parentShell;

	private final boolean readOnly;

	private final AttributeEditorFactory attributeEditorFactory;

	private static final int LABEL_WIDTH = 120;

	private static final int COLUMN_GAP = 5;

	private static final int MULTI_ROW_HEIGHT = 55;

	private static final int COLUMN_WIDTH = 200;

	private static final int MULTI_COLUMN_WIDTH = COLUMN_WIDTH + 5 + COLUMN_GAP + LABEL_WIDTH + 5 + COLUMN_WIDTH;

	private AbstractAttributeEditor commentEditor;

	private final TaskDataModel model;

	private boolean changed = false;

	public BugzillaAttachmentDialog(Shell parentShell, TaskDataModel model, AttributeEditorFactory factory,
			TaskAttribute attachment, boolean readonly) {
		super(parentShell);
		this.attachment = attachment;
		this.parentShell = parentShell;
		this.readOnly = readonly;
		attributeEditorFactory = factory;
		this.model = model;
		model.addModelListener(new TaskDataModelListener() {

			@Override
			public void attributeChanged(TaskDataModelEvent event) {
				if (!changed) {
					changed = true;
					getOkButton().setEnabled(changed);

				}
			}

		});
		toolkit = new FormToolkit(Display.getCurrent());
		this.setTitle(readonly ? Messages.BugzillaAttachmentDialog_DetailTitle
				: Messages.BugzillaAttachmentDialog_DetailTitle);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		int currentColumn = 1;
		int columnCount = 4;
		Composite composite = (Composite) super.createDialogArea(parent);

		initializeDialogUnits(composite);

		Composite attributeArea = new Composite(composite, SWT.FLAT);
		GridLayout layout = new GridLayout(4, false);
		attributeArea.setLayout(layout);
		createAttributeEditors(currentColumn, columnCount, attributeArea);
		createCommentEditor(currentColumn, columnCount, attributeArea);
		Composite advancedComposite = createFlagSection(attributeArea);
		createFlagEditors(columnCount, advancedComposite);
		createAttributeEditor(attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_URL), currentColumn, columnCount,
				advancedComposite);
		Dialog.applyDialogFont(composite);
		return composite;
	}

	private void createAttributeEditor(TaskAttribute attribute, int currentColumn, int columnCount,
			Composite attributeArea) {
		String type = attribute.getMetaData().getType();
		if (type != null) {
			AbstractAttributeEditor editor = attributeEditorFactory.createEditor(type, attribute);
			if (attribute.getId().equals(BugzillaAttribute.TOKEN.getKey())
					|| attribute.getId().equals("size") || attribute.getId().equals(TaskAttribute.ATTACHMENT_URL)) { //$NON-NLS-1$
				editor.setReadOnly(true);
			} else {
				editor.setReadOnly(readOnly);
			}
			if (editor.hasLabel()) {
				editor.createLabelControl(attributeArea, toolkit);
				Label label = editor.getLabelControl();
				label.setBackground(attributeArea.getBackground());
				String labelString = editor.getLabel();
				if (labelString != null && !labelString.equals("")) { //$NON-NLS-1$
					GridData gd = GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).hint(LABEL_WIDTH,
							SWT.DEFAULT).create();
					if (currentColumn > 1) {
						gd.horizontalIndent = COLUMN_GAP;
						gd.widthHint = LABEL_WIDTH + COLUMN_GAP;
					}
					label.setLayoutData(gd);
					currentColumn++;
				}
			}
			editor.createControl(attributeArea, toolkit);
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
			if (type.equals(TaskAttribute.TYPE_BOOLEAN) || type.equals(TaskAttribute.TYPE_SHORT_TEXT)
					|| type.equals(TaskAttribute.TYPE_URL)) {
				gd.horizontalSpan = 3;
			} else {
				gd.horizontalSpan = 1;
			}
			editor.getControl().setLayoutData(gd);
			editor.getControl().setBackground(parentShell.getBackground());
			currentColumn += gd.horizontalSpan;
			currentColumn %= columnCount;
		}
	}

	private void createAttributeEditors(int currentColumn, int columnCount, Composite attributeArea) {
		createAttributeEditor(attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_DESCRIPTION), currentColumn,
				columnCount, attributeArea);
		createAttributeEditor(attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_FILENAME), currentColumn,
				columnCount, attributeArea);
		createAttributeEditor(attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_SIZE), currentColumn, columnCount,
				attributeArea);
		createAttributeEditor(attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_CONTENT_TYPE), currentColumn,
				columnCount, attributeArea);
		createAttributeEditor(attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_PATCH), currentColumn,
				columnCount, attributeArea);
		createAttributeEditor(attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED), currentColumn,
				columnCount, attributeArea);
	}

	private void createFlagEditors(int columnCount, Composite advancedComposite) {
		int currentFlagColumn = 1;

		for (TaskAttribute attribute : attachment.getAttributes().values()) {
			if (!attribute.getId().startsWith("task.common.kind.flag")) { //$NON-NLS-1$
				continue;
			}
			String type = attribute.getMetaData().getType();
			if (type != null) {
				AbstractAttributeEditor editor = attributeEditorFactory.createEditor(type, attribute);
				editor.setReadOnly(readOnly);

				if (editor.hasLabel()) {
					editor.createLabelControl(advancedComposite, toolkit);
					Label label = editor.getLabelControl();
					label.setBackground(advancedComposite.getBackground());
					GridData gd = GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).hint(LABEL_WIDTH,
							SWT.DEFAULT).create();
					if (currentFlagColumn > 1) {
						gd.horizontalIndent = COLUMN_GAP;
						gd.widthHint = LABEL_WIDTH + COLUMN_GAP;
					}
					label.setLayoutData(gd);
					currentFlagColumn++;
				}
				editor.createControl(advancedComposite, toolkit);
				GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
				gd.widthHint = 150;//COLUMN_WIDTH;
				gd.horizontalSpan = 3;
				editor.getControl().setLayoutData(gd);
				currentFlagColumn += gd.horizontalSpan;
				currentFlagColumn %= columnCount;
				editor.getControl().setBackground(parentShell.getBackground());
			}
		}
	}

	private void createCommentEditor(int currentColumn, int columnCount, Composite attributeArea) {
		TaskAttribute commentAttribute = attachment.getAttribute("comment"); //$NON-NLS-1$
		if (commentAttribute == null || readOnly) {
			return;
		}
		String type = commentAttribute.getMetaData().getType();
		if (type != null) {
			commentEditor = attributeEditorFactory.createEditor(type, commentAttribute);
			String labelString = commentEditor.getLabel();
			if (commentEditor.hasLabel()) {
				commentEditor.createLabelControl(attributeArea, toolkit);
				if (!labelString.equals("")) { //$NON-NLS-1$
					Label label = commentEditor.getLabelControl();
					label.setBackground(attributeArea.getBackground());
					GridData gd = GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).hint(LABEL_WIDTH,
							SWT.DEFAULT).create();
					if (currentColumn > 1) {
						gd.horizontalIndent = COLUMN_GAP;
						gd.widthHint = LABEL_WIDTH + COLUMN_GAP;
					}
					label.setLayoutData(gd);
				}
			}
			commentEditor.createControl(attributeArea, toolkit);
			commentEditor.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
			gd.heightHint = MULTI_ROW_HEIGHT;
			gd.widthHint = MULTI_COLUMN_WIDTH;
			gd.horizontalSpan = 2;//columnCount - currentColumn + 1;
			commentEditor.getControl().setLayoutData(gd);
			toolkit.paintBordersFor(attributeArea);
		}
	}

	private Composite createFlagSection(Composite container) {
		flagExpandComposite = toolkit.createExpandableComposite(container, ExpandableComposite.COMPACT
				| ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		flagExpandComposite.setFont(container.getFont());
		flagExpandComposite.setBackground(container.getBackground());
		flagExpandComposite.setText(Messages.BugzillaTaskAttachmentPage_Advanced);
		GridLayout gLayout = new GridLayout(4, false);
		gLayout.horizontalSpacing = 0;
		gLayout.marginWidth = 0;
		flagExpandComposite.setLayout(new GridLayout(4, false));
		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 4;
		flagExpandComposite.setLayoutData(g);
		flagExpandComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				parentShell.getDisplay().getActiveShell().pack();
			}
		});

		scrollComposite = new Composite(flagExpandComposite, SWT.NONE);
		GridLayout gLayout2 = new GridLayout(4, false);
		gLayout2.horizontalSpacing = 0;
		scrollComposite.setLayout(gLayout2);
		flagExpandComposite.setClient(scrollComposite);
		return scrollComposite;
	}

	@Override
	protected void okPressed() {
		commentEditor.getControl().forceFocus();
		super.okPressed();
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		getOkButton().setText(Messages.BugzillaAttachmentDialog_OK_ButtonText);
		getOkButton().setEnabled(changed);
		return control;
	}

}