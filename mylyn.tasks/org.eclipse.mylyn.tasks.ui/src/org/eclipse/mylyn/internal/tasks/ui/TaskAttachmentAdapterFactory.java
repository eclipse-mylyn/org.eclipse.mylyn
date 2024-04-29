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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.ui.IActionFilter;

public class TaskAttachmentAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	private static final Class[] ADAPTER_TYPES = { IActionFilter.class };

	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTER_TYPES;
	}

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adaptableObject instanceof TaskAttachment) {
			return adapterType.cast((IActionFilter) (target, name, value) -> {
				TaskAttachment taskAttachment = (TaskAttachment) target;
				if ("connectorKind".equals(name)) { //$NON-NLS-1$
					return value.equals(taskAttachment.getConnectorKind());
				} else if ("contentType".equals(name)) { //$NON-NLS-1$
					return value.equals(taskAttachment.getContentType());
				} else if ("isDeprecated".equals(name)) { //$NON-NLS-1$
					return Boolean.parseBoolean(value) == taskAttachment.isDeprecated();
				} else if ("isPatch".equals(name)) { //$NON-NLS-1$
					return Boolean.parseBoolean(value) == taskAttachment.isPatch();
				}
				return false;
			});
		}
		// ignore
		return null;
	}
}
