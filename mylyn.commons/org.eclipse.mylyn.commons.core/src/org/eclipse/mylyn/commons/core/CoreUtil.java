/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @since 3.0
 * @author Steffen Pingel
 * @author Benjamin Muskalla
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CoreUtil {

	/**
	 * @since 3.0
	 */
	public static final boolean TEST_MODE;

	static {
		String application = System.getProperty("eclipse.application", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (application.length() > 0) {
			TEST_MODE = application.endsWith("testapplication") || application.endsWith("uitest"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			// eclipse 3.3 does not the eclipse.application property
			String commands = System.getProperty("eclipse.commands", ""); //$NON-NLS-1$ //$NON-NLS-2$
			TEST_MODE = commands.contains("testapplication\n"); //$NON-NLS-1$
		}
	}

	private static final String FRAMEWORK_VERSION = "4.0.0"; //$NON-NLS-1$

	private static final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	static {
		saxParserFactory.setNamespaceAware(true);
	}

	/**
	 * Returns a string representation of <code>object</code>. If object is a map or array the returned string will contains a comma
	 * separated list of contained elements.
	 *
	 * @since 3.4
	 */
	public static String toString(Object object) {
		StringBuilder sb = new StringBuilder();
		toString(sb, object);
		return sb.toString();
	}

	private static void toString(StringBuilder sb, Object object) {
		if (object instanceof Object[]) {
			sb.append("["); //$NON-NLS-1$
			Object[] entries = (Object[]) object;
			boolean prependSeparator = false;
			for (Object entry : entries) {
				if (prependSeparator) {
					sb.append(", "); //$NON-NLS-1$
				}
				toString(sb, entry);
				prependSeparator = true;
			}
			sb.append("]"); //$NON-NLS-1$
		} else if (object instanceof Map<?, ?>) {
			sb.append("{"); //$NON-NLS-1$
			boolean prependSeparator = false;
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
				if (prependSeparator) {
					sb.append(", "); //$NON-NLS-1$
				}
				toString(sb, entry.getKey());
				sb.append("="); //$NON-NLS-1$
				toString(sb, entry.getValue());
				prependSeparator = true;
			}
			sb.append("}"); //$NON-NLS-1$
		} else {
			sb.append(object);
		}
	}

	/**
	 * Returns the version of the bundle.
	 *
	 * @since 3.7
	 * @deprecated use {@link Bundle#getVersion()} instead
	 */
	@Deprecated
	public static Version getVersion(Bundle bundle) {
		return bundle.getVersion();
	}

	/**
	 * Returns true, if <code>o1</code> is equal to <code>o2</code> or <code>o1</code> and <code>o2</code> are <code>null</code>.
	 *
	 * @see Object#equals(Object)
	 * @since 3.7
	 */
	public static boolean areEqual(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
	}

	/**
	 * Compares <code>o1</code> and <code>o2</code>.
	 *
	 * @since 3.7
	 * @return a negative integer, 0, or a positive, if o1 is less than o2, o1 equals o2 or o1 is more than o2; null is considered less than
	 *         any value
	 */
	public static <T> int compare(Comparable<T> o1, T o2) {
		if (o1 == null) {
			return o2 != null ? 1 : 0;
		} else if (o2 == null) {
			return -1;
		}
		return o1.compareTo(o2);
	}

	/**
	 * Compares a boolean value.
	 *
	 * @since 3.7
	 * @see Boolean#equals(Object)
	 */
	public static boolean propertyEquals(boolean value, Object expectedValue) {
		return expectedValue == null ? value : Boolean.valueOf(value).equals(expectedValue);
	}

	/**
	 * Disables logging through the Apache commons logging system by default. This can be overridden by specifying the
	 * <code>org.apache.commons.logging.Log</code> system property.
	 *
	 * @since 3.7
	 */
	public static void initializeLoggingSettings() {
		defaultSystemProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Only sets system property if they are not already set to a value.
	 */
	private static void defaultSystemProperty(String key, String defaultValue) {
		if (System.getProperty(key) == null) {
			System.setProperty(key, defaultValue);
		}
	}

	/**
	 * Returns the version of the Java runtime.
	 *
	 * @since 3.7
	 * @return {@link Version#emptyVersion} if the version can not be determined
	 */
	public static Version getRuntimeVersion() {
		Version result = parseRuntimeVersion(Runtime.version().toString());
		return result;
	}

	/**
	 * @since 4.3
	 */
	public static Version parseRuntimeVersion(String versionString) {
		if (versionString != null) {
			int firstSeparator = versionString.indexOf('.');
			if (firstSeparator != -1) {
				try {
					int secondSeparator = versionString.indexOf('.', firstSeparator + 1);
					if (secondSeparator != -1) {
						int index = findLastNumberIndex(versionString, secondSeparator);
						String qualifier = versionString.substring(index + 1);
						if ((qualifier.startsWith("_") || qualifier.startsWith("+")) && qualifier.length() > 1) { //$NON-NLS-1$
							versionString = versionString.substring(0, index + 1) + "." + qualifier.substring(1); //$NON-NLS-1$
						} else {
							versionString = versionString.substring(0, index + 1);
						}
						return new Version(versionString);
					}
					return new Version(
							versionString.substring(0, findLastNumberIndex(versionString, firstSeparator) + 1));
				} catch (IllegalArgumentException e) {
					// ignore
				}
			} else { // Java 22 seems to have changed the format for some reason to "nn+nn"
				int plusSeparator = versionString.indexOf('+');
				if (plusSeparator != -1) {
					return new Version(
							versionString.substring(0, plusSeparator));
				}
			}
		}
		return Version.emptyVersion;
	}

	private static int findLastNumberIndex(String versionString, int secondSeparator) {
		int lastDigit = secondSeparator;
		for (int i = secondSeparator + 1; i < versionString.length(); i++) {
			if (Character.isDigit(versionString.charAt(i))) {
				lastDigit++;
			} else {
				break;
			}
		}
		if (lastDigit == secondSeparator) {
			return secondSeparator - 1;
		}
		return lastDigit;
	}

	/**
	 * Returns the running Mylyn version without the qualifier.
	 *
	 * @since 3.7
	 */
	public static Version getFrameworkVersion() {
		return new Version(FRAMEWORK_VERSION);
	}

	/**
	 * Returns a representation of <code>name</code> that is a valid file name.
	 *
	 * @since 3.7
	 */
	public static String asFileName(String name) {
		StringBuilder sb = new StringBuilder(name.length());
		char[] chars = name.toCharArray();
		for (char c : chars) {
			if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '.') {
				sb.append(c);
			} else {
				sb.append("%" + Integer.toHexString(c).toUpperCase()); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the decoded form of <code>text</code>.
	 *
	 * @since 3.8
	 * @see #encode(String)
	 * @throws IllegalArgumentException
	 *             if text is not in a valid form, i.e. it was not encoded using {@link CoreUtil#encode(String)}
	 */
	public static String decode(String text) {
		boolean escaped = false;
		StringBuilder sb = new StringBuilder(text.length());
		StringBuilder escapedText = new StringBuilder(4);
		char[] chars = text.toCharArray();
		for (char c : chars) {
			if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '.') {
				if (escaped) {
					escapedText.append(c);
				} else {
					sb.append(c);
				}
			} else if (c == '%') {
				if (escaped) {
					throw new IllegalArgumentException("Unexpected '%' sign in '" + text + "'"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				escaped = !escaped;
			} else if (c == '_') {
				if (!escaped) {
					throw new IllegalArgumentException("Unexpected '_' sign in '" + text + "'"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				try {
					sb.append((char) Integer.parseInt(escapedText.toString(), 16));
					escapedText.setLength(0);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid escape code in '" + text + "'"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				escaped = !escaped;
			} else {
				throw new IllegalArgumentException("Unexpected character '" + c + "' in '" + text + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		return sb.toString();
	}

	/**
	 * An encoded form of <code>text</code> that is suitable as a filename.
	 *
	 * @param text
	 *            the string to encode
	 * @see #decode(String)
	 * @since 3.8
	 */
	public static String encode(String text) {
		StringBuilder sb = new StringBuilder(text.length());
		char[] chars = text.toCharArray();
		for (char c : chars) {
			if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '.') {
				sb.append(c);
			} else {
				sb.append("%"); //$NON-NLS-1$
				sb.append(Integer.toHexString(c).toUpperCase());
				sb.append("_"); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	/**
	 * Returns a new {@link XMLReader} instance using default factories.
	 *
	 * @since 3.9
	 */
	public static SAXParser newSaxParser() throws SAXException {
		try {
			return saxParserFactory.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new SAXException(e);
		}
	}

	/**
	 * Returns a new {@link XMLReader} instance using default factories.
	 *
	 * @since 3.9
	 */
	public static XMLReader newXmlReader() throws SAXException {
		return newSaxParser().getXMLReader();
	}

}
