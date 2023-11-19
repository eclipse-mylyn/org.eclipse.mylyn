/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Head</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Head#getGroups <em>Groups</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Head#getSmilCustomTests <em>Smil Custom Tests</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Head#getMetas <em>Metas</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getHead()
 * @model
 * @generated
 */
public interface Head extends EObject {
	/**
	 * Returns the value of the '<em><b>Groups</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Groups</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Groups</em>' attribute list.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getHead_Groups()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:0'"
	 * @generated
	 */
	FeatureMap getGroups();

	/**
	 * Returns the value of the '<em><b>Smil Custom Tests</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Smil Custom Tests</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Smil Custom Tests</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getHead_SmilCustomTests()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='smilCustomTest' namespace='##targetNamespace' group='#group:0'"
	 * @generated
	 */
	EList<SmilCustomTest> getSmilCustomTests();

	/**
	 * Returns the value of the '<em><b>Metas</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.ncx.Meta}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Metas</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metas</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getHead_Metas()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='meta' namespace='##targetNamespace' group='#group:0'"
	 * @generated
	 */
	EList<Meta> getMetas();

} // Head
