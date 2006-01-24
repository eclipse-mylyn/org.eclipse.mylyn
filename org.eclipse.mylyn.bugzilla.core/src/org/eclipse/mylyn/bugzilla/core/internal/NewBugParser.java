/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
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

import javax.security.auth.login.LoginException;

import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.NewBugModel;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer.Token;

/**
 * @author Shawn Minto
 * 
 * This class parses the valid attribute values for a new bug
 */
public class NewBugParser {
	/** Tokenizer used on the stream */
	private HtmlStreamTokenizer tokenizer;

	/** Flag for whether we need to try to get the product or not */
	private static boolean getProd = false;

	public NewBugParser(Reader in) {
		tokenizer = new HtmlStreamTokenizer(in, null);
	}

	/**
	 * Parse the new bugs valid attributes
	 * 
	 * @param nbm
	 *            A reference to a NewBugModel where all of the information is
	 *            stored
	 * @throws IOException
	 * @throws ParseException
	 * @throws LoginException
	 */
	public void parseBugAttributes(NewBugModel nbm, boolean retrieveProducts) throws IOException, ParseException,
			LoginException {
		nbm.attributes.clear(); // clear any attriubtes in bug model from a
		// previous product

		NewBugParser.getProd = retrieveProducts;

		// create a new bug report and set the parser state to the start state
		ParserState state = ParserState.START;
		String attribute = null;

		boolean isTitle = false;
		boolean possibleBadLogin = false;
		boolean isErrorState = false;
		String title = "";
		// Default error message
		String errorMsg = "Bugzilla could not get the needed bug attribute since your login name or password is incorrect.  Please check your settings in the bugzilla preferences.";

		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			// make sure that bugzilla doesn't want us to login
			if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == HtmlTag.Type.TITLE
					&& !((HtmlTag) (token.getValue())).isEndTag()) {
				isTitle = true;
				continue;
			}

			if (isTitle) {
				// get all of the data in the title tag to compare with
				if (token.getType() != Token.TAG) {
					title += ((StringBuffer) token.getValue()).toString().toLowerCase() + " ";
					continue;
				} else if (token.getType() == Token.TAG
						&& ((HtmlTag) token.getValue()).getTagType() == HtmlTag.Type.TITLE
						&& ((HtmlTag) token.getValue()).isEndTag()) {
					// check if the title looks like we may have a problem with
					// login
					if (title.indexOf("login") != -1) {
						possibleBadLogin = true; // generic / default msg
						// passed to constructor re:
						// bad login
					}
					if ((title.indexOf("invalid") != -1 && title.indexOf("password") != -1)
							|| title.indexOf("check e-mail") != -1 || title.indexOf("error") != -1) {
						possibleBadLogin = true;
						isErrorState = true; // set flag so appropriate msg
						// is provide for the exception
						errorMsg = ""; // error message will be parsed from
						// error page
					}

					isTitle = false;
					title = "";
				}
				continue;
			}

			// we have found the start of an attribute name
			if ((state == ParserState.ATT_NAME || state == ParserState.START) && token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.TD && "right".equalsIgnoreCase(tag.getAttribute("align"))) {
					// parse the attribute's name
					attribute = parseAttributeName();
					if (attribute == null)
						continue;
					state = ParserState.ATT_VALUE;
					continue;
				}

				if (tag.getTagType() == HtmlTag.Type.TD && "#ff0000".equalsIgnoreCase(tag.getAttribute("bgcolor"))) {
					state = ParserState.ERROR;
					continue;
				}
			}

			// we have found the start of attribute values
			if (state == ParserState.ATT_VALUE && token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.TD) {
					// parse the attribute values
					parseAttributeValue(nbm, attribute);

					state = ParserState.ATT_NAME;
					attribute = null;
					continue;
				}
			}
			// page being parsed contains an Error message
			// parse error message so it can be given to the constructor of the
			// exception
			// so an appropriate error message is displayed
			if (state == ParserState.ERROR && isErrorState) {
				// tag should be text token, not a tag
				// get the error message
				if (token.getType() == Token.TEXT) {
					// get string value of next token to add to error messgage
					// unescape the string so any escape sequences parsed appear
					// unescaped in the details pane
					errorMsg += HtmlStreamTokenizer.unescape(((StringBuffer) token.getValue()).toString()) + " ";
				}
				// expect </font> tag to indicate end of error end msg
				// set next state to continue parsing remainder of page
				else if (token.getType() == Token.TAG
						&& ((HtmlTag) (token.getValue())).getTagType() == HtmlTag.Type.FONT
						&& ((HtmlTag) (token.getValue())).isEndTag()) {
					state = ParserState.ATT_NAME;
				}
				continue;
			}

			if ((state == ParserState.ATT_NAME || state == ParserState.START) && token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.INPUT && tag.getAttribute("type") != null
						&& "hidden".equalsIgnoreCase(tag.getAttribute("type").trim())) {
					Attribute a = new Attribute(tag.getAttribute("name"));
					a.setParameterName(tag.getAttribute("name"));
					a.setValue(tag.getAttribute("value"));
					a.setHidden(true);
					nbm.attributes.put(a.getName(), a);
					continue;
				}
			}
		}

		// if we have no attributes and we suspect a bad login, we assume that
		// the login info was bad
		if (possibleBadLogin && (nbm.getAttributes() == null || nbm.getAttributes().size() == 0)) {
			throw new LoginException(errorMsg);
		}
	}

	/**
	 * Parse the case where we have found an attribute name
	 * 
	 * @param tokenizer
	 *            The tokenizer to use to find the name
	 * @return The name of the attribute
	 * @throws IOException
	 * @throws ParseException
	 */
	private String parseAttributeName() throws IOException, ParseException {
		StringBuffer sb = new StringBuffer();

		parseTableCell(sb);
		HtmlStreamTokenizer.unescape(sb);
		// remove the colon if there is one
		if (sb.length() == 0)
			return null;
		if (sb.charAt(sb.length() - 1) == ':') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Reads text into a StringBuffer until it encounters a close table cell tag
	 * (&lt;/TD&gt;) or start of another cell. The text is appended to the
	 * existing value of the buffer. <b>NOTE:</b> Does not handle nested cells!
	 * 
	 * @param tokenizer
	 * @param sb
	 * @throws IOException
	 * @throws ParseException
	 */
	private void parseTableCell(StringBuffer sb) throws IOException, ParseException {
		boolean noWhitespace = false;
		for (HtmlStreamTokenizer.Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer
				.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.TD) {
					if (!tag.isEndTag()) {
						tokenizer.pushback(token);
					}
					break;
				}
				noWhitespace = token.getWhitespace().length() == 0;
			} else if (token.getType() == Token.TEXT) {
				// if there was no whitespace between the tag and the
				// preceding text, don't insert whitespace before this text
				// unless it is there in the source
				if (!noWhitespace && token.getWhitespace().length() > 0 && sb.length() > 0) {
					sb.append(' ');
				}
				sb.append((StringBuffer) token.getValue());
			}
		}
	}

	/**
	 * Parse the case where we have found attribute values
	 * 
	 * @param nbm
	 *            The NewBugModel that is to contain information about a new bug
	 * @param attributeName
	 *            The name of the attribute that we are parsing
	 * @param tokenizer
	 *            The tokenizer to use for parsing
	 * @throws IOException
	 * @throws ParseException
	 */
	private void parseAttributeValue(NewBugModel nbm, String attributeName) throws IOException, ParseException {

		HtmlStreamTokenizer.Token token = tokenizer.nextToken();
		if (token.getType() == Token.TAG) {
			HtmlTag tag = (HtmlTag) token.getValue();
			if (tag.getTagType() == HtmlTag.Type.SELECT && !tag.isEndTag()) {
				String parameterName = tag.getAttribute("name");
				parseSelect(nbm, attributeName, parameterName);
			} else if (tag.getTagType() == HtmlTag.Type.INPUT && !tag.isEndTag()) {
				parseInput(nbm, attributeName, tag);
			} else if (!tag.isEndTag()) {
				parseAttributeValueCell(nbm, attributeName);
			}
		} else {
			StringBuffer sb = new StringBuffer();
			if (token.getType() == Token.TEXT) {
				sb.append((StringBuffer) token.getValue());
				parseAttributeValueCell(nbm, attributeName, sb);
			}
		}
	}

	/**
	 * Parse the case where the attribute value is just text in a table cell
	 * 
	 * @param attributeName
	 *            The name of the attribute we are parsing
	 * @param tokenizer
	 *            The tokenizer to use for parsing
	 * @throws IOException
	 * @throws ParseException
	 */
	private void parseAttributeValueCell(NewBugModel nbm, String attributeName) throws IOException, ParseException {
		StringBuffer sb = new StringBuffer();

		parseAttributeValueCell(nbm, attributeName, sb);
	}

	private void parseAttributeValueCell(NewBugModel nbm, String attributeName, StringBuffer sb) throws IOException,
			ParseException {

		parseTableCell(sb);
		HtmlStreamTokenizer.unescape(sb);

		// if we need the product we will get it
		if (getProd && attributeName.equalsIgnoreCase("product")) {
			nbm.setProduct(sb.toString());
		}
	}

	/**
	 * Parse the case where the attribute value is an input
	 * 
	 * @param nbm
	 *            The new bug model to add information that we get to
	 * @param attributeName
	 *            The name of the attribute that we are parsing
	 * @param tag
	 *            The HTML tag that we are currently on
	 * @throws IOException
	 */
	private static void parseInput(NewBugModel nbm, String attributeName, HtmlTag tag) throws IOException {

		Attribute a = new Attribute(attributeName);
		a.setParameterName(tag.getAttribute("name"));
		String value = tag.getAttribute("value");
		if (value == null)
			value = "";

		// if we found the summary, add it to the bug report
		if (attributeName.equalsIgnoreCase("summary")) {
			nbm.setSummary(value);
		} else if (attributeName.equalsIgnoreCase("Attachments")) {
			// do nothing - not a problem after 2.14
		} else if (attributeName.equalsIgnoreCase("add cc")) {
			// do nothing
		} else if (attributeName.toLowerCase().startsWith("cc")) {
			// do nothing cc's are options not inputs
		} else {
			// otherwise just add the attribute
			a.setValue(value);
			nbm.attributes.put(attributeName, a);
		}
	}

	/**
	 * Parse the case where the attribute value is an option
	 * 
	 * @param nbm
	 *            The NewBugModel that we are storing information in
	 * @param attributeName
	 *            The name of the attribute that we are parsing
	 * @param parameterName
	 *            The SELECT tag's name
	 * @param tokenizer
	 *            The tokenizer that we are using for parsing
	 * @throws IOException
	 * @throws ParseException
	 */
	private void parseSelect(NewBugModel nbm, String attributeName, String parameterName) throws IOException,
			ParseException {

		boolean first = false;
		Attribute a = new Attribute(attributeName);
		a.setParameterName(parameterName);

		HtmlStreamTokenizer.Token token = tokenizer.nextToken();
		while (token.getType() != Token.EOF) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.SELECT && tag.isEndTag())
					break;
				if (tag.getTagType() == HtmlTag.Type.OPTION && !tag.isEndTag()) {
					String optionName = tag.getAttribute("value");
					boolean selected = tag.hasAttribute("selected");
					StringBuffer optionText = new StringBuffer();
					for (token = tokenizer.nextToken(); token.getType() == Token.TEXT; token = tokenizer.nextToken()) {
						if (optionText.length() > 0) {
							optionText.append(' ');
						}
						optionText.append((StringBuffer) token.getValue());
					}
					a.addOptionValue(optionText.toString(), optionName);

					if (selected || first) {
						a.setValue(optionText.toString());
						first = false;
					}
				} else {
					token = tokenizer.nextToken();
				}
			} else {
				token = tokenizer.nextToken();
			}
		}

		if (!(nbm.attributes).containsKey(attributeName)) {
			(nbm.attributes).put(attributeName, a);
		}
	}

	/**
	 * Enum class for describing current state of Bugzilla report parser.
	 */
	private static class ParserState {
		/** An instance of the start state */
		protected static final ParserState START = new ParserState("start");

		/** An instance of the state when the parser found an attribute name */
		protected static final ParserState ATT_NAME = new ParserState("att_name");

		/** An instance of the state when the parser found an attribute value */
		protected static final ParserState ATT_VALUE = new ParserState("att_value");

		/** An instance of the state when an error page is found */
		protected static final ParserState ERROR = new ParserState("error");

		/** State's human-readable name */
		private String name;

		/**
		 * Constructor
		 * 
		 * @param description -
		 *            The states human readable name
		 */
		private ParserState(String description) {
			this.name = description;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
