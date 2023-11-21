/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Nav Map</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.NavMap#getNavInfos <em>Nav Infos</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.NavMap#getNavLabels <em>Nav Labels</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.NavMap#getNavPoints <em>Nav Points</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.NavMap#getId <em>Id</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavMap()
 * @model
 * @generated
 */
public interface NavMap extends EObject {
	/**
	 * Returns the value of the '<em><b>Nav Infos</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.ncx.NavInfo}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nav Infos</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nav Infos</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavMap_NavInfos()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='navInfo' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<NavInfo> getNavInfos();

	/**
	 * Returns the value of the '<em><b>Nav Labels</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.ncx.NavLabel}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nav Labels</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nav Labels</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavMap_NavLabels()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='navLabel' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<NavLabel> getNavLabels();

	/**
	 * Returns the value of the '<em><b>Nav Points</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.ncx.NavPoint}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nav Points</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nav Points</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavMap_NavPoints()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='navPoint' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<NavPoint> getNavPoints();

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavMap_Id()
	 * @model id="true" dataType="org.eclipse.emf.ecore.xml.type.ID"
	 *        extendedMetaData="kind='attribute' name='id'"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.NavMap#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // NavMap
