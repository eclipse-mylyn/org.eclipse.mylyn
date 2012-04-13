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

package org.eclipse.mylyn.internal.commons.ui.notifications;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.ui.notifications.NotificationSink;

/**
 * Describes how a {@link NotificationEvent} is handled. {@link NotificationAction}s store enablement and parameters
 * that determine how the {@link NotificationSink} executes the action.
 * 
 * @author Steffen Pingel
 * @deprecated use classes in the <code>org.eclipse.mylyn.commons.notifications.core</code> bundle instead
 */
@Deprecated
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
