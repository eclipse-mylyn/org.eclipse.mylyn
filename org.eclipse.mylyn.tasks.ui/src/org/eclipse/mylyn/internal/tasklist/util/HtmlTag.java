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

package org.eclipse.mylar.internal.tasklist.util;

import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.text.html.HTML.Tag;

/**
 * Class representing an HTML (3.2) tag and its attributes.
 */
public class HtmlTag {
	/** tag's name */
	private String tagName;

	/** tag type enum */
	private Tag tagType;

	/** true if the tag is a closing tag */
	private boolean isEndTag;

	/** tag's attributes (keys are lowercase attribute names) */
	private HashMap<String, String> attributes;

	/** tag's base url */
	private URL baseUrl;
	
	/** tag is self terminated */
	private boolean selfTerminating;

	/**
	 * Basic constructor. The tag is uninitialized.
	 */
	public HtmlTag() {
		tagName = null;
		tagType = Type.UNKNOWN;
		isEndTag = false;
		attributes = new HashMap<String, String>();
		baseUrl = null;
	}

	/**
	 * Copy constructor.
	 */
	@SuppressWarnings("unchecked")
	public HtmlTag(HtmlTag htmltag) {
		tagName = null;
		tagType = Type.UNKNOWN;
		isEndTag = false;
		attributes = new HashMap<String, String>();
		tagName = new String(htmltag.tagName);
		baseUrl = htmltag.baseUrl;
		tagType = htmltag.tagType;
		isEndTag = htmltag.isEndTag;
		attributes = (HashMap) htmltag.attributes.clone();
	}

	/**
	 * Constructor.
	 */
	public HtmlTag(String s) throws ParseException {
		attributes = new HashMap<String, String>();
		setTagName(s);
		baseUrl = null;
	}

	/**
	 * Constructor creating an otherwise empty tag, but with a given base url.
	 */
	public HtmlTag(URL url) {
		tagName = null;
		tagType = Type.UNKNOWN;
		isEndTag = false;
		attributes = new HashMap<String, String>();
		baseUrl = url;
	}

	/**
	 * Returns the tag's type (linked to the tag's name).
	 */
	public Tag getTagType() {
		return tagType;
	}

	/**
	 * Returns the tag's name (e.g., "HEAD", "P", etc.).
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Sets the tag's name and type, if known.
	 * 
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code> or empty string
	 */
	public void setTagName(String s) throws IllegalArgumentException {
		if (s == null || s.length() == 0)
			throw new IllegalArgumentException("Empty tag name");
		if (s.charAt(0) == '/') {
			isEndTag = true;
			s = s.substring(1);
		}
		if (s.length() == 0)
			throw new IllegalArgumentException("Empty tag name");
		tagName = s;
		tagType = tags.get(s.toUpperCase());
		if (tagType == null) {
			tagType = Type.UNKNOWN;
		}
	}

	/**
	 * Returns <code>true</code> if the tag is a closing tag.
	 */
	public boolean isEndTag() {
		return isEndTag;
	}

	/**
	 * Returns the value of a tag's attribute as an integer.
	 */
	public int getIntAttribute(String s) throws NumberFormatException {
		return Integer.parseInt(getAttribute(s));
	}

	/**
	 * Returns the value of a tag's attribute, or NULL if it doesn't exist.
	 */
	public String getAttribute(String s) {
		return attributes.get(s);
	}

	/**
	 * Returns <code>true</code> if the tag contains attribute with the given
	 * name.
	 */
	public boolean hasAttribute(String s) {
		return getAttribute(s) != null;
	}

	/**
	 * Sets the value of a tag's attribute.
	 */
	public void setAttribute(String name, String value) {
		attributes.put(name.toLowerCase(), value);
	}

	public StringBuffer getURLs() {
		StringBuffer sb = new StringBuffer();

		Iterator<String> attributeNames = attributes.keySet().iterator();
		Iterator<String> attributeValues = attributes.values().iterator();
		while (attributeNames.hasNext()) {
			String attributeName = attributeNames.next();
			if (attributeName.compareTo("href") == 0 || attributeName.compareTo("src") == 0) {
				String target = attributeValues.next();
				if (!target.endsWith(".jpg") && !target.endsWith(".gif") && !target.endsWith(".css")
						&& !target.endsWith(".js") && !target.startsWith("mailto") && target.lastIndexOf("#") == -1
						&& target.length() > 0) {

					for (int i = 0; i < target.length(); i++) {
						char ch = target.charAt(i);
						if (!Character.isWhitespace(ch)) {
							if (i > 0)
								target = target.substring(i + 1);
							break;
						}
					}
					target = target.replace('\\', '/');

					if (target.startsWith("news:") || (target.indexOf("://") != -1 && target.length() >= 7)) {
						// Absolute URL
						if (target.substring(0, 7).compareToIgnoreCase("http://") == 0)
							sb.append(target);
					} else {
						// Relative URL

						String baseDir = baseUrl.getPath();
						int lastSep = -1;
						for (int i = 0; i < baseDir.length(); i++) {
							char ch = baseDir.charAt(i);
							if (ch == '/')
								lastSep = i;
							else if (ch == '?')
								break;
						}
						if (lastSep >= 0)
							baseDir = baseDir.substring(0, lastSep);
						while (baseDir.length() > 1 && baseDir.endsWith("/.")) {
							baseDir = baseDir.substring(0, baseDir.length() - 2);
						}

						if (target.startsWith("//")) {
							sb.append(baseUrl.getProtocol() + ":" + target);
						} else if (target.startsWith("/")) {
							sb.append(baseUrl.getProtocol() + "://" + baseUrl.getHost() + target);
						} else {
							while (target.startsWith("../")) {
								if (baseDir.length() > 0) {
									// can't go above root
									baseDir = baseDir.substring(0, baseDir.lastIndexOf("/"));
								}
								target = target.substring(3);
							}
							sb.append(baseUrl.getProtocol() + "://" + baseUrl.getHost() + baseDir + "/" + target);
						}
					}
				}
			} else {
				attributeValues.next();
			}
		}

		return sb;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('<');
		if (isEndTag)
			sb.append('/');
		sb.append(tagName);
		Iterator<String> keys = attributes.keySet().iterator();
		Iterator<String> values = attributes.values().iterator();
		while (keys.hasNext()) {
			String name = keys.next();
			sb.append(' ');
			sb.append(name);
			String value = values.next();
			if (value.length() > 0) {
				sb.append("=\"");
				sb.append(value);
				sb.append('"');
			}
		}
		if(selfTerminating) {
			sb.append('/');
		}
		sb.append('>');

		return sb.toString();
	}

	/**
	 * Enum class for tag types.
	 */
	public static class Type extends Tag {
		public static final Tag UNKNOWN = new Tag();

		public static final Tag THEAD = new Type("THEAD");

		public static final Tag DOCTYPE = new Type("!DOCTYPE");

		public static final Tag LABEL = new Type("LABEL");

		private Type(String name) {
			super(name);
		}
	}

	private static HashMap<String, Tag> tags;
	static {
		tags = new HashMap<String, Tag>();
		tags.put(new String("A"), Tag.A);
		tags.put(new String("ADDRESS"), Tag.ADDRESS);
		tags.put(new String("APPLET"), Tag.APPLET);
		tags.put(new String("AREA"), Tag.AREA);
		tags.put(new String("B"), Tag.B);
		tags.put(new String("BASE"), Tag.BASE);
		tags.put(new String("BASEFONT"), Tag.BASEFONT);
		tags.put(new String("BIG"), Tag.BIG);
		tags.put(new String("BLOCKQUOTE"), Tag.BLOCKQUOTE);
		tags.put(new String("BODY"), Tag.BODY);
		tags.put(new String("BR"), Tag.BR);
		tags.put(new String("CAPTION"), Tag.CAPTION);
		tags.put(new String("CENTER"), Tag.CENTER);
		tags.put(new String("CITE"), Tag.CITE);
		tags.put(new String("CODE"), Tag.CODE);
		tags.put(new String("DD"), Tag.DD);
		tags.put(new String("DFN"), Tag.DFN);
		tags.put(new String("DIR"), Tag.DIR);
		tags.put(new String("DIV"), Tag.DIV);
		tags.put(new String("DL"), Tag.DL);
		tags.put(new String("!DOCTYPE"), Type.DOCTYPE);
		tags.put(new String("DT"), Tag.DT);
		tags.put(new String("EM"), Tag.EM);
		tags.put(new String("FONT"), Tag.FONT);
		tags.put(new String("FORM"), Tag.FORM);
		tags.put(new String("FRAME"), Tag.FRAME);
		tags.put(new String("FRAMESET"), Tag.FRAMESET);
		tags.put(new String("H1"), Tag.H1);
		tags.put(new String("H2"), Tag.H2);
		tags.put(new String("H3"), Tag.H3);
		tags.put(new String("H4"), Tag.H4);
		tags.put(new String("H5"), Tag.H5);
		tags.put(new String("H6"), Tag.H6);
		tags.put(new String("HEAD"), Tag.HEAD);
		tags.put(new String("HTML"), Tag.HTML);
		tags.put(new String("HR"), Tag.HR);
		tags.put(new String("I"), Tag.I);
		tags.put(new String("IMG"), Tag.IMG);
		tags.put(new String("INPUT"), Tag.INPUT);
		tags.put(new String("ISINDEX"), Tag.ISINDEX);
		tags.put(new String("KBD"), Tag.KBD);
		tags.put(new String("LI"), Tag.LI);
		tags.put(new String("LABEL"), Type.LABEL);
		tags.put(new String("LINK"), Tag.LINK);
		tags.put(new String("MAP"), Tag.MAP);
		tags.put(new String("MENU"), Tag.MENU);
		tags.put(new String("META"), Tag.META);
		tags.put(new String("NOFRAMES"), Tag.NOFRAMES);
		tags.put(new String("OBJECT"), Tag.OBJECT);
		tags.put(new String("OL"), Tag.OL);
		tags.put(new String("OPTION"), Tag.OPTION);
		tags.put(new String("P"), Tag.P);
		tags.put(new String("PARAM"), Tag.PARAM);
		tags.put(new String("PRE"), Tag.PRE);
		tags.put(new String("S"), Tag.S);
		tags.put(new String("SAMP"), Tag.SAMP);
		tags.put(new String("SCRIPT"), Tag.SCRIPT);
		tags.put(new String("SELECT"), Tag.SELECT);
		tags.put(new String("SMALL"), Tag.SMALL);
		tags.put(new String("STRONG"), Tag.STRONG);
		tags.put(new String("STYLE"), Tag.STYLE);
		tags.put(new String("SUB"), Tag.SUB);
		tags.put(new String("SUP"), Tag.SUP);
		tags.put(new String("TABLE"), Tag.TABLE);
		tags.put(new String("TD"), Tag.TD);
		tags.put(new String("TEXTAREA"), Tag.TEXTAREA);
		tags.put(new String("TH"), Tag.TH);
		tags.put(new String("THEAD"), Type.THEAD);
		tags.put(new String("TITLE"), Tag.TITLE);
		tags.put(new String("TR"), Tag.TR);
		tags.put(new String("TT"), Tag.TT);
		tags.put(new String("U"), Tag.U);
		tags.put(new String("UL"), Tag.UL);
		tags.put(new String("VAR"), Tag.VAR);
	}
	
	public void setSelfTerminating(boolean terminating) {
		this.selfTerminating = terminating;
		
	}
	
	public boolean isSelfTerminating() {
		return selfTerminating;
	}
}
