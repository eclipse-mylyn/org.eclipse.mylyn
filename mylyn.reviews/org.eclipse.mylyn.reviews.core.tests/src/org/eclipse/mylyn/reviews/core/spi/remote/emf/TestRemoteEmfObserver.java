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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

@SuppressWarnings("nls")
final class TestRemoteEmfObserver<P extends EObject, T, L, C> extends RemoteEmfObserver<P, T, L, C> {

	static final int TEST_TIMEOUT = 100;

	boolean currentlyUpdating;

	int updated;

	int updating;

	int updatedMember;

	int responded;

	public TestRemoteEmfObserver() {
	}

	public TestRemoteEmfObserver(RemoteEmfConsumer<P, T, L, ?, ?, C> consumer) {
		super(consumer);
	}

	@Override
	public synchronized void updating() {
		updating++;
		currentlyUpdating = true;
	}

	@Override
	public void updated(boolean modified) {
		responded++;
		if (modified) {
			updated++;
		}
		if (consumer.getModelObject() instanceof Collection) {
			updatedMember++;
		}
		currentlyUpdating = false;
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
}