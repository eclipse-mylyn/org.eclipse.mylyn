/*******************************************************************************
 * Copyright (c) 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

public class BuiltInTemplateResolver extends TemplateResolver {

	private static Map<String, String> builtInTemplates = new HashMap<String, String>();
	static {
		builtInTemplates.put("mdash", "&nbsp;&mdash; "); //$NON-NLS-1$//$NON-NLS-2$
		builtInTemplates.put("ndash", "&nbsp;&ndash; "); //$NON-NLS-1$//$NON-NLS-2$
		builtInTemplates.put("emdash", "&nbsp;&mdash; "); //$NON-NLS-1$//$NON-NLS-2$
		builtInTemplates.put("endash", "&nbsp;&ndash; "); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public Template resolveTemplate(String templateName) {
		String templateText = builtInTemplates.get(templateName);
		if (templateText != null) {
			Template template = new Template();
			template.setName(templateName);
			template.setTemplateMarkup(templateText);
			return template;
		}
		return null;
	}

}
