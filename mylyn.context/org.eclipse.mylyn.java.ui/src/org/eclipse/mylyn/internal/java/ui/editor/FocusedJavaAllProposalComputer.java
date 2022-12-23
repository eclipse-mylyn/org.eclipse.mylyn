/*******************************************************************************
 * Copyright (c) 2008, 2013 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     IBM Corporation - initial API and implementation
 *     Tasktop Technologies - changes for bug 219692
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.editor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.java.JavaAllCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.mylyn.internal.java.ui.JavaUiUtil;

/**
 * Based on org.eclipse.jdt.internal.ui.text.java.JavaAllCompletionProposalComputer.
 * 
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class FocusedJavaAllProposalComputer extends JavaAllCompletionProposalComputer {

	public FocusedJavaAllProposalComputer() {
		FocusedJavaProposalProcessor.getDefault().addMonitoredComputer(this);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (shouldReturnResults()) {
			List proposals = super.computeCompletionProposals(context, monitor);
			return FocusedJavaProposalProcessor.getDefault().projectInterestModel(this, proposals);
		} else {
			return Collections.emptyList();
		}
	}

	private boolean shouldReturnResults() {
		if (JavaUiUtil.isDefaultAssistActive(JavaUiUtil.ASSIST_JDT_ALL)) {
			// do not return duplicates if the default JDT processor is already enabled on Eclipse 3.5
			return false;
		}
		Set<String> disabledIds = JavaUiUtil.getDisabledIds(JavaPlugin.getDefault().getPreferenceStore());
		if (!disabledIds.contains(JavaUiUtil.ASSIST_JDT_NOTYPE) && !disabledIds.contains(JavaUiUtil.ASSIST_JDT_TYPE)) {
			// do not return duplicates if the default JDT processors are already enabled on on Eclipse 3.3 and 3.4
			return false;
		}
		return true;
	}

}
