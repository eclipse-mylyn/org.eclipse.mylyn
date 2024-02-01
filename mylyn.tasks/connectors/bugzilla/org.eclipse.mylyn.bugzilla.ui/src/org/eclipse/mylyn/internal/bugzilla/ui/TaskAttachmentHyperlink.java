/*******************************************************************************
 * Copyright (c) 2009, 2011 Frank Becker and others.
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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @since 3.2
 */
public final class TaskAttachmentHyperlink implements IHyperlink {

	private final IRegion region;

	private final TaskRepository repository;

	private final String attachmentId;

	public TaskAttachmentHyperlink(IRegion region, TaskRepository repository, String attachmentId) {
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
		return MessageFormat.format(Messages.TaskAttachmentHyperlink_Open_Attachment_X_in_Y, attachmentId,
				repository.getRepositoryLabel());
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public void open() {
		String url = repository.getUrl() + IBugzillaConstants.URL_GET_ATTACHMENT_SUFFIX + attachmentId;
		TasksUiUtil.openUrl(url);
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
		TaskAttachmentHyperlink other = (TaskAttachmentHyperlink) obj;
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

}
