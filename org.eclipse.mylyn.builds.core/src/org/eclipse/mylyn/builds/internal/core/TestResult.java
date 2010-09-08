/**
 * <copyright>
 * </copyright>
 *
 * $Id: TestResult.java,v 1.3 2010/09/08 00:31:12 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import java.util.Collection;
import java.util.List;

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
import org.eclipse.mylyn.builds.core.ITestResult;
import org.eclipse.mylyn.builds.core.ITestSuite;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Result</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestResult#getBuild <em>Build</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestResult#getDuration <em>Duration</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestResult#getFailCount <em>Fail Count</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestResult#getPassCount <em>Pass Count</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestResult#getIgnoredCount <em>Ignored Count</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestResult#getErrorCount <em>Error Count</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestResult#getSuites <em>Suites</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TestResult extends EObjectImpl implements ITestResult {
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
	 * The default value of the '{@link #getIgnoredCount() <em>Ignored Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getIgnoredCount()
	 * @generated
	 * @ordered
	 */
	protected static final int IGNORED_COUNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getIgnoredCount() <em>Ignored Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getIgnoredCount()
	 * @generated
	 * @ordered
	 */
	protected int ignoredCount = IGNORED_COUNT_EDEFAULT;

	/**
	 * The default value of the '{@link #getErrorCount() <em>Error Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getErrorCount()
	 * @generated
	 * @ordered
	 */
	protected static final int ERROR_COUNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getErrorCount() <em>Error Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getErrorCount()
	 * @generated
	 * @ordered
	 */
	protected int errorCount = ERROR_COUNT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSuites() <em>Suites</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getSuites()
	 * @generated
	 * @ordered
	 */
	protected EList<ITestSuite> suites;

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
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Build</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setBuild(IBuild newBuild) {
		IBuild oldBuild = build;
		build = newBuild;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_RESULT__BUILD, oldBuild, build));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Duration</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
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
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fail Count</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getFailCount() {
		return failCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
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
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Pass Count</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getPassCount() {
		return passCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getIgnoredCount() {
		return ignoredCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setIgnoredCount(int newIgnoredCount) {
		int oldIgnoredCount = ignoredCount;
		ignoredCount = newIgnoredCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_RESULT__IGNORED_COUNT,
					oldIgnoredCount, ignoredCount));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getErrorCount() {
		return errorCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setErrorCount(int newErrorCount) {
		int oldErrorCount = errorCount;
		errorCount = newErrorCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_RESULT__ERROR_COUNT, oldErrorCount,
					errorCount));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Suites</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<ITestSuite> getSuites() {
		if (suites == null) {
			suites = new EObjectContainmentWithInverseEList<ITestSuite>(ITestSuite.class, this,
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
		case BuildPackage.TEST_RESULT__IGNORED_COUNT:
			return getIgnoredCount();
		case BuildPackage.TEST_RESULT__ERROR_COUNT:
			return getErrorCount();
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
		case BuildPackage.TEST_RESULT__IGNORED_COUNT:
			setIgnoredCount((Integer) newValue);
			return;
		case BuildPackage.TEST_RESULT__ERROR_COUNT:
			setErrorCount((Integer) newValue);
			return;
		case BuildPackage.TEST_RESULT__SUITES:
			getSuites().clear();
			getSuites().addAll((Collection<? extends ITestSuite>) newValue);
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
		case BuildPackage.TEST_RESULT__IGNORED_COUNT:
			setIgnoredCount(IGNORED_COUNT_EDEFAULT);
			return;
		case BuildPackage.TEST_RESULT__ERROR_COUNT:
			setErrorCount(ERROR_COUNT_EDEFAULT);
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
		case BuildPackage.TEST_RESULT__IGNORED_COUNT:
			return ignoredCount != IGNORED_COUNT_EDEFAULT;
		case BuildPackage.TEST_RESULT__ERROR_COUNT:
			return errorCount != ERROR_COUNT_EDEFAULT;
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
		result.append(", ignoredCount: "); //$NON-NLS-1$
		result.append(ignoredCount);
		result.append(", errorCount: "); //$NON-NLS-1$
		result.append(errorCount);
		result.append(')');
		return result.toString();
	}

} // TestResult
