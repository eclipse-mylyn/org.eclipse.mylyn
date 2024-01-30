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
 *     Leah Findlater - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.core.collection;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 * @author Leah Findlater
 */
public class CommandUsageCollector implements IUsageCollector {

	private final InteractionByTypeSummary commands = new InteractionByTypeSummary();

	private final Set<Integer> userIdSet = new HashSet<>();

	@Override
	public void consumeEvent(InteractionEvent event, int userId) {
		userIdSet.add(userId);
		if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
			commands.setUserCount(userId, InteractionEventUtil.getCleanOriginId(event),
					commands.getUserCount(userId, InteractionEventUtil.getCleanOriginId(event)) + 1);
		}
	}

	@Override
	public List<String> getReport() {
		return Collections.emptyList();
	}

	@Override
	public String getReportTitle() {
		return Messages.CommandUsageCollector_Command_Usage;
	}

	@Override
	public void exportAsCSVFile(String directoryName) {
		// TODO Auto-generated method stub

	}

	public InteractionByTypeSummary getCommands() {
		return commands;
	}

	@Override
	public List<String> getPlainTextReport() {
		return Collections.emptyList();
	}
}
