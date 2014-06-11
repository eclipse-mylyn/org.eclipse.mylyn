/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sebastien Dubois (Ericsson) - Improvements for Bug 400266
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.team.core.history.IFileRevision;

/**
 * @author Steffen Pingel
 * @author Guy Perron
 */
public abstract class ReviewBehavior {

	private final ITask task;

	public ReviewBehavior(ITask task) {
		this.task = task;
	}

	public ITask getTask() {
		return task;
	}

	public abstract IStatus addComment(IReviewItem fileItem, IComment comment, IProgressMonitor monitor);

	public abstract IStatus discardComment(IReviewItem fileItem, IComment comment, IProgressMonitor monitor);

	public abstract IFileRevision getFileRevision(IFileVersion reviewFileVersion);
}
