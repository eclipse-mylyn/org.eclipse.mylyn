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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.RequirementStatus;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Requirement Entry</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry#getStatus <em>Status</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry#getBy <em>By</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class RequirementEntry extends EObjectImpl implements IRequirementEntry {
	/**
	 * The default value of the '{@link #getStatus() <em>Status</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected static final RequirementStatus STATUS_EDEFAULT = RequirementStatus.UNKNOWN;

	/**
	 * The cached value of the '{@link #getStatus() <em>Status</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected RequirementStatus status = STATUS_EDEFAULT;

	/**
	 * The cached value of the '{@link #getBy() <em>By</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getBy()
	 * @generated
	 * @ordered
	 */
	protected IUser by;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected RequirementEntry() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REQUIREMENT_ENTRY;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public RequirementStatus getStatus() {
		return status;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setStatus(RequirementStatus newStatus) {
		RequirementStatus oldStatus = status;
		status = newStatus == null ? STATUS_EDEFAULT : newStatus;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REQUIREMENT_ENTRY__STATUS, oldStatus,
					status));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IUser getBy() {
		if (by != null && by.eIsProxy()) {
			InternalEObject oldBy = (InternalEObject) by;
			by = (IUser) eResolveProxy(oldBy);
			if (by != oldBy) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.REQUIREMENT_ENTRY__BY,
							oldBy, by));
				}
			}
		}
		return by;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser basicGetBy() {
		return by;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setBy(IUser newBy) {
		IUser oldBy = by;
		by = newBy;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REQUIREMENT_ENTRY__BY, oldBy, by));
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
			case ReviewsPackage.REQUIREMENT_ENTRY__STATUS:
				return getStatus();
			case ReviewsPackage.REQUIREMENT_ENTRY__BY:
				if (resolve) {
					return getBy();
				}
				return basicGetBy();
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
			case ReviewsPackage.REQUIREMENT_ENTRY__STATUS:
				setStatus((RequirementStatus) newValue);
				return;
			case ReviewsPackage.REQUIREMENT_ENTRY__BY:
				setBy((IUser) newValue);
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
			case ReviewsPackage.REQUIREMENT_ENTRY__STATUS:
				setStatus(STATUS_EDEFAULT);
				return;
			case ReviewsPackage.REQUIREMENT_ENTRY__BY:
				setBy((IUser) null);
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
			case ReviewsPackage.REQUIREMENT_ENTRY__STATUS:
				return status != STATUS_EDEFAULT;
			case ReviewsPackage.REQUIREMENT_ENTRY__BY:
				return by != null;
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
		result.append(" (status: "); //$NON-NLS-1$
		result.append(status);
		result.append(')');
		return result.toString();
	}

} //RequirementEntry
