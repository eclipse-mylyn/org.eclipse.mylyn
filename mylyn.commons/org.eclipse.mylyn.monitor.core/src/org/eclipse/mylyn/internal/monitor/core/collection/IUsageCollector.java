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
 *     Leah Findlater - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.core.collection;

import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 * @author Leah Findlater
 */
public interface IUsageCollector {

	String getReportTitle();

	void consumeEvent(InteractionEvent event, int userId);

	/**
	 * TODO: return report as HTML
	 * 
	 * @return a list corresponding to all of the lines of the report
	 */
	List<String> getReport();

	/**
	 * return report as plain text
	 * 
	 * @return a list corresponding to all of the lines of the report
	 */
	List<String> getPlainTextReport();

	/**
	 * Implementors will need to generate a unique filename given the directory in which to place the file
	 * 
	 * @param directory
	 */
	void exportAsCSVFile(String directory);
}
