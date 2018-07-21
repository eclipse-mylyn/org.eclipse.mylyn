/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.LabelsAttributeEditor;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

public class OptionsProposalProvider implements IContentProposalProvider {

	private static final String VALUE_SEPARATOR = ","; //$NON-NLS-1$

	private final Set<String> proposals;

	private final boolean isMultiSelect;

	public OptionsProposalProvider(Map<String, String> proposals, boolean isMultiSelect) {
		this.proposals = proposals.keySet();
		this.isMultiSelect = isMultiSelect;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		Set<String> filteredProposals = new HashSet<>(proposals);
		filteredProposals.remove(""); //$NON-NLS-1$
		String lastValue = ""; //$NON-NLS-1$
		// If the attribute is of type multi-select, filter the past values from the proposals
		if (isMultiSelect) {
			String[] contentsArray = contents.split(VALUE_SEPARATOR, -1);
			if (contentsArray.length > 0) {
				List<String> trimmedContents = LabelsAttributeEditor.getTrimmedValues(contentsArray);
				filteredProposals.removeAll(trimmedContents);
				lastValue = contentsArray[contentsArray.length - 1].trim();
			}
		} else {
			lastValue = contents;
		}

		// If there is a last value, then filter the remaining the proposals to contain it
		if (!lastValue.isEmpty()) {
			for (Iterator<String> iterator = filteredProposals.iterator(); iterator.hasNext();) {
				String proposal = iterator.next();
				if (!proposal.toLowerCase().contains(lastValue.toLowerCase())) {
					iterator.remove();
				}

			}
		}
		// Since the contents of the editor is replaced, we need to include the existing values in the replacement
		final String existingValues = contents.substring(0, contents.length() - lastValue.length());
		ImmutableList<String> sortedProposals = FluentIterable.from(filteredProposals).toSortedList(
				Ordering.from(String.CASE_INSENSITIVE_ORDER));
		return FluentIterable.from(sortedProposals).transform(new Function<String, IContentProposal>() {
			public IContentProposal apply(String proposal) {
				return new ContentProposal(existingValues + proposal, proposal, null);
			}
		}).toArray(IContentProposal.class);
	}

	public boolean isMultiSelect() {
		return isMultiSelect;
	}
}
