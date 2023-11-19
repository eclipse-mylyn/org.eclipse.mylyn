/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ocf.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.mylyn.docs.epub.ocf.OCFPackage;
import org.eclipse.mylyn.docs.epub.ocf.RootFile;
import org.eclipse.mylyn.docs.epub.ocf.RootFiles;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Root Files</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ocf.impl.RootFilesImpl#getRootfiles <em>Rootfiles</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RootFilesImpl extends EObjectImpl implements RootFiles {
	/**
	 * The cached value of the '{@link #getRootfiles() <em>Rootfiles</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRootfiles()
	 * @generated
	 * @ordered
	 */
	protected EList<RootFile> rootfiles;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RootFilesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OCFPackage.Literals.ROOT_FILES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<RootFile> getRootfiles() {
		if (rootfiles == null) {
			rootfiles = new EObjectContainmentEList<RootFile>(RootFile.class, this, OCFPackage.ROOT_FILES__ROOTFILES);
		}
		return rootfiles;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OCFPackage.ROOT_FILES__ROOTFILES:
				return ((InternalEList<?>)getRootfiles()).basicRemove(otherEnd, msgs);
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
			case OCFPackage.ROOT_FILES__ROOTFILES:
				return getRootfiles();
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
			case OCFPackage.ROOT_FILES__ROOTFILES:
				getRootfiles().clear();
				getRootfiles().addAll((Collection<? extends RootFile>)newValue);
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
			case OCFPackage.ROOT_FILES__ROOTFILES:
				getRootfiles().clear();
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
			case OCFPackage.ROOT_FILES__ROOTFILES:
				return rootfiles != null && !rootfiles.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //RootFilesImpl
