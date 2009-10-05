/*******************************************************************************
 * Copyright  (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.cdt.ui.contentassist;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.cdt.internal.ui.text.contentassist.CompletionProposalCategory;
import org.eclipse.cdt.internal.ui.text.contentassist.CompletionProposalComputerRegistry;
import org.eclipse.cdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Shawn Minto
 */
public class CDTContentAssistUtils {

	private static final String SEPARATOR_CODEASSIST = "\0"; //$NON-NLS-1$

	public static final String ASSIST_MYLYN_TYPE = "org.eclipse.mylyn.cdt.ui.parserProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_CDT_TYPE = "org.eclipse.cdt.ui.parserProposalCategory"; //$NON-NLS-1$

	public static void installContentAssist(IPreferenceStore cdtPrefs, boolean mylynContentAssist) {
		Set<String> disabledIds = getDisableContentAssistIds(cdtPrefs);
		if (!mylynContentAssist) {
			disabledIds.remove(ASSIST_CDT_TYPE);
			disabledIds.add(ASSIST_MYLYN_TYPE);
		} else {
			disabledIds.add(ASSIST_CDT_TYPE);
			disabledIds.remove(ASSIST_MYLYN_TYPE);
		}
		String newValue = ""; //$NON-NLS-1$
		for (String id : disabledIds) {
			newValue += id + SEPARATOR_CODEASSIST;
		}
		cdtPrefs.setValue(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, newValue);

		CompletionProposalComputerRegistry registry = CompletionProposalComputerRegistry.getDefault();
		List<CompletionProposalCategory> categories = registry.getProposalCategories();

		for (CompletionProposalCategory cat : categories) {
			if (disabledIds.contains(cat.getId())) {
				cat.setIncluded(false);
			} else {
				cat.setIncluded(true);
			}
		}
	}

	public static Set<String> getDisableContentAssistIds(IPreferenceStore cdtPrefs) {
		String oldValue = cdtPrefs.getString(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
		StringTokenizer tokenizer = new StringTokenizer(oldValue, SEPARATOR_CODEASSIST);
		Set<String> disabledIds = new HashSet<String>();
		while (tokenizer.hasMoreTokens()) {
			disabledIds.add((String) tokenizer.nextElement());
		}
		return disabledIds;
	}

	public static void updateDefaultPreference(IPreferenceStore cdtPrefs) {
		// the Task-Focused category should be disabled if the user reverts to the default 
		String defaultValue = cdtPrefs.getDefaultString(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
		cdtPrefs.setDefault(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, defaultValue + ASSIST_MYLYN_TYPE
				+ SEPARATOR_CODEASSIST);
	}

}
