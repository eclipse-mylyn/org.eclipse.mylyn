/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.core;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildElement#getUrl <em>Url</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildElement#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildElement#getOperations <em>Operations</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildElement#getElementStatus <em>Element Status</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildElement#getRefreshDate <em>Refresh Date</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IBuildElement {
	/**
	 * Returns the value of the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Url</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Url</em>' attribute.
	 * @see #setUrl(String)
	 * @generated
	 */
	String getUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildElement#getUrl <em>Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Url</em>' attribute.
	 * @see #getUrl()
	 * @generated
	 */
	void setUrl(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildElement#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Operations</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.IOperation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operations</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Operations</em>' attribute list.
	 * @generated
	 */
	List<IOperation> getOperations();

	/**
	 * Returns the value of the '<em><b>Element Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Element Status</em>' reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Element Status</em>' attribute.
	 * @see #setElementStatus(IStatus)
	 * @generated
	 */
	IStatus getElementStatus();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildElement#getElementStatus
	 * <em>Element Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Element Status</em>' attribute.
	 * @see #getElementStatus()
	 * @generated
	 */
	void setElementStatus(IStatus value);

	/**
	 * Returns the value of the '<em><b>Refresh Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Refresh Date</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Refresh Date</em>' attribute.
	 * @see #setRefreshDate(Date)
	 * @generated
	 */
	Date getRefreshDate();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildElement#getRefreshDate <em>Refresh Date</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Refresh Date</em>' attribute.
	 * @see #getRefreshDate()
	 * @generated
	 */
	void setRefreshDate(Date value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String getLabel();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	IBuildServer getServer();

} // IBuildElement
