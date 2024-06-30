/*******************************************************************************
 * Copyright (c) 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/**
 * compute the contents of a template based on
 *
 * @author dgreen
 * @since 3.0
 */
public class WikiTemplateResolver extends TemplateResolver {

	private String wikiBaseUrl;

	public String getWikiBaseUrl() {
		return wikiBaseUrl;
	}

	public void setWikiBaseUrl(String wikiBaseUrl) {
		this.wikiBaseUrl = wikiBaseUrl;
	}

	@Override
	public Template resolveTemplate(String templateName) {
		if (wikiBaseUrl != null) {
			int indexOf = templateName.indexOf(':');
			if (indexOf == 0) {
				templateName = templateName.substring(1);
			} else if (indexOf == -1) {
				templateName = "Template:" + templateName; //$NON-NLS-1$
			}
			URL url = computeRawUrl(templateName);
			if (url != null) {
				try {
					String content = readContent(url);
					Template template = new Template();
					String basicName = templateName.toLowerCase().startsWith("template:") //$NON-NLS-1$
							? templateName.substring(templateName.lastIndexOf(':') + 1)
									: templateName;
					template.setName(basicName);
					template.setTemplateMarkup(content);
					return template;
				} catch (final IOException e) {
					final String message = MessageFormat.format("Cannot read from {0}: {1}", url, e.getMessage()); //$NON-NLS-1$
					Logger.getLogger(WikiTemplateResolver.class.getName()).log(Level.WARNING, message, e);
				}
			}
		}
		return null;
	}

	protected String readContent(URL url) throws IOException {
		return IOUtils.toString(url, StandardCharsets.UTF_8);
	}

	private URL computeRawUrl(String path) {
		try {
			String qualifiedUrl = wikiBaseUrl;
			if (!qualifiedUrl.endsWith("/")) { //$NON-NLS-1$
				qualifiedUrl += "/"; //$NON-NLS-1$
			}
			qualifiedUrl += "index.php?title=" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "&action=raw"; //$NON-NLS-1$ //$NON-NLS-2$
			return new URL(qualifiedUrl);
		} catch (IOException e) {
			Logger.getLogger(WikiTemplateResolver.class.getName())
			.log(Level.WARNING,
					MessageFormat.format("Cannot compute raw URL for {0}: {1}", path, e.getMessage()), e); //$NON-NLS-1$
			return null;
		}
	}

}
