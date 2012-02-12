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
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.tasks.core.ITask;

import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchLineComment;

/**
 * @author Steffen Pingel
 */
public class GerritReviewBehavior extends ReviewBehavior {

	public GerritReviewBehavior(ITask task) {
		super(task);
	}

	public GerritOperationFactory getOperationFactory() {
		return GerritUiPlugin.getDefault().getOperationFactory();
	}

	@Override
	public IStatus addTopic(IReviewItem item, ITopic topic, IProgressMonitor monitor) {
		short side = 1;
		String id = item.getId();
		if (id.startsWith("base-")) {
			// base revision
			id = id.substring(5);
			side = 0;
		}
		Patch.Key key = Patch.Key.parse(id);
		SaveDraftRequest request = new SaveDraftRequest(key, ((ILineLocation) topic.getLocation()).getTotalMin(), side);
		request.setMessage(topic.getDescription());

		GerritOperation<PatchLineComment> operation = getOperationFactory().createSaveDraftOperation(getTask(), request);
		return operation.run(monitor);
	}

}
