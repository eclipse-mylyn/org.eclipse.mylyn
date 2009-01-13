/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.sync;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class SubmitJobEvent {

	private final SubmitJob job;

	/**
	 * @since 3.0
	 */
	public SubmitJobEvent(SubmitJob job) {
		this.job = job;
	}

	/**
	 * @since 3.0
	 */
	public SubmitJob getJob() {
		return job;
	}

}
