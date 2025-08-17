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
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import org.eclipse.emf.ecore.EObject;

/**
 * A concrete implementation of {@link RemoteEmfObserver}, providing a number of convenience methods for managing consumer interaction with
 * observers.
 *
 * @author Miles Parker
 */
public class RemoteEmfObserver<EParentObjectType extends EObject, EObjectType, LocalKeyType, ObjectCurrentType> {

	RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, ?, ?, ObjectCurrentType> consumer;

	/**
	 * Constructs an observer that listens to the supplied consumer.
	 *
	 * @param consumer
	 */
	public RemoteEmfObserver(
			RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, ?, ?, ObjectCurrentType> consumer) {
		setConsumer(consumer);
	}

	/**
	 * Constructs an observer.
	 */
	public RemoteEmfObserver() {
	}

	public void updating() {
	}

	public void updated(boolean modified) {
	}

	/**
	 * Returns the consumer the observer is listening to. This value may be null if the observer was added directly to the consumer.
	 */
	public RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, ?, ?, ObjectCurrentType> getConsumer() {
		return consumer;
	}

	/**
	 * Sets the consumer for the given observer, adding itself to the supplied consumer and removing it from an existing consumer if any.
	 * This supports reuse of an observer when the underlying model object changes.
	 *
	 * @param consumer
	 */
	public void setConsumer(
			RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, ?, ?, ObjectCurrentType> consumer) {
		if (this.consumer != consumer) {
			consumer.addObserver(this);
		}
	}

	/**
	 * Non-API. Intended for use by consumer only.
	 */
	void internalSetConsumer(
			RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, ?, ?, ObjectCurrentType> consumer) {
		this.consumer = consumer;
	}

	public void dispose() {
		if (consumer != null) {
			consumer.removeObserver(this);
		}
	}
}