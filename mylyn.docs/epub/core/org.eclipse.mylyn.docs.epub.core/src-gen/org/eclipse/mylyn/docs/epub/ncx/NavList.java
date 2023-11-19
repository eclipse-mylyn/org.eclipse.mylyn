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
 * A representation of the model object '<em><b>Nav List</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getNavInfos <em>Nav Infos</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getNavLabels <em>Nav Labels</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getNavTargets <em>Nav Targets</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getId <em>Id</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavList()
 * @model
 * @generated
 */
public interface NavList extends EObject {
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
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavList_NavInfos()
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
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavList_NavLabels()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='navLabel' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<NavLabel> getNavLabels();

	/**
	 * Returns the value of the '<em><b>Nav Targets</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.ncx.NavTarget}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nav Targets</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nav Targets</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavList_NavTargets()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='navTarget' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<NavTarget> getNavTargets();

	/**
	 * Returns the value of the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Class</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Class</em>' attribute.
	 * @see #setClass(Object)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavList_Class()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnySimpleType"
	 *        extendedMetaData="kind='attribute' name='class'"
	 * @generated
	 */
	Object getClass_();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getClass_ <em>Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Class</em>' attribute.
	 * @see #getClass_()
	 * @generated
	 */
	void setClass(Object value);

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
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNavList_Id()
	 * @model id="true" dataType="org.eclipse.emf.ecore.xml.type.ID"
	 *        extendedMetaData="kind='attribute' name='id'"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // NavList
