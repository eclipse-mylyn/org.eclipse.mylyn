/**
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.builds.internal.core;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.mylyn.builds.core.IBuildCause;
import org.eclipse.mylyn.builds.core.IBuildReference;
import org.eclipse.mylyn.builds.core.IUser;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Cause</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildCause#getDescription <em>Description</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildCause#getBuild <em>Build</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildCause#getUser <em>User</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class BuildCause extends EObjectImpl implements IBuildCause {
	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getBuild() <em>Build</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getBuild()
	 * @generated
	 * @ordered
	 */
	protected IBuildReference build;

	/**
	 * The cached value of the '{@link #getUser() <em>User</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getUser()
	 * @generated
	 * @ordered
	 */
	protected IUser user;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected BuildCause() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.BUILD_CAUSE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_CAUSE__DESCRIPTION, oldDescription,
					description));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IBuildReference getBuild() {
		return build;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetBuild(IBuildReference newBuild, NotificationChain msgs) {
		IBuildReference oldBuild = build;
		build = newBuild;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					BuildPackage.BUILD_CAUSE__BUILD, oldBuild, newBuild);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setBuild(IBuildReference newBuild) {
		if (newBuild != build) {
			NotificationChain msgs = null;
			if (build != null) {
				msgs = ((InternalEObject) build).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - BuildPackage.BUILD_CAUSE__BUILD, null, msgs);
			}
			if (newBuild != null) {
				msgs = ((InternalEObject) newBuild).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - BuildPackage.BUILD_CAUSE__BUILD, null, msgs);
			}
			msgs = basicSetBuild(newBuild, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_CAUSE__BUILD, newBuild, newBuild));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IUser getUser() {
		return user;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetUser(IUser newUser, NotificationChain msgs) {
		IUser oldUser = user;
		user = newUser;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					BuildPackage.BUILD_CAUSE__USER, oldUser, newUser);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setUser(IUser newUser) {
		if (newUser != user) {
			NotificationChain msgs = null;
			if (user != null) {
				msgs = ((InternalEObject) user).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - BuildPackage.BUILD_CAUSE__USER, null, msgs);
			}
			if (newUser != null) {
				msgs = ((InternalEObject) newUser).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - BuildPackage.BUILD_CAUSE__USER, null, msgs);
			}
			msgs = basicSetUser(newUser, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_CAUSE__USER, newUser, newUser));
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
			case BuildPackage.BUILD_CAUSE__BUILD:
				return basicSetBuild(null, msgs);
			case BuildPackage.BUILD_CAUSE__USER:
				return basicSetUser(null, msgs);
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
			case BuildPackage.BUILD_CAUSE__DESCRIPTION:
				return getDescription();
			case BuildPackage.BUILD_CAUSE__BUILD:
				return getBuild();
			case BuildPackage.BUILD_CAUSE__USER:
				return getUser();
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
			case BuildPackage.BUILD_CAUSE__DESCRIPTION:
				setDescription((String) newValue);
				return;
			case BuildPackage.BUILD_CAUSE__BUILD:
				setBuild((IBuildReference) newValue);
				return;
			case BuildPackage.BUILD_CAUSE__USER:
				setUser((IUser) newValue);
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
			case BuildPackage.BUILD_CAUSE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case BuildPackage.BUILD_CAUSE__BUILD:
				setBuild((IBuildReference) null);
				return;
			case BuildPackage.BUILD_CAUSE__USER:
				setUser((IUser) null);
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
			case BuildPackage.BUILD_CAUSE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case BuildPackage.BUILD_CAUSE__BUILD:
				return build != null;
			case BuildPackage.BUILD_CAUSE__USER:
				return user != null;
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
		result.append(" (description: "); //$NON-NLS-1$
		result.append(description);
		result.append(')');
		return result.toString();
	}

} //BuildCause
