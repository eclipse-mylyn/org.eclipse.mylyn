/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx.impl;

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

import org.eclipse.mylyn.docs.epub.ncx.Content;
import org.eclipse.mylyn.docs.epub.ncx.NCXPackage;
import org.eclipse.mylyn.docs.epub.ncx.NavLabel;
import org.eclipse.mylyn.docs.epub.ncx.NavTarget;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Nav Target</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavTargetImpl#getNavLabels <em>Nav Labels</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavTargetImpl#getContent <em>Content</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavTargetImpl#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavTargetImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavTargetImpl#getPlayOrder <em>Play Order</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavTargetImpl#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NavTargetImpl extends EObjectImpl implements NavTarget {
	/**
	 * The cached value of the '{@link #getNavLabels() <em>Nav Labels</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNavLabels()
	 * @generated
	 * @ordered
	 */
	protected EList<NavLabel> navLabels;

	/**
	 * The cached value of the '{@link #getContent() <em>Content</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContent()
	 * @generated
	 * @ordered
	 */
	protected Content content;

	/**
	 * The default value of the '{@link #getClass_() <em>Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClass_()
	 * @generated
	 * @ordered
	 */
	protected static final Object CLASS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getClass_() <em>Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClass_()
	 * @generated
	 * @ordered
	 */
	protected Object class_ = CLASS_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getPlayOrder() <em>Play Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPlayOrder()
	 * @generated
	 * @ordered
	 */
	protected static final Object PLAY_ORDER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPlayOrder() <em>Play Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPlayOrder()
	 * @generated
	 * @ordered
	 */
	protected Object playOrder = PLAY_ORDER_EDEFAULT;

	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final Object VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected Object value = VALUE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NavTargetImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return NCXPackage.Literals.NAV_TARGET;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<NavLabel> getNavLabels() {
		if (navLabels == null) {
			navLabels = new EObjectContainmentEList<NavLabel>(NavLabel.class, this, NCXPackage.NAV_TARGET__NAV_LABELS);
		}
		return navLabels;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Content getContent() {
		return content;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetContent(Content newContent, NotificationChain msgs) {
		Content oldContent = content;
		content = newContent;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_TARGET__CONTENT, oldContent, newContent);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContent(Content newContent) {
		if (newContent != content) {
			NotificationChain msgs = null;
			if (content != null)
				msgs = ((InternalEObject)content).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NAV_TARGET__CONTENT, null, msgs);
			if (newContent != null)
				msgs = ((InternalEObject)newContent).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NAV_TARGET__CONTENT, null, msgs);
			msgs = basicSetContent(newContent, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_TARGET__CONTENT, newContent, newContent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getClass_() {
		return class_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setClass(Object newClass) {
		Object oldClass = class_;
		class_ = newClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_TARGET__CLASS, oldClass, class_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_TARGET__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getPlayOrder() {
		return playOrder;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPlayOrder(Object newPlayOrder) {
		Object oldPlayOrder = playOrder;
		playOrder = newPlayOrder;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_TARGET__PLAY_ORDER, oldPlayOrder, playOrder));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValue(Object newValue) {
		Object oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_TARGET__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case NCXPackage.NAV_TARGET__NAV_LABELS:
				return ((InternalEList<?>)getNavLabels()).basicRemove(otherEnd, msgs);
			case NCXPackage.NAV_TARGET__CONTENT:
				return basicSetContent(null, msgs);
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
			case NCXPackage.NAV_TARGET__NAV_LABELS:
				return getNavLabels();
			case NCXPackage.NAV_TARGET__CONTENT:
				return getContent();
			case NCXPackage.NAV_TARGET__CLASS:
				return getClass_();
			case NCXPackage.NAV_TARGET__ID:
				return getId();
			case NCXPackage.NAV_TARGET__PLAY_ORDER:
				return getPlayOrder();
			case NCXPackage.NAV_TARGET__VALUE:
				return getValue();
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
			case NCXPackage.NAV_TARGET__NAV_LABELS:
				getNavLabels().clear();
				getNavLabels().addAll((Collection<? extends NavLabel>)newValue);
				return;
			case NCXPackage.NAV_TARGET__CONTENT:
				setContent((Content)newValue);
				return;
			case NCXPackage.NAV_TARGET__CLASS:
				setClass(newValue);
				return;
			case NCXPackage.NAV_TARGET__ID:
				setId((String)newValue);
				return;
			case NCXPackage.NAV_TARGET__PLAY_ORDER:
				setPlayOrder(newValue);
				return;
			case NCXPackage.NAV_TARGET__VALUE:
				setValue(newValue);
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
			case NCXPackage.NAV_TARGET__NAV_LABELS:
				getNavLabels().clear();
				return;
			case NCXPackage.NAV_TARGET__CONTENT:
				setContent((Content)null);
				return;
			case NCXPackage.NAV_TARGET__CLASS:
				setClass(CLASS_EDEFAULT);
				return;
			case NCXPackage.NAV_TARGET__ID:
				setId(ID_EDEFAULT);
				return;
			case NCXPackage.NAV_TARGET__PLAY_ORDER:
				setPlayOrder(PLAY_ORDER_EDEFAULT);
				return;
			case NCXPackage.NAV_TARGET__VALUE:
				setValue(VALUE_EDEFAULT);
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
			case NCXPackage.NAV_TARGET__NAV_LABELS:
				return navLabels != null && !navLabels.isEmpty();
			case NCXPackage.NAV_TARGET__CONTENT:
				return content != null;
			case NCXPackage.NAV_TARGET__CLASS:
				return CLASS_EDEFAULT == null ? class_ != null : !CLASS_EDEFAULT.equals(class_);
			case NCXPackage.NAV_TARGET__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case NCXPackage.NAV_TARGET__PLAY_ORDER:
				return PLAY_ORDER_EDEFAULT == null ? playOrder != null : !PLAY_ORDER_EDEFAULT.equals(playOrder);
			case NCXPackage.NAV_TARGET__VALUE:
				return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
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
		result.append(" (class: ");
		result.append(class_);
		result.append(", id: ");
		result.append(id);
		result.append(", playOrder: ");
		result.append(playOrder);
		result.append(", value: ");
		result.append(value);
		result.append(')');
		return result.toString();
	}

} //NavTargetImpl
