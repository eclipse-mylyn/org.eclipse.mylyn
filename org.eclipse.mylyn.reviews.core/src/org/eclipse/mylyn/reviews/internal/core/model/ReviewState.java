/**
 * Copyright (c) 2011 Tasktop Technologies and others.
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
import org.eclipse.mylyn.reviews.core.model.IReviewState;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Review State</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewState#getDescriptor <em>Descriptor</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class ReviewState extends ReviewComponent implements IReviewState {
	/**
	 * The default value of the '{@link #getDescriptor() <em>Descriptor</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getDescriptor()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTOR_EDEFAULT = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ReviewState() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REVIEW_STATE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getDescriptor() {
		// TODO: implement this method to return the 'Descriptor' attribute
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_STATE__DESCRIPTOR:
			return getDescriptor();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_STATE__DESCRIPTOR:
			return DESCRIPTOR_EDEFAULT == null ? getDescriptor() != null : !DESCRIPTOR_EDEFAULT.equals(getDescriptor());
		}
		return super.eIsSet(featureID);
	}

} //ReviewState
