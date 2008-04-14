/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * An editor used to view a locally created bug that does not yet exist on a repository.
 * 
 * @author Rob Elves
 */
public class NewBugzillaTaskEditor extends AbstractNewRepositoryTaskEditor {

	protected Text assignedTo;

	public NewBugzillaTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		setExpandAttributeSection(true);
	}

	@Override
	protected void saveTaskOffline(IProgressMonitor progressMonitor) {
		String text = descriptionTextViewer.getTextWidget().getText();
		if (repository.getVersion().startsWith("2.18")) {
			text = BugzillaUiPlugin.formatTextToLineWrap(text, true);
			descriptionTextViewer.getTextWidget().setText(text);
		}
		super.saveTaskOffline(progressMonitor);
	}

	@Override
	protected void createPeopleLayout(Composite composite) {
		FormToolkit toolkit = getManagedForm().getToolkit();
		Section peopleSection = createSection(composite, getSectionLabel(SECTION_NAME.PEOPLE_SECTION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(peopleSection);
		Composite peopleComposite = toolkit.createComposite(peopleSection);
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 5;
		peopleComposite.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(peopleComposite);

		addAssignedTo(peopleComposite);
		//addSelfToCC(peopleComposite);
		addCCList(peopleComposite);
		getManagedForm().getToolkit().paintBordersFor(peopleComposite);
		peopleSection.setClient(peopleComposite);
		peopleSection.setEnabled(true);
	}

	@Override
	public void submitToRepository() {
		if (summaryText.getText().equals("")) {
			MessageDialog.openInformation(this.getSite().getShell(), "Submit Error",
					"Please provide a brief summary with new reports.");
			summaryText.setFocus();
			return;
		} else if (descriptionTextViewer.getTextWidget().getText().equals("")) {
			MessageDialog.openInformation(this.getSite().getShell(), "Submit Error",
					"Please proved a detailed description with new reports");
			descriptionTextViewer.getTextWidget().setFocus();
			return;
		}
		RepositoryTaskAttribute attribute = taskData.getAttribute(BugzillaReportElement.COMPONENT.getKeyString());
		String componentValue = attribute.getValue();
		if (componentValue.equals("")) {
			MessageDialog.openInformation(this.getSite().getShell(), "Submit Error",
					"Please select a component with new reports");
			descriptionTextViewer.getTextWidget().setFocus();
			return;
		}
		super.submitToRepository();
	}

	@Override
	protected void createCustomAttributeLayout(Composite composite) {

		RepositoryTaskAttribute attribute = this.taskData.getAttribute(BugzillaReportElement.DEPENDSON.getKeyString());
		if (attribute != null && !attribute.isReadOnly()) {
			Label label = createLabel(composite, attribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Composite textFieldComposite = getManagedForm().getToolkit().createComposite(composite);
			GridLayout textLayout = new GridLayout();
			textLayout.marginWidth = 1;
			textLayout.marginHeight = 3;
			textLayout.verticalSpacing = 3;
			textFieldComposite.setLayout(textLayout);
			GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			textData.horizontalSpan = 1;
			textData.widthHint = 135;

			final Text text = createTextField(textFieldComposite, attribute, SWT.FLAT);
			text.setLayoutData(textData);
			getManagedForm().getToolkit().paintBordersFor(textFieldComposite);
		}

		attribute = this.taskData.getAttribute(BugzillaReportElement.BLOCKED.getKeyString());
		if (attribute != null && !attribute.isReadOnly()) {
			Label label = createLabel(composite, attribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Composite textFieldComposite = getManagedForm().getToolkit().createComposite(composite);
			GridLayout textLayout = new GridLayout();
			textLayout.marginWidth = 1;
			textLayout.marginHeight = 3;
			textLayout.verticalSpacing = 3;
			textFieldComposite.setLayout(textLayout);
			GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			textData.horizontalSpan = 1;
			textData.widthHint = 135;
			final Text text = createTextField(textFieldComposite, attribute, SWT.FLAT);
			text.setLayoutData(textData);
			getManagedForm().getToolkit().paintBordersFor(textFieldComposite);
		}
	}

	@Override
	protected boolean hasContentAssist(RepositoryTaskAttribute attribute) {
		return BugzillaReportElement.NEWCC.getKeyString().equals(attribute.getId());
	}

	/**
	 * FIXME: A lot of duplicated code here between this and BugzillaTaskEditor
	 */
	@Override
	protected void addAssignedTo(Composite peopleComposite) {
		RepositoryTaskAttribute assignedAttribute = taskData.getAttribute(RepositoryTaskAttribute.USER_ASSIGNED);
		if (assignedAttribute != null) {
			String bugzillaVersion;
			try {
				bugzillaVersion = BugzillaCorePlugin.getRepositoryConfiguration(repository, false, new NullProgressMonitor()).getInstallVersion();
			} catch (CoreException e1) {
				// ignore
				bugzillaVersion = "2.18";
			}
			if (bugzillaVersion.compareTo("3.1") < 0) {
				// old bugzilla workflow is used
				super.addAssignedTo(peopleComposite);
				return;
			}
			Label label = createLabel(peopleComposite, assignedAttribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			if (assignedAttribute.isReadOnly()) {
				assignedTo = createTextField(peopleComposite, assignedAttribute, SWT.FLAT | SWT.READ_ONLY);
			} else {
				assignedTo = createTextField(peopleComposite, assignedAttribute, SWT.FLAT);
			}
			GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(assignedTo);
			assignedTo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					String sel = assignedTo.getText();
					RepositoryTaskAttribute a = taskData.getAttribute(RepositoryTaskAttribute.USER_ASSIGNED);
					if (!(a.getValue().equals(sel))) {
						a.setValue(sel);
						markDirty(true);
					}
				}
			});
			ContentAssistCommandAdapter adapter = applyContentAssist(assignedTo,
					createContentProposalProvider(assignedAttribute));
			ILabelProvider propsalLabelProvider = createProposalLabelProvider(assignedAttribute);
			if (propsalLabelProvider != null) {
				adapter.setLabelProvider(propsalLabelProvider);
			}
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

			FormToolkit toolkit = getManagedForm().getToolkit();
			Label dummylabel = toolkit.createLabel(peopleComposite, "");
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(dummylabel);
			RepositoryTaskAttribute attribute = taskData.getAttribute(BugzillaReportElement.SET_DEFAULT_ASSIGNEE.getKeyString());
			if (attribute == null) {
				taskData.setAttributeValue(BugzillaReportElement.SET_DEFAULT_ASSIGNEE.getKeyString(), "0");
				attribute = taskData.getAttribute(BugzillaReportElement.SET_DEFAULT_ASSIGNEE.getKeyString());
			}
			addButtonField(peopleComposite, attribute, SWT.CHECK);
		}
	}

	private Button addButtonField(Composite rolesComposite, RepositoryTaskAttribute attribute, int style) {
		if (attribute == null) {
			return null;
		}
		String name = attribute.getName();
		if (hasOutgoingChange(attribute)) {
			name += "*";
		}

		final Button button = getManagedForm().getToolkit().createButton(rolesComposite, name, style);
		if (!attribute.isReadOnly()) {
			button.setData(attribute);
			button.setSelection(attribute.getValue().equals("1"));
			button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					String sel = "1";
					if (!button.getSelection()) {
						sel = "0";
					}
					RepositoryTaskAttribute a = (RepositoryTaskAttribute) button.getData();
					a.setValue(sel);
					attributeChanged(a);
				}
			});
		}
		return button;
	}

}
