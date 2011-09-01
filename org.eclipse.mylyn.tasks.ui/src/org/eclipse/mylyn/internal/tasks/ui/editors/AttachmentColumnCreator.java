/*******************************************************************************
 * Copyright (c) 2011 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImageManger;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public class AttachmentColumnCreator extends AttachmentColumnDefinition {
	private final CommonImageManger imageManager = new CommonImageManger();

	public AttachmentColumnCreator(int index) {
		super(index, 100, "Creator", SWT.LEFT, false, SWT.NONE);
	}

	@Override
	public Image getColumnImage(ITaskAttachment attachment, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		if (attachment.getAuthor() != null) {
			return getAuthorImage(attachment.getAuthor(), attachment.getTaskRepository());
		}
		return null;
	}

	/**
	 * Get author image for a specified repository person and task repository
	 * 
	 * @param person
	 * @param repository
	 * @return author image
	 */
	protected Image getAuthorImage(IRepositoryPerson person, TaskRepository repository) {
		if (repository != null && person != null && person.getPersonId().equals(repository.getUserName())) {
			return imageManager.getImage(CommonImages.PERSON_ME);
		} else {
			return imageManager.getImage(CommonImages.PERSON);
		}
	}

	@Override
	public String getColumnText(ITaskAttachment attachment, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		return (attachment.getAuthor() != null) ? attachment.getAuthor().toString() : ""; //$NON-NLS-1$
	}

	@Override
	public int compare(TableViewer viewer, ITaskAttachment attachment1, ITaskAttachment attachment2, int columnIndex) {
		Assert.isTrue(columnIndex == getIndex());
		return compare(attachment1.getAuthor().toString(), attachment2.getAuthor().toString());
	}

}
