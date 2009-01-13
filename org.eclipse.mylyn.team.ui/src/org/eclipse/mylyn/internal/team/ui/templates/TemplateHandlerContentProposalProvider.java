/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eike Stepper - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;

/**
 * @author Eike Stepper
 */
public class TemplateHandlerContentProposalProvider implements IContentProposalProvider {
	public IContentProposal[] getProposals(String contents, int position) {
		ProposalComputer proposalComputer = new ProposalComputer(contents, position);
		return proposalComputer.computeProposals();
	}

	/**
	 * @author Eike Stepper
	 */
	protected static class ProposalComputer {
		private final String contents;

		private final int position;

		private final List<IContentProposal> result = new ArrayList<IContentProposal>();

		private String[] keywords;

		private String prefix;

		public ProposalComputer(String contents, int position) {
			this.contents = contents;
			this.position = position;
			initKeywords();
			initPrefix();
		}

		public IContentProposal[] computeProposals() {
			for (String keyword : keywords) {
				String proposal = getMatch(keyword);
				if (proposal != null) {
					addProposal(proposal, keyword);
				}
			}

			return result.toArray(new IContentProposal[result.size()]);
		}

		private void initKeywords() {
			keywords = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager().getRecognizedKeywords();
		}

		private void initPrefix() {
			prefix = ""; //$NON-NLS-1$
			String beforePosition = contents.substring(0, position);
			if (beforePosition.endsWith("$")) { //$NON-NLS-1$
				prefix = "$"; //$NON-NLS-1$
			} else {
				int start = beforePosition.lastIndexOf("${"); //$NON-NLS-1$
				if (start >= 0) {
					int end = contents.indexOf('}', start);
					if (end >= position) {
						prefix = contents.substring(start, position);
					}
				}
			}
		}

		private String getMatch(String keyword) {
			String wholeProposal = "${" + keyword + "}"; //$NON-NLS-1$ //$NON-NLS-2$
			if (wholeProposal.startsWith(prefix)) {
				return wholeProposal.substring(prefix.length());
			}

			return null;
		}

		private void addProposal(String proposal, String keyword) {
			String description = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager().getHandlerDescription(
					keyword);
			result.add(new Proposal(proposal, keyword, description));
		}

		/**
		 * @author Eike Stepper
		 */
		private static final class Proposal implements IContentProposal {
			private final String proposal;

			private final String keyword;

			private final String description;

			private Proposal(String proposal, String keyword, String description) {
				this.proposal = proposal;
				this.keyword = keyword;
				this.description = description;
			}

			public String getContent() {
				return proposal;
			}

			public int getCursorPosition() {
				return proposal.length();
			}

			public String getDescription() {
				return description;
			}

			public String getLabel() {
				return "${" + keyword + "}"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
}
