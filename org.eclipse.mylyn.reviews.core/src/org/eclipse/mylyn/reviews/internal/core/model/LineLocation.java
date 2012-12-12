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

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Line Location</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.LineLocation#getRanges <em>Ranges</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.LineLocation#getRangeMin <em>Range Min</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.LineLocation#getRangeMax <em>Range Max</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class LineLocation extends Location implements ILineLocation {
	/**
	 * The cached value of the '{@link #getRanges() <em>Ranges</em>}' containment reference list. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see #getRanges()
	 * @generated
	 * @ordered
	 */
	protected EList<ILineRange> ranges;

	/**
	 * The default value of the '{@link #getRangeMin() <em>Range Min</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getRangeMin()
	 * @generated
	 * @ordered
	 */
	protected static final int RANGE_MIN_EDEFAULT = 0;

	/**
	 * The default value of the '{@link #getRangeMax() <em>Range Max</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getRangeMax()
	 * @generated
	 * @ordered
	 */
	protected static final int RANGE_MAX_EDEFAULT = 0;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected LineLocation() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.LINE_LOCATION;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<ILineRange> getRanges() {
		if (ranges == null) {
			ranges = new EObjectContainmentEList.Resolving<ILineRange>(ILineRange.class, this,
					ReviewsPackage.LINE_LOCATION__RANGES);
		}
		return ranges;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public int getRangeMin() {
		int min = -1;
		for (ILineRange range : getRanges()) {
			if (min == -1 || range.getStart() < min) {
				min = range.getStart();
			}
		}
		return (min == -1) ? 0 : min;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public int getRangeMax() {
		int max = -1;
		for (ILineRange range : getRanges()) {
			if (max == -1 || range.getStart() > max) {
				max = range.getStart();
			}
		}
		return (max == -1) ? 0 : max;
	}

	/**
	 * @generated NOT
	 */
	@Override
	public long getIndex() {
		return getRangeMin();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.LINE_LOCATION__RANGES:
			return ((InternalEList<?>) getRanges()).basicRemove(otherEnd, msgs);
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
		case ReviewsPackage.LINE_LOCATION__RANGES:
			return getRanges();
		case ReviewsPackage.LINE_LOCATION__RANGE_MIN:
			return getRangeMin();
		case ReviewsPackage.LINE_LOCATION__RANGE_MAX:
			return getRangeMax();
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
		case ReviewsPackage.LINE_LOCATION__RANGES:
			getRanges().clear();
			getRanges().addAll((Collection<? extends ILineRange>) newValue);
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
		case ReviewsPackage.LINE_LOCATION__RANGES:
			getRanges().clear();
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
		case ReviewsPackage.LINE_LOCATION__RANGES:
			return ranges != null && !ranges.isEmpty();
		case ReviewsPackage.LINE_LOCATION__RANGE_MIN:
			return getRangeMin() != RANGE_MIN_EDEFAULT;
		case ReviewsPackage.LINE_LOCATION__RANGE_MAX:
			return getRangeMax() != RANGE_MAX_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

} //LineLocation
