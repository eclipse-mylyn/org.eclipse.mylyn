/*******************************************************************************
 * Copyright (c) 2014 Daniel Deady and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Deady <daniel@clockworkgeek.com> - Bug 438968
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.tasks.ui.editor;

import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.tasks.ui.editor.MarkupTaskEditorExtension;

public class MarkdownMarkupTaskEditorExtension extends MarkupTaskEditorExtension<MarkdownLanguage> {

	public MarkdownMarkupTaskEditorExtension() {
		setMarkupLanguage(new MarkdownLanguage());
	}

}
