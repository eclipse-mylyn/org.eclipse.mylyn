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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.IAttributeContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.xml.sax.SAXException;

public abstract class SaxTaskListElementWriter<T extends IRepositoryElement> {

	protected final ContentHandlerWrapper handler;

	private final MultiStatus errors;

	public SaxTaskListElementWriter(ContentHandlerWrapper handler) {
		this.handler = handler;
		this.errors = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, IStatus.OK, null, null);
	}

	public abstract void writeElement(T element) throws SAXException;

	protected void writeAttributes(IAttributeContainer container) throws SAXException {
		Map<String, String> attributes = container.getAttributes();
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			AttributesWrapper xmlAttributes = new AttributesWrapper();
			xmlAttributes.addAttribute(TaskListExternalizationConstants.KEY_KEY, entry.getKey());

			handler.startElement(TaskListExternalizationConstants.NODE_ATTRIBUTE, xmlAttributes);
			handler.characters(entry.getValue());
			handler.endElement(TaskListExternalizationConstants.NODE_ATTRIBUTE);
		}
	}

	@SuppressWarnings({ "restriction" })
	protected String stripControlCharacters(String text) {
		if (text == null) {
			return ""; //$NON-NLS-1$
		}
		return org.eclipse.mylyn.internal.commons.core.XmlStringConverter.cleanXmlString(text);
	}

	protected String formatExternDate(Date date) {
		if (date == null) {
			return ""; //$NON-NLS-1$
		}
		SimpleDateFormat format = new SimpleDateFormat(TaskListExternalizationConstants.OUT_DATE_FORMAT,
				Locale.ENGLISH);
		return format.format(date);
	}

	protected String formatExternCalendar(Calendar date) {
		if (date == null) {
			return ""; //$NON-NLS-1$
		}
		return formatExternDate(date.getTime());
	}

	protected void addError(IStatus status) {
		errors.add(status);
	}

	public IStatus getErrors() {
		return errors;
	}

}
