/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfObserver;

/**
 * Extends TestRemoteObserver so that it wraps a consumer and provides delegate methods for common operations.
 */
public class TestRemoteObserverConsumer<EParentObject extends EObject, EObjectType, LocalKey, Remote, RemoteKey, //
		ObjectCurrentType> extends TestRemoteObserver<EParentObject, EObjectType, LocalKey, ObjectCurrentType> {

	RemoteEmfConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> consumer;

	public static <EParentObject extends EObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> //
			TestRemoteObserverConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> //
			retrieveForLocalKey(
					AbstractRemoteEmfFactory<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> factory,
					EParentObject item, LocalKey localKey, boolean expectUpdate) {
		RemoteEmfConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> consumer //
				= factory.getConsumerForLocalKey(item, localKey);
		return retrieve(factory, consumer, expectUpdate);
	}

	public static <EParentObject extends EObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> //
			TestRemoteObserverConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> //
			retrieveForRemoteKey(
					AbstractRemoteEmfFactory<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> factory,
					EParentObject item, RemoteKey remoteKey, boolean expectUpdate) {
		RemoteEmfConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> consumer //
				= factory.getConsumerForRemoteKey(item, remoteKey);
		return retrieve(factory, consumer, expectUpdate);
	}

	private static <EParentObject extends EObject, EObjectType, RemoteKey, ObjectCurrentType, LocalKey, Remote> //
	TestRemoteObserverConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> retrieve(
			AbstractRemoteEmfFactory<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> factory,
			RemoteEmfConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> consumer,
			boolean expectUpdate) {
		TestRemoteObserverConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> observer //
				= create(factory, consumer);
		consumer.addObserver(observer);
		consumer.retrieve(false);
		observer.waitForResponse(expectUpdate);
		return observer;
	}

	public static <EParentObject extends EObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> //
			TestRemoteObserverConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> //
			create(AbstractRemoteEmfFactory<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> factory,
					RemoteEmfConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> consumer) {
		return new TestRemoteObserverConsumer<>(
				factory, consumer);
	}

	public TestRemoteObserverConsumer(
			AbstractRemoteEmfFactory<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> factory,
			RemoteEmfConsumer<EParentObject, EObjectType, LocalKey, Remote, RemoteKey, ObjectCurrentType> consumer) {
		super(factory);
		this.consumer = consumer;
	}

	public void addObserver(RemoteEmfObserver<EParentObject, EObjectType, LocalKey, ObjectCurrentType> observer) {
		consumer.addObserver(observer);
	}

	public void retrieve(boolean force) {
		consumer.retrieve(force);
	}

	public Remote getRemoteObject() {
		return consumer.getRemoteObject();
	}

	public EObjectType getModelObject() {
		return consumer.getModelObject();
	}
}
