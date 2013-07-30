/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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