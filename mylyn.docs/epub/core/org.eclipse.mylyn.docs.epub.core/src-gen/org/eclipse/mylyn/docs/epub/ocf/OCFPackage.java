/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ocf;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.docs.epub.ocf.OCFFactory
 * @model kind="package"
 * @generated
 */
public interface OCFPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "ocf";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "urn:oasis:names:tc:opendocument:xmlns:container";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	OCFPackage eINSTANCE = org.eclipse.mylyn.docs.epub.ocf.impl.OCFPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ocf.impl.ContainerImpl <em>Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ocf.impl.ContainerImpl
	 * @see org.eclipse.mylyn.docs.epub.ocf.impl.OCFPackageImpl#getContainer()
	 * @generated
	 */
	int CONTAINER = 0;

	/**
	 * The feature id for the '<em><b>Rootfiles</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTAINER__ROOTFILES = 0;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTAINER__VERSION = 1;

	/**
	 * The number of structural features of the '<em>Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTAINER_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ocf.impl.RootFilesImpl <em>Root Files</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ocf.impl.RootFilesImpl
	 * @see org.eclipse.mylyn.docs.epub.ocf.impl.OCFPackageImpl#getRootFiles()
	 * @generated
	 */
	int ROOT_FILES = 1;

	/**
	 * The feature id for the '<em><b>Rootfiles</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROOT_FILES__ROOTFILES = 0;

	/**
	 * The number of structural features of the '<em>Root Files</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROOT_FILES_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ocf.impl.RootFileImpl <em>Root File</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ocf.impl.RootFileImpl
	 * @see org.eclipse.mylyn.docs.epub.ocf.impl.OCFPackageImpl#getRootFile()
	 * @generated
	 */
	int ROOT_FILE = 2;

	/**
	 * The feature id for the '<em><b>Full Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROOT_FILE__FULL_PATH = 0;

	/**
	 * The feature id for the '<em><b>Media Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROOT_FILE__MEDIA_TYPE = 1;

	/**
	 * The feature id for the '<em><b>Publication</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROOT_FILE__PUBLICATION = 2;

	/**
	 * The number of structural features of the '<em>Root File</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROOT_FILE_FEATURE_COUNT = 3;


	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ocf.Container <em>Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Container</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ocf.Container
	 * @generated
	 */
	EClass getContainer();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ocf.Container#getRootfiles <em>Rootfiles</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Rootfiles</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ocf.Container#getRootfiles()
	 * @see #getContainer()
	 * @generated
	 */
	EReference getContainer_Rootfiles();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ocf.Container#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ocf.Container#getVersion()
	 * @see #getContainer()
	 * @generated
	 */
	EAttribute getContainer_Version();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ocf.RootFiles <em>Root Files</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Root Files</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ocf.RootFiles
	 * @generated
	 */
	EClass getRootFiles();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ocf.RootFiles#getRootfiles <em>Rootfiles</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Rootfiles</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ocf.RootFiles#getRootfiles()
	 * @see #getRootFiles()
	 * @generated
	 */
	EReference getRootFiles_Rootfiles();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ocf.RootFile <em>Root File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Root File</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ocf.RootFile
	 * @generated
	 */
	EClass getRootFile();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ocf.RootFile#getFullPath <em>Full Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Full Path</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ocf.RootFile#getFullPath()
	 * @see #getRootFile()
	 * @generated
	 */
	EAttribute getRootFile_FullPath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ocf.RootFile#getMediaType <em>Media Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Media Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ocf.RootFile#getMediaType()
	 * @see #getRootFile()
	 * @generated
	 */
	EAttribute getRootFile_MediaType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ocf.RootFile#getPublication <em>Publication</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Publication</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ocf.RootFile#getPublication()
	 * @see #getRootFile()
	 * @generated
	 */
	EAttribute getRootFile_Publication();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	OCFFactory getOCFFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ocf.impl.ContainerImpl <em>Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ocf.impl.ContainerImpl
		 * @see org.eclipse.mylyn.docs.epub.ocf.impl.OCFPackageImpl#getContainer()
		 * @generated
		 */
		EClass CONTAINER = eINSTANCE.getContainer();

		/**
		 * The meta object literal for the '<em><b>Rootfiles</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTAINER__ROOTFILES = eINSTANCE.getContainer_Rootfiles();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTAINER__VERSION = eINSTANCE.getContainer_Version();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ocf.impl.RootFilesImpl <em>Root Files</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ocf.impl.RootFilesImpl
		 * @see org.eclipse.mylyn.docs.epub.ocf.impl.OCFPackageImpl#getRootFiles()
		 * @generated
		 */
		EClass ROOT_FILES = eINSTANCE.getRootFiles();

		/**
		 * The meta object literal for the '<em><b>Rootfiles</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ROOT_FILES__ROOTFILES = eINSTANCE.getRootFiles_Rootfiles();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ocf.impl.RootFileImpl <em>Root File</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ocf.impl.RootFileImpl
		 * @see org.eclipse.mylyn.docs.epub.ocf.impl.OCFPackageImpl#getRootFile()
		 * @generated
		 */
		EClass ROOT_FILE = eINSTANCE.getRootFile();

		/**
		 * The meta object literal for the '<em><b>Full Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ROOT_FILE__FULL_PATH = eINSTANCE.getRootFile_FullPath();

		/**
		 * The meta object literal for the '<em><b>Media Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ROOT_FILE__MEDIA_TYPE = eINSTANCE.getRootFile_MediaType();

		/**
		 * The meta object literal for the '<em><b>Publication</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ROOT_FILE__PUBLICATION = eINSTANCE.getRootFile_Publication();

	}

} //OCFPackage
