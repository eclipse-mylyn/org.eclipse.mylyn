/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktopp Technologies.
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

package org.eclipse.mylyn.internal.wikitext.tasks.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.mylyn.commons.workbench.texteditor.AbstractTextViewerHandler;

/**
 * A command handler for incremental find.
 * 
 * @author David Green
 * @see IFindReplaceTarget
 * @see IFindReplaceTargetExtension
 */
public class IncrementalFindHandler extends AbstractTextViewerHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ITextViewer viewer = getTextViewer(event);
		if (viewer != null) {
			IFindReplaceTarget findReplaceTarget = viewer.getFindReplaceTarget();
			if (findReplaceTarget.canPerformFind()) {
				if (findReplaceTarget instanceof IFindReplaceTargetExtension) {
					IFindReplaceTargetExtension extension = (IFindReplaceTargetExtension) findReplaceTarget;
					extension.beginSession();
				}
			}
		}
		return null;
	}

}
