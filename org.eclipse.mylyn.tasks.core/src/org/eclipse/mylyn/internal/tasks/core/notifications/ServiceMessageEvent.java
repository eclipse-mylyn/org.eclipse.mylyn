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

package org.eclipse.mylyn.internal.tasks.core.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

/**
 * @author Steffen Pingel
 */
public class ServiceMessageEvent extends EventObject {

	public enum EVENT_KIND {
		MESSAGE_UPDATE, STOP
	};

	private static final long serialVersionUID = 1L;

	private final List<ServiceMessage> messages;

	private final EVENT_KIND eventKind;

	public ServiceMessageEvent(ServiceMessageManager manager, EVENT_KIND kind) {
		this(manager, kind, new ArrayList<ServiceMessage>());
	}

	public ServiceMessageEvent(ServiceMessageManager manager, EVENT_KIND eventKind, List<ServiceMessage> messages) {
		super(manager);
		this.eventKind = eventKind;
		this.messages = Collections.unmodifiableList(new ArrayList<ServiceMessage>(messages));
	}

	public List<ServiceMessage> getMessages() {
		return messages;
	}

	public EVENT_KIND getEventKind() {
		return eventKind;
	}

}
