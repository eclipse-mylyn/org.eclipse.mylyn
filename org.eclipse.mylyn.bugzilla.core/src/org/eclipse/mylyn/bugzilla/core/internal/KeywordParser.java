/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.core.internal;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer.Token;

/**
 * Parses Bugzilla keywords page to determine keywords valid in this
 * installation
 * 
 * @author Shawn Minto
 */
public class KeywordParser {
	/** Tokenizer used on the stream */
	private static HtmlStreamTokenizer tokenizer;

	/**
	 * Constructor.
	 * 
	 * @param in
	 *            The input stream for the keywords page.
	 */
	public KeywordParser(Reader in) {
		tokenizer = new HtmlStreamTokenizer(in, null);
	}

	public String getEncoding() {
		return "";
	}

	/**
	 * Parse the keyword page for the valid products that a bug can be logged
	 * for
	 * 
	 * @return A list of the keywordds that we can enter bugs for
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<String> getKeywords() throws IOException, ParseException, LoginException {
		ArrayList<String> keywords = new ArrayList<String>();

		boolean isTitle = false;
		boolean possibleBadLogin = false;
		String title = "";

		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {

			// make sure that bugzilla doesn't want us to login
			if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == HtmlTag.Type.TITLE
					&& !((HtmlTag) (token.getValue())).isEndTag()) {
				isTitle = true;
				continue;
			}

			if (isTitle) {
				// get all of the data from inside of the title tag
				if (token.getType() != Token.TAG) {
					title += ((StringBuffer) token.getValue()).toString().toLowerCase() + " ";
					continue;
				} else if (token.getType() == Token.TAG
						&& ((HtmlTag) token.getValue()).getTagType() == HtmlTag.Type.TITLE
						&& ((HtmlTag) token.getValue()).isEndTag()) {
					// check if we may have a problem with login by looking at
					// the title of the page
					if ((title.indexOf("login") != -1
							|| (title.indexOf("invalid") != -1 && title.indexOf("password") != -1)
							|| title.indexOf("check e-mail") != -1 || title.indexOf("error") != -1))
						possibleBadLogin = true;
					isTitle = false;
					title = "";
				}
				continue;
			}

			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.TR && !tag.isEndTag()) {
					token = tokenizer.nextToken();
					if (token.getType() != Token.EOF && token.getType() == Token.TAG) {
						tag = (HtmlTag) token.getValue();
						if (tag.getTagType() != HtmlTag.Type.TH)
							continue;
						else {
							if (tag.getAttribute("align") == null
									|| !"left".equalsIgnoreCase(tag.getAttribute("align")))
								parseKeywords(keywords);

						}
					}
					continue;
				}
			}
		}

		// if we don't have any keywords and suspect that there was a login
		// problem, assume we had a login problem
		if (keywords == null && possibleBadLogin)
			throw new LoginException(IBugzillaConstants.MESSAGE_LOGIN_FAILURE);
		return keywords;
	}

	/**
	 * Parse the keywords that we can enter bugs for
	 * 
	 * @param keywords
	 *            The list of keywords to add this new product to
	 * @return
	 */
	private void parseKeywords(List<String> keywords) throws IOException, ParseException {
		StringBuffer sb = new StringBuffer();

		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.TH
						&& (tag.isEndTag() || !"left".equalsIgnoreCase(tag.getAttribute("align"))))
					break;
			} else if (token.getType() == Token.TEXT)
				sb.append(token.toString());
		}

		String prod = HtmlStreamTokenizer.unescape(sb).toString();
		if (prod.endsWith(":"))
			prod = prod.substring(0, prod.length() - 1);
		keywords.add(prod);

		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.TR && tag.isEndTag())
					break;

			}
		}
	}

}
