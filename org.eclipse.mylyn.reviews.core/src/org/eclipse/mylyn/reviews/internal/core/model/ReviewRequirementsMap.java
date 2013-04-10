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

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Review Requirements Map</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewRequirementsMap#getTypedKey <em>Key</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewRequirementsMap#getTypedValue <em>Value</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ReviewRequirementsMap extends EObjectImpl implements BasicEMap.Entry<IApprovalType, IRequirementEntry> {
	/**
	 * The cached value of the '{@link #getTypedKey() <em>Key</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTypedKey()
	 * @generated
	 * @ordered
	 */
	protected IApprovalType key;

	/**
	 * The cached value of the '{@link #getTypedValue() <em>Value</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTypedValue()
	 * @generated
	 * @ordered
	 */
	protected IRequirementEntry value;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ReviewRequirementsMap() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REVIEW_REQUIREMENTS_MAP;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IApprovalType getTypedKey() {
		if (key != null && key.eIsProxy()) {
			InternalEObject oldKey = (InternalEObject) key;
			key = (IApprovalType) eResolveProxy(oldKey);
			if (key != oldKey) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							ReviewsPackage.REVIEW_REQUIREMENTS_MAP__KEY, oldKey, key));
			}
		}
		return key;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IApprovalType basicGetTypedKey() {
		return key;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTypedKey(IApprovalType newKey) {
		IApprovalType oldKey = key;
		key = newKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_REQUIREMENTS_MAP__KEY, oldKey,
					key));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IRequirementEntry getTypedValue() {
		if (value != null && value.eIsProxy()) {
			InternalEObject oldValue = (InternalEObject) value;
			value = (IRequirementEntry) eResolveProxy(oldValue);
			if (value != oldValue) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							ReviewsPackage.REVIEW_REQUIREMENTS_MAP__VALUE, oldValue, value));
			}
		}
		return value;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IRequirementEntry basicGetTypedValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTypedValue(IRequirementEntry newValue) {
		IRequirementEntry oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_REQUIREMENTS_MAP__VALUE,
					oldValue, value));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_REQUIREMENTS_MAP__KEY:
			if (resolve)
				return getTypedKey();
			return basicGetTypedKey();
		case ReviewsPackage.REVIEW_REQUIREMENTS_MAP__VALUE:
			if (resolve)
				return getTypedValue();
			return basicGetTypedValue();
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
		case ReviewsPackage.REVIEW_REQUIREMENTS_MAP__KEY:
			setTypedKey((IApprovalType) newValue);
			return;
		case ReviewsPackage.REVIEW_REQUIREMENTS_MAP__VALUE:
			setTypedValue((IRequirementEntry) newValue);
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
		case ReviewsPackage.REVIEW_REQUIREMENTS_MAP__KEY:
			setTypedKey((IApprovalType) null);
			return;
		case ReviewsPackage.REVIEW_REQUIREMENTS_MAP__VALUE:
			setTypedValue((IRequirementEntry) null);
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
		case ReviewsPackage.REVIEW_REQUIREMENTS_MAP__KEY:
			return key != null;
		case ReviewsPackage.REVIEW_REQUIREMENTS_MAP__VALUE:
			return value != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected int hash = -1;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getHash() {
		if (hash == -1) {
			Object theKey = getKey();
			hash = (theKey == null ? 0 : theKey.hashCode());
		}
		return hash;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setHash(int hash) {
		this.hash = hash;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IApprovalType getKey() {
		return getTypedKey();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setKey(IApprovalType key) {
		setTypedKey(key);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IRequirementEntry getValue() {
		return getTypedValue();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IRequirementEntry setValue(IRequirementEntry value) {
		IRequirementEntry oldValue = getValue();
		setTypedValue(value);
		return oldValue;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EMap<IApprovalType, IRequirementEntry> getEMap() {
		EObject container = eContainer();
		return container == null
				? null
				: (EMap<IApprovalType, IRequirementEntry>) container.eGet(eContainmentFeature());
	}

} //ReviewRequirementsMap
