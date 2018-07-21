/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.operations;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Steffen Pingel
 */
public class OperationChangeEvent {

	private final AbstractOperation operation;

	private IStatus status = Status.OK_STATUS;

	public OperationChangeEvent(AbstractOperation operation) {
		this.operation = operation;
	}

	public AbstractOperation getOperation() {
		return operation;
	}

	public IStatus getStatus() {
		return status;
	}

	void setStatus(IStatus status) {
		Assert.isNotNull(status);
		this.status = status;
	}

}
