/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
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

	private static final String ATTR_CLASS = "class";

	private static final String ATTR_DESCRIPTION = "summary";

	private static final String ATTR_RECOGNIZED_KEYWORD = "recognizedKeyword";

	private static final String ELEM_TEMPLATE_HANDLER = "templateVariable";

	private static final String EXT_POINT_TEMPLATE_HANDLERS = "commitTemplates";

	public String generateComment(ITask task, String template) {
		return processKeywords(task, template);
	}

	public String getTaskIdFromCommentOrLabel(String commentOrLabel) {
		String id = getTaskIdFromComment(commentOrLabel);
		return id;
	}

	private String getTaskIdFromComment(String comment) {
		try {
			String template = FocusedTeamUiPlugin.getDefault().getPreferenceStore().getString(
					FocusedTeamUiPlugin.COMMIT_TEMPLATE);
			int templateNewline = template.indexOf('\n');
			String templateFirstLineIndex = template;
			if (templateNewline != -1) {
				templateFirstLineIndex = template.substring(0, templateNewline - 1);
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
					"Problem while parsing task id from comment", e));
		}

		return null;
	}

	public String getTaskIdRegEx(String template) {
		final String META_CHARS = " $()*+.< [\\]^{|}";
		final String TASK_ID_PLACEHOLDER = "\uffff";
		final String KEYWORD_PLACEHOLDER = "\ufffe";

		template = template.replaceFirst("\\$\\{task\\.id\\}", TASK_ID_PLACEHOLDER);
		template = template.replaceFirst("\\$\\{task\\.key\\}", TASK_ID_PLACEHOLDER);
		template = replaceKeywords(template, KEYWORD_PLACEHOLDER);
		template = quoteChars(template, META_CHARS);
		template = template.replaceFirst(TASK_ID_PLACEHOLDER, "(\\\\d+)");
		template = template.replaceAll(KEYWORD_PLACEHOLDER, ".*");
		return template;
	}

	private String replaceKeywords(String str, String placeholder) {
		String[] recognizedKeywords = getRecognizedKeywords();
		for (String keyword : recognizedKeywords) {
			str = str.replaceAll("\\$\\{" + keyword + "\\}", placeholder);
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
		final ArrayList<String> result = new ArrayList<String>();
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
			protected Object processContribution(IConfigurationElement element, String foundKeyword,
					String description, String className) throws Exception {
				return keyword.equals(foundKeyword) ? description : null;
			}
		}.run();
	}

	public AbstractCommitTemplateVariable createHandler(final String keyword) {
		return (AbstractCommitTemplateVariable) new ExtensionProcessor() {
			@Override
			protected Object processContribution(IConfigurationElement element, String foundKeyword,
					String description, String className) throws Exception {
				if (keyword.equals(foundKeyword)) {
					AbstractCommitTemplateVariable handler = (AbstractCommitTemplateVariable) element.createExecutableExtension(ATTR_CLASS);
					if (handler != null) {
						(handler).setDescription(description);
						(handler).setRecognizedKeyword(foundKeyword);
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
		String[] segments = template.split("\\$\\{");
		Stack<String> evaluated = new Stack<String>();
		evaluated.add(segments[0]);

		for (int i = 1; i < segments.length; i++) {
			String segment = segments[i];
			String value = null;
			int brace = segment.indexOf('}');
			if (brace > 0) {
				String keyword = segment.substring(0, brace);
				value = processKeyword(task, keyword);
			}

			if (value != null) {
				evaluated.add(value);
				evaluated.add(segment.substring(brace + 1));
			} else if (!evaluated.isEmpty()) {
				evaluated.pop();
			}
//			else {
//				buffer.append("${");
//				buffer.append(segment);
//			}
		}
		StringBuffer buffer = new StringBuffer();
		for (String string : evaluated) {
			buffer.append(string);
		}

		return buffer.toString();
	}

	private String processKeyword(ITask task, String keyword) {
		try {
			AbstractCommitTemplateVariable handler = createHandler(keyword);
			if (handler != null) {
				return handler.getValue(task);
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
					"Problem while dispatching to template handler for: " + keyword, e));
		}

		return null;
	}

	/**
	 * @author Eike Stepper
	 */
	private static class ExtensionProcessor {
		public Object run() {
			IExtensionPoint extPoint = Platform.getExtensionRegistry().getExtensionPoint(FocusedTeamUiPlugin.ID_PLUGIN,
					EXT_POINT_TEMPLATE_HANDLERS);
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
									"Error while processing template handler contribution {0} from plugin {1}.",
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
