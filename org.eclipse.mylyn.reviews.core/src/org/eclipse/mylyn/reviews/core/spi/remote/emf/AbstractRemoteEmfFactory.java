/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer.IObserver;

/**
 * Manages a set of model objects representing remote API analogs. While the factory can be accessed directly, generally
 * factory services should be requested from a consumer as this ensures that remote and local calls are handled
 * appropriately.
 * <p>
 * Factory users should usually obtain a fully managed {@link RemoteEmfConsumer} by calling the
 * {@link #consume(String, EObject, Object, IObserver)} method(s). They can then request pulls from the remote API and
 * be notified whenever the model objects are updated. A factory can support unlimited consumers.
 * </p>
 * <p>
 * Factory implementors should override the {@link AbstractRemoteEmfFactory#retrieve(Object, IProgressMonitor)},
 * {@link #create(EObject, Object)} and {@link #update(EObject, Object, Object)} methods as appropriate.
 * </p>
 * <p>
 * Typically, model objects are created using the {@link AbstractRemoteEmfFactory#retrieve(Object, IProgressMonitor)}
 * method. EMF objects can be also be obtained synchronously from an existing remote object using the
 * {@link #get(EObject, Object)} method. Remote objects can be obtained synchronously for appropriate remote keys using
 * {@link #retrieve(Object, IProgressMonitor)}.
 * </p>
 * 
 * @author Miles Parker
 */
public abstract class AbstractRemoteEmfFactory<EParentObjectType extends EObject, EObjectType, RemoteType, RemoteKeyType, LocalKeyType> {

	Map<EObjectType, RemoteType> remoteForObject = new HashMap<EObjectType, RemoteType>();

	Map<EObjectType, RemoteKeyType> remoteKeyForObject = new HashMap<EObjectType, RemoteKeyType>();

	Map<RemoteType, EObjectType> objectForRemote = new HashMap<RemoteType, EObjectType>();

	Map<LocalKeyType, EObjectType> objectForLocalKey = new HashMap<LocalKeyType, EObjectType>();

	Map<RemoteEmfConsumer.IObserver<EObjectType>, AdapterImpl> adapterForListener = new HashMap<IObserver<EObjectType>, AdapterImpl>();

	JobRemoteService service;

	EReference parentReference;

	EAttribute localAttribute;

	public AbstractRemoteEmfFactory(JobRemoteService service, EReference parentReference, EAttribute localAttribute) {
		this.service = service;
		this.parentReference = parentReference;
		this.localAttribute = localAttribute;
	}

	/**
	 * Returns the remote object that corresponds to a given model object.
	 * 
	 * @param object
	 *            A model object
	 * @return An object containing remotely derived state
	 */
	public RemoteType getRemoteObject(EObjectType object) {
		return remoteForObject.get(object);
	}

	/**
	 * Returns the model object that corresponds to a given remote API object.
	 * 
	 * @param object
	 *            A model object
	 * @return An object containing remotely derived state
	 */
	public EObjectType getModelObject(RemoteType remote) {
		return objectForRemote.get(remote);
	}

	/**
	 * Returns the remote object that matches a given model object within a given parent.
	 * 
	 * @param object
	 *            A model object
	 * @return An object containing remotely derived state
	 */
	public RemoteKeyType getRemoteKey(EParentObjectType parentObject, EObjectType object) {
		return remoteKeyForObject.get(object);
	}

	/**
	 * Returns the local object that matches a given local key. (This will be used to support local sharing of model
	 * resources.)
	 * 
	 * @param object
	 *            A model object
	 * @return An object containing remotely derived state
	 */
	public EObjectType getLocalModelObject(LocalKeyType localKey) {
		return objectForLocalKey.get(localKey);
	}

	/**
	 * Associates a remote object to it's model object. See {@link RemoteEmfConsumer#apply()}.
	 * 
	 * @param object
	 * @param remote
	 */
	void associateObjects(EObjectType object, RemoteType remote, LocalKeyType localKey, RemoteKeyType remoteKey) {
		remoteForObject.put(object, remote);
		objectForRemote.put(remote, object);
		objectForLocalKey.put(localKey, object);
		remoteKeyForObject.put(object, remoteKey);
	}

	/**
	 * Does the creation of an object require a call to the remote API? If false, no request job is created. True by
	 * default (safe case).
	 * 
	 * @return true by default
	 */
	public boolean isAsynchronous() {
		return true;
	}

	/**
	 * Override to perform request to remote API. This request is fully managed by remote service and could be invoked
	 * directly, but is typically invoked through a consumer.
	 * 
	 * @param remoteKey
	 *            A unique identifier in the target API
	 * @param monitor
	 * @return An object containing remotely derived state. (This might be a remote API object or a local object
	 *         containing remotly obtained data.)
	 * @throws CoreException
	 */
	protected abstract RemoteType retrieve(RemoteKeyType remoteKey, IProgressMonitor monitor) throws CoreException;

	/**
	 * Override to create an EObject from remote object. (Consumers should use
	 * {@link #get(EParentObjectType parent, RemoteType remoteObject)}, which ensures that any cached objects will be
	 * returned instead.)
	 * 
	 * @param parent
	 *            the parent EMF object that the new child object will be made a member of.
	 * @param remoteObject
	 *            the object representing the remote API request response
	 * @return a model object
	 */
	protected abstract EObjectType create(EParentObjectType parent, RemoteType remoteObject);

	/**
	 * Updates the values for the supplied EMF object based on any values that have changed in the remote object since
	 * the last call to {@link #retrieve(String, EObject, EReference, Object)} or {@link #update(Object)}. The object
	 * must have been previously retrieved using this factory.
	 * 
	 * @param object
	 *            a model object
	 * @return true if the object has changed, false if not
	 */
	public boolean update(EParentObjectType parent, EObjectType object, RemoteType remoteObject) {
		return false;
	}

	/**
	 * Returns a local object for the remote object, optionally creating a new object if one does not already exist.
	 * (Override {@link #create(EParentObjectType parent, RemoteType remoteObject)} to provide implementation for object
	 * creation.)
	 * 
	 * @param remoteObject
	 *            the object representing the remote API request response
	 * @return a model object
	 */
	public final EObjectType get(EParentObjectType parent, RemoteType remoteObject, LocalKeyType localKey,
			boolean create) {
		EObjectType object = getModelObject(remoteObject);
		if (object == null && localKey != null) {
			object = getLocalModelObject(localKey);
		}
		if (object == null && create) {
			object = create(parent, remoteObject);
			associateObjects(object, remoteObject, localKey, null);
		}
		return object;
	}

	/**
	 * Returns a local object for the remote object. (Override
	 * {@link #create(EParentObjectType parent, RemoteType remoteObject)} to provide implementation for object
	 * creation.)
	 * 
	 * @param remoteObject
	 *            the object representing the remote API request response
	 * @return a model object
	 */
	public final EObjectType get(EParentObjectType parent, RemoteType remoteObject, LocalKeyType localKey) {
		return get(parent, remoteObject, localKey, true);
	}

	/**
	 * Returns a local object for the remote object, optionally creating a new object if one does not already exist.
	 * (Override {@link #create(EParentObjectType parent, RemoteType remoteObject)} to provide implementation.)
	 * 
	 * @param remoteObject
	 *            the object representing the remote API request response
	 * @return a model object
	 */
	public final EObjectType get(EParentObjectType parent, RemoteType remoteObject) {
		return get(parent, remoteObject, null, true);
	}

	/**
	 * Factory method to create a new EMF object factor. This is the method that most factory consumers will be
	 * interested in. The method will asynchronously (as defined by remote service implementation):<li>
	 * <ol>
	 * Call the remote API, retrieving the results into a local (e.g. proxy) object representing the contents of the
	 * remote object
	 * </ol>
	 * <ol>
	 * Create a new EMF object and reference it from the supplied parent object.
	 * </ol>
	 * <ol>
	 * Notify the parent object via the EMF notification mechanism. (As a by-product of the above step.)
	 * </ol>
	 * </li>
	 * 
	 * @param description
	 *            a description of the purpose or context of this retrieval
	 * @param parent
	 *            the object to which the retrieved object will be added
	 * @param reference
	 *            the reference feature to use -- need not be a containment relation
	 * @param key
	 *            the key used by the remote API to identify the object
	 * @throws CoreException
	 */
	public RemoteEmfConsumer<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType> consume(
			String description, EParentObjectType parent, RemoteKeyType remoteKey, LocalKeyType localKey,
			RemoteEmfConsumer.IObserver<EObjectType> consumer) {
		return new RemoteEmfConsumer<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType>(
				description, this, parent, remoteKey, localKey, consumer);
	}

	public RemoteEmfConsumer<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType> consume(
			String description, EParentObjectType parent, EObjectType modelObject,
			RemoteEmfConsumer.IObserver<EObjectType> consumer) {
		return new RemoteEmfConsumer<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType>(
				description, this, parent, modelObject, consumer);
	}

	public EReference getParentReference() {
		return parentReference;
	}

	public EAttribute getLocalAttribute() {
		return localAttribute;
	}

	public JobRemoteService getService() {
		return service;
	}
}