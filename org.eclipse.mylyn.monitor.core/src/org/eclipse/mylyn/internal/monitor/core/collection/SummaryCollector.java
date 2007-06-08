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

	public void consumeEvent(InteractionEvent event, int userId) {
		if (mostRecentDate.compareTo(event.getDate()) == -1)
			mostRecentDate = event.getDate();
		if (leastRecentDate.compareTo(event.getDate()) == 1)
			leastRecentDate = event.getDate();

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

	public List<String> getReport() {
		List<String> summaries = new ArrayList<String>();

		summaries.add("Start date: " + leastRecentDate + ", End date: " + mostRecentDate + "<br>");

		summaries.add("Number of events: " + numUserEvents + "<br>");
		summaries.add("Number of commands: " + numCommands + "<br>");
		summaries.add("Number of preference changes: " + numPreference + "<br>");
		summaries.add("Number of selections: " + numSelections + "<br>");
		return summaries;
	}

	public String getReportTitle() {
		return "Summary";
	}

	public void exportAsCSVFile(String directory) {
		// TODO Auto-generated method stub

	}

	public List<String> getPlainTextReport() {
		List<String> summaries = new ArrayList<String>();

		summaries.add("Start date: " + leastRecentDate + ", End date: " + mostRecentDate);

		summaries.add("Number of events: " + numUserEvents);
		summaries.add("Number of commands: " + numCommands);
		summaries.add("Number of preference changes: " + numPreference);
		summaries.add("Number of selections: " + numSelections);
		return summaries;
	}

}