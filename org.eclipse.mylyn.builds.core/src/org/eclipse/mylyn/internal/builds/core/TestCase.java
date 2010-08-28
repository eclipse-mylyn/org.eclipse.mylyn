/**
 * <copyright>
 * </copyright>
 *
 * $Id: TestCase.java,v 1.2 2010/08/28 03:38:02 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Case</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestCase#getClassName <em>Class Name</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestCase#isSkipped <em>Skipped</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestCase#getSuite <em>Suite</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestCase#getStatus <em>Status</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestCase()
 * @model kind="class"
 * @generated
 */
public class TestCase extends TestElement {
	/**
	 * The default value of the '{@link #getClassName() <em>Class Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getClassName()
	 * @generated
	 * @ordered
	 */
	protected static final String CLASS_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getClassName() <em>Class Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getClassName()
	 * @generated
	 * @ordered
	 */
	protected String className = CLASS_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #isSkipped() <em>Skipped</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #isSkipped()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SKIPPED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSkipped() <em>Skipped</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #isSkipped()
	 * @generated
	 * @ordered
	 */
	protected boolean skipped = SKIPPED_EDEFAULT;

	/**
	 * The default value of the '{@link #getStatus() <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected static final TestCaseResult STATUS_EDEFAULT = TestCaseResult.PASSED;

	/**
	 * The cached value of the '{@link #getStatus() <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected TestCaseResult status = STATUS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TestCase() {
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
		return BuildPackage.Literals.TEST_CASE;
	}

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
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestCase_ClassName()
	 * @model
	 * @generated
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestCase#getClassName <em>Class Name</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Class Name</em>' attribute.
	 * @see #getClassName()
	 * @generated
	 */
	public void setClassName(String newClassName) {
		String oldClassName = className;
		className = newClassName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_CASE__CLASS_NAME, oldClassName,
					className));
	}

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
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestCase_Skipped()
	 * @model
	 * @generated
	 */
	public boolean isSkipped() {
		return skipped;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestCase#isSkipped <em>Skipped</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Skipped</em>' attribute.
	 * @see #isSkipped()
	 * @generated
	 */
	public void setSkipped(boolean newSkipped) {
		boolean oldSkipped = skipped;
		skipped = newSkipped;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_CASE__SKIPPED, oldSkipped, skipped));
	}

	/**
	 * Returns the value of the '<em><b>Suite</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.internal.builds.core.TestSuite#getCases
	 * <em>Cases</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Suite</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Suite</em>' container reference.
	 * @see #setSuite(TestSuite)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestCase_Suite()
	 * @see org.eclipse.mylyn.internal.builds.core.TestSuite#getCases
	 * @model opposite="cases" transient="false"
	 * @generated
	 */
	public TestSuite getSuite() {
		if (eContainerFeatureID() != BuildPackage.TEST_CASE__SUITE)
			return null;
		return (TestSuite) eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetSuite(TestSuite newSuite, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newSuite, BuildPackage.TEST_CASE__SUITE, msgs);
		return msgs;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestCase#getSuite <em>Suite</em>}' container
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Suite</em>' container reference.
	 * @see #getSuite()
	 * @generated
	 */
	public void setSuite(TestSuite newSuite) {
		if (newSuite != eInternalContainer()
				|| (eContainerFeatureID() != BuildPackage.TEST_CASE__SUITE && newSuite != null)) {
			if (EcoreUtil.isAncestor(this, newSuite))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newSuite != null)
				msgs = ((InternalEObject) newSuite).eInverseAdd(this, BuildPackage.TEST_SUITE__CASES, TestSuite.class,
						msgs);
			msgs = basicSetSuite(newSuite, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_CASE__SUITE, newSuite, newSuite));
	}

	/**
	 * Returns the value of the '<em><b>Status</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.mylyn.internal.builds.core.TestCaseResult}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Status</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Status</em>' attribute.
	 * @see org.eclipse.mylyn.internal.builds.core.TestCaseResult
	 * @see #setStatus(TestCaseResult)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestCase_Status()
	 * @model
	 * @generated
	 */
	public TestCaseResult getStatus() {
		return status;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestCase#getStatus <em>Status</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Status</em>' attribute.
	 * @see org.eclipse.mylyn.internal.builds.core.TestCaseResult
	 * @see #getStatus()
	 * @generated
	 */
	public void setStatus(TestCaseResult newStatus) {
		TestCaseResult oldStatus = status;
		status = newStatus == null ? STATUS_EDEFAULT : newStatus;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_CASE__STATUS, oldStatus, status));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case BuildPackage.TEST_CASE__SUITE:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetSuite((TestSuite) otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
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
		case BuildPackage.TEST_CASE__SUITE:
			return basicSetSuite(null, msgs);
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
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
		case BuildPackage.TEST_CASE__SUITE:
			return eInternalContainer().eInverseRemove(this, BuildPackage.TEST_SUITE__CASES, TestSuite.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
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
		case BuildPackage.TEST_CASE__CLASS_NAME:
			return getClassName();
		case BuildPackage.TEST_CASE__SKIPPED:
			return isSkipped();
		case BuildPackage.TEST_CASE__SUITE:
			return getSuite();
		case BuildPackage.TEST_CASE__STATUS:
			return getStatus();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case BuildPackage.TEST_CASE__CLASS_NAME:
			setClassName((String) newValue);
			return;
		case BuildPackage.TEST_CASE__SKIPPED:
			setSkipped((Boolean) newValue);
			return;
		case BuildPackage.TEST_CASE__SUITE:
			setSuite((TestSuite) newValue);
			return;
		case BuildPackage.TEST_CASE__STATUS:
			setStatus((TestCaseResult) newValue);
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
		case BuildPackage.TEST_CASE__CLASS_NAME:
			setClassName(CLASS_NAME_EDEFAULT);
			return;
		case BuildPackage.TEST_CASE__SKIPPED:
			setSkipped(SKIPPED_EDEFAULT);
			return;
		case BuildPackage.TEST_CASE__SUITE:
			setSuite((TestSuite) null);
			return;
		case BuildPackage.TEST_CASE__STATUS:
			setStatus(STATUS_EDEFAULT);
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
		case BuildPackage.TEST_CASE__CLASS_NAME:
			return CLASS_NAME_EDEFAULT == null ? className != null : !CLASS_NAME_EDEFAULT.equals(className);
		case BuildPackage.TEST_CASE__SKIPPED:
			return skipped != SKIPPED_EDEFAULT;
		case BuildPackage.TEST_CASE__SUITE:
			return getSuite() != null;
		case BuildPackage.TEST_CASE__STATUS:
			return status != STATUS_EDEFAULT;
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
		result.append(" (className: "); //$NON-NLS-1$
		result.append(className);
		result.append(", skipped: "); //$NON-NLS-1$
		result.append(skipped);
		result.append(", status: "); //$NON-NLS-1$
		result.append(status);
		result.append(')');
		return result.toString();
	}

} // TestCase
