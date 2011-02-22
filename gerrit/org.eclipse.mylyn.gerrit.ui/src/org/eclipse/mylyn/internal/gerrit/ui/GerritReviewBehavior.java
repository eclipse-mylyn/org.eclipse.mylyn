/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.gerrit.core.GerritOperationFactory;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.core.operations.SaveDraftRequest;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.tasks.core.ITask;

import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchLineComment;

/**
 * @author Steffen Pingel
 */
public class GerritReviewBehavior extends ReviewBehavior {

	private final IFileItem fileItem;

	public GerritReviewBehavior(ITask task, IFileItem fileItem) {
		super(task);
		this.fileItem = fileItem;
	}

	public GerritOperationFactory getOperationFactory() {
		return GerritUiPlugin.getDefault().getOperationFactory();
	}

	@Override
	public IStatus addTopic(ITopic topic, IProgressMonitor monitor) {
		Patch.Key key = Patch.Key.parse(fileItem.getId());
		short side = (topic.getItem() == fileItem.getBase()) ? (short) 0 : (short) 1;
		SaveDraftRequest request = new SaveDraftRequest(key, ((ILineLocation) topic.getLocation()).getTotalMin(), side);
		request.setMessage(topic.getDescription());

		GerritOperation<PatchLineComment> operation = getOperationFactory().createSaveDraftOperation(getTask(), request);
		return operation.run(monitor);
	}
}
