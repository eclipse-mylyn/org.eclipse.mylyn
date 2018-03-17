/*******************************************************************************
 * Copyright (c) 2018 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.preferences;

import java.util.regex.Pattern;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.text.FindReplaceDocumentAdapterContentProposalProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

class RegExStringFieldEditor extends StringFieldEditor {
	public RegExStringFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		setErrorMessage(Messages.EditorPreferencePage_openInPreviewInvalidRegEx);
	}

	@Override
	protected boolean doCheckState() {
		String expression = getStringValue();
		try {
			Pattern.compile(expression);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);
		Text regexpText = getTextControl();
		addContentAssist(regexpText);
	}

	private void addContentAssist(Text regexpText) {
		new ContentAssistCommandAdapter(regexpText, new TextContentAdapter(),
				new FindReplaceDocumentAdapterContentProposalProvider(true), null, new char[0], true);
	}
}