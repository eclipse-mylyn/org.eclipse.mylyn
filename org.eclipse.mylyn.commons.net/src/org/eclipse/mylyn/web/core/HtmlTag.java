/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.web.core;

import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.text.html.HTML.Tag;

/**
 * Class representing an HTML (3.2) tag and its attributes.
 * 
 * @author Shawn Minto
 * @since 2.0
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
	private final URL baseUrl;

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
		tagName = htmltag.tagName;
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
		if (s == null || s.length() == 0) {
			throw new IllegalArgumentException("Empty tag name");
		}
		if (s.charAt(0) == '/') {
			isEndTag = true;
			s = s.substring(1);
		}
		if (s.length() == 0) {
			throw new IllegalArgumentException("Empty tag name");
		}
		tagName = s;
		tagType = tags.get(s.toUpperCase(Locale.ENGLISH));
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
	 * Returns <code>true</code> if the tag contains attribute with the given name.
	 */
	public boolean hasAttribute(String s) {
		return getAttribute(s) != null;
	}

	/**
	 * Sets the value of a tag's attribute.
	 */
	public void setAttribute(String name, String value) {
		attributes.put(name.toLowerCase(Locale.ENGLISH), value);
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
							if (i > 0) {
								target = target.substring(i + 1);
							}
							break;
						}
					}
					target = target.replace('\\', '/');

					if (target.startsWith("news:") || (target.indexOf("://") != -1 && target.length() >= 7)) {
						// Absolute URL
						if (target.substring(0, 7).compareToIgnoreCase("http://") == 0) {
							sb.append(target);
						}
					} else {
						// Relative URL

						String baseDir = baseUrl.getPath();
						int lastSep = -1;
						for (int i = 0; i < baseDir.length(); i++) {
							char ch = baseDir.charAt(i);
							if (ch == '/') {
								lastSep = i;
							} else if (ch == '?') {
								break;
							}
						}
						if (lastSep >= 0) {
							baseDir = baseDir.substring(0, lastSep);
						}
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
		if (isEndTag) {
			sb.append('/');
		}
		sb.append(tagName);
		Iterator<String> keys = attributes.keySet().iterator();
		Iterator<String> values = attributes.values().iterator();
		while (keys.hasNext()) {
			String name = keys.next();
			sb.append(' ');
			sb.append(name);
			String value = values.next();
			sb.append("=\"");
			if (value.length() > 0) {
				sb.append(value);
			}
			sb.append('"');
		}
		if (selfTerminating) {
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
		tags.put("A", Tag.A);
		tags.put("ADDRESS", Tag.ADDRESS);
		tags.put("APPLET", Tag.APPLET);
		tags.put("AREA", Tag.AREA);
		tags.put("B", Tag.B);
		tags.put("BASE", Tag.BASE);
		tags.put("BASEFONT", Tag.BASEFONT);
		tags.put("BIG", Tag.BIG);
		tags.put("BLOCKQUOTE", Tag.BLOCKQUOTE);
		tags.put("BODY", Tag.BODY);
		tags.put("BR", Tag.BR);
		tags.put("CAPTION", Tag.CAPTION);
		tags.put("CENTER", Tag.CENTER);
		tags.put("CITE", Tag.CITE);
		tags.put("CODE", Tag.CODE);
		tags.put("DD", Tag.DD);
		tags.put("DFN", Tag.DFN);
		tags.put("DIR", Tag.DIR);
		tags.put("DIV", Tag.DIV);
		tags.put("DL", Tag.DL);
		tags.put("!DOCTYPE", Type.DOCTYPE);
		tags.put("DT", Tag.DT);
		tags.put("EM", Tag.EM);
		tags.put("FONT", Tag.FONT);
		tags.put("FORM", Tag.FORM);
		tags.put("FRAME", Tag.FRAME);
		tags.put("FRAMESET", Tag.FRAMESET);
		tags.put("H1", Tag.H1);
		tags.put("H2", Tag.H2);
		tags.put("H3", Tag.H3);
		tags.put("H4", Tag.H4);
		tags.put("H5", Tag.H5);
		tags.put("H6", Tag.H6);
		tags.put("HEAD", Tag.HEAD);
		tags.put("HTML", Tag.HTML);
		tags.put("HR", Tag.HR);
		tags.put("I", Tag.I);
		tags.put("IMG", Tag.IMG);
		tags.put("INPUT", Tag.INPUT);
		tags.put("ISINDEX", Tag.ISINDEX);
		tags.put("KBD", Tag.KBD);
		tags.put("LI", Tag.LI);
		tags.put("LABEL", Type.LABEL);
		tags.put("LINK", Tag.LINK);
		tags.put("MAP", Tag.MAP);
		tags.put("MENU", Tag.MENU);
		tags.put("META", Tag.META);
		tags.put("NOFRAMES", Tag.NOFRAMES);
		tags.put("OBJECT", Tag.OBJECT);
		tags.put("OL", Tag.OL);
		tags.put("OPTION", Tag.OPTION);
		tags.put("P", Tag.P);
		tags.put("PARAM", Tag.PARAM);
		tags.put("PRE", Tag.PRE);
		tags.put("S", Tag.S);
		tags.put("SAMP", Tag.SAMP);
		tags.put("SCRIPT", Tag.SCRIPT);
		tags.put("SELECT", Tag.SELECT);
		tags.put("SMALL", Tag.SMALL);
		tags.put("SPAN", Tag.SPAN);
		tags.put("STRONG", Tag.STRONG);
		tags.put("STYLE", Tag.STYLE);
		tags.put("SUB", Tag.SUB);
		tags.put("SUP", Tag.SUP);
		tags.put("TABLE", Tag.TABLE);
		tags.put("TD", Tag.TD);
		tags.put("TEXTAREA", Tag.TEXTAREA);
		tags.put("TH", Tag.TH);
		tags.put("THEAD", Type.THEAD);
		tags.put("TITLE", Tag.TITLE);
		tags.put("TR", Tag.TR);
		tags.put("TT", Tag.TT);
		tags.put("U", Tag.U);
		tags.put("UL", Tag.UL);
		tags.put("VAR", Tag.VAR);
	}

	public void setSelfTerminating(boolean terminating) {
		this.selfTerminating = terminating;

	}

	public boolean isSelfTerminating() {
		return selfTerminating;
	}
}
