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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

final class TestRemoteEmfObserver<P extends EObject, T, L, C> extends RemoteEmfObserver<P, T, L, C> {

	static final int TEST_TIMEOUT = 100;

	T createdObject;

	int updated;

	int updating;

	int updatedMember;

	int responded;

	IStatus failure;

	public TestRemoteEmfObserver() {
	}

	public TestRemoteEmfObserver(RemoteEmfConsumer<P, T, L, ?, ?, C> consumer) {
		super(consumer);
	}

	@Override
	public void created(P object, T child) {
		createdObject = child;
	}

	@Override
	public void updating(P parent, T object) {
		updating++;
	}

	@Override
	public void updated(P object, T child, boolean modified) {
		responded++;
		if (modified) {
			updated++;
		}
		if (child instanceof Collection) {
			updatedMember++;
		}
	}

	@Override
	public void failed(P object, T child, IStatus status) {
		failure = status;
	}

	protected void waitForResponse(int response, int update) {
		long delay;
		delay = 0;
		while (delay < TEST_TIMEOUT) {
			if (responded < response) {
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
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		assertThat("Wrong # responses: " + responded + ", updated: " + updated, responded, is(response));
		assertThat("Wrong # updates" + updated, updated, is(update));
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