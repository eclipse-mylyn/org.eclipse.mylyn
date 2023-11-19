/*******************************************************************************
 * Copyright (c) 2013 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.ui.commands;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Creates menu items for each markup language that supports
 * {@link MarkupLanguage#createDocumentBuilder(java.io.Writer)}.
 */
public class WikiMarkupGenerationContribution extends CompoundContributionItem implements IWorkbenchContribution {

	private IServiceLocator serviceLocator;

	@Override
	protected IContributionItem[] getContributionItems() {
		Map<String, IContributionItem> items = new TreeMap<>();

		for (MarkupLanguage markupLanguage : ServiceLocator.getInstance().getAllMarkupLanguages()) {
			try {
				// test to see if markup generation is supported
				markupLanguage.createDocumentBuilder(new StringWriter());
			} catch (UnsupportedOperationException e) {
				// markup langage doesn't provide document builder
				continue;
			}
			String commandId = ConvertMarkupToMarkup.COMMAND_ID;
			String id = commandId + '.' + markupLanguage.getName().replaceAll("\\W", "_"); //$NON-NLS-1$ //$NON-NLS-2$ 
			HashMap<String, String> args = new HashMap<>();
			args.put(ConvertMarkupToMarkup.PARAM_MARKUP_LANGUAGE, markupLanguage.getName());

			CommandContributionItemParameter parameters = new CommandContributionItemParameter(serviceLocator, id,
					commandId, CommandContributionItem.STYLE_PUSH);
			parameters.label = NLS.bind(Messages.WikiMarkupGenerationContribution_generate_markup,
					markupLanguage.getName());
			parameters.parameters = args;

			items.put(markupLanguage.getName(), new CommandContributionItem(parameters));

		}
		return items.values().toArray(new IContributionItem[items.size()]);
	}

	@Override
	public void initialize(IServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}
}
