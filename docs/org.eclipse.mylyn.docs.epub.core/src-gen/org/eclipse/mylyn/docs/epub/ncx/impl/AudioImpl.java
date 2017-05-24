/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.mylyn.docs.epub.ncx.Audio;
import org.eclipse.mylyn.docs.epub.ncx.NCXPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Audio</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.AudioImpl#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.AudioImpl#getClipBegin <em>Clip Begin</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.AudioImpl#getClipEnd <em>Clip End</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.AudioImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.AudioImpl#getSrc <em>Src</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AudioImpl extends EObjectImpl implements Audio {
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
	 * The default value of the '{@link #getClipBegin() <em>Clip Begin</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClipBegin()
	 * @generated
	 * @ordered
	 */
	protected static final String CLIP_BEGIN_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getClipBegin() <em>Clip Begin</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClipBegin()
	 * @generated
	 * @ordered
	 */
	protected String clipBegin = CLIP_BEGIN_EDEFAULT;

	/**
	 * The default value of the '{@link #getClipEnd() <em>Clip End</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClipEnd()
	 * @generated
	 * @ordered
	 */
	protected static final String CLIP_END_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getClipEnd() <em>Clip End</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClipEnd()
	 * @generated
	 * @ordered
	 */
	protected String clipEnd = CLIP_END_EDEFAULT;

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
	 * The default value of the '{@link #getSrc() <em>Src</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSrc()
	 * @generated
	 * @ordered
	 */
	protected static final String SRC_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSrc() <em>Src</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSrc()
	 * @generated
	 * @ordered
	 */
	protected String src = SRC_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AudioImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return NCXPackage.Literals.AUDIO;
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
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.AUDIO__CLASS, oldClass, class_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getClipBegin() {
		return clipBegin;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setClipBegin(String newClipBegin) {
		String oldClipBegin = clipBegin;
		clipBegin = newClipBegin;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.AUDIO__CLIP_BEGIN, oldClipBegin, clipBegin));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getClipEnd() {
		return clipEnd;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setClipEnd(String newClipEnd) {
		String oldClipEnd = clipEnd;
		clipEnd = newClipEnd;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.AUDIO__CLIP_END, oldClipEnd, clipEnd));
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
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.AUDIO__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSrc(String newSrc) {
		String oldSrc = src;
		src = newSrc;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.AUDIO__SRC, oldSrc, src));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case NCXPackage.AUDIO__CLASS:
				return getClass_();
			case NCXPackage.AUDIO__CLIP_BEGIN:
				return getClipBegin();
			case NCXPackage.AUDIO__CLIP_END:
				return getClipEnd();
			case NCXPackage.AUDIO__ID:
				return getId();
			case NCXPackage.AUDIO__SRC:
				return getSrc();
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
			case NCXPackage.AUDIO__CLASS:
				setClass(newValue);
				return;
			case NCXPackage.AUDIO__CLIP_BEGIN:
				setClipBegin((String)newValue);
				return;
			case NCXPackage.AUDIO__CLIP_END:
				setClipEnd((String)newValue);
				return;
			case NCXPackage.AUDIO__ID:
				setId((String)newValue);
				return;
			case NCXPackage.AUDIO__SRC:
				setSrc((String)newValue);
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
			case NCXPackage.AUDIO__CLASS:
				setClass(CLASS_EDEFAULT);
				return;
			case NCXPackage.AUDIO__CLIP_BEGIN:
				setClipBegin(CLIP_BEGIN_EDEFAULT);
				return;
			case NCXPackage.AUDIO__CLIP_END:
				setClipEnd(CLIP_END_EDEFAULT);
				return;
			case NCXPackage.AUDIO__ID:
				setId(ID_EDEFAULT);
				return;
			case NCXPackage.AUDIO__SRC:
				setSrc(SRC_EDEFAULT);
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
			case NCXPackage.AUDIO__CLASS:
				return CLASS_EDEFAULT == null ? class_ != null : !CLASS_EDEFAULT.equals(class_);
			case NCXPackage.AUDIO__CLIP_BEGIN:
				return CLIP_BEGIN_EDEFAULT == null ? clipBegin != null : !CLIP_BEGIN_EDEFAULT.equals(clipBegin);
			case NCXPackage.AUDIO__CLIP_END:
				return CLIP_END_EDEFAULT == null ? clipEnd != null : !CLIP_END_EDEFAULT.equals(clipEnd);
			case NCXPackage.AUDIO__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case NCXPackage.AUDIO__SRC:
				return SRC_EDEFAULT == null ? src != null : !SRC_EDEFAULT.equals(src);
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
		result.append(", clipBegin: ");
		result.append(clipBegin);
		result.append(", clipEnd: ");
		result.append(clipEnd);
		result.append(", id: ");
		result.append(id);
		result.append(", src: ");
		result.append(src);
		result.append(')');
		return result.toString();
	}

} //AudioImpl
