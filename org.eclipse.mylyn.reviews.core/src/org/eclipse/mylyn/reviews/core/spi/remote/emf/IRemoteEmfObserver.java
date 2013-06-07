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
 * The observer receives notification of events that affect the consumer model objects. This is like an Asynchronous
 * callback, except that the notification occurs every time a remote change to the object occurs. Notifications can
 * occur across factories or through other remote update notifications.
 * <p>
 * Note that the observer notifications provide different semantics not supported by EMF notifications, such as a parent
 * add.
 * </p>
 */
public interface IRemoteEmfObserver<EParentObjectType extends EObject, EObjectType> {

	/**
	 * Called whenever a model object has been created from a remote object and added to a parent object.
	 * 
	 * @param parentObject
	 *            The parent of the supplied object
	 * @param modelObject
	 *            The newly created object
	 */
	void created(EParentObjectType parentObject, EObjectType modelObject);

	/**
	 * Called whenever a model object's value has been updated from the remote object. Unlike with Set and Add EMF
	 * notifications, updates are batched so that one and only one notification occurs for a model change after all
	 * values have been updated from the remote API. Updated is also called for newly created objects, so it typically
	 * isn't neccesary to listen for {@link #created(EObject, Object)} events.
	 * 
	 * @param parentObject
	 *            The parent of the supplied object
	 * @param modelObject
	 *            The updated object
	 */
	void updated(EParentObjectType parentObject, EObjectType modelObject, boolean modified);

	/**
	 * Called whenever a model object begins the update process, that is, after a call to
	 * {@link RemoteEmfConsumer#retrieve(boolean)}.
	 * 
	 * @param parentObject
	 *            The parent of the supplied object
	 * @param modelObject
	 *            The updating object
	 */
	void updating(EParentObjectType parentObject, EObjectType modelObject);

	/**
	 * Called whenever a failure has occurred while attempting to retrieve a remote object.
	 * 
	 * @param parentObject
	 *            The parent of the supplied object
	 * @param modelObject
	 *            The object for which the failure occurred
	 */
	void failed(EParentObjectType parentObject, EObjectType modelObject, IStatus status);

}