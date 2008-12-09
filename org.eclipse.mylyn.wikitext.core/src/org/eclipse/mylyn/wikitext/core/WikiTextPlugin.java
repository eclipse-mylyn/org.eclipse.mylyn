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
package org.eclipse.mylyn.wikitext.core;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.wikitext.core.util.EclipseServiceLocator;
import org.eclipse.mylyn.internal.wikitext.core.validation.ValidationRules;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.eclipse.mylyn.wikitext.core.validation.MarkupValidator;
import org.eclipse.mylyn.wikitext.core.validation.ValidationRule;
import org.osgi.framework.BundleContext;

/**
 * The WikiText plug-in class. Use only in an Eclipse runtime environment. Stand-alone programs should use the
 * {@link ServiceLocator} instead.
 * 
 * Should not be instantiated directly, instead use {@link #getDefault()}.
 * 
 * @author David Green
 * 
 * @see #getDefault()
 * @see ServiceLocator
 */
public class WikiTextPlugin extends Plugin {

	private static final String EXTENSION_MARKUP_LANGUAGE = "markupLanguage"; //$NON-NLS-1$

	private static final String EXTENSION_VALIDATION_RULES = "markupValidationRule"; //$NON-NLS-1$

	private static WikiTextPlugin plugin;

	private SortedMap<String, Class<? extends MarkupLanguage>> languageByName;

	private Map<String, Class<? extends MarkupLanguage>> languageByFileExtension;

	private Map<Class<? extends MarkupLanguage>, String> languageNameByLanguage;

	private Map<String, String> languageExtensionByLanguage;

	private Map<String, ValidationRules> validationRulesByLanguageName;

	public WikiTextPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ServiceLocator.setImplementation(EclipseServiceLocator.class);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (plugin == this) {
			plugin = null;
		}
		super.stop(context);
	}

	public static WikiTextPlugin getDefault() {
		return plugin;
	}

	/**
	 * Get a markup language by name.
	 * 
	 * @param name
	 *            the name of the markup language to retrieve
	 * 
	 * @return the markup language or null if there is no markup language known by the given name
	 * 
	 * @see #getMarkupLanguageNames()
	 */
	public MarkupLanguage getMarkupLanguage(String name) {
		if (languageByName == null) {
			initializeMarkupLanguages();
		}
		Class<? extends MarkupLanguage> languageClass = languageByName.get(name);
		if (languageClass == null) {
			// if not found by name, attempt to lookup by class name. 
			for (Class<? extends MarkupLanguage> clazz : languageByName.values()) {
				if (clazz.getName().equals(name)) {
					languageClass = clazz;
					break;
				}
			}
		}
		if (languageClass != null) {
			return instantiateMarkupLanguage(name, languageClass);
		}
		return null;
	}

	private MarkupLanguage instantiateMarkupLanguage(String name, Class<? extends MarkupLanguage> languageClass) {
		try {
			MarkupLanguage language = languageClass.newInstance();
			language.setName(name);
			language.setExtendsLanguage(languageExtensionByLanguage.get(name));
			return language;
		} catch (Exception e) {
			log(IStatus.ERROR, MessageFormat.format(Messages.getString("WikiTextPlugin.2"), name, //$NON-NLS-1$
					languageClass.getName(), e.getMessage()), e);
		}
		return null;
	}

	/**
	 * Get a markup language name for a file. A markup language is selected based on the registered languages and their
	 * expected file extensions.
	 * 
	 * @param name
	 *            the name of the file for which a markup language is desired
	 * 
	 * @return the markup language name, or null if no markup language is registered for the specified file name
	 * 
	 * @see #getMarkupLanguageForFilename(String)
	 */
	public String getMarkupLanguageNameForFilename(String name) {
		if (languageByFileExtension == null) {
			initializeMarkupLanguages();
		}
		int lastIndexOfDot = name.lastIndexOf('.');
		String extension = lastIndexOfDot == -1 ? name : name.substring(lastIndexOfDot + 1);
		Class<? extends MarkupLanguage> languageClass = languageByFileExtension.get(extension);
		if (languageClass != null) {
			return languageNameByLanguage.get(languageClass);
		}
		return null;
	}

	/**
	 * Get the file extensions that are registered for markup languages. File extensions are specified without the
	 * leading dot.
	 */
	public Set<String> getMarkupFileExtensions() {
		if (languageByFileExtension == null) {
			initializeMarkupLanguages();
		}
		return Collections.unmodifiableSet(languageByFileExtension.keySet());
	}

	/**
	 * Get a markup language for a file. A markup language is selected based on the registered languages and their
	 * expected file extensions.
	 * 
	 * @param name
	 *            the name of the file for which a markup language is desired
	 * 
	 * @return the markup language, or null if no markup language is registered for the specified file name
	 * 
	 * @see #getMarkupLanguageForFilename(String)
	 */
	public MarkupLanguage getMarkupLanguageForFilename(String name) {
		if (languageByFileExtension == null) {
			initializeMarkupLanguages();
		}
		int lastIndexOfDot = name.lastIndexOf('.');
		String extension = lastIndexOfDot == -1 ? name : name.substring(lastIndexOfDot + 1);
		Class<? extends MarkupLanguage> languageClass = languageByFileExtension.get(extension);
		if (languageClass != null) {
			String languageName = null;
			for (Map.Entry<String, Class<? extends MarkupLanguage>> ent : languageByName.entrySet()) {
				if (ent.getValue() == languageClass) {
					languageName = ent.getKey();
					break;
				}
			}
			return instantiateMarkupLanguage(languageName, languageClass);
		}
		return null;
	}

	/**
	 * Get the names of all known markup languages
	 * 
	 * @see #getMarkupLanguage(String)
	 */
	public Set<String> getMarkupLanguageNames() {
		if (languageByName == null) {
			initializeMarkupLanguages();
		}
		return languageByName.keySet();
	}

	/**
	 * Get a markup validator by language name.
	 * 
	 * @param name
	 *            the name of the markup language for which a validator is desired
	 * 
	 * @return the markup validator
	 * 
	 * @see #getMarkupLanguageNames()
	 */
	public MarkupValidator getMarkupValidator(String name) {
		MarkupValidator markupValidator = new MarkupValidator();

		if (validationRulesByLanguageName == null) {
			initializeValidationRules();
		}
		ValidationRules rules = validationRulesByLanguageName.get(name);
		if (rules != null) {
			markupValidator.getRules().addAll(rules.getRules());
		}

		return markupValidator;
	}

	private void initializeValidationRules() {
		initializeMarkupLanguages();
		synchronized (this) {
			if (validationRulesByLanguageName == null) {
				Map<String, ValidationRules> validationRulesByLanguageName = new HashMap<String, ValidationRules>();

				IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(getPluginId(),
						EXTENSION_VALIDATION_RULES);
				if (extensionPoint != null) {
					IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
					for (IConfigurationElement element : configurationElements) {
						try {
							String markupLanguage = element.getAttribute("markupLanguage"); //$NON-NLS-1$
							if (markupLanguage == null || markupLanguage.length() == 0) {
								throw new Exception(Messages.getString("WikiTextPlugin.4")); //$NON-NLS-1$
							}
							if (!languageByName.containsKey(markupLanguage)) {
								throw new Exception(MessageFormat.format(
										Messages.getString("WikiTextPlugin.5"), languageByName)); //$NON-NLS-1$
							}
							Object extension;
							try {
								extension = element.createExecutableExtension("class"); //$NON-NLS-1$
							} catch (CoreException e) {
								getLog().log(e.getStatus());
								continue;
							}
							if (!(extension instanceof ValidationRule)) {
								throw new Exception(MessageFormat.format(
										Messages.getString("WikiTextPlugin.7"), extension.getClass() //$NON-NLS-1$
												.getName()));
							}
							ValidationRules rules = validationRulesByLanguageName.get(markupLanguage);
							if (rules == null) {
								rules = new ValidationRules();
								validationRulesByLanguageName.put(markupLanguage, rules);
							}
							rules.addValidationRule((ValidationRule) extension);
						} catch (Exception e) {
							log(IStatus.ERROR, MessageFormat.format(
									Messages.getString("WikiTextPlugin.8"), //$NON-NLS-1$
									element.getDeclaringExtension().getContributor().getName(),
									EXTENSION_VALIDATION_RULES, e.getMessage()), e);
						}
					}
				}

				// now that we have the basic validation rules, check for language extensions and connect the hierarchy

				// first ensure that all language names have templates defined
				Set<String> languageNames = WikiTextPlugin.getDefault().getMarkupLanguageNames();
				for (String languageName : languageNames) {
					ValidationRules rules = validationRulesByLanguageName.get(languageName);
					if (rules == null) {
						rules = new ValidationRules();
						validationRulesByLanguageName.put(languageName, rules);
					}
				}
				// next connect the hierarchy
				for (String languageName : languageNames) {
					MarkupLanguage markupLanguage = WikiTextPlugin.getDefault().getMarkupLanguage(languageName);
					if (markupLanguage != null && markupLanguage.getExtendsLanguage() != null) {
						ValidationRules languageRules = validationRulesByLanguageName.get(languageName);
						ValidationRules parentLanguageRules = validationRulesByLanguageName.get(markupLanguage.getExtendsLanguage());

						languageRules.setParent(parentLanguageRules);
					}
				}

				this.validationRulesByLanguageName = validationRulesByLanguageName;
			}
		}
	}

	private void initializeMarkupLanguages() {
		synchronized (this) {
			if (this.languageByName == null) {
				SortedMap<String, Class<? extends MarkupLanguage>> markupLanguageByName = new TreeMap<String, Class<? extends MarkupLanguage>>();
				Map<String, Class<? extends MarkupLanguage>> languageByFileExtension = new HashMap<String, Class<? extends MarkupLanguage>>();
				Map<String, String> languageExtensionByLanguage = new HashMap<String, String>();
				Map<Class<? extends MarkupLanguage>, String> languageNameByLanguage = new HashMap<Class<? extends MarkupLanguage>, String>();

				IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(getPluginId(),
						EXTENSION_MARKUP_LANGUAGE);
				if (extensionPoint != null) {
					IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
					for (IConfigurationElement element : configurationElements) {
						String name = element.getAttribute("name"); //$NON-NLS-1$
						if (name == null || name.length() == 0) {
							log(IStatus.ERROR, MessageFormat.format(EXTENSION_MARKUP_LANGUAGE
									+ Messages.getString("WikiTextPlugin.10"), element.getDeclaringExtension() //$NON-NLS-1$
									.getContributor()
									.getName()));
							continue;
						}
						String extendsLanguage = element.getAttribute("extends"); //$NON-NLS-1$
						Object markupLanguage;
						try {
							markupLanguage = element.createExecutableExtension("class"); //$NON-NLS-1$
						} catch (CoreException e) {
							getLog().log(e.getStatus());
							continue;
						}
						if (!(markupLanguage instanceof MarkupLanguage)) {
							log(IStatus.ERROR, MessageFormat.format(
									Messages.getString("WikiTextPlugin.13"), markupLanguage.getClass() //$NON-NLS-1$
											.getName()));
							continue;
						}
						MarkupLanguage d = (MarkupLanguage) markupLanguage;
						{
							Class<? extends MarkupLanguage> previous = markupLanguageByName.put(name, d.getClass());
							if (previous != null) {
								log(IStatus.ERROR, MessageFormat.format(EXTENSION_MARKUP_LANGUAGE
										+ Messages.getString("WikiTextPlugin.14"), //$NON-NLS-1$
										name, element.getDeclaringExtension().getContributor().getName(), name));
								markupLanguageByName.put(name, previous);
								continue;
							} else {
								languageNameByLanguage.put(d.getClass(), name);
							}
						}
						if (extendsLanguage != null) {
							languageExtensionByLanguage.put(name, extendsLanguage);
						}
						String fileExtensions = element.getAttribute("fileExtensions"); //$NON-NLS-1$
						if (fileExtensions != null) {
							String[] parts = fileExtensions.split("\\s*,\\s*"); //$NON-NLS-1$
							for (String part : parts) {
								if (part.length() != 0) {
									Class<? extends MarkupLanguage> previous = languageByFileExtension.put(part,
											d.getClass());
									if (previous != null) {
										log(IStatus.ERROR, MessageFormat.format(EXTENSION_MARKUP_LANGUAGE
												+ Messages.getString("WikiTextPlugin.17"), //$NON-NLS-1$
												part, element.getDeclaringExtension().getContributor().getName(), part));
										languageByFileExtension.put(part, previous);
										continue;
									}
								}
							}
						}
					}
				}

				this.languageByFileExtension = languageByFileExtension;
				this.languageByName = markupLanguageByName;
				this.languageExtensionByLanguage = languageExtensionByLanguage;
				this.languageNameByLanguage = languageNameByLanguage;
			}
		}
	}

	public void log(int severity, String message) {
		log(severity, message, null);
	}

	public void log(int severity, String message, Throwable t) {
		getLog().log(new Status(severity, getPluginId(), message, t));
	}

	public String getPluginId() {
		return getBundle().getSymbolicName();
	}
}
