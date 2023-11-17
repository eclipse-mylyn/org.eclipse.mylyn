/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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
package org.eclipse.mylyn.internal.wikitext.tasks.ui.editor;

import org.eclipse.mylyn.wikitext.mediawiki.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.tasks.ui.editor.MarkupTaskEditorExtension;

public class MediaWikiMarkupTaskEditorExtension extends MarkupTaskEditorExtension<MediaWikiLanguage> {

	public MediaWikiMarkupTaskEditorExtension() {
		setMarkupLanguage(new MediaWikiLanguage());
	}

}
