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
package org.eclipse.mylyn.internal.wikitext.ui.editor.commands;

import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.actions.ContextHelpAction;
import org.eclipse.mylyn.internal.wikitext.ui.editor.help.HelpContent;
import org.eclipse.mylyn.internal.wikitext.ui.util.InformationPresenterUtil;
import org.eclipse.mylyn.wikitext.core.WikiText;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * A command that can show a cheat-sheet (help content) for a specific markup language.
 * 
 * For the command to operate, the following conditions must be met:
 * <ul>
 * <li>The {@link ExecutionEvent#getApplicationContext() application context} must be an {@link EvaluationContext}.</li>
 * <li>The {@link EvaluationContext} must have an <code>activeFocusControl</code>
 * {@link EvaluationContext#getVariable(String) variable}</li>
 * <li>The control must have the folllowing data items:
 * 
 * <pre>
 * &lt;code&gt;
 * 	MarkupLanguage markupLanguage = (MarkupLanguage) control.getData(MarkupLanguage.class.getName());
 * 	ISourceViewer viewer = (ISourceViewer) control.getData(ISourceViewer.class.getName());
 *  &lt;/code&gt;
 * </pre>
 * 
 * </li>
 * </ul>
 * 
 * @author David Green
 */
public class ShowCheatSheetCommand extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object activeFocusControl = HandlerUtil.getVariable(event, "activeFocusControl"); //$NON-NLS-1$
		if (activeFocusControl instanceof Control) {
			Control control = (Control) activeFocusControl;
			MarkupLanguage markupLanguage = (MarkupLanguage) control.getData(MarkupLanguage.class.getName());
			ISourceViewer viewer = (ISourceViewer) control.getData(ISourceViewer.class.getName());

			if (markupLanguage != null && viewer != null) {
				ToolBarManager toolBarManager = new ToolBarManager();
				toolBarManager.add(new ContextHelpAction());
				final InformationPresenter informationPresenter = InformationPresenterUtil.getHtmlInformationPresenter(
						viewer, new InformationPresenterUtil.SizeConstraint(50, 200, true, true), toolBarManager,
						computeCheatSheet(markupLanguage));

				// show information asynchronously since on Eclipse 3.4 it will disappear otherwise
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						informationPresenter.showInformation();
					}
				});
			}
		}

		return null;
	}

	private String computeCheatSheet(MarkupLanguage markupLanguage) {
		HelpContent content = null;
		if (markupLanguage != null) {
			content = WikiTextUiPlugin.getDefault().getCheatSheets().get(markupLanguage.getName());
			if (content == null) {
				// explore the hierarchy for cheat-sheet content
				MarkupLanguage l = markupLanguage;

				while (content == null && l != null && l.getExtendsLanguage() != null) {
					l = WikiText.getMarkupLanguage(l.getExtendsLanguage());
					if (l != null) {
						content = WikiTextUiPlugin.getDefault().getCheatSheets().get(l.getName());
					}
				}
			}
		}
		if (content != null) {
			try {
				return content.getContent();
			} catch (Exception e) {
				WikiTextUiPlugin.getDefault().log(e);
			}
		}
		return MessageFormat.format(Messages.getString("MarkupEditor.noCheatSheetContent"), //$NON-NLS-1$
				markupLanguage == null ? Messages.getString("MarkupEditor.noDialect") : markupLanguage.getName()); //$NON-NLS-1$
	}
}
