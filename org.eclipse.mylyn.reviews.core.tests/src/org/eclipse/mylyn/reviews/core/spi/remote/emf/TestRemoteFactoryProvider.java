/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;

class TestRemoteFactoryProvider<R extends EObject, C> extends AbstractRemoteEmfFactoryProvider<R, C> {

	public TestRemoteFactoryProvider() {
		setService(new JobRemoteService());
	}

	@Override
	public R open() {
		// ignore
		return null;
	}

	@Override
	public C open(Object id) {
		// ignore
		return null;
	}

	@Override
	public void close() {
		// ignore

	}

	@Override
	public void close(EObject child) {
		// ignore

	}

	@Override
	public void save() {
		// ignore

	}

	@Override
	public void save(EObject child) {
		// ignore

	}
}