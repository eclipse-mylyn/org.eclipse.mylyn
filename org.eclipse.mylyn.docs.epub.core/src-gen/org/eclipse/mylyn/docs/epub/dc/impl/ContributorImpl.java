/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.dc.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.mylyn.docs.epub.dc.Contributor;
import org.eclipse.mylyn.docs.epub.dc.DCPackage;

import org.eclipse.mylyn.docs.epub.opf.Role;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Contributor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.dc.impl.ContributorImpl#getRole <em>Role</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.dc.impl.ContributorImpl#getFileAs <em>File As</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ContributorImpl extends LocalizedDCTypeImpl implements Contributor {
	/**
	 * The default value of the '{@link #getRole() <em>Role</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRole()
	 * @generated
	 * @ordered
	 */
	protected static final Role ROLE_EDEFAULT = Role.ART_COPYIST;

	/**
	 * The cached value of the '{@link #getRole() <em>Role</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRole()
	 * @generated
	 * @ordered
	 */
	protected Role role = ROLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getFileAs() <em>File As</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFileAs()
	 * @generated
	 * @ordered
	 */
	protected static final String FILE_AS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFileAs() <em>File As</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFileAs()
	 * @generated
	 * @ordered
	 */
	protected String fileAs = FILE_AS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ContributorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DCPackage.Literals.CONTRIBUTOR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRole(Role newRole) {
		Role oldRole = role;
		role = newRole == null ? ROLE_EDEFAULT : newRole;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DCPackage.CONTRIBUTOR__ROLE, oldRole, role));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFileAs() {
		return fileAs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFileAs(String newFileAs) {
		String oldFileAs = fileAs;
		fileAs = newFileAs;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DCPackage.CONTRIBUTOR__FILE_AS, oldFileAs, fileAs));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DCPackage.CONTRIBUTOR__ROLE:
				return getRole();
			case DCPackage.CONTRIBUTOR__FILE_AS:
				return getFileAs();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DCPackage.CONTRIBUTOR__ROLE:
				setRole((Role)newValue);
				return;
			case DCPackage.CONTRIBUTOR__FILE_AS:
				setFileAs((String)newValue);
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
			case DCPackage.CONTRIBUTOR__ROLE:
				setRole(ROLE_EDEFAULT);
				return;
			case DCPackage.CONTRIBUTOR__FILE_AS:
				setFileAs(FILE_AS_EDEFAULT);
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
			case DCPackage.CONTRIBUTOR__ROLE:
				return role != ROLE_EDEFAULT;
			case DCPackage.CONTRIBUTOR__FILE_AS:
				return FILE_AS_EDEFAULT == null ? fileAs != null : !FILE_AS_EDEFAULT.equals(fileAs);
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
		result.append(" (role: "); //$NON-NLS-1$
		result.append(role);
		result.append(", fileAs: "); //$NON-NLS-1$
		result.append(fileAs);
		result.append(')');
		return result.toString();
	}

} //ContributorImpl
