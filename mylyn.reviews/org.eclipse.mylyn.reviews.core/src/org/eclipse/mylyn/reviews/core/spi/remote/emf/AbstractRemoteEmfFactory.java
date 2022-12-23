/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteService;

/**
 * Manages a set of model objects representing remote API analogs. While the factory can be accessed directly, generally
 * factory services should be requested from a consumer as this ensures that remote and local calls are handled
 * appropriately.
 * <p>
 * Factory users should usually obtain a fully managed {@link RemoteEmfConsumer} by calling the {@link #getConsumer()}
 * method(s). They can then request model object creation, updates and retrieval from the consumer. Every model object
 * can have one and only one consumer, even if that consumer was first obtained from the factory using only a remote key
 * or object. This allows consumers to safely use a single consumer throughout the remote and local model object
 * life-cycle and to obtain a consumer at any point. Consumers do not need to be disposed or managed explicitly.
 * </p>
 * <p>
 * Factory implementors should override the {@link AbstractRemoteEmfFactory#pull(EObject, Object, IProgressMonitor)},
 * {@link #createModel(EObject, Object)} and {@link #updateModel(EObject, Object, Object)} methods as appropriate.
 * </p>
 * <p>
 * Typically, model objects are created using the {@link AbstractRemoteEmfFactory#createModel(EObject, Object)} method.
 * EMF objects can be also be obtained synchronously from an existing remote object using the
 * {@link #get(EObject, Object)} method. Remote objects can be obtained synchronously for appropriate remote keys using
 * {@link #pull(EObject, Object, IProgressMonitor)}.
 * </p>
 * 
 * @author Miles Parker
 */
public abstract class AbstractRemoteEmfFactory<EParentObjectType extends EObject, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> {

	class UniqueLocalReference<P, L> {
		P parent;

		L localKey;

		UniqueLocalReference(P parent, L localKey) {
			Assert.isLegal(parent != null, "Internal Exception: Parent must be specified."); //$NON-NLS-1$
			Assert.isLegal(localKey != null, "Internal Exception: Local key must be specified."); //$NON-NLS-1$
			this.parent = parent;
			this.localKey = localKey;
		}

		@Override
		public boolean equals(Object object) {
			if (object instanceof AbstractRemoteEmfFactory.UniqueLocalReference) {
				@SuppressWarnings("rawtypes")
				UniqueLocalReference reference = (UniqueLocalReference) object; //Cannot test for generic types because of erasure
				return parent.equals(reference.parent) && localKey.equals(reference.localKey);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return parent.hashCode() + 31 * localKey.hashCode();
		}
	}

	private final Map<UniqueLocalReference<EParentObjectType, LocalKeyType>, RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType>> consumerForLocalKey = new HashMap<UniqueLocalReference<EParentObjectType, LocalKeyType>, RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType>>();

	private final EReference parentReference;

	private final EAttribute localKeyAttribute;

	private final AbstractRemoteEmfFactoryProvider<?, ?> factoryProvider;

	/**
	 * Constructs the factory.
	 * 
	 * @param factoryProvider
	 *            The associated factory provider
	 * @param parentReference
	 *            The EMF reference in the parent object that points to model objects; must be available for all parent
	 *            object instances, but need not be a containment reference assuming persistence is managed separately
	 * @param localKeyAttribute
	 *            The EMF attribute specifying the local key; must be available for all model object instance
	 */
	public AbstractRemoteEmfFactory(AbstractRemoteEmfFactoryProvider<?, ?> factoryProvider, EReference parentReference,
			EAttribute localKeyAttribute) {
		this.factoryProvider = factoryProvider;
		this.parentReference = parentReference;
		this.localKeyAttribute = localKeyAttribute;
	}

	/**
	 * Returns a unique consumer for a model object that corresponds to a given remote API key. May be called from any
	 * thread.
	 * 
	 * @param parentObject
	 *            The object that contains or will contain the remote object type
	 * @return A key used for locating the remote object from remote API. That object does not have to exist on the
	 *         remote API yet, provided appropriate remote key to local key mappings are provided.
	 */
	public RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> getConsumerForRemoteKey(
			EParentObjectType parentObject, RemoteKeyType remoteKey) {
		LocalKeyType localKey = getLocalKeyForRemoteKey(remoteKey);
		synchronized (consumerForLocalKey) {
			RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> consumer = null;
			if (localKey != null) {
				consumer = getConsumerForLocalKey(parentObject, localKey);
			}
			if (consumer == null) {
				consumer = new RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType>(
						this, parentObject, null, localKey, null, remoteKey);
				assignConsumer(parentObject, localKey, consumer);
			} else {
				consumer.setRemoteKey(remoteKey);
			}
			return consumer;
		}
	}

	/**
	 * Returns unique consumer for a model object that corresponds to a given remote API object. May be called from any
	 * thread.
	 * 
	 * @param parentObject
	 *            The object that contains or will contain the remote object type
	 * @return An object containing remotely derived state
	 */
	public RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> getConsumerForRemoteObject(
			EParentObjectType parentObject, RemoteType remoteObject) {
		synchronized (consumerForLocalKey) {
			RemoteKeyType remoteKey = getRemoteKey(remoteObject);
			LocalKeyType localKey = getLocalKeyForRemoteKey(remoteKey);
			RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> consumer = findConsumer(
					parentObject, localKey);
			if (consumer == null) {
				consumer = new RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType>(
						this, parentObject, null, localKey, remoteObject, remoteKey);
				assignConsumer(parentObject, localKey, consumer);
			} else {
				consumer.setRemoteObject(remoteObject);
			}
			return consumer;
		}
	}

	/**
	 * Returns unique consumer for a model object that matches a given local key. <em>Must be called from EMF safe (e.g.
	 * UI) thread.</em>
	 * 
	 * @param parentObject
	 *            The object that contains or will contain the remote object type
	 * @return A key used for locating the model object from within the model parent object (Typically an EMF id). The
	 *         actual matching model object does not have to exist yet. It might for example be created as part of a
	 *         subsequent retrieval based on a matching remote key.
	 * @return An object containing remotely derived state
	 */
	public RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> getConsumerForLocalKey(
			EParentObjectType parentObject, LocalKeyType localKey) {
		RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> consumer = null;
		synchronized (consumerForLocalKey) {
			consumer = findConsumer(parentObject, localKey);
		}
		if (consumer == null) {
			EObjectType eo = open(parentObject, localKey);
			synchronized (consumerForLocalKey) {
				if (eo != null) {
					EObjectType modelObject = eo;
					consumer = new RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType>(
							this, parentObject, modelObject, localKey, null, null);
					assignConsumer(parentObject, localKey, consumer);
				}
				if (consumer == null) {
					consumer = new RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType>(
							this, parentObject, null, localKey, null, null);
					assignConsumer(parentObject, localKey, consumer);
				}
			}
		}
		return consumer;
	}

	class ObjectFinder implements Runnable {
		EObjectType foundObject;

		EParentObjectType parentObject;

		LocalKeyType localKey;

		private ObjectFinder(EParentObjectType parentObject, LocalKeyType localKey) {
			super();
			this.parentObject = parentObject;
			this.localKey = localKey;
		}

		@Override
		public void run() {
			Object parentField = parentObject.eGet(parentReference);
			if (parentField instanceof List<?>) {
				List<?> members = (List<?>) parentField;
				for (Object object : members) {
					if (object instanceof EObject) {
						LocalKeyType currentKey = getLocalKey(parentObject, (EObjectType) object);
						if (currentKey != null && localKey.equals(currentKey)) {
							foundObject = (EObjectType) object;
							break;
						}
					}
				}
			}
		}
	}

	protected EObjectType open(EParentObjectType parentObject, LocalKeyType localKey) {
		ObjectFinder finder = new ObjectFinder(parentObject, localKey);
		getService().modelExec(finder, true);
		return finder.foundObject;
	}

	/**
	 * Returns a unique consumer for a model object. <em>Must be called from EMF safe (e.g. UI) thread.</em>
	 * 
	 * @param parentObject
	 *            The object that contains the model object
	 * @param modelObject
	 *            The model object itself. Must currently exist in the parent.
	 * @return A key used for locating the model object from within the model parent object (Typically an EMF id)
	 * @return An object containing remotely derived state
	 */
	public RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> getConsumerForModel(
			EParentObjectType parentObject, EObjectType modelObject) {
		synchronized (consumerForLocalKey) {
			RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> consumer = null;
			LocalKeyType localKey = getLocalKey(parentObject, modelObject);
			if (localKey != null) {
				consumer = findConsumer(parentObject, localKey);
			}
			if (consumer == null) {
				consumer = new RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType>(
						this, parentObject, modelObject, localKey, null, null);
				assignConsumer(parentObject, localKey, consumer);
			}
			return consumer;
		}
	}

	private RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> findConsumer(
			EParentObjectType parentObject, LocalKeyType localKey) {
		UniqueLocalReference<EParentObjectType, LocalKeyType> key = new UniqueLocalReference<EParentObjectType, LocalKeyType>(
				parentObject, localKey);
		return consumerForLocalKey.get(key);
	}

	private RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> assignConsumer(
			EParentObjectType parentObject, LocalKeyType localKey,
			RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> consumer) {
		UniqueLocalReference<EParentObjectType, LocalKeyType> key = new UniqueLocalReference<EParentObjectType, LocalKeyType>(
				parentObject, localKey);
		return consumerForLocalKey.put(key, consumer);
	}

	public void removeConsumer(
			RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> consumer) {
		EParentObjectType parentObject = consumer.getParentObject();
		LocalKeyType localKey = consumer.getLocalKey();
		if (parentObject != null && localKey != null) {
			UniqueLocalReference<EParentObjectType, LocalKeyType> key = new UniqueLocalReference<EParentObjectType, LocalKeyType>(
					parentObject, localKey);
			consumerForLocalKey.remove(key);
		}
	}

	@SuppressWarnings("unchecked")
	public LocalKeyType getLocalKey(EParentObjectType parentObject, EObjectType modelObject) {
		if (modelObject instanceof EObject) {
			EObject eObject = (EObject) modelObject;
			return (LocalKeyType) eObject.eGet(getLocalKeyAttribute()); //Cannot test for type because of erasure
		}
		return null;
	}

	/**
	 * Override to infer a local key from the remote object. Should not usually need to be overridden -- by default
	 * returns the local key matching the remote key for the supplied object.
	 * 
	 * @param remoteObject
	 *            The remote object to obtain the local key from
	 */
	public LocalKeyType getLocalKeyForRemoteObject(RemoteType remoteObject) {
		return getLocalKeyForRemoteKey(getRemoteKey(remoteObject));
	}

	/**
	 * Override to infer a local key from a remote key. This method must be properly implemented with a one to one
	 * mapping in order for consumers to function correctly.
	 * 
	 * @param remoteKey
	 *            The remote key to obtain the local key from
	 */
	public abstract LocalKeyType getLocalKeyForRemoteKey(RemoteKeyType remoteKey);

	/**
	 * Returns the remote object that matches a given model object within a given parent.
	 * 
	 * @param object
	 *            A model object
	 * @return An object containing remotely derived state
	 */
	public abstract RemoteKeyType getRemoteKey(RemoteType remoteObject);

	/**
	 * Override to infer a remote key from a local key. This method is optional but should be supplied whenever it is
	 * possible to infer the remote key.
	 * 
	 * @param localKey
	 *            The local key to discover remote key from, null if unable to infer one
	 */
	public RemoteKeyType getRemoteKeyForLocalKey(EParentObjectType parentObject, LocalKeyType localKey) {
		RemoteType remoteObject = getRemoteObjectForLocalKey(parentObject, localKey);
		if (remoteObject != null) {
			return getRemoteKey(remoteObject);
		}
		return null;
	}

	/**
	 * Override to find a remote object from a local obejct. This method is optional but should be supplied whenever it
	 * is possible to infer the remote key.
	 * 
	 * @param localKey
	 *            The local key to discover remote key from, null if unable to infer one
	 */
	public RemoteType getRemoteObjectForLocalKey(EParentObjectType parentObject, LocalKeyType localKey) {
		return null;
	}

	/**
	 * Returns true if the creation of an object requires a call to the remote API. If false, no request job is created.
	 * True by default (safe case).
	 * 
	 * @return true by default
	 */
	public boolean isAsynchronous() {
		return true;
	}

	/**
	 * Override to perform request to remote API. This request is fully managed by remote service and could be invoked
	 * directly, but is typically invoked through a consumer. <em>This method may block or fail, and must not be called
	 * from UI thread.</em>
	 * 
	 * @param parentObject
	 *            The object that contains the model object
	 * @param remoteKey
	 *            A unique identifier in the target API
	 * @param monitor
	 * @return An object containing remotely derived state. (This might be a remote API object itself or any other
	 *         object containing remotely obtained data.)
	 * @throws CoreException
	 */
	public abstract RemoteType pull(EParentObjectType parent, RemoteKeyType remoteKey, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Override to return true if the remote object state should be requested from the remote API. Override to return
	 * true if there is no way to check the remote model object state without retrieving the whole object. The default
	 * implementation is sufficient if the remote state is immutable -- that is, if the update method is not implemented
	 * at all.
	 * 
	 * @param parentObject
	 *            The object that contains the model object
	 * @param modelObject
	 *            The model object to test
	 * @param remoteObject
	 *            A unique identifier in the target API
	 * @param monitor
	 * @return
	 */
	public boolean isPullNeeded(EParentObjectType parent, EObjectType object, RemoteType remote) {
		return object == null || remote == null;
	}

	/**
	 * Override to create an EObject from remote object. (Consumers should use
	 * {@link #get(EParentObjectType parent, RemoteType remoteObject)}, which ensures that any cached objects will be
	 * returned instead.) <em>Must be called from EMF safe (e.g. UI) thread and should have very fast execution
	 * time.</em>
	 * 
	 * @param parentObject
	 *            the parent EMF object that the new child object will be referenced from
	 * @param remoteObject
	 *            the object representing the remote API request response
	 * @return a model object
	 */
	protected abstract EObjectType createModel(EParentObjectType parentObject, RemoteType remoteObject);

	/**
	 * Override to return true if a remote object should be created.
	 * 
	 * @param parentObject
	 *            The object that contains the model object
	 * @param modelObject
	 *            The model object to test
	 * @param remoteObject
	 *            A unique identifier in the target API
	 * @param monitor
	 * @return
	 */
	public boolean isCreateModelNeeded(EParentObjectType parentObject, EObjectType modelObject) {
		return modelObject == null;
	}

	/**
	 * Updates the values for the supplied EMF object based on any values that have changed in the remote object since
	 * the last call to {@link #retrieve(String, EObject, EReference, Object)} or {@link #update(Object)}. The object
	 * must have been previously retrieved using this factory. <em>Must be called from EMF safe (e.g. UI) thread and
	 * should have very fast execution time.</em>
	 * 
	 * @param parentObject
	 *            the parent EMF object that the new child object is referenced from
	 * @param modelObject
	 *            The model object to update -- must currently exist in the parent
	 * @return true if the object has changed or the object delta is unknown, false otherwise
	 */
	public boolean updateModel(EParentObjectType parentObject, EObjectType modelObject, RemoteType remoteObject) {
		return false;
	}

	/**
	 * Override to return true if a remote object to model object update should occur, e.g. when the remote object state
	 * is more recent then the model object state. Return true by default and generally doesn't need to be overridden as
	 * most update operations should be inexpensive.
	 * 
	 * @param parentObject
	 *            The object that contains the model object
	 * @param modelObject
	 *            The model object to test
	 * @param remoteObject
	 *            A unique identifier in the target API
	 * @param monitor
	 * @return
	 */
	public boolean isUpdateModelNeeded(EParentObjectType parentObject, EObjectType modelObject, RemoteType remote) {
		return true;
	}

	/**
	 * Returns the model object for the supplied key(s), assuming that model object has already been created. This
	 * method can be called from any thread, and does not require any interaction with the remote server or local model
	 * object.
	 * 
	 * @param remoteObject
	 *            the object representing the remote API request response
	 * @return a model object
	 */
	public final EObjectType get(EParentObjectType parentObject, LocalKeyType localKey, RemoteKeyType remoteKey) {
		RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> consumer = null;
		if (localKey != null) {
			consumer = getConsumerForLocalKey(parentObject, localKey);
		}
		if (consumer == null && remoteKey != null) {
			consumer = getConsumerForRemoteKey(parentObject, remoteKey);
		}
		if (consumer != null) {
			return consumer.getModelObject();
		}
		return null;
	}

	/**
	 * Returns the EMF reference in the parent object that refers to model objects. Returns the EMF attribute specifying
	 * the local key.
	 * 
	 * @return
	 */
	public EReference getParentReference() {
		return parentReference;
	}

	/**
	 * Returns the EMF attribute specifying the local key.
	 * 
	 * @return
	 */
	public EAttribute getLocalKeyAttribute() {
		return localKeyAttribute;
	}

	/**
	 * Returns the service used to execute model operations as supplied by the factory provider.
	 * 
	 * @return
	 */
	public AbstractRemoteService getService() {
		return getFactoryProvider().getService();
	}

	/**
	 * Returns the parent factory provider that provides this factory.
	 */
	public AbstractRemoteEmfFactoryProvider<?, ?> getFactoryProvider() {
		return factoryProvider;
	}

	public String getModelDescription(EParentObjectType parentObject, EObjectType object, LocalKeyType localKey) {
		return getParentReference().getEReferenceType().getName() + " " + localKey; //$NON-NLS-1$
	}

}
