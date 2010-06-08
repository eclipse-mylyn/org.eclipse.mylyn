/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.cdt.ui.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.cdt.ui.text.contentassist.ICompletionProposalComputer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.cdt.ui.CDTUIBridgePlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;

/**
 * @author Shawn Minto
 */
public class FocusedCProposalProcessor {

	/**
	 * Range above which elements are part of the context.
	 */
	private static final int THRESHOLD_INTEREST = 10000;

	/**
	 * Range for implicitly interesting element, such as method parameters.
	 */
	private static final int THRESHOLD_IMPLICIT_INTEREST = THRESHOLD_INTEREST * 2;

	/**
	 * Threshold for determining which JDT proposals should be implicitly interesting.
	 */
	private static final int RELEVANCE_IMPLICIT_INTEREST_C = 600;

	/**
	 * Threshold for implicit interest of IJavaElement proposals.
	 */
	private static final int RELEVANCE_IMPLICIT_INTEREST_MISC = 110;

	private static final String IDENTIFIER_THIS = "this"; //$NON-NLS-1$

	public static final String LABEL_SEPARATOR = " -------------------------------------------- "; //$NON-NLS-1$

	public static final FocusedProposalSeparator PROPOSAL_SEPARATOR = new FocusedProposalSeparator();

	private final List<ICompletionProposalComputer> monitoredProposalComputers = new ArrayList<ICompletionProposalComputer>();

	private final List<ICompletionProposalComputer> alreadyComputedProposals = new ArrayList<ICompletionProposalComputer>();

	private final List<ICompletionProposalComputer> alreadyContainSeparator = new ArrayList<ICompletionProposalComputer>();

	private final List<ICompletionProposalComputer> containsSingleInterestingProposal = new ArrayList<ICompletionProposalComputer>();

	private static FocusedCProposalProcessor INSTANCE = new FocusedCProposalProcessor();

	private FocusedCProposalProcessor() {
	}

	public static FocusedCProposalProcessor getDefault() {
		return INSTANCE;
	}

	public void addMonitoredComputer(ICompletionProposalComputer proposalComputer) {
		monitoredProposalComputers.add(proposalComputer);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List projectInterestModel(ICompletionProposalComputer proposalComputer, List proposals) {
		try {
			if (!ContextCore.getContextManager().isContextActive()) {
				return proposals;
			} else {
				boolean hasInterestingProposals = false;
				for (Object object : proposals) {
					if (object instanceof CCompletionProposal) {
						boolean foundInteresting = boostRelevanceWithInterest((CCompletionProposal) object);
						if (!hasInterestingProposals && foundInteresting) {
							hasInterestingProposals = true;
						}
					}
				}

				// NOTE: this annoying state needs to be maintainted to ensure
				// the
				// separator is added only once, and not added for single
				// proposals
				if (containsSingleInterestingProposal.size() > 0 && proposals.size() > 0) {
					proposals.add(FocusedCProposalProcessor.PROPOSAL_SEPARATOR);
				} else if (hasInterestingProposals/* && alreadyContainSeparator.isEmpty()*/) { // FIXME WHY IS THIS DIFFERENT THAN JAVA?
					if (proposals.size() == 1) {
						containsSingleInterestingProposal.add(proposalComputer);
					} else {
						proposals.add(FocusedCProposalProcessor.PROPOSAL_SEPARATOR);
						alreadyContainSeparator.add(proposalComputer);
					}
				}

				alreadyComputedProposals.add(proposalComputer);
				if (alreadyComputedProposals.size() == monitoredProposalComputers.size()) {
					alreadyComputedProposals.clear();
					alreadyContainSeparator.clear();
					containsSingleInterestingProposal.clear();
				}

				return proposals;
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN,
					"Failed to project interest onto propsals", t)); //$NON-NLS-1$
			return proposals;
		}
	}

	private boolean boostRelevanceWithInterest(CCompletionProposal proposal) {

		boolean hasInteresting = false;
		String name = proposal.getBindingName();

		if (name != null) {
			IInteractionElement interactionElement = guessInteractionElement(name);
			if (interactionElement != null) {
				float interest = interactionElement.getInterest().getValue();
				if (interest > ContextCore.getCommonContextScaling().getInteresting()) {
					// TODO: losing precision here, only going to one decimal place
					proposal.setRelevance(THRESHOLD_INTEREST + (int) (interest * 10));
					hasInteresting = true;
				} else if (proposal.getRelevance() > RELEVANCE_IMPLICIT_INTEREST_C) {
					proposal.setRelevance(THRESHOLD_IMPLICIT_INTEREST + proposal.getRelevance());
				}
			} else if (proposal.getRelevance() > RELEVANCE_IMPLICIT_INTEREST_C) {
				proposal.setRelevance(THRESHOLD_IMPLICIT_INTEREST + proposal.getRelevance());
			}
		} else if (isImplicitlyInteresting(proposal)) {
			proposal.setRelevance(THRESHOLD_IMPLICIT_INTEREST + proposal.getRelevance());
			hasInteresting = true;
		}
		return hasInteresting;

// FIXME ADD BACK THE RIGHT WAY TO DO THIS!		
//		boolean hasInteresting = false;
//		ICElement cElement = proposal.getCElement();
//
//		if (cElement != null) {
//			IInteractionElement interactionElement = ContextCore.getContextManager().getElement(
//					cElement.getHandleIdentifier());
//			float interest = interactionElement.getInterest().getValue();
//			if (interest > ContextCore.getCommonContextScaling().getInteresting()) {
//				// TODO: losing precision here, only going to one decimal place
//				proposal.setRelevance(THRESHOLD_INTEREST + (int) (interest * 10));
//				hasInteresting = true;
//			} else if (proposal.getRelevance() > RELEVANCE_IMPLICIT_INTEREST_C) {
//				proposal.setRelevance(THRESHOLD_IMPLICIT_INTEREST + proposal.getRelevance());
//			}
//		} else if (isImplicitlyInteresting(proposal)) {
//			proposal.setRelevance(THRESHOLD_IMPLICIT_INTEREST + proposal.getRelevance());
//			hasInteresting = true;
//		}
//		return hasInteresting;
	}

	private IInteractionElement guessInteractionElement(String name) {
		for (IInteractionElement element : ContextCore.getContextManager().getActiveContext().getInteresting()) {
			String handle = element.getHandleIdentifier();
			// remove any line information so we can compare the end instead of using a contains
			if (handle.contains("#")) { //$NON-NLS-1$
				handle = handle.substring(0, handle.indexOf("#"));//$NON-NLS-1$
			}
			if (handle.endsWith(name)) {
				return element;
			}
		}
		return null;
	}

	public boolean isImplicitlyInteresting(ICCompletionProposal proposal) {
		return proposal.getRelevance() > RELEVANCE_IMPLICIT_INTEREST_MISC
				&& !IDENTIFIER_THIS.equals(proposal.getDisplayString());
	}

	static class FocusedProposalSeparator extends CCompletionProposal {
		public FocusedProposalSeparator() {
			super("", 0, 0, CommonImages.getImage(CommonImages.SEPARATOR_LIST), LABEL_SEPARATOR, //$NON-NLS-1$
					"", FocusedCProposalProcessor.THRESHOLD_INTEREST, null, null, null); //$NON-NLS-1$
		}

		@Override
		public void apply(IDocument document) {
		}

		@Override
		public void apply(IDocument document, char trigger, int offset) {
		}

		@Override
		public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		}
	}
}
