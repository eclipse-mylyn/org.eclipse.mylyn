/*******************************************************************************
 * Copyright (c) 2010, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Jeremie Bresson - Bug 379783
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

public class TemplateProcessor {

	private static final Pattern templatePattern = Pattern.compile("(?:^|(?<!\\{))(\\{\\{(#?[a-zA-Z0-9_ :\\.\\-]+)\\s*(\\|[^\\}]*)?\\}\\})"); //$NON-NLS-1$

	private static final Pattern templateParameterPattern = Pattern.compile("\\{\\{\\{([a-zA-Z0-9]+)(?:\\|([^\\}]*))?\\}\\}\\}"); //$NON-NLS-1$

	private static final Pattern parameterSpec = Pattern.compile("\\|\\s*([^\\|=]+)(?:\\s*=\\s*(([^|]*)))?"); //$NON-NLS-1$

	private static final Pattern includeOnlyPattern = Pattern.compile(".*?<includeonly>(.*?)</includeonly>.*", //$NON-NLS-1$
			Pattern.DOTALL);

	private static final Pattern noIncludePattern = Pattern.compile("<noinclude>(.*?)</noinclude>", Pattern.DOTALL); //$NON-NLS-1$

	private final AbstractMediaWikiLanguage mediaWikiLanguage;

	private final Map<String, Template> templateByName = new HashMap<String, Template>();

	private final List<Pattern> excludePatterns = new ArrayList<Pattern>();

	public TemplateProcessor(AbstractMediaWikiLanguage abstractMediaWikiLanguage) {
		this.mediaWikiLanguage = abstractMediaWikiLanguage;

		for (Template template : mediaWikiLanguage.getTemplates()) {
			templateByName.put(template.getName(), normalize(template));
		}
		String templateExcludes = abstractMediaWikiLanguage.getTemplateExcludes();
		if (templateExcludes != null) {
			String[] split = templateExcludes.split("\\s*,\\s*"); //$NON-NLS-1$
			for (String exclude : split) {
				String pattern = exclude.replaceAll("([^a-zA-Z:\\*])", "\\\\$1").replaceAll("\\*", ".*?"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				excludePatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
			}
		}
	}

	public String processTemplates(String markupContent) {
		return processTemplates(markupContent, Collections.<String> emptySet());
	}

	private String processTemplates(String markupContent, Set<String> usedTemplates) {
		StringBuilder processedMarkup = new StringBuilder();

		int lastIndex = 0;
		Matcher matcher = templatePattern.matcher(markupContent);
		while (matcher.find()) {
			int start = matcher.start();
			if (lastIndex < start) {
				processedMarkup.append(markupContent.substring(lastIndex, start));
			}
			String templateName = matcher.group(2);
			Template template = resolveTemplate(templateName);
			if (template != null) {
				String replacementText;
				if (usedTemplates.contains(templateName)) {
					StringBuilder sb = new StringBuilder();
					sb.append("<span class=\"error\">"); //$NON-NLS-1$
					sb.append(MessageFormat.format(
							Messages.getString("TemplateProcessor_loopDetected"), template.getName())); //$NON-NLS-1$
					sb.append("</span>"); //$NON-NLS-1$
					replacementText = sb.toString();
				} else {
					String parameters = matcher.group(3);
					replacementText = processTemplate(template, parameters);
					//The replacementText might contain other templates. Add the current template to the set of used template and call recursively this function again:
					Set<String> templates = new HashSet<String>(usedTemplates);
					templates.add(templateName);
					replacementText = processTemplates(replacementText, templates);
				}
				replacementText = processTemplates(replacementText);
				processedMarkup.append(replacementText);
			}
			lastIndex = matcher.end();
		}
		if (lastIndex == 0) {
			return markupContent;
		}
		if (lastIndex < markupContent.length()) {
			processedMarkup.append(markupContent.substring(lastIndex));
		}
		return processedMarkup.toString();
	}

	private String processTemplate(Template template, String parametersText) {
		if (template.getTemplateMarkup() == null) {
			return ""; //$NON-NLS-1$
		}
		String macro = template.getTemplateContent();

		List<Parameter> parameters = processParameters(parametersText);

		StringBuilder processedMarkup = new StringBuilder();
		int lastIndex = 0;
		Matcher matcher = templateParameterPattern.matcher(macro);
		while (matcher.find()) {
			int start = matcher.start();
			if (lastIndex < start) {
				processedMarkup.append(macro.substring(lastIndex, start));
			}
			String parameterName = matcher.group(1);
			String parameterValue = matcher.group(2);
			try {
				int parameterIndex = Integer.parseInt(parameterName);
				if (parameterIndex <= parameters.size() && parameterIndex > 0) {
					parameterValue = parameters.get(parameterIndex - 1).value;
				}
			} catch (NumberFormatException e) {
				for (Parameter param : parameters) {
					if (parameterName.equalsIgnoreCase(param.name)) {
						parameterValue = param.value;
						break;
					}
				}
			}
			if (parameterValue != null) {
				processedMarkup.append(parameterValue);
			}

			lastIndex = matcher.end();
		}
		if (lastIndex == 0) {
			return macro;
		}
		if (lastIndex < macro.length()) {
			processedMarkup.append(macro.substring(lastIndex));
		}
		return processedMarkup.toString();
	}

	private List<Parameter> processParameters(String parametersText) {
		List<Parameter> parameters = new ArrayList<TemplateProcessor.Parameter>();
		if (parametersText != null && parametersText.length() > 0) {
			Matcher matcher = parameterSpec.matcher(parametersText);
			while (matcher.find()) {
				String nameOrValue = matcher.group(1);
				String value = matcher.group(2);
				Parameter parameter = new Parameter();
				if (value != null) {
					parameter.name = nameOrValue;
					parameter.value = value;
				} else {
					parameter.value = nameOrValue;
				}
				parameters.add(parameter);
			}
		}
		return parameters;
	}

	private Template resolveTemplate(String templateName) {
		if (!excludePatterns.isEmpty()) {
			for (Pattern p : excludePatterns) {
				if (p.matcher(templateName).matches()) {
					return null;
				}
			}
		}
		Template template = templateByName.get(templateName);
		if (template == null) {
			for (TemplateResolver resolver : mediaWikiLanguage.getTemplateProviders()) {
				template = resolver.resolveTemplate(templateName);
				if (template != null) {
					template = normalize(template);
					break;
				}
			}
			if (template == null) {
				template = new Template();
				template.setName(templateName);
				template.setTemplateMarkup(""); //$NON-NLS-1$
			}
			templateByName.put(template.getName(), template);
		}
		return template;
	}

	private Template normalize(Template template) {
		Template normalizedTemplate = new Template();
		normalizedTemplate.setName(template.getName());
		normalizedTemplate.setTemplateMarkup(normalizeTemplateMarkup(template.getTemplateContent()));

		return normalizedTemplate;
	}

	private String normalizeTemplateMarkup(String templateMarkup) {
		Matcher matcher = includeOnlyPattern.matcher(templateMarkup);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		matcher = noIncludePattern.matcher(templateMarkup);
		return matcher.replaceAll(""); //$NON-NLS-1$
	}

	private static class Parameter {
		String name;

		String value;
	}
}
