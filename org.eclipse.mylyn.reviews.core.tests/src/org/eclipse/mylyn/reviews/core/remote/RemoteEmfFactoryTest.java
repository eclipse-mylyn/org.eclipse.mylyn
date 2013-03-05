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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.junit.Test;

/**
 * @author Miles Parker
 */
public class RemoteEmfFactoryTest {

	private static final int TEST_TIMEOUT = 100;

	private final class TestListener<T> implements RemoteEmfConsumer.IObserver<T> {

		T createdObject;

		int updated;

		int responded;

		IStatus failure;

		AbstractRemoteEmfFactory<?, ?, ?, ?, ?> factory;

		private TestListener(AbstractRemoteEmfFactory<?, ?, ?, ?, ?> factory) {
			this.factory = factory;
		}

		public void created(T object) {
			createdObject = object;
		}

		public void responded(boolean modified) {
			responded++;
			if (modified) {
				updated++;
			}
		}

		public void failed(org.eclipse.core.runtime.IStatus status) {
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

	class TestRemoteObject {
		String name;

		String data;

		private TestRemoteObject(String name) {
			this.name = name;
		}
	}

	class TestRemoteObjectManyObject {
		String name;

		String data;

		private TestRemoteObjectManyObject(String name) {
			this.name = name;
		}
	}

	TestRemoteObject remote1 = new TestRemoteObject("Object 1");

	TestRemoteObject remote2 = new TestRemoteObject("Object 2");

	Map<String, TestRemoteObject> remoteForKey = new HashMap<String, TestRemoteObject>();

	class TestRemoteFactory extends AbstractRemoteEmfFactory<EPackage, EClass, TestRemoteObject, String, String> {

		public TestRemoteFactory() {
			super(new JobRemoteService(), EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
					EcorePackage.Literals.ENAMED_ELEMENT__NAME);
			remoteForKey.put("object1", remote1);
			remoteForKey.put("object2", remote2);
		}

		@Override
		public TestRemoteObject retrieve(String remoteKey, IProgressMonitor monitor) throws CoreException {
			return remoteForKey.get(remoteKey);
		}

		@Override
		protected EClass create(EPackage parent, TestRemoteObject remoteObject) {
			EClass clazz = EcoreFactory.eINSTANCE.createEClass();
			clazz.setName(remoteObject.name);
			return clazz;
		}

		@Override
		public boolean update(EPackage parent, EClass object, TestRemoteObject remoteObject) {
			object.setInstanceTypeName(remoteObject.data);
			return true;
		}
	}

	class TestRemoteFactoryNoUpdate extends TestRemoteFactory {

		@Override
		public boolean update(EPackage parent, EClass object, TestRemoteObject remoteObject) {
			return false;
		}
	}

	class TestRemoteFactoryCollectionObject extends
			AbstractRemoteEmfFactory<EPackage, List<EClassifier>, TestRemoteObject, String, String> {

		public TestRemoteFactoryCollectionObject() {
			super(new JobRemoteService(), EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
					EcorePackage.Literals.ENAMED_ELEMENT__NAME);
			remoteForKey.put("object1", remote1);
			remoteForKey.put("object2", remote2);
		}

		@Override
		public TestRemoteObject retrieve(String remoteKey, IProgressMonitor monitor) throws CoreException {
			return remoteForKey.get(remoteKey);
		}

		@Override
		protected List<EClassifier> create(EPackage parent, TestRemoteObject remoteObject) {
			List<EClassifier> classifiers = parent.getEClassifiers();
			EClass class1 = EcoreFactory.eINSTANCE.createEClass();
			class1.setName("Many Class 1");
			classifiers.add(class1);
			EClass class2 = EcoreFactory.eINSTANCE.createEClass();
			class2.setName("Many Class 2");
			classifiers.add(class2);
			return classifiers;
		}

		@Override
		public boolean update(EPackage parent, List<EClassifier> classifiers, TestRemoteObject remoteObject) {
			EClass class2 = EcoreFactory.eINSTANCE.createEClass();
			class2.setName("Many Class " + (classifiers.size() + 1));
			classifiers.add(class2);
			return true;
		}
	}

	IStatus errorStatus = new Status(IStatus.ERROR, "blah", "Blah");

	class TestFailureFactory extends TestRemoteFactory {
		@Override
		public TestRemoteObject retrieve(String remoteKey, IProgressMonitor monitor) throws CoreException {
			throw new CoreException(errorStatus);
		}
	}

	@Test
	public void testRemoteProcessCreate() {
		TestRemoteFactory testRemoteFactory = new TestRemoteFactory();
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		EClass create1 = testRemoteFactory.create(parent, remote1);
		assertThat(create1.getName(), is("Object 1"));
		EClass create2 = testRemoteFactory.create(parent, remote2);
		assertThat(create2.getName(), is("Object 2"));
	}

	@Test
	public void testRemoteProcessGet() {
		TestRemoteFactory testRemoteFactory = new TestRemoteFactory();
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		EClass get2 = testRemoteFactory.get(parent, remote2);
		assertThat(get2.getName(), is("Object 2"));
		EClass get1 = testRemoteFactory.get(parent, remote1);
		assertThat(get1.getName(), is("Object 1"));

		EClass get1Again = testRemoteFactory.get(parent, remote1);
		assertThat(get1Again.getName(), is("Object 1"));
	}

	@Test
	public void testRemoteProcessRequest() throws CoreException {
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();

		TestRemoteFactory factory = new TestRemoteFactory();
		TestListener<EClass> testListener = new TestListener<EClass>(factory);
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consume = factory.consume("Test Process",
				parent, "object1", null, testListener);
		consume.request();
		testListener.waitForResponse(1, 1);
		assertThat(testListener.createdObject, notNullValue());
		EClass modelObject = factory.getModelObject(remote1);
		assertThat(modelObject.getName(), is("Object 1"));
		TestRemoteObject remoteObject = factory.getRemoteObject(modelObject);
		assertThat(remoteObject, sameInstance(remote1));
		assertThat(testListener.createdObject.getInstanceTypeName(), nullValue());
	}

	@Test
	public void testRemoteProcessUpdate() throws CoreException {
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();

		TestRemoteFactory factory = new TestRemoteFactory();
		TestListener<EClass> testListener = new TestListener<EClass>(factory);
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consume = factory.consume("Test Process",
				parent, "object1", null, testListener);
		consume.request();
		testListener.waitForResponse(1, 1);
		remote1.data = "new";
		consume.request();
		testListener.waitForResponse(2, 2);
		EClass modelObject = factory.getModelObject(remote1);
		assertThat(modelObject, sameInstance(consume.getModelObject()));
		assertThat(modelObject.getInstanceTypeName(), is("new"));
	}

	@Test
	public void testRemoteProcessRequestUpdateNoModification() throws CoreException {
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		TestRemoteFactory factory = new TestRemoteFactoryNoUpdate();
		TestListener<EClass> testListener = new TestListener<EClass>(factory);
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consume = factory.consume("Test Process",
				parent, "object1", null, testListener);
		consume.request();
		testListener.waitForResponse(1, 1);
		assertThat(testListener.createdObject, notNullValue());
		EClass modelObject = factory.getModelObject(remote1);
		assertThat(modelObject.getName(), is("Object 1"));
		TestRemoteObject remoteObject = factory.getRemoteObject(modelObject);
		assertThat(remoteObject, sameInstance(remote1));
		assertThat(testListener.createdObject.getInstanceTypeName(), nullValue());
		consume.request();
		testListener.waitForResponse(2, 1);
	}

	@Test
	public void testRemoteProcessCollectionRequestAndUpdate() throws CoreException {
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		TestRemoteFactoryCollectionObject factory = new TestRemoteFactoryCollectionObject();
		TestListener<List<EClassifier>> testListener = new TestListener<List<EClassifier>>(factory);
		RemoteEmfConsumer<EPackage, List<EClassifier>, TestRemoteObject, String, String> consume = factory.consume(
				"Test Process", parent, null, null, testListener);
		consume.request();
		testListener.waitForResponse(1, 1);
		assertThat(testListener.createdObject, notNullValue());
		List<EClassifier> modelObject = consume.getModelObject();
		assertThat(modelObject.size(), is(3));
		assertThat(modelObject.get(0).getName(), is("Many Class 1"));
		assertThat(modelObject.get(1).getName(), is("Many Class 2"));
		assertThat(modelObject.get(2).getName(), is("Many Class 3"));

		consume.request();
		testListener.waitForResponse(2, 2);
		consume.request();
		testListener.waitForResponse(3, 3);
		assertThat(modelObject.size(), is(5));
		assertThat(modelObject.get(3).getName(), is("Many Class 4"));
		assertThat(modelObject.get(4).getName(), is("Many Class 5"));
	}

	@Test
	public void testRemoteProcessConsumerVsSubscriber() throws CoreException {
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();

		TestRemoteFactory factory = new TestRemoteFactory();
		TestListener<EClass> testListener1 = new TestListener<EClass>(factory);
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consumer1 = factory.consume(
				"Test Process", parent, "object1", null, testListener1);
		TestListener<EClass> testListener2 = new TestListener<EClass>(factory);
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consumer2 = factory.consume(
				"Test Process", parent, "object1", null, testListener2);
		consumer1.request();
		testListener1.waitForResponse(1, 1);
		assertThat(testListener1.createdObject.getName(), is("Object 1"));
		assertThat(consumer1.getModelObject().getName(), is("Object 1"));
		assertThat(consumer2.getModelObject(), nullValue());

		consumer2.subscribe();
		assertThat(testListener2.createdObject, nullValue());
		assertThat(consumer2.getModelObject().getName(), is("Object 1"));
	}

	@Test
	public void testRemoteProcessMultipleConsumers() throws CoreException {
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();

		TestRemoteFactory factory = new TestRemoteFactoryNoUpdate();
		TestListener<EClass> testListener1 = new TestListener<EClass>(factory);
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consumer1 = factory.consume(
				"Test Process", parent, "object1", null, testListener1);
		consumer1.request();
		testListener1.waitForResponse(1, 1);
		assertThat(testListener1.createdObject.getName(), is("Object 1"));
		assertThat(consumer1.getModelObject().getName(), is("Object 1"));
		assertThat(factory.getService().isActive(), not(true));

		TestListener<EClass> testListener2 = new TestListener<EClass>(factory);
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consumer2 = factory.consume(
				"Test Process", parent, "object1", null, testListener2);
		assertThat(consumer2.getModelObject(), nullValue());
		consumer2.request();
		testListener1.waitForResponse(2, 1);
		testListener2.waitForResponse(1, 0);
		assertThat(factory.getService().isActive(), not(true));
		assertThat(testListener2.createdObject, nullValue());
		assertThat(consumer2.getModelObject(), sameInstance(consumer1.getModelObject()));
		consumer1.request();
		testListener1.waitForResponse(3, 1);
		testListener2.waitForResponse(2, 0);
	}

	@Test
	public void testRemoteProcessFailure() throws CoreException {
		EPackage parent = EcoreFactory.eINSTANCE.createEPackage();
		TestRemoteFactory factory = new TestFailureFactory();
		TestListener<EClass> testListener = new TestListener<EClass>(factory);
		RemoteEmfConsumer<EPackage, EClass, TestRemoteObject, String, String> consume = factory.consume("Test Process",
				parent, "object1", null, testListener);
		consume.request();
		testListener.waitForFailure();
		assertThat(testListener.failure, is(errorStatus));
	}

}
