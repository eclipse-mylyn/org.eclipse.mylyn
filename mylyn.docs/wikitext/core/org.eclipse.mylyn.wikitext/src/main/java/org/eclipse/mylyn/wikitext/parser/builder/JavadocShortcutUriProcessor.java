/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder;

import javax.lang.model.SourceVersion;

/**
 * A {@link UriProcessor} that simplifies linking to javadoc documentation. Links that start with {@code "@"} are
 * considered candidates for this processor, with the following characters interpreted as a Java package or type name. A
 * base Java package name can be provided, which is denoted with a single dot {@code "."}.
 * <p>
 * With a base package of {@code "com.example"}, examples:
 * </p>
 *
 * <pre>
 * "@foo.bar"      -> "index.html?foo/bar/package-summary.html"
 * "@foo.Bar"      -> "index.html?foo/Bar.html"
 * "@.foo.bar"     -> "index.html?com/example/foo/bar/package-summary.html"
 * "@.foo.Bar"     -> "index.html?com/example/foo/Bar.html"
 * "javadoc://Foo" -> "index.html?com/example/Foo.html"
 * </pre>
 *
 * @see HtmlDocumentBuilder#addLinkUriProcessor(UriProcessor)
 * @since 3.1
 */
public class JavadocShortcutUriProcessor implements UriProcessor {

	private static final String TARGET = "_javadoc";

	private static final String JAVADOC_URI_MARKER = "@";

	private static final String JAVADOC_ABSOLUTE_URI_MARKER = "javadoc://";

	private static final String BASE_PACKAGE_MARKER = ".";

	private final String basePackageName;

	private final String javadocRelativePath;

	/**
	 * @param javadocRelativePath
	 *            the relative path to the root folder of the javadoc, e.g. {@code "../../"}, or null
	 * @param basePackageName
	 *            the base Java package name, or null
	 */
	public JavadocShortcutUriProcessor(String javadocRelativePath, String basePackageName) {
		this.javadocRelativePath = javadocRelativePath;
		this.basePackageName = basePackageName;
	}

	@Override
	public String process(String uri) {
		String newUri = preprocessUri(uri);
		if (!newUri.equals(uri)) {
			newUri = prependWithBasePackage(newUri);
			if (SourceVersion.isName(newUri)) {
				if (isPotentialPackageName(newUri)) {
					return toPackagePage(newUri);
				}
				return toTypePage(newUri);
			}
		}
		return uri;
	}

	@Override
	public String target(String uri) {
		if (!preprocessUri(uri).equals(uri)) {
			return TARGET;
		}
		return null;
	}

	private String preprocessUri(String uri) {
		String newUri = uri;
		if (newUri.startsWith(JAVADOC_ABSOLUTE_URI_MARKER) && newUri.length() > JAVADOC_ABSOLUTE_URI_MARKER.length()) {
			newUri = newUri.substring(JAVADOC_ABSOLUTE_URI_MARKER.length());
		}
		if (newUri.startsWith(JAVADOC_URI_MARKER) && newUri.length() > JAVADOC_URI_MARKER.length()) {
			newUri = uri.substring(JAVADOC_URI_MARKER.length());
		}
		return newUri;
	}

	private String toTypePage(String newUri) {
		return javadocFramePage() + "?" + newUri.replace('.', '/') + ".html";
	}

	private String toPackagePage(String newUri) {
		return javadocFramePage() + "?" + newUri.replace('.', '/') + "/package-summary.html";
	}

	private String javadocFramePage() {
		if (javadocRelativePath == null) {
			return "index.html";
		}
		String pageUri = javadocRelativePath;
		if (!pageUri.endsWith("/")) {
			pageUri += "/";
		}
		return pageUri + "index.html";
	}

	private String prependWithBasePackage(String uri) {
		if (uri.startsWith(BASE_PACKAGE_MARKER) && basePackageName != null) {
			return basePackageName + uri;
		}
		return uri;
	}

	private boolean isPotentialPackageName(String newUri) {
		for (int x = 0; x < newUri.length(); ++x) {
			char c = newUri.charAt(x);
			if (Character.isUpperCase(c)) {
				return false;
			}
		}
		return true;
	}
}
