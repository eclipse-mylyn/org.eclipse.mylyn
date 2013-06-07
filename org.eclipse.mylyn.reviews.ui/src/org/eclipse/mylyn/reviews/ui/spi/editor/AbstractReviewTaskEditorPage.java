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
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.IRemoteEmfObserver;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
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
public abstract class AbstractReviewTaskEditorPage extends AbstractTaskEditorPage implements
		IRemoteEmfObserver<IRepository, IReview> {

	private RemoteEmfConsumer<IRepository, IReview, ?, String, String> consumer;

	private IReviewRemoteFactoryProvider factoryProvider;

	private boolean intialRefreshRequested;

	private boolean refreshRequested;

	public AbstractReviewTaskEditorPage(TaskEditor editor, String id, String label, String connectorKind) {
		super(editor, id, label, connectorKind);
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) {
		AbstractReviewTaskEditorPage.super.init(site, input);
		if (getFactoryProvider() != null) {
			consumer = getFactoryProvider().getReviewFactory().getConsumerForRemoteKey(getFactoryProvider().getRoot(),
					getTask().getTaskId());
			consumer.addObserver(AbstractReviewTaskEditorPage.this);
			intialRefreshRequested = true;
			consumer.retrieve(false);
		}
	}

	@Override
	public void refresh() {
		refreshRequested = true;
		//We defer the actual refresh until the model is also refreshed. See updated method.
		consumer.retrieve(true);
	}

	public IReviewRemoteFactoryProvider getFactoryProvider() {
		if (factoryProvider == null) {
			factoryProvider = ReviewsUiPlugin.getDefault().getFactoryProvider(getConnectorKind(), getTaskRepository());
		}
		return factoryProvider;
	}

	public void created(IRepository parent, IReview object) {
		//ignore
	}

	public void updating(IRepository parent, IReview object) {
		//ignore
	}

	public void updated(IRepository parent, IReview object, boolean modified) {
		if (refreshRequested || (modified && !intialRefreshRequested)) {
			//Prevent CME from observer and allow other UI processes time to execute
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					AbstractReviewTaskEditorPage.super.refresh();
				}
			});
			refreshRequested = false;
		}
		intialRefreshRequested = false;
	}

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
}
