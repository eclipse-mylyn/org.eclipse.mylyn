/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ocf.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.mylyn.docs.epub.ocf.OCFFactory;
import org.eclipse.mylyn.docs.epub.ocf.OCFPackage;
import org.eclipse.mylyn.docs.epub.ocf.RootFile;
import org.eclipse.mylyn.docs.epub.ocf.RootFiles;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class OCFPackageImpl extends EPackageImpl implements OCFPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass containerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass rootFilesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass rootFileEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.mylyn.docs.epub.ocf.OCFPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private OCFPackageImpl() {
		super(eNS_URI, OCFFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link OCFPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static OCFPackage init() {
		if (isInited) return (OCFPackage)EPackage.Registry.INSTANCE.getEPackage(OCFPackage.eNS_URI);

		// Obtain or create and register package
		OCFPackageImpl theOCFPackage = (OCFPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof OCFPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new OCFPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theOCFPackage.createPackageContents();

		// Initialize created meta-data
		theOCFPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theOCFPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(OCFPackage.eNS_URI, theOCFPackage);
		return theOCFPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getContainer() {
		return containerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getContainer_Rootfiles() {
		return (EReference)containerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContainer_Version() {
		return (EAttribute)containerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRootFiles() {
		return rootFilesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRootFiles_Rootfiles() {
		return (EReference)rootFilesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRootFile() {
		return rootFileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRootFile_FullPath() {
		return (EAttribute)rootFileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRootFile_MediaType() {
		return (EAttribute)rootFileEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRootFile_Publication() {
		return (EAttribute)rootFileEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OCFFactory getOCFFactory() {
		return (OCFFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		containerEClass = createEClass(CONTAINER);
		createEReference(containerEClass, CONTAINER__ROOTFILES);
		createEAttribute(containerEClass, CONTAINER__VERSION);

		rootFilesEClass = createEClass(ROOT_FILES);
		createEReference(rootFilesEClass, ROOT_FILES__ROOTFILES);

		rootFileEClass = createEClass(ROOT_FILE);
		createEAttribute(rootFileEClass, ROOT_FILE__FULL_PATH);
		createEAttribute(rootFileEClass, ROOT_FILE__MEDIA_TYPE);
		createEAttribute(rootFileEClass, ROOT_FILE__PUBLICATION);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(containerEClass, org.eclipse.mylyn.docs.epub.ocf.Container.class, "Container", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getContainer_Rootfiles(), this.getRootFiles(), null, "rootfiles", null, 1, 1, org.eclipse.mylyn.docs.epub.ocf.Container.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContainer_Version(), ecorePackage.getEString(), "version", null, 0, 1, org.eclipse.mylyn.docs.epub.ocf.Container.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(rootFilesEClass, RootFiles.class, "RootFiles", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRootFiles_Rootfiles(), this.getRootFile(), null, "rootfiles", null, 1, -1, RootFiles.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(rootFileEClass, RootFile.class, "RootFile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRootFile_FullPath(), ecorePackage.getEString(), "fullPath", null, 1, 1, RootFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRootFile_MediaType(), ecorePackage.getEString(), "mediaType", null, 1, 1, RootFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRootFile_Publication(), ecorePackage.getEJavaObject(), "publication", null, 0, 1, RootFile.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";	
		addAnnotation
		  (containerEClass, 
		   source, 
		   new String[] {
			 "name", "container",
			 "namespace", "urn:oasis:names:tc:opendocument:xmlns:container"
		   });	
		addAnnotation
		  (getContainer_Rootfiles(), 
		   source, 
		   new String[] {
			 "name", "rootfiles",
			 "namespace", "urn:oasis:names:tc:opendocument:xmlns:container"
		   });	
		addAnnotation
		  (getRootFiles_Rootfiles(), 
		   source, 
		   new String[] {
			 "name", "rootfile",
			 "namespace", "urn:oasis:names:tc:opendocument:xmlns:container"
		   });	
		addAnnotation
		  (getRootFile_FullPath(), 
		   source, 
		   new String[] {
			 "name", "full-path"
		   });	
		addAnnotation
		  (getRootFile_MediaType(), 
		   source, 
		   new String[] {
			 "name", "media-type"
		   });
	}

} //OCFPackageImpl
