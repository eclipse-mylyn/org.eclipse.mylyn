/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class DelegatingUsageCollector implements IUsageCollector {

	protected List<IUsageScanner> scanners = new ArrayList<>();

	public void addScanner(IUsageScanner aScanner) {
		scanners.add(aScanner);
	}

	private List<IUsageCollector> delegates = new ArrayList<>();

	private String reportTitle = ""; //$NON-NLS-1$

	public List<IUsageCollector> getDelegates() {
		return delegates;
	}

	public void setDelegates(List<IUsageCollector> delegates) {
		this.delegates = delegates;
	}

	@Override
	public void consumeEvent(InteractionEvent event, int userId) {
		for (IUsageCollector collector : delegates) {
			collector.consumeEvent(event, userId);
		}
	}

	@Override
	public List<String> getReport() {
		List<String> combinedReports = new ArrayList<>();
		for (IUsageCollector collector : delegates) {
			combinedReports.add("<h3>" + collector.getReportTitle() + "</h3>"); //$NON-NLS-1$ //$NON-NLS-2$
			combinedReports.addAll(collector.getReport());
		}
		return combinedReports;
	}

	@Override
	public void exportAsCSVFile(String directory) {

	}

	@Override
	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	@Override
	public List<String> getPlainTextReport() {
		List<String> combinedReports = new ArrayList<>();
		for (IUsageCollector collector : delegates) {
			combinedReports.add(collector.getReportTitle());
			combinedReports.addAll(collector.getPlainTextReport());
		}
		return combinedReports;
	}
}
