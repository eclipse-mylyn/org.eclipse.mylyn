/*******************************************************************************
 * Copyright (c) 2010, 2011 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.ui;

import java.text.MessageFormat;
import java.util.Objects;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @since 3.2
 */
public final class TaskAttachmentTableEditorHyperlink implements IHyperlink {

	private final IRegion region;

	private final TaskRepository repository;

	private final String attachmentId;

	public TaskAttachmentTableEditorHyperlink(IRegion region, TaskRepository repository, String attachmentId) {
		Assert.isNotNull(repository);
		this.region = region;
		this.repository = repository;
		this.attachmentId = attachmentId;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getHyperlinkText() {
		return MessageFormat.format(Messages.TaskAttachmentTableEditorHyperlink_Show_Attachment_X_in_Y, attachmentId);
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public void open() {
		AbstractTaskEditorPage page = getTaskEditorPage();
		if (page != null) {
			if (!page.selectReveal(TaskAttribute.PREFIX_ATTACHMENT + attachmentId)) {
				String msg = NLS.bind(Messages.TaskAttachmentTableEditorHyperlink_QuestionMsg, attachmentId);
				if (MessageDialog.openQuestion(WorkbenchUtil.getShell(),
						Messages.TaskAttachmentTableEditorHyperlink_AttachmentNotFound, msg)) {
					String url = repository.getUrl() + IBugzillaConstants.URL_GET_ATTACHMENT_SUFFIX + attachmentId;
					TasksUiUtil.openUrl(url);
				}
			}
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(attachmentId, region, repository);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		TaskAttachmentTableEditorHyperlink other = (TaskAttachmentTableEditorHyperlink) obj;
		if (!Objects.equals(attachmentId, other.attachmentId)) {
			return false;
		}
		if (!Objects.equals(region, other.region)) {
			return false;
		}
		if (!Objects.equals(repository, other.repository)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TaskAttachmentHyperlink [attachmentId=" + attachmentId + ", region=" + region + ", repository=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ repository + "]"; //$NON-NLS-1$
	}

	protected AbstractTaskEditorPage getTaskEditorPage() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null) {
			return null;
		}
		IEditorPart editorPart = activePage.getActiveEditor();
		AbstractTaskEditorPage taskEditorPage = null;
		if (editorPart instanceof TaskEditor taskEditor) {
			IFormPage formPage = taskEditor.getActivePageInstance();
			if (formPage instanceof AbstractTaskEditorPage) {
				taskEditorPage = (AbstractTaskEditorPage) formPage;
			}
		}
		return taskEditorPage;
	}

}
