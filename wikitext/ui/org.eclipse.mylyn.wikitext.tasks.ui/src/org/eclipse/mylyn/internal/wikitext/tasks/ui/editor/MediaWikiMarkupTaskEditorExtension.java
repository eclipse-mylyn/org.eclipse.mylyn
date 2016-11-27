/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.tasks.ui.editor;

import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.tasks.ui.editor.MarkupTaskEditorExtension;

public class MediaWikiMarkupTaskEditorExtension extends MarkupTaskEditorExtension<MediaWikiLanguage> {

	public MediaWikiMarkupTaskEditorExtension() {
		setMarkupLanguage(new MediaWikiLanguage());
	}

}
