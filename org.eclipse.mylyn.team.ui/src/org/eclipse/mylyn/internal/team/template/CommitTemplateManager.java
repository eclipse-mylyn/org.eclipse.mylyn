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
package org.eclipse.mylar.internal.team.template;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.team.ICommitTemplate;
import org.eclipse.mylar.team.MylarTeamPlugin;

/**
 * @author Eike Stepper
 * @author Mik Kersten
 */
public class CommitTemplateManager {

	private static final String ATTR_CLASS = "class";

	private static final String ATTR_DESCRIPTION = "description";

	private static final String ATTR_RECOGNIZED_KEYWORD = "recognizedKeyword";

	private static final String ELEM_TEMPLATE_HANDLER = "templateHandler";

	private static final String EXT_POINT_TEMPLATE_HANDLERS = "templateHandlers";

	public String generateComment(ITask task, String template) {
		return processKeywords(task, template);
	}

	public String getTaskIdFromCommentOrLabel(String commentOrLabel) {
		String id = getTaskIdFromComment(commentOrLabel);
//		if (id == null) {
//			id = getTaskIdFromLabel(commentOrLabel);
//		}
		return id;
	}

	private String getTaskIdFromComment(String comment) {
		try {
//			String regex = MylarTeamPlugin.getDefault().getPreferenceStore().getString(
//					MylarTeamPlugin.COMMIT_REGEX_TASK_ID);
			String template = MylarTeamPlugin.getDefault().getPreferenceStore().getString(
					MylarTeamPlugin.COMMIT_TEMPLATE);
			String regex = getTaskIdRegEx(template);
			
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(comment);
			if (matcher.find()) {
				return matcher.group(1);
			}
		} catch (Exception ex) {
			MylarStatusHandler.log(ex, "Problem while parsing task id from comment");
		}

		return null;
	}

//	private String getTaskIdFromLabel(String label) {
		// int firstDelimIndex = label.indexOf(PREFIX_DELIM);
		// if (firstDelimIndex != -1) {
		// int idStart = firstDelimIndex + PREFIX_DELIM.length();
		// int idEnd = label.indexOf(PREFIX_DELIM, firstDelimIndex +
		// PREFIX_DELIM.length());// comment.indexOf(PREFIX_DELIM);
		// if (idEnd != -1 && idStart < idEnd) {
		// String id = label.substring(idStart, idEnd);
		// if (id != null)
		// return id.trim();
		// } else {
		// // change set label
		// return label.substring(0, firstDelimIndex);
		// }
		// }
		// return null;
//	}

	public String getTaskIdRegEx(String template) {
		final String META_CHARS = "$()*+.< [\\]^{|}";
		final String TASK_ID_PLACEHOLDER = "\uffff";
		final String KEYWORD_PLACEHOLDER = "\ufffe";

		template = template.replaceFirst("\\$\\{task\\.id\\}", TASK_ID_PLACEHOLDER);
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

	public ICommitTemplate createHandler(final String keyword) {
		return (ICommitTemplate) new ExtensionProcessor() {
			@Override
			protected Object processContribution(IConfigurationElement element, String foundKeyword,
					String description, String className) throws Exception {
				if (keyword.equals(foundKeyword)) {
					ICommitTemplate handler = (ICommitTemplate) element.createExecutableExtension(ATTR_CLASS);
					if (handler instanceof CommitTemplate) {
						((CommitTemplate) handler).setDescription(description);
						((CommitTemplate) handler).setRecognizedKeyword(foundKeyword);
					} else {
						String recognizedKeyword = handler.getRecognizedKeyword();
						if (recognizedKeyword == null || !recognizedKeyword.equals(foundKeyword)) {
							throw new IllegalArgumentException("Keyword markup does not match handler implementation");
						}
					}

					return handler;
				}

				return null;
			}
		}.run();
	}

	private String processKeywords(ITask task, String template) {
		String[] segments = template.split("\\$\\{");
		StringBuffer buffer = new StringBuffer(segments[0]);

		for (int i = 1; i < segments.length; i++) {
			String segment = segments[i];
			String value = null;
			int brace = segment.indexOf('}');
			if (brace > 0) {
				String keyword = segment.substring(0, brace);
				value = processKeyword(task, keyword);
			}

			if (value != null) {
				buffer.append(value);
				buffer.append(segment.substring(brace + 1));
			} else {
				buffer.append("${");
				buffer.append(segment);
			}
		}

		return buffer.toString();
	}

	private String processKeyword(ITask task, String keyword) {
		try {
			ICommitTemplate handler = createHandler(keyword);
			if (handler != null) {
				return handler.getValue(task);
			}
		} catch (Exception ex) {
			MylarStatusHandler.log(ex, "Problem while dispatching to template handler for: " + keyword);
		}

		return null;
	}

	/**
	 * @author Eike Stepper
	 */
	private static class ExtensionProcessor {
		public Object run() {
			IExtensionPoint extPoint = Platform.getExtensionRegistry().getExtensionPoint(MylarTeamPlugin.PLUGIN_ID,
					EXT_POINT_TEMPLATE_HANDLERS);
			IExtension[] extensions = extPoint.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IExtension extension = extensions[i];
				IConfigurationElement[] elements = extension.getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					IConfigurationElement element = elements[j];
					if (ELEM_TEMPLATE_HANDLER.equals(element.getName())) {
						try {
							Object result = processContribution(element);
							if (result != null)
								return result;
						} catch (Exception ex) {
							final String msg = MessageFormat.format(
									"Error while processing template handler contribution {0} from plugin {1}.",
									element.getAttribute(ATTR_CLASS), element.getContributor().getName());
							MylarStatusHandler.log(ex, msg);
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
