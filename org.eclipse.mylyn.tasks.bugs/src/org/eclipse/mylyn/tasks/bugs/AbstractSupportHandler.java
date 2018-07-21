/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.bugs;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Base class for providing custom handling for support requests.
 * <p>
 * Clients may extend.
 * 
 * @author Steffen Pingel
 * @since 3.4
 * @see ISupportRequest
 * @see ISupportResponse
 */
public abstract class AbstractSupportHandler {

	/**
	 * @since 3.4
	 */
	public void preProcess(ISupportRequest request) {
	}

	/**
	 * @since 3.4
	 */
	public void process(ITaskContribution contribution, IProgressMonitor monitor) {
	}

	/**
	 * @since 3.4
	 */
	public void postProcess(ISupportResponse response, IProgressMonitor monitor) {
	}

}
