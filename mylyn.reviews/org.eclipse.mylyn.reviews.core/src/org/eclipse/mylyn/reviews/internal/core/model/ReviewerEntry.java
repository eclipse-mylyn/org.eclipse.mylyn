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

import java.util.Map;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Reviewer Entry</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewerEntry#getApprovals <em>Approvals</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ReviewerEntry extends EObjectImpl implements IReviewerEntry {
	/**
	 * The cached value of the '{@link #getApprovals() <em>Approvals</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getApprovals()
	 * @generated
	 * @ordered
	 */
	protected EMap<IApprovalType, Integer> approvals;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ReviewerEntry() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REVIEWER_ENTRY;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Map<IApprovalType, Integer> getApprovals() {
		if (approvals == null) {
			approvals = new EcoreEMap<>(ReviewsPackage.Literals.APPROVAL_VALUE_MAP,
					ApprovalValueMap.class, this, ReviewsPackage.REVIEWER_ENTRY__APPROVALS);
		}
		return approvals.map();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ReviewsPackage.REVIEWER_ENTRY__APPROVALS:
				return ((InternalEList<?>) ((EMap.InternalMapView<IApprovalType, Integer>) getApprovals()).eMap())
						.basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ReviewsPackage.REVIEWER_ENTRY__APPROVALS:
				if (coreType) {
					return ((EMap.InternalMapView<IApprovalType, Integer>) getApprovals()).eMap();
				} else {
					return getApprovals();
				}
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
			case ReviewsPackage.REVIEWER_ENTRY__APPROVALS:
				((EStructuralFeature.Setting) ((EMap.InternalMapView<IApprovalType, Integer>) getApprovals()).eMap())
						.set(newValue);
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
			case ReviewsPackage.REVIEWER_ENTRY__APPROVALS:
				getApprovals().clear();
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
			case ReviewsPackage.REVIEWER_ENTRY__APPROVALS:
				return approvals != null && !approvals.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ReviewerEntry
