/**
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.internal.core.model;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.mylyn.reviews.core.model.IRequirementReviewState;
import org.eclipse.mylyn.reviews.core.model.RequirementStatus;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Requirement Review State</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.RequirementReviewState#getStatus <em>Status</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class RequirementReviewState extends ReviewState implements IRequirementReviewState {
	/**
	 * The default value of the '{@link #getStatus() <em>Status</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected static final RequirementStatus STATUS_EDEFAULT = RequirementStatus.UNKNOWN;

	/**
	 * The cached value of the '{@link #getStatus() <em>Status</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected RequirementStatus status = STATUS_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected RequirementReviewState() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REQUIREMENT_REVIEW_STATE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public RequirementStatus getStatus() {
		return status;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public String getDescriptor() {
		return status.getName();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setStatus(RequirementStatus newStatus) {
		RequirementStatus oldStatus = status;
		status = newStatus == null ? STATUS_EDEFAULT : newStatus;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REQUIREMENT_REVIEW_STATE__STATUS,
					oldStatus, status));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReviewsPackage.REQUIREMENT_REVIEW_STATE__STATUS:
			return getStatus();
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
		case ReviewsPackage.REQUIREMENT_REVIEW_STATE__STATUS:
			setStatus((RequirementStatus) newValue);
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
		case ReviewsPackage.REQUIREMENT_REVIEW_STATE__STATUS:
			setStatus(STATUS_EDEFAULT);
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
		case ReviewsPackage.REQUIREMENT_REVIEW_STATE__STATUS:
			return status != STATUS_EDEFAULT;
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
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (status: "); //$NON-NLS-1$
		result.append(status);
		result.append(')');
		return result.toString();
	}

} //RequirementReviewState
