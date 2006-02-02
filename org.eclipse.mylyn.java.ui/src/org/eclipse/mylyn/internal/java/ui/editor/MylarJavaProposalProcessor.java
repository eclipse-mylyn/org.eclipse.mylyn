/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.java.ui.editor;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.ui.MylarImages;

/**
 * TODO: parametrize relevance levels
 * 
 * @author Mik Kersten
 */
public class MylarJavaProposalProcessor {

	static final int THRESHOLD_INTEREST = 1000;

	private static final int THRESHOLD_IMPLICIT_INTEREST = THRESHOLD_INTEREST * 2;

	private static final int RELEVANCE_IMPLICIT_INTEREST = 300;

	private static final String IDENTIFIER_THIS = "this";

	public static final String LABEL_SEPARATOR = " ------------------------------------------";
	
	public static final MylarProposalSeparator PROPOSAL_SEPARATOR = new MylarProposalSeparator();

	@SuppressWarnings("unchecked")
	public List projectInterestModel(List proposals, boolean addSeparator) {
		if (!MylarPlugin.getContextManager().isContextActive()) {
			return proposals;
		} else {
			for (Object object : proposals) {
				if (object instanceof AbstractJavaCompletionProposal) {
					boostRelevanceWithInterest((AbstractJavaCompletionProposal) object);
				}
			}
			if (addSeparator) {
				proposals.add(MylarJavaProposalProcessor.PROPOSAL_SEPARATOR);
			}
			return proposals;
		}
	}

	private boolean boostRelevanceWithInterest(AbstractJavaCompletionProposal proposal) {
		boolean hasInteresting = false;
		IJavaElement javaElement = proposal.getJavaElement();
		if (javaElement != null) {
			IMylarElement mylarElement = MylarPlugin.getContextManager().getElement(javaElement.getHandleIdentifier());
			float interest = mylarElement.getInterest().getValue();
			if (interest >= MylarContextManager.getScalingFactors().getInteresting()) {
				hasInteresting = true;
			}
			proposal.setRelevance(THRESHOLD_INTEREST + (int) interest);
		} else if (isImplicitlyInteresting(proposal)) {
			proposal.setRelevance(THRESHOLD_IMPLICIT_INTEREST + proposal.getRelevance());
		}
		return hasInteresting;
	}

	public boolean isImplicitlyInteresting(AbstractJavaCompletionProposal proposal) {
		return proposal.getRelevance() > RELEVANCE_IMPLICIT_INTEREST
				&& !IDENTIFIER_THIS.equals(proposal.getDisplayString());
	}

	static class MylarProposalSeparator extends JavaCompletionProposal {
		public MylarProposalSeparator() {
			super("", 0, 0, MylarImages.getImage(MylarImages.CONTENT_ASSIST_SEPARATOR),
					LABEL_SEPARATOR, MylarJavaProposalProcessor.THRESHOLD_INTEREST);
		}
	}
}
