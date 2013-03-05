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
 * Manages the interaction between a remote API and a local EMF object.
 * <p>
 * After obtaining a consumer using one of the
 * {@link AbstractRemoteEmfFactory#consume(String, EObject, Object, IObserver)} methods, users can then
 * {@link RemoteEmfConsumer#request()} the model state to be updated from the remote API. Consumers that have not
 * directly requested an object can attach to it by calling is no longer needed in order to avoid excessive EMF
 * notification overhead.
 * 
 * @author Miles Parker
 */
public class RemoteEmfConsumer<EParentObjectType extends EObject, EObjectType, RemoteType, RemoteKeyType, LocalKeyType>
		extends AbstractRemoteConsumer {

	/**
	 * The observer receives notification of events that affect the consumer model objects. This is like an Asynchronous
	 * callback, except that the notification occurs every time a remote change to the object occurs. Notifications
	 * occur regardless of the source consumer, and even across factories.
	 */
	public interface IObserver<EObjectType> {

		void created(EObjectType object);

		void responded(boolean modified);

		void failed(IStatus status);

	}

	public class ObserverImpl<ObjectType> implements IObserver<EObjectType> {

		public void created(EObjectType object) {
		}

		public void responded(boolean changed) {
		}

		public void failed(IStatus status) {
		}
	}

	private final String description;

	private final AbstractRemoteEmfFactory<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType> factory;

	private RemoteKeyType remoteKey;

	private RemoteType remoteObject;

	private final EParentObjectType parentObject;

	private EObjectType modelObject;

	private LocalKeyType localKey;

	private final IObserver<EObjectType> listener;

	class ConsumerAdapter extends AdapterImpl {

		@Override
		public void notifyChanged(Notification msg) {
			if (msg.getEventType() == RemoteNotification.REMOTE_MEMBER_CREATE) {
				if (msg.getNewValue() == modelObject) {
					listener.created(modelObject);
				}
			}
			if ((msg.getEventType() == RemoteNotification.REMOTE_MEMBER_UPDATE && msg.getNotifier() == parentObject && (!(modelObject instanceof EObject)))
					|| (msg.getEventType() == RemoteNotification.REMOTE_UPDATE && msg.getNotifier() == modelObject)) {
				listener.responded(((RemoteNotification) msg).isModification());
			}
			if ((msg.getEventType() == RemoteNotification.REMOTE_MEMBER_FAILURE && msg.getNotifier() == parentObject && msg.getNewValue() == modelObject)
					|| (msg.getEventType() == RemoteNotification.REMOTE_FAILURE && msg.getNotifier() == modelObject)) {
				listener.failed(((RemoteNotification) msg).getStatus());
			}
		}
	}

	ConsumerAdapter adapter = new ConsumerAdapter();

	RemoteEmfConsumer(String description,
			AbstractRemoteEmfFactory<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType> factory,
			EParentObjectType parent, RemoteKeyType remoteKey, LocalKeyType localKey, IObserver<EObjectType> consumer) {
		this.parentObject = parent;
		this.factory = factory;
		this.description = description;
		this.remoteKey = remoteKey;
		this.localKey = localKey;
		this.listener = consumer;
		parent.eAdapters().add(adapter);
	}

	RemoteEmfConsumer(String description,
			AbstractRemoteEmfFactory<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType> factory,
			EParentObjectType parent, EObjectType object, IObserver<EObjectType> consumer) {
		this.description = description;
		this.parentObject = parent;
		this.modelObject = object;
		this.factory = factory;
		this.listener = consumer;
		if (object instanceof EObject) {
			((EObject) object).eAdapters().add(adapter);
		} else if (parent != null) {
			parent.eAdapters().add(adapter);
		}
	}

	@Override
	protected void retrieve(IProgressMonitor monitor) throws CoreException {
		parentObject.eNotify(new RemoteENotificationImpl((InternalEObject) parentObject,
				RemoteNotification.REMOTE_MEMBER_UPDATING, factory.getParentReference(), null));
		if (modelObject instanceof EObject) {
			((EObject) modelObject).eNotify(new RemoteENotificationImpl((InternalEObject) modelObject,
					RemoteNotification.REMOTE_MEMBER_UPDATING, null, null));
		}
		if (remoteKey == null && modelObject != null) {
			remoteKey = factory.getRemoteKey(parentObject, modelObject);
		}
		try {
			remoteObject = factory.retrieve(remoteKey, monitor);
		} catch (final CoreException e) {
			getFactory().getService().modelExec(new Runnable() {
				public void run() {
					if (modelObject instanceof EObject) {
						((EObject) modelObject).eNotify(new RemoteENotificationImpl((InternalEObject) modelObject,
								RemoteNotification.REMOTE_FAILURE, null, null, e.getStatus()));
					} else {
						parentObject.eNotify(new RemoteENotificationImpl((InternalEObject) parentObject,
								RemoteNotification.REMOTE_MEMBER_FAILURE, factory.getParentReference(), null,
								e.getStatus()));
					}
				}
			});
			throw e;
		}
	}

	@Override
	protected void apply() {
		NotificationChain msgs = new NotificationChainImpl();
		subscribe();
		EReference reference = factory.getParentReference();
		boolean created = false;
		if (modelObject == null || reference.isMany() && (((Collection<?>) parentObject.eGet(reference)).size() == 0)) {
			created = true;
			modelObject = factory.create(parentObject, remoteObject);
			factory.associateObjects(modelObject, remoteObject, localKey, remoteKey);
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
				((EObject) modelObject).eAdapters().add(adapter);
			}
			msgs.add(new RemoteENotificationImpl((InternalEObject) parentObject,
					RemoteNotification.REMOTE_MEMBER_CREATE, reference, modelObject));
		}
		boolean update = factory.update(parentObject, modelObject, remoteObject);
		update |= created;
		msgs.add(new RemoteENotificationImpl((InternalEObject) parentObject, RemoteNotification.REMOTE_MEMBER_UPDATE,
				reference, modelObject, update));
		if (modelObject instanceof EObject) {
			msgs.add(new RemoteENotificationImpl((InternalEObject) modelObject, RemoteNotification.REMOTE_UPDATE, null,
					null, update));
		}
		msgs.dispatch();

	}

	/**
	 * Performs a complete remote request, result application and listener notification against the factory. This is the
	 * method primary factory consumers will be interested in. The method will asynchronously (as defined by remote
	 * service implementation):<li>
	 * <ol>
	 * Call the remote API, retrieving the results into a local object representing the contents of the remote object.
	 * If the object does not yet exist, one will be created.
	 * </ol>
	 * <ol>
	 * Create a new EMF object and reference it from the supplied parent object.
	 * </ol>
	 * <ol>
	 * Notify the parent object via the EMF notification mechanism. (As a by-product of the above step.)
	 * </ol>
	 * </li>
	 */
	public void request() {
		getFactory().getService().execute(this);
	}

	/**
	 * Registers the consumer with the factory and obtains a model object representing the remote object. If a model
	 * object does not yet exist, it will not be created. This is the method that factory observers such as views will
	 * interested in. This method may be called at any time and does not trigger a remote invocation.
	 */
	public void subscribe() {
		boolean modelWasNull = modelObject == null;
		if (modelObject == null) {
			modelObject = factory.getModelObject(remoteObject);
		}
		if (modelObject == null) {
			modelObject = factory.getLocalModelObject(localKey);
		}
		if (modelWasNull && modelObject instanceof EObject) {
			((EObject) modelObject).eAdapters().add(adapter);
		}
	}

	/**
	 * A no-op. Typically, consumers should handle failure notifications through the {@link IObserver#failed(IStatus)}
	 * method.
	 */
	@Override
	public void notifyDone(IStatus status) {
	}

	/**
	 * Unregisters all listeners.
	 */
	@Override
	public void dispose() {
		parentObject.eAdapters().remove(adapter);
		if (modelObject instanceof EObject) {
			((EObject) modelObject).eAdapters().remove(adapter);
		}
	}

	@Override
	public String getDescription() {
		return description;
	}

	public AbstractRemoteEmfFactory<EParentObjectType, EObjectType, RemoteType, RemoteKeyType, LocalKeyType> getFactory() {
		return factory;
	}

	@Override
	public boolean isAsynchronous() {
		return factory.isAsynchronous();
	}

	public EObjectType getModelObject() {
		return modelObject;
	}

	public RemoteType getRemoteObject() {
		return remoteObject;
	}

	public RemoteKeyType getRemoteKey() {
		return remoteKey;
	}

	public LocalKeyType getLocalKey() {
		return localKey;
	}
}
