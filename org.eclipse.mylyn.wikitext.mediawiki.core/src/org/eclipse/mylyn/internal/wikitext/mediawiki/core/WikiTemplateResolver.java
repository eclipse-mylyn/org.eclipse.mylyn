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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

/**
 * compute the contents of a template based on
 * 
 * @author dgreen
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
				templateName = "Template" + templateName; //$NON-NLS-1$
			} else if (indexOf == -1) {
				templateName = "Template:" + templateName; //$NON-NLS-1$
			}
			URL url = computeRawUrl(templateName);
			if (url != null) {
				Reader input;
				try {
					input = new InputStreamReader(new BufferedInputStream(url.openStream()), "UTF-8"); //$NON-NLS-1$
					try {
						String content = readFully(input);
						Template template = new Template();
						String basicName = templateName.toLowerCase().startsWith("template:") ? templateName.substring(templateName.lastIndexOf(':') + 1) : templateName; //$NON-NLS-1$
						template.setName(basicName);
						template.setTemplateMarkup(content);
						return template;
					} finally {
						input.close();
					}
				} catch (final IOException e) {
					final String message = MessageFormat.format("Cannot read from {0}: {1}", url, e.getMessage()); //$NON-NLS-1$
					Logger.getLogger(WikiTemplateResolver.class.getName()).log(Level.WARNING, message, e);
				}
			}
		}
		return null;
	}

	private String readFully(Reader input) throws IOException {
		StringWriter content = new StringWriter(1024);
		int i;
		while ((i = input.read()) != -1) {
			content.write(i);
		}
		return content.toString();
	}

	private URL computeRawUrl(String path) {
		try {
			String qualifiedUrl = wikiBaseUrl;
			if (!qualifiedUrl.endsWith("/")) { //$NON-NLS-1$
				qualifiedUrl += "/"; //$NON-NLS-1$
			}
			qualifiedUrl += "index.php?title=" + URLEncoder.encode(path, "UTF-8") + "&action=raw"; //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
			return new URL(qualifiedUrl);
		} catch (IOException e) {
			Logger.getLogger(WikiTemplateResolver.class.getName()).log(Level.WARNING,
					MessageFormat.format("Cannot compute raw URL for {0}: {1}", path, e.getMessage()), e); //$NON-NLS-1$
			return null;
		}
	}

}
