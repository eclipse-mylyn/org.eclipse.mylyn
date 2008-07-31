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
package org.eclipse.mylyn.internal.wikitext.ui.editor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor;
import org.eclipse.mylyn.wikitext.core.WikiTextPlugin;

/**
 *
 *
 * @author David Green
 */
public class SetMarkupLanguageAction extends Action {

	private final MarkupEditor markupEditor;
	private final String markupLanguageName;
	private final boolean checked;

	public SetMarkupLanguageAction(MarkupEditor markupEditor, String markupLanguageName,boolean checked) {
		super(markupLanguageName,IAction.AS_CHECK_BOX);
		setChecked(checked);
		this.markupEditor = markupEditor;
		this.markupLanguageName = markupLanguageName;
		this.checked = checked;
	}

	@Override
	public void run() {
		if (checked) {
			return;
		}
		markupEditor.setMarkupLanguage(WikiTextPlugin.getDefault().getMarkupLanguage(markupLanguageName),true);
	}
}
