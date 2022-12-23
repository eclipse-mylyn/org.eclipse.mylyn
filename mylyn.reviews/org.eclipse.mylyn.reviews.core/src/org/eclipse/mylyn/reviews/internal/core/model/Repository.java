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

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Repository</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Repository#getApprovalTypes <em>Approval Types</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Repository#getTaskRepositoryUrl <em>Task Repository Url
 * </em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Repository#getTaskConnectorKind <em>Task Connector Kind
 * </em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Repository#getTaskRepository <em>Task Repository</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Repository#getAccount <em>Account</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Repository#getReviews <em>Reviews</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Repository#getUsers <em>Users</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Repository#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Repository extends EObjectImpl implements IRepository {
	/**
	 * The cached value of the '{@link #getApprovalTypes() <em>Approval Types</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getApprovalTypes()
	 * @generated
	 * @ordered
	 */
	protected EList<IApprovalType> approvalTypes;

	/**
	 * The default value of the '{@link #getTaskRepositoryUrl() <em>Task Repository Url</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTaskRepositoryUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String TASK_REPOSITORY_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTaskRepositoryUrl() <em>Task Repository Url</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTaskRepositoryUrl()
	 * @generated
	 * @ordered
	 */
	protected String taskRepositoryUrl = TASK_REPOSITORY_URL_EDEFAULT;

	/**
	 * The default value of the '{@link #getTaskConnectorKind() <em>Task Connector Kind</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTaskConnectorKind()
	 * @generated
	 * @ordered
	 */
	protected static final String TASK_CONNECTOR_KIND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTaskConnectorKind() <em>Task Connector Kind</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTaskConnectorKind()
	 * @generated
	 * @ordered
	 */
	protected String taskConnectorKind = TASK_CONNECTOR_KIND_EDEFAULT;

	/**
	 * The default value of the '{@link #getTaskRepository() <em>Task Repository</em>}' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see #getTaskRepository()
	 * @generated
	 * @ordered
	 */
	protected static final TaskRepository TASK_REPOSITORY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTaskRepository() <em>Task Repository</em>}' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see #getTaskRepository()
	 * @generated
	 * @ordered
	 */
	protected TaskRepository taskRepository = TASK_REPOSITORY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAccount() <em>Account</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getAccount()
	 * @generated
	 * @ordered
	 */
	protected IUser account;

	/**
	 * The cached value of the '{@link #getReviews() <em>Reviews</em>}' containment reference list. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see #getReviews()
	 * @generated
	 * @ordered
	 */
	protected EList<IReview> reviews;

	/**
	 * The cached value of the '{@link #getUsers() <em>Users</em>}' containment reference list. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getUsers()
	 * @generated
	 * @ordered
	 */
	protected EList<IUser> users;

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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Repository() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REPOSITORY;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IApprovalType> getApprovalTypes() {
		if (approvalTypes == null) {
			approvalTypes = new EObjectContainmentEList.Resolving<IApprovalType>(IApprovalType.class, this,
					ReviewsPackage.REPOSITORY__APPROVAL_TYPES);
		}
		return approvalTypes;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getTaskRepositoryUrl() {
		return taskRepositoryUrl;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTaskRepositoryUrl(String newTaskRepositoryUrl) {
		String oldTaskRepositoryUrl = taskRepositoryUrl;
		taskRepositoryUrl = newTaskRepositoryUrl;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REPOSITORY__TASK_REPOSITORY_URL,
					oldTaskRepositoryUrl, taskRepositoryUrl));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getTaskConnectorKind() {
		return taskConnectorKind;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTaskConnectorKind(String newTaskConnectorKind) {
		String oldTaskConnectorKind = taskConnectorKind;
		taskConnectorKind = newTaskConnectorKind;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REPOSITORY__TASK_CONNECTOR_KIND,
					oldTaskConnectorKind, taskConnectorKind));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTaskRepository(TaskRepository newTaskRepository) {
		TaskRepository oldTaskRepository = taskRepository;
		taskRepository = newTaskRepository;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REPOSITORY__TASK_REPOSITORY,
					oldTaskRepository, taskRepository));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser getAccount() {
		if (account != null && account.eIsProxy()) {
			InternalEObject oldAccount = (InternalEObject) account;
			account = (IUser) eResolveProxy(oldAccount);
			if (account != oldAccount) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.REPOSITORY__ACCOUNT,
							oldAccount, account));
				}
			}
		}
		return account;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser basicGetAccount() {
		return account;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setAccount(IUser newAccount) {
		IUser oldAccount = account;
		account = newAccount;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REPOSITORY__ACCOUNT, oldAccount,
					account));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IReview> getReviews() {
		if (reviews == null) {
			reviews = new EObjectContainmentWithInverseEList.Resolving<IReview>(IReview.class, this,
					ReviewsPackage.REPOSITORY__REVIEWS, ReviewsPackage.REVIEW__REPOSITORY);
		}
		return reviews;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IUser> getUsers() {
		if (users == null) {
			users = new EObjectContainmentEList.Resolving<IUser>(IUser.class, this, ReviewsPackage.REPOSITORY__USERS);
		}
		return users;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REPOSITORY__DESCRIPTION,
					oldDescription, description));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.REPOSITORY__REVIEWS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getReviews()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
			return ((InternalEList<?>) getApprovalTypes()).basicRemove(otherEnd, msgs);
		case ReviewsPackage.REPOSITORY__REVIEWS:
			return ((InternalEList<?>) getReviews()).basicRemove(otherEnd, msgs);
		case ReviewsPackage.REPOSITORY__USERS:
			return ((InternalEList<?>) getUsers()).basicRemove(otherEnd, msgs);
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
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
			return getApprovalTypes();
		case ReviewsPackage.REPOSITORY__TASK_REPOSITORY_URL:
			return getTaskRepositoryUrl();
		case ReviewsPackage.REPOSITORY__TASK_CONNECTOR_KIND:
			return getTaskConnectorKind();
		case ReviewsPackage.REPOSITORY__TASK_REPOSITORY:
			return getTaskRepository();
		case ReviewsPackage.REPOSITORY__ACCOUNT:
			if (resolve) {
				return getAccount();
			}
			return basicGetAccount();
		case ReviewsPackage.REPOSITORY__REVIEWS:
			return getReviews();
		case ReviewsPackage.REPOSITORY__USERS:
			return getUsers();
		case ReviewsPackage.REPOSITORY__DESCRIPTION:
			return getDescription();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
			getApprovalTypes().clear();
			getApprovalTypes().addAll((Collection<? extends IApprovalType>) newValue);
			return;
		case ReviewsPackage.REPOSITORY__TASK_REPOSITORY_URL:
			setTaskRepositoryUrl((String) newValue);
			return;
		case ReviewsPackage.REPOSITORY__TASK_CONNECTOR_KIND:
			setTaskConnectorKind((String) newValue);
			return;
		case ReviewsPackage.REPOSITORY__TASK_REPOSITORY:
			setTaskRepository((TaskRepository) newValue);
			return;
		case ReviewsPackage.REPOSITORY__ACCOUNT:
			setAccount((IUser) newValue);
			return;
		case ReviewsPackage.REPOSITORY__REVIEWS:
			getReviews().clear();
			getReviews().addAll((Collection<? extends IReview>) newValue);
			return;
		case ReviewsPackage.REPOSITORY__USERS:
			getUsers().clear();
			getUsers().addAll((Collection<? extends IUser>) newValue);
			return;
		case ReviewsPackage.REPOSITORY__DESCRIPTION:
			setDescription((String) newValue);
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
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
			getApprovalTypes().clear();
			return;
		case ReviewsPackage.REPOSITORY__TASK_REPOSITORY_URL:
			setTaskRepositoryUrl(TASK_REPOSITORY_URL_EDEFAULT);
			return;
		case ReviewsPackage.REPOSITORY__TASK_CONNECTOR_KIND:
			setTaskConnectorKind(TASK_CONNECTOR_KIND_EDEFAULT);
			return;
		case ReviewsPackage.REPOSITORY__TASK_REPOSITORY:
			setTaskRepository(TASK_REPOSITORY_EDEFAULT);
			return;
		case ReviewsPackage.REPOSITORY__ACCOUNT:
			setAccount((IUser) null);
			return;
		case ReviewsPackage.REPOSITORY__REVIEWS:
			getReviews().clear();
			return;
		case ReviewsPackage.REPOSITORY__USERS:
			getUsers().clear();
			return;
		case ReviewsPackage.REPOSITORY__DESCRIPTION:
			setDescription(DESCRIPTION_EDEFAULT);
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
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
			return approvalTypes != null && !approvalTypes.isEmpty();
		case ReviewsPackage.REPOSITORY__TASK_REPOSITORY_URL:
			return TASK_REPOSITORY_URL_EDEFAULT == null
					? taskRepositoryUrl != null
					: !TASK_REPOSITORY_URL_EDEFAULT.equals(taskRepositoryUrl);
		case ReviewsPackage.REPOSITORY__TASK_CONNECTOR_KIND:
			return TASK_CONNECTOR_KIND_EDEFAULT == null
					? taskConnectorKind != null
					: !TASK_CONNECTOR_KIND_EDEFAULT.equals(taskConnectorKind);
		case ReviewsPackage.REPOSITORY__TASK_REPOSITORY:
			return TASK_REPOSITORY_EDEFAULT == null
					? taskRepository != null
					: !TASK_REPOSITORY_EDEFAULT.equals(taskRepository);
		case ReviewsPackage.REPOSITORY__ACCOUNT:
			return account != null;
		case ReviewsPackage.REPOSITORY__REVIEWS:
			return reviews != null && !reviews.isEmpty();
		case ReviewsPackage.REPOSITORY__USERS:
			return users != null && !users.isEmpty();
		case ReviewsPackage.REPOSITORY__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
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
		result.append(" (taskRepositoryUrl: "); //$NON-NLS-1$
		result.append(taskRepositoryUrl);
		result.append(", taskConnectorKind: "); //$NON-NLS-1$
		result.append(taskConnectorKind);
		result.append(", taskRepository: "); //$NON-NLS-1$
		result.append(taskRepository);
		result.append(", description: "); //$NON-NLS-1$
		result.append(description);
		result.append(')');
		return result.toString();
	}

} //Repository
