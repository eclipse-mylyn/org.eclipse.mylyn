/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * TODO: parametrize relevance levels (requires JDT changes, bug 119063)
 * 
 * @author Mik Kersten
 */
public class FocusedJavaProposalProcessor {

	static final int THRESHOLD_INTEREST = 10000;

	private static final int THRESHOLD_IMPLICIT_INTEREST = THRESHOLD_INTEREST * 2;

	private static final int RELEVANCE_IMPLICIT_INTEREST = 300;

	private static final String IDENTIFIER_THIS = "this";

	public static final String LABEL_SEPARATOR = " -------------------------------------------- ";

	public static final FocusedProposalSeparator PROPOSAL_SEPARATOR = new FocusedProposalSeparator();

	private final List<IJavaCompletionProposalComputer> monitoredProposalComputers = new ArrayList<IJavaCompletionProposalComputer>();

	private final List<IJavaCompletionProposalComputer> alreadyComputedProposals = new ArrayList<IJavaCompletionProposalComputer>();

	private final List<IJavaCompletionProposalComputer> alreadyContainSeparator = new ArrayList<IJavaCompletionProposalComputer>();

	private final List<IJavaCompletionProposalComputer> containsSingleInterestingProposal = new ArrayList<IJavaCompletionProposalComputer>();

	private static FocusedJavaProposalProcessor INSTANCE = new FocusedJavaProposalProcessor();

	private FocusedJavaProposalProcessor() {
	}

	public static FocusedJavaProposalProcessor getDefault() {
		return INSTANCE;
	}

	public void addMonitoredComputer(IJavaCompletionProposalComputer proposalComputer) {
		monitoredProposalComputers.add(proposalComputer);
	}

	@SuppressWarnings("unchecked")
	public List projectInterestModel(IJavaCompletionProposalComputer proposalComputer, List proposals) {
		try {
			if (!ContextCore.getContextManager().isContextActive()) {
				return proposals;
			} else {
				boolean hasInterestingProposals = false;
				for (Object object : proposals) {
					if (object instanceof AbstractJavaCompletionProposal) {
						boolean foundInteresting = boostRelevanceWithInterest((AbstractJavaCompletionProposal) object);
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
					proposals.add(FocusedJavaProposalProcessor.PROPOSAL_SEPARATOR);
				} else if (hasInterestingProposals && alreadyContainSeparator.isEmpty()) {
					if (proposals.size() == 1) {
						containsSingleInterestingProposal.add(proposalComputer);
					} else {
						proposals.add(FocusedJavaProposalProcessor.PROPOSAL_SEPARATOR);
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
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.PLUGIN_ID,
					"Failed to project interest onto propsals", t));
			return proposals;
		}
	}

	private boolean boostRelevanceWithInterest(AbstractJavaCompletionProposal proposal) {
		boolean hasInteresting = false;
		IJavaElement javaElement = proposal.getJavaElement();
		if (javaElement != null) {
			IInteractionElement interactionElement = ContextCore.getContextManager().getElement(
					javaElement.getHandleIdentifier());
			float interest = interactionElement.getInterest().getValue();
			if (interest > ContextCore.getCommonContextScaling().getInteresting()) {
				// TODO: losing precision here, only going to one decimal place
				proposal.setRelevance(THRESHOLD_INTEREST + (int) (interest * 10));
				hasInteresting = true;
			}
		} else if (isImplicitlyInteresting(proposal)) {
			proposal.setRelevance(THRESHOLD_IMPLICIT_INTEREST + proposal.getRelevance());
			hasInteresting = true;
		}
		return hasInteresting;
	}

	public boolean isImplicitlyInteresting(AbstractJavaCompletionProposal proposal) {
		return proposal.getRelevance() > RELEVANCE_IMPLICIT_INTEREST
				&& !IDENTIFIER_THIS.equals(proposal.getDisplayString());
	}

	static class FocusedProposalSeparator extends JavaCompletionProposal {
		public FocusedProposalSeparator() {
			super("", 0, 0, ContextUiImages.getImage(ContextUiImages.CONTENT_ASSIST_SEPARATOR), LABEL_SEPARATOR,
					FocusedJavaProposalProcessor.THRESHOLD_INTEREST);
		}
	}
}
