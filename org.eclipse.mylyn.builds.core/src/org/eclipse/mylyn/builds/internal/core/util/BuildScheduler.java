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

package org.eclipse.mylyn.builds.internal.core.util;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.builds.internal.core.operations.BuildJob;

/**
 * @author Steffen Pingel
 */
public class BuildScheduler {

	public BuildScheduler() {
	}

	public void schedule(Job job) {
		schedule(job, 0L);
	}

	public void schedule(Job job, long interval) {
		job.schedule(interval);
	}

	public void schedule(List<BuildJob> jobs) {
		for (BuildJob job : jobs) {
			schedule(job);
		}
	}

}
