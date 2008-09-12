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
package org.eclipse.mylyn.wikitext.ui;

import java.util.Collections;
import java.util.HashMap;
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
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.MarkupTemplateCompletionProcessor;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.Templates;
import org.eclipse.mylyn.internal.wikitext.ui.editor.help.CheatSheetContent;
import org.eclipse.mylyn.internal.wikitext.ui.editor.help.HelpContent;
import org.eclipse.mylyn.internal.wikitext.ui.editor.preferences.Preferences;
import org.eclipse.mylyn.wikitext.core.WikiTextPlugin;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

// FIXME: move to internal

/**
 * 
 * 
 * @author David Green
 */
public class WikiTextUiPlugin extends AbstractUIPlugin {

	public static final String COLOR_HR = "HR";

	public static final String COLOR_HR_SHADOW = "HR_SHADOW";

	private static final String EXTENSION_POINT_CHEAT_SHEET = "cheatSheet";

	private static final String EXTENSION_POINT_CONTENT_ASSIST = "contentAssist";

	private static final String EXTENSION_POINT_TEMPLATES = "templates";

	private static final String EXTENSION_POINT_TEMPLATE = "template";

	private static WikiTextUiPlugin plugin;

	private SortedMap<String, HelpContent> cheatSheets;

	private Map<String, Templates> templates;

	private final ColorRegistry colorRegistry = new ColorRegistry();

	public WikiTextUiPlugin() {
		plugin = this;
		colorRegistry.put(COLOR_HR, new RGB(132, 132, 132));
		colorRegistry.put(COLOR_HR_SHADOW, new RGB(206, 206, 206));
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
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

	public ColorRegistry getColorRegistry() {
		return colorRegistry;
	}

	public void log(Throwable ce) {
		if (ce instanceof CoreException) {
			getLog().log(((CoreException) ce).getStatus());
		} else {
			log(IStatus.ERROR, ce.getMessage(), ce);
		}
	}

	public void log(int severity, String message, Throwable exception) {
		if (message == null) {
			message = "";
		}
		ILog log = getLog();
		IStatus status = null;
		if (exception instanceof CoreException) {
			status = ((CoreException) exception).getStatus();
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
			message = exception.getClass().getName() + ": " + exception.getMessage();
		}
		Status status = new Status(statusCode, getPluginId(), statusCode, message, exception);
		return status;
	}

	public Preferences getPreferences() {
		Preferences prefs = new Preferences();
		prefs.load(getPreferenceStore());
		return prefs;
	}

	public SortedMap<String, HelpContent> getCheatSheets() {
		if (cheatSheets == null) {
			SortedMap<String, HelpContent> cheatSheets = new TreeMap<String, HelpContent>();

			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(getPluginId(),
					EXTENSION_POINT_CHEAT_SHEET);
			if (extensionPoint != null) {
				IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
				for (IConfigurationElement element : configurationElements) {
					String declaringPluginId = element.getDeclaringExtension().getContributor().getName();
					Bundle bundle = Platform.getBundle(declaringPluginId);
					String markupLanguage = element.getAttribute("markupLanguage");
					String contentLanguage = element.getAttribute("contentLanguage");
					String resource = element.getAttribute("resource");
					try {
						if (markupLanguage == null) {
							throw new Exception("Must specify markupLanguage");
						} else if (!WikiTextPlugin.getDefault().getMarkupLanguageNames().contains(markupLanguage)) {
							throw new Exception(String.format("'%s' is not a valid markupLanguage", markupLanguage));
						}
						if (resource == null || resource.length() == 0) {
							throw new Exception("Must specify resource");
						}
						HelpContent cheatSheet = new CheatSheetContent(bundle, resource, contentLanguage,
								markupLanguage);
						HelpContent previous = cheatSheets.put(cheatSheet.getMarkupLanguageName(), cheatSheet);
						if (previous != null) {
							cheatSheets.put(previous.getMarkupLanguageName(), previous);
							throw new Exception(String.format(
									"content for markupLanguage '%s' is already declared by plugin '%s'",
									previous.getMarkupLanguageName(), previous.getProvider().getSymbolicName()));
						}
					} catch (Exception e) {
						log(IStatus.ERROR, String.format("Plugin '%s' extension '%s' invalid: %s", declaringPluginId,
								EXTENSION_POINT_CHEAT_SHEET, e.getMessage()), e);
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
			Map<String, Templates> templates = new HashMap<String, Templates>();

			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(getPluginId(),
					EXTENSION_POINT_CONTENT_ASSIST);
			if (extensionPoint != null) {

				IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
				for (IConfigurationElement element : configurationElements) {
					String declaringPluginId = element.getDeclaringExtension().getContributor().getName();
					if (EXTENSION_POINT_TEMPLATES.equals(element.getName())) {
						try {
							String markupLanguage = element.getAttribute("markupLanguage");
							if (markupLanguage == null) {
								throw new Exception("Must specify markupLanguage");
							} else if (!WikiTextPlugin.getDefault().getMarkupLanguageNames().contains(markupLanguage)) {
								throw new Exception(String.format("'%s' is not a valid markupLanguage", markupLanguage));
							}
							Templates dialectTemplates = new Templates();
							dialectTemplates.setMarkupLanguageName(markupLanguage);

							for (IConfigurationElement templatesChild : element.getChildren()) {
								if (EXTENSION_POINT_TEMPLATE.equals(templatesChild.getName())) {
									try {
										// process the template
										String name = templatesChild.getAttribute("name");
										String description = templatesChild.getAttribute("description");
										String content = templatesChild.getAttribute("content");
										String autoInsert = templatesChild.getAttribute("autoInsert");
										String block = templatesChild.getAttribute("block");

										if (name == null || name.length() == 0) {
											throw new Exception(String.format("Must specify %s/name",
													EXTENSION_POINT_TEMPLATE));
										}
										if (description == null || description.length() == 0) {
											throw new Exception(String.format("Must specify %s/description",
													EXTENSION_POINT_TEMPLATE));
										}
										if (content == null || content.length() == 0) {
											throw new Exception(String.format("Must specify %s/content",
													EXTENSION_POINT_TEMPLATE));
										}
										content = content.replace("\\r\\n", Text.DELIMITER).replace("\\r",
												Text.DELIMITER).replace("\\n", Text.DELIMITER).replace("\\\\", "\\");
										if (content.endsWith("$")) {
											content = content.substring(0, content.length() - 1);
										}

										dialectTemplates.addTemplate(new Template(name, description,
												MarkupTemplateCompletionProcessor.CONTEXT_ID, content,
												autoInsert == null || !"false".equalsIgnoreCase(autoInsert)),
												block != null && "true".equalsIgnoreCase(block));
									} catch (Exception e) {
										log(IStatus.ERROR, String.format("Plugin '%s' extension '%s' invalid: %s",
												declaringPluginId, EXTENSION_POINT_CONTENT_ASSIST, e.getMessage()), e);
									}
								} else {
									log(IStatus.ERROR, String.format(
											"Plugin '%s' extension '%s' unexpected element: %s", declaringPluginId,
											EXTENSION_POINT_CONTENT_ASSIST, templatesChild.getName()), null);
								}
							}
							Templates previous = templates.put(dialectTemplates.getMarkupLanguageName(),
									dialectTemplates);
							if (previous != null) {
								dialectTemplates.addAll(previous);
							}
						} catch (Exception e) {
							log(IStatus.ERROR, String.format("Plugin '%s' extension '%s' invalid: %s",
									declaringPluginId, EXTENSION_POINT_TEMPLATES, e.getMessage()), e);
						}
					} else {
						log(IStatus.ERROR, String.format("Plugin '%s' extension '%s' unexpected element: %s",
								declaringPluginId, EXTENSION_POINT_CONTENT_ASSIST, element.getName()), null);
					}
				}
			}

			// now that we have the basic templates, check for language extensions and connect the hierarchy

			// first ensure that all language names have templates defined
			Set<String> languageNames = WikiTextPlugin.getDefault().getMarkupLanguageNames();
			for (String languageName : languageNames) {
				Templates languageTemplates = templates.get(languageName);
				if (languageTemplates == null) {
					languageTemplates = new Templates();
					templates.put(languageName, languageTemplates);
				}
			}
			// next connect the hierarchy
			for (String languageName : languageNames) {
				MarkupLanguage markupLanguage = WikiTextPlugin.getDefault().getMarkupLanguage(languageName);
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

}
