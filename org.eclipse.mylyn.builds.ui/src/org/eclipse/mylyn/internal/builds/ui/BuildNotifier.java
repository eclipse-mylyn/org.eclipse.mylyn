/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import java.util.Collections;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.commons.ui.notifications.Notifications;
import org.eclipse.mylyn.internal.builds.ui.view.BuildModelContentAdapter;

/**
 * @author Steffen Pingel
 */
public class BuildNotifier {

	private final Adapter modelListener = new BuildModelContentAdapter() {
		@Override
		public void doNotifyChanged(Notification msg) {
			handle(msg);
		}
	};

	public BuildNotifier() {

	}

	protected void handle(Notification msg) {
		BuildNotification notification = BuildNotification.createNotification(msg);
		if (notification != null) {
			Notifications.getService().notify(Collections.singletonList(notification));
		}
	}

	public void register(BuildModel model) {
		model.eAdapters().add(modelListener);
	}

	public void unregister(BuildModel model) {
		model.eAdapters().remove(modelListener);
	}

}
