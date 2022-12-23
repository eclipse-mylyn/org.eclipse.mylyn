/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies, Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.editor;

import java.io.File;
import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsImages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfObserver;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.spi.edit.remote.AbstractRemoteEditFactoryProvider;
import org.eclipse.mylyn.reviews.spi.edit.remote.review.ReviewsRemoteEditFactoryProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
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

	private RemoteEmfConsumer<IRepository, IReview, String, ?, ?, Date> reviewConsumer;

	private final RemoteEmfObserver<IRepository, IReview, String, Date> reviewObserver = new RemoteEmfObserver<IRepository, IReview, String, Date>() {
		@Override
		public void updating() {
			updateMessage();
		}

		@Override
		public void updated(boolean modified) {
			updateMessage();
		}
	};

	public AbstractReviewTaskEditorPage(TaskEditor editor, String id, String label, String connectorKind) {
		super(editor, id, label, connectorKind);
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) {
		AbstractReviewTaskEditorPage.super.init(site, input);

		checkIfModelIsCached();

		reviewConsumer = getFactoryProvider().getReviewFactory()
				.getConsumerForLocalKey(getFactoryProvider().getRoot(), getTask().getTaskId());
		reviewConsumer.addObserver(reviewObserver);
		reviewConsumer.open();
		if (reviewConsumer.getRemoteObject() == null) {
			reviewConsumer.retrieve(false);
		}
	}

	private void checkIfModelIsCached() {
		AbstractRemoteEditFactoryProvider factoryProvider = (AbstractRemoteEditFactoryProvider) getFactoryProvider();
		String reviewPath = factoryProvider.getDataLocator()
				.getFilePath(factoryProvider.getContainerSegment(), "Review", getTask().getTaskId(), "reviews") //$NON-NLS-1$ //$NON-NLS-2$
				.toOSString();
		if (!new File(reviewPath).exists()) {
			getTaskEditor().setMessage(
					org.eclipse.mylyn.internal.tasks.ui.editors.Messages.AbstractTaskEditorPage_Synchronize_to_retrieve_task_data,
					IMessageProvider.WARNING, new HyperlinkAdapter() {
						@Override
						public void linkActivated(HyperlinkEvent e) {
							SynchronizeEditorAction synchronizeEditorAction = new SynchronizeEditorAction();
							synchronizeEditorAction.selectionChanged(new StructuredSelection(getTaskEditor()));
							if (synchronizeEditorAction != null) {
								synchronizeEditorAction.run();
							}
						}
					});
		}
	}

	@Override
	public void refresh() {
		super.refresh();
		updateMessage();
	}

	private void updateMessage() {
		if (!reviewConsumer.getStatus().isOK()) {
			getTaskEditor().setMessage(
					org.eclipse.mylyn.internal.tasks.ui.editors.Messages.AbstractTaskEditorPage_Error_opening_task,
					IMessageProvider.ERROR, new HyperlinkAdapter() {
						@Override
						public void linkActivated(HyperlinkEvent event) {
							TasksUiInternal.displayStatus(
									org.eclipse.mylyn.internal.tasks.ui.editors.Messages.AbstractTaskEditorPage_Open_failed,
									reviewConsumer.getStatus());
						}
					});
		}
	}

	public IReviewRemoteFactoryProvider getFactoryProvider() {
		// obtain from editor input instead of calling getTaskRepository() to avoid NPE when task model could not be loaded
		// note that this would not return the expected result for unsubmitted tasks which is not supported for reviews
		TaskRepository repository = getTaskEditor().getTaskEditorInput().getTaskRepository();
		ReviewsConnector connector = (ReviewsConnector) TasksUi.getRepositoryConnector(repository.getConnectorKind());
		return (IReviewRemoteFactoryProvider) connector.getReviewClient(repository).getFactoryProvider();
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

	@Override
	public void fillToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(new Separator());
		toolBarManager
				.add(new Action(Messages.AbstractReviewTaskEditorPage_Show_Review_Navigator, ReviewsImages.REVIEW) {
					@Override
					public void run() {
						WorkbenchUtil.showViewInActiveWindow(ReviewsUiConstants.REVIEW_EXPLORER_ID);
					}
				});
		super.fillToolBar(toolBarManager);
	}
}
