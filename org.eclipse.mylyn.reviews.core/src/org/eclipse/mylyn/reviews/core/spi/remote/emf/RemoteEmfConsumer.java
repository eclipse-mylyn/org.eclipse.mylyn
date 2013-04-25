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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteConsumer;

/**
 * Manages the interaction between a remote API and a local EMF object. There can be only one instance of a consumer for
 * a given model object or remote object per factory.
 * <p>
 * After obtaining a consumer using one of the {@link RemoteEmfConsumer} <i>AbstractRemoteEmfFactory#getConsumer()</i>
 * methods, call {@link RemoteEmfConsumer#retrieve(boolean)} to request an update. Any registered
 * {@link IRemoteEmfObserver}s will then receive an {@link IRemoteEmfObserver#updated(EObject, Object, boolean)} event
 * regardless of whether or not the actual state changed.
 * 
 * @author Miles Parker
 */
public class RemoteEmfConsumer<EParentObjectType extends EObject, EObjectType, RemoteType, RemoteKeyType, LocalKeyType>
		extends AbstractRemoteConsumer {

	private final AbstractRemoteEmfFactory<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType> factory;

	private RemoteKeyType remoteKey;

	private RemoteType remoteObject;

	private final EParentObjectType parentObject;

	private EObjectType modelObject;

	private LocalKeyType localKey;

	Collection<IRemoteEmfObserver<EParentObjectType, EObjectType>> remoteEmfObservers;

	private boolean pulling;

	private boolean retrieving;

	private class ConsumerAdapter extends AdapterImpl {

		@Override
		public void notifyChanged(Notification msg) {
			if (msg instanceof RemoteNotification) {
				RemoteNotification remoteMessage = (RemoteNotification) msg;
				boolean notifyParent = remoteMessage.isMember()
						&& msg.getNotifier() == parentObject
						&& ((msg.getNewValue() == modelObject && (msg.getEventType() == RemoteNotification.REMOTE_MEMBER_CREATE || msg.getEventType() == RemoteNotification.REMOTE_MEMBER_FAILURE)) || modelObject instanceof Collection);
				boolean notifyChild = !remoteMessage.isMember() && msg.getNotifier() == modelObject;
				if (notifyParent || notifyChild) {
					synchronized (remoteEmfObservers) {
						for (IRemoteEmfObserver<EParentObjectType, EObjectType> listener : remoteEmfObservers) {
							switch (msg.getEventType()) {
							case RemoteNotification.REMOTE_MEMBER_CREATE:
								listener.created(parentObject, modelObject);
								break;
							case RemoteNotification.REMOTE_MEMBER_UPDATING:
							case RemoteNotification.REMOTE_UPDATING:
								listener.updating(parentObject, modelObject);
								break;
							case RemoteNotification.REMOTE_MEMBER_UPDATE:
							case RemoteNotification.REMOTE_UPDATE:
								listener.updated(parentObject, modelObject, remoteMessage.isModification());
								break;
							case RemoteNotification.REMOTE_MEMBER_FAILURE:
							case RemoteNotification.REMOTE_FAILURE:
								listener.failed(parentObject, modelObject, remoteMessage.getStatus());
							}
						}
					}
				}
			}
		}
	}

	ConsumerAdapter adapter = new ConsumerAdapter();

	RemoteEmfConsumer(
			AbstractRemoteEmfFactory<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType> factory,
			EParentObjectType parent, EObjectType modelObject, LocalKeyType localKey, RemoteType remoteObject,
			RemoteKeyType remoteKey) {
		this.parentObject = parent;
		this.modelObject = modelObject;
		this.remoteObject = remoteObject;
		this.remoteKey = remoteKey;
		this.localKey = localKey;
		this.factory = factory;
		if (remoteKey == null && remoteObject != null) {
			remoteKey = factory.getRemoteKey(remoteObject);
		}
		if (localKey == null && modelObject != null) {
			localKey = factory.getLocalKey(null, modelObject);
		}
		if (modelObject instanceof EObject) {
			((EObject) modelObject).eAdapters().add(adapter);
		} else if (parent != null) {
			parent.eAdapters().add(adapter);
		}
		remoteEmfObservers = new ArrayList<IRemoteEmfObserver<EParentObjectType, EObjectType>>();
	}

	/**
	 * Pulls the results from the factory, populating the remote object with the latest state from the remote API.
	 * Blocks until the remote API call completes. Does nothing if a retrieval is already occurring.
	 * <em>This method must not be called from the UI thread.</em>
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
		//Pull when "needed" or forced, but not when we don't have a remote key as that would be pointless.
		if ((factory.isPullNeeded(parentObject, modelObject, remoteObject) || force == true) && remoteKey != null) {

			getFactory().getService().modelExec(new Runnable() {
				public void run() {
					parentObject.eNotify(new RemoteENotificationImpl((InternalEObject) parentObject,
							RemoteNotification.REMOTE_MEMBER_UPDATING, factory.getParentReference(), modelObject));
					if (modelObject instanceof EObject) {
						((EObject) modelObject).eNotify(new RemoteENotificationImpl((InternalEObject) modelObject,
								RemoteNotification.REMOTE_MEMBER_UPDATING, null, null));
					}
				}
			}, false);
			try {
				remoteObject = factory.pull(parentObject, remoteKey, monitor);
				if (localKey == null) {
					localKey = factory.getLocalKeyForRemoteObject(remoteObject);
				}
				pulling = false;
			} catch (final CoreException e) {
				getFactory().getService().modelExec(new Runnable() {
					public void run() {
						parentObject.eNotify(new RemoteENotificationImpl((InternalEObject) parentObject,
								RemoteNotification.REMOTE_MEMBER_FAILURE, factory.getParentReference(), null,
								e.getStatus()));
						if (modelObject instanceof EObject) {
							((EObject) modelObject).eNotify(new RemoteENotificationImpl((InternalEObject) modelObject,
									RemoteNotification.REMOTE_FAILURE, null, null, e.getStatus()));
						}
					}
				}, false);
				throw e;
			}
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
	 * Apply the remote object to the local model object.
	 * <em>This method must be called from the EMF managed (e.g.) UI thread.</em>
	 * 
	 * @param force
	 *            apply the changes even when API doesn't require
	 * @throws CoreException
	 */
	@Override
	public void applyModel(boolean force) {
		NotificationChain msgs = new NotificationChainImpl();
		EReference reference = factory.getParentReference();
		boolean modified = false;
		if (remoteObject != null) {
			if (modelObject == null
					|| (reference.isMany() && (((Collection<?>) parentObject.eGet(reference)).size() == 0))) {
				modified = true;
				modelObject = factory.createModel(parentObject, remoteObject);
				if (reference.isMany()) {
					if (modelObject instanceof Collection) {
						((EList<EObjectType>) parentObject.eGet(reference)).addAll((Collection<EObjectType>) modelObject);
					} else {
						((EList<EObjectType>) parentObject.eGet(reference)).add(modelObject);
					}
				} else {
					parentObject.eSet(reference, modelObject);
				}
				if (modelObject instanceof EObject) {
					((EObject) modelObject).eSet(factory.getLocalKeyAttribute(),
							factory.getLocalKeyForRemoteObject(remoteObject));
					((EObject) modelObject).eAdapters().add(adapter);
				}
				msgs.add(new RemoteENotificationImpl((InternalEObject) parentObject,
						RemoteNotification.REMOTE_MEMBER_CREATE, reference, modelObject));
			}
			if (factory.isUpdateModelNeeded(parentObject, modelObject, remoteObject) || force) {
				modified |= factory.updateModel(parentObject, modelObject, remoteObject);
			}
		}
		msgs.add(new RemoteENotificationImpl((InternalEObject) parentObject, RemoteNotification.REMOTE_MEMBER_UPDATE,
				reference, modelObject, modified));
		if (modelObject instanceof EObject) {
			msgs.add(new RemoteENotificationImpl((InternalEObject) modelObject, RemoteNotification.REMOTE_UPDATE, null,
					null, modified));
		}
		retrieving = false;
		msgs.dispatch();
	}

	/**
	 * Returns true whenever the consumer is updating model state, that is after a {@link #retrieve(boolean)} has been
	 * called and until immediately after the {@link IRemoteEmfObserver#updated(EObject, Object, boolean)} has been
	 * called.
	 */
	public boolean isRetrieving() {
		return retrieving;
	}

	/**
	 * Performs a complete remote request, result application and listener notification against the factory. This is the
	 * method primary factory consumers will be interested in. The method will asynchronously (as defined by remote
	 * service implementation):<li>
	 * <ol>
	 * Notify any registered {@link IRemoteEmfObserver}s that the object is
	 * {@link IRemoteEmfObserver#updating(EObject, Object)}.
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
	 * Notify any registered {@link IRemoteEmfObserver}s of object creation or update. (An update is notified even if
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
		getFactory().getService().retrieve(this, force);
	}

	/**
	 * Notifies the consumer that a failure has occurred while performing a retrieval. (Consumers should generally
	 * handle update and failure notifications through the {@link IRemoteEmfObserver#failed(IStatus)} method instead.)
	 */
	@Override
	public void notifyDone(IStatus status) {
		retrieving = false;
	}

	/**
	 * Unregisters all listeners and adapters.
	 */
	@Override
	public void dispose() {
		parentObject.eAdapters().remove(adapter);
		if (modelObject instanceof EObject) {
			((EObject) modelObject).eAdapters().remove(adapter);
		}
		synchronized (remoteEmfObservers) {
			remoteEmfObservers.clear();
		}
	}

	/**
	 * Adds an observer to this consumer. Updates the consumer field for {@link RemoteEmfObserver}s.
	 * 
	 * @param observer
	 *            The observer to add
	 */
	public void addObserver(IRemoteEmfObserver<EParentObjectType, EObjectType> observer) {
		if (observer instanceof RemoteEmfObserver) {
			RemoteEmfObserver<EParentObjectType, EObjectType> remoteEmfObserver = (RemoteEmfObserver<EParentObjectType, EObjectType>) observer;
			if (remoteEmfObserver.getConsumer() != null && remoteEmfObserver.getConsumer() != this) {
				remoteEmfObserver.getConsumer().removeObserver(remoteEmfObserver);
			}
			remoteEmfObserver.internalSetConsumer(this);
		}
		synchronized (remoteEmfObservers) {
			remoteEmfObservers.add(observer);
		}
	}

	/**
	 * Adds an observer to this consumer. Updates the consumer field for {@link RemoteEmfObserver}s.
	 * 
	 * @param observer
	 *            The observer to remove
	 */
	public void removeObserver(IRemoteEmfObserver<EParentObjectType, EObjectType> observer) {
		if (observer instanceof RemoteEmfObserver) {
			RemoteEmfObserver<EParentObjectType, EObjectType> remoteEmfObserver = (RemoteEmfObserver<EParentObjectType, EObjectType>) observer;
			if (remoteEmfObserver.getConsumer() == this) {
				remoteEmfObserver.internalSetConsumer(null);
			}
		}
		synchronized (remoteEmfObservers) {
			remoteEmfObservers.remove(observer);
		}
	}

	public void updateObservers() {
		NotificationChain msgs = new NotificationChainImpl();
		msgs.add(new RemoteENotificationImpl((InternalEObject) parentObject, RemoteNotification.REMOTE_MEMBER_UPDATE,
				factory.getParentReference(), modelObject, false));
		if (modelObject instanceof EObject) {
			msgs.add(new RemoteENotificationImpl((InternalEObject) modelObject, RemoteNotification.REMOTE_UPDATE, null,
					null, false));
		}
		msgs.dispatch();
	}

	/**
	 * Returns the factory that providing services and objects for this consumer.
	 */
	public AbstractRemoteEmfFactory<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType> getFactory() {
		return factory;
	}

	/**
	 * Returns true if the factory is asynchronous, false otherwise.
	 */
	@Override
	public boolean isAsynchronous() {
		return getFactory().isAsynchronous();
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
					"Internal Error. Tried to set a remote object that doesn't match existing local key or object.");
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
					"Internal Error. Tried to set a remote object that doesn't match existing local key or object.");
		}
		this.remoteKey = remoteKey;
	}

	@Override
	public String getDescription() {
		return "Retrieving "
				+ factory.getParentReference().getEReferenceType().getName()
				+ " "
				+ (localKey != null ? localKey.toString() : (remoteKey != null
						? ("Remote Key: " + remoteKey)
						: "Unknown"));
	}
}
