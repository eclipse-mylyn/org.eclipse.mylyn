/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Itemref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Itemref#getIdref <em>Idref</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Itemref#getLinear <em>Linear</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItemref()
 * @model
 * @generated
 */
public interface Itemref extends EObject {
	/**
	 * Returns the value of the '<em><b>Idref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Idref</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Idref</em>' attribute.
	 * @see #setIdref(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItemref_Idref()
	 * @model required="true"
	 * @generated
	 */
	String getIdref();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Itemref#getIdref <em>Idref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Idref</em>' attribute.
	 * @see #getIdref()
	 * @generated
	 */
	void setIdref(String value);

	/**
	 * Returns the value of the '<em><b>Linear</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Linear</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Linear</em>' attribute.
	 * @see #setLinear(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItemref_Linear()
	 * @model
	 * @generated
	 */
	String getLinear();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Itemref#getLinear <em>Linear</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Linear</em>' attribute.
	 * @see #getLinear()
	 * @generated
	 */
	void setLinear(String value);

} // Itemref
