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

package org.eclipse.mylyn.internal.provisional.tasks.bugs;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 * @since 3.2
 */
public class AbstractSupportHandler {

	/**
	 * @since 3.2
	 */
	public void preProcess(ISupportRequest request) {
	}

	/**
	 * @since 3.2
	 */
	public void process(ITaskContribution contribution, IProgressMonitor monitor) {
	}

	/**
	 * @since 3.2
	 */
	public void postProcess(ISupportResponse response, IProgressMonitor monitor) {
	}

}
