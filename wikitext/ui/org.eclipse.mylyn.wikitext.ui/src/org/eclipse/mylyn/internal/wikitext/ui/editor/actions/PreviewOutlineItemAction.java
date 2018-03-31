/*******************************************************************************
 * Copyright (c) 2011 David Green and others.
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
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.osgi.util.NLS;

/**
 * An action that causes the editor to switch to the preview tab, displaying the preview at a specific heading/section.
 * 
 * @author David Green
 */
public class PreviewOutlineItemAction extends Action {

	private final MarkupEditor editor;

	private final OutlineItem outlineItem;

	public PreviewOutlineItemAction(MarkupEditor editor, OutlineItem outlineItem) {
		this.editor = editor;
		this.outlineItem = outlineItem;
		setText(NLS.bind(Messages.PreviewOutlineItemAction_label, outlineItem.getLabel()));
	}

	@Override
	public void run() {
		editor.showPreview(outlineItem);
	}
}
