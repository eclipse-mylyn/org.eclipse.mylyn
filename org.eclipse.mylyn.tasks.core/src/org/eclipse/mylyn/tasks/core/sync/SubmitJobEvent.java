/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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
