/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
	};

	private static final long serialVersionUID = 1L;

	private final List<ServiceMessage> messages;

	private final Kind eventKind;

	public ServiceMessageEvent(ServiceMessageManager manager, Kind kind) {
		this(manager, kind, new ArrayList<ServiceMessage>());
	}

	public ServiceMessageEvent(ServiceMessageManager manager, Kind eventKind, List<ServiceMessage> messages) {
		super(manager);
		this.eventKind = eventKind;
		this.messages = Collections.unmodifiableList(new ArrayList<ServiceMessage>(messages));
	}

	public List<ServiceMessage> getMessages() {
		return messages;
	}

	public Kind getEventKind() {
		return eventKind;
	}

}
