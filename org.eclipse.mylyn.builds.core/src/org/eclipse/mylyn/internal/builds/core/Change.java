/**
 * <copyright>
 * </copyright>
 *
 * $Id: Change.java,v 1.2 2010/08/04 07:38:41 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IFile;
import org.eclipse.mylyn.builds.core.IUser;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Change</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getChange()
 * @model kind="class" superTypes="org.eclipse.mylyn.internal.builds.core.IChange"
 * @generated
 */
public class Change extends EObjectImpl implements EObject, IChange {
	/**
	 * The cached value of the '{@link #getAuthor() <em>Author</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected IUser author;

	/**
	 * The cached value of the '{@link #getFile() <em>File</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getFile()
	 * @generated
	 * @ordered
	 */
	protected IFile file;

	/**
	 * The default value of the '{@link #getMessage() <em>Message</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected static final String MESSAGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMessage() <em>Message</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected String message = MESSAGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDate() <em>Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDate()
	 * @generated
	 * @ordered
	 */
	protected static final long DATE_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getDate() <em>Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDate()
	 * @generated
	 * @ordered
	 */
	protected long date = DATE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getUser() <em>User</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getUser()
	 * @generated
	 * @ordered
	 */
	protected IUser user;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Change() {
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
		return BuildPackage.Literals.CHANGE;
	}

	/**
	 * Returns the value of the '<em><b>Author</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Author</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Author</em>' reference.
	 * @see #setAuthor(IUser)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChange_Author()
	 * @model type="org.eclipse.mylyn.internal.builds.core.IUser"
	 * @generated
	 */
	public IUser getAuthor() {
		if (author != null && ((EObject) author).eIsProxy()) {
			InternalEObject oldAuthor = (InternalEObject) author;
			author = (IUser) eResolveProxy(oldAuthor);
			if (author != oldAuthor) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.CHANGE__AUTHOR, oldAuthor,
							author));
			}
		}
		return author;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser basicGetAuthor() {
		return author;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.Change#getAuthor <em>Author</em>}'
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Author</em>' reference.
	 * @see #getAuthor()
	 * @generated
	 */
	public void setAuthor(IUser newAuthor) {
		IUser oldAuthor = author;
		author = newAuthor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE__AUTHOR, oldAuthor, author));
	}

	/**
	 * Returns the value of the '<em><b>File</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File</em>' reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>File</em>' reference.
	 * @see #setFile(IFile)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChange_File()
	 * @model type="org.eclipse.mylyn.internal.builds.core.IFile"
	 * @generated
	 */
	public IFile getFile() {
		if (file != null && ((EObject) file).eIsProxy()) {
			InternalEObject oldFile = (InternalEObject) file;
			file = (IFile) eResolveProxy(oldFile);
			if (file != oldFile) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.CHANGE__FILE, oldFile, file));
			}
		}
		return file;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFile basicGetFile() {
		return file;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.Change#getFile <em>File</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>File</em>' reference.
	 * @see #getFile()
	 * @generated
	 */
	public void setFile(IFile newFile) {
		IFile oldFile = file;
		file = newFile;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE__FILE, oldFile, file));
	}

	/**
	 * Returns the value of the '<em><b>Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Message</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Message</em>' attribute.
	 * @see #setMessage(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChange_Message()
	 * @model
	 * @generated
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.Change#getMessage <em>Message</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Message</em>' attribute.
	 * @see #getMessage()
	 * @generated
	 */
	public void setMessage(String newMessage) {
		String oldMessage = message;
		message = newMessage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE__MESSAGE, oldMessage, message));
	}

	/**
	 * Returns the value of the '<em><b>Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Date</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Date</em>' attribute.
	 * @see #setDate(long)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChange_Date()
	 * @model
	 * @generated
	 */
	public long getDate() {
		return date;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.Change#getDate <em>Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Date</em>' attribute.
	 * @see #getDate()
	 * @generated
	 */
	public void setDate(long newDate) {
		long oldDate = date;
		date = newDate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE__DATE, oldDate, date));
	}

	/**
	 * Returns the value of the '<em><b>User</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>User</em>' reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>User</em>' reference.
	 * @see #setUser(IUser)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChange_User()
	 * @model type="org.eclipse.mylyn.internal.builds.core.IUser"
	 * @generated
	 */
	public IUser getUser() {
		if (user != null && ((EObject) user).eIsProxy()) {
			InternalEObject oldUser = (InternalEObject) user;
			user = (IUser) eResolveProxy(oldUser);
			if (user != oldUser) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.CHANGE__USER, oldUser, user));
			}
		}
		return user;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser basicGetUser() {
		return user;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.Change#getUser <em>User</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>User</em>' reference.
	 * @see #getUser()
	 * @generated
	 */
	public void setUser(IUser newUser) {
		IUser oldUser = user;
		user = newUser;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE__USER, oldUser, user));
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
		case BuildPackage.CHANGE__AUTHOR:
			if (resolve)
				return getAuthor();
			return basicGetAuthor();
		case BuildPackage.CHANGE__FILE:
			if (resolve)
				return getFile();
			return basicGetFile();
		case BuildPackage.CHANGE__MESSAGE:
			return getMessage();
		case BuildPackage.CHANGE__DATE:
			return getDate();
		case BuildPackage.CHANGE__USER:
			if (resolve)
				return getUser();
			return basicGetUser();
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
		case BuildPackage.CHANGE__AUTHOR:
			setAuthor((IUser) newValue);
			return;
		case BuildPackage.CHANGE__FILE:
			setFile((IFile) newValue);
			return;
		case BuildPackage.CHANGE__MESSAGE:
			setMessage((String) newValue);
			return;
		case BuildPackage.CHANGE__DATE:
			setDate((Long) newValue);
			return;
		case BuildPackage.CHANGE__USER:
			setUser((IUser) newValue);
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
		case BuildPackage.CHANGE__AUTHOR:
			setAuthor((IUser) null);
			return;
		case BuildPackage.CHANGE__FILE:
			setFile((IFile) null);
			return;
		case BuildPackage.CHANGE__MESSAGE:
			setMessage(MESSAGE_EDEFAULT);
			return;
		case BuildPackage.CHANGE__DATE:
			setDate(DATE_EDEFAULT);
			return;
		case BuildPackage.CHANGE__USER:
			setUser((IUser) null);
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
		case BuildPackage.CHANGE__AUTHOR:
			return author != null;
		case BuildPackage.CHANGE__FILE:
			return file != null;
		case BuildPackage.CHANGE__MESSAGE:
			return MESSAGE_EDEFAULT == null ? message != null : !MESSAGE_EDEFAULT.equals(message);
		case BuildPackage.CHANGE__DATE:
			return date != DATE_EDEFAULT;
		case BuildPackage.CHANGE__USER:
			return user != null;
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
		result.append(" (message: ");
		result.append(message);
		result.append(", date: ");
		result.append(date);
		result.append(')');
		return result.toString();
	}

} // Change
