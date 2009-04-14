/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.java.CompletionProposalCategory;
import org.eclipse.jdt.internal.ui.text.java.CompletionProposalComputerRegistry;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Mik Kersten
 */
public class JavaUiUtil {

	static final String SEPARATOR_CODEASSIST = "\0"; //$NON-NLS-1$

	public static final String ASSIST_MYLYN_ALL = "org.eclipse.mylyn.java.ui.javaAllProposalCategory"; //$NON-NLS-1$

//	public static final String ASSIST_MYLYN_TYPE = "org.eclipse.mylyn.java.javaTypeProposalCategory"; //$NON-NLS-1$

//	public static final String ASSIST_MYLYN_NOTYPE = "org.eclipse.mylyn.java.javaNoTypeProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_JDT_ALL = "org.eclipse.jdt.ui.javaAllProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_JDT_TYPE = "org.eclipse.jdt.ui.javaTypeProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_JDT_NOTYPE = "org.eclipse.jdt.ui.javaNoTypeProposalCategory"; //$NON-NLS-1$

	private static final String ASSIST_JDT_TEMPLATE = "org.eclipse.jdt.ui.templateProposalCategory"; //$NON-NLS-1$

	public static boolean isDefaultAssistActive(String computerId) {
		if (JavaUiUtil.ASSIST_JDT_ALL.equals(computerId)) {
			CompletionProposalCategory category = getProposalCategory(computerId);
			return (category != null) ? category.isEnabled() && category.isIncluded() : false;
		}
		Set<String> disabledIds = getDisabledIds(JavaPlugin.getDefault().getPreferenceStore());
		return !disabledIds.contains(computerId);
	}

	public static CompletionProposalCategory getProposalCategory(String computerId) {
		List<?> computers = CompletionProposalComputerRegistry.getDefault().getProposalCategories();
		for (Object object : computers) {
			CompletionProposalCategory proposalCategory = (CompletionProposalCategory) object;
			if (computerId.equals((proposalCategory).getId())) {
				return proposalCategory;
			}
		}
		return null;
	}

	public static void installContentAssist(IPreferenceStore javaPrefs, boolean mylynContentAssist) {
		Set<String> disabledIds = getDisabledIds(javaPrefs);
		if (!mylynContentAssist) {
			disabledIds.remove(ASSIST_JDT_ALL);
			disabledIds.remove(ASSIST_JDT_TYPE);
			disabledIds.remove(ASSIST_JDT_NOTYPE);
			//disabledIds.remove(ASSIST_JDT_TEMPLATE);
			disabledIds.add(ASSIST_MYLYN_ALL);
			//disabledIds.add(ASSIST_MYLYN_TYPE);
			//disabledIds.add(ASSIST_MYLYN_TEMPLATE);
		} else {
			disabledIds.add(ASSIST_JDT_ALL);
			disabledIds.add(ASSIST_JDT_TYPE);
			disabledIds.add(ASSIST_JDT_NOTYPE);
			// re-enable, Mylyn versions <3.1 had a focused template computer that has been removed  
			disabledIds.remove(ASSIST_JDT_TEMPLATE);
			disabledIds.remove(ASSIST_MYLYN_ALL);
			//disabledIds.remove(ASSIST_MYLYN_NOTYPE);
			//disabledIds.remove(ASSIST_MYLYN_TYPE);
			//disabledIds.remove(ASSIST_MYLYN_TEMPLATE);
		}
		StringBuilder sb = new StringBuilder();
		for (String id : disabledIds) {
			sb.append(id);
			sb.append(SEPARATOR_CODEASSIST);
		}
		javaPrefs.setValue(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, sb.toString());

		CompletionProposalComputerRegistry.getDefault().reload();
	}

	public static Set<String> getDisabledIds(IPreferenceStore javaPrefs) {
		String oldValue = javaPrefs.getString(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
		StringTokenizer tokenizer = new StringTokenizer(oldValue, SEPARATOR_CODEASSIST);
		Set<String> disabledIds = new HashSet<String>();
		while (tokenizer.hasMoreTokens()) {
			disabledIds.add((String) tokenizer.nextElement());
		}
		return disabledIds;
	}

}
