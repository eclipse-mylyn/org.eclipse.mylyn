/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.mylyn.docs.epub.dc.DCPackage;
import org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl;
import org.eclipse.mylyn.docs.epub.opf.Guide;
import org.eclipse.mylyn.docs.epub.opf.Item;
import org.eclipse.mylyn.docs.epub.opf.Itemref;
import org.eclipse.mylyn.docs.epub.opf.Manifest;
import org.eclipse.mylyn.docs.epub.opf.Meta;
import org.eclipse.mylyn.docs.epub.opf.Metadata;
import org.eclipse.mylyn.docs.epub.opf.OPFFactory;
import org.eclipse.mylyn.docs.epub.opf.OPFPackage;
import org.eclipse.mylyn.docs.epub.opf.Reference;
import org.eclipse.mylyn.docs.epub.opf.Role;
import org.eclipse.mylyn.docs.epub.opf.Spine;
import org.eclipse.mylyn.docs.epub.opf.Tours;
import org.eclipse.mylyn.docs.epub.opf.Type;
import org.eclipse.mylyn.docs.epub.opf.util.OPFValidator;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class OPFPackageImpl extends EPackageImpl implements OPFPackage {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass packageEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass manifestEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass itemEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass spineEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass guideEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass referenceEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass itemrefEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass toursEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metaEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum roleEEnum = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum typeEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
	 * EPackage.Registry} by the package package URI value.
	 * <p>
	 * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
	 * performs initialization of the package, or returns the registered package, if one already exists. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private OPFPackageImpl() {
		super(eNS_URI, OPFFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link OPFPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static OPFPackage init() {
		if (isInited) return (OPFPackage)EPackage.Registry.INSTANCE.getEPackage(OPFPackage.eNS_URI);

		// Obtain or create and register package
		OPFPackageImpl theOPFPackage = (OPFPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof OPFPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new OPFPackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		DCPackageImpl theDCPackage = (DCPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DCPackage.eNS_URI) instanceof DCPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DCPackage.eNS_URI) : DCPackage.eINSTANCE);

		// Create package meta-data objects
		theOPFPackage.createPackageContents();
		theDCPackage.createPackageContents();

		// Initialize created meta-data
		theOPFPackage.initializePackageContents();
		theDCPackage.initializePackageContents();

		// Register package validator
		EValidator.Registry.INSTANCE.put
			(theOPFPackage, 
			 new EValidator.Descriptor() {
				 public EValidator getEValidator() {
					 return OPFValidator.INSTANCE;
				 }
			 });

		// Mark meta-data to indicate it can't be changed
		theOPFPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(OPFPackage.eNS_URI, theOPFPackage);
		return theOPFPackage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPackage() {
		return packageEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPackage_Metadata() {
		return (EReference)packageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPackage_Manifest() {
		return (EReference)packageEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPackage_Spine() {
		return (EReference)packageEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPackage_Guide() {
		return (EReference)packageEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPackage_Tours() {
		return (EReference)packageEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPackage_Version() {
		return (EAttribute)packageEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPackage_UniqueIdentifier() {
		return (EAttribute)packageEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPackage_GenerateCoverHTML() {
		return (EAttribute)packageEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPackage_GenerateTableOfContents() {
		return (EAttribute)packageEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPackage_IncludeReferencedResources() {
		return (EAttribute)packageEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPackage_Prefix() {
		return (EAttribute)packageEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPackage_Lang() {
		return (EAttribute)packageEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPackage_Dir() {
		return (EAttribute)packageEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPackage_Id() {
		return (EAttribute)packageEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMetadata() {
		return metadataEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Titles() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Creators() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Subjects() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Descriptions() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Publishers() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Contributors() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Dates() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Types() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Formats() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Identifiers() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Sources() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Languages() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Relations() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Coverages() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Rights() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMetadata_Metas() {
		return (EReference)metadataEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getManifest() {
		return manifestEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getManifest_Items() {
		return (EReference)manifestEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getItem() {
		return itemEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Id() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Href() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Media_type() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Fallback() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Fallback_style() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Required_namespace() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Required_modules() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_File() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_NoToc() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Title() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Generated() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_SourcePath() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Properties() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItem_Media_overlay() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSpine() {
		return spineEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSpine_SpineItems() {
		return (EReference)spineEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSpine_Toc() {
		return (EAttribute)spineEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGuide() {
		return guideEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGuide_GuideItems() {
		return (EReference)guideEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getReference() {
		return referenceEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getReference_Type() {
		return (EAttribute)referenceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getReference_Title() {
		return (EAttribute)referenceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getReference_Href() {
		return (EAttribute)referenceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getItemref() {
		return itemrefEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItemref_Idref() {
		return (EAttribute)itemrefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getItemref_Linear() {
		return (EAttribute)itemrefEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTours() {
		return toursEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMeta() {
		return metaEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeta_Name() {
		return (EAttribute)metaEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeta_Content() {
		return (EAttribute)metaEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeta_Id() {
		return (EAttribute)metaEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeta_Property() {
		return (EAttribute)metaEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeta_Refines() {
		return (EAttribute)metaEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * 
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeta_Scheme() {
		return (EAttribute)metaEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * 
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeta_Dir() {
		return (EAttribute)metaEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getRole() {
		return roleEEnum;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getType() {
		return typeEEnum;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public OPFFactory getOPFFactory() {
		return (OPFFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		packageEClass = createEClass(PACKAGE);
		createEReference(packageEClass, PACKAGE__METADATA);
		createEReference(packageEClass, PACKAGE__MANIFEST);
		createEReference(packageEClass, PACKAGE__SPINE);
		createEReference(packageEClass, PACKAGE__GUIDE);
		createEReference(packageEClass, PACKAGE__TOURS);
		createEAttribute(packageEClass, PACKAGE__VERSION);
		createEAttribute(packageEClass, PACKAGE__UNIQUE_IDENTIFIER);
		createEAttribute(packageEClass, PACKAGE__GENERATE_COVER_HTML);
		createEAttribute(packageEClass, PACKAGE__GENERATE_TABLE_OF_CONTENTS);
		createEAttribute(packageEClass, PACKAGE__INCLUDE_REFERENCED_RESOURCES);
		createEAttribute(packageEClass, PACKAGE__PREFIX);
		createEAttribute(packageEClass, PACKAGE__LANG);
		createEAttribute(packageEClass, PACKAGE__DIR);
		createEAttribute(packageEClass, PACKAGE__ID);

		metadataEClass = createEClass(METADATA);
		createEReference(metadataEClass, METADATA__TITLES);
		createEReference(metadataEClass, METADATA__CREATORS);
		createEReference(metadataEClass, METADATA__SUBJECTS);
		createEReference(metadataEClass, METADATA__DESCRIPTIONS);
		createEReference(metadataEClass, METADATA__PUBLISHERS);
		createEReference(metadataEClass, METADATA__CONTRIBUTORS);
		createEReference(metadataEClass, METADATA__DATES);
		createEReference(metadataEClass, METADATA__TYPES);
		createEReference(metadataEClass, METADATA__FORMATS);
		createEReference(metadataEClass, METADATA__IDENTIFIERS);
		createEReference(metadataEClass, METADATA__SOURCES);
		createEReference(metadataEClass, METADATA__LANGUAGES);
		createEReference(metadataEClass, METADATA__RELATIONS);
		createEReference(metadataEClass, METADATA__COVERAGES);
		createEReference(metadataEClass, METADATA__RIGHTS);
		createEReference(metadataEClass, METADATA__METAS);

		manifestEClass = createEClass(MANIFEST);
		createEReference(manifestEClass, MANIFEST__ITEMS);

		itemEClass = createEClass(ITEM);
		createEAttribute(itemEClass, ITEM__ID);
		createEAttribute(itemEClass, ITEM__HREF);
		createEAttribute(itemEClass, ITEM__MEDIA_TYPE);
		createEAttribute(itemEClass, ITEM__FALLBACK);
		createEAttribute(itemEClass, ITEM__FALLBACK_STYLE);
		createEAttribute(itemEClass, ITEM__REQUIRED_NAMESPACE);
		createEAttribute(itemEClass, ITEM__REQUIRED_MODULES);
		createEAttribute(itemEClass, ITEM__FILE);
		createEAttribute(itemEClass, ITEM__NO_TOC);
		createEAttribute(itemEClass, ITEM__TITLE);
		createEAttribute(itemEClass, ITEM__GENERATED);
		createEAttribute(itemEClass, ITEM__SOURCE_PATH);
		createEAttribute(itemEClass, ITEM__PROPERTIES);
		createEAttribute(itemEClass, ITEM__MEDIA_OVERLAY);

		spineEClass = createEClass(SPINE);
		createEReference(spineEClass, SPINE__SPINE_ITEMS);
		createEAttribute(spineEClass, SPINE__TOC);

		guideEClass = createEClass(GUIDE);
		createEReference(guideEClass, GUIDE__GUIDE_ITEMS);

		referenceEClass = createEClass(REFERENCE);
		createEAttribute(referenceEClass, REFERENCE__TYPE);
		createEAttribute(referenceEClass, REFERENCE__TITLE);
		createEAttribute(referenceEClass, REFERENCE__HREF);

		itemrefEClass = createEClass(ITEMREF);
		createEAttribute(itemrefEClass, ITEMREF__IDREF);
		createEAttribute(itemrefEClass, ITEMREF__LINEAR);

		toursEClass = createEClass(TOURS);

		metaEClass = createEClass(META);
		createEAttribute(metaEClass, META__NAME);
		createEAttribute(metaEClass, META__CONTENT);
		createEAttribute(metaEClass, META__ID);
		createEAttribute(metaEClass, META__PROPERTY);
		createEAttribute(metaEClass, META__REFINES);
		createEAttribute(metaEClass, META__SCHEME);
		createEAttribute(metaEClass, META__DIR);

		// Create enums
		roleEEnum = createEEnum(ROLE);
		typeEEnum = createEEnum(TYPE);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		DCPackage theDCPackage = (DCPackage)EPackage.Registry.INSTANCE.getEPackage(DCPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(packageEClass, org.eclipse.mylyn.docs.epub.opf.Package.class, "Package", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getPackage_Metadata(), this.getMetadata(), null, "metadata", null, 1, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getPackage_Manifest(), this.getManifest(), null, "manifest", null, 1, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getPackage_Spine(), this.getSpine(), null, "spine", null, 1, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getPackage_Guide(), this.getGuide(), null, "guide", null, 0, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getPackage_Tours(), this.getTours(), null, "tours", null, 0, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getPackage_Version(), ecorePackage.getEString(), "version", "2.0", 1, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getPackage_UniqueIdentifier(), ecorePackage.getEString(), "uniqueIdentifier", null, 1, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getPackage_GenerateCoverHTML(), ecorePackage.getEBoolean(), "generateCoverHTML", null, 0, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getPackage_GenerateTableOfContents(), ecorePackage.getEBoolean(), "generateTableOfContents", null, 0, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getPackage_IncludeReferencedResources(), ecorePackage.getEBoolean(), "includeReferencedResources", null, 0, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getPackage_Prefix(), ecorePackage.getEString(), "prefix", null, 0, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getPackage_Lang(), ecorePackage.getEString(), "lang", null, 0, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getPackage_Dir(), ecorePackage.getEString(), "dir", null, 0, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getPackage_Id(), ecorePackage.getEString(), "id", null, 0, 1, org.eclipse.mylyn.docs.epub.opf.Package.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(metadataEClass, Metadata.class, "Metadata", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getMetadata_Titles(), theDCPackage.getTitle(), null, "titles", null, 1, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Creators(), theDCPackage.getCreator(), null, "creators", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Subjects(), theDCPackage.getSubject(), null, "subjects", null, 1, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Descriptions(), theDCPackage.getDescription(), null, "descriptions", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Publishers(), theDCPackage.getPublisher(), null, "publishers", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Contributors(), theDCPackage.getContributor(), null, "contributors", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Dates(), theDCPackage.getDate(), null, "dates", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Types(), theDCPackage.getType(), null, "types", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Formats(), theDCPackage.getFormat(), null, "formats", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Identifiers(), theDCPackage.getIdentifier(), null, "identifiers", null, 1, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Sources(), theDCPackage.getSource(), null, "sources", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Languages(), theDCPackage.getLanguage(), null, "languages", null, 1, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Relations(), theDCPackage.getRelation(), null, "relations", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Coverages(), theDCPackage.getCoverage(), null, "coverages", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Rights(), theDCPackage.getRights(), null, "rights", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getMetadata_Metas(), this.getMeta(), null, "metas", null, 0, -1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(manifestEClass, Manifest.class, "Manifest", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getManifest_Items(), this.getItem(), null, "items", null, 1, -1, Manifest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(itemEClass, Item.class, "Item", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getItem_Id(), ecorePackage.getEString(), "id", null, 1, 1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_Href(), ecorePackage.getEString(), "href", null, 1, 1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_Media_type(), ecorePackage.getEString(), "media_type", null, 1, 1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_Fallback(), ecorePackage.getEString(), "fallback", null, 0, 1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_Fallback_style(), ecorePackage.getEString(), "fallback_style", null, 0, 1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_Required_namespace(), ecorePackage.getEString(), "required_namespace", null, 0, 1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_Required_modules(), ecorePackage.getEString(), "required_modules", null, 0, 1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_File(), ecorePackage.getEString(), "file", null, 0, 1, Item.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_NoToc(), ecorePackage.getEBoolean(), "noToc", null, 0, 1, Item.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_Title(), ecorePackage.getEString(), "title", null, 0, 1, Item.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_Generated(), ecorePackage.getEBoolean(), "generated", null, 0, 1, Item.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_SourcePath(), ecorePackage.getEString(), "sourcePath", null, 0, 1, Item.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_Properties(), ecorePackage.getEString(), "properties", null, 0, 1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItem_Media_overlay(), ecorePackage.getEString(), "media_overlay", null, 0, 1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(spineEClass, Spine.class, "Spine", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getSpine_SpineItems(), this.getItemref(), null, "spineItems", null, 0, -1, Spine.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getSpine_Toc(), ecorePackage.getEString(), "toc", null, 1, 1, Spine.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(guideEClass, Guide.class, "Guide", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getGuide_GuideItems(), this.getReference(), null, "guideItems", null, 0, -1, Guide.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(referenceEClass, Reference.class, "Reference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getReference_Type(), ecorePackage.getEString(), "type", null, 1, 1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getReference_Title(), ecorePackage.getEString(), "title", null, 1, 1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getReference_Href(), ecorePackage.getEString(), "href", null, 1, 1, Reference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(itemrefEClass, Itemref.class, "Itemref", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getItemref_Idref(), ecorePackage.getEString(), "idref", null, 1, 1, Itemref.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getItemref_Linear(), ecorePackage.getEString(), "linear", null, 0, 1, Itemref.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(toursEClass, Tours.class, "Tours", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(metaEClass, Meta.class, "Meta", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getMeta_Name(), ecorePackage.getEString(), "name", null, 0, 1, Meta.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getMeta_Content(), ecorePackage.getEString(), "content", null, 0, 1, Meta.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getMeta_Id(), ecorePackage.getEString(), "id", null, 0, 1, Meta.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getMeta_Property(), ecorePackage.getEString(), "property", "", 0, 1, Meta.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getMeta_Refines(), ecorePackage.getEString(), "refines", null, 0, 1, Meta.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getMeta_Scheme(), ecorePackage.getEString(), "scheme", null, 0, 1, Meta.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getMeta_Dir(), ecorePackage.getEString(), "dir", null, 0, 1, Meta.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		// Initialize enums and add enum literals
		initEEnum(roleEEnum, Role.class, "Role"); //$NON-NLS-1$
		addEEnumLiteral(roleEEnum, Role.ART_COPYIST);
		addEEnumLiteral(roleEEnum, Role.ACTOR);
		addEEnumLiteral(roleEEnum, Role.ADAPTER);
		addEEnumLiteral(roleEEnum, Role.AUTHOR_OF_AFTERWORD_COLOPHON_ETC);
		addEEnumLiteral(roleEEnum, Role.ANALYST);
		addEEnumLiteral(roleEEnum, Role.ANIMATOR);
		addEEnumLiteral(roleEEnum, Role.ANNOTATOR);
		addEEnumLiteral(roleEEnum, Role.BIBLIOGRAPHIC_ANTECEDENT);
		addEEnumLiteral(roleEEnum, Role.APPLICANT);
		addEEnumLiteral(roleEEnum, Role.AUTHOR_IN_QUOTATIONS_OR_TEXT_ABSTRACTS);
		addEEnumLiteral(roleEEnum, Role.ARCHITECT);
		addEEnumLiteral(roleEEnum, Role.ARTISTIC_DIRECTOR);
		addEEnumLiteral(roleEEnum, Role.ARRANGER);
		addEEnumLiteral(roleEEnum, Role.ARTIST);
		addEEnumLiteral(roleEEnum, Role.ASSIGNEE);
		addEEnumLiteral(roleEEnum, Role.ASSOCIATED_NAME);
		addEEnumLiteral(roleEEnum, Role.ATTRIBUTED_NAME);
		addEEnumLiteral(roleEEnum, Role.AUCTIONEER);
		addEEnumLiteral(roleEEnum, Role.AUTHOR_OF_DIALOG);
		addEEnumLiteral(roleEEnum, Role.AUTHOR_OF_INTRODUCTION);
		addEEnumLiteral(roleEEnum, Role.AUTHOR_OF_SCREENPLAY);
		addEEnumLiteral(roleEEnum, Role.AUTHOR);
		addEEnumLiteral(roleEEnum, Role.BINDING_DESIGNER);
		addEEnumLiteral(roleEEnum, Role.BOOKJACKET_DESIGNER);
		addEEnumLiteral(roleEEnum, Role.BOOK_DESIGNER);
		addEEnumLiteral(roleEEnum, Role.BOOK_PRODUCER);
		addEEnumLiteral(roleEEnum, Role.BLURB_WRITER);
		addEEnumLiteral(roleEEnum, Role.BINDER);
		addEEnumLiteral(roleEEnum, Role.BOOKPLATE_DESIGNER);
		addEEnumLiteral(roleEEnum, Role.BOOKSELLER);
		addEEnumLiteral(roleEEnum, Role.CONCEPTOR);
		addEEnumLiteral(roleEEnum, Role.CHOREOGRAPHER);
		addEEnumLiteral(roleEEnum, Role.COLLABORATOR);
		addEEnumLiteral(roleEEnum, Role.CLIENT);
		addEEnumLiteral(roleEEnum, Role.CALLIGRAPHER);
		addEEnumLiteral(roleEEnum, Role.COLORIST);
		addEEnumLiteral(roleEEnum, Role.COLLOTYPER);
		addEEnumLiteral(roleEEnum, Role.COMMENTATOR);
		addEEnumLiteral(roleEEnum, Role.COMPOSER);
		addEEnumLiteral(roleEEnum, Role.COMPOSITOR);
		addEEnumLiteral(roleEEnum, Role.CINEMATOGRAPHER);
		addEEnumLiteral(roleEEnum, Role.CONDUCTOR);
		addEEnumLiteral(roleEEnum, Role.CENSOR);
		addEEnumLiteral(roleEEnum, Role.CONTESTANT_APPELLEE);
		addEEnumLiteral(roleEEnum, Role.COLLECTOR);
		addEEnumLiteral(roleEEnum, Role.COMPILER);
		addEEnumLiteral(roleEEnum, Role.CONSERVATOR);
		addEEnumLiteral(roleEEnum, Role.CONTESTANT);
		addEEnumLiteral(roleEEnum, Role.CONTESTANT_APPELLANT);
		addEEnumLiteral(roleEEnum, Role.COVER_DESIGNER);
		addEEnumLiteral(roleEEnum, Role.COPYRIGHT_CLAIMANT);
		addEEnumLiteral(roleEEnum, Role.COMPLAINANT_APPELLEE);
		addEEnumLiteral(roleEEnum, Role.COPYRIGHT_HOLDER);
		addEEnumLiteral(roleEEnum, Role.COMPLAINANT);
		addEEnumLiteral(roleEEnum, Role.COMPLAINANT_APPELLANT);
		addEEnumLiteral(roleEEnum, Role.CREATOR);
		addEEnumLiteral(roleEEnum, Role.CORRESPONDENT);
		addEEnumLiteral(roleEEnum, Role.CORRECTOR);
		addEEnumLiteral(roleEEnum, Role.CONSULTANT);
		addEEnumLiteral(roleEEnum, Role.CONSULTANT_TO_APROJECT);
		addEEnumLiteral(roleEEnum, Role.COSTUME_DESIGNER);
		addEEnumLiteral(roleEEnum, Role.CONTRIBUTOR);
		addEEnumLiteral(roleEEnum, Role.CONTESTEE_APPELLEE);
		addEEnumLiteral(roleEEnum, Role.CARTOGRAPHER);
		addEEnumLiteral(roleEEnum, Role.CONTRACTOR);
		addEEnumLiteral(roleEEnum, Role.CONTESTEE);
		addEEnumLiteral(roleEEnum, Role.CONTESTEE_APPELLANT);
		addEEnumLiteral(roleEEnum, Role.CURATOR);
		addEEnumLiteral(roleEEnum, Role.COMMENTATOR_FOR_WRITTEN_TEXT);
		addEEnumLiteral(roleEEnum, Role.DEFENDANT);
		addEEnumLiteral(roleEEnum, Role.DEFENDANT_APPELLEE);
		addEEnumLiteral(roleEEnum, Role.DEFENDANT_APPELLANT);
		addEEnumLiteral(roleEEnum, Role.DEGREE_GRANTOR);
		addEEnumLiteral(roleEEnum, Role.DISSERTANT);
		addEEnumLiteral(roleEEnum, Role.DELINEATOR);
		addEEnumLiteral(roleEEnum, Role.DANCER);
		addEEnumLiteral(roleEEnum, Role.DONOR);
		addEEnumLiteral(roleEEnum, Role.DISTRIBUTION_PLACE);
		addEEnumLiteral(roleEEnum, Role.DEPICTED);
		addEEnumLiteral(roleEEnum, Role.DEPOSITOR);
		addEEnumLiteral(roleEEnum, Role.DRAFTSMAN);
		addEEnumLiteral(roleEEnum, Role.DIRECTOR);
		addEEnumLiteral(roleEEnum, Role.DESIGNER);
		addEEnumLiteral(roleEEnum, Role.DISTRIBUTOR);
		addEEnumLiteral(roleEEnum, Role.DATA_CONTRIBUTOR);
		addEEnumLiteral(roleEEnum, Role.DEDICATEE);
		addEEnumLiteral(roleEEnum, Role.DATA_MANAGER);
		addEEnumLiteral(roleEEnum, Role.DEDICATOR);
		addEEnumLiteral(roleEEnum, Role.DUBIOUS_AUTHOR);
		addEEnumLiteral(roleEEnum, Role.EDITOR);
		addEEnumLiteral(roleEEnum, Role.ENGRAVER);
		addEEnumLiteral(roleEEnum, Role.ELECTRICIAN);
		addEEnumLiteral(roleEEnum, Role.ELECTROTYPER);
		addEEnumLiteral(roleEEnum, Role.ENGINEER);
		addEEnumLiteral(roleEEnum, Role.ETCHER);
		addEEnumLiteral(roleEEnum, Role.EVENT_PLACE);
		addEEnumLiteral(roleEEnum, Role.EXPERT);
		addEEnumLiteral(roleEEnum, Role.FACSIMILIST);
		addEEnumLiteral(roleEEnum, Role.FIELD_DIRECTOR);
		addEEnumLiteral(roleEEnum, Role.FILM_EDITOR);
		addEEnumLiteral(roleEEnum, Role.FORMER_OWNER);
		addEEnumLiteral(roleEEnum, Role.FIRST_PARTY);
		addEEnumLiteral(roleEEnum, Role.FUNDER);
		addEEnumLiteral(roleEEnum, Role.FORGER);
		addEEnumLiteral(roleEEnum, Role.GEOGRAPHIC_INFORMATION_SPECIALIST);
		addEEnumLiteral(roleEEnum, Role.GRAPHIC_TECHNICIAN);
		addEEnumLiteral(roleEEnum, Role.HONOREE);
		addEEnumLiteral(roleEEnum, Role.HOST);
		addEEnumLiteral(roleEEnum, Role.ILLUSTRATOR);
		addEEnumLiteral(roleEEnum, Role.ILLUMINATOR);
		addEEnumLiteral(roleEEnum, Role.INSCRIBER);
		addEEnumLiteral(roleEEnum, Role.INVENTOR);
		addEEnumLiteral(roleEEnum, Role.INSTRUMENTALIST);
		addEEnumLiteral(roleEEnum, Role.INTERVIEWEE);
		addEEnumLiteral(roleEEnum, Role.INTERVIEWER);
		addEEnumLiteral(roleEEnum, Role.LABORATORY);
		addEEnumLiteral(roleEEnum, Role.LIBRETTIST);
		addEEnumLiteral(roleEEnum, Role.LABORATORY_DIRECTOR);
		addEEnumLiteral(roleEEnum, Role.LEAD);
		addEEnumLiteral(roleEEnum, Role.LIBELEE_APPELLEE);
		addEEnumLiteral(roleEEnum, Role.LIBELEE);
		addEEnumLiteral(roleEEnum, Role.LENDER);
		addEEnumLiteral(roleEEnum, Role.LIBELEE_APPELLANT);
		addEEnumLiteral(roleEEnum, Role.LIGHTING_DESIGNER);
		addEEnumLiteral(roleEEnum, Role.LIBELANT_APPELLEE);
		addEEnumLiteral(roleEEnum, Role.LIBELANT);
		addEEnumLiteral(roleEEnum, Role.LIBELANT_APPELLANT);
		addEEnumLiteral(roleEEnum, Role.LANDSCAPE_ARCHITECT);
		addEEnumLiteral(roleEEnum, Role.LICENSEE);
		addEEnumLiteral(roleEEnum, Role.LICENSOR);
		addEEnumLiteral(roleEEnum, Role.LITHOGRAPHER);
		addEEnumLiteral(roleEEnum, Role.LYRICIST);
		addEEnumLiteral(roleEEnum, Role.MUSIC_COPYIST);
		addEEnumLiteral(roleEEnum, Role.MANUFACTURE_PLACE);
		addEEnumLiteral(roleEEnum, Role.MANUFACTURER);
		addEEnumLiteral(roleEEnum, Role.METADATA_CONTACT);
		addEEnumLiteral(roleEEnum, Role.MODERATOR);
		addEEnumLiteral(roleEEnum, Role.MONITOR);
		addEEnumLiteral(roleEEnum, Role.MARBLER);
		addEEnumLiteral(roleEEnum, Role.MARKUP_EDITOR);
		addEEnumLiteral(roleEEnum, Role.MUSICAL_DIRECTOR);
		addEEnumLiteral(roleEEnum, Role.METAL_ENGRAVER);
		addEEnumLiteral(roleEEnum, Role.MUSICIAN);
		addEEnumLiteral(roleEEnum, Role.NARRATOR);
		addEEnumLiteral(roleEEnum, Role.OPPONENT);
		addEEnumLiteral(roleEEnum, Role.ORIGINATOR);
		addEEnumLiteral(roleEEnum, Role.ORGANIZER_OF_MEETING);
		addEEnumLiteral(roleEEnum, Role.OTHER);
		addEEnumLiteral(roleEEnum, Role.OWNER);
		addEEnumLiteral(roleEEnum, Role.PATRON);
		addEEnumLiteral(roleEEnum, Role.PUBLISHING_DIRECTOR);
		addEEnumLiteral(roleEEnum, Role.PUBLISHER);
		addEEnumLiteral(roleEEnum, Role.PROJECT_DIRECTOR);
		addEEnumLiteral(roleEEnum, Role.PROOFREADER);
		addEEnumLiteral(roleEEnum, Role.PHOTOGRAPHER);
		addEEnumLiteral(roleEEnum, Role.PLATEMAKER);
		addEEnumLiteral(roleEEnum, Role.PERMITTING_AGENCY);
		addEEnumLiteral(roleEEnum, Role.PRODUCTION_MANAGER);
		addEEnumLiteral(roleEEnum, Role.PRINTER_OF_PLATES);
		addEEnumLiteral(roleEEnum, Role.PAPERMAKER);
		addEEnumLiteral(roleEEnum, Role.PUPPETEER);
		addEEnumLiteral(roleEEnum, Role.PROCESS_CONTACT);
		addEEnumLiteral(roleEEnum, Role.PRODUCTION_PERSONNEL);
		addEEnumLiteral(roleEEnum, Role.PERFORMER);
		addEEnumLiteral(roleEEnum, Role.PROGRAMMER);
		addEEnumLiteral(roleEEnum, Role.PRINTMAKER);
		addEEnumLiteral(roleEEnum, Role.PRODUCER);
		addEEnumLiteral(roleEEnum, Role.PRODUCTION_PLACE);
		addEEnumLiteral(roleEEnum, Role.PRINTER);
		addEEnumLiteral(roleEEnum, Role.PATENT_APPLICANT);
		addEEnumLiteral(roleEEnum, Role.PLAINTIFF_APPELLEE);
		addEEnumLiteral(roleEEnum, Role.PLAINTIFF);
		addEEnumLiteral(roleEEnum, Role.PATENT_HOLDER);
		addEEnumLiteral(roleEEnum, Role.PLAINTIFF_APPELLANT);
		addEEnumLiteral(roleEEnum, Role.PUBLICATION_PLACE);
		addEEnumLiteral(roleEEnum, Role.RUBRICATOR);
		addEEnumLiteral(roleEEnum, Role.RECORDING_ENGINEER);
		addEEnumLiteral(roleEEnum, Role.RECIPIENT);
		addEEnumLiteral(roleEEnum, Role.REDACTOR);
		addEEnumLiteral(roleEEnum, Role.RENDERER);
		addEEnumLiteral(roleEEnum, Role.RESEARCHER);
		addEEnumLiteral(roleEEnum, Role.REVIEWER);
		addEEnumLiteral(roleEEnum, Role.REPOSITORY);
		addEEnumLiteral(roleEEnum, Role.REPORTER);
		addEEnumLiteral(roleEEnum, Role.RESPONSIBLE_PARTY);
		addEEnumLiteral(roleEEnum, Role.RESPONDENT_APPELLEE);
		addEEnumLiteral(roleEEnum, Role.RESTAGER);
		addEEnumLiteral(roleEEnum, Role.RESPONDENT);
		addEEnumLiteral(roleEEnum, Role.RESPONDENT_APPELLANT);
		addEEnumLiteral(roleEEnum, Role.RESEARCH_TEAM_HEAD);
		addEEnumLiteral(roleEEnum, Role.RESEARCH_TEAM_MEMBER);
		addEEnumLiteral(roleEEnum, Role.SCIENTIFIC_ADVISOR);
		addEEnumLiteral(roleEEnum, Role.SCENARIST);
		addEEnumLiteral(roleEEnum, Role.SCULPTOR);
		addEEnumLiteral(roleEEnum, Role.SCRIBE);
		addEEnumLiteral(roleEEnum, Role.SOUND_DESIGNER);
		addEEnumLiteral(roleEEnum, Role.SECRETARY);
		addEEnumLiteral(roleEEnum, Role.SIGNER);
		addEEnumLiteral(roleEEnum, Role.SUPPORTING_HOST);
		addEEnumLiteral(roleEEnum, Role.SINGER);
		addEEnumLiteral(roleEEnum, Role.SPEAKER);
		addEEnumLiteral(roleEEnum, Role.SPONSOR);
		addEEnumLiteral(roleEEnum, Role.SECOND_PARTY);
		addEEnumLiteral(roleEEnum, Role.SURVEYOR);
		addEEnumLiteral(roleEEnum, Role.SET_DESIGNER);
		addEEnumLiteral(roleEEnum, Role.STORYTELLER);
		addEEnumLiteral(roleEEnum, Role.STAGE_MANAGER);
		addEEnumLiteral(roleEEnum, Role.STANDARDS_BODY);
		addEEnumLiteral(roleEEnum, Role.STEREOTYPER);
		addEEnumLiteral(roleEEnum, Role.TECHNICAL_DIRECTOR);
		addEEnumLiteral(roleEEnum, Role.TEACHER);
		addEEnumLiteral(roleEEnum, Role.THESIS_ADVISOR);
		addEEnumLiteral(roleEEnum, Role.TRANSCRIBER);
		addEEnumLiteral(roleEEnum, Role.TRANSLATOR);
		addEEnumLiteral(roleEEnum, Role.TYPE_DESIGNER);
		addEEnumLiteral(roleEEnum, Role.TYPOGRAPHER);
		addEEnumLiteral(roleEEnum, Role.UNIVERSITY_PLACE);
		addEEnumLiteral(roleEEnum, Role.VIDEOGRAPHER);
		addEEnumLiteral(roleEEnum, Role.VOCALIST);
		addEEnumLiteral(roleEEnum, Role.WRITER_OF_ACCOMPANYING_MATERIAL);
		addEEnumLiteral(roleEEnum, Role.WOODCUTTER);
		addEEnumLiteral(roleEEnum, Role.WOOD_ENGRAVER);
		addEEnumLiteral(roleEEnum, Role.WITNESS);

		initEEnum(typeEEnum, Type.class, "Type"); //$NON-NLS-1$
		addEEnumLiteral(typeEEnum, Type.COVER);
		addEEnumLiteral(typeEEnum, Type.TITLE);
		addEEnumLiteral(typeEEnum, Type.TOC);
		addEEnumLiteral(typeEEnum, Type.INDEX);
		addEEnumLiteral(typeEEnum, Type.GLOSSARY);
		addEEnumLiteral(typeEEnum, Type.ACKNOWLEDGEMENTS);
		addEEnumLiteral(typeEEnum, Type.BIBLIOGRAPHY);
		addEEnumLiteral(typeEEnum, Type.COLOPHON);
		addEEnumLiteral(typeEEnum, Type.COPYRIGHT);
		addEEnumLiteral(typeEEnum, Type.DEDICATION);
		addEEnumLiteral(typeEEnum, Type.EPIGRAPH);
		addEEnumLiteral(typeEEnum, Type.FOREWORD);
		addEEnumLiteral(typeEEnum, Type.ILLUSTRATIONS);
		addEEnumLiteral(typeEEnum, Type.TABLES);
		addEEnumLiteral(typeEEnum, Type.NOTES);
		addEEnumLiteral(typeEEnum, Type.PREFACE);
		addEEnumLiteral(typeEEnum, Type.TEXT);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
		// http://www.eclipse.org/emf/2002/Ecore
		createEcoreAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData"; //$NON-NLS-1$	
		addAnnotation
		  (packageEClass, 
		   source, 
		   new String[] {
			 "name", "package", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getPackage_Metadata(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getPackage_Manifest(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getPackage_Spine(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getPackage_Guide(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getPackage_Tours(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getPackage_UniqueIdentifier(), 
		   source, 
		   new String[] {
			 "name", "unique-identifier" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Titles(), 
		   source, 
		   new String[] {
			 "name", "title", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Creators(), 
		   source, 
		   new String[] {
			 "name", "creator", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Subjects(), 
		   source, 
		   new String[] {
			 "name", "subject", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Descriptions(), 
		   source, 
		   new String[] {
			 "name", "description", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Publishers(), 
		   source, 
		   new String[] {
			 "name", "publisher", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Contributors(), 
		   source, 
		   new String[] {
			 "name", "contributor", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Dates(), 
		   source, 
		   new String[] {
			 "name", "date", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Types(), 
		   source, 
		   new String[] {
			 "name", "type", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Formats(), 
		   source, 
		   new String[] {
			 "name", "format", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Identifiers(), 
		   source, 
		   new String[] {
			 "name", "identifier", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Sources(), 
		   source, 
		   new String[] {
			 "name", "source", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Languages(), 
		   source, 
		   new String[] {
			 "name", "language", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Relations(), 
		   source, 
		   new String[] {
			 "name", "relation", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Coverages(), 
		   source, 
		   new String[] {
			 "name", "coverage", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Rights(), 
		   source, 
		   new String[] {
			 "name", "rights", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://purl.org/dc/elements/1.1/" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getMetadata_Metas(), 
		   source, 
		   new String[] {
			 "name", "meta", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getManifest_Items(), 
		   source, 
		   new String[] {
			 "name", "item", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getItem_Media_type(), 
		   source, 
		   new String[] {
			 "name", "media-type" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getItem_Fallback_style(), 
		   source, 
		   new String[] {
			 "name", "fallback-style" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getItem_Required_namespace(), 
		   source, 
		   new String[] {
			 "name", "required-namespace" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getItem_Required_modules(), 
		   source, 
		   new String[] {
			 "name", "required-modules" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getItem_Media_overlay(), 
		   source, 
		   new String[] {
			 "name", "media-overlay", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getSpine_SpineItems(), 
		   source, 
		   new String[] {
			 "name", "itemref", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getGuide_GuideItems(), 
		   source, 
		   new String[] {
			 "name", "reference", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (roleEEnum, 
		   source, 
		   new String[] {
			 "namespace", "##targetNamespace" //$NON-NLS-1$ //$NON-NLS-2$
		   });
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/emf/2002/Ecore</b>.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	protected void createEcoreAnnotations() {
		String source = "http://www.eclipse.org/emf/2002/Ecore"; //$NON-NLS-1$	
		addAnnotation
		  (referenceEClass, 
		   source, 
		   new String[] {
			 "constraints", "validType" //$NON-NLS-1$ //$NON-NLS-2$
		   });
	}

} //OPFPackageImpl
