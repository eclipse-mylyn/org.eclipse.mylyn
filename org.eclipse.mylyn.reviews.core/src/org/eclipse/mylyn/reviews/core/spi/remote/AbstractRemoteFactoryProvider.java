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

/**
 * Support generic implementations of a set of remote API factories. In the base case, this just encapsulates a service.
 * 
 * @author Miles Parker
 */
public abstract class AbstractRemoteFactoryProvider {

	private final AbstractRemoteService service;

	public AbstractRemoteFactoryProvider(JobRemoteService service) {
		this.service = service;
	}

	public AbstractRemoteService getService() {
		return service;
	}

	public void dispose() {
		service.dispose();
	}
}
