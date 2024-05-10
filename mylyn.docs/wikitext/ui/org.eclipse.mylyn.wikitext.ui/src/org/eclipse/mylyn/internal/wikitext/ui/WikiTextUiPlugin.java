/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.MarkupTemplateCompletionProcessor;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.Templates;
import org.eclipse.mylyn.internal.wikitext.ui.editor.help.CheatSheetContent;
import org.eclipse.mylyn.internal.wikitext.ui.editor.help.HelpContent;
import org.eclipse.mylyn.internal.wikitext.ui.editor.preferences.Preferences;
import org.eclipse.mylyn.internal.wikitext.ui.registry.EclipseServiceLocator;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.WikiText;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author David Green
 */
public class WikiTextUiPlugin extends AbstractUIPlugin {

	private static final String EXTENSION_POINT_CHEAT_SHEET = "cheatSheet"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_CONTENT_ASSIST = "contentAssist"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_TEMPLATES = "templates"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_TEMPLATE = "template"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_RELATIVE_FILE_PATH_HYPERLINK_DECTOR = "relativeFilePathHyperlinkDetector"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_FILE_REF_REGEX = "regularExpression"; //$NON-NLS-1$

	private static WikiTextUiPlugin plugin;

	private SortedMap<String, HelpContent> cheatSheets;

	private Map<String, Templates> templates;

	private Preferences preferences;

	private IPropertyChangeListener preferencesListener;

	private Map<String, List<String>> fileRefRegexes;

	public WikiTextUiPlugin() {
		plugin = this;
		ServiceLocator.setImplementation(EclipseServiceLocator.class);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		preferencesListener = event -> preferences = null;
		getPreferenceStore().addPropertyChangeListener(preferencesListener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if (preferencesListener != null) {
			getPreferenceStore().removePropertyChangeListener(preferencesListener);
			preferencesListener = null;
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static WikiTextUiPlugin getDefault() {
		return plugin;
	}

	public void log(Throwable e) {
		if (e instanceof CoreException ce) {
			getLog().log(ce.getStatus());
		} else {
			log(IStatus.ERROR, e.getMessage(), e);
		}
	}

	public void log(int severity, String message, Throwable exception) {
		if (message == null) {
			message = ""; //$NON-NLS-1$
		}
		ILog log = getLog();
		IStatus status = null;
		if (exception instanceof CoreException ce) {
			status = ce.getStatus();
		}
		if (status == null) {
			status = new Status(severity, getPluginId(), severity, message, exception);
		}
		log.log(status);
	}

	public String getPluginId() {
		return getBundle().getSymbolicName();
	}

	public IStatus createStatus(int statusCode, Throwable exception) {
		return createStatus(null, statusCode, exception);
	}

	public IStatus createStatus(String message, int statusCode, Throwable exception) {
		if (message == null && exception != null) {
			message = exception.getClass().getName() + ": " + exception.getMessage(); //$NON-NLS-1$
		}
		return new Status(statusCode, getPluginId(), statusCode, message, exception);
	}

	public Preferences getPreferences() {
		if (preferences == null) {
			Preferences prefs = new Preferences();
			prefs.load(getPreferenceStore());
			prefs.makeImmutable();
			preferences = prefs;
		}
		return preferences;
	}

	public SortedMap<String, HelpContent> getCheatSheets() {
		if (cheatSheets == null) {
			SortedMap<String, HelpContent> cheatSheets = new TreeMap<>();

			IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
					.getExtensionPoint(getPluginId(), EXTENSION_POINT_CHEAT_SHEET);
			if (extensionPoint != null) {
				IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
				for (IConfigurationElement element : configurationElements) {
					String declaringPluginId = element.getDeclaringExtension().getContributor().getName();
					Bundle bundle = Platform.getBundle(declaringPluginId);
					String markupLanguage = element.getAttribute("markupLanguage"); //$NON-NLS-1$
					String contentLanguage = element.getAttribute("contentLanguage"); //$NON-NLS-1$
					String resource = element.getAttribute("resource"); //$NON-NLS-1$
					try {
						if (markupLanguage == null) {
							throw new Exception(Messages.WikiTextUiPlugin_markupLanguageRequired);
						} else if (!WikiText.getMarkupLanguageNames().contains(markupLanguage)) {
							throw new Exception(NLS.bind(Messages.WikiTextUiPlugin_invalidMarkupLanguage,
									new Object[] { markupLanguage }));
						}
						if (resource == null || resource.length() == 0) {
							throw new Exception(Messages.WikiTextUiPlugin_resourceRequired);
						}
						HelpContent cheatSheet = new CheatSheetContent(bundle, resource, contentLanguage,
								markupLanguage);
						HelpContent previous = cheatSheets.put(cheatSheet.getMarkupLanguageName(), cheatSheet);
						if (previous != null) {
							cheatSheets.put(previous.getMarkupLanguageName(), previous);
							throw new Exception(NLS.bind(Messages.WikiTextUiPlugin_markupLanguageContentAlreadyDeclared,
									new Object[] { previous.getMarkupLanguageName(),
											previous.getProvider().getSymbolicName() }));
						}
					} catch (Exception e) {
						log(IStatus.ERROR, NLS.bind(Messages.WikiTextUiPlugin_invalidExtension,
								new Object[] { declaringPluginId, EXTENSION_POINT_CHEAT_SHEET, e.getMessage() }), e);
					}
				}
			}

			this.cheatSheets = Collections.unmodifiableSortedMap(cheatSheets);
		}
		return cheatSheets;
	}

	/**
	 * get templates mapped by their markup language name
	 *
	 * @return the templates
	 */
	public Map<String, Templates> getTemplates() {
		if (templates == null) {
			Map<String, Templates> templates = new HashMap<>();

			IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
					.getExtensionPoint(getPluginId(), EXTENSION_POINT_CONTENT_ASSIST);
			if (extensionPoint != null) {

				IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
				for (IConfigurationElement element : configurationElements) {
					String declaringPluginId = element.getDeclaringExtension().getContributor().getName();
					if (EXTENSION_POINT_TEMPLATES.equals(element.getName())) {
						try {
							String markupLanguage = element.getAttribute("markupLanguage"); //$NON-NLS-1$
							if (markupLanguage == null) {
								throw new Exception(Messages.WikiTextUiPlugin_markupLanguageRequired);
							} else if (!WikiText.getMarkupLanguageNames().contains(markupLanguage)) {
								throw new Exception(NLS.bind(Messages.WikiTextUiPlugin_invalidMarkupLanguage,
										new Object[] { markupLanguage }));
							}
							Templates markupLanguageTemplates = new Templates();
							markupLanguageTemplates.setMarkupLanguageName(markupLanguage);

							for (IConfigurationElement templatesChild : element.getChildren()) {
								if (EXTENSION_POINT_TEMPLATE.equals(templatesChild.getName())) {
									try {
										// process the template
										String name = templatesChild.getAttribute("name"); //$NON-NLS-1$
										String description = templatesChild.getAttribute("description"); //$NON-NLS-1$
										String content = templatesChild.getAttribute("content"); //$NON-NLS-1$
										String autoInsert = templatesChild.getAttribute("autoInsert"); //$NON-NLS-1$
										String block = templatesChild.getAttribute("block"); //$NON-NLS-1$

										if (name == null || name.length() == 0) {
											throw new Exception(NLS.bind(Messages.WikiTextUiPlugin_nameRequired,
													new Object[] { EXTENSION_POINT_TEMPLATE }));
										}
										if (description == null || description.length() == 0) {
											throw new Exception(NLS.bind(Messages.WikiTextUiPlugin_descriptionRequired,
													new Object[] { EXTENSION_POINT_TEMPLATE }));
										}
										if (content == null || content.length() == 0) {
											throw new Exception(NLS.bind(Messages.WikiTextUiPlugin_contentRequired,
													new Object[] { EXTENSION_POINT_TEMPLATE }));
										}
										content = content.replace("\\t", "\t"); //$NON-NLS-1$//$NON-NLS-2$
										content = content.replace("\\r\\n", Text.DELIMITER) //$NON-NLS-1$
												.replace("\\r", //$NON-NLS-1$
														Text.DELIMITER)
												.replace("\\n", Text.DELIMITER) //$NON-NLS-1$
												.replace("\\\\", "\\"); //$NON-NLS-1$ //$NON-NLS-2$
										if (content.endsWith("$") //$NON-NLS-1$
												&& !(content.endsWith("\\$") || content.endsWith("$$"))) { //$NON-NLS-1$ //$NON-NLS-2$
											content = content.substring(0, content.length() - 1);
										}
										if (content.startsWith("^")) { //$NON-NLS-1$
											content = content.substring(1);
										}
										content = content.replace("\\$", "$$"); //$NON-NLS-1$ //$NON-NLS-2$

										markupLanguageTemplates.addTemplate(
												new Template(name, description,
														MarkupTemplateCompletionProcessor.CONTEXT_ID, content,
														autoInsert == null || !"false".equalsIgnoreCase(autoInsert)), //$NON-NLS-1$
												block != null && "true".equalsIgnoreCase(block)); //$NON-NLS-1$
									} catch (Exception e) {
										log(IStatus.ERROR,
												NLS.bind(Messages.WikiTextUiPlugin_invalidExtension,
														new Object[] { declaringPluginId,
																EXTENSION_POINT_CONTENT_ASSIST, e.getMessage() }),
												e);
									}
								} else {
									log(IStatus.ERROR,
											NLS.bind(Messages.WikiTextUiPlugin_unexpectedExtensionElement,
													new Object[] { declaringPluginId, EXTENSION_POINT_CONTENT_ASSIST,
															templatesChild.getName() }),
											null);
								}
							}
							Templates previous = templates.put(markupLanguageTemplates.getMarkupLanguageName(),
									markupLanguageTemplates);
							if (previous != null) {
								markupLanguageTemplates.addAll(previous);
							}
						} catch (Exception e) {
							log(IStatus.ERROR, NLS.bind(Messages.WikiTextUiPlugin_invalidExtension,
									new Object[] { declaringPluginId, EXTENSION_POINT_TEMPLATES, e.getMessage() }), e);
						}
					} else {
						log(IStatus.ERROR, NLS.bind(Messages.WikiTextUiPlugin_unexpectedExtensionElement,
								new Object[] { declaringPluginId, EXTENSION_POINT_CONTENT_ASSIST, element.getName() }),
								null);
					}
				}
			}

			// now that we have the basic templates, check for language extensions and connect the hierarchy

			// first ensure that all language names have templates defined
			Set<String> languageNames = WikiText.getMarkupLanguageNames();
			for (String languageName : languageNames) {
				Templates languageTemplates = templates.get(languageName);
				if (languageTemplates == null) {
					languageTemplates = new Templates();
					templates.put(languageName, languageTemplates);
				}
			}
			// next connect the hierarchy
			for (String languageName : languageNames) {
				MarkupLanguage markupLanguage = WikiText.getMarkupLanguage(languageName);
				if (markupLanguage != null && markupLanguage.getExtendsLanguage() != null) {
					Templates languageTemplates = templates.get(languageName);
					Templates parentLanguageTemplates = templates.get(markupLanguage.getExtendsLanguage());

					languageTemplates.setParent(parentLanguageTemplates);
				}
			}

			this.templates = Collections.unmodifiableMap(templates);
		}
		return templates;
	}

	public Map<String, List<String>> getHyperlinkDectectorFileRefRegexes() {
		if (fileRefRegexes == null) {
			Map<String, List<String>> markupLanguageToFileRefRegexes = new HashMap<>();

			IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
					.getExtensionPoint(getPluginId(), EXTENSION_POINT_RELATIVE_FILE_PATH_HYPERLINK_DECTOR);
			if (extensionPoint != null) {

				IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
				for (IConfigurationElement element : configurationElements) {
					String declaringPluginId = element.getDeclaringExtension().getContributor().getName();
					if (EXTENSION_POINT_RELATIVE_FILE_PATH_HYPERLINK_DECTOR.equals(element.getName())) {
						try {
							String markupLanguage = validateAndGetMarkupLanguage(element);
							List<String> regexes = createFielRefRegexes(element, declaringPluginId);
							markupLanguageToFileRefRegexes.put(markupLanguage, regexes);
						} catch (Exception e) {
							log(IStatus.ERROR,
									NLS.bind(Messages.WikiTextUiPlugin_invalidExtension,
											new Object[] { declaringPluginId,
													EXTENSION_POINT_RELATIVE_FILE_PATH_HYPERLINK_DECTOR,
													e.getMessage() }),
									e);
						}
					} else {
						log(IStatus.ERROR,
								NLS.bind(Messages.WikiTextUiPlugin_unexpectedExtensionElement,
										new Object[] { declaringPluginId,
												EXTENSION_POINT_RELATIVE_FILE_PATH_HYPERLINK_DECTOR,
												element.getName() }),
								null);
					}
				}
			}

			fileRefRegexes = Map.copyOf(markupLanguageToFileRefRegexes);
		}
		return fileRefRegexes;
	}

	private List<String> createFielRefRegexes(IConfigurationElement element, String declaringPluginId) {
		List<String> regexes = new ArrayList<>();

		for (IConfigurationElement fileRefRegexesChild : element.getChildren()) {
			if (EXTENSION_POINT_FILE_REF_REGEX.equals(fileRefRegexesChild.getName())) {
				try {
					String fileRefRegex = fileRefRegexesChild.getAttribute(EXTENSION_POINT_FILE_REF_REGEX);
					regexes.add(fileRefRegex);
				} catch (Exception e) {
					log(IStatus.ERROR, NLS.bind(Messages.WikiTextUiPlugin_invalidExtension, new Object[] {
							declaringPluginId, EXTENSION_POINT_RELATIVE_FILE_PATH_HYPERLINK_DECTOR, e.getMessage() }),
							e);
				}
			} else {
				log(IStatus.ERROR,
						NLS.bind(Messages.WikiTextUiPlugin_unexpectedExtensionElement, new Object[] { declaringPluginId,
								EXTENSION_POINT_RELATIVE_FILE_PATH_HYPERLINK_DECTOR, fileRefRegexesChild.getName() }),
						null);
			}
		}
		return List.copyOf(regexes);
	}

	private String validateAndGetMarkupLanguage(IConfigurationElement element) throws Exception {
		String markupLanguage = element.getAttribute("markupLanguage"); //$NON-NLS-1$
		if (markupLanguage == null) {
			throw new Exception(Messages.WikiTextUiPlugin_markupLanguageRequired);
		} else if (!WikiText.getMarkupLanguageNames().contains(markupLanguage)) {
			throw new Exception(
					NLS.bind(Messages.WikiTextUiPlugin_invalidMarkupLanguage, new Object[] { markupLanguage }));
		}
		return markupLanguage;
	}
}
