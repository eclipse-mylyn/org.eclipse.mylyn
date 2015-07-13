/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.dc;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Identifier</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.dc.Identifier#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.dc.Identifier#getScheme <em>Scheme</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.dc.Identifier#getMixed <em>Mixed</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.dc.DCPackage#getIdentifier()
 * @model extendedMetaData="kind='mixed'"
 *        extendedMetaData="kind='mixed'"
 * @generated
 */
public interface Identifier extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * The default value is <code>"BookId"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.mylyn.docs.epub.dc.DCPackage#getIdentifier_Id()
	 * @model default="BookId" required="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.dc.Identifier#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Scheme</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Scheme</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Scheme</em>' attribute.
	 * @see #isSetScheme()
	 * @see #unsetScheme()
	 * @see #setScheme(String)
	 * @see org.eclipse.mylyn.docs.epub.dc.DCPackage#getIdentifier_Scheme()
	 * @model unsettable="true"
	 *        extendedMetaData="namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	String getScheme();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.dc.Identifier#getScheme <em>Scheme</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Scheme</em>' attribute.
	 * @see #isSetScheme()
	 * @see #unsetScheme()
	 * @see #getScheme()
	 * @generated
	 */
	void setScheme(String value);

	/**
	 * Unsets the value of the '{@link org.eclipse.mylyn.docs.epub.dc.Identifier#getScheme <em>Scheme</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetScheme()
	 * @see #getScheme()
	 * @see #setScheme(String)
	 * @generated
	 */
	void unsetScheme();

	/**
	 * Returns whether the value of the '{@link org.eclipse.mylyn.docs.epub.dc.Identifier#getScheme <em>Scheme</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Scheme</em>' attribute is set.
	 * @see #unsetScheme()
	 * @see #getScheme()
	 * @see #setScheme(String)
	 * @generated
	 */
	boolean isSetScheme();

	/**
	 * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mixed</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mixed</em>' attribute list.
	 * @see org.eclipse.mylyn.docs.epub.dc.DCPackage#getIdentifier_Mixed()
	 * @model dataType="org.eclipse.emf.ecore.EFeatureMapEntry" required="true" many="true"
	 *        extendedMetaData="kind='elementWildcard' name=':mixed'"
	 * @generated
	 */
	FeatureMap getMixed();

} // Identifier
