/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.net;

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
			throw new IllegalArgumentException("Empty tag name"); //$NON-NLS-1$
		}
		if (s.charAt(0) == '/') {
			isEndTag = true;
			s = s.substring(1);
		}
		if (s.length() == 0) {
			throw new IllegalArgumentException("Empty tag name"); //$NON-NLS-1$
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
			if (attributeName.compareTo("href") == 0 || attributeName.compareTo("src") == 0) { //$NON-NLS-1$ //$NON-NLS-2$
				String target = attributeValues.next();
				if (!target.endsWith(".jpg") && !target.endsWith(".gif") && !target.endsWith(".css") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						&& !target.endsWith(".js") && !target.startsWith("mailto") && target.lastIndexOf("#") == -1 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

					if (target.startsWith("news:") || (target.indexOf("://") != -1 && target.length() >= 7)) { //$NON-NLS-1$ //$NON-NLS-2$
						// Absolute URL
						if (target.substring(0, 7).compareToIgnoreCase("http://") == 0) { //$NON-NLS-1$
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
						while (baseDir.length() > 1 && baseDir.endsWith("/.")) { //$NON-NLS-1$
							baseDir = baseDir.substring(0, baseDir.length() - 2);
						}

						if (target.startsWith("//")) { //$NON-NLS-1$
							sb.append(baseUrl.getProtocol() + ":" + target); //$NON-NLS-1$
						} else if (target.startsWith("/")) { //$NON-NLS-1$
							sb.append(baseUrl.getProtocol() + "://" + baseUrl.getHost() + target); //$NON-NLS-1$
						} else {
							while (target.startsWith("../")) { //$NON-NLS-1$
								if (baseDir.length() > 0) {
									// can't go above root
									baseDir = baseDir.substring(0, baseDir.lastIndexOf("/")); //$NON-NLS-1$
								}
								target = target.substring(3);
							}
							sb.append(baseUrl.getProtocol() + "://" + baseUrl.getHost() + baseDir + "/" + target); //$NON-NLS-1$ //$NON-NLS-2$
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
			sb.append("=\""); //$NON-NLS-1$
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

		public static final Tag THEAD = new Type("THEAD"); //$NON-NLS-1$

		public static final Tag DOCTYPE = new Type("!DOCTYPE"); //$NON-NLS-1$

		public static final Tag LABEL = new Type("LABEL"); //$NON-NLS-1$

		private Type(String name) {
			super(name);
		}
	}

	private static HashMap<String, Tag> tags;
	static {
		tags = new HashMap<String, Tag>();
		tags.put("A", Tag.A); //$NON-NLS-1$
		tags.put("ADDRESS", Tag.ADDRESS); //$NON-NLS-1$
		tags.put("APPLET", Tag.APPLET); //$NON-NLS-1$
		tags.put("AREA", Tag.AREA); //$NON-NLS-1$
		tags.put("B", Tag.B); //$NON-NLS-1$
		tags.put("BASE", Tag.BASE); //$NON-NLS-1$
		tags.put("BASEFONT", Tag.BASEFONT); //$NON-NLS-1$
		tags.put("BIG", Tag.BIG); //$NON-NLS-1$
		tags.put("BLOCKQUOTE", Tag.BLOCKQUOTE); //$NON-NLS-1$
		tags.put("BODY", Tag.BODY); //$NON-NLS-1$
		tags.put("BR", Tag.BR); //$NON-NLS-1$
		tags.put("CAPTION", Tag.CAPTION); //$NON-NLS-1$
		tags.put("CENTER", Tag.CENTER); //$NON-NLS-1$
		tags.put("CITE", Tag.CITE); //$NON-NLS-1$
		tags.put("CODE", Tag.CODE); //$NON-NLS-1$
		tags.put("DD", Tag.DD); //$NON-NLS-1$
		tags.put("DFN", Tag.DFN); //$NON-NLS-1$
		tags.put("DIR", Tag.DIR); //$NON-NLS-1$
		tags.put("DIV", Tag.DIV); //$NON-NLS-1$
		tags.put("DL", Tag.DL); //$NON-NLS-1$
		tags.put("!DOCTYPE", Type.DOCTYPE); //$NON-NLS-1$
		tags.put("DT", Tag.DT); //$NON-NLS-1$
		tags.put("EM", Tag.EM); //$NON-NLS-1$
		tags.put("FONT", Tag.FONT); //$NON-NLS-1$
		tags.put("FORM", Tag.FORM); //$NON-NLS-1$
		tags.put("FRAME", Tag.FRAME); //$NON-NLS-1$
		tags.put("FRAMESET", Tag.FRAMESET); //$NON-NLS-1$
		tags.put("H1", Tag.H1); //$NON-NLS-1$
		tags.put("H2", Tag.H2); //$NON-NLS-1$
		tags.put("H3", Tag.H3); //$NON-NLS-1$
		tags.put("H4", Tag.H4); //$NON-NLS-1$
		tags.put("H5", Tag.H5); //$NON-NLS-1$
		tags.put("H6", Tag.H6); //$NON-NLS-1$
		tags.put("HEAD", Tag.HEAD); //$NON-NLS-1$
		tags.put("HTML", Tag.HTML); //$NON-NLS-1$
		tags.put("HR", Tag.HR); //$NON-NLS-1$
		tags.put("I", Tag.I); //$NON-NLS-1$
		tags.put("IMG", Tag.IMG); //$NON-NLS-1$
		tags.put("INPUT", Tag.INPUT); //$NON-NLS-1$
		tags.put("ISINDEX", Tag.ISINDEX); //$NON-NLS-1$
		tags.put("KBD", Tag.KBD); //$NON-NLS-1$
		tags.put("LI", Tag.LI); //$NON-NLS-1$
		tags.put("LABEL", Type.LABEL); //$NON-NLS-1$
		tags.put("LINK", Tag.LINK); //$NON-NLS-1$
		tags.put("MAP", Tag.MAP); //$NON-NLS-1$
		tags.put("MENU", Tag.MENU); //$NON-NLS-1$
		tags.put("META", Tag.META); //$NON-NLS-1$
		tags.put("NOFRAMES", Tag.NOFRAMES); //$NON-NLS-1$
		tags.put("OBJECT", Tag.OBJECT); //$NON-NLS-1$
		tags.put("OL", Tag.OL); //$NON-NLS-1$
		tags.put("OPTION", Tag.OPTION); //$NON-NLS-1$
		tags.put("P", Tag.P); //$NON-NLS-1$
		tags.put("PARAM", Tag.PARAM); //$NON-NLS-1$
		tags.put("PRE", Tag.PRE); //$NON-NLS-1$
		tags.put("S", Tag.S); //$NON-NLS-1$
		tags.put("SAMP", Tag.SAMP); //$NON-NLS-1$
		tags.put("SCRIPT", Tag.SCRIPT); //$NON-NLS-1$
		tags.put("SELECT", Tag.SELECT); //$NON-NLS-1$
		tags.put("SMALL", Tag.SMALL); //$NON-NLS-1$
		tags.put("SPAN", Tag.SPAN); //$NON-NLS-1$
		tags.put("STRONG", Tag.STRONG); //$NON-NLS-1$
		tags.put("STYLE", Tag.STYLE); //$NON-NLS-1$
		tags.put("SUB", Tag.SUB); //$NON-NLS-1$
		tags.put("SUP", Tag.SUP); //$NON-NLS-1$
		tags.put("TABLE", Tag.TABLE); //$NON-NLS-1$
		tags.put("TD", Tag.TD); //$NON-NLS-1$
		tags.put("TEXTAREA", Tag.TEXTAREA); //$NON-NLS-1$
		tags.put("TH", Tag.TH); //$NON-NLS-1$
		tags.put("THEAD", Type.THEAD); //$NON-NLS-1$
		tags.put("TITLE", Tag.TITLE); //$NON-NLS-1$
		tags.put("TR", Tag.TR); //$NON-NLS-1$
		tags.put("TT", Tag.TT); //$NON-NLS-1$
		tags.put("U", Tag.U); //$NON-NLS-1$
		tags.put("UL", Tag.UL); //$NON-NLS-1$
		tags.put("VAR", Tag.VAR); //$NON-NLS-1$
	}

	public void setSelfTerminating(boolean terminating) {
		this.selfTerminating = terminating;

	}

	public boolean isSelfTerminating() {
		return selfTerminating;
	}
}
