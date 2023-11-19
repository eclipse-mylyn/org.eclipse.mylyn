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
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor;
import org.eclipse.mylyn.wikitext.ui.WikiText;

/**
 * @author David Green
 */
public class SetMarkupLanguageAction extends Action {

	private final MarkupEditor markupEditor;

	private final String markupLanguageName;

	private final boolean checked;

	public SetMarkupLanguageAction(MarkupEditor markupEditor, String markupLanguageName, boolean checked) {
		super(markupLanguageName, IAction.AS_CHECK_BOX);
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
		markupEditor.setMarkupLanguage(WikiText.getMarkupLanguage(markupLanguageName), true);
	}
}
