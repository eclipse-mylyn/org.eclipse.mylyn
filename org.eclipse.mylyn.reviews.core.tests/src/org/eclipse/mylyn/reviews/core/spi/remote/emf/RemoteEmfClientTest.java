/*******************************************************************************
 * Copyright (c) 2011 GitHub Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     GitHub Inc. - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/
package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.Test;

/**
 * @author Miles Parker
 */
public class RemoteEmfClientTest {

	class TestRemoteEmfClient extends RemoteEmfClient<EPackage, EClass, String, Integer> {

		boolean clientReady;

		boolean modelReady;

		boolean createCalled;

		boolean updateCalled;

		boolean rebuildCalled;

		@Override
		protected boolean isClientReady() {
			return clientReady;
		}

		@Override
		protected void create() {
			createCalled = true;
		}

		@Override
		protected void update() {
			super.update();
			updateCalled = true;
		}

		@Override
		protected void rebuild() {
			rebuildCalled = true;
		}
	}

	@Test
	public void testRequestUpdate() {
		TestRemoteFactory factory = new TestRemoteFactory();
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteObject, String, Integer> consumer = factory.getConsumerForRemoteKey(
				parent, "remoteKeyFor Object 1");
		TestRemoteEmfClient client = new TestRemoteEmfClient();
		client.setConsumer(consumer);
		TestRemoteEmfObserver<EPackage, EClass, String, Integer> listener = new TestRemoteEmfObserver<EPackage, EClass, String, Integer>(
				consumer); //just to keep track of updates
		client.checkUpdate(false);
		assertThat(client.createCalled, is(false));
		assertThat(client.updateCalled, is(false));
		assertThat(client.rebuildCalled, is(false));
		client.requestUpdate(true);
		listener.waitForResponse(1, 1);
		assertThat(client.createCalled, is(false));
		assertThat(client.updateCalled, is(false));
		assertThat(client.rebuildCalled, is(false));
		client.clientReady = true;
		client.requestUpdate(false);
		listener.waitForResponse(2, 2);
		assertThat(client.createCalled, is(true));
		assertThat(client.updateCalled, is(true));
		assertThat(client.rebuildCalled, is(false));
	}

	@Test
	public void testForceUpdate() {
		TestRemoteFactory factory = new TestRemoteFactory() {
			@Override
			public boolean updateModel(EPackage parent, EClass object, TestRemoteObject remoteObject) {
				return false;
			}

			@Override
			public Integer getModelCurrentValue(EPackage parentObject, EClass object) {
				return 123;
			}
		};
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteObject, String, Integer> consumer = factory.getConsumerForRemoteKey(
				parent, "remoteKeyFor Object 1");
		TestRemoteEmfClient client = new TestRemoteEmfClient();
		client.setConsumer(consumer);
		TestRemoteEmfObserver<EPackage, EClass, String, Integer> listener = new TestRemoteEmfObserver<EPackage, EClass, String, Integer>(
				consumer); //just to keep track of updates
		client.clientReady = true;
		client.requestUpdate(true);
		listener.waitForResponse(1, 1);
		assertThat(client.createCalled, is(true));
		assertThat(client.updateCalled, is(true));
		assertThat(client.rebuildCalled, is(false));
		client.updateCalled = false;
		client.requestUpdate(false);
		listener.waitForResponse(2, 1);
		assertThat(client.createCalled, is(true));
		assertThat(client.updateCalled, is(false));
		assertThat(client.rebuildCalled, is(false));
		client.requestUpdate(true);
		listener.waitForResponse(3, 1);
		assertThat(client.createCalled, is(true));
		assertThat(client.updateCalled, is(true));
		assertThat(client.rebuildCalled, is(false));
	}

	@Test
	public void testModelCurrentValueChanged() {
		TestRemoteFactory factory = new TestRemoteFactory() {
			@Override
			public boolean updateModel(EPackage parent, EClass object, TestRemoteObject remoteObject) {
				return false;
			}
		};
		factory.currentVal = 123;
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteObject, String, Integer> consumer = factory.getConsumerForRemoteKey(
				parent, "remoteKeyFor Object 1");
		TestRemoteEmfClient client = new TestRemoteEmfClient();
		client.setConsumer(consumer);
		TestRemoteEmfObserver<EPackage, EClass, String, Integer> listener = new TestRemoteEmfObserver<EPackage, EClass, String, Integer>(
				consumer); //just to keep track of updates
		client.clientReady = true;
		client.requestUpdate(true);
		listener.waitForResponse(1, 1);
		assertThat(client.updateCalled, is(true));
		client.updateCalled = false;
		client.requestUpdate(false);
		listener.waitForResponse(2, 1);
		assertThat(client.updateCalled, is(false));
		factory.currentVal = 456;
		client.requestUpdate(false);
		listener.waitForResponse(3, 1);
		assertThat(client.updateCalled, is(true));

	}

	@Test
	public void testRebuild() {
		TestRemoteFactory factory = new TestRemoteFactory() {
			@Override
			public boolean updateModel(EPackage parent, EClass object, TestRemoteObject remoteObject) {
				return false;
			}
		};
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteObject, String, Integer> consumer = factory.getConsumerForRemoteKey(
				parent, "remoteKeyFor Object 1");
		TestRemoteEmfClient client = new TestRemoteEmfClient();
		client.setConsumer(consumer);
		TestRemoteEmfObserver<EPackage, EClass, String, Integer> listener = new TestRemoteEmfObserver<EPackage, EClass, String, Integer>(
				consumer); //just to keep track of updates
		client.clientReady = true;
		client.requestUpdate(true);
		listener.waitForResponse(1, 1);
		assertThat(client.createCalled, is(true));
		assertThat(client.updateCalled, is(true));
		assertThat(client.rebuildCalled, is(false));
		client.createCalled = false;
		client.updateCalled = false;
		client.requestUpdate(false, true);
		listener.waitForResponse(2, 1);
		assertThat(client.createCalled, is(false));
		assertThat(client.rebuildCalled, is(true));
		assertThat(client.updateCalled, is(true));
	}

	@Test
	public void testGetModelThenCheckClient() {
		TestRemoteFactory factory = new TestRemoteFactory();
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteObject, String, Integer> consumer = factory.getConsumerForRemoteKey(
				parent, "remoteKeyFor Object 1");
		TestRemoteEmfClient client = new TestRemoteEmfClient();
		client.setConsumer(consumer);
		TestRemoteEmfObserver<EPackage, EClass, String, Integer> listener = new TestRemoteEmfObserver<EPackage, EClass, String, Integer>(
				consumer); //just to keep track of updates
		client.requestUpdate(true);
		listener.waitForResponse(1, 1);
		assertThat(client.createCalled, is(false));
		assertThat(client.updateCalled, is(false));
		assertThat(client.rebuildCalled, is(false));
		client.clientReady = true;
		client.checkUpdate(true);
		listener.waitForResponse(1, 1);
		assertThat(client.createCalled, is(true));
		assertThat(client.updateCalled, is(true));
		assertThat(client.rebuildCalled, is(false));
	}

	@Test
	public void testRequestUpdateNoForce() {
		TestRemoteFactory factory = new TestRemoteFactory() {
			@Override
			public boolean updateModel(EPackage parent, EClass object, TestRemoteObject remoteObject) {
				return true;
			}
		};
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteObject, String, Integer> consumer = factory.getConsumerForRemoteKey(
				parent, "remoteKeyFor Object 1");
		TestRemoteEmfClient client = new TestRemoteEmfClient();
		client.setConsumer(consumer);
		TestRemoteEmfObserver<EPackage, EClass, String, Integer> listener = new TestRemoteEmfObserver<EPackage, EClass, String, Integer>(
				consumer); //just to keep track of updates
		client.clientReady = true;
		client.requestUpdate(false);
		listener.waitForResponse(1, 1);
		assertThat(client.createCalled, is(true));
		assertThat(client.updateCalled, is(true));
		assertThat(client.rebuildCalled, is(false));
	}
}
