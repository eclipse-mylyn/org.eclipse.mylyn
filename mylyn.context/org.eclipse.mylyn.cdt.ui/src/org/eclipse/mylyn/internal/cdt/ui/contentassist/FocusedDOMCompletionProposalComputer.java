/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.cdt.ui.contentassist;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.text.contentassist.ContentAssistInvocationContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author Shawn Minto
 */
public class FocusedDOMCompletionProposalComputer extends DOMCompletionProposalComputer {

	public FocusedDOMCompletionProposalComputer() {
		FocusedCProposalProcessor.getDefault().addMonitoredComputer(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		if (shouldReturnResults()) {
			List<?> proposals = super.computeCompletionProposals(context, monitor);
			return FocusedCProposalProcessor.getDefault().projectInterestModel(this, proposals);
		} else {
			return Collections.emptyList();
		}
	}

	private boolean shouldReturnResults() {
		Set<String> disabledIds = CDTContentAssistUtils
				.getDisableContentAssistIds(CUIPlugin.getDefault().getPreferenceStore());
		if (!disabledIds.contains(CDTContentAssistUtils.ASSIST_CDT_TYPE)) {
			// do not return duplicates if the default parser completions is enabled
			return false;
		}
		return true;
	}

}
