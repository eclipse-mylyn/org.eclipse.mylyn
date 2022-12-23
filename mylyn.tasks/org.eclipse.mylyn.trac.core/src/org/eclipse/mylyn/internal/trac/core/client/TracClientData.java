/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.trac.core.model.TracComponent;
import org.eclipse.mylyn.internal.trac.core.model.TracMilestone;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;
import org.eclipse.mylyn.internal.trac.core.model.TracSeverity;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketField;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketResolution;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketStatus;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketType;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;

public class TracClientData implements Serializable {

	private static final long serialVersionUID = 6891961984245981675L;

	List<TracComponent> components;

	List<TracMilestone> milestones;

	List<TracPriority> priorities;

	List<TracSeverity> severities;

	List<TracTicketField> ticketFields;

	List<TracTicketResolution> ticketResolutions;

	List<TracTicketStatus> ticketStatus;

	List<TracTicketType> ticketTypes;

	List<TracVersion> versions;

	long lastUpdate;

	transient Map<String, TracTicketField> ticketFieldByName;

}
