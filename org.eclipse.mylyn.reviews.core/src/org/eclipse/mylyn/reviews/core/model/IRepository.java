/**
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import java.util.List;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Repository</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getApprovalTypes <em>Approval Types</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getReviewStates <em>Review States</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepositoryUrl <em>Task Repository Url</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskConnectorKind <em>Task Connector Kind</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepository <em>Task Repository</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IRepository extends IReviewGroup {
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
	 * Returns the value of the '<em><b>Review States</b></em>' containment reference list. The list contents are of
	 * type {@link org.eclipse.mylyn.reviews.core.model.IReviewState}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Review States</em>' containment reference list isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Review States</em>' containment reference list.
	 * @generated
	 */
	List<IReviewState> getReviewStates();

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
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepositoryUrl
	 * <em>Task Repository Url</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskConnectorKind
	 * <em>Task Connector Kind</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepository
	 * <em>Task Repository</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Task Repository</em>' attribute.
	 * @see #getTaskRepository()
	 * @generated
	 */
	void setTaskRepository(TaskRepository value);

} // IRepository
