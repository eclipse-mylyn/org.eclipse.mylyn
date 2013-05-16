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
package org.eclipse.mylyn.reviews.core.remote;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
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
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.junit.Test;

/**
 * @author Miles Parker
 */
public class RemoteEmfFactoryTest {

	class TestRemoteObjectManyObject {
		String name;

		String data;

		private TestRemoteObjectManyObject(String name) {
			this.name = name;
		}
	}

	class TestRemoteFactoryNoUpdate extends TestRemoteFactory {

		@Override
		public boolean updateModel(EPackage parent, EClass object, TestRemoteObject remoteObject) {
			return false;
		}
	}

	EPackage parent = EcoreFactory.eINSTANCE.createEPackage();

	class TestManagerHarness {

		TestRemoteFactory factory;

		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consumer;

		TestIRemoteEmfObserver<EPackage, EClass> listener;

		TestManagerHarness(TestRemoteFactory factory) {
			this.factory = factory;
			listener = new TestIRemoteEmfObserver<EPackage, EClass>(factory);
			consumer = createConsumer();
			consumer.addObserver(listener);
		}

		TestManagerHarness() {
			this(new TestRemoteFactory());
		}

		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
			return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 1");
		}

		void basicTest() {
			consumer.retrieve(false);
			listener.waitForResponse(1, 1);
			checkConsumer(consumer, "remoteKeyFor Object 1", "Remote Object 1", "localKeyFor Object 1",
					"Local Object 1");
		}
	}

	IStatus errorStatus = new Status(IStatus.ERROR, "blah", "Blah");

	class TestFailureFactory extends TestRemoteFactory {
		@Override
		public TestRemoteObject pull(EPackage parent, String remoteKey, IProgressMonitor monitor) throws CoreException {
			throw new CoreException(errorStatus);
		}
	}

	protected void checkConsumer(RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> manager,
			String remoteKey, String remoteObject, String localKey, String localObject) {
		if (remoteKey != null) {
			assertThat("Bad Remote Key", manager.getRemoteKey(), is(remoteKey));
		} else {
			assertThat("Bad Remote Key", manager.getRemoteKey(), nullValue());
		}
		if (remoteObject != null) {
			assertThat("Bad Remote Object", manager.getRemoteObject(), notNullValue());
			assertThat(manager.getRemoteObject().getName(), is(remoteObject));
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
			assertThat(manager.getModelObject().getName(), is(localObject));
		} else {
			assertThat("Bad Local Object", manager.getModelObject(), nullValue());
		}
	}

	@Test
	public void testRemoteProcessCreate() {
		TestRemoteFactory testRemoteFactory = new TestRemoteFactory();
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		EClass create1 = testRemoteFactory.createModel(parent, TestRemoteFactory.remote1);
		assertThat(create1.getName(), is("Local Object 1"));
		EClass create2 = testRemoteFactory.createModel(parent, TestRemoteFactory.remote2);
		assertThat(create2.getName(), is("Local Object 2"));
	}

	@Test
	public void testGetConsumerForRemoteKey() throws CoreException {
		TestManagerHarness harness = new TestManagerHarness() {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 1");
			}
		};
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", null, "localKeyFor Object 1", null);
		harness.basicTest();
	}

	@Test
	public void testGetConsumerForRemoteKeyUpdate() throws CoreException {
		TestManagerHarness harness = new TestManagerHarness() {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 1");
			}
		};
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", null, "localKeyFor Object 1", null);
		harness.basicTest();
		TestRemoteFactory.remote1.data = "new";
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(2, 2);
		EClass modelObject = harness.consumer.getModelObject();
		assertThat(modelObject.getInstanceTypeName(), is("new"));
	}

	@Test
	public void testGetConsumerForDifferentParentSameLocalKey() throws CoreException {
		EPackage parent1 = EcoreFactory.eINSTANCE.createEPackage();
		TestRemoteFactory factory = new TestRemoteFactory();
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consumer1 = factory.getConsumerForRemoteKey(
				parent1, "remoteKeyFor Object 1");

		EPackage parent2 = EcoreFactory.eINSTANCE.createEPackage();
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consumer2 = factory.getConsumerForRemoteKey(
				parent2, "remoteKeyFor Object 1");

		assertThat(consumer1, not(sameInstance(consumer2)));
	}

	@Test
	public void testGetConsumerForLocalKey() throws CoreException {
		TestManagerHarness harness = new TestManagerHarness() {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForLocalKey(parent, "localKeyFor Object 1");
			}
		};
		checkConsumer(harness.consumer, null, null, "localKeyFor Object 1", null);
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(0, 0);
		checkConsumer(harness.consumer, null, null, "localKeyFor Object 1", null);
	}

	@Test
	public void testGetConsumerForRemote() throws CoreException {
		TestManagerHarness harness = new TestManagerHarness() {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteObject(parent, TestRemoteFactory.remote1);
			}
		};
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", "Remote Object 1", "localKeyFor Object 1", null);
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(1, 1);
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", "Remote Object 1", "localKeyFor Object 1",
				"Local Object 1");
	}

	@Test
	public void testRemoteKeyThenRemoteObject() throws CoreException {
		TestRemoteFactory testRemoteFactory = new TestRemoteFactory();
		TestManagerHarness keyHarness = new TestManagerHarness(testRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 2");
			}
		};
		checkConsumer(keyHarness.consumer, "remoteKeyFor Object 2", null, "localKeyFor Object 2", null);
		TestManagerHarness remoteHarness = new TestManagerHarness(testRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteObject(parent, TestRemoteFactory.remote2);
			}
		};
		assertThat(keyHarness.consumer, sameInstance(remoteHarness.consumer));
		checkConsumer(remoteHarness.consumer, "remoteKeyFor Object 2", "Remote Object 2", "localKeyFor Object 2", null);
	}

	@Test
	public void testRemoteObjectThenRemoteKey() throws CoreException {
		TestRemoteFactory testRemoteFactory = new TestRemoteFactory();
		TestManagerHarness remoteHarness = new TestManagerHarness(testRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteObject(parent, TestRemoteFactory.remote2);
			}
		};
		checkConsumer(remoteHarness.consumer, "remoteKeyFor Object 2", "Remote Object 2", "localKeyFor Object 2", null);
		TestManagerHarness keyHarness = new TestManagerHarness(testRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 2");
			}
		};
		assertThat(keyHarness.consumer, sameInstance(remoteHarness.consumer));
		checkConsumer(keyHarness.consumer, "remoteKeyFor Object 2", "Remote Object 2", "localKeyFor Object 2", null);
	}

	@Test
	public void testLocalKeyThenRemoteKeyAndObject() throws CoreException {
		TestRemoteFactory testRemoteFactory = new TestRemoteFactory();
		TestManagerHarness remoteHarness = new TestManagerHarness(testRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForLocalKey(parent, "localKeyFor Object 2");
			}
		};
		checkConsumer(remoteHarness.consumer, null, null, "localKeyFor Object 2", null);
		TestManagerHarness keyHarness = new TestManagerHarness(testRemoteFactory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 2");
			}
		};
		assertThat(keyHarness.consumer, sameInstance(remoteHarness.consumer));
		checkConsumer(keyHarness.consumer, "remoteKeyFor Object 2", null, "localKeyFor Object 2", null);
		keyHarness.consumer.retrieve(false);
		keyHarness.listener.waitForResponse(1, 1);
		checkConsumer(keyHarness.consumer, "remoteKeyFor Object 2", "Remote Object 2", "localKeyFor Object 2",
				"Local Object 2");
	}

	@Test
	public void testLocalObjectThenRemoteKeyAndObject() {
		TestRemoteFactory factory = new TestRemoteFactory();
		EClass create1 = EcoreFactory.eINSTANCE.createEClass();
		create1.setName("Object 1");
		create1.setInstanceClassName("localKeyFor Object 1");
		parent.getEClassifiers().add(create1);
		EClass create2 = EcoreFactory.eINSTANCE.createEClass();
		create2.setName("Object 2");
		create2.setInstanceClassName("localKeyFor Object 2");
		parent.getEClassifiers().add(create2);
		TestIRemoteEmfObserver<EPackage, EClass> testListener1 = new TestIRemoteEmfObserver<EPackage, EClass>(factory);
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer = factory.getConsumerForLocalKey(
				parent, "localKeyFor Object 2");
		createConsumer.addObserver(testListener1);
		createConsumer.retrieve(false);
		testListener1.waitForResponse(1, 0);
		assertThat(testListener1.createdObject, nullValue());
		checkConsumer(createConsumer, null, null, "localKeyFor Object 2", "Object 2");

		TestManagerHarness keyHarness = new TestManagerHarness(factory) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 2");
			}
		};
		assertThat(keyHarness.consumer, sameInstance(createConsumer));
		checkConsumer(keyHarness.consumer, "remoteKeyFor Object 2", null, "localKeyFor Object 2", "Object 2");
		createConsumer.retrieve(false);
		testListener1.waitForResponse(2, 1);
		checkConsumer(keyHarness.consumer, "remoteKeyFor Object 2", "Remote Object 2", "localKeyFor Object 2",
				"Object 2");
	}

	@Test
	public void testRemoteProcessFailure() throws CoreException {
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		TestRemoteFactory factory = new TestFailureFactory();
		TestIRemoteEmfObserver<EPackage, EClass> testListener = new TestIRemoteEmfObserver<EPackage, EClass>(factory);
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consumer1 = factory.getConsumerForRemoteKey(
				parent, "object1");
		consumer1.addObserver(testListener);
		consumer1.retrieve(false);
		testListener.waitForFailure();
		assertThat(testListener.failure, is(errorStatus));
	}

	@Test
	public void testMultipleConsumers() throws CoreException {
		TestRemoteFactory factory = new TestRemoteFactory();
		TestManagerHarness[] harnesses = new TestManagerHarness[5];
		for (int i = 0; i < harnesses.length; i++) {
			harnesses[i] = new TestManagerHarness(factory);
			harnesses[i].basicTest();
		}
		for (int i = 1; i < harnesses.length; i++) {
			assertThat(harnesses[i].consumer, sameInstance(harnesses[0].consumer));
		}
	}

	class TestRemoteFactoryCollectionObject extends
			AbstractRemoteEmfFactory<EPackage, List<EClassifier>, TestRemoteObject, String, String> {

		public TestRemoteFactoryCollectionObject() {
			super(new TestRemoteFactoryProvider(), EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
					EcorePackage.Literals.ENAMED_ELEMENT__NAME);
		}

		@Override
		public TestRemoteObject pull(EPackage parent, String remoteKey, IProgressMonitor monitor) throws CoreException {
			return TestRemoteFactory.remoteForKey.get(remoteKey);
		}

		@Override
		protected List<EClassifier> createModel(EPackage parent, TestRemoteObject remoteObject) {
			List<EClassifier> classifiers = parent.getEClassifiers();
			EClass class1 = EcoreFactory.eINSTANCE.createEClass();
			class1.setName("Many " + remoteObject.getName() + "_1");
			classifiers.add(class1);
			EClass class2 = EcoreFactory.eINSTANCE.createEClass();
			class2.setName("Many " + remoteObject.getName() + "_2");
			classifiers.add(class2);
			return classifiers;
		}

		@Override
		public boolean updateModel(EPackage parent, List<EClassifier> classifiers, TestRemoteObject remoteObject) {
			EClass class2 = EcoreFactory.eINSTANCE.createEClass();
			class2.setName("Many " + remoteObject.getName() + "_" + (classifiers.size() + 1));
			classifiers.add(class2);
			return true;
		}

		@Override
		public String getRemoteKey(TestRemoteObject remoteObject) {
			return "remoteKeyFor" + remoteObject.getName();
		}

		@Override
		public String getLocalKeyForRemoteObject(TestRemoteObject remoteObject) {
			return "localKeyFor" + remoteObject.getName();
		}

		@Override
		public String getLocalKeyForRemoteKey(String remoteKey) {
			return remoteKey.replace("remote", "local");
		}
	}

	@Test
	public void testRemoteProcessCollectionRequestAndUpdate() throws CoreException {
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		TestRemoteFactoryCollectionObject factory = new TestRemoteFactoryCollectionObject();
		TestIRemoteEmfObserver<EPackage, List<EClassifier>> testListener = new TestIRemoteEmfObserver<EPackage, List<EClassifier>>(
				factory);
		RemoteEmfConsumer<EPackage, List<EClassifier>, TestRemoteObject, String, String> consumer1 = factory.getConsumerForRemoteKey(
				parent, "remoteKeyFor Object 1");
		consumer1.addObserver(testListener);
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

	class TestNoPullFactory extends TestRemoteFactory {
		@Override
		public TestRemoteObject pull(EPackage parent, String remoteKey, IProgressMonitor monitor) throws CoreException {
			fail("No retrieve call expected.");
			return null;
		}

		@Override
		public boolean isPullNeeded(EPackage parent, EClass object, TestRemoteObject remote) {
			return false;
		}
	}

	@Test
	public void testRemoteKeyNoPull() throws CoreException {
		TestManagerHarness harness = new TestManagerHarness(new TestNoPullFactory()) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 1");
			}
		};
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", null, "localKeyFor Object 1", null);
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(0, 0);
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", null, "localKeyFor Object 1", null);
	}

	class TestNoPullForceOnlyFactory extends TestRemoteFactory {

		@Override
		public boolean isPullNeeded(EPackage parent, EClass object, TestRemoteObject remote) {
			return false;
		}
	}

	@Test
	public void testRemoteKeyNoPullForceOnly() throws CoreException {
		TestManagerHarness harness = new TestManagerHarness(new TestNoPullForceOnlyFactory()) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 1");
			}
		};
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", null, "localKeyFor Object 1", null);
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(0, 0);
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", null, "localKeyFor Object 1", null);
		harness.consumer.retrieve(true);
		harness.listener.waitForResponse(1, 1);
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", "Remote Object 1", "localKeyFor Object 1",
				"Local Object 1");
	}

	class TestPullCreateOnlyFactory extends TestRemoteFactory {

		@Override
		public boolean isUpdateModelNeeded(EPackage parent, EClass object, TestRemoteObject remote) {
			return false;
		}
	}

	@Test
	public void testRemoteKeyNoUpdate() throws CoreException {
		TestManagerHarness harness = new TestManagerHarness(new TestPullCreateOnlyFactory()) {
			@Override
			RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> createConsumer() {
				return factory.getConsumerForRemoteKey(parent, "remoteKeyFor Object 1");
			}
		};
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", null, "localKeyFor Object 1", null);
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(1, 1);
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", "Remote Object 1", "localKeyFor Object 1",
				"Local Object 1");
		TestRemoteFactory.remote1.data = "newData";
		harness.consumer.retrieve(false);
		harness.listener.waitForResponse(2, 1);
		EClass modelObject = harness.consumer.getModelObject();
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", "Remote Object 1", "localKeyFor Object 1",
				"Local Object 1");
		assertThat(modelObject.getInstanceTypeName(), is("localKeyFor Object 1"));
		harness.consumer.retrieve(true);
		harness.listener.waitForResponse(3, 2);
		checkConsumer(harness.consumer, "remoteKeyFor Object 1", "Remote Object 1", "localKeyFor Object 1",
				"Local Object 1");
		assertThat(modelObject.getInstanceTypeName(), is("newData"));
	}
}
