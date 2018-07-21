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

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getHyperlinkText() {
		return MessageFormat.format(Messages.TaskAttachmentTableEditorHyperlink_Show_Attachment_X_in_Y, attachmentId);
	}

	public String getTypeLabel() {
		return null;
	}

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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attachmentId == null) ? 0 : attachmentId.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + ((repository == null) ? 0 : repository.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TaskAttachmentTableEditorHyperlink other = (TaskAttachmentTableEditorHyperlink) obj;
		if (attachmentId == null) {
			if (other.attachmentId != null) {
				return false;
			}
		} else if (!attachmentId.equals(other.attachmentId)) {
			return false;
		}
		if (region == null) {
			if (other.region != null) {
				return false;
			}
		} else if (!region.equals(other.region)) {
			return false;
		}
		if (repository == null) {
			if (other.repository != null) {
				return false;
			}
		} else if (!repository.equals(other.repository)) {
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
		if (editorPart instanceof TaskEditor) {
			TaskEditor taskEditor = (TaskEditor) editorPart;
			IFormPage formPage = taskEditor.getActivePageInstance();
			if (formPage instanceof AbstractTaskEditorPage) {
				taskEditorPage = (AbstractTaskEditorPage) formPage;
			}
		}
		return taskEditorPage;
	}

}
