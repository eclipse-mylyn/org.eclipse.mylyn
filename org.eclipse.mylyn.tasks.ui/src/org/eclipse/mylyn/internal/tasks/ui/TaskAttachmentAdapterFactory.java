/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.ui.IActionFilter;

public class TaskAttachmentAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	private static final Class[] ADAPTER_TYPES = new Class[] { IActionFilter.class };

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return ADAPTER_TYPES;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof TaskAttachment) {
			return new IActionFilter() {
				public boolean testAttribute(Object target, String name, String value) {
					TaskAttachment taskAttachment = (TaskAttachment) target;
					if ("connectorKind".equals(name)) { //$NON-NLS-1$
						return value.equals(taskAttachment.getConnectorKind());
					} else if ("contentType".equals(name)) { //$NON-NLS-1$
						return value.equals(taskAttachment.getContentType());
					} else if ("isDeprecated".equals(name)) { //$NON-NLS-1$
						return Boolean.valueOf(value).booleanValue() == taskAttachment.isDeprecated();
					} else if ("isPatch".equals(name)) { //$NON-NLS-1$
						return Boolean.valueOf(value).booleanValue() == taskAttachment.isPatch();
					}
					return false;
				}
			};
		}
		// ignore
		return null;
	}
}
