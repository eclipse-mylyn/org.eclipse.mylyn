/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.gitlab.ui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.identity.core.spi.ProfileImage;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.internal.gitlab.core.GitlabRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorSummaryPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.UserAttributeEditor;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class GitlabTaskEditorSummaryPart extends TaskEditorSummaryPart {

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {

		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = EditorUtil.createSectionClientLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 3;
		composite.setLayout(layout);

		TaskAttribute priorityAttribute = getTaskData().getRoot().getMappedAttribute(TaskAttribute.PRIORITY);
		final Control priorityEditor = addPriorityAttributeWithIcon(composite, toolkit, priorityAttribute, false);
		if (priorityEditor != null) {
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).span(1, 2).applyTo(priorityEditor);
			// forward focus to the summary editor
			priorityEditor.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					if (summaryEditor != null && summaryEditor.getControl() != null) {
						summaryEditor.getControl().setFocus();
						// only forward it on first view
						priorityEditor.removeFocusListener(this);
					}
				}
			});
			layout.numColumns++;
		}

		addSummaryText(composite, toolkit);

		if (Boolean.parseBoolean(getModel().getTaskRepository().getProperty(GitlabCoreActivator.AVANTAR))) {
			TaskAttribute userAssignedAttribute = getTaskData().getRoot()
					.getMappedAttribute(TaskAttribute.USER_ASSIGNED);
			if (userAssignedAttribute != null && !StringUtils.isBlank(userAssignedAttribute.getValue())) {
				UserAttributeEditor editor = new UserAttributeEditor(getModel(), userAssignedAttribute);
				editor.createControl(composite, toolkit);
				GridDataFactory.fillDefaults()
						.align(SWT.CENTER, SWT.CENTER)
						.span(1, 2)
						.indent(0, 2)
						.applyTo(editor.getControl());
				layout.marginRight = 1;
				layout.numColumns++;
				TaskAttribute avatarURL = userAssignedAttribute.getAttribute("avatar_url");

				if (avatarURL != null) {
					GitlabRepositoryConnector gitlabConnector = (GitlabRepositoryConnector) TasksUi
							.getRepositoryManager()
							.getRepositoryConnector(userAssignedAttribute.getTaskData().getConnectorKind());
					byte[] avatarBytes = gitlabConnector.getAvatarData(avatarURL.getValue());
					editor.updateImage(new ProfileImage(avatarBytes, 30, 30, ""));
				}
			}
		}

		if (needsHeader()) {
			createHeaderLayout(composite, toolkit);
		}

		toolkit.paintBordersFor(composite);

		setControl(composite);
	}
}
