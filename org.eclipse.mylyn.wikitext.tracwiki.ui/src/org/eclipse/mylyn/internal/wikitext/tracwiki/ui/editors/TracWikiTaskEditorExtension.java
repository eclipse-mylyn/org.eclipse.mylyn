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

package org.eclipse.mylyn.internal.wikitext.tracwiki.ui.editors;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.ui.editor.MarkupTaskEditorExtension;

/**
 * 
 * 
 * @author David Green
 */
public class TracWikiTaskEditorExtension extends MarkupTaskEditorExtension {

	public TracWikiTaskEditorExtension() {
		setMarkupLanguage(new TracWikiLanguage());
	}

	@Override
	protected void configureDefaultInternalLinkPattern(TaskRepository taskRepository, MarkupLanguage markupLanguage) {
		String url = taskRepository.getRepositoryUrl();
		if (url != null && url.length() > 0) {
			if (!url.endsWith("/")) {
				url = url + "/";
			}
			markupLanguage.setInternalLinkPattern(url + "wiki/{0}");
		}
	}
}
