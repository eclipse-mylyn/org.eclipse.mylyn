/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.dc.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.mylyn.docs.epub.dc.Contributor;
import org.eclipse.mylyn.docs.epub.dc.Coverage;
import org.eclipse.mylyn.docs.epub.dc.Creator;
import org.eclipse.mylyn.docs.epub.dc.DCFactory;
import org.eclipse.mylyn.docs.epub.dc.DCPackage;
import org.eclipse.mylyn.docs.epub.dc.DCType;
import org.eclipse.mylyn.docs.epub.dc.Date;
import org.eclipse.mylyn.docs.epub.dc.Description;
import org.eclipse.mylyn.docs.epub.dc.Format;
import org.eclipse.mylyn.docs.epub.dc.Identifier;
import org.eclipse.mylyn.docs.epub.dc.Language;
import org.eclipse.mylyn.docs.epub.dc.LocalizedDCType;
import org.eclipse.mylyn.docs.epub.dc.Publisher;
import org.eclipse.mylyn.docs.epub.dc.Relation;
import org.eclipse.mylyn.docs.epub.dc.Rights;
import org.eclipse.mylyn.docs.epub.dc.Source;
import org.eclipse.mylyn.docs.epub.dc.Subject;
import org.eclipse.mylyn.docs.epub.dc.Title;
import org.eclipse.mylyn.docs.epub.dc.Type;

import org.eclipse.mylyn.docs.epub.opf.OPFPackage;

import org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DCPackageImpl extends EPackageImpl implements DCPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass titleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass creatorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass subjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass descriptionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass publisherEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass contributorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass typeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass formatEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass identifierEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass languageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass relationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass coverageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass rightsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dcTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localizedDCTypeEClass = null;

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
	 * @see org.eclipse.mylyn.docs.epub.dc.DCPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private DCPackageImpl() {
		super(eNS_URI, DCFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link DCPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static DCPackage init() {
		if (isInited) return (DCPackage)EPackage.Registry.INSTANCE.getEPackage(DCPackage.eNS_URI);

		// Obtain or create and register package
		DCPackageImpl theDCPackage = (DCPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof DCPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new DCPackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		OPFPackageImpl theOPFPackage = (OPFPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(OPFPackage.eNS_URI) instanceof OPFPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(OPFPackage.eNS_URI) : OPFPackage.eINSTANCE);

		// Create package meta-data objects
		theDCPackage.createPackageContents();
		theOPFPackage.createPackageContents();

		// Initialize created meta-data
		theDCPackage.initializePackageContents();
		theOPFPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theDCPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(DCPackage.eNS_URI, theDCPackage);
		return theDCPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTitle() {
		return titleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCreator() {
		return creatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCreator_Role() {
		return (EAttribute)creatorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCreator_FileAs() {
		return (EAttribute)creatorEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSubject() {
		return subjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDescription() {
		return descriptionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPublisher() {
		return publisherEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getContributor() {
		return contributorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContributor_Role() {
		return (EAttribute)contributorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContributor_FileAs() {
		return (EAttribute)contributorEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDate() {
		return dateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDate_Event() {
		return (EAttribute)dateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getType() {
		return typeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getFormat() {
		return formatEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getIdentifier() {
		return identifierEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getIdentifier_Id() {
		return (EAttribute)identifierEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getIdentifier_Scheme() {
		return (EAttribute)identifierEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getIdentifier_Mixed() {
		return (EAttribute)identifierEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSource() {
		return sourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLanguage() {
		return languageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLanguage_Type() {
		return (EAttribute)languageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRelation() {
		return relationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCoverage() {
		return coverageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRights() {
		return rightsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDCType() {
		return dcTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDCType_Id() {
		return (EAttribute)dcTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDCType_Mixed() {
		return (EAttribute)dcTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalizedDCType() {
		return localizedDCTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLocalizedDCType_Lang() {
		return (EAttribute)localizedDCTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DCFactory getDCFactory() {
		return (DCFactory)getEFactoryInstance();
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
		titleEClass = createEClass(TITLE);

		creatorEClass = createEClass(CREATOR);
		createEAttribute(creatorEClass, CREATOR__ROLE);
		createEAttribute(creatorEClass, CREATOR__FILE_AS);

		subjectEClass = createEClass(SUBJECT);

		descriptionEClass = createEClass(DESCRIPTION);

		publisherEClass = createEClass(PUBLISHER);

		contributorEClass = createEClass(CONTRIBUTOR);
		createEAttribute(contributorEClass, CONTRIBUTOR__ROLE);
		createEAttribute(contributorEClass, CONTRIBUTOR__FILE_AS);

		dateEClass = createEClass(DATE);
		createEAttribute(dateEClass, DATE__EVENT);

		typeEClass = createEClass(TYPE);

		formatEClass = createEClass(FORMAT);

		identifierEClass = createEClass(IDENTIFIER);
		createEAttribute(identifierEClass, IDENTIFIER__ID);
		createEAttribute(identifierEClass, IDENTIFIER__SCHEME);
		createEAttribute(identifierEClass, IDENTIFIER__MIXED);

		sourceEClass = createEClass(SOURCE);

		languageEClass = createEClass(LANGUAGE);
		createEAttribute(languageEClass, LANGUAGE__TYPE);

		relationEClass = createEClass(RELATION);

		coverageEClass = createEClass(COVERAGE);

		rightsEClass = createEClass(RIGHTS);

		dcTypeEClass = createEClass(DC_TYPE);
		createEAttribute(dcTypeEClass, DC_TYPE__ID);
		createEAttribute(dcTypeEClass, DC_TYPE__MIXED);

		localizedDCTypeEClass = createEClass(LOCALIZED_DC_TYPE);
		createEAttribute(localizedDCTypeEClass, LOCALIZED_DC_TYPE__LANG);
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

		// Obtain other dependent packages
		OPFPackage theOPFPackage = (OPFPackage)EPackage.Registry.INSTANCE.getEPackage(OPFPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		titleEClass.getESuperTypes().add(this.getLocalizedDCType());
		creatorEClass.getESuperTypes().add(this.getLocalizedDCType());
		subjectEClass.getESuperTypes().add(this.getLocalizedDCType());
		descriptionEClass.getESuperTypes().add(this.getLocalizedDCType());
		publisherEClass.getESuperTypes().add(this.getLocalizedDCType());
		contributorEClass.getESuperTypes().add(this.getLocalizedDCType());
		dateEClass.getESuperTypes().add(this.getDCType());
		typeEClass.getESuperTypes().add(this.getDCType());
		formatEClass.getESuperTypes().add(this.getDCType());
		sourceEClass.getESuperTypes().add(this.getLocalizedDCType());
		languageEClass.getESuperTypes().add(this.getDCType());
		relationEClass.getESuperTypes().add(this.getLocalizedDCType());
		coverageEClass.getESuperTypes().add(this.getLocalizedDCType());
		rightsEClass.getESuperTypes().add(this.getLocalizedDCType());
		localizedDCTypeEClass.getESuperTypes().add(this.getDCType());

		// Initialize classes and features; add operations and parameters
		initEClass(titleEClass, Title.class, "Title", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(creatorEClass, Creator.class, "Creator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getCreator_Role(), theOPFPackage.getRole(), "role", null, 0, 1, Creator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getCreator_FileAs(), ecorePackage.getEString(), "fileAs", null, 0, 1, Creator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(subjectEClass, Subject.class, "Subject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(descriptionEClass, Description.class, "Description", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(publisherEClass, Publisher.class, "Publisher", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(contributorEClass, Contributor.class, "Contributor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getContributor_Role(), theOPFPackage.getRole(), "role", null, 0, 1, Contributor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getContributor_FileAs(), ecorePackage.getEString(), "fileAs", null, 0, 1, Contributor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(dateEClass, Date.class, "Date", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getDate_Event(), ecorePackage.getEString(), "event", null, 0, 1, Date.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(typeEClass, Type.class, "Type", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(formatEClass, Format.class, "Format", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(identifierEClass, Identifier.class, "Identifier", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getIdentifier_Id(), ecorePackage.getEString(), "id", "BookId", 1, 1, Identifier.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getIdentifier_Scheme(), ecorePackage.getEString(), "scheme", null, 0, 1, Identifier.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getIdentifier_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 1, -1, Identifier.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(sourceEClass, Source.class, "Source", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(languageEClass, Language.class, "Language", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getLanguage_Type(), ecorePackage.getEString(), "type", null, 0, 1, Language.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(relationEClass, Relation.class, "Relation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(coverageEClass, Coverage.class, "Coverage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(rightsEClass, Rights.class, "Rights", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(dcTypeEClass, DCType.class, "DCType", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getDCType_Id(), ecorePackage.getEString(), "id", null, 0, 1, DCType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getDCType_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 1, -1, DCType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(localizedDCTypeEClass, LocalizedDCType.class, "LocalizedDCType", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getLocalizedDCType_Lang(), ecorePackage.getEString(), "lang", null, 0, 1, LocalizedDCType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

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
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData"; //$NON-NLS-1$	
		addAnnotation
		  (titleEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (creatorEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getCreator_Role(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getCreator_FileAs(), 
		   source, 
		   new String[] {
			 "name", "file-as", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (subjectEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (descriptionEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (publisherEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (contributorEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getContributor_Role(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getContributor_FileAs(), 
		   source, 
		   new String[] {
			 "name", "file-as", //$NON-NLS-1$ //$NON-NLS-2$
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (dateEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getDate_Event(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (typeEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (formatEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (identifierEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (identifierEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getIdentifier_Scheme(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.idpf.org/2007/opf" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getIdentifier_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", ":mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (sourceEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (languageEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getLanguage_Type(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.w3.org/2001/XMLSchema-instance" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (relationEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (coverageEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (rightsEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (dcTypeEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getDCType_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", ":mixed" //$NON-NLS-1$ //$NON-NLS-2$
		   });	
		addAnnotation
		  (getLocalizedDCType_Lang(), 
		   source, 
		   new String[] {
			 "namespace", "http://www.w3.org/XML/1998/namespace" //$NON-NLS-1$ //$NON-NLS-2$
		   });
	}

} //DCPackageImpl
