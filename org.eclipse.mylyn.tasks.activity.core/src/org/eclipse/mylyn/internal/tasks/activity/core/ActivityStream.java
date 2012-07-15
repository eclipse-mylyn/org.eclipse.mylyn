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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.activity.core.ActivityEvent;
import org.eclipse.mylyn.tasks.activity.core.ActivityScope;
import org.eclipse.mylyn.tasks.activity.core.IActivityManager;
import org.eclipse.mylyn.tasks.activity.core.IActivityStream;

/**
 * @author Steffen Pingel
 */
public class ActivityStream implements IActivityStream {

	private final List<ActivityEvent> events;

	private final ActivityScope scope;

	private final ActivityManager manager;

	public ActivityStream(ActivityManager manager, ActivityScope scope) {
		Assert.isNotNull(manager);
		Assert.isNotNull(scope);
		this.manager = manager;
		this.scope = scope;
		this.events = new CopyOnWriteArrayList<ActivityEvent>();
		initialize();
	}

	private void initialize() {
		events.addAll(manager.getEvents(scope));
	}

	public void addEvent(ActivityEvent event) {
		events.add(event);
	}

	public List<ActivityEvent> getEvents() {
		return events;
	}

	public IActivityManager getManager() {
		return manager;
	}

	public ActivityScope getScope() {
		return scope;
	}

	public void removeEvent(ActivityEvent event) {
		events.remove(event);
	}

}
