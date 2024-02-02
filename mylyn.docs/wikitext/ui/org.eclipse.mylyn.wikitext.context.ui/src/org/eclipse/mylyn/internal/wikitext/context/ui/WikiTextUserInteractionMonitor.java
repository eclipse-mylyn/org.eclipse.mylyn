/*******************************************************************************
 * Copyright (c) 2009, 2013 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.context.ui;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author David Green
 */
class WikiTextUserInteractionMonitor extends AbstractUserInteractionMonitor {

	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection,
			boolean contributeToContext) {
		if (selection instanceof ITextSelection) {
			OutlineItem item = part.getAdapter(OutlineItem.class);
			if (item != null) {
				OutlineItem relevantItem = item.findNearestMatchingOffset(((ITextSelection) selection).getOffset());
				if (relevantItem == null) {
					relevantItem = item;
				}
				handleElementSelection(part, relevantItem, contributeToContext);
			}
		} else if (selection instanceof IStructuredSelection) {
			for (Object element : ((IStructuredSelection) selection).toArray()) {
				if (element instanceof OutlineItem) {
					handleElementSelection(part, element, contributeToContext);
				}
			}
		}
	}

}
