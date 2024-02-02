/**
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Choice Parameter Definition</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.IChoiceParameterDefinition#getOptions <em>Options</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IChoiceParameterDefinition#getDefaultValue <em>Default Value</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IChoiceParameterDefinition extends IParameterDefinition {
	/**
	 * Returns the value of the '<em><b>Options</b></em>' attribute list. The list contents are of type {@link java.lang.String}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Options</em>' attribute list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Options</em>' attribute list.
	 * @generated
	 */
	List<String> getOptions();

	/**
	 * Returns the value of the '<em><b>Default Value</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Default Value</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Default Value</em>' attribute.
	 * @see #setDefaultValue(String)
	 * @generated
	 */
	String getDefaultValue();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IChoiceParameterDefinition#getDefaultValue <em>Default Value</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Default Value</em>' attribute.
	 * @see #getDefaultValue()
	 * @generated
	 */
	void setDefaultValue(String value);

} // IChoiceParameterDefinition
