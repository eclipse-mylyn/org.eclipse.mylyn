/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.mylyn.docs.epub.ncx.Head;
import org.eclipse.mylyn.docs.epub.ncx.Meta;
import org.eclipse.mylyn.docs.epub.ncx.NCXPackage;
import org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Head</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.HeadImpl#getGroups <em>Groups</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.HeadImpl#getSmilCustomTests <em>Smil Custom Tests</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.HeadImpl#getMetas <em>Metas</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HeadImpl extends EObjectImpl implements Head {
	/**
	 * The cached value of the '{@link #getGroups() <em>Groups</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroups()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap groups;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HeadImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return NCXPackage.Literals.HEAD;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroups() {
		if (groups == null) {
			groups = new BasicFeatureMap(this, NCXPackage.HEAD__GROUPS);
		}
		return groups;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SmilCustomTest> getSmilCustomTests() {
		return getGroups().list(NCXPackage.Literals.HEAD__SMIL_CUSTOM_TESTS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Meta> getMetas() {
		return getGroups().list(NCXPackage.Literals.HEAD__METAS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case NCXPackage.HEAD__GROUPS:
				return ((InternalEList<?>)getGroups()).basicRemove(otherEnd, msgs);
			case NCXPackage.HEAD__SMIL_CUSTOM_TESTS:
				return ((InternalEList<?>)getSmilCustomTests()).basicRemove(otherEnd, msgs);
			case NCXPackage.HEAD__METAS:
				return ((InternalEList<?>)getMetas()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case NCXPackage.HEAD__GROUPS:
				if (coreType) return getGroups();
				return ((FeatureMap.Internal)getGroups()).getWrapper();
			case NCXPackage.HEAD__SMIL_CUSTOM_TESTS:
				return getSmilCustomTests();
			case NCXPackage.HEAD__METAS:
				return getMetas();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case NCXPackage.HEAD__GROUPS:
				((FeatureMap.Internal)getGroups()).set(newValue);
				return;
			case NCXPackage.HEAD__SMIL_CUSTOM_TESTS:
				getSmilCustomTests().clear();
				getSmilCustomTests().addAll((Collection<? extends SmilCustomTest>)newValue);
				return;
			case NCXPackage.HEAD__METAS:
				getMetas().clear();
				getMetas().addAll((Collection<? extends Meta>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case NCXPackage.HEAD__GROUPS:
				getGroups().clear();
				return;
			case NCXPackage.HEAD__SMIL_CUSTOM_TESTS:
				getSmilCustomTests().clear();
				return;
			case NCXPackage.HEAD__METAS:
				getMetas().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case NCXPackage.HEAD__GROUPS:
				return groups != null && !groups.isEmpty();
			case NCXPackage.HEAD__SMIL_CUSTOM_TESTS:
				return !getSmilCustomTests().isEmpty();
			case NCXPackage.HEAD__METAS:
				return !getMetas().isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (groups: ");
		result.append(groups);
		result.append(')');
		return result.toString();
	}

} //HeadImpl
