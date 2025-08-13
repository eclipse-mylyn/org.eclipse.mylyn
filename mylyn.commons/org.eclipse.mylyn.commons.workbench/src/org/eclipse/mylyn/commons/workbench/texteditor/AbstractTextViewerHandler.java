/*******************************************************************************
 * Copyright (c) 2007, 2011 Tasktop Technologies Inc. and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.texteditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Abstract command handler that can get the current text viewer.
 *
 * @author David Green
 * @since 3.7
 */
public abstract class AbstractTextViewerHandler extends AbstractHandler {

	/**
	 * get the {@link ITextViewer} for the given event. Depends on the <tt>activeFocusControl</tt> event variable being an instanceof
	 * {@link StyledText}. The {@link StyledText#getData(String))} is expected to have a value for one of
	 * <code>ITextViewer.class.getName()</code> or <code>ISourceViewer.class.getName()</code>.
	 *
	 * @return the text viewer or null if it cannot be found
	 */
	protected ITextViewer getTextViewer(ExecutionEvent event) throws ExecutionException {
		Object activeFocusControl = HandlerUtil.getVariable(event, "activeFocusControl"); //$NON-NLS-1$
		if (activeFocusControl instanceof StyledText textWidget) {
			ITextViewer viewer = (ITextViewer) textWidget.getData(ITextViewer.class.getName());
			if (viewer == null) {
				viewer = (ITextViewer) textWidget.getData(ISourceViewer.class.getName());
			}
			return viewer;
		}
		return null;
	}

}