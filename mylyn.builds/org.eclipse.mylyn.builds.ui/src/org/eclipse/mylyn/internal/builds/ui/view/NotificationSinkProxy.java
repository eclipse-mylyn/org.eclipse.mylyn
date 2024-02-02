/*******************************************************************************
 * Copyright (c) 2010, 2011 Itema AS and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Itema AS - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.mylyn.commons.notifications.core.NotificationSink;
import org.eclipse.mylyn.commons.notifications.core.NotificationSinkEvent;
import org.eclipse.mylyn.internal.builds.ui.notifications.BuildsServiceMessageControl;

/**
 * The notification mechanism should never create instances of the builds view when notifications are being sent. Hence we're using this
 * proxy in between.
 * 
 * @author Torkild U. Resheim
 */
public class NotificationSinkProxy extends NotificationSink {

	private static BuildsServiceMessageControl control;

	public NotificationSinkProxy() {
	}

	@Override
	public void notify(NotificationSinkEvent event) {
		if (control != null) {
			control.notify(event);
		}
	}

	static void setControl(BuildsServiceMessageControl control) {
		NotificationSinkProxy.control = control;
	}

}
