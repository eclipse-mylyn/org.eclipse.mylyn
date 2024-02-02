/**
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.internal.core.model;

import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.mylyn.reviews.core.model.IChange;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Change</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Change#getCreationDate <em>Creation Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Change#getModificationDate <em>Modification Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Change#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Change#getKey <em>Key</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Change#getSubject <em>Subject</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Change#getMessage <em>Message</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Change#getOwner <em>Owner</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Change#getState <em>State</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Change extends EObjectImpl implements IChange {
	/**
	 * The default value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getCreationDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date CREATION_DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getCreationDate()
	 * @generated
	 * @ordered
	 */
	protected Date creationDate = CREATION_DATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getModificationDate() <em>Modification Date</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getModificationDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date MODIFICATION_DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getModificationDate() <em>Modification Date</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getModificationDate()
	 * @generated
	 * @ordered
	 */
	protected Date modificationDate = MODIFICATION_DATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getKey() <em>Key</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected static final String KEY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getKey() <em>Key</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected String key = KEY_EDEFAULT;

	/**
	 * The default value of the '{@link #getSubject() <em>Subject</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSubject()
	 * @generated
	 * @ordered
	 */
	protected static final String SUBJECT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSubject() <em>Subject</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSubject()
	 * @generated
	 * @ordered
	 */
	protected String subject = SUBJECT_EDEFAULT;

	/**
	 * The default value of the '{@link #getMessage() <em>Message</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected static final String MESSAGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMessage() <em>Message</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected String message = MESSAGE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOwner() <em>Owner</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getOwner()
	 * @generated
	 * @ordered
	 */
	protected IUser owner;

	/**
	 * The default value of the '{@link #getState() <em>State</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected static final ReviewStatus STATE_EDEFAULT = ReviewStatus.NEW;

	/**
	 * The cached value of the '{@link #getState() <em>State</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected ReviewStatus state = STATE_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Change() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.CHANGE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public void setCreationDate(Date newCreationDate) {
		Date oldCreationDate = creationDate;
		//Protect against case where java.sql.Timestamp is used
		creationDate = new Date(newCreationDate.getTime());
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.CHANGE__CREATION_DATE, oldCreationDate,
					creationDate));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public void setModificationDate(Date newModificationDate) {
		Date oldModificationDate = modificationDate;
		//Protect against case where java.sql.Timestamp is used
		modificationDate = new Date(newModificationDate.getTime());
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.CHANGE__MODIFICATION_DATE,
					oldModificationDate, modificationDate));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.CHANGE__ID, oldId, id));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getKey() {
		return key;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setKey(String newKey) {
		String oldKey = key;
		key = newKey;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.CHANGE__KEY, oldKey, key));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getSubject() {
		return subject;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setSubject(String newSubject) {
		String oldSubject = subject;
		subject = newSubject;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.CHANGE__SUBJECT, oldSubject, subject));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setMessage(String newMessage) {
		String oldMessage = message;
		message = newMessage;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.CHANGE__MESSAGE, oldMessage, message));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IUser getOwner() {
		if (owner != null && owner.eIsProxy()) {
			InternalEObject oldOwner = (InternalEObject) owner;
			owner = (IUser) eResolveProxy(oldOwner);
			if (owner != oldOwner) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.CHANGE__OWNER, oldOwner,
							owner));
				}
			}
		}
		return owner;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser basicGetOwner() {
		return owner;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setOwner(IUser newOwner) {
		IUser oldOwner = owner;
		owner = newOwner;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.CHANGE__OWNER, oldOwner, owner));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ReviewStatus getState() {
		return state;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setState(ReviewStatus newState) {
		ReviewStatus oldState = state;
		state = newState == null ? STATE_EDEFAULT : newState;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.CHANGE__STATE, oldState, state));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ReviewsPackage.CHANGE__CREATION_DATE:
				return getCreationDate();
			case ReviewsPackage.CHANGE__MODIFICATION_DATE:
				return getModificationDate();
			case ReviewsPackage.CHANGE__ID:
				return getId();
			case ReviewsPackage.CHANGE__KEY:
				return getKey();
			case ReviewsPackage.CHANGE__SUBJECT:
				return getSubject();
			case ReviewsPackage.CHANGE__MESSAGE:
				return getMessage();
			case ReviewsPackage.CHANGE__OWNER:
				if (resolve) {
					return getOwner();
				}
				return basicGetOwner();
			case ReviewsPackage.CHANGE__STATE:
				return getState();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ReviewsPackage.CHANGE__CREATION_DATE:
				setCreationDate((Date) newValue);
				return;
			case ReviewsPackage.CHANGE__MODIFICATION_DATE:
				setModificationDate((Date) newValue);
				return;
			case ReviewsPackage.CHANGE__ID:
				setId((String) newValue);
				return;
			case ReviewsPackage.CHANGE__KEY:
				setKey((String) newValue);
				return;
			case ReviewsPackage.CHANGE__SUBJECT:
				setSubject((String) newValue);
				return;
			case ReviewsPackage.CHANGE__MESSAGE:
				setMessage((String) newValue);
				return;
			case ReviewsPackage.CHANGE__OWNER:
				setOwner((IUser) newValue);
				return;
			case ReviewsPackage.CHANGE__STATE:
				setState((ReviewStatus) newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ReviewsPackage.CHANGE__CREATION_DATE:
				setCreationDate(CREATION_DATE_EDEFAULT);
				return;
			case ReviewsPackage.CHANGE__MODIFICATION_DATE:
				setModificationDate(MODIFICATION_DATE_EDEFAULT);
				return;
			case ReviewsPackage.CHANGE__ID:
				setId(ID_EDEFAULT);
				return;
			case ReviewsPackage.CHANGE__KEY:
				setKey(KEY_EDEFAULT);
				return;
			case ReviewsPackage.CHANGE__SUBJECT:
				setSubject(SUBJECT_EDEFAULT);
				return;
			case ReviewsPackage.CHANGE__MESSAGE:
				setMessage(MESSAGE_EDEFAULT);
				return;
			case ReviewsPackage.CHANGE__OWNER:
				setOwner((IUser) null);
				return;
			case ReviewsPackage.CHANGE__STATE:
				setState(STATE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ReviewsPackage.CHANGE__CREATION_DATE:
				return CREATION_DATE_EDEFAULT == null
						? creationDate != null
						: !CREATION_DATE_EDEFAULT.equals(creationDate);
			case ReviewsPackage.CHANGE__MODIFICATION_DATE:
				return MODIFICATION_DATE_EDEFAULT == null
						? modificationDate != null
						: !MODIFICATION_DATE_EDEFAULT.equals(modificationDate);
			case ReviewsPackage.CHANGE__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case ReviewsPackage.CHANGE__KEY:
				return KEY_EDEFAULT == null ? key != null : !KEY_EDEFAULT.equals(key);
			case ReviewsPackage.CHANGE__SUBJECT:
				return SUBJECT_EDEFAULT == null ? subject != null : !SUBJECT_EDEFAULT.equals(subject);
			case ReviewsPackage.CHANGE__MESSAGE:
				return MESSAGE_EDEFAULT == null ? message != null : !MESSAGE_EDEFAULT.equals(message);
			case ReviewsPackage.CHANGE__OWNER:
				return owner != null;
			case ReviewsPackage.CHANGE__STATE:
				return state != STATE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (creationDate: "); //$NON-NLS-1$
		result.append(creationDate);
		result.append(", modificationDate: "); //$NON-NLS-1$
		result.append(modificationDate);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(", key: "); //$NON-NLS-1$
		result.append(key);
		result.append(", subject: "); //$NON-NLS-1$
		result.append(subject);
		result.append(", message: "); //$NON-NLS-1$
		result.append(message);
		result.append(", state: "); //$NON-NLS-1$
		result.append(state);
		result.append(')');
		return result.toString();
	}

} //Change
