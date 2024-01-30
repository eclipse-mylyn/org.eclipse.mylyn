/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.feed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;

/**
 * @author Steffen Pingel
 */
public class ServiceMessageEvent extends EventObject {

	public enum Kind {
		MESSAGE_UPDATE, STOP
	}

	private static final long serialVersionUID = 1L;

	private final List<ServiceMessage> messages;

	private final Kind eventKind;

	public ServiceMessageEvent(ServiceMessageManager manager, Kind kind) {
		this(manager, kind, new ArrayList<>());
	}

	public ServiceMessageEvent(ServiceMessageManager manager, Kind eventKind, List<ServiceMessage> messages) {
		super(manager);
		this.eventKind = eventKind;
		this.messages = Collections.unmodifiableList(new ArrayList<>(messages));
	}

	public List<ServiceMessage> getMessages() {
		return messages;
	}

	public Kind getEventKind() {
		return eventKind;
	}

}
