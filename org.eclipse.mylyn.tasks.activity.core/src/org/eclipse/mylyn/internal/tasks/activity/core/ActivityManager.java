/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.activity.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.mylyn.tasks.activity.core.ActivityEvent;
import org.eclipse.mylyn.tasks.activity.core.ActivityScope;
import org.eclipse.mylyn.tasks.activity.core.IActivityManager;
import org.eclipse.mylyn.tasks.activity.core.IActivityStream;
import org.eclipse.mylyn.tasks.activity.core.spi.IActivitySession;

/**
 * @author Steffen Pingel
 */
public class ActivityManager implements IActivityManager, IActivitySession {

	private final Set<ActivityEvent> events;

	public ActivityManager() {
		this.events = Collections.synchronizedSet(new TreeSet<ActivityEvent>());
	}

	public Collection<ActivityEvent> getEvents(ActivityScope scope) {
		return events;
	}

	public IActivityStream getStream(ActivityScope scope) {
		return new ActivityStream(this, scope);
	}

	public IActivityManager getManger() {
		return this;
	}

	public void fireActivityEvent(ActivityEvent event) {
		events.add(event);
	}
}