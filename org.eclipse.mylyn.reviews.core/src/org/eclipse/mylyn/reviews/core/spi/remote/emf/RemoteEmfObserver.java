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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

/**
 * A concrete implementation of {@link IRemoteEmfObserver}, providing a number of convenience methods for managing
 * consumer interaction with observers.
 * 
 * @author Miles Parker
 */
public class RemoteEmfObserver<EParentObjectType extends EObject, EObjectType> implements
		IRemoteEmfObserver<EParentObjectType, EObjectType> {

	RemoteEmfConsumer<EParentObjectType, EObjectType, ?, ?, ?> consumer;

	/**
	 * Constructs an observer that listens to the supplied consumer.
	 * 
	 * @param consumer
	 */
	public RemoteEmfObserver(RemoteEmfConsumer<EParentObjectType, EObjectType, ?, ?, ?> consumer) {
		setConsumer(consumer);
	}

	/**
	 * Constructs an observer.
	 */
	public RemoteEmfObserver() {
	}

	public void created(EParentObjectType parentObject, EObjectType modelObject) {
	}

	public void updating(EParentObjectType parent, EObjectType object) {
	}

	public void updated(EParentObjectType parentObject, EObjectType modelObject, boolean modified) {
	}

	public void failed(EParentObjectType parentObject, EObjectType modelObject, IStatus status) {
	}

	/**
	 * Returns the consumer the observer is listening to. This value may be null if the observer was added directly to
	 * the consumer.
	 */
	public RemoteEmfConsumer<EParentObjectType, EObjectType, ?, ?, ?> getConsumer() {
		return consumer;
	}

	/**
	 * Sets the consumer for the given observer, adding itself to the supplied consumer and removing it from an existing
	 * consumer if any. This supports reuse of an observer when the underlying model object changes.
	 * 
	 * @param consumer
	 */
	public void setConsumer(RemoteEmfConsumer<EParentObjectType, EObjectType, ?, ?, ?> consumer) {
		if (this.consumer != consumer) {
			consumer.addObserver(this);
		}
	}

	/**
	 * Non-API. Intended for use by consumer only.
	 */
	void internalSetConsumer(RemoteEmfConsumer<EParentObjectType, EObjectType, ?, ?, ?> consumer) {
		this.consumer = consumer;
	}

	public void dispose() {
		if (consumer != null) {
			consumer.removeObserver(this);
		}
	}
}