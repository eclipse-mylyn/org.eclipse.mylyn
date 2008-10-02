/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.core.validation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.core.validation.ValidationRule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * validate markup given a set of rules (stand-alone, outside of an Eclipse environment)
 * 
 * @author David Green
 */
public class StandaloneMarkupValidator {

	private final List<ValidationRule> rules = new ArrayList<ValidationRule>();

	public List<ValidationProblem> validate(String markup) {
		return validate(markup, 0, markup.length());
	}

	public List<ValidationProblem> validate(String markup, int offset, int length) {
		if (length == 0 || rules.isEmpty()) {
			return Collections.emptyList();
		}
		int end = offset + length;
		if (end > markup.length()) {
			end = markup.length();
		}
		List<ValidationProblem> problems = new ArrayList<ValidationProblem>();

		for (ValidationRule rule : rules) {
			int o = offset;
			while (o < end) {
				ValidationProblem problem = rule.findProblem(markup, o, length - (o - offset));
				if (problem == null) {
					break;
				}
				problems.add(problem);
				int newO = problem.getOffset() + problem.getLength();
				if (newO <= o) {
					break;
				}
				o = newO;
			}
		}
		if (!problems.isEmpty()) {
			Collections.sort(problems);
		}
		return problems;
	}

	public List<ValidationRule> getRules() {
		return rules;
	}

	public void computeRules(String markupLanguage) {
		try {
			Enumeration<URL> resources = getClassLoader().getResources("plugin.xml");
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				computeRules(markupLanguage, url);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void computeRules(String markupLanguage, URL url) {
		try {
			InputStream in = url.openStream();
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				dbf.setValidating(false);
				Document pluginXml = dbf.newDocumentBuilder().parse(in);

				computeRules(markupLanguage, pluginXml);

			} catch (SAXException e) {
				throw new IllegalStateException(String.format("Cannot parse file %s", url), e);
			} catch (ParserConfigurationException e) {
				throw new IllegalStateException(e);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			// ignore
		}
	}

	private void computeRules(String markupLanguage, Document pluginXml) {
		for (Node child = pluginXml.getDocumentElement().getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getLocalName().equals("extension")) {
				Element element = (Element) child;
				String point = element.getAttribute("point");
				if ("org.eclipse.mylyn.wikitext.core.markupValidationRule".equals(point)) {
					for (Node vrNode = child.getFirstChild(); vrNode != null; vrNode = vrNode.getNextSibling()) {
						if (vrNode.getNodeType() == Node.ELEMENT_NODE && vrNode.getLocalName().equals("rule")) {
							Element rule = (Element) vrNode;
							if (markupLanguage.equals(rule.getAttribute("markupLanguage"))) {
								String className = rule.getAttribute("class");
								try {
									Class<?> validationRuleClass = Class.forName(className, true, getClassLoader());
									rules.add((ValidationRule) validationRuleClass.newInstance());
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
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (contextClassLoader != null) {
			return contextClassLoader;
		}
		return StandaloneMarkupValidator.class.getClassLoader();
	}

}
