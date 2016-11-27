/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.assist;

import java.io.IOException;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

public class WikiTextTemplateAccess {
	private static final String CUSTOM_TEMPLATES_KEY = "org.eclipse.mylyn.wikitext.ui.customtemplates"; //$NON-NLS-1$

	private static WikiTextTemplateAccess instance;

	public static WikiTextTemplateAccess getInstance() {
		if (instance == null) {
			instance = new WikiTextTemplateAccess();
		}
		return instance;
	}

	private ContextTypeRegistry registry;

	private TemplateStore templateStore;

	public ContextTypeRegistry getContextTypeRegistry() {
		if (registry == null) {
			if (WikiTextUiPlugin.getDefault() != null) {
				ContributionContextTypeRegistry contributionRegistry = new ContributionContextTypeRegistry();
				contributionRegistry.addContextType(SourceTemplateContextType.ID);
				registry = contributionRegistry;
			} else {
				ContextTypeRegistry contextTypeRegistry = new ContextTypeRegistry();
				contextTypeRegistry.addContextType(new SourceTemplateContextType());
				registry = contextTypeRegistry;
			}
		}
		return registry;
	}

	/**
	 * @return this template store, or null if there is no template store.
	 */
	public TemplateStore getTemplateStore() {
		if (templateStore == null) {
			if (WikiTextUiPlugin.getDefault() != null) {
				templateStore = new ContributionTemplateStore(getContextTypeRegistry(), WikiTextUiPlugin.getDefault()
						.getPreferenceStore(), CUSTOM_TEMPLATES_KEY);
				try {
					templateStore.load();
				} catch (IOException e) {
					WikiTextUiPlugin.getDefault().log(e);
				}
			}
		}
		return templateStore;
	}
}
