/*******************************************************************************
 * Copyright (c) 2004, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.core.collection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class DataOverviewCollector implements IUsageCollector {

	private static long FIVEMININMS = 5 * 60 * 1000;

	private final Map<Integer, Integer> interactionHistorySizes = new HashMap<>();

	private final Map<Integer, List<Date>> interactionHistoryRanges = new HashMap<>();

	private final Map<Integer, Long> interactionHistoryActiveDuration = new HashMap<>();

	// For calculating active milliseconds
	private int currentUser = -1;

	private InteractionEvent lastUserEvent;

	private static int startDatePosition = 0;

	private static int endDatePosition = 1;

	private String filePrefix = ""; //$NON-NLS-1$

	public DataOverviewCollector(String prefix) {
		filePrefix = prefix;
	}

	@Override
	public String getReportTitle() {
		return Messages.DataOverviewCollector_Data_Overview;
	}

	@Override
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
			dateRange = new ArrayList<>(2);
			interactionHistoryRanges.put(userId, dateRange);
		}
		dateRange = interactionHistoryRanges.get(userId);
		if (dateRange.size() == 0 || dateRange.size() == 1) {
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
				interactionHistoryActiveDuration.put(userId, (long) 0);
			}
			interactionHistoryActiveDuration.put(userId, interactionHistoryActiveDuration.get(userId) + elapsed);
		}
		lastUserEvent = event;

	}

	@Override
	public List<String> getReport() {
		List<String> report = new ArrayList<>();
		report.add(Messages.DataOverviewCollector__h4_Data_Overview_h4_);
		report.add(Messages.DataOverviewCollector_Number_of_Users_ + interactionHistorySizes.size() + "<br>"); //$NON-NLS-1$
		for (Map.Entry<Integer, Integer> entry : interactionHistorySizes.entrySet()) {
			report.add(entry.getKey() + ": " + entry.getValue() + Messages.DataOverviewCollector_events); //$NON-NLS-1$
			report.add(InteractionEventClassifier.formatDuration(interactionHistoryActiveDuration.get(entry.getKey()))
					+ Messages.DataOverviewCollector_active_use);
			List<Date> dateRange = interactionHistoryRanges.get(entry.getKey());
			long duration = dateRange.get(endDatePosition).getTime() - dateRange.get(startDatePosition).getTime();
			report.add(MessageFormat.format(Messages.DataOverviewCollector_TO_PERIOD_OF_HOURS,
					dateRange.get(startDatePosition), dateRange.get(endDatePosition),
					InteractionEventClassifier.formatDuration(duration)));

			report.add("<br><br>"); //$NON-NLS-1$
		}
		return report;
	}

	@Override
	public void exportAsCSVFile(String directory) {

		String filename = directory + File.separator + filePrefix + "baseLine.csv"; //$NON-NLS-1$

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)))) {
			// Write the header
			writer.write(Messages.DataOverviewCollector_CSV_USER);
			writer.write(","); //$NON-NLS-1$
			writer.write(Messages.DataOverviewCollector_CSV_EVENTS);
			writer.write(","); //$NON-NLS-1$
			writer.write(Messages.DataOverviewCollector_CSV_START);
			writer.write(","); //$NON-NLS-1$
			writer.write(Messages.DataOverviewCollector_CSV_END);
			writer.write(","); //$NON-NLS-1$
			writer.write(Messages.DataOverviewCollector_CSV_ACTIVE_USE);
			writer.write(","); //$NON-NLS-1$
			writer.write(Messages.DataOverviewCollector_CSV_ELAPSED_USE);
			writer.newLine();

			// Writer the rows
			for (Map.Entry<Integer, Integer> entry : interactionHistorySizes.entrySet()) {
				writer.write(entry.getKey().toString());
				writer.write(","); //$NON-NLS-1$
				writer.write(entry.getValue().toString());
				writer.write(","); //$NON-NLS-1$
				List<Date> dateRange = interactionHistoryRanges.get(entry.getKey());
				writer.write(dateRange.get(startDatePosition).toString());
				writer.write(","); //$NON-NLS-1$
				writer.write(dateRange.get(endDatePosition).toString());
				writer.write(","); //$NON-NLS-1$
				long elapsed = interactionHistoryActiveDuration.get(entry.getKey());
				writer.write(InteractionEventClassifier.formatDuration(elapsed));
				writer.write(","); //$NON-NLS-1$
				long duration = dateRange.get(endDatePosition).getTime() - dateRange.get(startDatePosition).getTime();
				writer.write(InteractionEventClassifier.formatDuration(duration));
				writer.newLine();
			}

			writer.flush();
			writer.close();

		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, "org.eclipse.mylyn.monitor.core", "Unable to write CVS file <" //$NON-NLS-1$//$NON-NLS-2$
					+ filename + ">", e)); //$NON-NLS-1$
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
			return dateRange.get(endDatePosition).getTime() - dateRange.get(startDatePosition).getTime();
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

	@Override
	public List<String> getPlainTextReport() {
		List<String> report = new ArrayList<>();
		report.add(Messages.DataOverviewCollector_Data_Overview);
		report.add(Messages.DataOverviewCollector_Number_of_Users_ + interactionHistorySizes.size());
		for (Map.Entry<Integer, Integer> entry : interactionHistorySizes.entrySet()) {
			report.add(entry.getKey() + ": " + entry.getValue() + Messages.DataOverviewCollector_events); //$NON-NLS-1$
			report.add(InteractionEventClassifier.formatDuration(interactionHistoryActiveDuration.get(entry.getKey()))
					+ Messages.DataOverviewCollector_active_use);
			List<Date> dateRange = interactionHistoryRanges.get(entry.getKey());
			long duration = dateRange.get(endDatePosition).getTime() - dateRange.get(startDatePosition).getTime();
			report.add(MessageFormat.format(Messages.DataOverviewCollector_TO_PERIOD_OF_HOURS,
					dateRange.get(startDatePosition), dateRange.get(endDatePosition),
					InteractionEventClassifier.formatDuration(duration)));

		}
		return report;
	}

}
