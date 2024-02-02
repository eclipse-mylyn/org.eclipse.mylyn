/*******************************************************************************
 * Copyright (c) 2011, 2013 GitHub Inc. and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     GitHub Inc. - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/
package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Miles Parker
 */
public class RemoteEmfObserverTest {

	RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> consumer1;

	RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> consumer2;

	TestRemoteEmfObserver<EPackage, EClass, String, Integer> listener1;

	TestRemoteEmfObserver<EPackage, EClass, String, Integer> listener2;

	@Before
	public void setup() {
		TestEClassRemoteFactory factory = new TestEClassRemoteFactory();
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();

		consumer1 = factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 1");
		consumer2 = factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 2");
		assertThat(consumer1, not(sameInstance(consumer2)));

		listener1 = new TestRemoteEmfObserver<>(consumer1);
		listener2 = new TestRemoteEmfObserver<>();
	}

	@Test
	public void testListener2NotUpdated() {
		consumer1.retrieve(false);

		listener1.waitForResponse(1, 1);
		assertThat(listener2.responded, is(0));
		assertThat(listener2.updated, is(0));
	}

	@Test
	public void testListener2SetConsumer1() {
		listener2.setConsumer(consumer1);

		consumer1.retrieve(false);

		listener1.waitForResponse(1, 1);
		assertThat(listener2.responded, is(1));
		assertThat(listener2.updated, is(1));
	}

	@Test
	public void testConsumer1RemoveObserver1() {
		listener2.setConsumer(consumer1);
		consumer1.removeObserver(listener1);
		assertThat(listener1.getConsumer(), nullValue());
		assertThat(listener2.getConsumer() == consumer1, is(true));

		consumer1.retrieve(false);

		listener2.waitForResponse(1, 1);
		assertThat(listener1.responded, is(0));
		assertThat(listener1.updated, is(0));
	}

	@Test
	public void testConsumer2AddObserver1() {
		listener2.setConsumer(consumer1);
		consumer2.addObserver(listener1);
		assertThat(listener1.getConsumer() == consumer2, is(true));
		assertThat(listener2.getConsumer() == consumer1, is(true));

		consumer1.retrieve(false);

		listener2.waitForResponse(1, 1);
		assertThat(listener1.responded, is(0));
		assertThat(listener1.updated, is(0));

		consumer2.retrieve(false);

		listener1.waitForResponse(1, 1);
		assertThat(listener1.responded, is(1));
		assertThat(listener1.updated, is(1));
	}

	@Test
	public void testListener2SetConsumer2() {
		consumer2.addObserver(listener1);
		listener2.setConsumer(consumer2);
		assertThat(listener1.getConsumer() == consumer2, is(true));
		assertThat(listener2.getConsumer() == consumer2, is(true));

		consumer2.retrieve(false);

		listener2.waitForResponse(1, 1);
		assertThat(listener1.responded, is(1));
		assertThat(listener1.updated, is(1));
	}

	@Test
	public void testAddRemoteAndSetListeners() {
		consumer1.retrieve(false);
		listener1.waitForResponse(1, 1);
		assertThat(listener2.responded, is(0));
		assertThat(listener2.updated, is(0));

		listener2.setConsumer(consumer1);
		consumer1.retrieve(false);
		listener1.waitForResponse(2, 2);
		assertThat(listener2.responded, is(1));
		assertThat(listener2.updated, is(1));

		consumer1.removeObserver(listener1);
		assertThat(listener1.getConsumer(), nullValue());
		assertThat(listener2.getConsumer() == consumer1, is(true));
		consumer1.retrieve(false);
		listener2.waitForResponse(2, 2);
		assertThat(listener1.responded, is(2));
		assertThat(listener1.updated, is(2));

		consumer2.addObserver(listener1);
		assertThat(listener1.getConsumer() == consumer2, is(true));
		assertThat(listener2.getConsumer() == consumer1, is(true));
		consumer1.retrieve(false);
		listener2.waitForResponse(3, 3);
		assertThat(listener1.responded, is(2));
		assertThat(listener1.updated, is(2));
		consumer2.retrieve(false);
		listener1.waitForResponse(3, 3);
		assertThat(listener1.responded, is(3));
		assertThat(listener1.updated, is(3));

		listener2.setConsumer(consumer2);
		assertThat(listener1.getConsumer() == consumer2, is(true));
		assertThat(listener2.getConsumer() == consumer2, is(true));
		consumer2.retrieve(false);
		listener2.waitForResponse(4, 4);
		assertThat(listener1.responded, is(4));
		assertThat(listener1.updated, is(4));
	}
}
