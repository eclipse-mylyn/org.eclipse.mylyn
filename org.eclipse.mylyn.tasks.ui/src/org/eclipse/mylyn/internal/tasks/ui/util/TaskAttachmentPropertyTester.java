/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Maarten Meijer - improvements for bug 252699
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;

/**
 * @author Steffen Pingel
 */
public class TaskAttachmentPropertyTester extends PropertyTester {

	private static final String PROPERTY_IS_CONTEXT = "isContext"; //$NON-NLS-1$

	private static final String PROPERTY_HAS_URL = "hasUrl"; //$NON-NLS-1$

	private boolean equals(boolean value, Object expectedValue) {
		return new Boolean(value).equals(expectedValue);
	}

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof ITaskAttachment) {
			ITaskAttachment taskAttachment = (ITaskAttachment) receiver;
			if (PROPERTY_IS_CONTEXT.equals(property)) {
				return equals(AttachmentUtil.isContext(taskAttachment), expectedValue);
			}
			if (PROPERTY_HAS_URL.equals(property)) {
				return equals(taskAttachment.getUrl() != null && taskAttachment.getUrl().length() > 0, expectedValue);
			}

		}
		return false;
	}

}
