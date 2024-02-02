/**
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.builds.core;

import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Test Result</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestResult#getBuild <em>Build</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestResult#getDuration <em>Duration</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestResult#getFailCount <em>Fail Count</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestResult#getPassCount <em>Pass Count</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestResult#getIgnoredCount <em>Ignored Count</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestResult#getErrorCount <em>Error Count</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.ITestResult#getSuites <em>Suites</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface ITestResult {
	/**
	 * Returns the value of the '<em><b>Build</b></em>' container reference. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.builds.core.IBuild#getTestResult <em>Test Result</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Build</em>' reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Build</em>' container reference.
	 * @see #setBuild(IBuild)
	 * @see org.eclipse.mylyn.builds.core.IBuild#getTestResult
	 * @generated
	 */
	IBuild getBuild();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestResult#getBuild <em>Build</em>}' container reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Build</em>' container reference.
	 * @see #getBuild()
	 * @generated
	 */
	void setBuild(IBuild value);

	/**
	 * Returns the value of the '<em><b>Duration</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Duration</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Duration</em>' attribute.
	 * @see #setDuration(long)
	 * @generated
	 */
	long getDuration();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestResult#getDuration <em>Duration</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Duration</em>' attribute.
	 * @see #getDuration()
	 * @generated
	 */
	void setDuration(long value);

	/**
	 * Returns the value of the '<em><b>Fail Count</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fail Count</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Fail Count</em>' attribute.
	 * @see #setFailCount(int)
	 * @generated
	 */
	int getFailCount();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestResult#getFailCount <em>Fail Count</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Fail Count</em>' attribute.
	 * @see #getFailCount()
	 * @generated
	 */
	void setFailCount(int value);

	/**
	 * Returns the value of the '<em><b>Pass Count</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Pass Count</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Pass Count</em>' attribute.
	 * @see #setPassCount(int)
	 * @generated
	 */
	int getPassCount();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestResult#getPassCount <em>Pass Count</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Pass Count</em>' attribute.
	 * @see #getPassCount()
	 * @generated
	 */
	void setPassCount(int value);

	/**
	 * Returns the value of the '<em><b>Ignored Count</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ignored Count</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Ignored Count</em>' attribute.
	 * @see #setIgnoredCount(int)
	 * @generated
	 */
	int getIgnoredCount();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestResult#getIgnoredCount <em>Ignored Count</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Ignored Count</em>' attribute.
	 * @see #getIgnoredCount()
	 * @generated
	 */
	void setIgnoredCount(int value);

	/**
	 * Returns the value of the '<em><b>Error Count</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Error Count</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Error Count</em>' attribute.
	 * @see #setErrorCount(int)
	 * @generated
	 */
	int getErrorCount();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.ITestResult#getErrorCount <em>Error Count</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Error Count</em>' attribute.
	 * @see #getErrorCount()
	 * @generated
	 */
	void setErrorCount(int value);

	/**
	 * Returns the value of the '<em><b>Suites</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.builds.core.ITestSuite}. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.builds.core.ITestSuite#getResult <em>Result</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Suites</em>' containment reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Suites</em>' containment reference list.
	 * @see org.eclipse.mylyn.builds.core.ITestSuite#getResult
	 * @generated
	 */
	List<ITestSuite> getSuites();

} // ITestResult
