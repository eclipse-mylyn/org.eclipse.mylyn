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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiConstants;
import org.eclipse.mylyn.internal.reviews.ui.views.ReviewExplorer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewGroup;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.ReviewsRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

/**
 * Marks task editor as providing Review model for extending classes.
 * 
 * @author Miles Parker
 */
public abstract class AbstractReviewTaskEditorPage extends AbstractTaskEditorPage {

	//TODO Much of this class will not be neccesary at all once we have workspace model support and notification working.
	private ReviewsRemoteFactoryProvider remoteFactory;

	private IReview review;

	private RemoteEmfConsumer<IReviewGroup, IReview, ?, String, String> consumer;

	public AbstractReviewTaskEditorPage(TaskEditor editor, String id, String label, String connectorKind) {
		super(editor, id, label, connectorKind);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		IReviewGroup group = ReviewsFactory.eINSTANCE.createReviewGroup();
		remoteFactory = createRemoteFactory();
		consumer = remoteFactory.getReviewFactory().consume("Manage", remoteFactory.getGroup(), getTask().getTaskId(),
				getTask().getTaskId(), new RemoteEmfConsumer.IObserver<IReview>() {

					public void created(IReview review) {
						AbstractReviewTaskEditorPage.this.review = review;
					}

					public void responded(boolean modified) {
					}

					public void failed(final IStatus status) {
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
				});
		consumer.request();
	}

	@Override
	public void refresh() {
		super.refresh();
		consumer.request();
	}

	public void refreshExplorer() {
		//TODO, we really shouldn't be updating the view directly, it should be listening for model change, but that needs to be implemented with care given EMF update and UI threading concerns, etc.
		IViewPart view = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.findView(ReviewsUiConstants.REVIEW_EXPLORER_ID);
		if (view instanceof ReviewExplorer) {
			((ReviewExplorer) view).refreshView();
		}
	}

	protected abstract ReviewsRemoteFactoryProvider createRemoteFactory();

	public ReviewsRemoteFactoryProvider getRemoteFactory() {
		return remoteFactory;
	}

	/**
	 * Returns the current review. All instances should provide one open, accessible review model instance at init time,
	 * and that review should be constant throughout the editor life-cycle.
	 */
	public IReview getReview() {
		return review;
	}

	@Override
	public void dispose() {
		super.dispose();
		remoteFactory.dispose();
	}
}
