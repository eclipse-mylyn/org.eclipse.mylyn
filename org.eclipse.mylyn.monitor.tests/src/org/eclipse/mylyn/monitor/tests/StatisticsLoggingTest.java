/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
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
import java.util.List;

import org.eclipse.mylar.core.tests.ContextTest;
import org.eclipse.mylar.monitor.InteractionEventLogger;
import org.eclipse.mylar.monitor.reports.InteractionEventSummary;
import org.eclipse.mylar.monitor.reports.ReportGenerator;

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
		logger.start();
//    	List<IStatsCollector> collectors = new ArrayList<IStatsCollector>();
//		collectors.add(new SummaryCollector());
		report = new ReportGenerator(logger, false);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		logFile.delete();
	}

	public void testFileReading() {
		logger.interactionObserved(mockSelection());
		logger.interactionObserved(mockSelection());
		logger.stop();
		
		List<InteractionEventSummary> summary = report.getStatisticsFromInteractionHistory(logFile).getSingleSummaries();
		assertEquals(1, summary.size());
		InteractionEventSummary first = (InteractionEventSummary)summary.get(0);
		assertEquals(2, first.getUsageCount());
	}
}
