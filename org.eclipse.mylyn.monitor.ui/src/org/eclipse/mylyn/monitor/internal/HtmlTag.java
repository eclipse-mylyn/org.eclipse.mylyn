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
package org.eclipse.mylar.monitor.internal;

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
		attributes = (HashMap)htmltag.attributes.clone();
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
	 * 		if the argument is <code>null</code> or empty string
	 */
	public void setTagName(String s) throws IllegalArgumentException {
	    if (s == null || s.length() == 0) throw new IllegalArgumentException("Empty tag name"); 
		if (s.charAt(0) == '/') {
			isEndTag = true;
			s = s.substring(1);
		}
	    if (s.length() == 0) throw new IllegalArgumentException("Empty tag name"); 
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
	 * Returns <code>true</code> if the tag contains attribute with the given name.
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
				if (!target.endsWith(".jpg")
					&& !target.endsWith(".gif")
					&& !target.endsWith(".css")
					&& !target.endsWith(".js")
					&& !target.startsWith("mailto")
					&& target.lastIndexOf("#") == -1
					&& target.length() > 0) {
						
					for (int i = 0; i < target.length(); i++) {
						char ch = target.charAt(i);
						if (!Character.isWhitespace(ch)) {
							if (i > 0) target = target.substring(i+1);
							break;
						}
					}
					target = target.replace('\\', '/');

					if (target.startsWith("news:") || (target.indexOf("://") != -1 && target.length() >= 7)) {
						// Absolute URL
						if (target.substring(0, 7).compareToIgnoreCase("http://") == 0)
							sb.append(target);
					} 
					else {
						// Relative URL

						String baseDir = baseUrl.getPath();
						int lastSep = -1;
						for (int i = 0; i < baseDir.length(); i++) {
							char ch = baseDir.charAt(i);
							if (ch == '/') lastSep = i;
							else if (ch == '?') break;
						}
						if (lastSep >= 0) baseDir = baseDir.substring(0, lastSep);
						while (baseDir.length() > 1 && baseDir.endsWith("/.")) {
							baseDir = baseDir.substring(0, baseDir.length()-2);
						}
						
						if (target.startsWith("//")) {
							sb.append(baseUrl.getProtocol() + ":" + target);
						}
						else if (target.startsWith("/")) {
							sb.append(baseUrl.getProtocol() + "://" + baseUrl.getHost() + target);
						}
						else {
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
			} 
			else {
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
		sb.append('>');

		return sb.toString();
	}

	/**
	 * Enum class for tag types.
	 */
	public static class Type extends Tag {
		public static final Tag UNKNOWN = new Tag();
//		public static final Tag THEAD = new Type("THEAD");
//		public static final Tag DOCTYPE = new Type("!DOCTYPE");
//		public static final Tag LABEL = new Type("LABEL");
		
//		<interactionEvent>
//		  <kind>PREFERENCE</kind>
//		  <date>2005-07-04 20:11:53.490 PDT</date>
//		  <endDate>2005-07-04 20:11:53.490 PDT</endDate>
//		  <originId>org.eclipse.mylar.ui.auto.fold.isChecked</originId>
//		  <structureKind>null</structureKind>
//		  <structureHandle>null</structureHandle>
//		  <navigation>null</navigation>
//		  <delta>true</delta>
//		  <interestContribution>1.0</interestContribution>
//		</interactionEvent>
		
		public static final Tag INTERACTION_EVENT = new Type("interactionEvent");
		public static final Tag KIND = new Type("kind");
		public static final Tag START_DATE = new Type("date");
		public static final Tag END_DATE = new Type("endDate");
		public static final Tag ORIGIN_ID = new Type("originId");
		public static final Tag STRUCTURE_KIND = new Type("structureKind");
		public static final Tag STRUCTURE_HANDLE = new Type("structureHandle");
		public static final Tag NAVIGATION = new Type("navigation");
		public static final Tag DELTA = new Type("delta");
		public static final Tag INTEREST_CONTRIBUTION = new Type("interestContribution");
		
		private Type(String name) {
		    super(name);
		}
	}

	private static HashMap<String, Tag> tags;
	static {
		tags = new HashMap<String, Tag>();
		tags.put(new String(Type.INTERACTION_EVENT.toString()), Type.INTERACTION_EVENT);
		tags.put(new String(Type.KIND.toString()), Type.KIND);
		tags.put(new String(Type.START_DATE.toString()), Type.START_DATE);
		tags.put(new String(Type.END_DATE.toString()), Type.END_DATE);
		tags.put(new String(Type.ORIGIN_ID.toString()), Type.ORIGIN_ID);
		tags.put(new String(Type.STRUCTURE_KIND.toString()), Type.STRUCTURE_KIND);
		tags.put(new String(Type.STRUCTURE_HANDLE.toString()), Type.STRUCTURE_HANDLE);
		tags.put(new String(Type.NAVIGATION.toString()), Type.NAVIGATION);
		tags.put(new String(Type.DELTA.toString()), Type.DELTA);
		tags.put(new String(Type.INTEREST_CONTRIBUTION.toString()), Type.INTEREST_CONTRIBUTION);
	}
}
