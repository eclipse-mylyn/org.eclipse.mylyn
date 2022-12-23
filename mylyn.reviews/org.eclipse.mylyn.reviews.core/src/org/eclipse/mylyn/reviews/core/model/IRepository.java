/**
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Repository</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getApprovalTypes <em>Approval Types</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepositoryUrl <em>Task Repository Url</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskConnectorKind <em>Task Connector Kind</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepository <em>Task Repository</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getAccount <em>Account</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getReviews <em>Reviews</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getUsers <em>Users</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IRepository extends EObject {
	/**
	 * Returns the value of the '<em><b>Approval Types</b></em>' containment reference list. The list contents are of
	 * type {@link org.eclipse.mylyn.reviews.core.model.IApprovalType}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Approval Types</em>' containment reference list isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Approval Types</em>' containment reference list.
	 * @generated
	 */
	List<IApprovalType> getApprovalTypes();

	/**
	 * Returns the value of the '<em><b>Task Repository Url</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Task Repository Url</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Task Repository Url</em>' attribute.
	 * @see #setTaskRepositoryUrl(String)
	 * @generated
	 */
	String getTaskRepositoryUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepositoryUrl <em>Task
	 * Repository Url</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Task Repository Url</em>' attribute.
	 * @see #getTaskRepositoryUrl()
	 * @generated
	 */
	void setTaskRepositoryUrl(String value);

	/**
	 * Returns the value of the '<em><b>Task Connector Kind</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Task Connector Kind</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Task Connector Kind</em>' attribute.
	 * @see #setTaskConnectorKind(String)
	 * @generated
	 */
	String getTaskConnectorKind();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskConnectorKind <em>Task
	 * Connector Kind</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Task Connector Kind</em>' attribute.
	 * @see #getTaskConnectorKind()
	 * @generated
	 */
	void setTaskConnectorKind(String value);

	/**
	 * Returns the value of the '<em><b>Task Repository</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Task Repository</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Task Repository</em>' attribute.
	 * @see #setTaskRepository(TaskRepository)
	 * @generated
	 */
	TaskRepository getTaskRepository();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepository <em>Task
	 * Repository</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Task Repository</em>' attribute.
	 * @see #getTaskRepository()
	 * @generated
	 */
	void setTaskRepository(TaskRepository value);

	/**
	 * Returns the value of the '<em><b>Account</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Account</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Account</em>' reference.
	 * @see #setAccount(IUser)
	 * @generated
	 */
	IUser getAccount();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRepository#getAccount <em>Account</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Account</em>' reference.
	 * @see #getAccount()
	 * @generated
	 */
	void setAccount(IUser value);

	/**
	 * Returns the value of the '<em><b>Reviews</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IReview}. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReview#getRepository <em>Repository</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reviews</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Reviews</em>' containment reference list.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getRepository
	 * @generated
	 */
	List<IReview> getReviews();

	/**
	 * Returns the value of the '<em><b>Users</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IUser}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Users</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Users</em>' containment reference list.
	 * @generated
	 */
	List<IUser> getUsers();

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRepository#getDescription
	 * <em>Description</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

} // IRepository
