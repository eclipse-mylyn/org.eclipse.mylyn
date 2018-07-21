/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.index.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.osgi.util.NLS;

/**
 * A content proposal provider for proposals on a task list index and task list.
 * 
 * @author David Green
 */
public class ContentProposalProvider implements IContentProposalProvider {

	private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s"); //$NON-NLS-1$

	private static final Pattern PATTERN_SPECIAL_CHARACTERS = Pattern.compile("([+&\\|!\\(\\)\\{\\}\\[\\]^\"~\\*\\?:\\\\-])"); //$NON-NLS-1$

	private final AbstractIndexReference reference;

	private final TaskList taskList;

	public ContentProposalProvider(TaskList taskList, AbstractIndexReference reference) {
		this.taskList = taskList;
		this.reference = reference;
	}

	public static class ProposalContentState {

		public String beforePrefixContent;

		public String fieldPrefix = ""; //$NON-NLS-1$

		public String prefix = ""; //$NON-NLS-1$

		public String suffix = ""; //$NON-NLS-1$

		public ProposalContentState(String contents) {
			beforePrefixContent = contents;
		}

	}

	public IContentProposal[] getProposals(String contents, int position) {
		List<IContentProposal> proposals = new ArrayList<IContentProposal>(10);

		ProposalContentState contentState = computeProposalContentState(contents, position);

		// if we have a field prefix
		if (contentState.fieldPrefix.length() > 0) {
			AbstractTaskSchema.Field indexField = computeIndexField(contentState.fieldPrefix);

			// if it's a person field then suggest
			// people from the task list
			if (indexField != null && TaskAttribute.TYPE_PERSON.equals(indexField.getType())) {
				computePersonProposals(proposals, contentState);
			}

		} else {

			// suggest field name prefixes
			for (Field field : reference.index().getIndexedFields()) {

				// searching on identifier field (task handle) is not useful
				if (field.equals(TaskListIndex.FIELD_IDENTIFIER)) {
					continue;
				}

				String indexKey = field.getIndexKey();
				if (indexKey.startsWith(contentState.prefix)) {
					String description;
					if (TaskListIndex.FIELD_CONTENT.equals(field)) {
						description = Messages.IndexSearchHandler_hint_content;
					} else if (TaskListIndex.FIELD_PERSON.equals(field)) {
						description = Messages.IndexSearchHandler_hint_person;
					} else {
						description = NLS.bind(Messages.IndexSearchHandler_hint_generic, field.getLabel());
					}
					proposals.add(new ContentProposal(contentState.beforePrefixContent + indexKey + ":", //$NON-NLS-1$
							contentState.suffix, indexKey, description));

					if (TaskAttribute.TYPE_DATE.equals(field.getType())
							|| TaskAttribute.TYPE_DATETIME.equals(field.getType())) {
						computeDateRangeProposals(proposals, contentState, field);
					}
				}
			}
		}

		return proposals.toArray(new IContentProposal[proposals.size()]);
	}

	public ProposalContentState computeProposalContentState(String contents, int position) {
		ProposalContentState contentState = new ProposalContentState(contents);

		if (position >= 0 && position <= contents.length()) {
			int i = position;
			while (i > 0 && !Character.isWhitespace(contents.charAt(i - 1)) && contents.charAt(i - 1) != ':') {
				--i;
			}
			if (i > 0 && contents.charAt(i - 1) == ':') {
				int fieldEnd = i - 1;
				int fieldStart = i - 1;
				while (fieldStart > 0 && Character.isLetter(contents.charAt(fieldStart - 1))) {
					--fieldStart;
				}
				contentState.fieldPrefix = contents.substring(fieldStart, fieldEnd);
			}

			contentState.prefix = contents.substring(i, position);
			contentState.beforePrefixContent = contents.substring(0, i);

			if (position < contents.length()) {
				i = position;
				while (i < contents.length()) {
					if (Character.isWhitespace(contents.charAt(i))) {
						break;
					}
					++i;
				}
				contentState.suffix = contents.substring(i);
			}
		}
		return contentState;
	}

	public Field computeIndexField(String fieldPrefix) {
		for (Field field : reference.index().getIndexedFields()) {
			if (field.getIndexKey().equals(fieldPrefix)) {
				return field;
			}
		}
		return null;
	}

	public void computeDateRangeProposals(List<IContentProposal> proposals, ProposalContentState contentState,
			Field field) {
		// for date fields give suggestion of date range search
		String description;
		final Date now = new Date();
		final Date dateSearchUpperBound;
		final Date dateSearchOneWeekLowerBound;
		{
			GregorianCalendar calendar = new GregorianCalendar();

			calendar.setTime(now);
			calendar.add(Calendar.DAY_OF_WEEK, 1); // one day in future due to GMT conversion in index
			dateSearchUpperBound = calendar.getTime();

			calendar.setTime(now);
			calendar.add(Calendar.DAY_OF_WEEK, -7);
			dateSearchOneWeekLowerBound = calendar.getTime();
		}

		description = NLS.bind(Messages.IndexSearchHandler_Generic_date_range_search_1_week, field.getLabel());

		String label = NLS.bind(Messages.IndexSearchHandler_Past_week_date_range_label, field.getIndexKey());

		String queryText = reference.index().computeQueryFieldDateRange(field, dateSearchOneWeekLowerBound,
				dateSearchUpperBound);

		if (queryText.startsWith(contentState.prefix)) {
			proposals.add(new ContentProposal(contentState.beforePrefixContent + queryText, contentState.suffix, label,
					description));
		}
	}

	public void computePersonProposals(List<IContentProposal> proposals, ProposalContentState contentState) {
		Set<String> addresses = new TreeSet<String>();

		Collection<AbstractTask> allTasks = taskList.getAllTasks();
		for (AbstractTask task : allTasks) {
			addAddresses(addresses, task);
		}

		for (String address : addresses) {
			if (address.startsWith(contentState.prefix)) {
				String proposalContent = address;
				proposalContent = PATTERN_SPECIAL_CHARACTERS.matcher(proposalContent).replaceAll("\\\\$1"); //$NON-NLS-1$
				if (PATTERN_WHITESPACE.matcher(proposalContent).find()) {
					proposalContent = "\"" + proposalContent + "\""; //$NON-NLS-1$//$NON-NLS-2$
				}
				proposals.add(new ContentProposal(contentState.beforePrefixContent + proposalContent,
						contentState.suffix, address, null));
			}
		}
	}

	private void addAddresses(Set<String> addresses, AbstractTask task) {
		String name = task.getOwner();
		if (name != null && name.trim().length() > 0) {
			addresses.add(name.trim());
		}
	}
}
