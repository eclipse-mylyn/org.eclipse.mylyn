/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
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
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * Implements {@link RemoteNotification} to support specialized remote EMF notifications.
 * 
 * @author Miles Parker
 */
public class RemoteENotificationImpl extends ENotificationImpl implements RemoteNotification {

	private IStatus status;

	private boolean modified = false;

	public RemoteENotificationImpl(InternalEObject notifier, int eventType, EReference feature, Object object,
			IStatus status) {
		super(notifier, eventType, feature, null, object);
		this.status = status;
	}

	public RemoteENotificationImpl(InternalEObject notifier, int eventType, EReference feature, Object object,
			boolean modified) {
		this(notifier, eventType, feature, object, Status.OK_STATUS);
		this.modified = modified;
	}

	public RemoteENotificationImpl(InternalEObject notifier, int eventType, EReference feature, Object object) {
		this(notifier, eventType, feature, object, Status.OK_STATUS);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature,
			Object oldValue, Object newValue, boolean isSetChange) {
		super(notifier, eventType, feature, oldValue, newValue, isSetChange);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature,
			Object oldValue, Object newValue, int position, boolean wasSet) {
		super(notifier, eventType, feature, oldValue, newValue, position, wasSet);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature,
			Object oldValue, Object newValue, int position) {
		super(notifier, eventType, feature, oldValue, newValue, position);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature,
			Object oldValue, Object newValue) {
		super(notifier, eventType, feature, oldValue, newValue);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, boolean oldBooleanValue,
			boolean newBooleanValue, boolean isSetChange) {
		super(notifier, eventType, featureID, oldBooleanValue, newBooleanValue, isSetChange);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, boolean oldBooleanValue,
			boolean newBooleanValue) {
		super(notifier, eventType, featureID, oldBooleanValue, newBooleanValue);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, byte oldByteValue,
			byte newByteValue, boolean isSetChange) {
		super(notifier, eventType, featureID, oldByteValue, newByteValue, isSetChange);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, byte oldByteValue,
			byte newByteValue) {
		super(notifier, eventType, featureID, oldByteValue, newByteValue);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, char oldCharValue,
			char newCharValue, boolean isSetChange) {
		super(notifier, eventType, featureID, oldCharValue, newCharValue, isSetChange);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, char oldCharValue,
			char newCharValue) {
		super(notifier, eventType, featureID, oldCharValue, newCharValue);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, double oldDoubleValue,
			double newDoubleValue, boolean isSetChange) {
		super(notifier, eventType, featureID, oldDoubleValue, newDoubleValue, isSetChange);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, double oldDoubleValue,
			double newDoubleValue) {
		super(notifier, eventType, featureID, oldDoubleValue, newDoubleValue);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, float oldFloatValue,
			float newFloatValue, boolean isSetChange) {
		super(notifier, eventType, featureID, oldFloatValue, newFloatValue, isSetChange);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, float oldFloatValue,
			float newFloatValue) {
		super(notifier, eventType, featureID, oldFloatValue, newFloatValue);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, int oldIntValue,
			int newIntValue, boolean isSetChange) {
		super(notifier, eventType, featureID, oldIntValue, newIntValue, isSetChange);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, int oldIntValue,
			int newIntValue) {
		super(notifier, eventType, featureID, oldIntValue, newIntValue);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, long oldLongValue,
			long newLongValue, boolean isSetChange) {
		super(notifier, eventType, featureID, oldLongValue, newLongValue, isSetChange);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, long oldLongValue,
			long newLongValue) {
		super(notifier, eventType, featureID, oldLongValue, newLongValue);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue,
			Object newValue, boolean isSetChange) {
		super(notifier, eventType, featureID, oldValue, newValue, isSetChange);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue,
			Object newValue, int position, boolean wasSet) {
		super(notifier, eventType, featureID, oldValue, newValue, position, wasSet);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue,
			Object newValue, int position) {
		super(notifier, eventType, featureID, oldValue, newValue, position);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue,
			Object newValue) {
		super(notifier, eventType, featureID, oldValue, newValue);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, short oldShortValue,
			short newShortValue, boolean isSetChange) {
		super(notifier, eventType, featureID, oldShortValue, newShortValue, isSetChange);
	}

	private RemoteENotificationImpl(InternalEObject notifier, int eventType, int featureID, short oldShortValue,
			short newShortValue) {
		super(notifier, eventType, featureID, oldShortValue, newShortValue);
	}

	public boolean isDone() {
		return getEventType() == REMOTE_UPDATE || getEventType() == REMOTE_FAILURE
				|| getEventType() == REMOTE_MEMBER_UPDATE || getEventType() == REMOTE_MEMBER_FAILURE;
	}

	public boolean isMember() {
		return getEventType() == REMOTE_MEMBER_CREATE || getEventType() == REMOTE_MEMBER_FAILURE
				|| getEventType() == REMOTE_MEMBER_UPDATE || getEventType() == REMOTE_MEMBER_UPDATING;
	}

	public IStatus getStatus() {
		return status;
	}

	public boolean isModification() {
		return (modified && getEventType() != REMOTE_MEMBER_FAILURE && getEventType() != REMOTE_FAILURE
				&& getEventType() != REMOTE_UPDATING && getEventType() != REMOTE_MEMBER_UPDATING)
				|| getEventType() == REMOTE_MEMBER_CREATE;
	}
}
