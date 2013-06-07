/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies, Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.editor;

import java.util.Date;

import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfObserver;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.spi.edit.remote.review.ReviewsRemoteEditFactoryProvider;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;

/**
 * Marks task editor as providing Review model for extending classes.
 * 
 * @author Miles Parker
 */
public abstract class AbstractReviewTaskEditorPage extends AbstractTaskEditorPage {

	private RemoteEmfConsumer<IRepository, IReview, String, ?, ?, Date> reviewConsumer;

	private final RemoteEmfObserver<IRepository, IReview, String, Date> reviewObserver = new RemoteEmfObserver<IRepository, IReview, String, Date>();

	public AbstractReviewTaskEditorPage(TaskEditor editor, String id, String label, String connectorKind) {
		super(editor, id, label, connectorKind);
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) {
		AbstractReviewTaskEditorPage.super.init(site, input);
		reviewConsumer = getFactoryProvider().getReviewFactory().getConsumerForLocalKey(getFactoryProvider().getRoot(),
				getTask().getTaskId());
		reviewConsumer.addObserver(reviewObserver);
		reviewConsumer.open();
	}

	public IReviewRemoteFactoryProvider getFactoryProvider() {
		return (IReviewRemoteFactoryProvider) ((ReviewsConnector) getConnector()).getReviewClient(getTaskRepository())
				.getFactoryProvider();
	}

	@Override
	public void dispose() {
		IReviewRemoteFactoryProvider provider = getFactoryProvider();
		if (provider instanceof ReviewsRemoteEditFactoryProvider) {
			ReviewsRemoteEditFactoryProvider reviewsProvider = (ReviewsRemoteEditFactoryProvider) provider;
			reviewsProvider.save(getReview());
			reviewObserver.dispose();
		}
		super.dispose();
	}

	public IReview getReview() {
		return reviewConsumer.getModelObject();
	}
}
