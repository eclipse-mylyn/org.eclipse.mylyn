/**
 * <copyright>
 * </copyright>
 *
 * $Id: TestElement.java,v 1.1 2010/08/28 06:14:17 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.mylyn.builds.core.ITestElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.builds.internal.core.TestElement#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.mylyn.builds.internal.core.TestElement#getDuration <em>Duration</em>}</li>
 *   <li>{@link org.eclipse.mylyn.builds.internal.core.TestElement#getErrorOutput <em>Error Output</em>}</li>
 *   <li>{@link org.eclipse.mylyn.builds.internal.core.TestElement#getOutput <em>Output</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TestElement extends EObjectImpl implements ITestElement {
	/**
	 * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected static final String LABEL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected String label = LABEL_EDEFAULT;

	/**
	 * The default value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected static final long DURATION_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected long duration = DURATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getErrorOutput() <em>Error Output</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getErrorOutput()
	 * @generated
	 * @ordered
	 */
	protected static final String ERROR_OUTPUT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getErrorOutput() <em>Error Output</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getErrorOutput()
	 * @generated
	 * @ordered
	 */
	protected String errorOutput = ERROR_OUTPUT_EDEFAULT;

	/**
	 * The default value of the '{@link #getOutput() <em>Output</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutput()
	 * @generated
	 * @ordered
	 */
	protected static final String OUTPUT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOutput() <em>Output</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutput()
	 * @generated
	 * @ordered
	 */
	protected String output = OUTPUT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TestElement() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.TEST_ELEMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLabel(String newLabel) {
		String oldLabel = label;
		label = newLabel;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_ELEMENT__LABEL, oldLabel, label));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Duration</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDuration(long newDuration) {
		long oldDuration = duration;
		duration = newDuration;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_ELEMENT__DURATION, oldDuration,
					duration));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Error Output</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getErrorOutput() {
		return errorOutput;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setErrorOutput(String newErrorOutput) {
		String oldErrorOutput = errorOutput;
		errorOutput = newErrorOutput;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_ELEMENT__ERROR_OUTPUT,
					oldErrorOutput, errorOutput));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Output</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOutput(String newOutput) {
		String oldOutput = output;
		output = newOutput;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_ELEMENT__OUTPUT, oldOutput, output));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case BuildPackage.TEST_ELEMENT__LABEL:
			return getLabel();
		case BuildPackage.TEST_ELEMENT__DURATION:
			return getDuration();
		case BuildPackage.TEST_ELEMENT__ERROR_OUTPUT:
			return getErrorOutput();
		case BuildPackage.TEST_ELEMENT__OUTPUT:
			return getOutput();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case BuildPackage.TEST_ELEMENT__LABEL:
			setLabel((String) newValue);
			return;
		case BuildPackage.TEST_ELEMENT__DURATION:
			setDuration((Long) newValue);
			return;
		case BuildPackage.TEST_ELEMENT__ERROR_OUTPUT:
			setErrorOutput((String) newValue);
			return;
		case BuildPackage.TEST_ELEMENT__OUTPUT:
			setOutput((String) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case BuildPackage.TEST_ELEMENT__LABEL:
			setLabel(LABEL_EDEFAULT);
			return;
		case BuildPackage.TEST_ELEMENT__DURATION:
			setDuration(DURATION_EDEFAULT);
			return;
		case BuildPackage.TEST_ELEMENT__ERROR_OUTPUT:
			setErrorOutput(ERROR_OUTPUT_EDEFAULT);
			return;
		case BuildPackage.TEST_ELEMENT__OUTPUT:
			setOutput(OUTPUT_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case BuildPackage.TEST_ELEMENT__LABEL:
			return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
		case BuildPackage.TEST_ELEMENT__DURATION:
			return duration != DURATION_EDEFAULT;
		case BuildPackage.TEST_ELEMENT__ERROR_OUTPUT:
			return ERROR_OUTPUT_EDEFAULT == null ? errorOutput != null : !ERROR_OUTPUT_EDEFAULT.equals(errorOutput);
		case BuildPackage.TEST_ELEMENT__OUTPUT:
			return OUTPUT_EDEFAULT == null ? output != null : !OUTPUT_EDEFAULT.equals(output);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (label: "); //$NON-NLS-1$
		result.append(label);
		result.append(", duration: "); //$NON-NLS-1$
		result.append(duration);
		result.append(", errorOutput: "); //$NON-NLS-1$
		result.append(errorOutput);
		result.append(", output: "); //$NON-NLS-1$
		result.append(output);
		result.append(')');
		return result.toString();
	}

} // TestElement
