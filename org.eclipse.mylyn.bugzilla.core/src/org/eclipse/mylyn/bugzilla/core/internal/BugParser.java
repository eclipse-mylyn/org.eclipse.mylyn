/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.core.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.bugzilla.core.Comment;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.Operation;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer.Token;


/**
 * @author Shawn Minto
 *
 * This class parses bugs so that they can be displayed using the bug editor
 */
public class BugParser 
{
	/** Parser for dates in the report */
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private static final String keywordsUrl = "describekeywords.cgi";

	/**
	 * Parse the case where we have found an attribute name
	 * @param in The input stream for the bug
	 * @return The name of the attribute that we are parsing
	 * @throws IOException
	 */
	private static String parseAttributeName(HtmlStreamTokenizer tokenizer)
		throws IOException, ParseException {
		StringBuffer sb = new StringBuffer();

		parseTableCell(tokenizer, sb);
		HtmlStreamTokenizer.unescape(sb);
		// remove the colon if there is one
		if (sb.charAt(sb.length() - 1) == ':') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Parse the case where we have found attribute values
	 * @param in The input stream of the bug
	 * @param bug The bug report for the current bug
	 * @param attribute The name of the attribute
	 * @throws IOException
	 */
	private static void parseAttributeValue(
		BugReport bug,
		String attributeName, HtmlStreamTokenizer tokenizer, 
		String userName, String password)
		throws IOException, ParseException {

		Token token = tokenizer.nextToken();
		if (token.getType() == Token.TAG) {
			HtmlTag tag = (HtmlTag) token.getValue();

			// make sure that we are on a tag that we care about, not a label
			// fix added so that we can parse the mozilla bug pages
			if(tag.getTagType() == HtmlTag.Type.LABEL)
			{
				token = tokenizer.nextToken();
				if (token.getType() == Token.TAG)
					tag = (HtmlTag) token.getValue();
				else
				{
					StringBuffer sb = new StringBuffer();
					if (token.getType() == Token.TEXT) {
						sb.append((StringBuffer) token.getValue());
						parseAttributeValueCell(bug, attributeName, tokenizer, sb);
					}
				}
			}
			
			if (tag.getTagType() == HtmlTag.Type.SELECT && !tag.isEndTag()) {
				String parameterName = tag.getAttribute("name");
				parseSelect(bug, attributeName, parameterName, tokenizer);
			}
			else if (tag.getTagType() == HtmlTag.Type.INPUT && !tag.isEndTag()) {
				parseInput(bug, attributeName, tag, userName, password);
			}
			else if (!tag.isEndTag() || attributeName.equalsIgnoreCase("resolution")) {
				if(tag.isEndTag() && attributeName.equalsIgnoreCase("resolution"))
				{
					Attribute a = new Attribute(attributeName);
					a.setValue("");
					bug.addAttribute(a);
				}
				parseAttributeValueCell(bug, attributeName, tokenizer);
			}
		}
		else {
			StringBuffer sb = new StringBuffer();
			if (token.getType() == Token.TEXT) {
				sb.append((StringBuffer) token.getValue());
				parseAttributeValueCell(bug, attributeName, tokenizer, sb);
			}
		}
	}

	/**
	 * Parse the case where the attribute value is just text in a table cell
	 * @param in The input stream of the bug
	 * @param bug The bug report for the current bug
	 * @param attributeName The name of the attribute that we are parsing
	 * @throws IOException
	 */
	private static void parseAttributeValueCell(
		BugReport bug,
		String attributeName,
		HtmlStreamTokenizer tokenizer)
		throws IOException, ParseException {
		StringBuffer sb = new StringBuffer();

		parseAttributeValueCell(bug, attributeName, tokenizer, sb);
	}
	
	private static void parseAttributeValueCell(
		BugReport bug,
		String attributeName,
		HtmlStreamTokenizer tokenizer,
		StringBuffer sb)
		throws IOException, ParseException {
		
		parseTableCell(tokenizer, sb);
		HtmlStreamTokenizer.unescape(sb);
		
		// create a new attribute and set its value to the value that we retrieved
		Attribute a = new Attribute(attributeName);
		a.setValue(sb.toString());

		// if we found an attachment attribute, forget about it, else add the
		// attribute to the bug report
		if (attributeName.toLowerCase().startsWith("attachments")) {
			// do nothing
		}
		else {
			if(attributeName.equals("Bug#"))
				a.setValue(a.getValue().replaceFirst("alias:", ""));
			bug.addAttribute(a);
		}
	}

	/**
	 * Reads text into a StringBuffer until it encounters a close table cell tag (&lt;/TD&gt;) or start of another cell.
	 * The text is appended to the existing value of the buffer. <b>NOTE:</b> Does not handle nested cells!
	 * @param tokenizer
	 * @param sb
	 * @throws IOException
	 * @throws ParseException
	 */
	private static void parseTableCell(HtmlStreamTokenizer tokenizer, StringBuffer sb)
		throws IOException, ParseException {
		boolean noWhitespace = false;
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.TD) {
					if (!tag.isEndTag()) {
						tokenizer.pushback(token);
					}
					break;
				}
				noWhitespace = token.getWhitespace().length() == 0;
			}
			else if (token.getType() == Token.TEXT) {
				// if there was no whitespace between the tag and the
				// preceding text, don't insert whitespace before this text
				// unless it is there in the source 
				if (!noWhitespace && token.getWhitespace().length() > 0 && sb.length() > 0) {
					sb.append(' ');
				}
				sb.append((StringBuffer)token.getValue());
			}
		}
	}

	/**
	 * Parse the case where the attribute value is an option
	 * @param in The input stream for the bug
	 * @param bug The bug report for the current bug
	 * @param attribute The name of the attribute that we are parsing
	 * @param parameterName the SELECT tag's name
	 * @throws IOException
	 */
	private static void parseSelect(
		BugReport bug,
		String attributeName,
		String parameterName,
		HtmlStreamTokenizer tokenizer)
		throws IOException, ParseException {
		
		boolean first = false;
		Attribute a = new Attribute(attributeName);
		a.setParameterName(parameterName);
		
		Token token = tokenizer.nextToken();
		while ( token.getType() != Token.EOF) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.SELECT && tag.isEndTag()) break;
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
				}
				else {
					token = tokenizer.nextToken();
				}
			}
			else {
				token = tokenizer.nextToken();
			}
		}

		// if we parsed the cc field add the e-mails to the bug report else add the attribute to the bug report
		if (attributeName.toLowerCase().startsWith("cc")) {
			for (Iterator<String> it = a.getOptionValues().keySet().iterator(); it.hasNext(); ) {
				String email = it.next();
				bug.addCC(HtmlStreamTokenizer.unescape(email));
			}
		}
		else {
			bug.addAttribute(a);
		}
	}

	/**
	 * Parse the case where the attribute value is an input
	 * @param bug The bug report for the current bug
	 * @param attributeName The name of the attribute
	 * @param tag The INPUT tag
	 * @throws IOException
	 */
	private static void parseInput(
		BugReport bug,
		String attributeName,
		HtmlTag tag, String userName, String password)
		throws IOException {

		Attribute a = new Attribute(attributeName);
		a.setParameterName(tag.getAttribute("name"));
		String name = tag.getAttribute("name");
		String value = tag.getAttribute("value");
		if (value == null) value = "";
		
		// if we found the summary, add it to the bug report
		if (name.equalsIgnoreCase("short_desc")) {
			bug.setSummary(value);
		}
		else if (name.equalsIgnoreCase("bug_file_loc")) {
			a.setValue(value);
			bug.addAttribute(a);
		}
		else if (name.equalsIgnoreCase("newcc")) {
			a.setValue(value);
			bug.addAttribute(a);
		}
		else {
			// otherwise just add the attribute
			a.setValue(value);
			bug.addAttribute(a);

			if (attributeName.equalsIgnoreCase("keywords") && BugzillaRepository.getURL() != null) {
				
				BufferedReader input = null;
				try {
					
					String urlText = "";
			
					// if we have a user name, may as well log in just in case it is required
					if(userName != null && !userName.equals("") && password != null && !password.equals(""))
					{
						/*
						 * The UnsupportedEncodingException exception for
						 * URLEncoder.encode() should not be thrown, since every
						 * implementation of the Java platform is required to support
						 * the standard charset "UTF-8"
						 */
						urlText += "?GoAheadAndLogIn=1&Bugzilla_login=" + URLEncoder.encode(userName, "UTF-8") + "&Bugzilla_password=" + URLEncoder.encode(password, "UTF-8");
					}
					
					// connect to the bugzilla server to get the keyword list
					input = new BufferedReader(new InputStreamReader(new URL(BugzillaRepository.getURL() + "/" + keywordsUrl+urlText).openStream()));
				
					// parse the valid keywords and add them to the bug
					List<String> keywords = new KeywordParser(input).getKeywords();
					bug.setKeywords(keywords);
				
				} catch(Exception e) {
					// throw an exception if there is a problem reading the bug from the server
					throw new IOException("Exception while fetching the list of keywords from the server: " + e.getMessage());
				}
				finally
				{
					try{
						if(input != null)
							input.close();
					}catch(IOException e)
					{
						BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID,IStatus.ERROR,"Problem closing the stream", e));
					}
				}
			}
		}
	}

	/**
	 * Parse the case where we are dealing with the description
	 * @param bug The bug report for the bug
	 * @throws IOException
	 */
	private static void parseDescription(BugReport bug, HtmlStreamTokenizer tokenizer)
		throws IOException, ParseException {

		StringBuffer sb = new StringBuffer();
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.PRE && tag.isEndTag()) break;
			}
			else if (token.getType() == Token.TEXT) {
				if (sb.length() > 0) {
					sb.append(token.getWhitespace());
				}
				sb.append((StringBuffer) token.getValue());
			}
		}

		// set the bug to have the description we retrieved
		String text = HtmlStreamTokenizer.unescape(sb).toString();
		bug.setDescription(text);
	}

	/**
	 * Parse the case where we have found the start of a comment
	 * @param in The input stream of the bug
	 * @param bug The bug report for the current bug
	 * @return The comment that we have created with the information
	 * @throws IOException
	 * @throws ParseException
	 */
	private static Comment parseCommentHead(BugReport bug, HtmlStreamTokenizer tokenizer)
		throws IOException, ParseException {
		int number = 0;
		Date date = null;
		String author = null;
		String authorName = null;
		
		// get the comment's number
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.A) {
					String href = tag.getAttribute("href");
					if (href != null) {
						int index = href.toLowerCase().indexOf("#c");
						if (index == -1) continue;
						token = tokenizer.nextToken();
						number = Integer.parseInt(((StringBuffer)token.getValue()).toString().substring(1));
						break;
					}
				}
			}
		}

		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.A) {
					String href = tag.getAttribute("href");
					if (href != null) {
						int index = href.toLowerCase().indexOf("mailto");
						if (index == -1) continue;
						author = href.substring(index + 7);
						break;
					}
				}
			}
		}

		// get the author's real name
		StringBuffer sb = new StringBuffer();
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.A && tag.isEndTag()) break;
			}
			else if (token.getType() == Token.TEXT) {
				if (sb.length() > 0) {
					sb.append(' ');
				}
				sb.append((StringBuffer)token.getValue());
			}
		}
		authorName = sb.toString();
		
		// get the comment's date
		sb.setLength(0);
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.I && tag.isEndTag()) break;
			}
			else if (token.getType() == Token.TEXT) {
				if (sb.length() > 0) {
					sb.append(' ');
				}
				sb.append((StringBuffer)token.getValue());
			}
		}
		if (sb.length() > 16) {
			date = df.parse(sb.substring(0, 16));
		} else {
			date = Calendar.getInstance().getTime(); // XXX: failed to get date
		}
		return new Comment(bug, number, date, author, authorName);
	}

	/**
	 * Parse the case where we have comment text
	 * @param in The input stream for the bug
	 * @param bug The bug report for the current bug
	 * @param comment The comment to add the text to
	 * @throws IOException
	 */
	private static void parseCommentText(
		BugReport bug,
		Comment comment, HtmlStreamTokenizer tokenizer)
		throws IOException, ParseException {
			
		StringBuffer sb = new StringBuffer();
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (sb.length() > 0) {    // added to ensure whitespace is not lost if adding a tag within a tag
					sb.append(token.getWhitespace());
				}
				if (tag.getTagType() == HtmlTag.Type.PRE && tag.isEndTag()) break;
			}
			else if (token.getType() == Token.TEXT) {
				if (sb.length() > 0) {
					sb.append(token.getWhitespace());
				}
				sb.append((StringBuffer) token.getValue());
			}
		}

		HtmlStreamTokenizer.unescape(sb);
		comment.setText(sb.toString());
		bug.addComment(comment);
	}

	/**
	 * Parse the full html version of the bug
	 * @param in - the input stream for the bug
	 * @param id - the id of the bug that is to be parsed
	 * @return A bug report for the bug that was parsed
	 * @throws IOException
	 * @throws ParseException
	 */
	public static BugReport parseBug(Reader in, int id, String serverName, boolean is218, String userName, String password) throws IOException, ParseException, LoginException
	{
		// create a new bug report and set the parser state to the start state
		BugReport bug = new BugReport(id, serverName);
		ParserState state = ParserState.START;
		Comment comment = null;
		String attribute = null;
		
		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);
		
		boolean isTitle = false;
		boolean possibleBadLogin = false;
		boolean checkBody = false;
		String title = "";
		StringBuffer body = new StringBuffer();
		
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			 
			// make sure that bugzilla doesn't want us to login
			if(token.getType() == Token.TAG && ((HtmlTag)(token.getValue())).getTagType() == HtmlTag.Type.TITLE && !((HtmlTag)(token.getValue())).isEndTag())
			{
				isTitle = true;
				continue;
			}
	
			if(isTitle)
			{
				// get all of the data in the title tag
				if(token.getType() != Token.TAG)
				{
					title += ((StringBuffer)token.getValue()).toString().toLowerCase() + " ";
					continue;
				}
				else if(token.getType() == Token.TAG && ((HtmlTag)token.getValue()).getTagType() == HtmlTag.Type.TITLE && ((HtmlTag)token.getValue()).isEndTag())
				{
					// check and see if the title seems as though we have wrong login info
					if(title.indexOf("login") != -1 || (title.indexOf("invalid") != -1 && title.indexOf("password") != -1) ||  title.indexOf("check e-mail") != -1)
						possibleBadLogin = true; // we possibly have a bad login
					
					// if the title starts with error, we may have a login problem, or
					// there is a problem with the bug (doesn't exist), so we must do
					// some more checks
					if(title.startsWith("error"))
						checkBody = true;
						
					isTitle = false;
					title = "";
				}
				continue;
			}
			
			// if we have to add all of the text so that we can check it later
			// for problems with the username and password
			if(checkBody && token.getType() == Token.TEXT)
			{
				body.append((StringBuffer)token.getValue());
				body.append(" ");
			}
							
			// we have found the start of an attribute name
			if ((state == ParserState.ATT_NAME || state == ParserState.START) && token.getType() == Token.TAG ) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.TD && "right".equalsIgnoreCase(tag.getAttribute("align"))) {
					// parse the attribute's name
					attribute = parseAttributeName(tokenizer);
					
					if (attribute.toLowerCase().startsWith("opened")) {
						// find the colon so we can get the date
						int index = attribute.toLowerCase().indexOf(":");
						String date;
						if (index != -1)
							date = attribute.substring(index + 1).trim();
						else
							date = attribute.substring(6).trim();

                                        
						// set the bugs opened date to be the date we parsed
						bug.setCreated(df.parse(date));
						state = ParserState.ATT_NAME;
						continue;
					}
                    
                    // in 2.18, the last modified looks like the opened so we need to parse it differently
                    if (attribute.toLowerCase().startsWith("last modified")&& is218) {
                        // find the colon so we can get the date
                        int index = attribute.toLowerCase().indexOf(":");
                        String date;
                        if (index != -1)
                            date = attribute.substring(index + 1).trim();
                        else
                            date = attribute.substring(6).trim();

                        // create a new attribute and set the date
                        Attribute t = new Attribute("Last Modified");
                        t.setValue(date);
                        
                        // add the attribute to the bug report
                        bug.addAttribute(t);
                        state = ParserState.ATT_NAME;
                        continue;
                    }

					state = ParserState.ATT_VALUE;
					continue;
				}
				else if (tag.getTagType() == HtmlTag.Type.INPUT && "radio".equalsIgnoreCase(tag.getAttribute("type")) && "knob".equalsIgnoreCase(tag.getAttribute("name")))
				{
					// we found a radio button
					parseOperations(bug, tokenizer, tag, is218);				
				}
			}

			// we have found the start of attribute values
			if (state == ParserState.ATT_VALUE && token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.TD) {
					// parse the attribute values
					parseAttributeValue(bug, attribute, tokenizer, userName, password);
					
					state = ParserState.ATT_NAME;
					attribute = null;
					continue;
				}
			}

			// we have found the start of a comment
			if (state == ParserState.DESC_START && token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.I) {
					// parse the comment's start
					comment = parseCommentHead(bug, tokenizer);
	
					state = ParserState.DESC_VALUE;
					continue;
				}
			}

			// we have found the start of the comment text 
			if (state == ParserState.DESC_VALUE && token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.PRE) {
					// parse the text of the comment
					parseCommentText(bug, comment, tokenizer);
					
					comment = null;
					state = ParserState.DESC_START;
					continue;
				}
			}
			
			// we have found the description of the bug
			if ((state == ParserState.ATT_NAME || state == ParserState.START) && 
				token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.PRE) {
					// parse the description for the bug
					parseDescription(bug, tokenizer);
	
					state = ParserState.DESC_START;
					continue;
				}
			}

			//parse hidden fields
			if((state == ParserState.ATT_NAME || state == ParserState.START) && token.getType() == Token.TAG)
			{
				HtmlTag tag = (HtmlTag)token.getValue();
				if(tag.getTagType() == HtmlTag.Type.INPUT && tag.getAttribute("type") != null &&"hidden".equalsIgnoreCase(tag.getAttribute("type").trim()))
				{
					
					Attribute a = new Attribute(tag.getAttribute("name"));
					a.setParameterName(tag.getAttribute("name"));
					a.setValue(tag.getAttribute("value"));
					a.setHidden(true);
					bug.addAttribute(a);
					continue;
				}
			}
		}
		
		
		
		// if we are to check the body, make sure that there wasn't a bad login
		if(checkBody)
		{
			String b = body.toString();
			if(b.indexOf("login") != -1 || ((b.indexOf("invalid") != -1 || b.indexOf("not valid") != -1) && b.indexOf("password") != -1) ||  b.indexOf("check e-mail") != -1)
				possibleBadLogin = true; // we possibly have a bad login
		}

		// fixed bug 59
		// if there is no summary or created date, we expect that
		// the bug doesn't exist, so set it to null
		
		// if the bug seems like it doesn't exist, and we suspect a login problem, assume that there was a login problem
		if(bug.getCreated() == null && bug.getAttributes().isEmpty()) {
			if (possibleBadLogin) {
				throw new LoginException("Bugzilla login information incorrect");
			}
			else {
				return null;
			}
		}	
		// we are done...return the bug    
		return bug;
	}

	/**
	 * Parse the operations that are allowed on the bug (Assign, Re-open, fix)
	 * @param bug The bug to add the operations to
	 * @param tokenizer The stream tokenizer for the bug
	 * @param tag The last tag that we were on
	 */
	private static void parseOperations(BugReport bug, HtmlStreamTokenizer tokenizer, HtmlTag tag, boolean is218) throws ParseException, IOException {
		
		String knobName = tag.getAttribute("value");
		boolean isChecked = false;
		if(tag.getAttribute("checked") != null && tag.getAttribute("checked").equals("checked"))
			isChecked = true;
		StringBuffer sb = new StringBuffer();
		
		Token lastTag = null;
		
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if(token.getType() == Token.TAG)
			{
				tag = (HtmlTag)token.getValue();
                
				if(!(tag.getTagType() == HtmlTag.Type.A || tag.getTagType() == HtmlTag.Type.B || tag.getTagType() == HtmlTag.Type.STRONG || tag.getTagType() == HtmlTag.Type.LABEL))
				{
					lastTag = token;
					break;
				}
				else{
                    
                    if(is218 && tag.getTagType() == HtmlTag.Type.LABEL){
                        continue;
                    }                        
                    else if(tag.getTagType() == HtmlTag.Type.A || tag.getTagType() == HtmlTag.Type.B || tag.getTagType() == HtmlTag.Type.STRONG){
                        sb.append(tag.toString().trim() + " ");
                    } else {
                        break;
                    }
                }
			}
			else if(token.getType() == Token.TEXT && !token.toString().trim().equals("\n"))
				sb.append(token.toString().trim() + " ");
		}
					
		String displayName = HtmlStreamTokenizer.unescape(sb).toString();
		Operation o = new Operation(knobName, displayName);
		o.setChecked(isChecked);
		
		if(lastTag != null)
		{
			tag = (HtmlTag)lastTag.getValue();
			if(tag.getTagType() != HtmlTag.Type.SELECT)
			{
				tokenizer.pushback(lastTag);
				if(tag.getTagType() == HtmlTag.Type.INPUT && !("radio".equalsIgnoreCase(tag.getAttribute("type")) && "knob".equalsIgnoreCase(tag.getAttribute("name"))))
				{
					o.setInputName(((HtmlTag)lastTag.getValue()).getAttribute("name"));
					o.setInputValue(((HtmlTag)lastTag.getValue()).getAttribute("value"));
				}
			}
			else
			{
				Token token = tokenizer.nextToken();
				// parse the options

				tag = (HtmlTag)token.getValue();
				o.setUpOptions(((HtmlTag)lastTag.getValue()).getAttribute("name"));
				
				
				while ( token.getType() != Token.EOF) {
					if (token.getType() == Token.TAG) {
						tag = (HtmlTag) token.getValue();
						if (tag.getTagType() == HtmlTag.Type.SELECT && tag.isEndTag()) break;
						if (tag.getTagType() == HtmlTag.Type.OPTION && !tag.isEndTag()) {
							String optionName = tag.getAttribute("value");
							StringBuffer optionText = new StringBuffer();
							for (token = tokenizer.nextToken(); token.getType() == Token.TEXT; token = tokenizer.nextToken()) {
								if (optionText.length() > 0) {
									optionText.append(' ');
								}
								optionText.append((StringBuffer) token.getValue());
							}
							o.addOption(optionText.toString(), optionName);
						}
						else {
							token = tokenizer.nextToken();
						}
					}
					else {
						token = tokenizer.nextToken();
					}
				}
			}
		}
		
		bug.addOperation(o);
	}

	/**
	 * Enum class for describing current state of Bugzilla report parser.
	 */
	private static class ParserState 
	{
		/** An instance of the start state */
		protected static final ParserState START = new ParserState("start");
	
		/** An instance of the state when the parser found an attribute name */
		protected static final ParserState ATT_NAME = new ParserState("att_name");
	
		/** An instance of the state when the parser found an attribute value */
		protected static final ParserState ATT_VALUE = new ParserState("att_value");
	
		/** An instance of the state when the parser found a description */
		protected static final ParserState DESC_START = new ParserState("desc_start");
	
		/** An instance of the state when the parser found a description value */
		protected static final ParserState DESC_VALUE = new ParserState("desc_value");

		/** State's human-readable name */
		private String name;

		/**
		 * Constructor
		 * @param description - The states human readable name
		 */
		private ParserState(String description) 
		{
			this.name = description;
		}

		@Override
		public String toString() 
		{
			return name;
		}
	}
}
