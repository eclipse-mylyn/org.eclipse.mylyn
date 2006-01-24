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

package org.eclipse.mylar.monitor.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.core.tests.ContextTest;
import org.eclipse.mylar.internal.monitor.InteractionEventLogger;
import org.eclipse.mylar.internal.monitor.reports.InteractionEventSummary;
import org.eclipse.mylar.internal.monitor.reports.ReportGenerator;
import org.eclipse.mylar.internal.monitor.reports.collectors.SummaryCollector;
import org.eclipse.mylar.monitor.reports.IUsageCollector;

/**
 * @author Mik Kersten
 */
public class StatisticsLoggingTest extends ContextTest {

	private File logFile;

	private InteractionEventLogger logger;

	private ReportGenerator report;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		logFile = new File("test-log.xml");
		logFile.delete();
		logger = new InteractionEventLogger(logFile);
		logger.startObserving();
		List<IUsageCollector> collectors = new ArrayList<IUsageCollector>();
		collectors.add(new SummaryCollector());
		report = new ReportGenerator(logger, collectors);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		logFile.delete();
	}

	public void testFileReading() {
		logger.interactionObserved(mockSelection());
		mockUserDelay();
		logger.interactionObserved(mockSelection());
		logger.stopObserving();

		List<InteractionEventSummary> summary = report.getStatisticsFromInteractionHistory(logFile)
				.getSingleSummaries();
		assertEquals(1, summary.size());
		InteractionEventSummary first = (InteractionEventSummary) summary.get(0);
		assertEquals(2, first.getUsageCount());
	}

	/**
	 * Delay enough to make replicated events different
	 */
	private void mockUserDelay() {
		// TODO: Refactor into mylar.core.tests
		try {
			Thread.sleep(100);
		} catch (InterruptedException ie) {
			;
		}
	}

}
