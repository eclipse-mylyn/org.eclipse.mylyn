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
package org.eclipse.mylyn.internal.wikitext.ui.editor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor;
import org.eclipse.mylyn.internal.wikitext.ui.util.NlsResourceBundle;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.IFoldingCommandIds;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextOperationAction;

/**
 * 
 * @author David Green
 */
public class ToggleFoldingRulerAction extends AbstractRulerActionDelegate {

	private IAction callerAction;

	private TextOperationAction action;

	private ITextEditor editor;

	@Override
	protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		this.editor = editor;
		action = new TextOperationAction(new NlsResourceBundle(Messages.class), "ToggleFoldingRulerAction_", //$NON-NLS-1$
				editor, ProjectionViewer.TOGGLE, true);
		action.setActionDefinitionId(IFoldingCommandIds.FOLDING_TOGGLE);
		return action;
	}

	@Override
	public void setActiveEditor(IAction callerAction, IEditorPart targetEditor) {
		this.callerAction = callerAction;
		super.setActiveEditor(callerAction, targetEditor);
	}

	@Override
	public void menuAboutToShow(IMenuManager manager) {
		update();
		super.menuAboutToShow(manager);
	}

	private void update() {
		if (editor instanceof MarkupEditor) {
			callerAction.setChecked(((MarkupEditor) editor).isFoldingEnabled());
		}
	}
}
