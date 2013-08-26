/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote;

import org.eclipse.core.runtime.Assert;

/**
 * Support generic implementations of a set of remote API factories. In the base case, this just encapsulates a service.
 * 
 * @author Miles Parker
 */
public abstract class AbstractRemoteFactoryProvider {

	private AbstractRemoteService service;

	private AbstractDataLocator dataLocator;

	public void modelExec(Runnable runnable, boolean block) {
		Assert.isLegal(service != null, "Internal Error: Connector must supply a service for execution."); //$NON-NLS-1$
		service.modelExec(runnable, block);
	}

	public AbstractRemoteService getService() {
		return service;
	}

	public void setService(AbstractRemoteService service) {
		this.service = service;
	}

	public void setDataLocator(AbstractDataLocator dataLocator) {
		Assert.isLegal(dataLocator != null);
		this.dataLocator = dataLocator;
		this.dataLocator.migrate();
	}

	public AbstractDataLocator getDataLocator() {
		return dataLocator;
	}
}
