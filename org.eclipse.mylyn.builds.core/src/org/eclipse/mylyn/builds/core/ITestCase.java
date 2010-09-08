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
package org.eclipse.mylyn.builds.core;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Case</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestCase#getClassName <em>Class Name</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestCase#isSkipped <em>Skipped</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestCase#getSuite <em>Suite</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestCase#getStatus <em>Status</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestCase#getMessage <em>Message</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestCase#getStackTrace <em>Stack Trace</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface ITestCase extends ITestElement {
	/**
	 * Returns the value of the '<em><b>Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Class Name</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Class Name</em>' attribute.
	 * @see #setClassName(String)
	 * @generated
	 */
	String getClassName();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestCase#getClassName <em>Class Name</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Class Name</em>' attribute.
	 * @see #getClassName()
	 * @generated
	 */
	void setClassName(String value);

	/**
	 * Returns the value of the '<em><b>Skipped</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Skipped</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Skipped</em>' attribute.
	 * @see #setSkipped(boolean)
	 * @generated
	 */
	boolean isSkipped();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestCase#isSkipped <em>Skipped</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Skipped</em>' attribute.
	 * @see #isSkipped()
	 * @generated
	 */
	void setSkipped(boolean value);

	/**
	 * Returns the value of the '<em><b>Suite</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.builds.core.ITestSuite#getCases <em>Cases</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Suite</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Suite</em>' container reference.
	 * @see #setSuite(ITestSuite)
	 * @see org.eclipse.mylyn.builds.core.ITestSuite#getCases
	 * @generated
	 */
	ITestSuite getSuite();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestCase#getSuite <em>Suite</em>}' container
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Suite</em>' container reference.
	 * @see #getSuite()
	 * @generated
	 */
	void setSuite(ITestSuite value);

	/**
	 * Returns the value of the '<em><b>Status</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.mylyn.builds.core.TestCaseResult}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Status</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Status</em>' attribute.
	 * @see org.eclipse.mylyn.builds.core.TestCaseResult
	 * @see #setStatus(TestCaseResult)
	 * @generated
	 */
	TestCaseResult getStatus();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestCase#getStatus <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Status</em>' attribute.
	 * @see org.eclipse.mylyn.builds.core.TestCaseResult
	 * @see #getStatus()
	 * @generated
	 */
	void setStatus(TestCaseResult value);

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
	 * @generated
	 */
	String getMessage();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestCase#getMessage <em>Message</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Message</em>' attribute.
	 * @see #getMessage()
	 * @generated
	 */
	void setMessage(String value);

	/**
	 * Returns the value of the '<em><b>Stack Trace</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Stack Trace</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Stack Trace</em>' attribute.
	 * @see #setStackTrace(String)
	 * @generated
	 */
	String getStackTrace();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestCase#getStackTrace <em>Stack Trace</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Stack Trace</em>' attribute.
	 * @see #getStackTrace()
	 * @generated
	 */
	void setStackTrace(String value);

} // ITestCase
