/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Meta</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.mylyn.docs.epub.opf.Meta#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.mylyn.docs.epub.opf.Meta#getContent <em>Content</em>}</li>
 * <li>{@link org.eclipse.mylyn.docs.epub.opf.Meta#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.docs.epub.opf.Meta#getProperty <em>Property</em>}</li>
 * <li>{@link org.eclipse.mylyn.docs.epub.opf.Meta#getRefines <em>Refines</em>}</li>
 * <li>{@link org.eclipse.mylyn.docs.epub.opf.Meta#getScheme <em>Scheme</em>}</li>
 * <li>{@link org.eclipse.mylyn.docs.epub.opf.Meta#getDir <em>Dir</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMeta()
 * @model
 * @generated
 */
public interface Meta extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMeta_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getName <em>Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Content</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Content</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Content</em>' attribute.
	 * @see #setContent(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMeta_Content()
	 * @model
	 * @generated
	 */
	String getContent();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getContent <em>Content</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Content</em>' attribute.
	 * @see #getContent()
	 * @generated
	 */
	void setContent(String value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc --> <!-- begin-model-doc --> EPUB 3 -
	 * http://www.idpf.org/epub/301/spec/epub-publications.html#sec-meta-elem
	 *
	 * @since 3.0 <!-- end-model-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMeta_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getId <em>Id</em>}' attribute. <!--
	 * begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @param value
	 *            the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Property</b></em>' attribute. The default value is <code>""</code>. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Property</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc --> <!-- begin-model-doc --> EPUB 3 -
	 * http://www.idpf.org/epub/301/spec/epub-publications.html#sec-meta-elem
	 *
	 * @since 3.0 <!-- end-model-doc -->
	 * @return the value of the '<em>Property</em>' attribute.
	 * @see #setProperty(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMeta_Property()
	 * @model default=""
	 * @generated
	 */
	String getProperty();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getProperty <em>Property</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @param value
	 *            the new value of the '<em>Property</em>' attribute.
	 * @see #getProperty()
	 * @generated
	 */
	void setProperty(String value);

	/**
	 * Returns the value of the '<em><b>Refines</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Refines</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc --> <!-- begin-model-doc --> EPUB 3 -
	 * http://www.idpf.org/epub/301/spec/epub-publications.html#sec-meta-elem
	 *
	 * @since 3.0 <!-- end-model-doc -->
	 * @return the value of the '<em>Refines</em>' attribute.
	 * @see #setRefines(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMeta_Refines()
	 * @model
	 * @generated
	 */
	String getRefines();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getRefines <em>Refines</em>}' attribute. <!--
	 * begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @param value
	 *            the new value of the '<em>Refines</em>' attribute.
	 * @see #getRefines()
	 * @generated
	 */
	void setRefines(String value);

	/**
	 * Returns the value of the '<em><b>Scheme</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Scheme</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc --> <!-- begin-model-doc --> EPUB 3 -
	 * http://www.idpf.org/epub/301/spec/epub-publications.html#sec-meta-elem
	 *
	 * @since 3.0 <!-- end-model-doc -->
	 * @return the value of the '<em>Scheme</em>' attribute.
	 * @see #setScheme(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMeta_Scheme()
	 * @model
	 * @generated
	 */
	String getScheme();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getScheme <em>Scheme</em>}' attribute. <!--
	 * begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @param value
	 *            the new value of the '<em>Scheme</em>' attribute.
	 * @see #getScheme()
	 * @generated
	 */
	void setScheme(String value);

	/**
	 * Returns the value of the '<em><b>Dir</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc --> <!--
	 * begin-model-doc --> EPUB 3 - http://www.idpf.org/epub/301/spec/epub-publications.html#sec-meta-elem
	 *
	 * @since 3.0 <!-- end-model-doc -->
	 * @return the value of the '<em>Dir</em>' attribute.
	 * @see #setDir(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMeta_Dir()
	 * @model
	 * @generated
	 */
	String getDir();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getDir <em>Dir</em>}' attribute. <!--
	 * begin-user-doc -->
	 * 
	 * @since 3.0 <!-- end-user-doc -->
	 * @param value
	 *            the new value of the '<em>Dir</em>' attribute.
	 * @see #getDir()
	 * @generated
	 */
	void setDir(String value);

} // Meta
