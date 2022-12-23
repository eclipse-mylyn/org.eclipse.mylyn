/**
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.internal.core.model;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.team.core.history.IFileRevision;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>File Version</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.FileVersion#getPath <em>Path</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.FileVersion#getDescription <em>Description</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.FileVersion#getContent <em>Content</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.FileVersion#getFile <em>File</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.FileVersion#getFileRevision <em>File Revision</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.FileVersion#getBinaryContent <em>Binary Content</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class FileVersion extends ReviewItem implements IFileVersion {
	/**
	 * The default value of the '{@link #getPath() <em>Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getPath()
	 * @generated
	 * @ordered
	 */
	protected static final String PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPath() <em>Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getPath()
	 * @generated
	 * @ordered
	 */
	protected String path = PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getContent() <em>Content</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getContent()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContent() <em>Content</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getContent()
	 * @generated
	 * @ordered
	 */
	protected String content = CONTENT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getFile() <em>File</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getFile()
	 * @generated
	 * @ordered
	 */
	protected IFileItem file;

	/**
	 * The default value of the '{@link #getFileRevision() <em>File Revision</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getFileRevision()
	 * @generated
	 * @ordered
	 */
	protected static final IFileRevision FILE_REVISION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFileRevision() <em>File Revision</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getFileRevision()
	 * @generated
	 * @ordered
	 */
	protected IFileRevision fileRevision = FILE_REVISION_EDEFAULT;

	/**
	 * The default value of the '{@link #getBinaryContent() <em>Binary Content</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getBinaryContent()
	 * @generated
	 * @ordered
	 */
	protected static final byte[] BINARY_CONTENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBinaryContent() <em>Binary Content</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getBinaryContent()
	 * @generated
	 * @ordered
	 */
	protected byte[] binaryContent = BINARY_CONTENT_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected FileVersion() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.FILE_VERSION;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getPath() {
		return path;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setPath(String newPath) {
		String oldPath = path;
		path = newPath;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_VERSION__PATH, oldPath, path));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_VERSION__DESCRIPTION,
					oldDescription, description));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getContent() {
		return content;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setContent(String newContent) {
		String oldContent = content;
		content = newContent;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_VERSION__CONTENT, oldContent,
					content));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileItem getFile() {
		if (file != null && file.eIsProxy()) {
			InternalEObject oldFile = (InternalEObject) file;
			file = (IFileItem) eResolveProxy(oldFile);
			if (file != oldFile) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.FILE_VERSION__FILE,
							oldFile, file));
				}
			}
		}
		return file;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public IReview getReview() {
		return getFile().getReview();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileItem basicGetFile() {
		return file;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setFile(IFileItem newFile) {
		IFileItem oldFile = file;
		file = newFile;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_VERSION__FILE, oldFile, file));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileRevision getFileRevision() {
		return fileRevision;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setFileRevision(IFileRevision newFileRevision) {
		IFileRevision oldFileRevision = fileRevision;
		fileRevision = newFileRevision;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_VERSION__FILE_REVISION,
					oldFileRevision, fileRevision));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public byte[] getBinaryContent() {
		return binaryContent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setBinaryContent(byte[] newBinaryContent) {
		byte[] oldBinaryContent = binaryContent;
		binaryContent = newBinaryContent;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_VERSION__BINARY_CONTENT,
					oldBinaryContent, binaryContent));
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
		case ReviewsPackage.FILE_VERSION__PATH:
			return getPath();
		case ReviewsPackage.FILE_VERSION__DESCRIPTION:
			return getDescription();
		case ReviewsPackage.FILE_VERSION__CONTENT:
			return getContent();
		case ReviewsPackage.FILE_VERSION__FILE:
			if (resolve) {
				return getFile();
			}
			return basicGetFile();
		case ReviewsPackage.FILE_VERSION__FILE_REVISION:
			return getFileRevision();
		case ReviewsPackage.FILE_VERSION__BINARY_CONTENT:
			return getBinaryContent();
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
		case ReviewsPackage.FILE_VERSION__PATH:
			setPath((String) newValue);
			return;
		case ReviewsPackage.FILE_VERSION__DESCRIPTION:
			setDescription((String) newValue);
			return;
		case ReviewsPackage.FILE_VERSION__CONTENT:
			setContent((String) newValue);
			return;
		case ReviewsPackage.FILE_VERSION__FILE:
			setFile((IFileItem) newValue);
			return;
		case ReviewsPackage.FILE_VERSION__FILE_REVISION:
			setFileRevision((IFileRevision) newValue);
			return;
		case ReviewsPackage.FILE_VERSION__BINARY_CONTENT:
			setBinaryContent((byte[]) newValue);
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
		case ReviewsPackage.FILE_VERSION__PATH:
			setPath(PATH_EDEFAULT);
			return;
		case ReviewsPackage.FILE_VERSION__DESCRIPTION:
			setDescription(DESCRIPTION_EDEFAULT);
			return;
		case ReviewsPackage.FILE_VERSION__CONTENT:
			setContent(CONTENT_EDEFAULT);
			return;
		case ReviewsPackage.FILE_VERSION__FILE:
			setFile((IFileItem) null);
			return;
		case ReviewsPackage.FILE_VERSION__FILE_REVISION:
			setFileRevision(FILE_REVISION_EDEFAULT);
			return;
		case ReviewsPackage.FILE_VERSION__BINARY_CONTENT:
			setBinaryContent(BINARY_CONTENT_EDEFAULT);
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
		case ReviewsPackage.FILE_VERSION__PATH:
			return PATH_EDEFAULT == null ? path != null : !PATH_EDEFAULT.equals(path);
		case ReviewsPackage.FILE_VERSION__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
		case ReviewsPackage.FILE_VERSION__CONTENT:
			return CONTENT_EDEFAULT == null ? content != null : !CONTENT_EDEFAULT.equals(content);
		case ReviewsPackage.FILE_VERSION__FILE:
			return file != null;
		case ReviewsPackage.FILE_VERSION__FILE_REVISION:
			return FILE_REVISION_EDEFAULT == null ? fileRevision != null : !FILE_REVISION_EDEFAULT.equals(fileRevision);
		case ReviewsPackage.FILE_VERSION__BINARY_CONTENT:
			return BINARY_CONTENT_EDEFAULT == null
					? binaryContent != null
					: !BINARY_CONTENT_EDEFAULT.equals(binaryContent);
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

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (path: "); //$NON-NLS-1$
		result.append(path);
		result.append(", description: "); //$NON-NLS-1$
		result.append(description);
		result.append(", content: "); //$NON-NLS-1$
		result.append(content);
		result.append(", fileRevision: "); //$NON-NLS-1$
		result.append(fileRevision);
		result.append(", binaryContent: "); //$NON-NLS-1$
		result.append(binaryContent);
		result.append(')');
		return result.toString();
	}

} //FileVersion
