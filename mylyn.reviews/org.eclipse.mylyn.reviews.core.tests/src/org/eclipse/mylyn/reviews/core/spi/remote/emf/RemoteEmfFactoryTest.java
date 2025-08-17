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
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.Test;

/**
 * @author Miles Parker
 */
@SuppressWarnings("nls")
public class RemoteEmfFactoryTest {

	private static final String LOCAL_1 = "Local Object 1";

	private static final String LOCAL_2 = "Local Object 2";

	private static final String REMOTE_1 = "Remote Object 1";

	private static final String REMOTE_2 = "Remote Object 2";

	private static final String LOCAL_KEY_1 = "localKeyFor Object 1";

	private static final String LOCAL_KEY_2 = "localKeyFor Object 2";

	private static final String REMOTE_KEY_1 = "remoteKeyFor Object 1";

	private static final String REMOTE_KEY_2 = "remoteKeyFor Object 2";

	EPackage parent = EcoreFactory.eINSTANCE.createEPackage();

	final IStatus errorStatus = new Status(IStatus.ERROR, "blah", "Blah");

	class TestManagerEClassHarness {

		TestEClassRemoteFactory factory;

		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> consumer;

		TestRemoteEmfObserver<EPackage, EClass, String, Integer> listener;

		TestManagerEClassHarness(TestEClassRemoteFactory factory) {
			this.factory = factory;
			consumer = createConsumer();
			listener = new TestRemoteEmfObserver<>(consumer);
		}

		TestManagerEClassHarness() {
			this(new TestEClassRemoteFactory());
		}

		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
			return factory.getConsumerForRemoteKey(parent, REMOTE_KEY_1);
		}

		void basicTest() {
			consumer.retrieve(false);
			listener.waitForResponse(1, 1);
			checkConsumer(consumer, REMOTE_KEY_1, REMOTE_1, LOCAL_KEY_1, LOCAL_1);
		}
	}

	class TestManagerEPackageHarness {

		TestEPackageRemoteFactory factory;

		RemoteEmfConsumer<EPackage, EPackage, String, TestRemoteEPackage, String, Integer> consumer;

		TestRemoteEmfObserver<EPackage, EPackage, String, Integer> listener;

		TestManagerEPackageHarness(TestEPackageRemoteFactory factory) {
			this.factory = factory;
			consumer = createConsumer();
			listener = new TestRemoteEmfObserver<>(consumer);
		}

		TestManagerEPackageHarness() {
			this(new TestEPackageRemoteFactory());
		}

		RemoteEmfConsumer<EPackage, EPackage, String, TestRemoteEPackage, String, Integer> createConsumer() {
			return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Package 1");
		}

		void basicTest() {
			consumer.retrieve(false);
			listener.waitForResponse(1, 1);
			checkConsumer(consumer, "remoteKeyFor Package 1", "Remote Package 1", "localKeyFor Package 1",
					"Local Package 1");
		}
	}

	private static void checkConsumer(RemoteEmfConsumer<?, ?, String, ?, String, Integer> manager, String remoteKey,
			String remoteObjectString, String localKey, String localObject) {

		if (remoteKey != null) {
			assertThat("Bad Remote Key", manager.getRemoteKey(), is(remoteKey));
		} else {
			assertThat("Bad Remote Key", manager.getRemoteKey(), nullValue());
		}
		if (remoteObjectString != null) {
			assertThat("Bad Remote Object", manager.getRemoteObject(), notNullValue());
		} else {
			assertThat("Bad Remote Object", manager.getRemoteObject(), nullValue());
		}
		if (localKey != null) {
			assertThat("Bad Local Key", manager.getLocalKey(), is(localKey));
		} else {
			assertThat("Bad Local Key", manager.getLocalKey(), nullValue());
		}
		if (localObject != null) {
			assertThat("Bad Local Object", manager.getModelObject(), notNullValue());
		} else {
			assertThat("Bad Local Object", manager.getModelObject(), nullValue());
		}
	}

	@Test
	public void testRemoteProcessCreate() {
		TestEClassRemoteFactory testEClassRemoteFactory = new TestEClassRemoteFactory();
		EClass create1 = testEClassRemoteFactory.createModel(parent, TestEClassRemoteFactory.remote1);
		assertThat(create1.getName(), is(LOCAL_1));
		EClass create2 = testEClassRemoteFactory.createModel(parent, TestEClassRemoteFactory.remote2);
		assertThat(create2.getName(), is(LOCAL_2));
	}

	@Test
	public void testGetConsumerForRemoteKey() throws CoreException {
		TestManagerEClassHarness harness = new TestManagerEClassHarness() {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, REMOTE_KEY_1);
			}
		};
		checkConsumer(harness.consumer, REMOTE_KEY_1, null, LOCAL_KEY_1, null);
		harness.basicTest();
	}

	@Test
	public void testGetConsumerForRemoteKeyUpdate() throws CoreException {
		TestManagerEClassHarness harness = new TestManagerEClassHarness() {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, REMOTE_KEY_1);
			}
		};
		checkConsumer(harness.consumer, REMOTE_KEY_1, null, LOCAL_KEY_1, null);
		harness.basicTest();

		TestEClassRemoteFactory.remote1.data = "new";
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(2, 2);
		EClass modelObject = harness.consumer.getModelObject();
		assertThat(modelObject.getInstanceTypeName(), is("new"));
	}

	@Test
	public void testGetConsumerForDifferentParentSameLocalKey() throws CoreException {
		EPackage parent1 = EcoreFactory.eINSTANCE.createEPackage();
		TestEClassRemoteFactory factory = new TestEClassRemoteFactory();
		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> consumer1 = factory
				.getConsumerForRemoteKey(parent1, REMOTE_KEY_1);

		EPackage parent2 = EcoreFactory.eINSTANCE.createEPackage();
		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> consumer2 = factory
				.getConsumerForRemoteKey(parent2, REMOTE_KEY_1);

		assertThat(consumer1, not(sameInstance(consumer2)));
	}

	@Test
	public void testGetConsumerForLocalKey() throws CoreException {
		TestManagerEClassHarness harness = new TestManagerEClassHarness() {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForLocalKey(parent, LOCAL_KEY_1);
			}
		};
		checkConsumer(harness.consumer, null, null, LOCAL_KEY_1, null);
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(1, 0);
		checkConsumer(harness.consumer, null, null, LOCAL_KEY_1, null);
	}

	@Test
	public void testGetConsumerForRemote() throws CoreException {
		TestManagerEClassHarness harness = new TestManagerEClassHarness() {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteObject(parent, TestEClassRemoteFactory.remote1);
			}
		};
		checkConsumer(harness.consumer, REMOTE_KEY_1, REMOTE_1, LOCAL_KEY_1, null);
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(1, 1);
		checkConsumer(harness.consumer, REMOTE_KEY_1, REMOTE_1, LOCAL_KEY_1, LOCAL_1);
	}

	@Test
	public void testRemoteKeyThenRemoteObject() throws CoreException {
		TestEClassRemoteFactory testEClassRemoteFactory = new TestEClassRemoteFactory();
		TestManagerEClassHarness keyHarness = new TestManagerEClassHarness(testEClassRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, REMOTE_KEY_2);
			}
		};
		checkConsumer(keyHarness.consumer, REMOTE_KEY_2, null, LOCAL_KEY_2, null);
		TestManagerEClassHarness remoteHarness = new TestManagerEClassHarness(testEClassRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteObject(parent, TestEClassRemoteFactory.remote2);
			}
		};
		assertThat(keyHarness.consumer, sameInstance(remoteHarness.consumer));
		checkConsumer(remoteHarness.consumer, REMOTE_KEY_2, REMOTE_2, LOCAL_KEY_2, null);
	}

	@Test
	public void testRemoteObjectThenRemoteKey() throws CoreException {
		TestEClassRemoteFactory testEClassRemoteFactory = new TestEClassRemoteFactory();
		TestManagerEClassHarness remoteHarness = new TestManagerEClassHarness(testEClassRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteObject(parent, TestEClassRemoteFactory.remote2);
			}
		};
		checkConsumer(remoteHarness.consumer, REMOTE_KEY_2, REMOTE_2, LOCAL_KEY_2, null);
		TestManagerEClassHarness keyHarness = new TestManagerEClassHarness(testEClassRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, REMOTE_KEY_2);
			}
		};
		assertThat(keyHarness.consumer, sameInstance(remoteHarness.consumer));
		checkConsumer(keyHarness.consumer, REMOTE_KEY_2, REMOTE_2, LOCAL_KEY_2, null);
	}

	@Test
	public void testLocalKeyThenRemoteKeyAndObject() throws CoreException {
		TestEClassRemoteFactory testEClassRemoteFactory = new TestEClassRemoteFactory();
		TestManagerEClassHarness remoteHarness = new TestManagerEClassHarness(testEClassRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForLocalKey(parent, LOCAL_KEY_2);
			}
		};
		checkConsumer(remoteHarness.consumer, null, null, LOCAL_KEY_2, null);
		TestManagerEClassHarness keyHarness = new TestManagerEClassHarness(testEClassRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, REMOTE_KEY_2);
			}
		};
		assertThat(keyHarness.consumer, sameInstance(remoteHarness.consumer));
		checkConsumer(keyHarness.consumer, REMOTE_KEY_2, null, LOCAL_KEY_2, null);
		keyHarness.consumer.retrieve(false);
		keyHarness.listener.waitForResponse(1, 1);
		checkConsumer(keyHarness.consumer, REMOTE_KEY_2, REMOTE_2, LOCAL_KEY_2, LOCAL_2);
	}

	@Test
	public void testLocalObjectThenRemoteKeyAndObject() {
		TestEClassRemoteFactory factory = new TestEClassRemoteFactory();
		EClass create1 = EcoreFactory.eINSTANCE.createEClass();
		create1.setName("Object 1");
		create1.setInstanceClassName(LOCAL_KEY_1);
		parent.getEClassifiers().add(create1);
		EClass create2 = EcoreFactory.eINSTANCE.createEClass();
		create2.setName("Object 2");
		create2.setInstanceClassName(LOCAL_KEY_2);
		parent.getEClassifiers().add(create2);
		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> consumer = factory
				.getConsumerForLocalKey(parent, LOCAL_KEY_2);
		TestRemoteEmfObserver<EPackage, EClass, String, Integer> testListener1 = new TestRemoteEmfObserver<>(
				consumer);
		consumer.retrieve(false);
		testListener1.waitForResponse(1, 0);
		checkConsumer(consumer, null, null, LOCAL_KEY_2, "Object 2");

		TestManagerEClassHarness keyHarness = new TestManagerEClassHarness(factory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, REMOTE_KEY_2);
			}
		};
		assertThat(keyHarness.consumer, sameInstance(consumer));
		checkConsumer(keyHarness.consumer, REMOTE_KEY_2, null, LOCAL_KEY_2, "Object 2");
		consumer.retrieve(false);
		testListener1.waitForResponse(2, 1);
		checkConsumer(keyHarness.consumer, REMOTE_KEY_2, REMOTE_2, LOCAL_KEY_2, "Object 2");
	}

	@Test
	public void testRemoteProcessFailure() throws CoreException {
		TestEClassRemoteFactory factory = new TestEClassRemoteFactory() {
			@Override
			public TestRemoteEClass pull(EPackage parent, String remoteKey, IProgressMonitor monitor)
					throws CoreException {
				throw new CoreException(errorStatus);
			}
		};
		RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> consumer1 = factory
				.getConsumerForRemoteKey(parent, "object1");
		TestRemoteEmfObserver<EPackage, EClass, String, Integer> testListener = new TestRemoteEmfObserver<>(
				consumer1);
		consumer1.retrieve(false);
		testListener.waitForResponse(1, 0);
		assertThat(consumer1.getStatus(), sameInstance(errorStatus));
	}

	@Test
	public void testMultipleConsumers() throws CoreException {
		TestEClassRemoteFactory factory = new TestEClassRemoteFactory();
		TestManagerEClassHarness[] harnesses = new TestManagerEClassHarness[5];
		for (int i = 0; i < harnesses.length; i++) {
			harnesses[i] = new TestManagerEClassHarness(factory);
			harnesses[i].basicTest();
		}
		for (int i = 1; i < harnesses.length; i++) {
			assertThat(harnesses[i].consumer, sameInstance(harnesses[0].consumer));
		}
	}

	@Test
	public void testRemoteProcessCollectionRequestAndUpdate() throws CoreException {
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		TestCollectionObjectRemoteFactory factory = new TestCollectionObjectRemoteFactory();
		RemoteEmfConsumer<EPackage, List<EClassifier>, String, TestRemoteEClass, String, Integer> consumer1 = factory
				.getConsumerForRemoteKey(parent, REMOTE_KEY_1);
		TestRemoteEmfObserver<EPackage, List<EClassifier>, String, Integer> testListener = new TestRemoteEmfObserver<>(
				consumer1);
		consumer1.retrieve(false);
		testListener.waitForResponse(1, 1);
		List<EClassifier> modelObject = consumer1.getModelObject();
		assertThat(modelObject.size(), is(3));
		assertThat(modelObject.get(0).getName(), is("Many Remote Object 1_1"));
		assertThat(modelObject.get(1).getName(), is("Many Remote Object 1_2"));
		assertThat(modelObject.get(2).getName(), is("Many Remote Object 1_3"));

		consumer1.retrieve(false);
		testListener.waitForResponse(2, 2);
		consumer1.retrieve(false);
		testListener.waitForResponse(3, 3);
		assertThat(modelObject.size(), is(5));
		assertThat(modelObject.get(3).getName(), is("Many Remote Object 1_4"));
		assertThat(modelObject.get(4).getName(), is("Many Remote Object 1_5"));
	}

	@Test
	public void testRemoteKeyNoPull() throws CoreException {
		class TestNoPullFactory extends TestEClassRemoteFactory {
			@Override
			public TestRemoteEClass pull(EPackage parent, String remoteKey, IProgressMonitor monitor)
					throws CoreException {
				fail("No retrieve call expected.");
				return null;
			}

			@Override
			public boolean isPullNeeded(EPackage parent, EClass object, TestRemoteEClass remote) {
				return false;
			}
		}
		TestManagerEClassHarness harness = new TestManagerEClassHarness(new TestNoPullFactory()) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, REMOTE_KEY_1);
			}
		};
		checkConsumer(harness.consumer, REMOTE_KEY_1, null, LOCAL_KEY_1, null);
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(1, 0);
		checkConsumer(harness.consumer, REMOTE_KEY_1, null, LOCAL_KEY_1, null);
	}

	@Test
	public void testRemoteKeyNoPullForceOnly() throws CoreException {
		class TestNoPullForceOnlyFactory extends TestEClassRemoteFactory {

			@Override
			public boolean isPullNeeded(EPackage parent, EClass object, TestRemoteEClass remote) {
				return false;
			}
		}
		TestManagerEClassHarness harness = new TestManagerEClassHarness(new TestNoPullForceOnlyFactory()) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, REMOTE_KEY_1);
			}
		};
		checkConsumer(harness.consumer, REMOTE_KEY_1, null, LOCAL_KEY_1, null);
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(1, 0);
		checkConsumer(harness.consumer, REMOTE_KEY_1, null, LOCAL_KEY_1, null);
		harness.consumer.retrieve(true);
		harness.listener.waitForResponse(2, 1);
		checkConsumer(harness.consumer, REMOTE_KEY_1, REMOTE_1, LOCAL_KEY_1, LOCAL_1);
	}

	@Test
	public void testRemoteKeyNoUpdate() throws CoreException {
		class TestPullCreateOnlyFactory extends TestEClassRemoteFactory {

			@Override
			public boolean isUpdateModelNeeded(EPackage parent, EClass object, TestRemoteEClass remote) {
				return false;
			}
		}
		TestManagerEClassHarness harness = new TestManagerEClassHarness(new TestPullCreateOnlyFactory()) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, REMOTE_KEY_1);
			}
		};
		checkConsumer(harness.consumer, REMOTE_KEY_1, null, LOCAL_KEY_1, null);
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(1, 1);
		checkConsumer(harness.consumer, REMOTE_KEY_1, REMOTE_1, LOCAL_KEY_1, LOCAL_1);
		TestEClassRemoteFactory.remote1.data = "newData";
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(2, 1);
		EClass modelObject = harness.consumer.getModelObject();
		checkConsumer(harness.consumer, REMOTE_KEY_1, REMOTE_1, LOCAL_KEY_1, LOCAL_1);
		assertThat(modelObject.getInstanceTypeName(), is(LOCAL_KEY_1));
		harness.consumer.retrieve(true);
		harness.listener.waitForResponse(3, 2);
		checkConsumer(harness.consumer, REMOTE_KEY_1, REMOTE_1, LOCAL_KEY_1, LOCAL_1);
		assertThat(modelObject.getInstanceTypeName(), is("newData"));
	}

	@Test
	public void testParentConsumerUpdate() {
		final TestManagerEPackageHarness packageHarness = new TestManagerEPackageHarness() {
			@Override
			RemoteEmfConsumer<EPackage, EPackage, String, TestRemoteEPackage, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Package 1");
			}
		};
		checkConsumer(packageHarness.consumer, "remoteKeyFor Package 1", null, "localKeyFor Package 1", null);
		packageHarness.basicTest();
		assertThat(packageHarness.listener.updatedMember, is(0));

		TestManagerEClassHarness childHarness = new TestManagerEClassHarness() {
			@Override
			RemoteEmfConsumer<EPackage, EClass, String, TestRemoteEClass, String, Integer> createConsumer() {
				return factory.getConsumerForRemoteObject(packageHarness.consumer.getModelObject(),
						TestEClassRemoteFactory.remote1);
			}
		};
		checkConsumer(childHarness.consumer, REMOTE_KEY_1, REMOTE_1, LOCAL_KEY_1, null);
		childHarness.consumer.retrieve(false);
		childHarness.listener.waitForResponse(1, 1);
		checkConsumer(childHarness.consumer, REMOTE_KEY_1, REMOTE_1, LOCAL_KEY_1, LOCAL_1);
		assertThat(packageHarness.listener.updated, is(1));
		assertThat(packageHarness.listener.updatedMember, is(0));
	}

	@Test
	public void testParentConsumerCollectionUpdate() {
		TestCollectionObjectRemoteFactory factory = new TestCollectionObjectRemoteFactory();
		RemoteEmfConsumer<EPackage, List<EClassifier>, String, TestRemoteEClass, String, Integer> consumer = factory
				.getConsumerForRemoteKey(parent, REMOTE_KEY_1);
		TestRemoteEmfObserver<EPackage, List<EClassifier>, String, Integer> testListener = new TestRemoteEmfObserver<>(
				consumer);
		consumer.retrieve(false);
		testListener.waitForResponse(1, 1);
		assertThat(testListener.updating, is(1));
		assertThat(testListener.currentlyUpdating, is(false));
		assertThat(testListener.updatedMember, is(1));

		consumer.retrieve(true);
		testListener.waitForResponse(2, 2);
		assertThat(testListener.currentlyUpdating, is(false));
		assertThat(testListener.updating, is(2));
		assertThat(testListener.updatedMember, is(2));
	}

	@Test
	public void testParentConsumerCollectionFailure() throws CoreException {
		TestCollectionObjectRemoteFactory factory = new TestCollectionObjectRemoteFactory() {
			@Override
			public TestRemoteEClass pull(EPackage parent, String remoteKey, IProgressMonitor monitor)
					throws CoreException {
				throw new CoreException(errorStatus);
			}
		};
		RemoteEmfConsumer<EPackage, List<EClassifier>, String, TestRemoteEClass, String, Integer> consumer = factory
				.getConsumerForRemoteKey(parent, REMOTE_KEY_1);
		TestRemoteEmfObserver<EPackage, List<EClassifier>, String, Integer> testListener = new TestRemoteEmfObserver<>(
				consumer);
		consumer.retrieve(false);
		testListener.waitForResponse(1, 0);
		assertThat(consumer.getStatus(), sameInstance(errorStatus));
	}

}
