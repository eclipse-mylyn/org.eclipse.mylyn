/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Eike Stepper- initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.templates;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.team.ui.AbstractCommitTemplateVariable;

/**
 * @author Eike Stepper
 * @author Mik Kersten
 */
public class CommitTemplateManager {

	private static final Pattern ARGUMENT_PATTERN = Pattern.compile("""
			(.+)\
			\\(\\s*"\
			(.+(?:"\\s*,\\s*".+)*)\
			"\\s*\\)}\
			(.*)""", Pattern.DOTALL); //$NON-NLS-1$

	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static final String ATTR_DESCRIPTION = "description"; //$NON-NLS-1$

	private static final String ATTR_RECOGNIZED_KEYWORD = "recognizedKeyword"; //$NON-NLS-1$

	private static final String ELEM_TEMPLATE_HANDLER = "templateVariable"; //$NON-NLS-1$

	private static final String EXT_POINT_TEMPLATE_HANDLERS = "commitTemplates"; //$NON-NLS-1$

	private static final String[] EMPTY_STRING_ARRAY = {};

	public String generateComment(ITask task, String template) {
		return processKeywords(task, template);
	}

	public String getTaskIdFromCommentOrLabel(String comment) {
		try {
			String template = FocusedTeamUiPlugin.getDefault()
					.getPreferenceStore()
					.getString(FocusedTeamUiPlugin.COMMIT_TEMPLATE);
			int templateNewline = template.indexOf('\n');
			String templateFirstLineIndex = template;
			if (templateNewline != -1) {
				templateFirstLineIndex = template.substring(0, templateNewline);
			}

			String regex = getTaskIdRegEx(templateFirstLineIndex);

			int commentNewlineIndex = comment.indexOf('\n');
			String commentFirstLine = comment;
			if (commentNewlineIndex != -1) {
				commentFirstLine = comment.substring(0, commentNewlineIndex);
			}

			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(commentFirstLine);

			if (matcher.find()) {
				return matcher.group(1);
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
					"Problem while parsing task id from comment", e)); //$NON-NLS-1$
		}

		return null;
	}

	public String getTaskIdRegEx(String template) {
		final String META_CHARS = " $()*+.< [\\]^{|}"; //$NON-NLS-1$
		final String TASK_ID_PLACEHOLDER = "\uffff"; //$NON-NLS-1$
		final String KEYWORD_PLACEHOLDER = "\ufffe"; //$NON-NLS-1$

		template = template.replaceFirst("\\$\\{task\\.id\\}", TASK_ID_PLACEHOLDER); //$NON-NLS-1$
		template = template.replaceFirst("\\$\\{task\\.key\\}", TASK_ID_PLACEHOLDER); //$NON-NLS-1$
		template = replaceKeywords(template, KEYWORD_PLACEHOLDER);
		template = quoteChars(template, META_CHARS);
		template = template.replaceFirst(TASK_ID_PLACEHOLDER, "(\\\\d+)"); //$NON-NLS-1$
		template = template.replace(KEYWORD_PLACEHOLDER, ".*"); //$NON-NLS-1$
		return template;
	}

	private String replaceKeywords(String str, String placeholder) {
		String[] recognizedKeywords = getRecognizedKeywords();
		for (String keyword : recognizedKeywords) {
			str = str.replaceAll("\\$\\{" + keyword + "\\}", placeholder); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return str;
	}

	private String quoteChars(String str, String charsToQuote) {
		StringBuilder builder = new StringBuilder(str.length() * 2);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (charsToQuote.indexOf(c) != -1) {
				builder.append('\\');
			}
			builder.append(c);
		}
		return builder.toString();
	}

	public String[] getRecognizedKeywords() {
		final ArrayList<String> result = new ArrayList<>();
		new ExtensionProcessor() {
			@Override
			protected Object processContribution(IConfigurationElement element, String keyword, String description,
					String className) throws Exception {
				result.add(keyword);
				return null;
			}
		}.run();

		return result.toArray(new String[result.size()]);
	}

	public String getHandlerDescription(final String keyword) {
		return (String) new ExtensionProcessor() {
			@Override
			protected Object processContribution(IConfigurationElement element, String foundKeyword, String description,
					String className) throws Exception {
				return keyword.equals(foundKeyword) ? description : null;
			}
		}.run();
	}

	public AbstractCommitTemplateVariable createHandler(final String keyword) {
		return (AbstractCommitTemplateVariable) new ExtensionProcessor() {
			@Override
			protected Object processContribution(IConfigurationElement element, String foundKeyword, String description,
					String className) throws Exception {
				if (keyword.equals(foundKeyword)) {
					AbstractCommitTemplateVariable handler = (AbstractCommitTemplateVariable) element
							.createExecutableExtension(ATTR_CLASS);
					if (handler != null) {
						handler.setDescription(description);
						handler.setRecognizedKeyword(foundKeyword);
					}
//					else {
//						String recognizedKeyword = handler.getRecognizedKeyword();
//						if (recognizedKeyword == null || !recognizedKeyword.equals(foundKeyword)) {
//							throw new IllegalArgumentException("Keyword markup does not match handler implementation");
//						}
//					}

					return handler;
				}

				return null;
			}
		}.run();
	}

	private String processKeywords(ITask task, String template) {
		String[] segments = template.split("\\$\\{"); //$NON-NLS-1$
		Stack<String> evaluated = new Stack<>();
		evaluated.add(segments[0]);

		for (int i = 1; i < segments.length; i++) {
			String segment = segments[i];
			String value = null;
			String trailingCharacters;
			Matcher argumentMatcher = ARGUMENT_PATTERN.matcher(segment);
			if (argumentMatcher.matches()) {
				String keyword = argumentMatcher.group(1);
				String[] args = argumentMatcher.group(2).split("\"\\s*,\\s*\""); //$NON-NLS-1$
				value = processKeyword(task, keyword, args);
				trailingCharacters = argumentMatcher.group(3);
			} else {
				int brace = segment.indexOf('}');
				if (brace > 0) {
					String keyword = segment.substring(0, brace);
					value = processKeyword(task, keyword, EMPTY_STRING_ARRAY);
				}
				trailingCharacters = segment.substring(brace + 1);
			}

			if (value != null) {
				evaluated.add(value);
				evaluated.add(trailingCharacters);
			} else if (!evaluated.isEmpty()) {
				evaluated.add(trailingCharacters);
			}
//			else {
//				buffer.append("${");
//				buffer.append(segment);
//			}
		}
		StringBuilder buffer = new StringBuilder();
		for (String string : evaluated) {
			buffer.append(string);
		}

		// remove duplicate whitespace
		String commitTemplate = buffer.toString();
		return commitTemplate.replaceAll("[ ]+", " "); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String processKeyword(ITask task, String keyword, String[] args) {
		try {
			AbstractCommitTemplateVariable handler = createHandler(keyword);
			if (handler != null) {
				handler.setArguments(args);
				return handler.getValue(task);
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
					"Problem while dispatching to template handler for: " + keyword, e)); //$NON-NLS-1$
		}

		return null;
	}

	/**
	 * @author Eike Stepper
	 */
	private static class ExtensionProcessor {
		public Object run() {
			IExtensionPoint extPoint = Platform.getExtensionRegistry()
					.getExtensionPoint(FocusedTeamUiPlugin.ID_PLUGIN, EXT_POINT_TEMPLATE_HANDLERS);
			IExtension[] extensions = extPoint.getExtensions();
			for (IExtension extension : extensions) {
				IConfigurationElement[] elements = extension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (ELEM_TEMPLATE_HANDLER.equals(element.getName())) {
						try {
							Object result = processContribution(element);
							if (result != null) {
								return result;
							}
						} catch (Exception e) {
							String msg = MessageFormat.format(
									Messages.CommitTemplateManager_Error_while_processing_template_handler_contribution_X_from_plugin_X,
									element.getAttribute(ATTR_CLASS), element.getContributor().getName());
							StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN, msg, e));
						}
					}
				}
			}

			return null;
		}

		protected Object processContribution(IConfigurationElement element) throws Exception {
			String keyword = element.getAttribute(ATTR_RECOGNIZED_KEYWORD);
			String description = element.getAttribute(ATTR_DESCRIPTION);
			String className = element.getAttribute(ATTR_CLASS);
			return processContribution(element, keyword, description, className);
		}

		protected Object processContribution(IConfigurationElement element, String keyword, String description,
				String className) throws Exception {
			return null;
		}
	}
}
