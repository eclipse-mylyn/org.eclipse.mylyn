/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.core.history;

/**
 * @author John Anvik
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import javax.security.auth.login.LoginException;

import org.eclipse.mylyn.web.core.HtmlStreamTokenizer;
import org.eclipse.mylyn.web.core.HtmlTag;
import org.eclipse.mylyn.web.core.HtmlStreamTokenizer.Token;

/**
 * Parses Bugzilla bug activity page to fill in a BugActivity object
 * 
 * @author John Anvik
 */

public class BugzillaTaskHistoryParser {

	private InputStream inStream;

	private String characterEncoding;

	public BugzillaTaskHistoryParser(InputStream inStream, String characterEncoding) {
		this.inStream = inStream;
		this.characterEncoding = characterEncoding;
	}

	/**
	 * Parse the activity page for events
	 * 
	 * @return A BugActivity object containing the activity history
	 */
	public TaskHistory retrieveHistory() throws IOException, ParseException, LoginException {
		TaskHistory activity = new TaskHistory();
		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(new BufferedReader(new InputStreamReader(inStream,
				characterEncoding)), null);

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
					// check if we may have a problem with login by looking
					// at
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
				// Skip till find <br> - "there can be only one"
				if (tag.getTagType() == HtmlTag.Type.BR && !tag.isEndTag()) {
					// skip tags until start of real data
					while (true) {
						token = tokenizer.nextToken();
						if (token.getType() == Token.TAG) {
							tag = (HtmlTag) token.getValue();
							if ((tag.isEndTag() && tag.getTagType() == HtmlTag.Type.TR)
									|| tag.getTagType() == HtmlTag.Type.P)
								break;
						}
					}
					parseActivity(tokenizer, activity);
				}
			}
		}

		// if we don't have any keywords and suspect that there was a login
		// problem, assume we had a login problem
		if (activity.size() == 0 && possibleBadLogin)
			throw new LoginException("Bugzilla login information incorrect");
		return activity;
	}

	/**
	 * Parse the events that have happened to the bug
	 * 
	 * @param tokenizer
	 *            the token stream
	 * @param activity
	 *            the activity object to store the events in
	 * @return
	 */
	private void parseActivity(HtmlStreamTokenizer tokenizer, TaskHistory activity) throws IOException, ParseException {
		HtmlTag tag;

		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				tag = (HtmlTag) token.getValue();

				// End of events
				if (tag.getTagType() == HtmlTag.Type.TABLE && tag.isEndTag())
					break;

				// Get event entry
				this.parseEvent(tokenizer, activity);
			}
		}
	}

	/**
	 * Parse an activity entry
	 * 
	 * @param tokenizer
	 *            the stream of tokens
	 * @param activity
	 *            the activity object to store events in
	 */
	private void parseEvent(HtmlStreamTokenizer tokenizer, TaskHistory activity) {

		HtmlTag tag;
		int numChanges = 0;

		try {
			// Discover how many changes for this entry
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getType() == Token.TAG) {
					tag = (HtmlTag) token.getValue();
					if (tag.getTagType() == HtmlTag.Type.TD && tag.hasAttribute("rowspan")) {
						numChanges = tag.getIntAttribute("rowspan");
						break;
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String name = getData(tokenizer);
		String date = getData(tokenizer);

		String what, removed, added;
		TaskRevision event;
		for (int change = 0; change < numChanges; change++) {
			what = getData(tokenizer);
			removed = getData(tokenizer);
			added = getData(tokenizer);

			event = TaskRevision.createEvent(what, added);
			event.setName(name);
			event.setDate(date);
			event.setRemoved(removed);

			activity.addEvent(event);
		}
	}

	private String getData(HtmlStreamTokenizer tokenizer) {

		Token token;
		HtmlTag tag;
		StringBuffer sb = new StringBuffer();

		try {
			for (token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getType() == Token.TAG) {
					tag = (HtmlTag) token.getValue();
					if (tag.getTagType() == HtmlTag.Type.TD && tag.isEndTag()) {
						String data = HtmlStreamTokenizer.unescape(sb.toString());
						if (data.startsWith("\n") && (data.contains("Attachment") == false)) {
							data = ""; // empty field
						}
						return data;
					}
				}
				if (token.getType() == Token.TEXT) {
					sb.append(token.toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
