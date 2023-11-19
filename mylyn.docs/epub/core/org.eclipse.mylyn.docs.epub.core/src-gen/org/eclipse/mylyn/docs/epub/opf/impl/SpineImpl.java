/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.mylyn.docs.epub.opf.Itemref;
import org.eclipse.mylyn.docs.epub.opf.OPFPackage;
import org.eclipse.mylyn.docs.epub.opf.Spine;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Spine</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.SpineImpl#getSpineItems <em>Spine Items</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.SpineImpl#getToc <em>Toc</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SpineImpl extends EObjectImpl implements Spine {
	/**
	 * The cached value of the '{@link #getSpineItems() <em>Spine Items</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpineItems()
	 * @generated
	 * @ordered
	 */
	protected EList<Itemref> spineItems;

	/**
	 * The default value of the '{@link #getToc() <em>Toc</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getToc()
	 * @generated
	 * @ordered
	 */
	protected static final String TOC_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getToc() <em>Toc</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getToc()
	 * @generated
	 * @ordered
	 */
	protected String toc = TOC_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SpineImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OPFPackage.Literals.SPINE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Itemref> getSpineItems() {
		if (spineItems == null) {
			spineItems = new EObjectContainmentEList<Itemref>(Itemref.class, this, OPFPackage.SPINE__SPINE_ITEMS);
		}
		return spineItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getToc() {
		return toc;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setToc(String newToc) {
		String oldToc = toc;
		toc = newToc;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.SPINE__TOC, oldToc, toc));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OPFPackage.SPINE__SPINE_ITEMS:
				return ((InternalEList<?>)getSpineItems()).basicRemove(otherEnd, msgs);
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
			case OPFPackage.SPINE__SPINE_ITEMS:
				return getSpineItems();
			case OPFPackage.SPINE__TOC:
				return getToc();
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
			case OPFPackage.SPINE__SPINE_ITEMS:
				getSpineItems().clear();
				getSpineItems().addAll((Collection<? extends Itemref>)newValue);
				return;
			case OPFPackage.SPINE__TOC:
				setToc((String)newValue);
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
			case OPFPackage.SPINE__SPINE_ITEMS:
				getSpineItems().clear();
				return;
			case OPFPackage.SPINE__TOC:
				setToc(TOC_EDEFAULT);
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
			case OPFPackage.SPINE__SPINE_ITEMS:
				return spineItems != null && !spineItems.isEmpty();
			case OPFPackage.SPINE__TOC:
				return TOC_EDEFAULT == null ? toc != null : !TOC_EDEFAULT.equals(toc);
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
		result.append(" (toc: "); //$NON-NLS-1$
		result.append(toc);
		result.append(')');
		return result.toString();
	}

} //SpineImpl
