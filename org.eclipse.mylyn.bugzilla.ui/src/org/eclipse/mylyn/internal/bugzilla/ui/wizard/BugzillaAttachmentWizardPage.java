/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.wizard;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class BugzillaAttachmentWizardPage extends WizardPage {
	private static final String PAGE_NAME = "AttachmentDetailPage"; //$NON-NLS-1$

	private static final String DIALOG_SETTING_RUN_IN_BACKGROUND = "run-in-background"; //$NON-NLS-1$

	private static final String DIALOG_SETTING_ADVANCED_EXPANDED = "advanced-expanded"; //$NON-NLS-1$

	private final AttributeEditorFactory factory;

	private final TaskAttribute attachmentAttribute;

	private FormToolkit toolkit;

	private static final int LABEL_WIDTH = 120;

	private static final int COLUMN_GAP = 5;

	private static final int MULTI_ROW_HEIGHT = 55;

	private static final int COLUMN_WIDTH = 100;

	private static final int MULTI_COLUMN_WIDTH = COLUMN_WIDTH + 5 + COLUMN_GAP + LABEL_WIDTH + 5 + COLUMN_WIDTH;

	private AbstractAttributeEditor commentEditor;

	private ExpandableComposite advancedExpandComposite;

	private Button runInBackgroundButton;

	private ScrolledComposite scrolledComposite;

	private Composite scrolledBodyComposite;

	private int currentColumn = 1;

	private final int columnCount = 4;

	private final String repositoryLabel;

	public BugzillaAttachmentWizardPage(Shell parentShell, AttributeEditorFactory factory, String taskID,
			TaskAttribute attachmentAttribute, String repositoryLabel) {
		super(PAGE_NAME);
		setTitle(Messages.BugzillaAttachmentWizardPage_Titel);
		this.repositoryLabel = repositoryLabel;
		this.attachmentAttribute = attachmentAttribute;
		setDescription(MessageFormat.format(Messages.BugzillaAttachmentWizardPage_Description,
				attachmentAttribute.getValue(), taskID, repositoryLabel));
		setImageDescriptor(createImageDescriptor());
		this.factory = factory;
	}

	private static ImageDescriptor createImageDescriptor() {
		return ImageDescriptor.createFromURL(makeIconFileURL());
	}

	private static URL makeIconFileURL() {
		URL baseURL = BugzillaUiPlugin.getDefault().getBundle().getEntry("/icons/"); //$NON-NLS-1$
		if (baseURL == null) {
			return null;
		}
		try {
			return new URL(baseURL, "wizban/banner-attachment-update.gif"); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void createControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
//		Color background = new Color(Display.getDefault(), 192, 192, 192);
//		Color background = new Color(Display.getDefault(), 232, 232, 232);
		Color background = parent.getBackground();
		final Composite pageArea = new Composite(parent, SWT.NONE);
		pageArea.setBackground(parent.getBackground());
		pageArea.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		pageArea.setLayout(new GridLayout(columnCount, false));

		scrolledComposite = new ScrolledComposite(pageArea, SWT.H_SCROLL | SWT.V_SCROLL);
		GridData scrolledCompositeData = new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1);
		scrolledComposite.setLayoutData(scrolledCompositeData);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setBackground(background);

		scrolledBodyComposite = new Composite(scrolledComposite, SWT.NONE);
		scrolledBodyComposite.setBackground(background);
		scrolledBodyComposite.setForeground(scrolledBodyComposite.getDisplay()
				.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		GridLayout layout = new GridLayout(columnCount, false);
		scrolledBodyComposite.setLayout(layout);
		scrolledComposite.setContent(scrolledBodyComposite);
		createAttributeEditors(scrolledBodyComposite);
		createCommentEditor(pageArea);

		createAdvancedSection(pageArea);

		runInBackgroundButton = new Button(pageArea, SWT.CHECK);
		runInBackgroundButton.setText(Messages.BugzillaAttachmentWizardPage_RunInBackground);
		setControl(pageArea);
		Dialog.applyDialogFont(pageArea);
		IDialogSettings settings = BugzillaUiPlugin.getDefault().getDialogSettings();
		IDialogSettings attachmentsSettings = settings.getSection(BugzillaUiPlugin.ATTACHMENT_WIZARD_SETTINGS_SECTION
				+ repositoryLabel);
		boolean advancesExpanded = false;
		if (attachmentsSettings != null) {
			runInBackgroundButton.setSelection(attachmentsSettings.getBoolean(DIALOG_SETTING_RUN_IN_BACKGROUND));
			try {
				advancesExpanded = attachmentsSettings.getBoolean(DIALOG_SETTING_ADVANCED_EXPANDED);
			} catch (Exception e) {
				// ignore
			}

		}
		advancedExpandComposite.setExpanded(advancesExpanded);
		scrolledComposite.setMinSize(scrolledBodyComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));

	}

	private void createAttributeEditor(TaskAttribute attribute, Composite attributeArea) {
		String type = attribute.getMetaData().getType();
		if (type != null) {
			AbstractAttributeEditor editor = factory.createEditor(type, attribute);
			if (attribute.getId().equals(BugzillaAttribute.TOKEN.getKey())
					|| attribute.getId().equals("size") || attribute.getId().equals(TaskAttribute.ATTACHMENT_URL)) { //$NON-NLS-1$
				editor.setReadOnly(true);
			} else {
				editor.setReadOnly(false);
			}
			if (editor.hasLabel()
					&& (!TaskAttribute.ATTACHMENT_IS_PATCH.equals(attribute.getId()) && !TaskAttribute.ATTACHMENT_IS_DEPRECATED.equals(attribute.getId()))) {
				editor.createLabelControl(attributeArea, toolkit);
				Label label = editor.getLabelControl();
				label.setBackground(attributeArea.getBackground());
				label.setForeground(attributeArea.getForeground());

				String labelString = editor.getLabel();
				if (labelString != null && !labelString.equals("")) { //$NON-NLS-1$
					GridData gd = GridDataFactory.fillDefaults()
							.align(SWT.RIGHT, SWT.CENTER)
							.hint(LABEL_WIDTH, SWT.DEFAULT)
							.create();
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
			if (BugzillaAttribute.CTYPE.getKey().equals(attribute.getId())) {
				gd.horizontalSpan = 1;
				gd.widthHint = LABEL_WIDTH;
				gd.grabExcessHorizontalSpace = true;
			} else if (TaskAttribute.ATTACHMENT_IS_PATCH.equals(attribute.getId())
					|| TaskAttribute.ATTACHMENT_IS_DEPRECATED.equals(attribute.getId())) {
				gd.horizontalSpan = 1;
			} else if (TaskAttribute.ATTACHMENT_CONTENT_TYPE.equals(attribute.getId())) {
				gd.horizontalSpan = 2;
			} else if (type.equals(TaskAttribute.TYPE_BOOLEAN) || type.equals(TaskAttribute.TYPE_SHORT_TEXT)) {
				gd.horizontalSpan = 3;
			} else if (type.equals(TaskAttribute.TYPE_URL)) {
				gd.horizontalSpan = 1;
				gd.grabExcessHorizontalSpace = true;
			} else {
				gd.horizontalSpan = 1;
			}
			editor.getControl().setLayoutData(gd);
			editor.getControl().setBackground(attributeArea.getBackground());
			editor.getControl().setForeground(attributeArea.getForeground());
			currentColumn += gd.horizontalSpan;
			currentColumn %= columnCount;
		}
	}

	private void createAttributeEditors(Composite attributeArea) {
		createAttributeEditor(attachmentAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_DESCRIPTION),
				attributeArea);
		createAttributeEditor(attachmentAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_FILENAME), attributeArea);
		createAttributeEditor(attachmentAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_SIZE), attributeArea);
		createAttributeEditor(attachmentAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_CONTENT_TYPE),
				attributeArea);
		createAttributeEditor(attachmentAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_PATCH), attributeArea);
		createAttributeEditor(attachmentAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED),
				attributeArea);
	}

	private void createCommentEditor(Composite attributeArea) {
		TaskAttribute commentAttribute = attachmentAttribute.getAttribute("comment"); //$NON-NLS-1$
		if (commentAttribute == null) {
			return;
		}
		String type = commentAttribute.getMetaData().getType();
		if (type != null) {
			commentEditor = factory.createEditor(type, commentAttribute);
			String labelString = commentEditor.getLabel();
			if (commentEditor.hasLabel()) {
				commentEditor.createLabelControl(attributeArea, toolkit);
				if (!labelString.equals("")) { //$NON-NLS-1$
					Label label = commentEditor.getLabelControl();
					label.setBackground(attributeArea.getBackground());
					label.setForeground(attributeArea.getForeground());
					GridData gd = GridDataFactory.fillDefaults()
							.align(SWT.RIGHT, SWT.TOP)
							.hint(LABEL_WIDTH, SWT.DEFAULT)
							.create();
					if (currentColumn > 1) {
						gd.horizontalIndent = COLUMN_GAP;
						gd.widthHint = LABEL_WIDTH + COLUMN_GAP;
					}
					label.setLayoutData(gd);
				}
			}
			commentEditor.createControl(attributeArea, toolkit);
			commentEditor.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = MULTI_ROW_HEIGHT;
			gd.widthHint = MULTI_COLUMN_WIDTH;
			gd.horizontalSpan = 3;
			commentEditor.getControl().setLayoutData(gd);
			commentEditor.getControl().setForeground(attributeArea.getForeground());

			toolkit.paintBordersFor(attributeArea);
		}

	}

	private void createAdvancedSection(final Composite container) {
		boolean flagFound = false;
		for (TaskAttribute attribute : attachmentAttribute.getAttributes().values()) {
			if (!attribute.getId().startsWith("task.common.kind.flag")) { //$NON-NLS-1$
				continue;
			}
			flagFound = true;
			break;
		}
		if (!flagFound) {
			return;
		}
		advancedExpandComposite = toolkit.createExpandableComposite(container, ExpandableComposite.COMPACT
				| ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		advancedExpandComposite.setFont(container.getFont());
		advancedExpandComposite.setBackground(container.getBackground());
		advancedExpandComposite.setText(Messages.BugzillaAttachmentWizardPage_Advanced);
		advancedExpandComposite.setLayout(new GridLayout(4, false));
		GridDataFactory.fillDefaults()
				.indent(-6, 5)
				.grab(true, false)
				.span(4, SWT.DEFAULT)
				.applyTo(advancedExpandComposite);

		advancedExpandComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				Shell shell = getControl().getShell();
				Point size = shell.getSize();
				size.x++;
				shell.setSize(size);
				size.x--;
				shell.setSize(size);
			}
		});
		Composite advancedBodyComposite = new Composite(advancedExpandComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).applyTo(advancedBodyComposite);
		advancedBodyComposite.setBackground(container.getBackground());
		createFlagEditors(2, advancedBodyComposite);
		createAttributeEditor(attachmentAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_URL),
				advancedBodyComposite);
		advancedExpandComposite.setClient(advancedBodyComposite);
	}

	private void createFlagEditors(int columnCount, Composite flagBodyComposite) {
		int currentFlagColumn = 1;

		for (TaskAttribute attribute : attachmentAttribute.getAttributes().values()) {
			if (!attribute.getId().startsWith("task.common.kind.flag")) { //$NON-NLS-1$
				continue;
			}
			String type = attribute.getMetaData().getType();
			if (type != null) {
				AbstractAttributeEditor editor = factory.createEditor(type, attribute);

				if (editor.hasLabel()) {
					editor.createLabelControl(flagBodyComposite, toolkit);
					Label label = editor.getLabelControl();
					label.setBackground(flagBodyComposite.getBackground());
					label.setForeground(flagBodyComposite.getForeground());

					GridData gd = GridDataFactory.fillDefaults()
							.align(SWT.RIGHT, SWT.CENTER)
							.hint(LABEL_WIDTH - (4 * COLUMN_GAP), SWT.DEFAULT)
							.create();
					if (currentFlagColumn > 1) {
						gd.horizontalIndent = COLUMN_GAP;
						gd.widthHint = LABEL_WIDTH + COLUMN_GAP;
					} else {
						gd.horizontalIndent = COLUMN_GAP * 3;
					}
					label.setLayoutData(gd);
					currentFlagColumn++;
				}
				editor.createControl(flagBodyComposite, toolkit);
				GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
				gd.widthHint = MULTI_COLUMN_WIDTH;//COLUMN_WIDTH;
				editor.getControl().setLayoutData(gd);
				currentFlagColumn += gd.horizontalSpan;
				currentFlagColumn %= columnCount;
				editor.getControl().setBackground(flagBodyComposite.getBackground());
				editor.getControl().setForeground(flagBodyComposite.getForeground());
			}
		}
	}

	@Override
	public void dispose() {
		IDialogSettings settings = BugzillaUiPlugin.getDefault().getDialogSettings();
		IDialogSettings attachmentsSettings = settings.getSection(BugzillaUiPlugin.ATTACHMENT_WIZARD_SETTINGS_SECTION
				+ repositoryLabel);
		if (attachmentsSettings == null) {
			attachmentsSettings = settings.addNewSection(BugzillaUiPlugin.ATTACHMENT_WIZARD_SETTINGS_SECTION
					+ repositoryLabel);
		}
		attachmentsSettings.put(DIALOG_SETTING_RUN_IN_BACKGROUND, runInBackgroundButton.getSelection());
		attachmentsSettings.put(DIALOG_SETTING_ADVANCED_EXPANDED, advancedExpandComposite.isExpanded());
		super.dispose();
	}

	public boolean runInBackground() {
		return runInBackgroundButton.getSelection();
	}

}
