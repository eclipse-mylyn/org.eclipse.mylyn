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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Change Artifact</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getFile <em>File</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getRelativePath <em>Relative Path</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getPrevRevision <em>Prev Revision</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getRevision <em>Revision</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IChangeArtifact#isDead <em>Dead</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getEditType <em>Edit Type</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IChangeArtifact {
	/**
	 * Returns the value of the '<em><b>File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>File</em>' attribute.
	 * @see #setFile(String)
	 * @generated
	 */
	String getFile();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getFile <em>File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>File</em>' attribute.
	 * @see #getFile()
	 * @generated
	 */
	void setFile(String value);

	/**
	 * Returns the value of the '<em><b>Relative Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Relative Path</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Relative Path</em>' attribute.
	 * @see #setRelativePath(String)
	 * @generated
	 */
	String getRelativePath();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getRelativePath
	 * <em>Relative Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Relative Path</em>' attribute.
	 * @see #getRelativePath()
	 * @generated
	 */
	void setRelativePath(String value);

	/**
	 * Returns the value of the '<em><b>Prev Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Prev Revision</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Prev Revision</em>' attribute.
	 * @see #setPrevRevision(String)
	 * @generated
	 */
	String getPrevRevision();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getPrevRevision
	 * <em>Prev Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Prev Revision</em>' attribute.
	 * @see #getPrevRevision()
	 * @generated
	 */
	void setPrevRevision(String value);

	/**
	 * Returns the value of the '<em><b>Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Revision</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Revision</em>' attribute.
	 * @see #setRevision(String)
	 * @generated
	 */
	String getRevision();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getRevision <em>Revision</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Revision</em>' attribute.
	 * @see #getRevision()
	 * @generated
	 */
	void setRevision(String value);

	/**
	 * Returns the value of the '<em><b>Dead</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dead</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Dead</em>' attribute.
	 * @see #setDead(boolean)
	 * @generated
	 */
	boolean isDead();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#isDead <em>Dead</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Dead</em>' attribute.
	 * @see #isDead()
	 * @generated
	 */
	void setDead(boolean value);

	/**
	 * Returns the value of the '<em><b>Edit Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Edit Type</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Edit Type</em>' attribute.
	 * @see #setEditType(EditType)
	 * @generated
	 */
	EditType getEditType();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getEditType <em>Edit Type</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Edit Type</em>' attribute.
	 * @see #getEditType()
	 * @generated
	 */
	void setEditType(EditType value);

} // IChangeArtifact
