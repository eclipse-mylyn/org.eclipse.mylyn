/**
 * <copyright>
 * </copyright>
 *
 * $Id: TestResult.java,v 1.2 2010/08/28 03:38:02 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Result</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestResult#getBuild <em>Build</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestResult#getDuration <em>Duration</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestResult#getFailCount <em>Fail Count</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestResult#getPassCount <em>Pass Count</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestResult#getSuites <em>Suites</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestResult()
 * @model kind="class"
 * @generated
 */
public class TestResult extends EObjectImpl implements EObject {
	/**
	 * The cached value of the '{@link #getBuild() <em>Build</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getBuild()
	 * @generated
	 * @ordered
	 */
	protected IBuild build;

	/**
	 * The default value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected static final long DURATION_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected long duration = DURATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getFailCount() <em>Fail Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getFailCount()
	 * @generated
	 * @ordered
	 */
	protected static final int FAIL_COUNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getFailCount() <em>Fail Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getFailCount()
	 * @generated
	 * @ordered
	 */
	protected int failCount = FAIL_COUNT_EDEFAULT;

	/**
	 * The default value of the '{@link #getPassCount() <em>Pass Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getPassCount()
	 * @generated
	 * @ordered
	 */
	protected static final int PASS_COUNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPassCount() <em>Pass Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getPassCount()
	 * @generated
	 * @ordered
	 */
	protected int passCount = PASS_COUNT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSuites() <em>Suites</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getSuites()
	 * @generated
	 * @ordered
	 */
	protected EList<TestSuite> suites;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TestResult() {
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
		return BuildPackage.Literals.TEST_RESULT;
	}

	/**
	 * Returns the value of the '<em><b>Build</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Build</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Build</em>' reference.
	 * @see #setBuild(IBuild)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestResult_Build()
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuild"
	 * @generated
	 */
	public IBuild getBuild() {
		if (build != null && ((EObject) build).eIsProxy()) {
			InternalEObject oldBuild = (InternalEObject) build;
			build = (IBuild) eResolveProxy(oldBuild);
			if (build != oldBuild) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.TEST_RESULT__BUILD,
							oldBuild, build));
			}
		}
		return build;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IBuild basicGetBuild() {
		return build;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestResult#getBuild <em>Build</em>}'
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Build</em>' reference.
	 * @see #getBuild()
	 * @generated
	 */
	public void setBuild(IBuild newBuild) {
		IBuild oldBuild = build;
		build = newBuild;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_RESULT__BUILD, oldBuild, build));
	}

	/**
	 * Returns the value of the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Duration</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Duration</em>' attribute.
	 * @see #setDuration(long)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestResult_Duration()
	 * @model
	 * @generated
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestResult#getDuration <em>Duration</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Duration</em>' attribute.
	 * @see #getDuration()
	 * @generated
	 */
	public void setDuration(long newDuration) {
		long oldDuration = duration;
		duration = newDuration;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_RESULT__DURATION, oldDuration,
					duration));
	}

	/**
	 * Returns the value of the '<em><b>Fail Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fail Count</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Fail Count</em>' attribute.
	 * @see #setFailCount(int)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestResult_FailCount()
	 * @model
	 * @generated
	 */
	public int getFailCount() {
		return failCount;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestResult#getFailCount <em>Fail Count</em>}
	 * ' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Fail Count</em>' attribute.
	 * @see #getFailCount()
	 * @generated
	 */
	public void setFailCount(int newFailCount) {
		int oldFailCount = failCount;
		failCount = newFailCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_RESULT__FAIL_COUNT, oldFailCount,
					failCount));
	}

	/**
	 * Returns the value of the '<em><b>Pass Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Pass Count</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Pass Count</em>' attribute.
	 * @see #setPassCount(int)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestResult_PassCount()
	 * @model
	 * @generated
	 */
	public int getPassCount() {
		return passCount;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestResult#getPassCount <em>Pass Count</em>}
	 * ' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Pass Count</em>' attribute.
	 * @see #getPassCount()
	 * @generated
	 */
	public void setPassCount(int newPassCount) {
		int oldPassCount = passCount;
		passCount = newPassCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_RESULT__PASS_COUNT, oldPassCount,
					passCount));
	}

	/**
	 * Returns the value of the '<em><b>Suites</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.internal.builds.core.TestSuite}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.internal.builds.core.TestSuite#getResult
	 * <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Suites</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Suites</em>' containment reference list.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestResult_Suites()
	 * @see org.eclipse.mylyn.internal.builds.core.TestSuite#getResult
	 * @model opposite="result" containment="true"
	 * @generated
	 */
	public EList<TestSuite> getSuites() {
		if (suites == null) {
			suites = new EObjectContainmentWithInverseEList<TestSuite>(TestSuite.class, this,
					BuildPackage.TEST_RESULT__SUITES, BuildPackage.TEST_SUITE__RESULT);
		}
		return suites;
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
		case BuildPackage.TEST_RESULT__SUITES:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getSuites()).basicAdd(otherEnd, msgs);
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
		case BuildPackage.TEST_RESULT__SUITES:
			return ((InternalEList<?>) getSuites()).basicRemove(otherEnd, msgs);
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
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case BuildPackage.TEST_RESULT__BUILD:
			if (resolve)
				return getBuild();
			return basicGetBuild();
		case BuildPackage.TEST_RESULT__DURATION:
			return getDuration();
		case BuildPackage.TEST_RESULT__FAIL_COUNT:
			return getFailCount();
		case BuildPackage.TEST_RESULT__PASS_COUNT:
			return getPassCount();
		case BuildPackage.TEST_RESULT__SUITES:
			return getSuites();
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
		case BuildPackage.TEST_RESULT__BUILD:
			setBuild((IBuild) newValue);
			return;
		case BuildPackage.TEST_RESULT__DURATION:
			setDuration((Long) newValue);
			return;
		case BuildPackage.TEST_RESULT__FAIL_COUNT:
			setFailCount((Integer) newValue);
			return;
		case BuildPackage.TEST_RESULT__PASS_COUNT:
			setPassCount((Integer) newValue);
			return;
		case BuildPackage.TEST_RESULT__SUITES:
			getSuites().clear();
			getSuites().addAll((Collection<? extends TestSuite>) newValue);
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
		case BuildPackage.TEST_RESULT__BUILD:
			setBuild((IBuild) null);
			return;
		case BuildPackage.TEST_RESULT__DURATION:
			setDuration(DURATION_EDEFAULT);
			return;
		case BuildPackage.TEST_RESULT__FAIL_COUNT:
			setFailCount(FAIL_COUNT_EDEFAULT);
			return;
		case BuildPackage.TEST_RESULT__PASS_COUNT:
			setPassCount(PASS_COUNT_EDEFAULT);
			return;
		case BuildPackage.TEST_RESULT__SUITES:
			getSuites().clear();
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
		case BuildPackage.TEST_RESULT__BUILD:
			return build != null;
		case BuildPackage.TEST_RESULT__DURATION:
			return duration != DURATION_EDEFAULT;
		case BuildPackage.TEST_RESULT__FAIL_COUNT:
			return failCount != FAIL_COUNT_EDEFAULT;
		case BuildPackage.TEST_RESULT__PASS_COUNT:
			return passCount != PASS_COUNT_EDEFAULT;
		case BuildPackage.TEST_RESULT__SUITES:
			return suites != null && !suites.isEmpty();
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
		result.append(" (duration: "); //$NON-NLS-1$
		result.append(duration);
		result.append(", failCount: "); //$NON-NLS-1$
		result.append(failCount);
		result.append(", passCount: "); //$NON-NLS-1$
		result.append(passCount);
		result.append(')');
		return result.toString();
	}

} // TestResult
