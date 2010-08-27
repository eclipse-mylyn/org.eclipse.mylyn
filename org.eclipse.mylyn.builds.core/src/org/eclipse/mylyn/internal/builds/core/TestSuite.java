/**
 * <copyright>
 * </copyright>
 *
 * $Id: TestSuite.java,v 1.1 2010/08/27 09:00:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Suite</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestSuite#getCases <em>Cases</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestSuite#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestSuite()
 * @model kind="class"
 * @generated
 */
public class TestSuite extends TestElement {
	/**
	 * The cached value of the '{@link #getCases() <em>Cases</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getCases()
	 * @generated
	 * @ordered
	 */
	protected EList<TestCase> cases;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TestSuite() {
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
		return BuildPackage.Literals.TEST_SUITE;
	}

	/**
	 * Returns the value of the '<em><b>Cases</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.internal.builds.core.TestCase}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.internal.builds.core.TestCase#getSuite
	 * <em>Suite</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Cases</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Cases</em>' containment reference list.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestSuite_Cases()
	 * @see org.eclipse.mylyn.internal.builds.core.TestCase#getSuite
	 * @model opposite="suite" containment="true"
	 * @generated
	 */
	public EList<TestCase> getCases() {
		if (cases == null) {
			cases = new EObjectContainmentWithInverseEList<TestCase>(TestCase.class, this,
					BuildPackage.TEST_SUITE__CASES, BuildPackage.TEST_CASE__SUITE);
		}
		return cases;
	}

	/**
	 * Returns the value of the '<em><b>Result</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.internal.builds.core.TestResult#getSuites
	 * <em>Suites</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Result</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Result</em>' container reference.
	 * @see #setResult(TestResult)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestSuite_Result()
	 * @see org.eclipse.mylyn.internal.builds.core.TestResult#getSuites
	 * @model opposite="suites" transient="false"
	 * @generated
	 */
	public TestResult getResult() {
		if (eContainerFeatureID() != BuildPackage.TEST_SUITE__RESULT)
			return null;
		return (TestResult) eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetResult(TestResult newResult, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newResult, BuildPackage.TEST_SUITE__RESULT, msgs);
		return msgs;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestSuite#getResult <em>Result</em>}'
	 * container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Result</em>' container reference.
	 * @see #getResult()
	 * @generated
	 */
	public void setResult(TestResult newResult) {
		if (newResult != eInternalContainer()
				|| (eContainerFeatureID() != BuildPackage.TEST_SUITE__RESULT && newResult != null)) {
			if (EcoreUtil.isAncestor(this, newResult))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newResult != null)
				msgs = ((InternalEObject) newResult).eInverseAdd(this, BuildPackage.TEST_RESULT__SUITES,
						TestResult.class, msgs);
			msgs = basicSetResult(newResult, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_SUITE__RESULT, newResult, newResult));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case BuildPackage.TEST_SUITE__CASES:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getCases()).basicAdd(otherEnd, msgs);
		case BuildPackage.TEST_SUITE__RESULT:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetResult((TestResult) otherEnd, msgs);
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
		case BuildPackage.TEST_SUITE__CASES:
			return ((InternalEList<?>) getCases()).basicRemove(otherEnd, msgs);
		case BuildPackage.TEST_SUITE__RESULT:
			return basicSetResult(null, msgs);
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
		case BuildPackage.TEST_SUITE__RESULT:
			return eInternalContainer().eInverseRemove(this, BuildPackage.TEST_RESULT__SUITES, TestResult.class, msgs);
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
		case BuildPackage.TEST_SUITE__CASES:
			return getCases();
		case BuildPackage.TEST_SUITE__RESULT:
			return getResult();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case BuildPackage.TEST_SUITE__CASES:
			getCases().clear();
			getCases().addAll((Collection<? extends TestCase>) newValue);
			return;
		case BuildPackage.TEST_SUITE__RESULT:
			setResult((TestResult) newValue);
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
		case BuildPackage.TEST_SUITE__CASES:
			getCases().clear();
			return;
		case BuildPackage.TEST_SUITE__RESULT:
			setResult((TestResult) null);
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
		case BuildPackage.TEST_SUITE__CASES:
			return cases != null && !cases.isEmpty();
		case BuildPackage.TEST_SUITE__RESULT:
			return getResult() != null;
		}
		return super.eIsSet(featureID);
	}

} // TestSuite
