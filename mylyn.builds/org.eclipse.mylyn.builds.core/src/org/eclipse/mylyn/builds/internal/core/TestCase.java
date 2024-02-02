/**
 * <copyright>
 * </copyright>
 *
 * $Id: TestCase.java,v 1.3 2010/09/08 03:27:17 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.TestCaseResult;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Test Case</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestCase#getClassName <em>Class Name</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestCase#isSkipped <em>Skipped</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestCase#getSuite <em>Suite</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestCase#getStatus <em>Status</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestCase#getMessage <em>Message</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.TestCase#getStackTrace <em>Stack Trace</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TestCase extends TestElement implements ITestCase {
	/**
	 * The default value of the '{@link #getClassName() <em>Class Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getClassName()
	 * @generated
	 * @ordered
	 */
	protected static final String CLASS_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getClassName() <em>Class Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getClassName()
	 * @generated
	 * @ordered
	 */
	protected String className = CLASS_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #isSkipped() <em>Skipped</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #isSkipped()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SKIPPED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSkipped() <em>Skipped</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #isSkipped()
	 * @generated
	 * @ordered
	 */
	protected boolean skipped = SKIPPED_EDEFAULT;

	/**
	 * The default value of the '{@link #getStatus() <em>Status</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected static final TestCaseResult STATUS_EDEFAULT = TestCaseResult.PASSED;

	/**
	 * The cached value of the '{@link #getStatus() <em>Status</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected TestCaseResult status = STATUS_EDEFAULT;

	/**
	 * The default value of the '{@link #getMessage() <em>Message</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected static final String MESSAGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMessage() <em>Message</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected String message = MESSAGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getStackTrace() <em>Stack Trace</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getStackTrace()
	 * @generated
	 * @ordered
	 */
	protected static final String STACK_TRACE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getStackTrace() <em>Stack Trace</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getStackTrace()
	 * @generated
	 * @ordered
	 */
	protected String stackTrace = STACK_TRACE_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TestCase() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.TEST_CASE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Class Name</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getClassName() {
		return className;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setClassName(String newClassName) {
		String oldClassName = className;
		className = newClassName;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_CASE__CLASS_NAME, oldClassName,
					className));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Skipped</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean isSkipped() {
		return skipped;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setSkipped(boolean newSkipped) {
		boolean oldSkipped = skipped;
		skipped = newSkipped;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_CASE__SKIPPED, oldSkipped,
					skipped));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Suite</em>' container reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ITestSuite getSuite() {
		if (eContainerFeatureID() != BuildPackage.TEST_CASE__SUITE) {
			return null;
		}
		return (ITestSuite) eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetSuite(ITestSuite newSuite, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newSuite, BuildPackage.TEST_CASE__SUITE, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setSuite(ITestSuite newSuite) {
		if (newSuite != eInternalContainer()
				|| eContainerFeatureID() != BuildPackage.TEST_CASE__SUITE && newSuite != null) {
			if (EcoreUtil.isAncestor(this, (EObject) newSuite)) {
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			}
			NotificationChain msgs = null;
			if (eInternalContainer() != null) {
				msgs = eBasicRemoveFromContainer(msgs);
			}
			if (newSuite != null) {
				msgs = ((InternalEObject) newSuite).eInverseAdd(this, BuildPackage.TEST_SUITE__CASES, ITestSuite.class,
						msgs);
			}
			msgs = basicSetSuite(newSuite, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_CASE__SUITE, newSuite, newSuite));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Status</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public TestCaseResult getStatus() {
		return status;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setStatus(TestCaseResult newStatus) {
		TestCaseResult oldStatus = status;
		status = newStatus == null ? STATUS_EDEFAULT : newStatus;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_CASE__STATUS, oldStatus, status));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setMessage(String newMessage) {
		String oldMessage = message;
		message = newMessage;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_CASE__MESSAGE, oldMessage,
					message));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getStackTrace() {
		return stackTrace;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setStackTrace(String newStackTrace) {
		String oldStackTrace = stackTrace;
		stackTrace = newStackTrace;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_CASE__STACK_TRACE, oldStackTrace,
					stackTrace));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BuildPackage.TEST_CASE__SUITE:
				if (eInternalContainer() != null) {
					msgs = eBasicRemoveFromContainer(msgs);
				}
				return basicSetSuite((ITestSuite) otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case BuildPackage.TEST_CASE__SUITE:
				return eInternalContainer().eInverseRemove(this, BuildPackage.TEST_SUITE__CASES, ITestSuite.class,
						msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
			case BuildPackage.TEST_CASE__MESSAGE:
				return getMessage();
			case BuildPackage.TEST_CASE__STACK_TRACE:
				return getStackTrace();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
				setSuite((ITestSuite) newValue);
				return;
			case BuildPackage.TEST_CASE__STATUS:
				setStatus((TestCaseResult) newValue);
				return;
			case BuildPackage.TEST_CASE__MESSAGE:
				setMessage((String) newValue);
				return;
			case BuildPackage.TEST_CASE__STACK_TRACE:
				setStackTrace((String) newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
				setSuite((ITestSuite) null);
				return;
			case BuildPackage.TEST_CASE__STATUS:
				setStatus(STATUS_EDEFAULT);
				return;
			case BuildPackage.TEST_CASE__MESSAGE:
				setMessage(MESSAGE_EDEFAULT);
				return;
			case BuildPackage.TEST_CASE__STACK_TRACE:
				setStackTrace(STACK_TRACE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
			case BuildPackage.TEST_CASE__MESSAGE:
				return MESSAGE_EDEFAULT == null ? message != null : !MESSAGE_EDEFAULT.equals(message);
			case BuildPackage.TEST_CASE__STACK_TRACE:
				return STACK_TRACE_EDEFAULT == null ? stackTrace != null : !STACK_TRACE_EDEFAULT.equals(stackTrace);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (className: "); //$NON-NLS-1$
		result.append(className);
		result.append(", skipped: "); //$NON-NLS-1$
		result.append(skipped);
		result.append(", status: "); //$NON-NLS-1$
		result.append(status);
		result.append(", message: "); //$NON-NLS-1$
		result.append(message);
		result.append(", stackTrace: "); //$NON-NLS-1$
		result.append(stackTrace);
		result.append(')');
		return result.toString();
	}

} // TestCase
