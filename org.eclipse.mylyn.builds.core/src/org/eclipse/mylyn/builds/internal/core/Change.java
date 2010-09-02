/**
 * <copyright>
 * </copyright>
 *
 * $Id: Change.java,v 1.3 2010/09/02 06:23:14 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeArtifact;
import org.eclipse.mylyn.builds.core.IUser;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Change</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Change#getArtifacts <em>Artifacts</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Change#getAuthor <em>Author</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Change#getMessage <em>Message</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Change#getDate <em>Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Change#getRevision <em>Revision</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Change extends EObjectImpl implements IChange {
	/**
	 * The cached value of the '{@link #getArtifacts() <em>Artifacts</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getArtifacts()
	 * @generated
	 * @ordered
	 */
	protected EList<IChangeArtifact> artifacts;

	/**
	 * The cached value of the '{@link #getAuthor() <em>Author</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected IUser author;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IChangeArtifact> getArtifacts() {
		if (artifacts == null) {
			artifacts = new EObjectContainmentEList<IChangeArtifact>(IChangeArtifact.class, this,
					BuildPackage.CHANGE__ARTIFACTS);
		}
		return artifacts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Author</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser getAuthor() {
		return author;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetAuthor(IUser newAuthor, NotificationChain msgs) {
		IUser oldAuthor = author;
		author = newAuthor;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE__AUTHOR,
					oldAuthor, newAuthor);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setAuthor(IUser newAuthor) {
		if (newAuthor != author) {
			NotificationChain msgs = null;
			if (author != null)
				msgs = ((InternalEObject) author).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- BuildPackage.CHANGE__AUTHOR, null, msgs);
			if (newAuthor != null)
				msgs = ((InternalEObject) newAuthor).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- BuildPackage.CHANGE__AUTHOR, null, msgs);
			msgs = basicSetAuthor(newAuthor, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE__AUTHOR, newAuthor, newAuthor));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Message</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setMessage(String newMessage) {
		String oldMessage = message;
		message = newMessage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE__MESSAGE, oldMessage, message));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Date</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public long getDate() {
		return date;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDate(long newDate) {
		long oldDate = date;
		date = newDate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE__DATE, oldDate, date));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setRevision(String newRevision) {
		String oldRevision = revision;
		revision = newRevision;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.CHANGE__REVISION, oldRevision, revision));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case BuildPackage.CHANGE__ARTIFACTS:
			return ((InternalEList<?>) getArtifacts()).basicRemove(otherEnd, msgs);
		case BuildPackage.CHANGE__AUTHOR:
			return basicSetAuthor(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
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
		case BuildPackage.CHANGE__ARTIFACTS:
			return getArtifacts();
		case BuildPackage.CHANGE__AUTHOR:
			return getAuthor();
		case BuildPackage.CHANGE__MESSAGE:
			return getMessage();
		case BuildPackage.CHANGE__DATE:
			return getDate();
		case BuildPackage.CHANGE__REVISION:
			return getRevision();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case BuildPackage.CHANGE__ARTIFACTS:
			getArtifacts().clear();
			getArtifacts().addAll((Collection<? extends IChangeArtifact>) newValue);
			return;
		case BuildPackage.CHANGE__AUTHOR:
			setAuthor((IUser) newValue);
			return;
		case BuildPackage.CHANGE__MESSAGE:
			setMessage((String) newValue);
			return;
		case BuildPackage.CHANGE__DATE:
			setDate((Long) newValue);
			return;
		case BuildPackage.CHANGE__REVISION:
			setRevision((String) newValue);
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
		case BuildPackage.CHANGE__ARTIFACTS:
			getArtifacts().clear();
			return;
		case BuildPackage.CHANGE__AUTHOR:
			setAuthor((IUser) null);
			return;
		case BuildPackage.CHANGE__MESSAGE:
			setMessage(MESSAGE_EDEFAULT);
			return;
		case BuildPackage.CHANGE__DATE:
			setDate(DATE_EDEFAULT);
			return;
		case BuildPackage.CHANGE__REVISION:
			setRevision(REVISION_EDEFAULT);
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
		case BuildPackage.CHANGE__ARTIFACTS:
			return artifacts != null && !artifacts.isEmpty();
		case BuildPackage.CHANGE__AUTHOR:
			return author != null;
		case BuildPackage.CHANGE__MESSAGE:
			return MESSAGE_EDEFAULT == null ? message != null : !MESSAGE_EDEFAULT.equals(message);
		case BuildPackage.CHANGE__DATE:
			return date != DATE_EDEFAULT;
		case BuildPackage.CHANGE__REVISION:
			return REVISION_EDEFAULT == null ? revision != null : !REVISION_EDEFAULT.equals(revision);
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
		result.append(" (message: "); //$NON-NLS-1$
		result.append(message);
		result.append(", date: "); //$NON-NLS-1$
		result.append(date);
		result.append(", revision: "); //$NON-NLS-1$
		result.append(revision);
		result.append(')');
		return result.toString();
	}

} // Change
