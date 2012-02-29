/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.search.AbstractSearchHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

/**
 * @author David Green
 */
public class IndexSearchHandler extends AbstractSearchHandler {

	private class ContentProposalProvider implements IContentProposalProvider {
		public IContentProposal[] getProposals(String contents, int position) {
			List<IContentProposal> proposals = new ArrayList<IContentProposal>(10);

			String fieldPrefix = ""; //$NON-NLS-1$
			String prefix = ""; //$NON-NLS-1$
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
					fieldPrefix = contents.substring(fieldStart, fieldEnd);
				}

				prefix = contents.substring(i, position);
			}

			// if we have a field prefix
			if (fieldPrefix.length() > 0) {
				AbstractTaskSchema.Field indexField = computeIndexField(fieldPrefix);

				// if it's a person field then suggest
				// people from the task list
				if (indexField != null && TaskAttribute.TYPE_PERSON.equals(indexField.getType())) {
					computePersonProposals(proposals, prefix);
				}

			} else {

				// suggest field name prefixes
				for (Field field : reference.index().getIndexedFields()) {

					// searching on URL is not useful
					if (field.equals(TaskListIndex.FIELD_IDENTIFIER)) {
						continue;
					}

					if (field.getIndexKey().startsWith(prefix)) {
						String description;
						if (TaskListIndex.FIELD_CONTENT.equals(field)) {
							description = Messages.IndexSearchHandler_hint_content;
						} else if (TaskListIndex.FIELD_PERSON.equals(field)) {
							description = Messages.IndexSearchHandler_hint_person;
						} else {
							description = NLS.bind(Messages.IndexSearchHandler_hint_generic, field.getLabel());
						}
						proposals.add(new ContentProposal(field.getIndexKey().substring(prefix.length()) + ":", //$NON-NLS-1$
								field.getIndexKey(), description));

						if (TaskAttribute.TYPE_DATE.equals(field.getType())
								|| TaskAttribute.TYPE_DATETIME.equals(field.getType())) {
							computeDateRangeProposals(proposals, field);
						}
					}
				}
			}

			return proposals.toArray(new IContentProposal[proposals.size()]);
		}

		public void computeDateRangeProposals(List<IContentProposal> proposals, Field field) {
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
			proposals.add(new ContentProposal(queryText, label, description));
		}

		public void computePersonProposals(List<IContentProposal> proposals, String prefix) {
			Set<String> addresses = new TreeSet<String>();

			Collection<AbstractTask> allTasks = TasksUiPlugin.getTaskList().getAllTasks();
			for (AbstractTask task : allTasks) {
				addAddresses(addresses, task);
			}

			for (String address : addresses) {
				if (address.startsWith(prefix)) {
					proposals.add(new ContentProposal(address.substring(prefix.length()), address, null));
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

	private IndexReference reference;

	public IndexSearchHandler() {
		reference = new IndexReference();
	}

	public Field computeIndexField(String fieldPrefix) {
		for (Field field : reference.index().getIndexedFields()) {
			if (field.getIndexKey().equals(fieldPrefix)) {
				return field;
			}
		}
		return null;
	}

	@Override
	public Composite createSearchComposite(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayoutFactory.swtDefaults().applyTo(container);

		final Button button = new Button(container, SWT.CHECK);
		button.setText(Messages.IndexSearchHandler_summaryOnly);
		button.setToolTipText(Messages.IndexSearchHandler_summaryOnly_tooltip);
		button.setSelection(true);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Field newDefaultField = button.getSelection()
						? TaskListIndex.FIELD_SUMMARY
						: TaskListIndex.FIELD_CONTENT;
				reference.index().setDefaultField(newDefaultField);
				fireFilterChanged();
			}
		});

		return container;
	}

	@Override
	public PatternFilter createFilter() {
		return new IndexedSubstringPatternFilter(reference.index());
	}

	@Override
	public void adaptTextSearchControl(Text textControl) {
		IContentProposalProvider proposalProvider = new ContentProposalProvider();
		ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(textControl, new TextContentAdapter(),
				proposalProvider, null, new char[0]);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);

		// if we decorate the control it lets the user know that they can use content assist...
		// BUT it looks pretty bad.
//		ControlDecoration controlDecoration = new ControlDecoration(textControl, (SWT.TOP | SWT.LEFT));
//		controlDecoration.setShowOnlyOnFocus(true);
//		FieldDecoration contentProposalImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
//				FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
//		controlDecoration.setImage(contentProposalImage.getImage());
	}

	@Override
	public void dispose() {
		reference.dispose();
		reference = null;
	}

}
