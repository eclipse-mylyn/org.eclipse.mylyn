/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.sync;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class SubmitJobEvent {

	private final SubmitJob job;

	public SubmitJobEvent(SubmitJob job) {
		this.job = job;
	}

	public SubmitJob getJob() {
		return job;
	}

}
