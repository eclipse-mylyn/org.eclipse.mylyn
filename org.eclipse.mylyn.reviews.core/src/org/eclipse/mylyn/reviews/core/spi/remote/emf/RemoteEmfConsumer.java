/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson and others.
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

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteConsumer;
import org.eclipse.osgi.util.NLS;

/**
 * Manages the interaction between a remote API and a local EMF object. There can be only one instance of a consumer for
 * a given model object or remote object per factory.
 * <p>
 * After obtaining a consumer using one of the {@link RemoteEmfConsumer} <i>AbstractRemoteEmfFactory#getConsumer()</i>
 * methods, call {@link RemoteEmfConsumer#retrieve(boolean)} to request an update. Any registered
 * {@link RemoteEmfObserver}s will then receive an {@link RemoteEmfObserver#updated(boolean)} event regardless of
 * whether or not the actual state changed.
 *
 * @author Miles Parker
 */
public class RemoteEmfConsumer<EParentObjectType extends EObject, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType>
		extends AbstractRemoteConsumer {

	private final AbstractRemoteEmfFactory<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> factory;

	private RemoteKeyType remoteKey;

	private RemoteType remoteObject;

	private final EParentObjectType parentObject;

	private EObjectType modelObject;

	private LocalKeyType localKey;

	private final Collection<RemoteEmfObserver<EParentObjectType, EObjectType, LocalKeyType, ObjectCurrentType>> remoteEmfObservers;

	private boolean pulling;

	private boolean retrieving;

	boolean userJob;

	boolean systemJob;

	//Can set to false for running with in another job
	boolean asynchronous = true;

	private IStatus lastStatus = Status.OK_STATUS;

	RemoteEmfConsumer(
			AbstractRemoteEmfFactory<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> factory,
			final EParentObjectType parent, final EObjectType modelObject, LocalKeyType localKey,
			RemoteType remoteObject, RemoteKeyType remoteKey) {
		this.parentObject = parent;
		this.modelObject = modelObject;
		this.remoteObject = remoteObject;
		this.remoteKey = remoteKey;
		this.localKey = localKey;
		this.factory = factory;
		if (remoteKey == null && remoteObject != null) {
			this.remoteKey = factory.getRemoteKey(remoteObject);
		}
		if (localKey == null && modelObject != null) {
			this.localKey = factory.getLocalKey(null, modelObject);
		}
		remoteEmfObservers = new CopyOnWriteArrayList<RemoteEmfObserver<EParentObjectType, EObjectType, LocalKeyType, ObjectCurrentType>>();
	}

	public void notifyObservers(final RemoteNotification notification) {
		getFactory().getService().modelExec(new Runnable() {
			@Override
			public void run() {
				for (RemoteEmfObserver<EParentObjectType, EObjectType, LocalKeyType, ObjectCurrentType> listener : remoteEmfObservers) {
					switch (notification.getType()) {
					case RemoteNotification.REMOTE_UPDATE:
						listener.updated(notification.isModification());
						break;
					case RemoteNotification.REMOTE_UPDATING:
						listener.updating();
						break;
					}
				}
			}
		});
	}

	/**
	 * Pulls the results from the factory, populating the remote object with the latest state from the remote API.
	 * Blocks until the remote API call completes. Does nothing if a retrieval is already occurring. <em>This method
	 * must not be called from the UI thread.</em>
	 *
	 * @param force
	 *            pull from remote even when API doesn't require
	 * @param monitor
	 * @throws CoreException
	 */
	@Override
	public void pull(boolean force, IProgressMonitor monitor) throws CoreException {
		pulling = true;
		if (remoteObject != null && remoteKey == null) {
			remoteKey = factory.getRemoteKey(remoteObject);
		}
		if (remoteKey == null && localKey != null) {
			remoteKey = factory.getRemoteKeyForLocalKey(parentObject, localKey);
		}
		//Pull when "needed" or forced, but not when we don't have a remote key as that would be pointless.
		if ((factory.isPullNeeded(parentObject, modelObject, remoteObject) || force == true) && remoteKey != null) {
			notifyObservers(RemoteNotification.createUpdatingNotification());
			try {
				remoteObject = factory.pull(parentObject, remoteKey, monitor);
				if (localKey == null) {
					localKey = factory.getLocalKeyForRemoteObject(remoteObject);
				}
				pulling = false;
			} catch (final CoreException e) {
				lastStatus = e.getStatus();
				notifyObservers(RemoteNotification.createUpdateNotification(false));
				throw e;
			}
			lastStatus = Status.OK_STATUS;
		}
		pulling = false;
	}

	/**
	 * Returns true whenever the consumer is pulling from Remote API to update the remote state, that is after
	 * {@link #retrieve(boolean)} has been called but before the {@link RemoteEmfConsumer#applyModel(boolean)} call has
	 * occurred.
	 */
	public boolean isPulling() {
		return pulling;
	}

	/**
	 * Apply the remote object to the local model object. <em>This method must be called from the EMF managed (e.g.) UI
	 * thread.</em>
	 *
	 * @param force
	 *            apply the changes even when API doesn't require
	 * @throws CoreException
	 */
	@Override
	public void applyModel(boolean force) {
		EReference reference = factory.getParentReference();
		boolean modified = false;
		if (remoteObject != null) {
			if (modelObject == null || factory.isCreateModelNeeded(parentObject, modelObject)
					|| (reference.isMany() && (((Collection<?>) parentObject.eGet(reference)).size() == 0))) {
				modified = true;
				modelObject = factory.createModel(parentObject, remoteObject);
				if (reference.isMany()) {
					if (modelObject instanceof Collection) {
						((EList<EObjectType>) parentObject.eGet(reference))
								.addAll((Collection<EObjectType>) modelObject);
					} else {
						((EList<EObjectType>) parentObject.eGet(reference)).add(modelObject);
					}
				} else {
					parentObject.eSet(reference, modelObject);
				}
				if (modelObject instanceof EObject) {
					((EObject) modelObject).eSet(factory.getLocalKeyAttribute(),
							factory.getLocalKeyForRemoteObject(remoteObject));
				}
			}
			if (factory.isUpdateModelNeeded(parentObject, modelObject, remoteObject) || force) {
				modified |= factory.updateModel(parentObject, modelObject, remoteObject);
			}
		}
		retrieving = false;
		notifyObservers(RemoteNotification.createUpdateNotification(modified));
	}

	/**
	 * Returns true whenever the consumer is updating model state, that is after a {@link #retrieve(boolean)} has been
	 * called and until immediately after the {@link RemoteEmfObserver#updated(boolean)} has been called.
	 */
	public boolean isRetrieving() {
		return retrieving;
	}

	/**
	 * Performs a complete remote request, result application and listener notification against the factory. This is the
	 * method primary factory consumers will be interested in. The method will asynchronously (as defined by remote
	 * service implementation):
	 * <li>
	 * <ol>
	 * Notify any registered {@link RemoteEmfObserver}s that the object is {@link RemoteEmfObserver#updating()}.
	 * </ol>
	 * <ol>
	 * Call the remote API, retrieving the results into a local object representing the contents of the remote object.
	 * </ol>
	 * <ol>
	 * If the object does not yet exist, one will be created and added to the appropriate parent object.
	 * </ol>
	 * <ol>
	 * Notify objects of any changes via the standard EMF notification mechanisms. (As a by-product of the above step.)
	 * </ol>
	 * <ol>
	 * Notify any registered {@link RemoteEmfObserver}s of object creation or update. (An update is notified even if
	 * object state does not change.)
	 * </ol>
	 * </li>
	 *
	 * @param force
	 *            Forces pull and update, even if factory methods
	 *            {@link AbstractRemoteEmfFactory#isPullNeeded(EObject, Object, Object)} and/or
	 *            {@link AbstractRemoteEmfFactory#isUpdateModelNeeded(EObject, Object, Object)} return false.
	 */
	public void retrieve(boolean force) {
		if (retrieving) {
			return;
		}
		retrieving = true;
		getFactory().getService().retrieve(this, force || !lastStatus.isOK());
	}

	/**
	 * Handles notification from the service that a retrieval has completed.
	 */
	@Override
	public void notifyDone(IStatus status) {
		retrieving = false;
		release();
	}

	/**
	 * Unregisters all listeners and adapters.
	 */
	@Override
	public void dispose() {
		retrieving = false;
		remoteEmfObservers.clear();
		getFactory().removeConsumer(this);
		if (getModelObject() instanceof EObject) {
			getFactory().getFactoryProvider().close((EObject) getModelObject());
		}
		modelObject = null;
		remoteObject = null;
	}

	/**
	 * Adds an observer to this consumer. Updates the consumer field for {@link RemoteEmfObserver}s.
	 *
	 * @param observer
	 *            The observer to add
	 */
	public void addObserver(
			RemoteEmfObserver<EParentObjectType, EObjectType, LocalKeyType, ObjectCurrentType> observer) {
		if (observer != null) {
			RemoteEmfObserver<EParentObjectType, EObjectType, LocalKeyType, ObjectCurrentType> remoteEmfObserver = observer;
			if (remoteEmfObserver.getConsumer() != null && remoteEmfObserver.getConsumer() != this) {
				remoteEmfObserver.getConsumer().removeObserver(remoteEmfObserver);
			}
			remoteEmfObserver.internalSetConsumer(this);
			remoteEmfObservers.add(observer);
		}
	}

	/**
	 * Adds an observer to this consumer. Updates the consumer field for {@link RemoteEmfObserver}s.
	 *
	 * @param observer
	 *            The observer to remove
	 */
	public void removeObserver(
			RemoteEmfObserver<EParentObjectType, EObjectType, LocalKeyType, ObjectCurrentType> observer) {
		if (observer != null) {
			RemoteEmfObserver<EParentObjectType, EObjectType, LocalKeyType, ObjectCurrentType> remoteEmfObserver = observer;
			if (remoteEmfObserver.getConsumer() == this) {
				remoteEmfObserver.internalSetConsumer(null);
			}
			remoteEmfObservers.remove(observer);
		}
		release();
	}

	public void release() {
		if (remoteEmfObservers.size() == 0 && !retrieving) {
			dispose();
		}
	}

	public void updateObservers() {
		notifyObservers(RemoteNotification.createUpdateNotification(false));
	}

	/**
	 * Returns the factory that providing services and objects for this consumer.
	 */
	public AbstractRemoteEmfFactory<EParentObjectType, EObjectType, LocalKeyType, RemoteType, RemoteKeyType, ObjectCurrentType> getFactory() {
		return factory;
	}

	public void open() {
		getFactory().open(parentObject, localKey);
	}

	public void save() {
		if (getModelObject() instanceof EObject) {
			getFactory().getFactoryProvider().save();
			getFactory().getFactoryProvider().save((EObject) getModelObject());
		}
	}

	/**
	 * Returns the parent object for this consumer.
	 */
	public EParentObjectType getParentObject() {
		return parentObject;
	}

	/**
	 * Returns the model object for this consumer, if one has been obtained through the {@link #retrieve(boolean)}
	 * method or supplied when any object obtained this consumer.
	 */
	public EObjectType getModelObject() {
		return modelObject;
	}

	/**
	 * Returns the local key supplied by the consumer, or the local remote key if it can be inferred from the remote key
	 * or remote object.
	 */
	public LocalKeyType getLocalKey() {
		if (localKey != null) {
			return localKey;
		} else if (remoteKey != null) {
			return getFactory().getLocalKeyForRemoteKey(remoteKey);
		} else if (remoteObject != null) {
			return getFactory().getLocalKeyForRemoteObject(remoteObject);
		}
		return null;
	}

	/**
	 * Returns the remote key for this consumer.
	 */
	public RemoteKeyType getRemoteKey() {
		return remoteKey;
	}

	/**
	 * Returns the remote object that maps to this consumer's model object, local key or remote key, if one has been
	 * supplied or obtained using the remote key.
	 *
	 * @return
	 */
	public RemoteType getRemoteObject() {
		return remoteObject;
	}

	/**
	 * Should only be called by RemoteEmfFactory.
	 *
	 * @param remoteObject
	 */
	void setRemoteObject(RemoteType remoteObject) {
		if (!factory.getLocalKeyForRemoteObject(remoteObject).equals(getLocalKey())) {
			throw new RuntimeException(
					"Internal Error. Tried to set a remote object that doesn't match existing local key or object."); //$NON-NLS-1$
		}
		this.remoteObject = remoteObject;
	}

	/**
	 * Should only be called by RemoteEmfFactory.
	 *
	 * @param remoteObject
	 */
	void setRemoteKey(RemoteKeyType remoteKey) {
		if (!factory.getLocalKeyForRemoteKey(remoteKey).equals(getLocalKey())) {
			throw new RuntimeException(
					"Internal Error. Tried to set a remote object that doesn't match existing local key or object."); //$NON-NLS-1$
		}
		this.remoteKey = remoteKey;
	}

	@Override
	public String getDescription() {
		return NLS.bind(Messages.RemoteEmfConsumer_Retrieving_X,
				factory.getModelDescription(getParentObject(), getModelObject(), getLocalKey()));
	}

	@Override
	public boolean isUserJob() {
		return userJob;
	}

	public void setUiJob(boolean userJob) {
		this.userJob = userJob;
	}

	@Override
	public boolean isSystemJob() {
		return systemJob;
	}

	public void setSystemJob(boolean systemJob) {
		this.systemJob = systemJob;
	}

	@Override
	public boolean isAsynchronous() {
		return getFactory().isAsynchronous() && asynchronous;
	}

	public void setAsynchronous(boolean asynchronous) {
		this.asynchronous = asynchronous;
	}

	public IStatus getStatus() {
		return lastStatus;
	}
}
