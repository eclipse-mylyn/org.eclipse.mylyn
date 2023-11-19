/*******************************************************************************
 * Copyright (c) 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.mylyn.wikitext.mediawiki.Template;

/**
 * A template that produces date/time content.
 * 
 * @author David Green
 */
public class DateTimeTemplate extends Template {

	public DateTimeTemplate() {
	}

	/**
	 * @param name
	 * @param templateMarkup
	 *            the format to use with {@link SimpleDateFormat}.
	 */
	public DateTimeTemplate(String name, String templateMarkup) {
		super(name, templateMarkup);
	}

	@Override
	public String getTemplateContent() {
		String markup = getTemplateMarkup();
		if (markup != null) {
			SimpleDateFormat format = new SimpleDateFormat(markup);
			markup = format.format(new Date());
		}
		return markup;
	}
}
