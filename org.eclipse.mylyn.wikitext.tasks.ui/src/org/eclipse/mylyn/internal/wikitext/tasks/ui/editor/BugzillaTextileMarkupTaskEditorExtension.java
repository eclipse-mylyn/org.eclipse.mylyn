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

import org.eclipse.mylyn.wikitext.tasks.ui.editor.MarkupTaskEditorExtension;
import org.eclipse.mylyn.wikitext.textile.core.BugzillaTextileLanguage;

/**
 * A task editor extension that uses the {@link BugzillaTextileLanguage}.
 * 
 * @author David Green
 */
public class BugzillaTextileMarkupTaskEditorExtension extends MarkupTaskEditorExtension<BugzillaTextileLanguage> {

	public BugzillaTextileMarkupTaskEditorExtension() {
		setMarkupLanguage(new BugzillaTextileLanguage());
	}

}
