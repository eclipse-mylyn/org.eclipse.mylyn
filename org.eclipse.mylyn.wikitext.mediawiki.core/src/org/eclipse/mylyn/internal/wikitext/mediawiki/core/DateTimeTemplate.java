/*******************************************************************************
 * Copyright (c) 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;

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
