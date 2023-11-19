/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx.impl;

import org.eclipse.emf.common.util.Enumerator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.eclipse.mylyn.docs.epub.ncx.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class NCXFactoryImpl extends EFactoryImpl implements NCXFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static NCXFactory init() {
		try {
			NCXFactory theNCXFactory = (NCXFactory)EPackage.Registry.INSTANCE.getEFactory(NCXPackage.eNS_URI);
			if (theNCXFactory != null) {
				return theNCXFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new NCXFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NCXFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case NCXPackage.AUDIO: return createAudio();
			case NCXPackage.CONTENT: return createContent();
			case NCXPackage.DOC_AUTHOR: return createDocAuthor();
			case NCXPackage.DOC_TITLE: return createDocTitle();
			case NCXPackage.HEAD: return createHead();
			case NCXPackage.IMG: return createImg();
			case NCXPackage.META: return createMeta();
			case NCXPackage.NAV_INFO: return createNavInfo();
			case NCXPackage.NAV_LABEL: return createNavLabel();
			case NCXPackage.NAV_LIST: return createNavList();
			case NCXPackage.NAV_MAP: return createNavMap();
			case NCXPackage.NAV_POINT: return createNavPoint();
			case NCXPackage.NAV_TARGET: return createNavTarget();
			case NCXPackage.NCX: return createNcx();
			case NCXPackage.PAGE_LIST: return createPageList();
			case NCXPackage.PAGE_TARGET: return createPageTarget();
			case NCXPackage.SMIL_CUSTOM_TEST: return createSmilCustomTest();
			case NCXPackage.TEXT: return createText();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case NCXPackage.BOOK_STRUCT:
				return createBookStructFromString(eDataType, initialValue);
			case NCXPackage.DEFAULT_STATE:
				return createDefaultStateFromString(eDataType, initialValue);
			case NCXPackage.DIR_TYPE:
				return createDirTypeFromString(eDataType, initialValue);
			case NCXPackage.OVERRIDE_TYPE:
				return createOverrideTypeFromString(eDataType, initialValue);
			case NCXPackage.TYPE:
				return createTypeFromString(eDataType, initialValue);
			case NCXPackage.BOOK_STRUCT_OBJECT:
				return createBookStructObjectFromString(eDataType, initialValue);
			case NCXPackage.DEFAULT_STATE_OBJECT:
				return createDefaultStateObjectFromString(eDataType, initialValue);
			case NCXPackage.DIR_TYPE_OBJECT:
				return createDirTypeObjectFromString(eDataType, initialValue);
			case NCXPackage.OVERRIDE_OBJECT:
				return createOverrideObjectFromString(eDataType, initialValue);
			case NCXPackage.SMI_LTIME_VAL:
				return createSMILtimeValFromString(eDataType, initialValue);
			case NCXPackage.TYPE_OBJECT:
				return createTypeObjectFromString(eDataType, initialValue);
			case NCXPackage.URI:
				return createURIFromString(eDataType, initialValue);
			case NCXPackage.VERSION_OBJECT:
				return createVersionObjectFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case NCXPackage.BOOK_STRUCT:
				return convertBookStructToString(eDataType, instanceValue);
			case NCXPackage.DEFAULT_STATE:
				return convertDefaultStateToString(eDataType, instanceValue);
			case NCXPackage.DIR_TYPE:
				return convertDirTypeToString(eDataType, instanceValue);
			case NCXPackage.OVERRIDE_TYPE:
				return convertOverrideTypeToString(eDataType, instanceValue);
			case NCXPackage.TYPE:
				return convertTypeToString(eDataType, instanceValue);
			case NCXPackage.BOOK_STRUCT_OBJECT:
				return convertBookStructObjectToString(eDataType, instanceValue);
			case NCXPackage.DEFAULT_STATE_OBJECT:
				return convertDefaultStateObjectToString(eDataType, instanceValue);
			case NCXPackage.DIR_TYPE_OBJECT:
				return convertDirTypeObjectToString(eDataType, instanceValue);
			case NCXPackage.OVERRIDE_OBJECT:
				return convertOverrideObjectToString(eDataType, instanceValue);
			case NCXPackage.SMI_LTIME_VAL:
				return convertSMILtimeValToString(eDataType, instanceValue);
			case NCXPackage.TYPE_OBJECT:
				return convertTypeObjectToString(eDataType, instanceValue);
			case NCXPackage.URI:
				return convertURIToString(eDataType, instanceValue);
			case NCXPackage.VERSION_OBJECT:
				return convertVersionObjectToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Audio createAudio() {
		AudioImpl audio = new AudioImpl();
		return audio;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Content createContent() {
		ContentImpl content = new ContentImpl();
		return content;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DocAuthor createDocAuthor() {
		DocAuthorImpl docAuthor = new DocAuthorImpl();
		return docAuthor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DocTitle createDocTitle() {
		DocTitleImpl docTitle = new DocTitleImpl();
		return docTitle;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Head createHead() {
		HeadImpl head = new HeadImpl();
		return head;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Img createImg() {
		ImgImpl img = new ImgImpl();
		return img;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Meta createMeta() {
		MetaImpl meta = new MetaImpl();
		return meta;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NavInfo createNavInfo() {
		NavInfoImpl navInfo = new NavInfoImpl();
		return navInfo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NavLabel createNavLabel() {
		NavLabelImpl navLabel = new NavLabelImpl();
		return navLabel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NavList createNavList() {
		NavListImpl navList = new NavListImpl();
		return navList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NavMap createNavMap() {
		NavMapImpl navMap = new NavMapImpl();
		return navMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NavPoint createNavPoint() {
		NavPointImpl navPoint = new NavPointImpl();
		return navPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NavTarget createNavTarget() {
		NavTargetImpl navTarget = new NavTargetImpl();
		return navTarget;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Ncx createNcx() {
		NcxImpl ncx = new NcxImpl();
		return ncx;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PageList createPageList() {
		PageListImpl pageList = new PageListImpl();
		return pageList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PageTarget createPageTarget() {
		PageTargetImpl pageTarget = new PageTargetImpl();
		return pageTarget;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SmilCustomTest createSmilCustomTest() {
		SmilCustomTestImpl smilCustomTest = new SmilCustomTestImpl();
		return smilCustomTest;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Text createText() {
		TextImpl text = new TextImpl();
		return text;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BookStruct createBookStructFromString(EDataType eDataType, String initialValue) {
		BookStruct result = BookStruct.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertBookStructToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DefaultState createDefaultStateFromString(EDataType eDataType, String initialValue) {
		DefaultState result = DefaultState.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDefaultStateToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DirType createDirTypeFromString(EDataType eDataType, String initialValue) {
		DirType result = DirType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDirTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OverrideType createOverrideTypeFromString(EDataType eDataType, String initialValue) {
		OverrideType result = OverrideType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertOverrideTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Type createTypeFromString(EDataType eDataType, String initialValue) {
		Type result = Type.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BookStruct createBookStructObjectFromString(EDataType eDataType, String initialValue) {
		return createBookStructFromString(NCXPackage.Literals.BOOK_STRUCT, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertBookStructObjectToString(EDataType eDataType, Object instanceValue) {
		return convertBookStructToString(NCXPackage.Literals.BOOK_STRUCT, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Enumerator createDefaultStateObjectFromString(EDataType eDataType, String initialValue) {
		return (Enumerator)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDefaultStateObjectToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Enumerator createDirTypeObjectFromString(EDataType eDataType, String initialValue) {
		return (Enumerator)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDirTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Enumerator createOverrideObjectFromString(EDataType eDataType, String initialValue) {
		return (Enumerator)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertOverrideObjectToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createSMILtimeValFromString(EDataType eDataType, String initialValue) {
		return (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.STRING, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSMILtimeValToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.STRING, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Enumerator createTypeObjectFromString(EDataType eDataType, String initialValue) {
		return (Enumerator)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createURIFromString(EDataType eDataType, String initialValue) {
		return (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.STRING, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertURIToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.STRING, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Enumerator createVersionObjectFromString(EDataType eDataType, String initialValue) {
		return (Enumerator)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertVersionObjectToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NCXPackage getNCXPackage() {
		return (NCXPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static NCXPackage getPackage() {
		return NCXPackage.eINSTANCE;
	}

} //NCXFactoryImpl
