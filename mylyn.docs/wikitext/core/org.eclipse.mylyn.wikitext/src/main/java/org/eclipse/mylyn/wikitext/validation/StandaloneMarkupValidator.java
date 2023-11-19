/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.validation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Validate markup given a set of rules (stand-alone, outside of an Eclipse environment)
 *
 * @author David Green
 * @since 3.0
 */
public class StandaloneMarkupValidator {

	private static Map<ClassLoader, Map<String, StandaloneMarkupValidator>> validatorCacheByClassLoader = new WeakHashMap<>();

	private List<ValidationRule> rules = new ArrayList<>();

	private ClassLoader classLoader;

	private boolean immutable;

	/**
	 * Get the default validator for the specified markup language. Validators that are returned by this method are
	 * immutable and thread-safe
	 *
	 * @param markupLanguage
	 *            the markup language for which a validator is desired
	 * @return the validator
	 */
	public static StandaloneMarkupValidator getValidator(String markupLanguage) {
		// For correctness we cache validators by class loader.  This is necessary for usage
		// in a container where the calling class loader could affect the rules that are loaded.
		// Also we use a weak hash map so that class loaders can be GC'd
		ClassLoader classLoader = computeClassLoader();
		synchronized (StandaloneMarkupValidator.class) {
			Map<String, StandaloneMarkupValidator> validatorByMarkupLanguage = validatorCacheByClassLoader
					.get(classLoader);
			if (validatorByMarkupLanguage == null) {
				validatorByMarkupLanguage = new HashMap<>();
				validatorCacheByClassLoader.put(classLoader, validatorByMarkupLanguage);
			}
			StandaloneMarkupValidator validator = validatorByMarkupLanguage.computeIfAbsent(markupLanguage,
					s -> new StandaloneMarkupValidator());
			validator.computeRules(markupLanguage);
			validator.setImmutable();
			return validator;
		}
	}

	private static ClassLoader computeClassLoader() {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (contextClassLoader != null) {
			return contextClassLoader;
		}
		return StandaloneMarkupValidator.class.getClassLoader();
	}

	private void setImmutable() {
		if (!immutable) {
			this.immutable = true;
			rules = Collections.unmodifiableList(rules);
		}
	}

	public List<ValidationProblem> validate(String markup) {
		return validate(markup, 0, markup.length());
	}

	public List<ValidationProblem> validate(String markup, int offset, int length) {
		if (length == 0 || rules.isEmpty()) {
			return Collections.emptyList();
		}

		List<ValidationProblem> problems = new ArrayList<>();

		for (ValidationRule rule : rules) {
			problems.addAll(rule.findProblems(markup, offset, length));
		}
		if (!problems.isEmpty()) {
			Collections.sort(problems);
		}
		return problems;
	}

	public List<ValidationRule> getRules() {
		return rules;
	}

	/**
	 * Compute rules for a markup language based on looking them up in the available plugin.xml files
	 *
	 * @param markupLanguage
	 */
	public void computeRules(String markupLanguage) {
		// NOTE: we load plugin.xml files here directly since we assume that we're not running in an Eclipse (OSGi) container.
		try {
			Enumeration<URL> resources = getClassLoader().getResources("plugin.xml"); //$NON-NLS-1$
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				computeRules(markupLanguage, url);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * compute rules for the specified markup language given an URL to a plugin.xml
	 *
	 * @param markupLanguage
	 *            the markup language for which rules should be loaded
	 * @param url
	 *            the URL to the plugin.xml that specifies the rules
	 */
	public void computeRules(String markupLanguage, URL url) {
		try (InputStream in = url.openStream()) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setValidating(false);
			Document pluginXml = dbf.newDocumentBuilder().parse(in);

			computeRules(markupLanguage, pluginXml);
		} catch (SecurityException e) {
			// ignore, we can get this during unit testing.  It can happen due to a digest mismatch for resources in a jar file.
			Logger.getLogger(StandaloneMarkupValidator.class.getName())
					.log(Level.WARNING, String.format("Ignoring plugin.xml due to security exception: %s", url), e); //$NON-NLS-1$
		} catch (SAXException e) {
			throw new IllegalStateException(String.format("Cannot parse file %s", url), e); //$NON-NLS-1$
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			// ignore
		}
	}

	void computeRules(String markupLanguage, Document pluginXml) {
		for (Node child = pluginXml.getDocumentElement().getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getLocalName().equals("extension")) { //$NON-NLS-1$
				Element element = (Element) child;
				String point = element.getAttribute("point"); //$NON-NLS-1$
				if ("org.eclipse.mylyn.wikitext.ui.markupValidationRule".equals(point)) { //$NON-NLS-1$
					for (Node vrNode = child.getFirstChild(); vrNode != null; vrNode = vrNode.getNextSibling()) {
						if (vrNode.getNodeType() == Node.ELEMENT_NODE && vrNode.getLocalName().equals("rule")) { //$NON-NLS-1$
							Element rule = (Element) vrNode;
							if (markupLanguage.equals(rule.getAttribute("markupLanguage"))) { //$NON-NLS-1$
								String className = rule.getAttribute("class"); //$NON-NLS-1$
								try {
									Class<?> validationRuleClass = Class.forName(className, true, getClassLoader());
									rules.add((ValidationRule) validationRuleClass.getConstructor().newInstance());
								} catch (Exception e) {
									// ignore
								}
							}
						}
					}
				}
			}
		}
	}

	private ClassLoader getClassLoader() {
		if (classLoader != null) {
			return classLoader;
		}
		return computeClassLoader();
	}

	/**
	 * for testing purposes, set the class loader to use when loading validation rules
	 */
	public void setClassLoader(ClassLoader classLoader) {
		if (immutable) {
			throw new IllegalStateException();
		}
		this.classLoader = classLoader;
	}
}
