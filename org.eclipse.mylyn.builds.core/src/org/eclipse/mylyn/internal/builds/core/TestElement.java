/**
 * <copyright>
 * </copyright>
 *
 * $Id: TestElement.java,v 1.1 2010/08/27 09:00:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestElement#getLabel <em>Label</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestElement#getDuration <em>Duration</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestElement#getErrorOutput <em>Error Output</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.TestElement#getOutput <em>Output</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestElement()
 * @model kind="class"
 * @generated
 */
public class TestElement extends EObjectImpl implements EObject {
	/**
	 * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected static final String LABEL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected String label = LABEL_EDEFAULT;

	/**
	 * The default value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected static final double DURATION_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected double duration = DURATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getErrorOutput() <em>Error Output</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getErrorOutput()
	 * @generated
	 * @ordered
	 */
	protected static final String ERROR_OUTPUT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getErrorOutput() <em>Error Output</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getErrorOutput()
	 * @generated
	 * @ordered
	 */
	protected String errorOutput = ERROR_OUTPUT_EDEFAULT;

	/**
	 * The default value of the '{@link #getOutput() <em>Output</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getOutput()
	 * @generated
	 * @ordered
	 */
	protected static final String OUTPUT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOutput() <em>Output</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getOutput()
	 * @generated
	 * @ordered
	 */
	protected String output = OUTPUT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TestElement() {
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
		return BuildPackage.Literals.TEST_ELEMENT;
	}

	/**
	 * Returns the value of the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Label</em>' attribute.
	 * @see #setLabel(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestElement_Label()
	 * @model
	 * @generated
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestElement#getLabel <em>Label</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Label</em>' attribute.
	 * @see #getLabel()
	 * @generated
	 */
	public void setLabel(String newLabel) {
		String oldLabel = label;
		label = newLabel;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_ELEMENT__LABEL, oldLabel, label));
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
	 * @see #setDuration(double)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestElement_Duration()
	 * @model
	 * @generated
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestElement#getDuration <em>Duration</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Duration</em>' attribute.
	 * @see #getDuration()
	 * @generated
	 */
	public void setDuration(double newDuration) {
		double oldDuration = duration;
		duration = newDuration;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.TEST_ELEMENT__DURATION, oldDuration,
					duration));
	}

	/**
	 * Returns the value of the '<em><b>Error Output</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Error Output</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Error Output</em>' attribute.
	 * @see #setErrorOutput(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestElement_ErrorOutput()
	 * @model
	 * @generated
	 */
	public String getErrorOutput() {
		return errorOutput;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestElement#getErrorOutput
	 * <em>Error Output</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Error Output</em>' attribute.
	 * @see #getErrorOutput()
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
	 * Returns the value of the '<em><b>Output</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Output</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Output</em>' attribute.
	 * @see #setOutput(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestElement_Output()
	 * @model
	 * @generated
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.TestElement#getOutput <em>Output</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Output</em>' attribute.
	 * @see #getOutput()
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
	 * 
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
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case BuildPackage.TEST_ELEMENT__LABEL:
			setLabel((String) newValue);
			return;
		case BuildPackage.TEST_ELEMENT__DURATION:
			setDuration((Double) newValue);
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
	 * 
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
	 * 
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
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (label: ");
		result.append(label);
		result.append(", duration: ");
		result.append(duration);
		result.append(", errorOutput: ");
		result.append(errorOutput);
		result.append(", output: ");
		result.append(output);
		result.append(')');
		return result.toString();
	}

} // TestElement
