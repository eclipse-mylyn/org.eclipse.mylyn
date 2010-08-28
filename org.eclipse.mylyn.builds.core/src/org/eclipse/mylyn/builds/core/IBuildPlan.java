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
 * A representation of the model object '<em><b>Plan</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getServer <em>Server</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getChildren <em>Children</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getParent <em>Parent</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getHealth <em>Health</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getInfo <em>Info</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#isSelected <em>Selected</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getSummary <em>Summary</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getState <em>State</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getStatus <em>Status</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getDescription <em>Description</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getLastBuild <em>Last Build</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildPlan#getParameterDefinitions <em>Parameter Definitions</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IBuildPlan extends IBuildElement {
	/**
	 * Returns the value of the '<em><b>Server</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Server</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Server</em>' reference.
	 * @see #setServer(IBuildServer)
	 * @generated
	 */
	IBuildServer getServer();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getServer <em>Server</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Server</em>' reference.
	 * @see #getServer()
	 * @generated
	 */
	void setServer(IBuildServer value);

	/**
	 * Returns the value of the '<em><b>Children</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.IBuildPlan}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getParent
	 * <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Children</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Children</em>' reference list.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getParent
	 * @generated
	 */
	List<IBuildPlan> getChildren();

	/**
	 * Returns the value of the '<em><b>Parent</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getChildren
	 * <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parent</em>' reference.
	 * @see #setParent(IBuildPlan)
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getChildren
	 * @generated
	 */
	IBuildPlan getParent();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getParent <em>Parent</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Parent</em>' reference.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(IBuildPlan value);

	/**
	 * Returns the value of the '<em><b>Health</b></em>' attribute.
	 * The default value is <code>"-1"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Health</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Health</em>' attribute.
	 * @see #setHealth(int)
	 * @generated
	 */
	int getHealth();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getHealth <em>Health</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Health</em>' attribute.
	 * @see #getHealth()
	 * @generated
	 */
	void setHealth(int value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Info</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Info</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Info</em>' attribute.
	 * @see #setInfo(String)
	 * @generated
	 */
	String getInfo();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getInfo <em>Info</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Info</em>' attribute.
	 * @see #getInfo()
	 * @generated
	 */
	void setInfo(String value);

	/**
	 * Returns the value of the '<em><b>Selected</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Selected</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Selected</em>' attribute.
	 * @see #setSelected(boolean)
	 * @generated
	 */
	boolean isSelected();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#isSelected <em>Selected</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Selected</em>' attribute.
	 * @see #isSelected()
	 * @generated
	 */
	void setSelected(boolean value);

	/**
	 * Returns the value of the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Summary</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Summary</em>' attribute.
	 * @see #setSummary(String)
	 * @generated
	 */
	String getSummary();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getSummary <em>Summary</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Summary</em>' attribute.
	 * @see #getSummary()
	 * @generated
	 */
	void setSummary(String value);

	/**
	 * Returns the value of the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>State</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>State</em>' attribute.
	 * @see #setState(BuildState)
	 * @generated
	 */
	BuildState getState();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getState <em>State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>State</em>' attribute.
	 * @see #getState()
	 * @generated
	 */
	void setState(BuildState value);

	/**
	 * Returns the value of the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Status</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Status</em>' attribute.
	 * @see #setStatus(BuildStatus)
	 * @generated
	 */
	BuildStatus getStatus();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getStatus <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Status</em>' attribute.
	 * @see #getStatus()
	 * @generated
	 */
	void setStatus(BuildStatus value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getDescription <em>Description</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Last Build</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Build</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Last Build</em>' reference.
	 * @see #setLastBuild(IBuild)
	 * @generated
	 */
	IBuild getLastBuild();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getLastBuild <em>Last Build</em>}'
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Last Build</em>' reference.
	 * @see #getLastBuild()
	 * @generated
	 */
	void setLastBuild(IBuild value);

	/**
	 * Returns the value of the '<em><b>Parameter Definitions</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.IParameterDefinition}.
	 * It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.builds.core.IParameterDefinition#getContainingBuildPlan <em>Containing Build Plan</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Definitions</em>' containment reference list isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parameter Definitions</em>' containment reference list.
	 * @see org.eclipse.mylyn.builds.core.IParameterDefinition#getContainingBuildPlan
	 * @generated
	 */
	List<IParameterDefinition> getParameterDefinitions();

} // IBuildPlan
