/*******************************************************************************
 * Copyright (c) 2010, 2012 David Green and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.mediawiki.Template;
import org.eclipse.mylyn.wikitext.mediawiki.TemplateResolver;

public class BuiltInTemplateResolver extends TemplateResolver {

	private static Map<String, Template> builtInTemplates = new HashMap<>();
	static {
		registerTemplate(new Template("mdash", "&nbsp;&mdash; ")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new Template("mdash", "&nbsp;&mdash; ")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new Template("ndash", "&nbsp;&ndash; ")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new Template("emdash", "&nbsp;&mdash; ")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new Template("endash", "&nbsp;&ndash; ")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTYEAR", "yyyy")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTMONTH", "MM")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTMONTHNAME", "MMMMMMMM")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTMONTHNAMEGEN", "MMMMMMMM")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTMONTHABBREV", "MMM")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTDAY", "dd")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTDAY2", "dd")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTDOW", "F")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTDAYNAME", "EEEEEEEE")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTTIME", "HH:mm")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTHOUR", "HH")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTWEEK", "ww")); //$NON-NLS-1$//$NON-NLS-2$
		registerTemplate(new DateTimeTemplate("CURRENTTIMESTAMP", "yyyyMMddHHmmss")); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public Template resolveTemplate(String templateName) {
		return builtInTemplates.get(templateName);
	}

	private static void registerTemplate(Template template) {
		builtInTemplates.put(template.getName(), template);
	}

}
