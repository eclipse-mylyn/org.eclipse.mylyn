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
 * A representation of the model object '<em><b>Audio</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getClipBegin <em>Clip Begin</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getClipEnd <em>Clip End</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getSrc <em>Src</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getAudio()
 * @model
 * @generated
 */
public interface Audio extends EObject {
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
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getAudio_Class()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnySimpleType"
	 *        extendedMetaData="kind='attribute' name='class'"
	 * @generated
	 */
	Object getClass_();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getClass_ <em>Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Class</em>' attribute.
	 * @see #getClass_()
	 * @generated
	 */
	void setClass(Object value);

	/**
	 * Returns the value of the '<em><b>Clip Begin</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Clip Begin</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Clip Begin</em>' attribute.
	 * @see #setClipBegin(String)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getAudio_ClipBegin()
	 * @model dataType="org.eclipse.mylyn.docs.epub.ncx.SMILtimeVal" required="true"
	 *        extendedMetaData="kind='attribute' name='clipBegin'"
	 * @generated
	 */
	String getClipBegin();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getClipBegin <em>Clip Begin</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Clip Begin</em>' attribute.
	 * @see #getClipBegin()
	 * @generated
	 */
	void setClipBegin(String value);

	/**
	 * Returns the value of the '<em><b>Clip End</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Clip End</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Clip End</em>' attribute.
	 * @see #setClipEnd(String)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getAudio_ClipEnd()
	 * @model dataType="org.eclipse.mylyn.docs.epub.ncx.SMILtimeVal" required="true"
	 *        extendedMetaData="kind='attribute' name='clipEnd'"
	 * @generated
	 */
	String getClipEnd();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getClipEnd <em>Clip End</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Clip End</em>' attribute.
	 * @see #getClipEnd()
	 * @generated
	 */
	void setClipEnd(String value);

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
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getAudio_Id()
	 * @model id="true" dataType="org.eclipse.emf.ecore.xml.type.ID"
	 *        extendedMetaData="kind='attribute' name='id'"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Src</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Src</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Src</em>' attribute.
	 * @see #setSrc(String)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getAudio_Src()
	 * @model dataType="org.eclipse.mylyn.docs.epub.ncx.URI" required="true"
	 *        extendedMetaData="kind='attribute' name='src'"
	 * @generated
	 */
	String getSrc();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getSrc <em>Src</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Src</em>' attribute.
	 * @see #getSrc()
	 * @generated
	 */
	void setSrc(String value);

} // Audio
