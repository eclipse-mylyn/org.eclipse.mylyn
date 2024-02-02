/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class GerritOperation<T> extends Job {

	private final GerritClient client;

	private final AbstractRequest<T> request;

	private T operationResult;

	public GerritOperation(String name, GerritClient client, AbstractRequest<T> request) {
		super(name);
		this.client = client;
		this.request = request;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		SubMonitor.convert(monitor);
		try {
			execute(monitor);
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		} catch (GerritException e) {
			return new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					NLS.bind(Messages.GerritOperation_Operation_Failed, e.getMessage()), e);
		}
		return Status.OK_STATUS;
	}

	protected void execute(IProgressMonitor monitor) throws GerritException {
		operationResult = request.execute(client, monitor);
	}

	public T getOperationResult() {
		return operationResult;
	}

}
