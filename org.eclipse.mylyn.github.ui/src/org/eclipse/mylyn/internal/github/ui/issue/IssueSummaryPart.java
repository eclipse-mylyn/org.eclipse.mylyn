/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.issue;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.internal.github.core.issue.IssueAttribute;
import org.eclipse.mylyn.internal.github.ui.AvatarLabel;
import org.eclipse.mylyn.internal.github.ui.GitHubUi;
import org.eclipse.mylyn.internal.tasks.ui.editors.DateAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorSummaryPart;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Issue summary task editor part modeled after {@link TaskEditorSummaryPart}
 * but displaying reporter and assignee avatar images.
 */
public class IssueSummaryPart extends AbstractTaskEditorPart {

	/**
	 * AVATAR_SIZE
	 */
	public static final int AVATAR_SIZE = 48;

	private AbstractAttributeEditor summaryEditor;

	private String reporterAvatarId;

	private String assigneeAvatarId;

	/**
	 * Create issue summary part
	 * 
	 * @param reporterAvatarId
	 * @param assigneeAvatarId
	 */
	public IssueSummaryPart(String reporterAvatarId, String assigneeAvatarId) {
		setPartName("Summary"); //$NON-NLS-1$
		this.reporterAvatarId = reporterAvatarId;
		this.assigneeAvatarId = assigneeAvatarId;
	}

	private void addAttribute(Composite composite, FormToolkit toolkit,
			TaskAttribute attribute) {
		addAttribute(composite, toolkit, attribute,
				EditorUtil.HEADER_COLUMN_MARGIN);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit,
			TaskAttribute attribute, int indent) {
		addAttribute(composite, toolkit, attribute, indent, true);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit,
			TaskAttribute attribute, int indent, boolean showLabel) {
		AbstractAttributeEditor editor = createAttributeEditor(attribute);
		if (editor == null)
			return;

		editor.setReadOnly(true);
		editor.setDecorationEnabled(false);

		if (showLabel) {
			editor.createLabelControl(composite, toolkit);
			GridDataFactory.defaultsFor(editor.getLabelControl())
					.indent(indent, 0).applyTo(editor.getLabelControl());
		}

		if (isAttribute(attribute, TaskAttribute.DATE_MODIFICATION)
				&& editor instanceof DateAttributeEditor)
			((DateAttributeEditor) editor).setShowTime(true);

		editor.createControl(composite, toolkit);
		getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
	}

	private boolean isAttribute(TaskAttribute attribute, String id) {
		return attribute
				.getId()
				.equals(attribute.getTaskData().getAttributeMapper()
						.mapToRepositoryKey(attribute.getParentAttribute(), id));
	}

	private void addSummaryText(Composite composite, final FormToolkit toolkit) {
		TaskAttribute summaryAttrib = getTaskData().getRoot()
				.getMappedAttribute(TaskAttribute.SUMMARY);
		summaryEditor = createAttributeEditor(summaryAttrib);
		if (summaryEditor == null)
			return;

		if (summaryAttrib.getMetaData().isReadOnly())
			summaryEditor.setReadOnly(true);

		if (summaryEditor instanceof RichTextAttributeEditor) {
			Composite roundedBorder = EditorUtil.createBorder(composite,
					toolkit, !summaryEditor.isReadOnly());
			summaryEditor.createControl(roundedBorder, toolkit);
			EditorUtil.setHeaderFontSizeAndStyle(summaryEditor.getControl());
		} else {
			final Composite border = toolkit.createComposite(composite);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING)
					.hint(EditorUtil.MAXIMUM_WIDTH, SWT.DEFAULT)
					.grab(true, false).applyTo(border);
			GridLayoutFactory.fillDefaults().margins(1, 4).applyTo(border);
			summaryEditor.createControl(border, toolkit);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER)
					.grab(true, false).applyTo(summaryEditor.getControl());
			toolkit.paintBordersFor(border);
		}
		getTaskEditorPage().getAttributeEditorToolkit().adapt(summaryEditor);
	}

	private TaskAttribute getAttribute(IssueAttribute attribute) {
		return getAttribute(attribute.getMetadata().getId());
	}

	private TaskAttribute getAttribute(String id) {
		return getTaskData().getRoot().getAttribute(id);
	}

	private boolean addAvatarPart(Composite parent, FormToolkit toolkit,
			TaskAttribute avatarAttribute, IRepositoryPerson person) {
		if (avatarAttribute == null || avatarAttribute.getValue().length() == 0)
			return false;

		AvatarLabel label = new AvatarLabel(GitHubUi.getDefault().getStore(),
				person, avatarAttribute);
		label.create(parent, toolkit);
		return true;
	}

	/**
	 * Create control
	 * 
	 * @param parent
	 * @param toolkit
	 */
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = EditorUtil.createSectionClientLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 3;
		composite.setLayout(layout);

		TaskAttribute reporter = getAttribute(TaskAttribute.USER_REPORTER);
		if (reporter != null) {
			IRepositoryPerson person = getTaskData().getAttributeMapper()
					.getRepositoryPerson(reporter);
			if (reporterAvatarId != null
					&& addAvatarPart(composite, toolkit,
							getAttribute(reporterAvatarId), person))
				layout.numColumns++;
		}
		addSummaryText(composite, toolkit);

		TaskAttribute assignee = getAttribute(IssueAttribute.ASSIGNEE);
		if (assignee != null) {
			IRepositoryPerson person = getTaskData().getAttributeMapper()
					.getRepositoryPerson(assignee);
			if (this.assigneeAvatarId != null
					&& addAvatarPart(composite, toolkit,
							getAttribute(this.assigneeAvatarId), person))
				layout.numColumns++;
		}

		if (needsHeader())
			createHeaderLayout(composite, toolkit);

		toolkit.paintBordersFor(composite);
		setControl(composite);
	}

	private Composite createHeaderLayout(Composite composite,
			FormToolkit toolkit) {
		Composite headerComposite = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		headerComposite.setLayout(layout);
		GridDataFactory.fillDefaults().grab(true, false)
				.applyTo(headerComposite);

		TaskAttribute root = getTaskData().getRoot();

		addAttribute(headerComposite, toolkit,
				root.getMappedAttribute(TaskAttribute.STATUS), 0);

		addAttribute(headerComposite, toolkit,
				root.getMappedAttribute(TaskAttribute.DATE_CREATION));

		addAttribute(headerComposite, toolkit,
				root.getMappedAttribute(TaskAttribute.DATE_MODIFICATION));

		// ensure layout does not wrap
		layout.numColumns = headerComposite.getChildren().length;

		// ensure that the composite does not show a bunch of blank space
		if (layout.numColumns == 0) {
			layout.numColumns = 1;
			toolkit.createLabel(headerComposite, " "); //$NON-NLS-1$
		}
		return headerComposite;
	}

	private boolean needsHeader() {
		return !getTaskData().isNew();
	}

	/**
	 * @see org.eclipse.ui.forms.AbstractFormPart#setFocus()
	 */
	public void setFocus() {
		if (summaryEditor != null)
			summaryEditor.getControl().setFocus();
	}
}
