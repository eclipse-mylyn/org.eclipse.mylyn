/**
 * <copyright>
 * </copyright>
 *
 * $Id: ChangeSet.java,v 1.3 2010/09/02 06:23:14 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeSet;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Change Set</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.ChangeSet#getChanges <em>Changes</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.ChangeSet#getKind <em>Kind</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ChangeSet extends EObjectImpl implements IChangeSet {
	/**
	 * The cached value of the '{@link #getChanges() <em>Changes</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getChanges()
	 * @generated
	 * @ordered
	 */
	protected EList<IChange> changes;

	/**
	 * The default value of the '{@link #getKind() <em>Kind</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected static final String KIND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getKind() <em>Kind</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected String kind = KIND_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ChangeSet() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.CHANGE_SET;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Changes</em>' reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IChange> getChanges() {
		if (changes == null) {
			changes = new EObjectContainmentEList<>(IChange.class, this, BuildPackage.CHANGE_SET__CHANGES);
		}
		return changes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Kind</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getKind() {
		return kind;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setKind(String newKind) {
		String oldKind = kind;
		kind = newKind;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE_SET__KIND, oldKind, kind));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BuildPackage.CHANGE_SET__CHANGES:
				return ((InternalEList<?>) getChanges()).basicRemove(otherEnd, msgs);
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
			case BuildPackage.CHANGE_SET__CHANGES:
				return getChanges();
			case BuildPackage.CHANGE_SET__KIND:
				return getKind();
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
			case BuildPackage.CHANGE_SET__CHANGES:
				getChanges().clear();
				getChanges().addAll((Collection<? extends IChange>) newValue);
				return;
			case BuildPackage.CHANGE_SET__KIND:
				setKind((String) newValue);
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
			case BuildPackage.CHANGE_SET__CHANGES:
				getChanges().clear();
				return;
			case BuildPackage.CHANGE_SET__KIND:
				setKind(KIND_EDEFAULT);
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
			case BuildPackage.CHANGE_SET__CHANGES:
				return changes != null && !changes.isEmpty();
			case BuildPackage.CHANGE_SET__KIND:
				return KIND_EDEFAULT == null ? kind != null : !KIND_EDEFAULT.equals(kind);
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
		result.append(" (kind: "); //$NON-NLS-1$
		result.append(kind);
		result.append(')');
		return result.toString();
	}

} // ChangeSet
