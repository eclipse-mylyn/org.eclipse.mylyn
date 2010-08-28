/**
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.builds.internal.core;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.mylyn.builds.core.EditType;
import org.eclipse.mylyn.builds.core.IChangeArtifact;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Change Artifact</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.builds.internal.core.ChangeArtifact#getFile <em>File</em>}</li>
 *   <li>{@link org.eclipse.mylyn.builds.internal.core.ChangeArtifact#getRelativePath <em>Relative Path</em>}</li>
 *   <li>{@link org.eclipse.mylyn.builds.internal.core.ChangeArtifact#getPrevRevision <em>Prev Revision</em>}</li>
 *   <li>{@link org.eclipse.mylyn.builds.internal.core.ChangeArtifact#getRevision <em>Revision</em>}</li>
 *   <li>{@link org.eclipse.mylyn.builds.internal.core.ChangeArtifact#isDead <em>Dead</em>}</li>
 *   <li>{@link org.eclipse.mylyn.builds.internal.core.ChangeArtifact#getEditType <em>Edit Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ChangeArtifact extends EObjectImpl implements IChangeArtifact {
	/**
	 * The default value of the '{@link #getFile() <em>File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFile()
	 * @generated
	 * @ordered
	 */
	protected static final String FILE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFile() <em>File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFile()
	 * @generated
	 * @ordered
	 */
	protected String file = FILE_EDEFAULT;

	/**
	 * The default value of the '{@link #getRelativePath() <em>Relative Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRelativePath()
	 * @generated
	 * @ordered
	 */
	protected static final String RELATIVE_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRelativePath() <em>Relative Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRelativePath()
	 * @generated
	 * @ordered
	 */
	protected String relativePath = RELATIVE_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getPrevRevision() <em>Prev Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrevRevision()
	 * @generated
	 * @ordered
	 */
	protected static final String PREV_REVISION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPrevRevision() <em>Prev Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrevRevision()
	 * @generated
	 * @ordered
	 */
	protected String prevRevision = PREV_REVISION_EDEFAULT;

	/**
	 * The default value of the '{@link #getRevision() <em>Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRevision()
	 * @generated
	 * @ordered
	 */
	protected static final String REVISION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRevision() <em>Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRevision()
	 * @generated
	 * @ordered
	 */
	protected String revision = REVISION_EDEFAULT;

	/**
	 * The default value of the '{@link #isDead() <em>Dead</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDead()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DEAD_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDead() <em>Dead</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDead()
	 * @generated
	 * @ordered
	 */
	protected boolean dead = DEAD_EDEFAULT;

	/**
	 * The default value of the '{@link #getEditType() <em>Edit Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEditType()
	 * @generated
	 * @ordered
	 */
	protected static final EditType EDIT_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEditType() <em>Edit Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEditType()
	 * @generated
	 * @ordered
	 */
	protected EditType editType = EDIT_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ChangeArtifact() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.CHANGE_ARTIFACT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFile() {
		return file;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFile(String newFile) {
		String oldFile = file;
		file = newFile;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE_ARTIFACT__FILE, oldFile, file));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRelativePath() {
		return relativePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRelativePath(String newRelativePath) {
		String oldRelativePath = relativePath;
		relativePath = newRelativePath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE_ARTIFACT__RELATIVE_PATH,
					oldRelativePath, relativePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPrevRevision() {
		return prevRevision;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPrevRevision(String newPrevRevision) {
		String oldPrevRevision = prevRevision;
		prevRevision = newPrevRevision;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE_ARTIFACT__PREV_REVISION,
					oldPrevRevision, prevRevision));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRevision(String newRevision) {
		String oldRevision = revision;
		revision = newRevision;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE_ARTIFACT__REVISION, oldRevision,
					revision));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDead(boolean newDead) {
		boolean oldDead = dead;
		dead = newDead;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE_ARTIFACT__DEAD, oldDead, dead));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EditType getEditType() {
		return editType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEditType(EditType newEditType) {
		EditType oldEditType = editType;
		editType = newEditType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE_ARTIFACT__EDIT_TYPE, oldEditType,
					editType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case BuildPackage.CHANGE_ARTIFACT__FILE:
			return getFile();
		case BuildPackage.CHANGE_ARTIFACT__RELATIVE_PATH:
			return getRelativePath();
		case BuildPackage.CHANGE_ARTIFACT__PREV_REVISION:
			return getPrevRevision();
		case BuildPackage.CHANGE_ARTIFACT__REVISION:
			return getRevision();
		case BuildPackage.CHANGE_ARTIFACT__DEAD:
			return isDead();
		case BuildPackage.CHANGE_ARTIFACT__EDIT_TYPE:
			return getEditType();
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
		case BuildPackage.CHANGE_ARTIFACT__FILE:
			setFile((String) newValue);
			return;
		case BuildPackage.CHANGE_ARTIFACT__RELATIVE_PATH:
			setRelativePath((String) newValue);
			return;
		case BuildPackage.CHANGE_ARTIFACT__PREV_REVISION:
			setPrevRevision((String) newValue);
			return;
		case BuildPackage.CHANGE_ARTIFACT__REVISION:
			setRevision((String) newValue);
			return;
		case BuildPackage.CHANGE_ARTIFACT__DEAD:
			setDead((Boolean) newValue);
			return;
		case BuildPackage.CHANGE_ARTIFACT__EDIT_TYPE:
			setEditType((EditType) newValue);
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
		case BuildPackage.CHANGE_ARTIFACT__FILE:
			setFile(FILE_EDEFAULT);
			return;
		case BuildPackage.CHANGE_ARTIFACT__RELATIVE_PATH:
			setRelativePath(RELATIVE_PATH_EDEFAULT);
			return;
		case BuildPackage.CHANGE_ARTIFACT__PREV_REVISION:
			setPrevRevision(PREV_REVISION_EDEFAULT);
			return;
		case BuildPackage.CHANGE_ARTIFACT__REVISION:
			setRevision(REVISION_EDEFAULT);
			return;
		case BuildPackage.CHANGE_ARTIFACT__DEAD:
			setDead(DEAD_EDEFAULT);
			return;
		case BuildPackage.CHANGE_ARTIFACT__EDIT_TYPE:
			setEditType(EDIT_TYPE_EDEFAULT);
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
		case BuildPackage.CHANGE_ARTIFACT__FILE:
			return FILE_EDEFAULT == null ? file != null : !FILE_EDEFAULT.equals(file);
		case BuildPackage.CHANGE_ARTIFACT__RELATIVE_PATH:
			return RELATIVE_PATH_EDEFAULT == null ? relativePath != null : !RELATIVE_PATH_EDEFAULT.equals(relativePath);
		case BuildPackage.CHANGE_ARTIFACT__PREV_REVISION:
			return PREV_REVISION_EDEFAULT == null ? prevRevision != null : !PREV_REVISION_EDEFAULT.equals(prevRevision);
		case BuildPackage.CHANGE_ARTIFACT__REVISION:
			return REVISION_EDEFAULT == null ? revision != null : !REVISION_EDEFAULT.equals(revision);
		case BuildPackage.CHANGE_ARTIFACT__DEAD:
			return dead != DEAD_EDEFAULT;
		case BuildPackage.CHANGE_ARTIFACT__EDIT_TYPE:
			return EDIT_TYPE_EDEFAULT == null ? editType != null : !EDIT_TYPE_EDEFAULT.equals(editType);
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
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (file: "); //$NON-NLS-1$
		result.append(file);
		result.append(", relativePath: "); //$NON-NLS-1$
		result.append(relativePath);
		result.append(", prevRevision: "); //$NON-NLS-1$
		result.append(prevRevision);
		result.append(", revision: "); //$NON-NLS-1$
		result.append(revision);
		result.append(", dead: "); //$NON-NLS-1$
		result.append(dead);
		result.append(", editType: "); //$NON-NLS-1$
		result.append(editType);
		result.append(')');
		return result.toString();
	}

} //ChangeArtifact
