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

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Steffen Pingel
 */
public class NotificationEvent extends NotificationElement {

	public NotificationEvent(IConfigurationElement element) {
		super(element);
	}

	private NotificationCategory category;

	public NotificationCategory getCategory() {
		return category;
	}

	public void setCategory(NotificationCategory category) {
		this.category = category;
	}

	public String getCategoryId() {
		return element.getAttribute("categoryId");
	}

}
