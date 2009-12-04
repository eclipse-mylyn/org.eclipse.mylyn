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

package org.eclipse.mylyn.internal.wikitext.ui.editor.preferences;

import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.WikiTextTemplateAccess;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

public class WikiTextTemplatePreferencePage extends TemplatePreferencePage {

	public WikiTextTemplatePreferencePage() {
		setPreferenceStore(WikiTextUiPlugin.getDefault().getPreferenceStore());
		setTemplateStore(WikiTextTemplateAccess.getInstance().getTemplateStore());
		setContextTypeRegistry(WikiTextTemplateAccess.getInstance().getContextTypeRegistry());
	}

	@Override
	protected boolean isShowFormatterSetting() {
		return false;
	}
}
