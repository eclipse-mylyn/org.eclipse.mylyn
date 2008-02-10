/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.List;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class TaskEditorPeoplePart extends AbstractTaskEditorPart {

	private static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	private org.eclipse.swt.widgets.List ccList;

	public TaskEditorPeoplePart(AbstractTaskEditorPage taskEditorPage) {
		super(taskEditorPage);
	}

	/**
	 * This method allow you to overwrite the generation of the form area for "assigned to" in the peopleLayout.<br>
	 * <br>
	 * The overwrite is used for Bugzilla Versions > 3.0
	 * 
	 * @since 2.1
	 * @author Frank Becker (bug 198027)
	 * @param toolkit 
	 */
	protected void addAssignedTo(Composite peopleComposite, FormToolkit toolkit) {
		boolean haveRealName = false;
		RepositoryTaskAttribute assignedAttribute = getTaskData().getAttribute(
				RepositoryTaskAttribute.USER_ASSIGNED_NAME);
		if (assignedAttribute == null) {
			assignedAttribute = getTaskData().getAttribute(RepositoryTaskAttribute.USER_ASSIGNED);
		} else {
			haveRealName = true;
		}
		if (assignedAttribute != null) {
			Label label = createLabel(peopleComposite, assignedAttribute, toolkit);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Text textField;
			if (assignedAttribute.isReadOnly()) {
				textField = createTextField(peopleComposite, assignedAttribute, SWT.FLAT | SWT.READ_ONLY, toolkit);
			} else {
				textField = createTextField(peopleComposite, assignedAttribute, SWT.FLAT, toolkit);
				ContentAssistCommandAdapter adapter = getTaskEditorPage().getAttributeEditorToolkit().applyContentAssist(textField,
						getTaskEditorPage().getAttributeEditorToolkit().createContentProposalProvider(assignedAttribute));
				ILabelProvider propsalLabelProvider = getTaskEditorPage().getAttributeEditorToolkit().createLabelProposalProvider(assignedAttribute);
				if (propsalLabelProvider != null) {
					adapter.setLabelProvider(propsalLabelProvider);
				}
				adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			}
			GridDataFactory.fillDefaults().grab(true, false).applyTo(textField);
			if (haveRealName) {
				textField.setText(textField.getText() + " <"
						+ getTaskData().getAttributeValue(RepositoryTaskAttribute.USER_ASSIGNED) + ">");
			}

		}
	}

	protected void addCCList(Composite attributesComposite, FormToolkit toolkit) {

		RepositoryTaskAttribute addCCattribute = getTaskData().getAttribute(RepositoryTaskAttribute.NEW_CC);
		if (addCCattribute == null) {
			return;
		}

		Label label = createLabel(attributesComposite, addCCattribute, toolkit);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
		Text text = createTextField(attributesComposite, addCCattribute, SWT.FLAT, toolkit);
		GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(text);

		if (getTaskEditorPage().getAttributeEditorToolkit().hasContentAssist(addCCattribute)) {
			ContentAssistCommandAdapter adapter = getTaskEditorPage().getAttributeEditorToolkit().applyContentAssist(text,
					getTaskEditorPage().getAttributeEditorToolkit().createContentProposalProvider(addCCattribute));
			ILabelProvider propsalLabelProvider = getTaskEditorPage().getAttributeEditorToolkit().createLabelProposalProvider(addCCattribute);
			if (propsalLabelProvider != null) {
				adapter.setLabelProvider(propsalLabelProvider);
			}
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		}

		RepositoryTaskAttribute CCattribute = getTaskData().getAttribute(RepositoryTaskAttribute.USER_CC);
		if (CCattribute != null) {
			label = createLabel(attributesComposite, CCattribute, toolkit);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).applyTo(label);
			ccList = new org.eclipse.swt.widgets.List(attributesComposite, SWT.MULTI | SWT.V_SCROLL);// SWT.BORDER
			ccList.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			ccList.setFont(TEXT_FONT);
			GridData ccListData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			ccListData.horizontalSpan = 1;
			ccListData.widthHint = 150;
			ccListData.heightHint = 95;
			ccList.setLayoutData(ccListData);

			RepositoryTaskAttribute taskAttribute = getTaskData().getAttribute(RepositoryTaskAttribute.USER_CC);
			if (getTaskEditorPage().getAttributeManager().hasIncomingChanges(taskAttribute)) {
				ccList.setBackground(getTaskEditorPage().getColorIncoming());
			}

			java.util.List<String> ccs = getTaskData().getCc();
			if (ccs != null) {
				for (String cc : ccs) {
					ccList.add(cc);
				}
			}
			java.util.List<String> removedCCs = getTaskData().getAttributeValues(RepositoryTaskAttribute.REMOVE_CC);
			if (removedCCs != null) {
				for (String item : removedCCs) {
					int i = ccList.indexOf(item);
					if (i != -1) {
						ccList.select(i);
					}
				}
			}
			ccList.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					for (String cc : ccList.getItems()) {
						int index = ccList.indexOf(cc);
						if (ccList.isSelected(index)) {
							List<String> remove = getTaskData().getAttributeValues(RepositoryTaskAttribute.REMOVE_CC);
							if (!remove.contains(cc)) {
								getTaskData().addAttributeValue(RepositoryTaskAttribute.REMOVE_CC, cc);
							}
						} else {
							getTaskData().removeAttributeValue(RepositoryTaskAttribute.REMOVE_CC, cc);
						}
					}
					getTaskEditorPage().getAttributeManager().attributeChanged(getTaskData().getAttribute(RepositoryTaskAttribute.REMOVE_CC));
				}
			});
			toolkit.createLabel(attributesComposite, "");
			label = toolkit.createLabel(attributesComposite, "(Select to remove)");
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(label);
		}

	}

	/**
	 * Creates a check box for adding the repository user to the cc list. Does nothing if the repository does not have a
	 * valid username, the repository user is the assignee, reporter or already on the the cc list.
	 * @param toolkit 
	 */
	protected void addSelfToCC(Composite composite, FormToolkit toolkit) {

		if (getTaskRepository().getUserName() == null) {
			return;
		}

		RepositoryTaskAttribute owner = getTaskData().getAttribute(RepositoryTaskAttribute.USER_ASSIGNED);
		if (owner != null && owner.getValue().indexOf(getTaskRepository().getUserName()) != -1) {
			return;
		}

		RepositoryTaskAttribute reporter = getTaskData().getAttribute(RepositoryTaskAttribute.USER_REPORTER);
		if (reporter != null && reporter.getValue().indexOf(getTaskRepository().getUserName()) != -1) {
			return;
		}

		RepositoryTaskAttribute ccAttribute = getTaskData().getAttribute(RepositoryTaskAttribute.USER_CC);
		if (ccAttribute != null && ccAttribute.getValues().contains(getTaskRepository().getUserName())) {
			return;
		}

		toolkit.createLabel(composite, "");
		final Button addSelfButton = toolkit.createButton(composite, "Add me to CC", SWT.CHECK);
		addSelfButton.setSelection(RepositoryTaskAttribute.TRUE.equals(getTaskData().getAttributeValue(
				RepositoryTaskAttribute.ADD_SELF_CC)));
		addSelfButton.setImage(TasksUiImages.getImage(TasksUiImages.PERSON));
		addSelfButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (addSelfButton.getSelection()) {
					getTaskData().setAttributeValue(RepositoryTaskAttribute.ADD_SELF_CC, RepositoryTaskAttribute.TRUE);
				} else {
					getTaskData().setAttributeValue(RepositoryTaskAttribute.ADD_SELF_CC, RepositoryTaskAttribute.FALSE);
				}
				RepositoryTaskAttribute attribute = getTaskData().getAttribute(RepositoryTaskAttribute.ADD_SELF_CC);
				getTaskEditorPage().getAttributeManager().attributeChanged(attribute);
			}
		});
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite peopleComposite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 5;
		peopleComposite.setLayout(layout);

		addAssignedTo(peopleComposite, toolkit);
		boolean haveRealName = false;
		RepositoryTaskAttribute reporterAttribute = getTaskData().getAttribute(
				RepositoryTaskAttribute.USER_REPORTER_NAME);
		if (reporterAttribute == null) {
			reporterAttribute = getTaskData().getAttribute(RepositoryTaskAttribute.USER_REPORTER);
		} else {
			haveRealName = true;
		}
		if (reporterAttribute != null) {
			Label label = createLabel(peopleComposite, reporterAttribute, toolkit);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Text textField = createTextField(peopleComposite, reporterAttribute, SWT.FLAT | SWT.READ_ONLY, toolkit);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(textField);
			if (haveRealName) {
				textField.setText(textField.getText() + " <"
						+ getTaskData().getAttributeValue(RepositoryTaskAttribute.USER_REPORTER) + ">");
			}
		}
		
		addSelfToCC(peopleComposite, toolkit);
		addCCList(peopleComposite, toolkit);

		toolkit.paintBordersFor(peopleComposite);

		setControl(peopleComposite);
	}

	protected Label createLabel(Composite composite, RepositoryTaskAttribute attribute, FormToolkit toolkit) {
		Label label;
		if (getTaskEditorPage().getAttributeManager().hasOutgoingChanges(attribute)) {
			label = toolkit.createLabel(composite, "*" + attribute.getName());
		} else {
			label = toolkit.createLabel(composite, attribute.getName());
		}
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
		return label;
	}

	/**
	 * Utility method to create text field sets background to TaskListColorsAndFonts.COLOR_ATTRIBUTE_CHANGED if
	 * attribute has changed.
	 * 
	 * @param composite
	 * @param attribute
	 * @param style
	 * @param toolkit 
	 */
	protected Text createTextField(Composite composite, RepositoryTaskAttribute attribute, int style, FormToolkit toolkit) {
		String value;
		if (attribute == null || attribute.getValue() == null) {
			value = "";
		} else {
			value = attribute.getValue();
		}

		final Text text;
		if ((SWT.READ_ONLY & style) == SWT.READ_ONLY) {
			text = new Text(composite, style);
			toolkit.adapt(text, true, true);
			text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			text.setText(value);
		} else {
			text = toolkit.createText(composite, value, style);
		}

		if (attribute != null && !attribute.isReadOnly()) {
			text.setData(attribute);
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					String newValue = text.getText();
					RepositoryTaskAttribute attribute = (RepositoryTaskAttribute) text.getData();
					attribute.setValue(newValue);
					getTaskEditorPage().getAttributeManager().attributeChanged(attribute);
				}
			});
		}
		if (getTaskEditorPage().getAttributeManager().hasIncomingChanges(attribute)) {
			text.setBackground(getTaskEditorPage().getColorIncoming());
		}

		return text;
	}

}
