/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.xml.sax.SAXException;

public class SaxQueryWriter extends SaxTaskListElementWriter<RepositoryQuery> {

	public SaxQueryWriter(ContentHandlerWrapper handler) {
		super(handler);
	}

	@Override
	public void writeElement(RepositoryQuery query) throws SAXException {
		if (query.getClass() == RepositoryQuery.class) {
			AttributesWrapper attributes = new AttributesWrapper();
			attributes.addAttribute(TaskListExternalizationConstants.KEY_HANDLE, query.getHandleIdentifier());
			attributes.addAttribute(TaskListExternalizationConstants.KEY_CONNECTOR_KIND, query.getConnectorKind());
			attributes.addAttribute(TaskListExternalizationConstants.KEY_NAME, query.getSummary());
			attributes.addAttribute(TaskListExternalizationConstants.KEY_QUERY_STRING, query.getUrl());
			attributes.addAttribute(TaskListExternalizationConstants.KEY_REPOSITORY_URL, query.getRepositoryUrl());
			attributes.addAttribute(TaskListExternalizationConstants.KEY_LAST_REFRESH,
					query.getLastSynchronizedTimeStamp());
			handler.startElement(TaskListExternalizationConstants.NODE_QUERY, attributes);
			writeAttributes(query);
			for (ITask task : query.getChildren()) {
				writeQueryHit(task);
			}
			handler.endElement(TaskListExternalizationConstants.NODE_QUERY);
		} else {
			addError(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
					String.format("Unable to externalize query \"%s\" as it is of an unsupported type %s", //$NON-NLS-1$
							query.getHandleIdentifier(), query.getClass())));
		}
	}

	private void writeQueryHit(ITask task) throws SAXException {
		AttributesWrapper attributes = new AttributesWrapper();
		attributes.addAttribute(TaskListExternalizationConstants.KEY_HANDLE, task.getHandleIdentifier());
		handler.startElement(TaskListExternalizationConstants.NODE_QUERY_HIT, attributes);
		handler.endElement(TaskListExternalizationConstants.NODE_QUERY_HIT);
	}

}
