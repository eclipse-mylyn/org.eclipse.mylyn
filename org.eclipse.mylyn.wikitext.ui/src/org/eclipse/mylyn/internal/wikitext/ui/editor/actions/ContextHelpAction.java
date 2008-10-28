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
package org.eclipse.mylyn.internal.wikitext.ui.editor.actions;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * An action that invokes context help with a specific help context id.
 * 
 * @author David Green
 */
public class ContextHelpAction extends Action {

	private static final String DEFAULT_WIKITEXT_HELP_ID = "org.eclipse.mylyn.wikitext.ui.markupSourceContext"; //$NON-NLS-1$

	private final String contextId;

	public ContextHelpAction(String contextId) {
		super("", AS_PUSH_BUTTON); //$NON-NLS-1$
		this.contextId = contextId;
		setToolTipText(Messages.getString("ContextHelpAction.2")); //$NON-NLS-1$
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(WikiTextUiPlugin.getDefault().getPluginId(),
				"icons/help.gif")); //$NON-NLS-1$
	}

	public ContextHelpAction() {
		this(DEFAULT_WIKITEXT_HELP_ID);
	}

	@Override
	public void run() {
		IContext context = HelpSystem.getContext(contextId);
		if (context != null) {
			PlatformUI.getWorkbench().getHelpSystem().displayHelp(context);
		} else {
			PlatformUI.getWorkbench().getHelpSystem().displayHelp();
		}
	}
}
