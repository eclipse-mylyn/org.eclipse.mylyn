/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.core.collection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class SummaryCollector implements IUsageCollector {

	protected int numSelections = 0;

	protected int numEdits = 0;

	protected int numUserEvents = 0;

	protected Date leastRecentDate = new Date();

	protected Date mostRecentDate = new Date(0);

	protected int numCommands = 0;

	protected int numPreference = 0;

	@Override
	public void consumeEvent(InteractionEvent event, int userId) {
		if (mostRecentDate.compareTo(event.getDate()) == -1) {
			mostRecentDate = event.getDate();
		}
		if (leastRecentDate.compareTo(event.getDate()) == 1) {
			leastRecentDate = event.getDate();
		}

		if (event.getKind().equals(InteractionEvent.Kind.SELECTION)) {
			numSelections++;
		} else if (event.getKind().equals(InteractionEvent.Kind.EDIT)) {
			numEdits++;
		} else if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
			numCommands++;
		} else if (event.getKind().equals(InteractionEvent.Kind.PREFERENCE)) {
			numPreference++;
		}
		numUserEvents++;
	}

	@Override
	public List<String> getReport() {
		List<String> summaries = new ArrayList<>();

		summaries.add(Messages.SummaryCollector_Start_date_ + leastRecentDate + Messages.SummaryCollector_END_DATE
				+ mostRecentDate + "<br>"); //$NON-NLS-1$

		summaries.add(Messages.SummaryCollector_Number_of_events_ + numUserEvents + "<br>"); //$NON-NLS-1$
		summaries.add(Messages.SummaryCollector_Number_of_commands_ + numCommands + "<br>"); //$NON-NLS-1$
		summaries.add(Messages.SummaryCollector_Number_of_preference_changes + numPreference + "<br>"); //$NON-NLS-1$
		summaries.add(Messages.SummaryCollector_Number_of_selections_ + numSelections + "<br>"); //$NON-NLS-1$
		return summaries;
	}

	@Override
	public String getReportTitle() {
		return Messages.SummaryCollector_Summary;
	}

	@Override
	public void exportAsCSVFile(String directory) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getPlainTextReport() {
		List<String> summaries = new ArrayList<>();

		summaries.add(Messages.SummaryCollector_Start_date_ + leastRecentDate + Messages.SummaryCollector_END_DATE
				+ mostRecentDate);

		summaries.add(Messages.SummaryCollector_Number_of_events_ + numUserEvents);
		summaries.add(Messages.SummaryCollector_Number_of_commands_ + numCommands);
		summaries.add(Messages.SummaryCollector_Number_of_preference_changes + numPreference);
		summaries.add(Messages.SummaryCollector_Number_of_selections_ + numSelections);
		return summaries;
	}

}
