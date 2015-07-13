/**
 */
package org.eclipse.mylyn.docs.epub.opf.util;

import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.mylyn.docs.epub.opf.Guide;
import org.eclipse.mylyn.docs.epub.opf.Item;
import org.eclipse.mylyn.docs.epub.opf.Itemref;
import org.eclipse.mylyn.docs.epub.opf.Manifest;
import org.eclipse.mylyn.docs.epub.opf.Meta;
import org.eclipse.mylyn.docs.epub.opf.Metadata;
import org.eclipse.mylyn.docs.epub.opf.OPFPackage;
import org.eclipse.mylyn.docs.epub.opf.Package;
import org.eclipse.mylyn.docs.epub.opf.Reference;
import org.eclipse.mylyn.docs.epub.opf.Role;
import org.eclipse.mylyn.docs.epub.opf.Spine;
import org.eclipse.mylyn.docs.epub.opf.Tours;
import org.eclipse.mylyn.docs.epub.opf.Type;

/**
 * <!-- begin-user-doc --> The <b>Validator</b> for the model. <!-- end-user-doc -->
 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage
 * @generated
 */
public class OPFValidator extends EObjectValidator {
	/**
	 * The cached model package
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static final OPFValidator INSTANCE = new OPFValidator();

	/**
	 * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see org.eclipse.emf.common.util.Diagnostic#getSource()
	 * @see org.eclipse.emf.common.util.Diagnostic#getCode()
	 * @generated
	 */
	public static final String DIAGNOSTIC_SOURCE = "org.eclipse.mylyn.docs.epub.opf"; //$NON-NLS-1$

	/**
	 * A constant with a fixed name that can be used as the base value for additional hand written constants. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

	/**
	 * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public OPFValidator() {
		super();
	}

	/**
	 * Returns the package of this validator switch.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EPackage getEPackage() {
	  return OPFPackage.eINSTANCE;
	}

	/**
	 * Calls <code>validateXXX</code> for the corresponding classifier of the model.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	@Override
	protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		switch (classifierID) {
			case OPFPackage.PACKAGE:
				return validatePackage((org.eclipse.mylyn.docs.epub.opf.Package)value, diagnostics, context);
			case OPFPackage.METADATA:
				return validateMetadata((Metadata)value, diagnostics, context);
			case OPFPackage.MANIFEST:
				return validateManifest((Manifest)value, diagnostics, context);
			case OPFPackage.ITEM:
				return validateItem((Item)value, diagnostics, context);
			case OPFPackage.SPINE:
				return validateSpine((Spine)value, diagnostics, context);
			case OPFPackage.GUIDE:
				return validateGuide((Guide)value, diagnostics, context);
			case OPFPackage.REFERENCE:
				return validateReference((Reference)value, diagnostics, context);
			case OPFPackage.ITEMREF:
				return validateItemref((Itemref)value, diagnostics, context);
			case OPFPackage.TOURS:
				return validateTours((Tours)value, diagnostics, context);
			case OPFPackage.META:
				return validateMeta((Meta)value, diagnostics, context);
			case OPFPackage.ROLE:
				return validateRole((Role)value, diagnostics, context);
			case OPFPackage.TYPE:
				return validateType((Type)value, diagnostics, context);
			default:
				return true;
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePackage(org.eclipse.mylyn.docs.epub.opf.Package package_, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(package_, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMetadata(Metadata metadata, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(metadata, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateManifest(Manifest manifest, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(manifest, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateItem(Item item, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(item, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateSpine(Spine spine, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(spine, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateGuide(Guide guide, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(guide, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateReference(Reference reference, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!validate_NoCircularContainment(reference, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(reference, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(reference, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(reference, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(reference, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(reference, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(reference, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(reference, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(reference, diagnostics, context);
		if (result || diagnostics != null) result &= validateReference_validType(reference, diagnostics, context);
		return result;
	}

	/**
	 * Validates the validType constraint of '<em>Reference</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	public boolean validateReference_validType(Reference reference, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		if (reference.getType().startsWith("other.")) { //$NON-NLS-1$
			return true;
		}
		// Must match one of the defined types.
		if (Type.get(reference.getType()) == null) {
			if (diagnostics != null) {
				diagnostics.add(
						createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0, "_UI_GenericConstraint_diagnostic", //$NON-NLS-1$
								new Object[] { "validType", getObjectLabel(reference, context) }, //$NON-NLS-1$
								new Object[] { reference }, context));
			}
			return false;

		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateItemref(Itemref itemref, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(itemref, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateTours(Tours tours, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(tours, diagnostics, context);
	}

	/**
	 * The Meta items are very different in the EPUB 2 and 3 specifications.
	 *
	 * @generated NOT
	 */
	public boolean validateMeta(Meta meta, DiagnosticChain diagnostics, Map<Object, Object> context) {
		org.eclipse.mylyn.docs.epub.opf.Package p = (Package) meta.eContainer().eContainer();
		String version = p.getVersion();
		if (version.equals("3.0")) {
			// Anything goes
			return validate_EveryDefaultConstraint(meta, diagnostics, context);
		} else {
			// May not use the 3.0 Meta element
			return validate_EveryDefaultConstraint(meta, diagnostics, context)
					&& !meta.eIsSet(OPFPackage.Literals.META__PROPERTY)
					&& !meta.eIsSet(OPFPackage.Literals.META__REFINES)
					&& !meta.eIsSet(OPFPackage.Literals.META__ID)
					&& !meta.eIsSet(OPFPackage.Literals.META__DIR)
					&& !meta.eIsSet(OPFPackage.Literals.META__SCHEME)
					&& meta.eIsSet(OPFPackage.Literals.META__NAME)
					&& meta.eIsSet(OPFPackage.Literals.META__CONTENT);
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateRole(Role role, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateType(Type type, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	/**
	 * Returns the resource locator that will be used to fetch messages for this validator's diagnostics. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		// TODO
		// Specialize this to return a resource locator for messages specific to this validator.
		// Ensure that you remove @generated or mark it @generated NOT
		return super.getResourceLocator();
	}

} //OPFValidator
