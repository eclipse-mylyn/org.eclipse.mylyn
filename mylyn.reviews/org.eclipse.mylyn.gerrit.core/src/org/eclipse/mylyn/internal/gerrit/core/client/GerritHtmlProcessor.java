/*******************************************************************************
 * Copyright (c) 2012, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTML.Tag;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.commons.core.HtmlTag;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.osgi.util.NLS;

public class GerritHtmlProcessor {

	private static GerritConfigX gerritConfigFromString(String token) {
		try {
			JSonSupport support = new JSonSupport();
			return support.parseResponse(token, GerritConfigX.class);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					NLS.bind("Failed to deserialize Gerrit configuration: ''{0}''", token), e)); //$NON-NLS-1$
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
		return StringEscapeUtils.unescapeHtml4(sb.toString());
	}

	private GerritConfigX config;

	private String xsrfKey;

	/**
	 * Introduced in Gerrit 2.6 as a replacement for {@link #xsrfKey}.
	 */
	private String xGerritAuth;

	public GerritConfigX getConfig() {
		return config;
	}

	public String getXsrfKey() {
		return xsrfKey;
	}

	public String getXGerritAuth() {
		return xGerritAuth;
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
						text = text.replace("\n", ""); //$NON-NLS-1$ //$NON-NLS-2$
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
		Pattern p = Pattern.compile("var gerrit_hostpagedata=\\{(\"version\":\"([^\"]+)\",)?\"config\":"); //$NON-NLS-1$
		String configXsrfToken = "hostpagedata.xsrfToken=\""; //$NON-NLS-1$
		String configXGerritAuth = "hostpagedata.xGerritAuth=\""; //$NON-NLS-1$
		String[] tokens = text.split(";gerrit_"); //$NON-NLS-1$
		for (String token : tokens) {
			Matcher m = p.matcher(token);
			if (m.find()) {
				token = token.substring(m.toMatchResult().group(0).length());
				token = removeExcessJson(token);
				config = gerritConfigFromString(token);
			} else if (token.startsWith(configXsrfToken)) {
				token = token.substring(configXsrfToken.length());
				// remove closing "
				token = token.substring(0, token.length() - 1);
				xsrfKey = token;
			} else if (token.startsWith(configXGerritAuth)) {
				token = token.substring(configXGerritAuth.length());
				// remove closing "
				token = token.substring(0, token.length() - 1);
				xGerritAuth = token;
			}
		}
	}

	/**
	 * Remove everything after second to last '}'
	 * 
	 * @param token
	 *            a non-parsable Json string of the form '{ ... }}' or '{ ... } ...}' where '...' is any valid JSON
	 */
	private String removeExcessJson(String token) {
		int lastCurlyIndex = token.lastIndexOf("}"); //$NON-NLS-1$
		int secondLastCurlyIndex = token.lastIndexOf("}", lastCurlyIndex - 1); //$NON-NLS-1$
		return token.substring(0, secondLastCurlyIndex + 1);
	}
}
