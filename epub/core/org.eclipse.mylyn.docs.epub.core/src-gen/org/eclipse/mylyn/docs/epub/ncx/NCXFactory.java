/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage
 * @generated
 */
public interface NCXFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	NCXFactory eINSTANCE = org.eclipse.mylyn.docs.epub.ncx.impl.NCXFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Audio</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Audio</em>'.
	 * @generated
	 */
	Audio createAudio();

	/**
	 * Returns a new object of class '<em>Content</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Content</em>'.
	 * @generated
	 */
	Content createContent();

	/**
	 * Returns a new object of class '<em>Doc Author</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Doc Author</em>'.
	 * @generated
	 */
	DocAuthor createDocAuthor();

	/**
	 * Returns a new object of class '<em>Doc Title</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Doc Title</em>'.
	 * @generated
	 */
	DocTitle createDocTitle();

	/**
	 * Returns a new object of class '<em>Head</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Head</em>'.
	 * @generated
	 */
	Head createHead();

	/**
	 * Returns a new object of class '<em>Img</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Img</em>'.
	 * @generated
	 */
	Img createImg();

	/**
	 * Returns a new object of class '<em>Meta</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Meta</em>'.
	 * @generated
	 */
	Meta createMeta();

	/**
	 * Returns a new object of class '<em>Nav Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Nav Info</em>'.
	 * @generated
	 */
	NavInfo createNavInfo();

	/**
	 * Returns a new object of class '<em>Nav Label</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Nav Label</em>'.
	 * @generated
	 */
	NavLabel createNavLabel();

	/**
	 * Returns a new object of class '<em>Nav List</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Nav List</em>'.
	 * @generated
	 */
	NavList createNavList();

	/**
	 * Returns a new object of class '<em>Nav Map</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Nav Map</em>'.
	 * @generated
	 */
	NavMap createNavMap();

	/**
	 * Returns a new object of class '<em>Nav Point</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Nav Point</em>'.
	 * @generated
	 */
	NavPoint createNavPoint();

	/**
	 * Returns a new object of class '<em>Nav Target</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Nav Target</em>'.
	 * @generated
	 */
	NavTarget createNavTarget();

	/**
	 * Returns a new object of class '<em>Ncx</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Ncx</em>'.
	 * @generated
	 */
	Ncx createNcx();

	/**
	 * Returns a new object of class '<em>Page List</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Page List</em>'.
	 * @generated
	 */
	PageList createPageList();

	/**
	 * Returns a new object of class '<em>Page Target</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Page Target</em>'.
	 * @generated
	 */
	PageTarget createPageTarget();

	/**
	 * Returns a new object of class '<em>Smil Custom Test</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Smil Custom Test</em>'.
	 * @generated
	 */
	SmilCustomTest createSmilCustomTest();

	/**
	 * Returns a new object of class '<em>Text</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Text</em>'.
	 * @generated
	 */
	Text createText();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	NCXPackage getNCXPackage();

} //NCXFactory
