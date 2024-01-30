/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.notifications.core.NotificationSink;

/**
 * Describes how a {@link NotificationEvent} is handled. {@link NotificationAction}s store enablement and parameters that determine how the
 * {@link NotificationSink} executes the action.
 * 
 * @author Steffen Pingel
 */
public class NotificationAction {

	private boolean selected;

	private final NotificationSinkDescriptor sinkDescriptor;

	public NotificationAction(NotificationSinkDescriptor sinkDescriptor) {
		Assert.isNotNull(sinkDescriptor);
		this.sinkDescriptor = sinkDescriptor;
	}

	public NotificationSinkDescriptor getSinkDescriptor() {
		return sinkDescriptor;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return sinkDescriptor.getLabel();
	}

}
