/*********************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.reviews.ui;

import org.eclipse.mylyn.commons.workbench.CommonImageManger;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteService;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactoryProvider;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.factories.UiDataLocator;
import org.eclipse.mylyn.reviews.ui.spi.remote.RemoteUiService;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ReviewsUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.ui"; //$NON-NLS-1$

	private static ReviewsUiPlugin plugin;

	CommonImageManger imageManager;

	AbstractRemoteService service;

	public ReviewsUiPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		imageManager = new CommonImageManger();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		imageManager.dispose();
	}

	public static ReviewsUiPlugin getDefault() {
		return plugin;
	}

	public CommonImageManger getImageManager() {
		return imageManager;
	}

	public IReviewRemoteFactoryProvider getFactoryProvider(String connectorKind, TaskRepository repository) {
		ReviewsConnector connector = (ReviewsConnector) TasksUi.getRepositoryConnector(connectorKind);
		AbstractRemoteEmfFactoryProvider<IRepository, IReview> factoryProvider = connector.getFactoryProvider(repository);
		if (service == null) {
			service = new RemoteUiService();
		}
		factoryProvider.setService(service);
		if (!(factoryProvider.getDataLocator() instanceof UiDataLocator)) {
			factoryProvider.setDataLocator(new UiDataLocator());
		}
		factoryProvider.open();
		if (factoryProvider instanceof IReviewRemoteFactoryProvider) {
			return (IReviewRemoteFactoryProvider) factoryProvider;
		}
		throw new RuntimeException("The connector factory propvider must implement IReviewRemoteFactoryProvider");
	}
}
