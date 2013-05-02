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
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.core.spi.remote.review.ReviewRemoteFactory;
import org.eclipse.mylyn.reviews.spi.edit.remote.review.ReviewsRemoteEditFactoryProvider;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

/**
 * Marks task editor as providing Review model for extending classes.
 * 
 * @author Miles Parker
 */
public abstract class AbstractReviewTaskEditorPage extends AbstractTaskEditorPage {

	private IReviewRemoteFactoryProvider factoryProvider;

	private final ReviewRemoteFactory.Client emfClient = new ReviewRemoteFactory.Client() {
		@Override
		protected boolean isClientReady() {
			return getManagedForm() != null && !getManagedForm().getForm().isDisposed();
		}

		@Override
		protected void rebuild() {
			//Prevent CME from observer and allow other UI processes time to execute
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					AbstractReviewTaskEditorPage.super.refresh();
				}
			});
			updateSets();
		}

		@Override
		public void failed(IRepository parent, IReview object, final IStatus status) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Error loading task", status.getException())); //$NON-NLS-1$
			getTaskEditor().setMessage(
					org.eclipse.mylyn.internal.tasks.ui.editors.Messages.AbstractTaskEditorPage_Error_opening_task,
					IMessageProvider.ERROR, new HyperlinkAdapter() {
						@Override
						public void linkActivated(HyperlinkEvent event) {
							TasksUiInternal.displayStatus(
									org.eclipse.mylyn.internal.tasks.ui.editors.Messages.AbstractTaskEditorPage_Open_failed,
									status);
						}
					});
		}
	};

	public AbstractReviewTaskEditorPage(TaskEditor editor, String id, String label, String connectorKind) {
		super(editor, id, label, connectorKind);
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) {
		AbstractReviewTaskEditorPage.super.init(site, input);
		final RemoteEmfConsumer<IRepository, IReview, String, ?, ?, Date> reviewConsumer = getFactoryProvider().getReviewFactory()
				.getConsumerForLocalKey(getFactoryProvider().getRoot(), getTask().getTaskId());
		emfClient.setConsumer(reviewConsumer);
		reviewConsumer.open();
	}

	@Override
	public void refresh() {
		//We defer the actual refresh until the model is also refreshed. See updated method.
		emfClient.requestUpdate(true, true);
	}

	public IReviewRemoteFactoryProvider getFactoryProvider() {
		if (factoryProvider == null) {
			factoryProvider = ReviewsUiPlugin.getDefault().getFactoryProvider(getConnectorKind(), getTaskRepository());
		}
		return factoryProvider;
	}

	@Override
	public void dispose() {
		IReviewRemoteFactoryProvider provider = getFactoryProvider();
		if (provider instanceof ReviewsRemoteEditFactoryProvider) {
			ReviewsRemoteEditFactoryProvider reviewsProvider = (ReviewsRemoteEditFactoryProvider) provider;
			reviewsProvider.close(emfClient.getConsumer().getModelObject());
			reviewsProvider.save();
		}
		emfClient.dispose();
		super.dispose();
	}

	private void updateSets() {
		for (IReviewItemSet set : emfClient.getConsumer().getModelObject().getSets()) {
			if (set.getItems() != null) {
				RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, String, ?, ?, Long> contentConsumer = getFactoryProvider().getReviewItemSetContentFactory()
						.getConsumerForModel(set, set.getItems());
				//Don't retrieve items that have not already been explicitly requested by user..
				if (contentConsumer != null && contentConsumer.getModelObject() != null
						&& !contentConsumer.getModelObject().isEmpty()) {
					contentConsumer.retrieve(true);
				}
			}
		}
	}
}
