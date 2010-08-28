/**
 * <copyright>
 * </copyright>
 *
 * $Id: File.java,v 1.3 2010/08/28 03:38:02 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.mylyn.builds.core.EditType;
import org.eclipse.mylyn.builds.core.IFile;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>File</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getFile()
 * @model kind="class" superTypes="org.eclipse.mylyn.internal.builds.core.IFile"
 * @generated
 */
public class File extends EObjectImpl implements EObject, IFile {
	/**
	 * The default value of the '{@link #getRelativePath() <em>Relative Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRelativePath()
	 * @generated
	 * @ordered
	 */
	protected static final String RELATIVE_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRelativePath() <em>Relative Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRelativePath()
	 * @generated
	 * @ordered
	 */
	protected String relativePath = RELATIVE_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getPrevRevision() <em>Prev Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getPrevRevision()
	 * @generated
	 * @ordered
	 */
	protected static final String PREV_REVISION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPrevRevision() <em>Prev Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getPrevRevision()
	 * @generated
	 * @ordered
	 */
	protected String prevRevision = PREV_REVISION_EDEFAULT;

	/**
	 * The default value of the '{@link #getRevision() <em>Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRevision()
	 * @generated
	 * @ordered
	 */
	protected static final String REVISION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRevision() <em>Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRevision()
	 * @generated
	 * @ordered
	 */
	protected String revision = REVISION_EDEFAULT;

	/**
	 * The default value of the '{@link #isDead() <em>Dead</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #isDead()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DEAD_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDead() <em>Dead</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #isDead()
	 * @generated
	 * @ordered
	 */
	protected boolean dead = DEAD_EDEFAULT;

	/**
	 * The default value of the '{@link #getEditType() <em>Edit Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getEditType()
	 * @generated
	 * @ordered
	 */
	protected static final EditType EDIT_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEditType() <em>Edit Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getEditType()
	 * @generated
	 * @ordered
	 */
	protected EditType editType = EDIT_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected File() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.FILE;
	}

	/**
	 * Returns the value of the '<em><b>Relative Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Relative Path</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Relative Path</em>' attribute.
	 * @see #setRelativePath(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIFile_RelativePath()
	 * @model
	 * @generated
	 */
	public String getRelativePath() {
		return relativePath;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.File#getRelativePath <em>Relative Path</em>}
	 * ' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Relative Path</em>' attribute.
	 * @see #getRelativePath()
	 * @generated
	 */
	public void setRelativePath(String newRelativePath) {
		String oldRelativePath = relativePath;
		relativePath = newRelativePath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.FILE__RELATIVE_PATH, oldRelativePath,
					relativePath));
	}

	/**
	 * Returns the value of the '<em><b>Prev Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Prev Revision</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Prev Revision</em>' attribute.
	 * @see #setPrevRevision(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIFile_PrevRevision()
	 * @model
	 * @generated
	 */
	public String getPrevRevision() {
		return prevRevision;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.File#getPrevRevision <em>Prev Revision</em>}
	 * ' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Prev Revision</em>' attribute.
	 * @see #getPrevRevision()
	 * @generated
	 */
	public void setPrevRevision(String newPrevRevision) {
		String oldPrevRevision = prevRevision;
		prevRevision = newPrevRevision;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.FILE__PREV_REVISION, oldPrevRevision,
					prevRevision));
	}

	/**
	 * Returns the value of the '<em><b>Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Revision</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Revision</em>' attribute.
	 * @see #setRevision(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIFile_Revision()
	 * @model
	 * @generated
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.File#getRevision <em>Revision</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Revision</em>' attribute.
	 * @see #getRevision()
	 * @generated
	 */
	public void setRevision(String newRevision) {
		String oldRevision = revision;
		revision = newRevision;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.FILE__REVISION, oldRevision, revision));
	}

	/**
	 * Returns the value of the '<em><b>Dead</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dead</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Dead</em>' attribute.
	 * @see #setDead(boolean)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIFile_Dead()
	 * @model
	 * @generated
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.File#isDead <em>Dead</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Dead</em>' attribute.
	 * @see #isDead()
	 * @generated
	 */
	public void setDead(boolean newDead) {
		boolean oldDead = dead;
		dead = newDead;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.FILE__DEAD, oldDead, dead));
	}

	/**
	 * Returns the value of the '<em><b>Edit Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Edit Type</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Edit Type</em>' attribute.
	 * @see #setEditType(EditType)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIFile_EditType()
	 * @model dataType="org.eclipse.mylyn.internal.builds.core.EditType"
	 * @generated
	 */
	public EditType getEditType() {
		return editType;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.File#getEditType <em>Edit Type</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Edit Type</em>' attribute.
	 * @see #getEditType()
	 * @generated
	 */
	public void setEditType(EditType newEditType) {
		EditType oldEditType = editType;
		editType = newEditType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.FILE__EDIT_TYPE, oldEditType, editType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case BuildPackage.FILE__RELATIVE_PATH:
			return getRelativePath();
		case BuildPackage.FILE__PREV_REVISION:
			return getPrevRevision();
		case BuildPackage.FILE__REVISION:
			return getRevision();
		case BuildPackage.FILE__DEAD:
			return isDead();
		case BuildPackage.FILE__EDIT_TYPE:
			return getEditType();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case BuildPackage.FILE__RELATIVE_PATH:
			setRelativePath((String) newValue);
			return;
		case BuildPackage.FILE__PREV_REVISION:
			setPrevRevision((String) newValue);
			return;
		case BuildPackage.FILE__REVISION:
			setRevision((String) newValue);
			return;
		case BuildPackage.FILE__DEAD:
			setDead((Boolean) newValue);
			return;
		case BuildPackage.FILE__EDIT_TYPE:
			setEditType((EditType) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case BuildPackage.FILE__RELATIVE_PATH:
			setRelativePath(RELATIVE_PATH_EDEFAULT);
			return;
		case BuildPackage.FILE__PREV_REVISION:
			setPrevRevision(PREV_REVISION_EDEFAULT);
			return;
		case BuildPackage.FILE__REVISION:
			setRevision(REVISION_EDEFAULT);
			return;
		case BuildPackage.FILE__DEAD:
			setDead(DEAD_EDEFAULT);
			return;
		case BuildPackage.FILE__EDIT_TYPE:
			setEditType(EDIT_TYPE_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case BuildPackage.FILE__RELATIVE_PATH:
			return RELATIVE_PATH_EDEFAULT == null ? relativePath != null : !RELATIVE_PATH_EDEFAULT.equals(relativePath);
		case BuildPackage.FILE__PREV_REVISION:
			return PREV_REVISION_EDEFAULT == null ? prevRevision != null : !PREV_REVISION_EDEFAULT.equals(prevRevision);
		case BuildPackage.FILE__REVISION:
			return REVISION_EDEFAULT == null ? revision != null : !REVISION_EDEFAULT.equals(revision);
		case BuildPackage.FILE__DEAD:
			return dead != DEAD_EDEFAULT;
		case BuildPackage.FILE__EDIT_TYPE:
			return EDIT_TYPE_EDEFAULT == null ? editType != null : !EDIT_TYPE_EDEFAULT.equals(editType);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (relativePath: "); //$NON-NLS-1$
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

} // File
