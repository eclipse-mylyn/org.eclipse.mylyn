/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ocf.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.mylyn.docs.epub.ocf.OCFPackage;
import org.eclipse.mylyn.docs.epub.ocf.RootFile;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Root File</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ocf.impl.RootFileImpl#getFullPath <em>Full Path</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ocf.impl.RootFileImpl#getMediaType <em>Media Type</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ocf.impl.RootFileImpl#getPublication <em>Publication</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RootFileImpl extends EObjectImpl implements RootFile {
	/**
	 * The default value of the '{@link #getFullPath() <em>Full Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFullPath()
	 * @generated
	 * @ordered
	 */
	protected static final String FULL_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFullPath() <em>Full Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFullPath()
	 * @generated
	 * @ordered
	 */
	protected String fullPath = FULL_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getMediaType() <em>Media Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMediaType()
	 * @generated
	 * @ordered
	 */
	protected static final String MEDIA_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMediaType() <em>Media Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMediaType()
	 * @generated
	 * @ordered
	 */
	protected String mediaType = MEDIA_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getPublication() <em>Publication</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPublication()
	 * @generated
	 * @ordered
	 */
	protected static final Object PUBLICATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPublication() <em>Publication</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPublication()
	 * @generated
	 * @ordered
	 */
	protected Object publication = PUBLICATION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RootFileImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OCFPackage.Literals.ROOT_FILE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFullPath() {
		return fullPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFullPath(String newFullPath) {
		String oldFullPath = fullPath;
		fullPath = newFullPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OCFPackage.ROOT_FILE__FULL_PATH, oldFullPath, fullPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMediaType() {
		return mediaType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMediaType(String newMediaType) {
		String oldMediaType = mediaType;
		mediaType = newMediaType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OCFPackage.ROOT_FILE__MEDIA_TYPE, oldMediaType, mediaType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getPublication() {
		return publication;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPublication(Object newPublication) {
		Object oldPublication = publication;
		publication = newPublication;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OCFPackage.ROOT_FILE__PUBLICATION, oldPublication, publication));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case OCFPackage.ROOT_FILE__FULL_PATH:
				return getFullPath();
			case OCFPackage.ROOT_FILE__MEDIA_TYPE:
				return getMediaType();
			case OCFPackage.ROOT_FILE__PUBLICATION:
				return getPublication();
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
			case OCFPackage.ROOT_FILE__FULL_PATH:
				setFullPath((String)newValue);
				return;
			case OCFPackage.ROOT_FILE__MEDIA_TYPE:
				setMediaType((String)newValue);
				return;
			case OCFPackage.ROOT_FILE__PUBLICATION:
				setPublication(newValue);
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
			case OCFPackage.ROOT_FILE__FULL_PATH:
				setFullPath(FULL_PATH_EDEFAULT);
				return;
			case OCFPackage.ROOT_FILE__MEDIA_TYPE:
				setMediaType(MEDIA_TYPE_EDEFAULT);
				return;
			case OCFPackage.ROOT_FILE__PUBLICATION:
				setPublication(PUBLICATION_EDEFAULT);
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
			case OCFPackage.ROOT_FILE__FULL_PATH:
				return FULL_PATH_EDEFAULT == null ? fullPath != null : !FULL_PATH_EDEFAULT.equals(fullPath);
			case OCFPackage.ROOT_FILE__MEDIA_TYPE:
				return MEDIA_TYPE_EDEFAULT == null ? mediaType != null : !MEDIA_TYPE_EDEFAULT.equals(mediaType);
			case OCFPackage.ROOT_FILE__PUBLICATION:
				return PUBLICATION_EDEFAULT == null ? publication != null : !PUBLICATION_EDEFAULT.equals(publication);
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
		result.append(" (fullPath: ");
		result.append(fullPath);
		result.append(", mediaType: ");
		result.append(mediaType);
		result.append(", publication: ");
		result.append(publication);
		result.append(')');
		return result.toString();
	}

} //RootFileImpl
