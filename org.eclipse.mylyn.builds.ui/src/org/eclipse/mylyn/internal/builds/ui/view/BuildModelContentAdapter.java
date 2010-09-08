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

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;

/**
 * Observes elements relevant to the Builds view.
 * 
 * @author Steffen Pingel
 */
public abstract class BuildModelContentAdapter extends EContentAdapter {

	@Override
	public void notifyChanged(Notification msg) {
		super.notifyChanged(msg);
		// ignore adapter removals
		if (msg.getEventType() == Notification.REMOVING_ADAPTER) {
			return;
		}
		// XXX improve check to only notify if model changed
		if (msg.getOldValue() != msg.getNewValue()) {
			doNotifyChanged(msg);
		}
	}

	protected abstract void doNotifyChanged(Notification msg);

	@Override
	protected void addAdapter(Notifier notifier) {
		if (observing(notifier)) {
			super.addAdapter(notifier);
		}
	}

	protected boolean observing(Notifier notifier) {
		return notifier instanceof IBuildServer || notifier instanceof IBuildPlan || notifier instanceof IBuildModel;
	}

	@Override
	protected void removeAdapter(Notifier notifier) {
		if (observing(notifier)) {
			notifier.eAdapters().remove(this);
		}
	}

}