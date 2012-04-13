/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import javax.swing.text.html.HTML.Tag;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.commons.core.HtmlTag;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;

public class GerritHtmlProcessor {

	private static GerritConfigX gerritConfigFromString(String token) {
		try {
			JSonSupport support = new JSonSupport();
			return support.getGson().fromJson(token, GerritConfigX.class);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					"Failed to deserialize Gerrit configuration: '" + token + "'", e));
			return null;
		}
	}

	private static String getText(HtmlStreamTokenizer tokenizer) throws IOException, ParseException {
		StringBuilder sb = new StringBuilder();
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TEXT) {
				sb.append(token.toString());
			} else if (token.getType() == Token.COMMENT) {
				// ignore
			} else {
				break;
			}
		}
		return StringEscapeUtils.unescapeHtml(sb.toString());
	}

	private GerritConfigX config;

	private String xsrfKey;

	public GerritConfigX getConfig() {
		return config;
	}

	public String getXsrfKey() {
		return xsrfKey;
	}

	public void parse(InputStream in, String charset) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
		try {
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getType() == Token.TAG) {
					HtmlTag tag = (HtmlTag) token.getValue();
					if (tag.getTagType() == Tag.SCRIPT) {
						String text = getText(tokenizer);
						text = text.replaceAll("\n", ""); //$NON-NLS-1$ //$NON-NLS-2$
						text = text.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$
						parse(text);
					}
				}
			}
		} catch (ParseException e) {
			throw new IOException("Error reading url"); //$NON-NLS-1$
		}
	}

	/**
	 * Parses the configuration from <code>text</code>.
	 */
	private void parse(String text) {
		String configPrefix = "var gerrit_hostpagedata={\"config\":"; //$NON-NLS-1$
		String configXsrfToken = "hostpagedata.xsrfToken=\""; //$NON-NLS-1$
		String[] tokens = text.split(";gerrit_"); //$NON-NLS-1$
		for (String token : tokens) {
			if (token.startsWith(configPrefix)) {
				token = token.substring(configPrefix.length());
				// remove closing }
				token = token.substring(0, token.length() - 1);
				this.config = gerritConfigFromString(token);
			}
			if (token.startsWith(configXsrfToken)) {
				token = token.substring(configXsrfToken.length());
				// remove closing "
				token = token.substring(0, token.length() - 1);
				this.xsrfKey = token;
			}
		}
	}

}
