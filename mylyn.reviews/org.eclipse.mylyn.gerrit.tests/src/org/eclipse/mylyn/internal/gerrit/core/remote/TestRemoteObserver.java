/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfObserver;

@SuppressWarnings("nls")
class TestRemoteObserver<P extends EObject, T, L, C> extends RemoteEmfObserver<P, T, L, C> {

	static final int TEST_TIMEOUT = 15000;

	int updated;

	int responded;

	AbstractRemoteEmfFactory<P, T, L, ?, ?, C> factory;

	public TestRemoteObserver(AbstractRemoteEmfFactory<P, T, L, ?, ?, C> factory) {
		this.factory = factory;
	}

	@Override
	public void updating() {
	}

	@Override
	public void updated(boolean modified) {
		responded++;
		if (modified) {
			updated++;
		}
	}

	void waitForResponse() {
		waitForResponse(1, 1);
	}

	void waitForResponse(boolean updated) {
		waitForResponse(1, updated ? 1 : 0);
	}

	private void waitForResponse(int responses, int updates) {
		try {
			long delay;
			delay = 0;
			while (delay < TEST_TIMEOUT) {
				if (responded < responses || updated < updates) {
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
			assertThat("Wrong # responses: " + responded + ", updated: " + updated, responded, is(responses));
			assertThat("Wrong # updates " + updated, updated, is(updates));
			if (factory != null) {
				assertThat(factory.getService().isActive(), is(false));
			}
		} finally {
			responded = 0;
			updated = 0;
		}
	}

}