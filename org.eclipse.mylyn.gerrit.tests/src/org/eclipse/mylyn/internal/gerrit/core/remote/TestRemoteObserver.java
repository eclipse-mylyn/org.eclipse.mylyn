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

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.IRemoteEmfObserver;

final class TestRemoteObserver<P extends EObject, T> implements IRemoteEmfObserver<P, T> {

	static final int TEST_TIMEOUT = 7500;

	T createdObject;

	int updated;

	int responded;

	IStatus failure;

	AbstractRemoteEmfFactory<?, ?, ?, ?, ?> factory;

	public TestRemoteObserver(AbstractRemoteEmfFactory<?, ?, ?, ?, ?> factory) {
		this.factory = factory;
	}

	public void created(P object, T child) {
		createdObject = child;
	}

	public void updating(P parent, T object) {
	}

	public void updated(P object, T child, boolean modified) {
		responded++;
		if (modified) {
			updated++;
		}
	}

	public void failed(P object, T child, IStatus status) {
		failure = status;
		throw new RuntimeException(failure.getException());
	}

	protected void waitForResponse(int response, int update) {
		long delay;
		delay = 0;
		while (delay < TEST_TIMEOUT) {
			if (responded < response || updated < update) {
				try {
					Thread.sleep(10);
					delay += 10;
				} catch (InterruptedException e) {
				}
			} else {
				break;
			}
		}
		try {
			//wait extra to ensure there aren't remaining jobs
			Thread.sleep(25);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		assertThat("Wrong # responses", responded, is(response));
		assertThat("Wrong # updates", updated, is(update));
		if (factory != null) {
			assertThat(factory.getService().isActive(), is(false));
		}
	}

	protected void waitForFailure() {
		long delay = 0;
		while (delay < TEST_TIMEOUT) {
			if (failure == null) {
				try {
					Thread.sleep(10);
					delay += 10;
				} catch (InterruptedException e) {
				}
			} else {
				break;
			}
		}
	}

	void clear() {
		createdObject = null;
		updated = 0;
		failure = null;
	}
}