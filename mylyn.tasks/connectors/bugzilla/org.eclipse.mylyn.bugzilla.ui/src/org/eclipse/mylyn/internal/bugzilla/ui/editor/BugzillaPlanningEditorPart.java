/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Rob Elves
 */
public class BugzillaPlanningEditorPart extends AbstractTaskEditorPart {

	private boolean hasIncoming;

	private static final Set<BugzillaAttribute> PLANNING_ATTRIBUTES = EnumSet.of(BugzillaAttribute.ACTUAL_TIME,
			BugzillaAttribute.ESTIMATED_TIME, BugzillaAttribute.WORK_TIME, BugzillaAttribute.REMAINING_TIME,
			BugzillaAttribute.DEADLINE);

	public BugzillaPlanningEditorPart() {
		setPartName(Messages.BugzillaPlanningEditorPart_Team_Planning);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		initialize();
		int style = ExpandableComposite.TWISTIE;
		if (hasIncoming) {
			style |= ExpandableComposite.EXPANDED;
		}
		Section timeSection = createSection(parent, toolkit, style);
		EditorUtil.setTitleBarForeground(timeSection, toolkit.getColors().getColor(IFormColors.TITLE));

		GridLayout gl = new GridLayout();
		GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.horizontalSpan = 4;
		timeSection.setLayout(gl);
		timeSection.setLayoutData(gd);

		Composite timeComposite = toolkit.createComposite(timeSection);
		gl = new GridLayout(6, false);
		timeComposite.setLayout(gl);
		gd = new GridData();
		gd.horizontalSpan = 4;
		timeComposite.setLayoutData(gd);

		TaskAttribute attribute = getTaskData().getRoot().getMappedAttribute(BugzillaAttribute.DEADLINE.getKey());
		if (attribute != null) {
			AbstractAttributeEditor attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(timeComposite, toolkit);
			attributeEditor.createControl(timeComposite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(attributeEditor);
		}

		attribute = getTaskData().getRoot().getMappedAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey());
		AbstractAttributeEditor attributeEditor = createAttributeEditor(attribute);
		attributeEditor.createLabelControl(timeComposite, toolkit);
		attributeEditor.createControl(timeComposite, toolkit);
		getTaskEditorPage().getAttributeEditorToolkit().adapt(attributeEditor);

		Label label = toolkit.createLabel(timeComposite, Messages.BugzillaPlanningEditorPart_Current_Estimate);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		float total = 0;
		try {
			TaskAttribute attrActualTime = getTaskData().getRoot()
					.getMappedAttribute(BugzillaAttribute.ACTUAL_TIME.getKey());
			float actual = 0;
			if (attrActualTime != null) {
				actual = Float.parseFloat(attrActualTime.getValue());
			}
			TaskAttribute attrRemainingTime = getTaskData().getRoot()
					.getMappedAttribute(BugzillaAttribute.REMAINING_TIME.getKey());
			float remaining = 0;
			if (attrRemainingTime != null) {
				remaining = Float.parseFloat(attrRemainingTime.getValue());
			}
			total = actual + remaining;
		} catch (Exception e) {
			// ignore NumberFormatException
		}

		Text currentEstimate = toolkit.createText(timeComposite, "" + total, SWT.FLAT | SWT.READ_ONLY); //$NON-NLS-1$
		currentEstimate.setFont(TEXT_FONT);
		toolkit.adapt(currentEstimate, false, false);
		currentEstimate.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
		currentEstimate.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		attribute = getTaskData().getRoot().getMappedAttribute(BugzillaAttribute.ACTUAL_TIME.getKey());
		if (attribute != null) {
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(timeComposite, toolkit);
			attributeEditor.createControl(timeComposite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(attributeEditor);
		}

		// Add Time
		TaskAttribute addTimeAttribute = getTaskData().getRoot()
				.getMappedAttribute(BugzillaAttribute.WORK_TIME.getKey());
		if (addTimeAttribute == null) {
			addTimeAttribute = BugzillaTaskDataHandler.createAttribute(getTaskData(), BugzillaAttribute.WORK_TIME);

		}
		if (addTimeAttribute != null) {
			attributeEditor = createAttributeEditor(addTimeAttribute);
			attributeEditor.createLabelControl(timeComposite, toolkit);
			attributeEditor.createControl(timeComposite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(attributeEditor);
		}

		attribute = getTaskData().getRoot().getAttribute(BugzillaAttribute.REMAINING_TIME.getKey());
		if (attribute != null) {
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(timeComposite, toolkit);
			attributeEditor.createControl(timeComposite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(attributeEditor);
		}

		timeSection.setClient(timeComposite);
		toolkit.paintBordersFor(timeComposite);
		setSection(toolkit, timeSection);

	}

	private void initialize() {
		hasIncoming = false;
		Map<String, TaskAttribute> attributes = getTaskData().getRoot().getAttributes();
		for (TaskAttribute attribute : attributes.values()) {

			BugzillaAttribute bugzillaAttribute = BugzillaAttribute.UNKNOWN;
			try {
				bugzillaAttribute = BugzillaAttribute.valueOf(attribute.getId().trim().toUpperCase(Locale.ENGLISH));
			} catch (RuntimeException e) {
				if (e instanceof IllegalArgumentException) {
					// ignore unrecognized tags
					continue;
				}
				throw e;
			}

			if (PLANNING_ATTRIBUTES.contains(bugzillaAttribute)) {
				if (getModel().hasIncomingChanges(attribute)) {
					hasIncoming = true;
				}
			}
		}
	}
}
