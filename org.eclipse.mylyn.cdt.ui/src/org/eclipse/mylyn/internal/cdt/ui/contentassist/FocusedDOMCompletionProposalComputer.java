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
		super();
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
		Set<String> disabledIds = CDTContentAssistUtils.getDisableContentAssistIds(CUIPlugin.getDefault()
				.getPreferenceStore());
		if (!disabledIds.contains(CDTContentAssistUtils.ASSIST_CDT_TYPE)) {
			// do not return duplicates if the default parser completions is enabled
			return false;
		}
		return true;
	}

}
