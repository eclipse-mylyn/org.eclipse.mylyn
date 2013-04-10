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

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReviewState;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Repository</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Repository#getApprovalTypes <em>Approval Types</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Repository#getReviewStates <em>Review States</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Repository extends ReviewGroup implements IRepository {
	/**
	 * The cached value of the '{@link #getApprovalTypes() <em>Approval Types</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getApprovalTypes()
	 * @generated
	 * @ordered
	 */
	protected EList<IApprovalType> approvalTypes;

	/**
	 * The cached value of the '{@link #getReviewStates() <em>Review States</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getReviewStates()
	 * @generated
	 * @ordered
	 */
	protected EList<IReviewState> reviewStates;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Repository() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REPOSITORY;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IApprovalType> getApprovalTypes() {
		if (approvalTypes == null) {
			approvalTypes = new EObjectContainmentEList.Resolving<IApprovalType>(IApprovalType.class, this,
					ReviewsPackage.REPOSITORY__APPROVAL_TYPES);
		}
		return approvalTypes;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IReviewState> getReviewStates() {
		if (reviewStates == null) {
			reviewStates = new EObjectContainmentEList.Resolving<IReviewState>(IReviewState.class, this,
					ReviewsPackage.REPOSITORY__REVIEW_STATES);
		}
		return reviewStates;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
			return ((InternalEList<?>) getApprovalTypes()).basicRemove(otherEnd, msgs);
		case ReviewsPackage.REPOSITORY__REVIEW_STATES:
			return ((InternalEList<?>) getReviewStates()).basicRemove(otherEnd, msgs);
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
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
			return getApprovalTypes();
		case ReviewsPackage.REPOSITORY__REVIEW_STATES:
			return getReviewStates();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
			getApprovalTypes().clear();
			getApprovalTypes().addAll((Collection<? extends IApprovalType>) newValue);
			return;
		case ReviewsPackage.REPOSITORY__REVIEW_STATES:
			getReviewStates().clear();
			getReviewStates().addAll((Collection<? extends IReviewState>) newValue);
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
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
			getApprovalTypes().clear();
			return;
		case ReviewsPackage.REPOSITORY__REVIEW_STATES:
			getReviewStates().clear();
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
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
			return approvalTypes != null && !approvalTypes.isEmpty();
		case ReviewsPackage.REPOSITORY__REVIEW_STATES:
			return reviewStates != null && !reviewStates.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //Repository
