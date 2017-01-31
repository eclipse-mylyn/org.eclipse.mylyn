/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Smil Custom Test</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getBookStruct <em>Book Struct</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getDefaultState <em>Default State</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getOverride <em>Override</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getSmilCustomTest()
 * @model
 * @generated
 */
public interface SmilCustomTest extends EObject {
	/**
	 * Returns the value of the '<em><b>Book Struct</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.mylyn.docs.epub.ncx.BookStruct}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Book Struct</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Book Struct</em>' attribute.
	 * @see org.eclipse.mylyn.docs.epub.ncx.BookStruct
	 * @see #isSetBookStruct()
	 * @see #unsetBookStruct()
	 * @see #setBookStruct(BookStruct)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getSmilCustomTest_BookStruct()
	 * @model unsettable="true"
	 * @generated
	 */
	BookStruct getBookStruct();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getBookStruct <em>Book Struct</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Book Struct</em>' attribute.
	 * @see org.eclipse.mylyn.docs.epub.ncx.BookStruct
	 * @see #isSetBookStruct()
	 * @see #unsetBookStruct()
	 * @see #getBookStruct()
	 * @generated
	 */
	void setBookStruct(BookStruct value);

	/**
	 * Unsets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getBookStruct <em>Book Struct</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetBookStruct()
	 * @see #getBookStruct()
	 * @see #setBookStruct(BookStruct)
	 * @generated
	 */
	void unsetBookStruct();

	/**
	 * Returns whether the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getBookStruct <em>Book Struct</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Book Struct</em>' attribute is set.
	 * @see #unsetBookStruct()
	 * @see #getBookStruct()
	 * @see #setBookStruct(BookStruct)
	 * @generated
	 */
	boolean isSetBookStruct();

	/**
	 * Returns the value of the '<em><b>Default State</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * The literals are from the enumeration {@link org.eclipse.mylyn.docs.epub.ncx.DefaultState}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Default State</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Default State</em>' attribute.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DefaultState
	 * @see #isSetDefaultState()
	 * @see #unsetDefaultState()
	 * @see #setDefaultState(DefaultState)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getSmilCustomTest_DefaultState()
	 * @model default="false" unsettable="true"
	 * @generated
	 */
	DefaultState getDefaultState();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getDefaultState <em>Default State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default State</em>' attribute.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DefaultState
	 * @see #isSetDefaultState()
	 * @see #unsetDefaultState()
	 * @see #getDefaultState()
	 * @generated
	 */
	void setDefaultState(DefaultState value);

	/**
	 * Unsets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getDefaultState <em>Default State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDefaultState()
	 * @see #getDefaultState()
	 * @see #setDefaultState(DefaultState)
	 * @generated
	 */
	void unsetDefaultState();

	/**
	 * Returns whether the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getDefaultState <em>Default State</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Default State</em>' attribute is set.
	 * @see #unsetDefaultState()
	 * @see #getDefaultState()
	 * @see #setDefaultState(DefaultState)
	 * @generated
	 */
	boolean isSetDefaultState();

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
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getSmilCustomTest_Id()
	 * @model id="true" dataType="org.eclipse.emf.ecore.xml.type.ID" required="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Override</b></em>' attribute.
	 * The default value is <code>"hidden"</code>.
	 * The literals are from the enumeration {@link org.eclipse.mylyn.docs.epub.ncx.OverrideType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Override</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Override</em>' attribute.
	 * @see org.eclipse.mylyn.docs.epub.ncx.OverrideType
	 * @see #isSetOverride()
	 * @see #unsetOverride()
	 * @see #setOverride(OverrideType)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getSmilCustomTest_Override()
	 * @model default="hidden" unsettable="true"
	 * @generated
	 */
	OverrideType getOverride();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getOverride <em>Override</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Override</em>' attribute.
	 * @see org.eclipse.mylyn.docs.epub.ncx.OverrideType
	 * @see #isSetOverride()
	 * @see #unsetOverride()
	 * @see #getOverride()
	 * @generated
	 */
	void setOverride(OverrideType value);

	/**
	 * Unsets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getOverride <em>Override</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetOverride()
	 * @see #getOverride()
	 * @see #setOverride(OverrideType)
	 * @generated
	 */
	void unsetOverride();

	/**
	 * Returns whether the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getOverride <em>Override</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Override</em>' attribute is set.
	 * @see #unsetOverride()
	 * @see #getOverride()
	 * @see #setOverride(OverrideType)
	 * @generated
	 */
	boolean isSetOverride();

} // SmilCustomTest
