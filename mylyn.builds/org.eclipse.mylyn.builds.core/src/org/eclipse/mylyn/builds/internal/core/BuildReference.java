/**
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.builds.internal.core;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.mylyn.builds.core.IBuildReference;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Reference</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildReference#getPlan <em>Plan</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildReference#getBuild <em>Build</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BuildReference extends EObjectImpl implements IBuildReference {
	/**
	 * The default value of the '{@link #getPlan() <em>Plan</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getPlan()
	 * @generated
	 * @ordered
	 */
	protected static final String PLAN_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPlan() <em>Plan</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getPlan()
	 * @generated
	 * @ordered
	 */
	protected String plan = PLAN_EDEFAULT;

	/**
	 * The default value of the '{@link #getBuild() <em>Build</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getBuild()
	 * @generated
	 * @ordered
	 */
	protected static final String BUILD_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBuild() <em>Build</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getBuild()
	 * @generated
	 * @ordered
	 */
	protected String build = BUILD_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected BuildReference() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.BUILD_REFERENCE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getPlan() {
		return plan;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setPlan(String newPlan) {
		String oldPlan = plan;
		plan = newPlan;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_REFERENCE__PLAN, oldPlan, plan));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getBuild() {
		return build;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setBuild(String newBuild) {
		String oldBuild = build;
		build = newBuild;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_REFERENCE__BUILD, oldBuild,
					build));
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
			case BuildPackage.BUILD_REFERENCE__PLAN:
				return getPlan();
			case BuildPackage.BUILD_REFERENCE__BUILD:
				return getBuild();
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
			case BuildPackage.BUILD_REFERENCE__PLAN:
				setPlan((String) newValue);
				return;
			case BuildPackage.BUILD_REFERENCE__BUILD:
				setBuild((String) newValue);
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
			case BuildPackage.BUILD_REFERENCE__PLAN:
				setPlan(PLAN_EDEFAULT);
				return;
			case BuildPackage.BUILD_REFERENCE__BUILD:
				setBuild(BUILD_EDEFAULT);
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
			case BuildPackage.BUILD_REFERENCE__PLAN:
				return PLAN_EDEFAULT == null ? plan != null : !PLAN_EDEFAULT.equals(plan);
			case BuildPackage.BUILD_REFERENCE__BUILD:
				return BUILD_EDEFAULT == null ? build != null : !BUILD_EDEFAULT.equals(build);
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
		result.append(" (plan: "); //$NON-NLS-1$
		result.append(plan);
		result.append(", build: "); //$NON-NLS-1$
		result.append(build);
		result.append(')');
		return result.toString();
	}

} //BuildReference
