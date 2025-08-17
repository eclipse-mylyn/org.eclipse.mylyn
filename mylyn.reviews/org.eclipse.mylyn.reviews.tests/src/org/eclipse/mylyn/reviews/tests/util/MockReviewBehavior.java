/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tests.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.team.core.history.IFileRevision;

public class MockReviewBehavior extends ReviewBehavior {

	public MockReviewBehavior() {
		super(null);
	}

	@Override
	public IStatus addComment(IReviewItem fileItem, IComment comment, IProgressMonitor monitor) {
		return null;
	}

	@Override
	public IFileRevision getFileRevision(IFileVersion reviewFileVersion) {
		return null;
	}

	@Override
	public IStatus discardComment(IReviewItem fileItem, IComment comment, IProgressMonitor monitor) {
		return null;
	}

}