/**
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.internal.core.model;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.mylyn.reviews.core.model.ITaskReference;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Task Reference</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.TaskReference#getTaskId <em>Task Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.TaskReference#getRepositoryURL <em>Repository URL</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TaskReference extends ReviewComponent implements ITaskReference {
	/**
	 * The default value of the '{@link #getTaskId() <em>Task Id</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTaskId()
	 * @generated
	 * @ordered
	 */
	protected static final String TASK_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTaskId() <em>Task Id</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTaskId()
	 * @generated
	 * @ordered
	 */
	protected String taskId = TASK_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getRepositoryURL() <em>Repository URL</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRepositoryURL()
	 * @generated
	 * @ordered
	 */
	protected static final String REPOSITORY_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRepositoryURL() <em>Repository URL</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRepositoryURL()
	 * @generated
	 * @ordered
	 */
	protected String repositoryURL = REPOSITORY_URL_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TaskReference() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.TASK_REFERENCE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTaskId(String newTaskId) {
		String oldTaskId = taskId;
		taskId = newTaskId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.TASK_REFERENCE__TASK_ID, oldTaskId,
					taskId));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getRepositoryURL() {
		return repositoryURL;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setRepositoryURL(String newRepositoryURL) {
		String oldRepositoryURL = repositoryURL;
		repositoryURL = newRepositoryURL;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.TASK_REFERENCE__REPOSITORY_URL,
					oldRepositoryURL, repositoryURL));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReviewsPackage.TASK_REFERENCE__TASK_ID:
			return getTaskId();
		case ReviewsPackage.TASK_REFERENCE__REPOSITORY_URL:
			return getRepositoryURL();
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
		case ReviewsPackage.TASK_REFERENCE__TASK_ID:
			setTaskId((String) newValue);
			return;
		case ReviewsPackage.TASK_REFERENCE__REPOSITORY_URL:
			setRepositoryURL((String) newValue);
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
		case ReviewsPackage.TASK_REFERENCE__TASK_ID:
			setTaskId(TASK_ID_EDEFAULT);
			return;
		case ReviewsPackage.TASK_REFERENCE__REPOSITORY_URL:
			setRepositoryURL(REPOSITORY_URL_EDEFAULT);
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
		case ReviewsPackage.TASK_REFERENCE__TASK_ID:
			return TASK_ID_EDEFAULT == null ? taskId != null : !TASK_ID_EDEFAULT.equals(taskId);
		case ReviewsPackage.TASK_REFERENCE__REPOSITORY_URL:
			return REPOSITORY_URL_EDEFAULT == null
					? repositoryURL != null
					: !REPOSITORY_URL_EDEFAULT.equals(repositoryURL);
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
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (taskId: "); //$NON-NLS-1$
		result.append(taskId);
		result.append(", repositoryURL: "); //$NON-NLS-1$
		result.append(repositoryURL);
		result.append(')');
		return result.toString();
	}

} //TaskReference
