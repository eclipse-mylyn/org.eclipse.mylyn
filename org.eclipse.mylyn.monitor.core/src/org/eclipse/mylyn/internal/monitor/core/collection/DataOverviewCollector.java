/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.monitor.core.collection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

public class DataOverviewCollector implements IUsageCollector {

	private static long FIVEMININMS = 5 * 60 * 1000;

	private Map<Integer, Integer> interactionHistorySizes = new HashMap<Integer, Integer>();

	private Map<Integer, List<Date>> interactionHistoryRanges = new HashMap<Integer, List<Date>>();

	private Map<Integer, Long> interactionHistoryActiveDuration = new HashMap<Integer, Long>();

	// For calculating active milliseconds
	private int currentUser = -1;

	private InteractionEvent lastUserEvent;

	private static int startDatePosition = 0;

	private static int endDatePosition = 1;

	private String filePrefix = "";

	public DataOverviewCollector(String prefix) {
		filePrefix = prefix;
	}

	public String getReportTitle() {
		return "Data Overview";
	}

	public void consumeEvent(InteractionEvent event, int userId) {

		// Add to size of history
		if (!interactionHistorySizes.containsKey(userId)) {
			interactionHistorySizes.put(userId, 0);
		}
		interactionHistorySizes.put(userId, interactionHistorySizes.get(userId) + 1);

		// Record start and end date of history
		List<Date> dateRange;
		if (!interactionHistoryRanges.containsKey(userId)) {
			// There are two positions in the array: start and end date
			dateRange = new ArrayList<Date>(2);
			interactionHistoryRanges.put(userId, dateRange);
		}
		dateRange = interactionHistoryRanges.get(userId);
		if (dateRange.size() == 0) {
			dateRange.add(event.getDate());
		} else if (dateRange.size() == 1) {
			dateRange.add(event.getDate());
		} else {
			dateRange.set(endDatePosition, event.getDate());
		}

		// Accumulate active duration -- assumes see all of events of a user in
		// order
		if (currentUser == -1 || currentUser != userId) {
			lastUserEvent = event;
			currentUser = userId;
		}
		// Restart accumulation if greater than 5 min has elapsed between events
		long elapsed = event.getDate().getTime() - lastUserEvent.getDate().getTime();
		if (elapsed < FIVEMININMS) {
			if (!interactionHistoryActiveDuration.containsKey(userId)) {
				interactionHistoryActiveDuration.put(userId, new Long(0));
			}
			interactionHistoryActiveDuration.put(userId, interactionHistoryActiveDuration.get(userId) + elapsed);
		}
		lastUserEvent = event;

	}

	public List<String> getReport() {
		List<String> report = new ArrayList<String>();
		report.add("<h4>Data Overview</h4>");
		report.add("Number of Users: " + interactionHistorySizes.keySet().size() + "<br>");
		for (Map.Entry<Integer, Integer> entry : interactionHistorySizes.entrySet()) {
			report.add(entry.getKey() + ": " + entry.getValue() + " events; ");
			report.add(InteractionEventClassifier.formatDuration(interactionHistoryActiveDuration.get(entry.getKey()))
					+ " active use; ");
			List<Date> dateRange = interactionHistoryRanges.get(entry.getKey());
			long duration = dateRange.get(endDatePosition).getTime() - dateRange.get(startDatePosition).getTime();
			report.add(dateRange.get(startDatePosition) + " to " + dateRange.get(endDatePosition) + ", a period of "
					+ InteractionEventClassifier.formatDuration(duration) + " hours.");

			report.add("<br><br>");
		}
		return report;
	}

	public void exportAsCSVFile(String directory) {

		String filename = directory + File.separator + filePrefix + "baseLine.csv";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));

			// Write the header
			writer.write("User");
			writer.write(",");
			writer.write("Events");
			writer.write(",");
			writer.write("Start");
			writer.write(",");
			writer.write("End");
			writer.write(",");
			writer.write("Active Use");
			writer.write(",");
			writer.write("Elapsed Use");
			writer.newLine();

			// Writer the rows
			for (Map.Entry<Integer, Integer> entry : interactionHistorySizes.entrySet()) {
				writer.write(entry.getKey().toString());
				writer.write(",");
				writer.write(entry.getValue().toString());
				writer.write(",");
				List<Date> dateRange = interactionHistoryRanges.get(entry.getKey());
				writer.write(dateRange.get(startDatePosition).toString());
				writer.write(",");
				writer.write(dateRange.get(endDatePosition).toString());
				writer.write(",");
				long elapsed = interactionHistoryActiveDuration.get(entry.getKey());
				writer.write(InteractionEventClassifier.formatDuration(elapsed));
				writer.write(",");
				long duration = dateRange.get(endDatePosition).getTime() - dateRange.get(startDatePosition).getTime();
				writer.write(InteractionEventClassifier.formatDuration(duration));
				writer.newLine();
			}

			writer.flush();
			writer.close();

		} catch (IOException e) {
			System.err.println("Unable to write CVS file <" + filename + ">");
			e.printStackTrace(System.err);
		}
	}

	/**
	 * For testing - return active use of a user
	 */
	public long getActiveUseOfUser(int userid) {
		if (interactionHistoryActiveDuration.containsKey(userid)) {
			return interactionHistoryActiveDuration.get(userid);
		}
		return -1;
	}

	/**
	 * For testing - return number of users
	 */
	public int getNumberOfUsers() {
		return interactionHistorySizes.size();
	}

	/**
	 * For testing - return duration of use
	 */
	public long getDurationUseOfUser(int userid) {
		if (interactionHistoryRanges.containsKey(userid)) {
			List<Date> dateRange = interactionHistoryRanges.get(userid);
			return (dateRange.get(endDatePosition).getTime() - dateRange.get(startDatePosition).getTime());
		}
		return -1;
	}

	/**
	 * For testing - return size of interaction history
	 */
	public int getSizeOfHistory(int userid) {
		if (interactionHistorySizes.containsKey(userid)) {
			return interactionHistorySizes.get(userid);
		}
		return -1;
	}

	public List<String> getPlainTextReport() {
		List<String> report = new ArrayList<String>();
		report.add("Data Overview");
		report.add("Number of Users: " + interactionHistorySizes.keySet().size());
		for (Map.Entry<Integer, Integer> entry : interactionHistorySizes.entrySet()) {
			report.add(entry.getKey() + ": " + entry.getValue() + " events; ");
			report.add(InteractionEventClassifier.formatDuration(interactionHistoryActiveDuration.get(entry.getKey()))
					+ " active use; ");
			List<Date> dateRange = interactionHistoryRanges.get(entry.getKey());
			long duration = dateRange.get(endDatePosition).getTime() - dateRange.get(startDatePosition).getTime();
			report.add(dateRange.get(startDatePosition) + " to " + dateRange.get(endDatePosition) + ", a period of "
					+ InteractionEventClassifier.formatDuration(duration) + " hours.");

		}
		return report;
	}

}
