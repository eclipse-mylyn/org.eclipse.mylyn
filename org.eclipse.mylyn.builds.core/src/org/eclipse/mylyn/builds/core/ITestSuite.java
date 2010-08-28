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

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Suite</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestSuite#getCases <em>Cases</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestSuite#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface ITestSuite extends ITestElement {
	/**
	 * Returns the value of the '<em><b>Cases</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.ITestCase}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.builds.core.ITestCase#getSuite <em>Suite</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Cases</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Cases</em>' containment reference list.
	 * @see org.eclipse.mylyn.builds.core.ITestCase#getSuite
	 * @generated
	 */
	List<ITestCase> getCases();

	/**
	 * Returns the value of the '<em><b>Result</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.builds.core.ITestResult#getSuites
	 * <em>Suites</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Result</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Result</em>' container reference.
	 * @see #setResult(ITestResult)
	 * @see org.eclipse.mylyn.builds.core.ITestResult#getSuites
	 * @generated
	 */
	ITestResult getResult();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestSuite#getResult <em>Result</em>}' container
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Result</em>' container reference.
	 * @see #getResult()
	 * @generated
	 */
	void setResult(ITestResult value);

} // ITestSuite
