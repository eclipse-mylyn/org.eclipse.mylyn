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
/*
 * Created on Mar 14, 2005
 */
package org.eclipse.mylar.internal.java.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProcessor;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.ui.MylarImages;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class MylarJavaCompletionProcessor extends JavaCompletionProcessor {

	private static final String LABEL_SEPARATOR = " -----------------------------------";

	private static final String IDENTIFIER_THIS = "this";
	
	/**
	 * TODO: parametrize this based on JDT heuristics
	 */
	private static final int IMPLICIT_INTEREST_RELEVANCE = 300;

	public MylarJavaCompletionProcessor(IEditorPart editor, ContentAssistant assistant, String partition) {
		super(editor, assistant, partition);
	}

	@Override
	protected List filterAndSortProposals(List proposals, IProgressMonitor monitor,
			ContentAssistInvocationContext context) {
		super.filterAndSortProposals(proposals, monitor, context);
		if (!MylarPlugin.getContextManager().isContextActive())
			return proposals;
		try {
			TreeMap<Float, ICompletionProposal> interesting = new TreeMap<Float, ICompletionProposal>();
			List<ICompletionProposal> rest = new ArrayList<ICompletionProposal>();
			int unresolvedProposals = 0;
			for (Object proposalObject : proposals) {
				ICompletionProposal proposal = (ICompletionProposal) proposalObject;
				IJavaElement element = null;
				boolean isImplicitlyInteresting = false;
				if (proposal instanceof AbstractJavaCompletionProposal) {
					AbstractJavaCompletionProposal javaProposal = (AbstractJavaCompletionProposal)proposal;
					element = javaProposal.getJavaElement();
					isImplicitlyInteresting = isImplicitlyInteresting(javaProposal);
				}
				if (element != null) { 
					IMylarElement node = MylarPlugin.getContextManager().getElement(element.getHandleIdentifier());
					if (node != null) {
						float interest = node.getInterest().getValue();
						if (interest > MylarContextManager.getScalingFactors().getInteresting()) {
							// negative to invert sorting order
							interesting.put(-interest, proposal);
						} else {
							rest.add(proposal);
						}
					} else {
						rest.add(proposal);
					}
				} else if (isImplicitlyInteresting) {
					unresolvedProposals++;
					// HACK: parametrize
					interesting.put((float) unresolvedProposals - 1000000, proposal);
				} else {
					rest.add(proposal);
				}
			}
			if (interesting.keySet().size() == 0) {
				return proposals;
			} else {
				ICompletionProposal[] sorted = new ICompletionProposal[interesting.keySet().size() + rest.size() + 1];
				int i = 0;
				for (Entry<Float, ICompletionProposal> entry : interesting.entrySet()) {
					sorted[i] = entry.getValue();
					i++;
				}
				if (interesting.keySet().size() > 0) {
					int replacementOffset = -1;
					if (sorted[i - 1] instanceof JavaCompletionProposal) {
						replacementOffset = ((JavaCompletionProposal) sorted[i - 1]).getReplacementOffset();
					} else if (sorted[i - 1] instanceof LazyJavaCompletionProposal) {
						replacementOffset = ((LazyJavaCompletionProposal) sorted[i - 1]).getReplacementOffset();
					} else {
						MylarStatusHandler.log("Could not create proposal separator for class: "
								+ sorted[i - 1].getClass(), this);
					}
					sorted[i] = new JavaCompletionProposal("", replacementOffset, 0, MylarImages
							.getImage(MylarImages.CONTENT_ASSIST_SEPARATOR), LABEL_SEPARATOR, 0);
					i++;
				}
				for (ICompletionProposal proposal : rest) {
					sorted[i] = proposal;
					i++;
				}
				return Arrays.asList(sorted);
			}
		} catch (Throwable t) {
			MylarStatusHandler.log(t, "completion proposal filter and sort failed");
		}
		return null;
	}

	/**
	 * TODO: separate relevance from interest
	 */
	private boolean isImplicitlyInteresting(AbstractJavaCompletionProposal proposal) {
		return proposal.getRelevance() > IMPLICIT_INTEREST_RELEVANCE && !IDENTIFIER_THIS.equals(proposal.getDisplayString());
	}
}
