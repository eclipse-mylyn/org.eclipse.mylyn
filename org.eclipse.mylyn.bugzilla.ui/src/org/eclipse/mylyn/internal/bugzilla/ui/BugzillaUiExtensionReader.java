/*******************************************************************************
 * Copyright (c) 2004, 2008 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaLanguageSettings;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;

/**
 * @author Frank Becker
 */
public class BugzillaUiExtensionReader {

	public static final String EXTENSION_LANGUAGES = "org.eclipse.mylyn.bugzilla.core.languages"; //$NON-NLS-1$

	public static final String EXTENSION_TMPL_LANGUAGE = "language"; //$NON-NLS-1$

	public static final String ATTR_LANG_VALUE = "value"; //$NON-NLS-1$

	public static final String ELMNT_LANG_NAME = "name"; //$NON-NLS-1$

	public static final String ELMNT_LANG_ERROR_LOGIN = "error_login"; //$NON-NLS-1$

	public static final String ELMNT_LANG_ERROR_COLLISION = "error_collision"; //$NON-NLS-1$

	public static final String ELMNT_LANG_ERROR_COMMENT_REQIRED = "error_comment_required"; //$NON-NLS-1$

	public static final String ELMNT_LANG_ERROR_LOGGED_OUT = "error_logged_out"; //$NON-NLS-1$

	public static final String ELMNT_LANG_BAD_LOGIN = "bad_login"; //$NON-NLS-1$

	public static final String ELMNT_LANG_PROCESSED = "processed"; //$NON-NLS-1$

	public static final String ELMNT_LANG_CHANGES_SUBMITTED = "changes_submitted"; //$NON-NLS-1$

	private static boolean coreExtensionsRead = false;

	public static void initStartupExtensions() {
		if (!coreExtensionsRead) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();

			IExtensionPoint templatesExtensionPoint = registry.getExtensionPoint(EXTENSION_LANGUAGES);
			IExtension[] templateExtensions = templatesExtensionPoint.getExtensions();
			for (IExtension templateExtension : templateExtensions) {
				IConfigurationElement[] elements = templateExtension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (element.getName().equals(EXTENSION_TMPL_LANGUAGE)) {
						readLanguageTemplate(element);
					}
				}
			}

			coreExtensionsRead = true;
		}

	}

	private static void readLanguageTemplate(IConfigurationElement element) {
		String languageName = element.getAttribute(ELMNT_LANG_NAME);
		if (languageName != null) {
			BugzillaLanguageSettings bugzillaLanguageSettings = new BugzillaLanguageSettings(languageName);

			for (IConfigurationElement configElement : element.getChildren()) {
				String name = configElement.getName();
				if (name != null && name.equals("languageAttribute")) { //$NON-NLS-1$
					String command = configElement.getAttribute("command"); //$NON-NLS-1$
					String response = configElement.getAttribute("response"); //$NON-NLS-1$
					bugzillaLanguageSettings.addLanguageAttribute(command, response);
				}
			}
			BugzillaRepositoryConnector.addLanguageSetting(bugzillaLanguageSettings);
		} else {
			StatusHandler.log(new Status(IStatus.WARNING, BugzillaUiPlugin.ID_PLUGIN,
					"Could not load language template extension " + element.getName())); //$NON-NLS-1$
		}
	}
}
