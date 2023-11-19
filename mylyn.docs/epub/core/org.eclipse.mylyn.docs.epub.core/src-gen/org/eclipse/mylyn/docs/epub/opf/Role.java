/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Role</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getRole()
 * @model extendedMetaData="namespace='##targetNamespace'"
 * @generated
 */
public enum Role implements Enumerator {
	/**
	 * The '<em><b>Art copyist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ART_COPYIST_VALUE
	 * @generated
	 * @ordered
	 */
	ART_COPYIST(0, "Art_copyist", "acp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Actor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ACTOR_VALUE
	 * @generated
	 * @ordered
	 */
	ACTOR(0, "Actor", "act"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Adapter</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ADAPTER_VALUE
	 * @generated
	 * @ordered
	 */
	ADAPTER(0, "Adapter", "adp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Author of afterword colophon etc</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_OF_AFTERWORD_COLOPHON_ETC_VALUE
	 * @generated
	 * @ordered
	 */
	AUTHOR_OF_AFTERWORD_COLOPHON_ETC(0, "Author_of_afterword_colophon_etc", "aft"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Analyst</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ANALYST_VALUE
	 * @generated
	 * @ordered
	 */
	ANALYST(0, "Analyst", "anl"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Animator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ANIMATOR_VALUE
	 * @generated
	 * @ordered
	 */
	ANIMATOR(0, "Animator", "anm"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Annotator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ANNOTATOR_VALUE
	 * @generated
	 * @ordered
	 */
	ANNOTATOR(0, "Annotator", "ann"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Bibliographic antecedent</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BIBLIOGRAPHIC_ANTECEDENT_VALUE
	 * @generated
	 * @ordered
	 */
	BIBLIOGRAPHIC_ANTECEDENT(0, "Bibliographic_antecedent", "ant"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Applicant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #APPLICANT_VALUE
	 * @generated
	 * @ordered
	 */
	APPLICANT(0, "Applicant", "app"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Author in quotations or text abstracts</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_IN_QUOTATIONS_OR_TEXT_ABSTRACTS_VALUE
	 * @generated
	 * @ordered
	 */
	AUTHOR_IN_QUOTATIONS_OR_TEXT_ABSTRACTS(0, "Author_in_quotations_or_text_abstracts", "aqt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Architect</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ARCHITECT_VALUE
	 * @generated
	 * @ordered
	 */
	ARCHITECT(0, "Architect", "arc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Artistic director</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ARTISTIC_DIRECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	ARTISTIC_DIRECTOR(0, "Artistic_director", "ard"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Arranger</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ARRANGER_VALUE
	 * @generated
	 * @ordered
	 */
	ARRANGER(0, "Arranger", "arr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Artist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ARTIST_VALUE
	 * @generated
	 * @ordered
	 */
	ARTIST(0, "Artist", "art"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Assignee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ASSIGNEE_VALUE
	 * @generated
	 * @ordered
	 */
	ASSIGNEE(0, "Assignee", "asg"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Associated name</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ASSOCIATED_NAME_VALUE
	 * @generated
	 * @ordered
	 */
	ASSOCIATED_NAME(0, "Associated_name", "asn"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Attributed name</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ATTRIBUTED_NAME_VALUE
	 * @generated
	 * @ordered
	 */
	ATTRIBUTED_NAME(0, "Attributed_name", "att"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Auctioneer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #AUCTIONEER_VALUE
	 * @generated
	 * @ordered
	 */
	AUCTIONEER(0, "Auctioneer", "auc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Author of dialog</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_OF_DIALOG_VALUE
	 * @generated
	 * @ordered
	 */
	AUTHOR_OF_DIALOG(0, "Author_of_dialog", "aud"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Author of introduction</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_OF_INTRODUCTION_VALUE
	 * @generated
	 * @ordered
	 */
	AUTHOR_OF_INTRODUCTION(0, "Author_of_introduction", "aui"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Author of screenplay</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_OF_SCREENPLAY_VALUE
	 * @generated
	 * @ordered
	 */
	AUTHOR_OF_SCREENPLAY(0, "Author_of_screenplay", "aus"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Author</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_VALUE
	 * @generated
	 * @ordered
	 */
	AUTHOR(0, "Author", "aut"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Binding designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BINDING_DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	BINDING_DESIGNER(0, "Binding_designer", "bdd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Bookjacket designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BOOKJACKET_DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	BOOKJACKET_DESIGNER(0, "Bookjacket_designer", "bjd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Book designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BOOK_DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	BOOK_DESIGNER(0, "Book_designer", "bkd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Book producer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BOOK_PRODUCER_VALUE
	 * @generated
	 * @ordered
	 */
	BOOK_PRODUCER(0, "Book_producer", "bkp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Blurb writer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BLURB_WRITER_VALUE
	 * @generated
	 * @ordered
	 */
	BLURB_WRITER(0, "Blurb_writer", "blw"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Binder</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BINDER_VALUE
	 * @generated
	 * @ordered
	 */
	BINDER(0, "Binder", "bnd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Bookplate designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BOOKPLATE_DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	BOOKPLATE_DESIGNER(0, "Bookplate_designer", "bpd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Bookseller</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BOOKSELLER_VALUE
	 * @generated
	 * @ordered
	 */
	BOOKSELLER(0, "Bookseller", "bsl"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Conceptor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONCEPTOR_VALUE
	 * @generated
	 * @ordered
	 */
	CONCEPTOR(0, "Conceptor", "ccp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Choreographer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CHOREOGRAPHER_VALUE
	 * @generated
	 * @ordered
	 */
	CHOREOGRAPHER(0, "Choreographer", "chr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Collaborator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COLLABORATOR_VALUE
	 * @generated
	 * @ordered
	 */
	COLLABORATOR(0, "Collaborator", "clb"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Client</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CLIENT_VALUE
	 * @generated
	 * @ordered
	 */
	CLIENT(0, "Client", "cli"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Calligrapher</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CALLIGRAPHER_VALUE
	 * @generated
	 * @ordered
	 */
	CALLIGRAPHER(0, "Calligrapher", "cll"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Colorist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COLORIST_VALUE
	 * @generated
	 * @ordered
	 */
	COLORIST(0, "Colorist", "clr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Collotyper</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COLLOTYPER_VALUE
	 * @generated
	 * @ordered
	 */
	COLLOTYPER(0, "Collotyper", "clt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Commentator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COMMENTATOR_VALUE
	 * @generated
	 * @ordered
	 */
	COMMENTATOR(0, "Commentator", "cmm"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Composer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COMPOSER_VALUE
	 * @generated
	 * @ordered
	 */
	COMPOSER(0, "Composer", "cmp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Compositor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COMPOSITOR_VALUE
	 * @generated
	 * @ordered
	 */
	COMPOSITOR(0, "Compositor", "cmt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Cinematographer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CINEMATOGRAPHER_VALUE
	 * @generated
	 * @ordered
	 */
	CINEMATOGRAPHER(0, "Cinematographer", "cng"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Conductor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONDUCTOR_VALUE
	 * @generated
	 * @ordered
	 */
	CONDUCTOR(0, "Conductor", "cnd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Censor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CENSOR_VALUE
	 * @generated
	 * @ordered
	 */
	CENSOR(0, "Censor", "cns"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Contestant appellee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTESTANT_APPELLEE_VALUE
	 * @generated
	 * @ordered
	 */
	CONTESTANT_APPELLEE(0, "Contestant_appellee", "coe"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Collector</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COLLECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	COLLECTOR(0, "Collector", "col"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Compiler</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COMPILER_VALUE
	 * @generated
	 * @ordered
	 */
	COMPILER(0, "Compiler", "com"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Conservator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONSERVATOR_VALUE
	 * @generated
	 * @ordered
	 */
	CONSERVATOR(0, "Conservator", "con"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Contestant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTESTANT_VALUE
	 * @generated
	 * @ordered
	 */
	CONTESTANT(0, "Contestant", "cos"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Contestant appellant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTESTANT_APPELLANT_VALUE
	 * @generated
	 * @ordered
	 */
	CONTESTANT_APPELLANT(0, "Contestant_appellant", "cot"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Cover designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COVER_DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	COVER_DESIGNER(0, "Cover_designer", "cov"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Copyright claimant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COPYRIGHT_CLAIMANT_VALUE
	 * @generated
	 * @ordered
	 */
	COPYRIGHT_CLAIMANT(0, "Copyright_claimant", "cpc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Complainant appellee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COMPLAINANT_APPELLEE_VALUE
	 * @generated
	 * @ordered
	 */
	COMPLAINANT_APPELLEE(0, "Complainant_appellee", "cpe"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Copyright holder</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COPYRIGHT_HOLDER_VALUE
	 * @generated
	 * @ordered
	 */
	COPYRIGHT_HOLDER(0, "Copyright_holder", "cph"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Complainant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COMPLAINANT_VALUE
	 * @generated
	 * @ordered
	 */
	COMPLAINANT(0, "Complainant", "cpl"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Complainant appellant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COMPLAINANT_APPELLANT_VALUE
	 * @generated
	 * @ordered
	 */
	COMPLAINANT_APPELLANT(0, "Complainant_appellant", "cpt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Creator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CREATOR_VALUE
	 * @generated
	 * @ordered
	 */
	CREATOR(0, "Creator", "cre"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Correspondent</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CORRESPONDENT_VALUE
	 * @generated
	 * @ordered
	 */
	CORRESPONDENT(0, "Correspondent", "crp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Corrector</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CORRECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	CORRECTOR(0, "Corrector", "crr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Consultant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONSULTANT_VALUE
	 * @generated
	 * @ordered
	 */
	CONSULTANT(0, "Consultant", "csl"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Consultant to aproject</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONSULTANT_TO_APROJECT_VALUE
	 * @generated
	 * @ordered
	 */
	CONSULTANT_TO_APROJECT(0, "Consultant_to_a_project", "csp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Costume designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COSTUME_DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	COSTUME_DESIGNER(0, "Costume_designer", "cst"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Contributor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTRIBUTOR_VALUE
	 * @generated
	 * @ordered
	 */
	CONTRIBUTOR(0, "Contributor", "ctb"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Contestee appellee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTESTEE_APPELLEE_VALUE
	 * @generated
	 * @ordered
	 */
	CONTESTEE_APPELLEE(0, "Contestee_appellee", "cte"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Cartographer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CARTOGRAPHER_VALUE
	 * @generated
	 * @ordered
	 */
	CARTOGRAPHER(0, "Cartographer", "ctg"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Contractor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTRACTOR_VALUE
	 * @generated
	 * @ordered
	 */
	CONTRACTOR(0, "Contractor", "ctr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Contestee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTESTEE_VALUE
	 * @generated
	 * @ordered
	 */
	CONTESTEE(0, "Contestee", "cts"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Contestee appellant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTESTEE_APPELLANT_VALUE
	 * @generated
	 * @ordered
	 */
	CONTESTEE_APPELLANT(0, "Contestee_appellant", "ctt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Curator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CURATOR_VALUE
	 * @generated
	 * @ordered
	 */
	CURATOR(0, "Curator", "cur"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Commentator for written text</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COMMENTATOR_FOR_WRITTEN_TEXT_VALUE
	 * @generated
	 * @ordered
	 */
	COMMENTATOR_FOR_WRITTEN_TEXT(0, "Commentator_for_written_text", "cwt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Defendant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEFENDANT_VALUE
	 * @generated
	 * @ordered
	 */
	DEFENDANT(0, "Defendant", "dfd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Defendant appellee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEFENDANT_APPELLEE_VALUE
	 * @generated
	 * @ordered
	 */
	DEFENDANT_APPELLEE(0, "Defendant_appellee", "dfe"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Defendant appellant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEFENDANT_APPELLANT_VALUE
	 * @generated
	 * @ordered
	 */
	DEFENDANT_APPELLANT(0, "Defendant_appellant", "dft"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Degree grantor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEGREE_GRANTOR_VALUE
	 * @generated
	 * @ordered
	 */
	DEGREE_GRANTOR(0, "Degree_grantor", "dgg"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Dissertant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DISSERTANT_VALUE
	 * @generated
	 * @ordered
	 */
	DISSERTANT(0, "Dissertant", "dis"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Delineator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DELINEATOR_VALUE
	 * @generated
	 * @ordered
	 */
	DELINEATOR(0, "Delineator", "dln"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Dancer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DANCER_VALUE
	 * @generated
	 * @ordered
	 */
	DANCER(0, "Dancer", "dnc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Donor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DONOR_VALUE
	 * @generated
	 * @ordered
	 */
	DONOR(0, "Donor", "dnr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Distribution place</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DISTRIBUTION_PLACE_VALUE
	 * @generated
	 * @ordered
	 */
	DISTRIBUTION_PLACE(0, "Distribution_place", "dpb"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Depicted</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEPICTED_VALUE
	 * @generated
	 * @ordered
	 */
	DEPICTED(0, "Depicted", "dpc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Depositor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEPOSITOR_VALUE
	 * @generated
	 * @ordered
	 */
	DEPOSITOR(0, "Depositor", "dpt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Draftsman</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DRAFTSMAN_VALUE
	 * @generated
	 * @ordered
	 */
	DRAFTSMAN(0, "Draftsman", "drm"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Director</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DIRECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	DIRECTOR(0, "Director", "drt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	DESIGNER(0, "Designer", "dsr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Distributor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DISTRIBUTOR_VALUE
	 * @generated
	 * @ordered
	 */
	DISTRIBUTOR(0, "Distributor", "dst"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Data contributor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DATA_CONTRIBUTOR_VALUE
	 * @generated
	 * @ordered
	 */
	DATA_CONTRIBUTOR(0, "Data_contributor", "dtc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Dedicatee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEDICATEE_VALUE
	 * @generated
	 * @ordered
	 */
	DEDICATEE(0, "Dedicatee", "dte"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Data manager</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DATA_MANAGER_VALUE
	 * @generated
	 * @ordered
	 */
	DATA_MANAGER(0, "Data_manager", "dtm"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Dedicator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEDICATOR_VALUE
	 * @generated
	 * @ordered
	 */
	DEDICATOR(0, "Dedicator", "dto"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Dubious author</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DUBIOUS_AUTHOR_VALUE
	 * @generated
	 * @ordered
	 */
	DUBIOUS_AUTHOR(0, "Dubious_author", "dub"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Editor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #EDITOR_VALUE
	 * @generated
	 * @ordered
	 */
	EDITOR(0, "Editor", "edt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Engraver</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ENGRAVER_VALUE
	 * @generated
	 * @ordered
	 */
	ENGRAVER(0, "Engraver", "egr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Electrician</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ELECTRICIAN_VALUE
	 * @generated
	 * @ordered
	 */
	ELECTRICIAN(0, "Electrician", "elg"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Electrotyper</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ELECTROTYPER_VALUE
	 * @generated
	 * @ordered
	 */
	ELECTROTYPER(0, "Electrotyper", "elt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Engineer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ENGINEER_VALUE
	 * @generated
	 * @ordered
	 */
	ENGINEER(0, "Engineer", "eng"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Etcher</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ETCHER_VALUE
	 * @generated
	 * @ordered
	 */
	ETCHER(0, "Etcher", "etr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Event place</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #EVENT_PLACE_VALUE
	 * @generated
	 * @ordered
	 */
	EVENT_PLACE(0, "Event_place", "evp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Expert</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #EXPERT_VALUE
	 * @generated
	 * @ordered
	 */
	EXPERT(0, "Expert", "exp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Facsimilist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FACSIMILIST_VALUE
	 * @generated
	 * @ordered
	 */
	FACSIMILIST(0, "Facsimilist", "fac"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Field director</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FIELD_DIRECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	FIELD_DIRECTOR(0, "Field_director", "fld"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Film editor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FILM_EDITOR_VALUE
	 * @generated
	 * @ordered
	 */
	FILM_EDITOR(0, "Film_editor", "flm"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Former owner</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FORMER_OWNER_VALUE
	 * @generated
	 * @ordered
	 */
	FORMER_OWNER(0, "Former_owner", "fmo"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>First party</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FIRST_PARTY_VALUE
	 * @generated
	 * @ordered
	 */
	FIRST_PARTY(0, "First_party", "fpy"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Funder</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FUNDER_VALUE
	 * @generated
	 * @ordered
	 */
	FUNDER(0, "Funder", "fnd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Forger</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FORGER_VALUE
	 * @generated
	 * @ordered
	 */
	FORGER(0, "Forger", "frg"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Geographic information specialist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #GEOGRAPHIC_INFORMATION_SPECIALIST_VALUE
	 * @generated
	 * @ordered
	 */
	GEOGRAPHIC_INFORMATION_SPECIALIST(0, "Geographic_information_specialist", "gis"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Graphic technician</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #GRAPHIC_TECHNICIAN_VALUE
	 * @generated
	 * @ordered
	 */
	GRAPHIC_TECHNICIAN(0, "Graphic_technician", "_grt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Honoree</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #HONOREE_VALUE
	 * @generated
	 * @ordered
	 */
	HONOREE(0, "Honoree", "hnr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Host</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #HOST_VALUE
	 * @generated
	 * @ordered
	 */
	HOST(0, "Host", "hst"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Illustrator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ILLUSTRATOR_VALUE
	 * @generated
	 * @ordered
	 */
	ILLUSTRATOR(0, "Illustrator", "ill"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Illuminator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ILLUMINATOR_VALUE
	 * @generated
	 * @ordered
	 */
	ILLUMINATOR(0, "Illuminator", "ilu"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Inscriber</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #INSCRIBER_VALUE
	 * @generated
	 * @ordered
	 */
	INSCRIBER(0, "Inscriber", "ins"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Inventor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #INVENTOR_VALUE
	 * @generated
	 * @ordered
	 */
	INVENTOR(0, "Inventor", "inv"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Instrumentalist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #INSTRUMENTALIST_VALUE
	 * @generated
	 * @ordered
	 */
	INSTRUMENTALIST(0, "Instrumentalist", "itr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Interviewee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #INTERVIEWEE_VALUE
	 * @generated
	 * @ordered
	 */
	INTERVIEWEE(0, "Interviewee", "ive"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Interviewer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #INTERVIEWER_VALUE
	 * @generated
	 * @ordered
	 */
	INTERVIEWER(0, "Interviewer", "ivr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Laboratory</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LABORATORY_VALUE
	 * @generated
	 * @ordered
	 */
	LABORATORY(0, "Laboratory", "lbr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Librettist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LIBRETTIST_VALUE
	 * @generated
	 * @ordered
	 */
	LIBRETTIST(0, "Librettist", "lbt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Laboratory director</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LABORATORY_DIRECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	LABORATORY_DIRECTOR(0, "Laboratory_director", "ldr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Lead</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LEAD_VALUE
	 * @generated
	 * @ordered
	 */
	LEAD(0, "Lead", "led"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Libelee appellee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LIBELEE_APPELLEE_VALUE
	 * @generated
	 * @ordered
	 */
	LIBELEE_APPELLEE(0, "Libelee_appellee", "lee"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Libelee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LIBELEE_VALUE
	 * @generated
	 * @ordered
	 */
	LIBELEE(0, "Libelee", "lel"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Lender</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LENDER_VALUE
	 * @generated
	 * @ordered
	 */
	LENDER(0, "Lender", "len"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Libelee appellant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LIBELEE_APPELLANT_VALUE
	 * @generated
	 * @ordered
	 */
	LIBELEE_APPELLANT(0, "Libelee_appellant", "let"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Lighting designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LIGHTING_DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	LIGHTING_DESIGNER(0, "Lighting_designer", "lgd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Libelant appellee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LIBELANT_APPELLEE_VALUE
	 * @generated
	 * @ordered
	 */
	LIBELANT_APPELLEE(0, "Libelant_appellee", "lie"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Libelant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LIBELANT_VALUE
	 * @generated
	 * @ordered
	 */
	LIBELANT(0, "Libelant", "lil"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Libelant appellant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LIBELANT_APPELLANT_VALUE
	 * @generated
	 * @ordered
	 */
	LIBELANT_APPELLANT(0, "Libelant_appellant", "lit"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Landscape architect</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LANDSCAPE_ARCHITECT_VALUE
	 * @generated
	 * @ordered
	 */
	LANDSCAPE_ARCHITECT(0, "Landscape_architect", "lsa"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Licensee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LICENSEE_VALUE
	 * @generated
	 * @ordered
	 */
	LICENSEE(0, "Licensee", "lse"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Licensor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LICENSOR_VALUE
	 * @generated
	 * @ordered
	 */
	LICENSOR(0, "Licensor", "lso"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Lithographer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LITHOGRAPHER_VALUE
	 * @generated
	 * @ordered
	 */
	LITHOGRAPHER(0, "Lithographer", "ltg"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Lyricist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LYRICIST_VALUE
	 * @generated
	 * @ordered
	 */
	LYRICIST(0, "Lyricist", "lyr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Music copyist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MUSIC_COPYIST_VALUE
	 * @generated
	 * @ordered
	 */
	MUSIC_COPYIST(0, "Music_copyist", "mcp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Manufacture place</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MANUFACTURE_PLACE_VALUE
	 * @generated
	 * @ordered
	 */
	MANUFACTURE_PLACE(0, "Manufacture_place", "mfp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Manufacturer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MANUFACTURER_VALUE
	 * @generated
	 * @ordered
	 */
	MANUFACTURER(0, "Manufacturer", "mfr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Metadata contact</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #METADATA_CONTACT_VALUE
	 * @generated
	 * @ordered
	 */
	METADATA_CONTACT(0, "Metadata_contact", "mdc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Moderator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MODERATOR_VALUE
	 * @generated
	 * @ordered
	 */
	MODERATOR(0, "Moderator", "mod"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Monitor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MONITOR_VALUE
	 * @generated
	 * @ordered
	 */
	MONITOR(0, "Monitor", "mon"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Marbler</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MARBLER_VALUE
	 * @generated
	 * @ordered
	 */
	MARBLER(0, "Marbler", "mrb"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Markup editor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MARKUP_EDITOR_VALUE
	 * @generated
	 * @ordered
	 */
	MARKUP_EDITOR(0, "Markup_editor", "mrk"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Musical director</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MUSICAL_DIRECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	MUSICAL_DIRECTOR(0, "Musical_director", "msd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Metal engraver</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #METAL_ENGRAVER_VALUE
	 * @generated
	 * @ordered
	 */
	METAL_ENGRAVER(0, "Metal_engraver", "mte"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Musician</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MUSICIAN_VALUE
	 * @generated
	 * @ordered
	 */
	MUSICIAN(0, "Musician", "mus"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Narrator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NARRATOR_VALUE
	 * @generated
	 * @ordered
	 */
	NARRATOR(0, "Narrator", "nrt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Opponent</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OPPONENT_VALUE
	 * @generated
	 * @ordered
	 */
	OPPONENT(0, "Opponent", "opn"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Originator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ORIGINATOR_VALUE
	 * @generated
	 * @ordered
	 */
	ORIGINATOR(0, "Originator", "org"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Organizer of meeting</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ORGANIZER_OF_MEETING_VALUE
	 * @generated
	 * @ordered
	 */
	ORGANIZER_OF_MEETING(0, "Organizer_of_meeting", "orm"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Other</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OTHER_VALUE
	 * @generated
	 * @ordered
	 */
	OTHER(0, "Other", "oth"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Owner</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OWNER_VALUE
	 * @generated
	 * @ordered
	 */
	OWNER(0, "Owner", "own"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Patron</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PATRON_VALUE
	 * @generated
	 * @ordered
	 */
	PATRON(0, "Patron", "pat"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Publishing director</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PUBLISHING_DIRECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	PUBLISHING_DIRECTOR(0, "Publishing_director", "pbd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Publisher</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PUBLISHER_VALUE
	 * @generated
	 * @ordered
	 */
	PUBLISHER(0, "Publisher", "pbl"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Project director</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PROJECT_DIRECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	PROJECT_DIRECTOR(0, "Project_director", "pdr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Proofreader</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PROOFREADER_VALUE
	 * @generated
	 * @ordered
	 */
	PROOFREADER(0, "Proofreader", "pfr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Photographer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PHOTOGRAPHER_VALUE
	 * @generated
	 * @ordered
	 */
	PHOTOGRAPHER(0, "Photographer", "pht"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Platemaker</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PLATEMAKER_VALUE
	 * @generated
	 * @ordered
	 */
	PLATEMAKER(0, "Platemaker", "plt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Permitting agency</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PERMITTING_AGENCY_VALUE
	 * @generated
	 * @ordered
	 */
	PERMITTING_AGENCY(0, "Permitting_agency", "pma"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Production manager</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PRODUCTION_MANAGER_VALUE
	 * @generated
	 * @ordered
	 */
	PRODUCTION_MANAGER(0, "Production_manager", "pmn"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Printer of plates</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PRINTER_OF_PLATES_VALUE
	 * @generated
	 * @ordered
	 */
	PRINTER_OF_PLATES(0, "Printer_of_plates", "pop"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Papermaker</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PAPERMAKER_VALUE
	 * @generated
	 * @ordered
	 */
	PAPERMAKER(0, "Papermaker", "ppm"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Puppeteer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PUPPETEER_VALUE
	 * @generated
	 * @ordered
	 */
	PUPPETEER(0, "Puppeteer", "ppt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Process contact</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PROCESS_CONTACT_VALUE
	 * @generated
	 * @ordered
	 */
	PROCESS_CONTACT(0, "Process_contact", "prc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Production personnel</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PRODUCTION_PERSONNEL_VALUE
	 * @generated
	 * @ordered
	 */
	PRODUCTION_PERSONNEL(0, "Production_personnel", "prd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Performer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PERFORMER_VALUE
	 * @generated
	 * @ordered
	 */
	PERFORMER(0, "Performer", "prf"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Programmer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PROGRAMMER_VALUE
	 * @generated
	 * @ordered
	 */
	PROGRAMMER(0, "Programmer", "prg"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Printmaker</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PRINTMAKER_VALUE
	 * @generated
	 * @ordered
	 */
	PRINTMAKER(0, "Printmaker", "prm"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Producer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PRODUCER_VALUE
	 * @generated
	 * @ordered
	 */
	PRODUCER(0, "Producer", "pro"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Production place</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PRODUCTION_PLACE_VALUE
	 * @generated
	 * @ordered
	 */
	PRODUCTION_PLACE(0, "Production_place", "prp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Printer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PRINTER_VALUE
	 * @generated
	 * @ordered
	 */
	PRINTER(0, "Printer", "prt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Patent applicant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PATENT_APPLICANT_VALUE
	 * @generated
	 * @ordered
	 */
	PATENT_APPLICANT(0, "Patent_applicant", "pta"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Plaintiff appellee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PLAINTIFF_APPELLEE_VALUE
	 * @generated
	 * @ordered
	 */
	PLAINTIFF_APPELLEE(0, "Plaintiff_appellee", "pte"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Plaintiff</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PLAINTIFF_VALUE
	 * @generated
	 * @ordered
	 */
	PLAINTIFF(0, "Plaintiff", "ptf"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Patent holder</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PATENT_HOLDER_VALUE
	 * @generated
	 * @ordered
	 */
	PATENT_HOLDER(0, "Patent_holder", "pth"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Plaintiff appellant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PLAINTIFF_APPELLANT_VALUE
	 * @generated
	 * @ordered
	 */
	PLAINTIFF_APPELLANT(0, "Plaintiff_appellant", "ptt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Publication place</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PUBLICATION_PLACE_VALUE
	 * @generated
	 * @ordered
	 */
	PUBLICATION_PLACE(0, "Publication_place", "pup"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Rubricator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RUBRICATOR_VALUE
	 * @generated
	 * @ordered
	 */
	RUBRICATOR(0, "Rubricator", "rbr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Recording engineer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RECORDING_ENGINEER_VALUE
	 * @generated
	 * @ordered
	 */
	RECORDING_ENGINEER(0, "Recording_engineer", "rce"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Recipient</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RECIPIENT_VALUE
	 * @generated
	 * @ordered
	 */
	RECIPIENT(0, "Recipient", "rcp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Redactor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #REDACTOR_VALUE
	 * @generated
	 * @ordered
	 */
	REDACTOR(0, "Redactor", "red"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Renderer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RENDERER_VALUE
	 * @generated
	 * @ordered
	 */
	RENDERER(0, "Renderer", "ren"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Researcher</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RESEARCHER_VALUE
	 * @generated
	 * @ordered
	 */
	RESEARCHER(0, "Researcher", "res"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Reviewer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #REVIEWER_VALUE
	 * @generated
	 * @ordered
	 */
	REVIEWER(0, "Reviewer", "rev"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Repository</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #REPOSITORY_VALUE
	 * @generated
	 * @ordered
	 */
	REPOSITORY(0, "Repository", "rps"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Reporter</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #REPORTER_VALUE
	 * @generated
	 * @ordered
	 */
	REPORTER(0, "Reporter", "rpt"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Responsible party</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RESPONSIBLE_PARTY_VALUE
	 * @generated
	 * @ordered
	 */
	RESPONSIBLE_PARTY(0, "Responsible_party", "rpy"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Respondent appellee</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RESPONDENT_APPELLEE_VALUE
	 * @generated
	 * @ordered
	 */
	RESPONDENT_APPELLEE(0, "Respondent_appellee", "rse"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Restager</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RESTAGER_VALUE
	 * @generated
	 * @ordered
	 */
	RESTAGER(0, "Restager", "rsg"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Respondent</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RESPONDENT_VALUE
	 * @generated
	 * @ordered
	 */
	RESPONDENT(0, "Respondent", "rsp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Respondent appellant</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RESPONDENT_APPELLANT_VALUE
	 * @generated
	 * @ordered
	 */
	RESPONDENT_APPELLANT(0, "Respondent_appellant", "rst"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Research team head</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RESEARCH_TEAM_HEAD_VALUE
	 * @generated
	 * @ordered
	 */
	RESEARCH_TEAM_HEAD(0, "Research_team_head", "rth"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Research team member</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RESEARCH_TEAM_MEMBER_VALUE
	 * @generated
	 * @ordered
	 */
	RESEARCH_TEAM_MEMBER(0, "Research_team_member", "rtm"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Scientific advisor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SCIENTIFIC_ADVISOR_VALUE
	 * @generated
	 * @ordered
	 */
	SCIENTIFIC_ADVISOR(0, "Scientific_advisor", "sad"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Scenarist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SCENARIST_VALUE
	 * @generated
	 * @ordered
	 */
	SCENARIST(0, "Scenarist", "sce"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Sculptor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SCULPTOR_VALUE
	 * @generated
	 * @ordered
	 */
	SCULPTOR(0, "Sculptor", "scl"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Scribe</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SCRIBE_VALUE
	 * @generated
	 * @ordered
	 */
	SCRIBE(0, "Scribe", "scr"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Sound designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SOUND_DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	SOUND_DESIGNER(0, "Sound_designer", "sds"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Secretary</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SECRETARY_VALUE
	 * @generated
	 * @ordered
	 */
	SECRETARY(0, "Secretary", "sec"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Signer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	SIGNER(0, "Signer", "sgn"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Supporting host</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SUPPORTING_HOST_VALUE
	 * @generated
	 * @ordered
	 */
	SUPPORTING_HOST(0, "Supporting_host", "sht"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Singer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SINGER_VALUE
	 * @generated
	 * @ordered
	 */
	SINGER(0, "Singer", "sng"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Speaker</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SPEAKER_VALUE
	 * @generated
	 * @ordered
	 */
	SPEAKER(0, "Speaker", "spk"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Sponsor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SPONSOR_VALUE
	 * @generated
	 * @ordered
	 */
	SPONSOR(0, "Sponsor", "spn"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Second party</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SECOND_PARTY_VALUE
	 * @generated
	 * @ordered
	 */
	SECOND_PARTY(0, "Second_party", "spy"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Surveyor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SURVEYOR_VALUE
	 * @generated
	 * @ordered
	 */
	SURVEYOR(0, "Surveyor", "srv"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Set designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SET_DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	SET_DESIGNER(0, "Set_designer", "std"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Storyteller</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #STORYTELLER_VALUE
	 * @generated
	 * @ordered
	 */
	STORYTELLER(0, "Storyteller", "stl"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Stage manager</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #STAGE_MANAGER_VALUE
	 * @generated
	 * @ordered
	 */
	STAGE_MANAGER(0, "Stage_manager", "stm"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Standards body</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #STANDARDS_BODY_VALUE
	 * @generated
	 * @ordered
	 */
	STANDARDS_BODY(0, "Standards_body", "stn"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Stereotyper</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #STEREOTYPER_VALUE
	 * @generated
	 * @ordered
	 */
	STEREOTYPER(0, "Stereotyper", "str"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Technical director</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TECHNICAL_DIRECTOR_VALUE
	 * @generated
	 * @ordered
	 */
	TECHNICAL_DIRECTOR(0, "Technical_director", "tcd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Teacher</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TEACHER_VALUE
	 * @generated
	 * @ordered
	 */
	TEACHER(0, "Teacher", "tch"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Thesis advisor</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #THESIS_ADVISOR_VALUE
	 * @generated
	 * @ordered
	 */
	THESIS_ADVISOR(0, "Thesis_advisor", "ths"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Transcriber</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TRANSCRIBER_VALUE
	 * @generated
	 * @ordered
	 */
	TRANSCRIBER(0, "Transcriber", "trc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Translator</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TRANSLATOR_VALUE
	 * @generated
	 * @ordered
	 */
	TRANSLATOR(0, "Translator", "trl"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Type designer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TYPE_DESIGNER_VALUE
	 * @generated
	 * @ordered
	 */
	TYPE_DESIGNER(0, "Type_designer", "tyd"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Typographer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TYPOGRAPHER_VALUE
	 * @generated
	 * @ordered
	 */
	TYPOGRAPHER(0, "Typographer", "tyg"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>University place</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #UNIVERSITY_PLACE_VALUE
	 * @generated
	 * @ordered
	 */
	UNIVERSITY_PLACE(0, "University_place", "uvp"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Videographer</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #VIDEOGRAPHER_VALUE
	 * @generated
	 * @ordered
	 */
	VIDEOGRAPHER(0, "Videographer", "vdg"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Vocalist</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #VOCALIST_VALUE
	 * @generated
	 * @ordered
	 */
	VOCALIST(0, "Vocalist", "voc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Writer of accompanying material</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #WRITER_OF_ACCOMPANYING_MATERIAL_VALUE
	 * @generated
	 * @ordered
	 */
	WRITER_OF_ACCOMPANYING_MATERIAL(0, "Writer_of_accompanying_material", "wam"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Woodcutter</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #WOODCUTTER_VALUE
	 * @generated
	 * @ordered
	 */
	WOODCUTTER(0, "Woodcutter", "wdc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Wood engraver</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #WOOD_ENGRAVER_VALUE
	 * @generated
	 * @ordered
	 */
	WOOD_ENGRAVER(0, "Wood_engraver", "wde"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Witness</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #WITNESS_VALUE
	 * @generated
	 * @ordered
	 */
	WITNESS(0, "Witness", "wit"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Art copyist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Art copyist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ART_COPYIST
	 * @model name="Art_copyist" literal="acp"
	 * @generated
	 * @ordered
	 */
	public static final int ART_COPYIST_VALUE = 0;

	/**
	 * The '<em><b>Actor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Actor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ACTOR
	 * @model name="Actor" literal="act"
	 * @generated
	 * @ordered
	 */
	public static final int ACTOR_VALUE = 0;

	/**
	 * The '<em><b>Adapter</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Adapter</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ADAPTER
	 * @model name="Adapter" literal="adp"
	 * @generated
	 * @ordered
	 */
	public static final int ADAPTER_VALUE = 0;

	/**
	 * The '<em><b>Author of afterword colophon etc</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Author of afterword colophon etc</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_OF_AFTERWORD_COLOPHON_ETC
	 * @model name="Author_of_afterword_colophon_etc" literal="aft"
	 * @generated
	 * @ordered
	 */
	public static final int AUTHOR_OF_AFTERWORD_COLOPHON_ETC_VALUE = 0;

	/**
	 * The '<em><b>Analyst</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Analyst</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ANALYST
	 * @model name="Analyst" literal="anl"
	 * @generated
	 * @ordered
	 */
	public static final int ANALYST_VALUE = 0;

	/**
	 * The '<em><b>Animator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Animator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ANIMATOR
	 * @model name="Animator" literal="anm"
	 * @generated
	 * @ordered
	 */
	public static final int ANIMATOR_VALUE = 0;

	/**
	 * The '<em><b>Annotator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Annotator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ANNOTATOR
	 * @model name="Annotator" literal="ann"
	 * @generated
	 * @ordered
	 */
	public static final int ANNOTATOR_VALUE = 0;

	/**
	 * The '<em><b>Bibliographic antecedent</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Bibliographic antecedent</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BIBLIOGRAPHIC_ANTECEDENT
	 * @model name="Bibliographic_antecedent" literal="ant"
	 * @generated
	 * @ordered
	 */
	public static final int BIBLIOGRAPHIC_ANTECEDENT_VALUE = 0;

	/**
	 * The '<em><b>Applicant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Applicant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #APPLICANT
	 * @model name="Applicant" literal="app"
	 * @generated
	 * @ordered
	 */
	public static final int APPLICANT_VALUE = 0;

	/**
	 * The '<em><b>Author in quotations or text abstracts</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Author in quotations or text abstracts</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_IN_QUOTATIONS_OR_TEXT_ABSTRACTS
	 * @model name="Author_in_quotations_or_text_abstracts" literal="aqt"
	 * @generated
	 * @ordered
	 */
	public static final int AUTHOR_IN_QUOTATIONS_OR_TEXT_ABSTRACTS_VALUE = 0;

	/**
	 * The '<em><b>Architect</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Architect</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ARCHITECT
	 * @model name="Architect" literal="arc"
	 * @generated
	 * @ordered
	 */
	public static final int ARCHITECT_VALUE = 0;

	/**
	 * The '<em><b>Artistic director</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Artistic director</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ARTISTIC_DIRECTOR
	 * @model name="Artistic_director" literal="ard"
	 * @generated
	 * @ordered
	 */
	public static final int ARTISTIC_DIRECTOR_VALUE = 0;

	/**
	 * The '<em><b>Arranger</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Arranger</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ARRANGER
	 * @model name="Arranger" literal="arr"
	 * @generated
	 * @ordered
	 */
	public static final int ARRANGER_VALUE = 0;

	/**
	 * The '<em><b>Artist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Artist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ARTIST
	 * @model name="Artist" literal="art"
	 * @generated
	 * @ordered
	 */
	public static final int ARTIST_VALUE = 0;

	/**
	 * The '<em><b>Assignee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Assignee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ASSIGNEE
	 * @model name="Assignee" literal="asg"
	 * @generated
	 * @ordered
	 */
	public static final int ASSIGNEE_VALUE = 0;

	/**
	 * The '<em><b>Associated name</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Associated name</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ASSOCIATED_NAME
	 * @model name="Associated_name" literal="asn"
	 * @generated
	 * @ordered
	 */
	public static final int ASSOCIATED_NAME_VALUE = 0;

	/**
	 * The '<em><b>Attributed name</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Attributed name</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ATTRIBUTED_NAME
	 * @model name="Attributed_name" literal="att"
	 * @generated
	 * @ordered
	 */
	public static final int ATTRIBUTED_NAME_VALUE = 0;

	/**
	 * The '<em><b>Auctioneer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Auctioneer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #AUCTIONEER
	 * @model name="Auctioneer" literal="auc"
	 * @generated
	 * @ordered
	 */
	public static final int AUCTIONEER_VALUE = 0;

	/**
	 * The '<em><b>Author of dialog</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Author of dialog</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_OF_DIALOG
	 * @model name="Author_of_dialog" literal="aud"
	 * @generated
	 * @ordered
	 */
	public static final int AUTHOR_OF_DIALOG_VALUE = 0;

	/**
	 * The '<em><b>Author of introduction</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Author of introduction</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_OF_INTRODUCTION
	 * @model name="Author_of_introduction" literal="aui"
	 * @generated
	 * @ordered
	 */
	public static final int AUTHOR_OF_INTRODUCTION_VALUE = 0;

	/**
	 * The '<em><b>Author of screenplay</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Author of screenplay</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #AUTHOR_OF_SCREENPLAY
	 * @model name="Author_of_screenplay" literal="aus"
	 * @generated
	 * @ordered
	 */
	public static final int AUTHOR_OF_SCREENPLAY_VALUE = 0;

	/**
	 * The '<em><b>Author</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Author</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #AUTHOR
	 * @model name="Author" literal="aut"
	 * @generated
	 * @ordered
	 */
	public static final int AUTHOR_VALUE = 0;

	/**
	 * The '<em><b>Binding designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Binding designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BINDING_DESIGNER
	 * @model name="Binding_designer" literal="bdd"
	 * @generated
	 * @ordered
	 */
	public static final int BINDING_DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Bookjacket designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Bookjacket designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BOOKJACKET_DESIGNER
	 * @model name="Bookjacket_designer" literal="bjd"
	 * @generated
	 * @ordered
	 */
	public static final int BOOKJACKET_DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Book designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Book designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BOOK_DESIGNER
	 * @model name="Book_designer" literal="bkd"
	 * @generated
	 * @ordered
	 */
	public static final int BOOK_DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Book producer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Book producer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BOOK_PRODUCER
	 * @model name="Book_producer" literal="bkp"
	 * @generated
	 * @ordered
	 */
	public static final int BOOK_PRODUCER_VALUE = 0;

	/**
	 * The '<em><b>Blurb writer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Blurb writer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BLURB_WRITER
	 * @model name="Blurb_writer" literal="blw"
	 * @generated
	 * @ordered
	 */
	public static final int BLURB_WRITER_VALUE = 0;

	/**
	 * The '<em><b>Binder</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Binder</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BINDER
	 * @model name="Binder" literal="bnd"
	 * @generated
	 * @ordered
	 */
	public static final int BINDER_VALUE = 0;

	/**
	 * The '<em><b>Bookplate designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Bookplate designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BOOKPLATE_DESIGNER
	 * @model name="Bookplate_designer" literal="bpd"
	 * @generated
	 * @ordered
	 */
	public static final int BOOKPLATE_DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Bookseller</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Bookseller</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BOOKSELLER
	 * @model name="Bookseller" literal="bsl"
	 * @generated
	 * @ordered
	 */
	public static final int BOOKSELLER_VALUE = 0;

	/**
	 * The '<em><b>Conceptor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Conceptor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONCEPTOR
	 * @model name="Conceptor" literal="ccp"
	 * @generated
	 * @ordered
	 */
	public static final int CONCEPTOR_VALUE = 0;

	/**
	 * The '<em><b>Choreographer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Choreographer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CHOREOGRAPHER
	 * @model name="Choreographer" literal="chr"
	 * @generated
	 * @ordered
	 */
	public static final int CHOREOGRAPHER_VALUE = 0;

	/**
	 * The '<em><b>Collaborator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Collaborator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COLLABORATOR
	 * @model name="Collaborator" literal="clb"
	 * @generated
	 * @ordered
	 */
	public static final int COLLABORATOR_VALUE = 0;

	/**
	 * The '<em><b>Client</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Client</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CLIENT
	 * @model name="Client" literal="cli"
	 * @generated
	 * @ordered
	 */
	public static final int CLIENT_VALUE = 0;

	/**
	 * The '<em><b>Calligrapher</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Calligrapher</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CALLIGRAPHER
	 * @model name="Calligrapher" literal="cll"
	 * @generated
	 * @ordered
	 */
	public static final int CALLIGRAPHER_VALUE = 0;

	/**
	 * The '<em><b>Colorist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Colorist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COLORIST
	 * @model name="Colorist" literal="clr"
	 * @generated
	 * @ordered
	 */
	public static final int COLORIST_VALUE = 0;

	/**
	 * The '<em><b>Collotyper</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Collotyper</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COLLOTYPER
	 * @model name="Collotyper" literal="clt"
	 * @generated
	 * @ordered
	 */
	public static final int COLLOTYPER_VALUE = 0;

	/**
	 * The '<em><b>Commentator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Commentator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COMMENTATOR
	 * @model name="Commentator" literal="cmm"
	 * @generated
	 * @ordered
	 */
	public static final int COMMENTATOR_VALUE = 0;

	/**
	 * The '<em><b>Composer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Composer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COMPOSER
	 * @model name="Composer" literal="cmp"
	 * @generated
	 * @ordered
	 */
	public static final int COMPOSER_VALUE = 0;

	/**
	 * The '<em><b>Compositor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Compositor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COMPOSITOR
	 * @model name="Compositor" literal="cmt"
	 * @generated
	 * @ordered
	 */
	public static final int COMPOSITOR_VALUE = 0;

	/**
	 * The '<em><b>Cinematographer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Cinematographer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CINEMATOGRAPHER
	 * @model name="Cinematographer" literal="cng"
	 * @generated
	 * @ordered
	 */
	public static final int CINEMATOGRAPHER_VALUE = 0;

	/**
	 * The '<em><b>Conductor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Conductor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONDUCTOR
	 * @model name="Conductor" literal="cnd"
	 * @generated
	 * @ordered
	 */
	public static final int CONDUCTOR_VALUE = 0;

	/**
	 * The '<em><b>Censor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Censor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CENSOR
	 * @model name="Censor" literal="cns"
	 * @generated
	 * @ordered
	 */
	public static final int CENSOR_VALUE = 0;

	/**
	 * The '<em><b>Contestant appellee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Contestant appellee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTESTANT_APPELLEE
	 * @model name="Contestant_appellee" literal="coe"
	 * @generated
	 * @ordered
	 */
	public static final int CONTESTANT_APPELLEE_VALUE = 0;

	/**
	 * The '<em><b>Collector</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Collector</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COLLECTOR
	 * @model name="Collector" literal="col"
	 * @generated
	 * @ordered
	 */
	public static final int COLLECTOR_VALUE = 0;

	/**
	 * The '<em><b>Compiler</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Compiler</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COMPILER
	 * @model name="Compiler" literal="com"
	 * @generated
	 * @ordered
	 */
	public static final int COMPILER_VALUE = 0;

	/**
	 * The '<em><b>Conservator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Conservator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONSERVATOR
	 * @model name="Conservator" literal="con"
	 * @generated
	 * @ordered
	 */
	public static final int CONSERVATOR_VALUE = 0;

	/**
	 * The '<em><b>Contestant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Contestant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTESTANT
	 * @model name="Contestant" literal="cos"
	 * @generated
	 * @ordered
	 */
	public static final int CONTESTANT_VALUE = 0;

	/**
	 * The '<em><b>Contestant appellant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Contestant appellant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTESTANT_APPELLANT
	 * @model name="Contestant_appellant" literal="cot"
	 * @generated
	 * @ordered
	 */
	public static final int CONTESTANT_APPELLANT_VALUE = 0;

	/**
	 * The '<em><b>Cover designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Cover designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COVER_DESIGNER
	 * @model name="Cover_designer" literal="cov"
	 * @generated
	 * @ordered
	 */
	public static final int COVER_DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Copyright claimant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Copyright claimant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COPYRIGHT_CLAIMANT
	 * @model name="Copyright_claimant" literal="cpc"
	 * @generated
	 * @ordered
	 */
	public static final int COPYRIGHT_CLAIMANT_VALUE = 0;

	/**
	 * The '<em><b>Complainant appellee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Complainant appellee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COMPLAINANT_APPELLEE
	 * @model name="Complainant_appellee" literal="cpe"
	 * @generated
	 * @ordered
	 */
	public static final int COMPLAINANT_APPELLEE_VALUE = 0;

	/**
	 * The '<em><b>Copyright holder</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Copyright holder</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COPYRIGHT_HOLDER
	 * @model name="Copyright_holder" literal="cph"
	 * @generated
	 * @ordered
	 */
	public static final int COPYRIGHT_HOLDER_VALUE = 0;

	/**
	 * The '<em><b>Complainant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Complainant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COMPLAINANT
	 * @model name="Complainant" literal="cpl"
	 * @generated
	 * @ordered
	 */
	public static final int COMPLAINANT_VALUE = 0;

	/**
	 * The '<em><b>Complainant appellant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Complainant appellant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COMPLAINANT_APPELLANT
	 * @model name="Complainant_appellant" literal="cpt"
	 * @generated
	 * @ordered
	 */
	public static final int COMPLAINANT_APPELLANT_VALUE = 0;

	/**
	 * The '<em><b>Creator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Creator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CREATOR
	 * @model name="Creator" literal="cre"
	 * @generated
	 * @ordered
	 */
	public static final int CREATOR_VALUE = 0;

	/**
	 * The '<em><b>Correspondent</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Correspondent</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CORRESPONDENT
	 * @model name="Correspondent" literal="crp"
	 * @generated
	 * @ordered
	 */
	public static final int CORRESPONDENT_VALUE = 0;

	/**
	 * The '<em><b>Corrector</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Corrector</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CORRECTOR
	 * @model name="Corrector" literal="crr"
	 * @generated
	 * @ordered
	 */
	public static final int CORRECTOR_VALUE = 0;

	/**
	 * The '<em><b>Consultant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Consultant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONSULTANT
	 * @model name="Consultant" literal="csl"
	 * @generated
	 * @ordered
	 */
	public static final int CONSULTANT_VALUE = 0;

	/**
	 * The '<em><b>Consultant to aproject</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Consultant to aproject</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONSULTANT_TO_APROJECT
	 * @model name="Consultant_to_a_project" literal="csp"
	 * @generated
	 * @ordered
	 */
	public static final int CONSULTANT_TO_APROJECT_VALUE = 0;

	/**
	 * The '<em><b>Costume designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Costume designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COSTUME_DESIGNER
	 * @model name="Costume_designer" literal="cst"
	 * @generated
	 * @ordered
	 */
	public static final int COSTUME_DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Contributor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Contributor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTRIBUTOR
	 * @model name="Contributor" literal="ctb"
	 * @generated
	 * @ordered
	 */
	public static final int CONTRIBUTOR_VALUE = 0;

	/**
	 * The '<em><b>Contestee appellee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Contestee appellee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTESTEE_APPELLEE
	 * @model name="Contestee_appellee" literal="cte"
	 * @generated
	 * @ordered
	 */
	public static final int CONTESTEE_APPELLEE_VALUE = 0;

	/**
	 * The '<em><b>Cartographer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Cartographer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CARTOGRAPHER
	 * @model name="Cartographer" literal="ctg"
	 * @generated
	 * @ordered
	 */
	public static final int CARTOGRAPHER_VALUE = 0;

	/**
	 * The '<em><b>Contractor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Contractor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTRACTOR
	 * @model name="Contractor" literal="ctr"
	 * @generated
	 * @ordered
	 */
	public static final int CONTRACTOR_VALUE = 0;

	/**
	 * The '<em><b>Contestee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Contestee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTESTEE
	 * @model name="Contestee" literal="cts"
	 * @generated
	 * @ordered
	 */
	public static final int CONTESTEE_VALUE = 0;

	/**
	 * The '<em><b>Contestee appellant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Contestee appellant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTESTEE_APPELLANT
	 * @model name="Contestee_appellant" literal="ctt"
	 * @generated
	 * @ordered
	 */
	public static final int CONTESTEE_APPELLANT_VALUE = 0;

	/**
	 * The '<em><b>Curator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Curator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CURATOR
	 * @model name="Curator" literal="cur"
	 * @generated
	 * @ordered
	 */
	public static final int CURATOR_VALUE = 0;

	/**
	 * The '<em><b>Commentator for written text</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Commentator for written text</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COMMENTATOR_FOR_WRITTEN_TEXT
	 * @model name="Commentator_for_written_text" literal="cwt"
	 * @generated
	 * @ordered
	 */
	public static final int COMMENTATOR_FOR_WRITTEN_TEXT_VALUE = 0;

	/**
	 * The '<em><b>Defendant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Defendant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEFENDANT
	 * @model name="Defendant" literal="dfd"
	 * @generated
	 * @ordered
	 */
	public static final int DEFENDANT_VALUE = 0;

	/**
	 * The '<em><b>Defendant appellee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Defendant appellee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEFENDANT_APPELLEE
	 * @model name="Defendant_appellee" literal="dfe"
	 * @generated
	 * @ordered
	 */
	public static final int DEFENDANT_APPELLEE_VALUE = 0;

	/**
	 * The '<em><b>Defendant appellant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Defendant appellant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEFENDANT_APPELLANT
	 * @model name="Defendant_appellant" literal="dft"
	 * @generated
	 * @ordered
	 */
	public static final int DEFENDANT_APPELLANT_VALUE = 0;

	/**
	 * The '<em><b>Degree grantor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Degree grantor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEGREE_GRANTOR
	 * @model name="Degree_grantor" literal="dgg"
	 * @generated
	 * @ordered
	 */
	public static final int DEGREE_GRANTOR_VALUE = 0;

	/**
	 * The '<em><b>Dissertant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Dissertant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DISSERTANT
	 * @model name="Dissertant" literal="dis"
	 * @generated
	 * @ordered
	 */
	public static final int DISSERTANT_VALUE = 0;

	/**
	 * The '<em><b>Delineator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Delineator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DELINEATOR
	 * @model name="Delineator" literal="dln"
	 * @generated
	 * @ordered
	 */
	public static final int DELINEATOR_VALUE = 0;

	/**
	 * The '<em><b>Dancer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Dancer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DANCER
	 * @model name="Dancer" literal="dnc"
	 * @generated
	 * @ordered
	 */
	public static final int DANCER_VALUE = 0;

	/**
	 * The '<em><b>Donor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Donor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DONOR
	 * @model name="Donor" literal="dnr"
	 * @generated
	 * @ordered
	 */
	public static final int DONOR_VALUE = 0;

	/**
	 * The '<em><b>Distribution place</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Distribution place</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DISTRIBUTION_PLACE
	 * @model name="Distribution_place" literal="dpb"
	 * @generated
	 * @ordered
	 */
	public static final int DISTRIBUTION_PLACE_VALUE = 0;

	/**
	 * The '<em><b>Depicted</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Depicted</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEPICTED
	 * @model name="Depicted" literal="dpc"
	 * @generated
	 * @ordered
	 */
	public static final int DEPICTED_VALUE = 0;

	/**
	 * The '<em><b>Depositor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Depositor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEPOSITOR
	 * @model name="Depositor" literal="dpt"
	 * @generated
	 * @ordered
	 */
	public static final int DEPOSITOR_VALUE = 0;

	/**
	 * The '<em><b>Draftsman</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Draftsman</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DRAFTSMAN
	 * @model name="Draftsman" literal="drm"
	 * @generated
	 * @ordered
	 */
	public static final int DRAFTSMAN_VALUE = 0;

	/**
	 * The '<em><b>Director</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Director</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DIRECTOR
	 * @model name="Director" literal="drt"
	 * @generated
	 * @ordered
	 */
	public static final int DIRECTOR_VALUE = 0;

	/**
	 * The '<em><b>Designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DESIGNER
	 * @model name="Designer" literal="dsr"
	 * @generated
	 * @ordered
	 */
	public static final int DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Distributor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Distributor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DISTRIBUTOR
	 * @model name="Distributor" literal="dst"
	 * @generated
	 * @ordered
	 */
	public static final int DISTRIBUTOR_VALUE = 0;

	/**
	 * The '<em><b>Data contributor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Data contributor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DATA_CONTRIBUTOR
	 * @model name="Data_contributor" literal="dtc"
	 * @generated
	 * @ordered
	 */
	public static final int DATA_CONTRIBUTOR_VALUE = 0;

	/**
	 * The '<em><b>Dedicatee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Dedicatee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEDICATEE
	 * @model name="Dedicatee" literal="dte"
	 * @generated
	 * @ordered
	 */
	public static final int DEDICATEE_VALUE = 0;

	/**
	 * The '<em><b>Data manager</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Data manager</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DATA_MANAGER
	 * @model name="Data_manager" literal="dtm"
	 * @generated
	 * @ordered
	 */
	public static final int DATA_MANAGER_VALUE = 0;

	/**
	 * The '<em><b>Dedicator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Dedicator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEDICATOR
	 * @model name="Dedicator" literal="dto"
	 * @generated
	 * @ordered
	 */
	public static final int DEDICATOR_VALUE = 0;

	/**
	 * The '<em><b>Dubious author</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Dubious author</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DUBIOUS_AUTHOR
	 * @model name="Dubious_author" literal="dub"
	 * @generated
	 * @ordered
	 */
	public static final int DUBIOUS_AUTHOR_VALUE = 0;

	/**
	 * The '<em><b>Editor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Editor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #EDITOR
	 * @model name="Editor" literal="edt"
	 * @generated
	 * @ordered
	 */
	public static final int EDITOR_VALUE = 0;

	/**
	 * The '<em><b>Engraver</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Engraver</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ENGRAVER
	 * @model name="Engraver" literal="egr"
	 * @generated
	 * @ordered
	 */
	public static final int ENGRAVER_VALUE = 0;

	/**
	 * The '<em><b>Electrician</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Electrician</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ELECTRICIAN
	 * @model name="Electrician" literal="elg"
	 * @generated
	 * @ordered
	 */
	public static final int ELECTRICIAN_VALUE = 0;

	/**
	 * The '<em><b>Electrotyper</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Electrotyper</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ELECTROTYPER
	 * @model name="Electrotyper" literal="elt"
	 * @generated
	 * @ordered
	 */
	public static final int ELECTROTYPER_VALUE = 0;

	/**
	 * The '<em><b>Engineer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Engineer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ENGINEER
	 * @model name="Engineer" literal="eng"
	 * @generated
	 * @ordered
	 */
	public static final int ENGINEER_VALUE = 0;

	/**
	 * The '<em><b>Etcher</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Etcher</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ETCHER
	 * @model name="Etcher" literal="etr"
	 * @generated
	 * @ordered
	 */
	public static final int ETCHER_VALUE = 0;

	/**
	 * The '<em><b>Event place</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Event place</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #EVENT_PLACE
	 * @model name="Event_place" literal="evp"
	 * @generated
	 * @ordered
	 */
	public static final int EVENT_PLACE_VALUE = 0;

	/**
	 * The '<em><b>Expert</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Expert</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #EXPERT
	 * @model name="Expert" literal="exp"
	 * @generated
	 * @ordered
	 */
	public static final int EXPERT_VALUE = 0;

	/**
	 * The '<em><b>Facsimilist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Facsimilist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FACSIMILIST
	 * @model name="Facsimilist" literal="fac"
	 * @generated
	 * @ordered
	 */
	public static final int FACSIMILIST_VALUE = 0;

	/**
	 * The '<em><b>Field director</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Field director</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FIELD_DIRECTOR
	 * @model name="Field_director" literal="fld"
	 * @generated
	 * @ordered
	 */
	public static final int FIELD_DIRECTOR_VALUE = 0;

	/**
	 * The '<em><b>Film editor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Film editor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FILM_EDITOR
	 * @model name="Film_editor" literal="flm"
	 * @generated
	 * @ordered
	 */
	public static final int FILM_EDITOR_VALUE = 0;

	/**
	 * The '<em><b>Former owner</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Former owner</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FORMER_OWNER
	 * @model name="Former_owner" literal="fmo"
	 * @generated
	 * @ordered
	 */
	public static final int FORMER_OWNER_VALUE = 0;

	/**
	 * The '<em><b>First party</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>First party</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FIRST_PARTY
	 * @model name="First_party" literal="fpy"
	 * @generated
	 * @ordered
	 */
	public static final int FIRST_PARTY_VALUE = 0;

	/**
	 * The '<em><b>Funder</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Funder</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FUNDER
	 * @model name="Funder" literal="fnd"
	 * @generated
	 * @ordered
	 */
	public static final int FUNDER_VALUE = 0;

	/**
	 * The '<em><b>Forger</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Forger</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FORGER
	 * @model name="Forger" literal="frg"
	 * @generated
	 * @ordered
	 */
	public static final int FORGER_VALUE = 0;

	/**
	 * The '<em><b>Geographic information specialist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Geographic information specialist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #GEOGRAPHIC_INFORMATION_SPECIALIST
	 * @model name="Geographic_information_specialist" literal="gis"
	 * @generated
	 * @ordered
	 */
	public static final int GEOGRAPHIC_INFORMATION_SPECIALIST_VALUE = 0;

	/**
	 * The '<em><b>Graphic technician</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Graphic technician</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #GRAPHIC_TECHNICIAN
	 * @model name="Graphic_technician" literal="_grt"
	 * @generated
	 * @ordered
	 */
	public static final int GRAPHIC_TECHNICIAN_VALUE = 0;

	/**
	 * The '<em><b>Honoree</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Honoree</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #HONOREE
	 * @model name="Honoree" literal="hnr"
	 * @generated
	 * @ordered
	 */
	public static final int HONOREE_VALUE = 0;

	/**
	 * The '<em><b>Host</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Host</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #HOST
	 * @model name="Host" literal="hst"
	 * @generated
	 * @ordered
	 */
	public static final int HOST_VALUE = 0;

	/**
	 * The '<em><b>Illustrator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Illustrator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ILLUSTRATOR
	 * @model name="Illustrator" literal="ill"
	 * @generated
	 * @ordered
	 */
	public static final int ILLUSTRATOR_VALUE = 0;

	/**
	 * The '<em><b>Illuminator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Illuminator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ILLUMINATOR
	 * @model name="Illuminator" literal="ilu"
	 * @generated
	 * @ordered
	 */
	public static final int ILLUMINATOR_VALUE = 0;

	/**
	 * The '<em><b>Inscriber</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Inscriber</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #INSCRIBER
	 * @model name="Inscriber" literal="ins"
	 * @generated
	 * @ordered
	 */
	public static final int INSCRIBER_VALUE = 0;

	/**
	 * The '<em><b>Inventor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Inventor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #INVENTOR
	 * @model name="Inventor" literal="inv"
	 * @generated
	 * @ordered
	 */
	public static final int INVENTOR_VALUE = 0;

	/**
	 * The '<em><b>Instrumentalist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Instrumentalist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #INSTRUMENTALIST
	 * @model name="Instrumentalist" literal="itr"
	 * @generated
	 * @ordered
	 */
	public static final int INSTRUMENTALIST_VALUE = 0;

	/**
	 * The '<em><b>Interviewee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Interviewee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #INTERVIEWEE
	 * @model name="Interviewee" literal="ive"
	 * @generated
	 * @ordered
	 */
	public static final int INTERVIEWEE_VALUE = 0;

	/**
	 * The '<em><b>Interviewer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Interviewer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #INTERVIEWER
	 * @model name="Interviewer" literal="ivr"
	 * @generated
	 * @ordered
	 */
	public static final int INTERVIEWER_VALUE = 0;

	/**
	 * The '<em><b>Laboratory</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Laboratory</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LABORATORY
	 * @model name="Laboratory" literal="lbr"
	 * @generated
	 * @ordered
	 */
	public static final int LABORATORY_VALUE = 0;

	/**
	 * The '<em><b>Librettist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Librettist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LIBRETTIST
	 * @model name="Librettist" literal="lbt"
	 * @generated
	 * @ordered
	 */
	public static final int LIBRETTIST_VALUE = 0;

	/**
	 * The '<em><b>Laboratory director</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Laboratory director</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LABORATORY_DIRECTOR
	 * @model name="Laboratory_director" literal="ldr"
	 * @generated
	 * @ordered
	 */
	public static final int LABORATORY_DIRECTOR_VALUE = 0;

	/**
	 * The '<em><b>Lead</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Lead</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LEAD
	 * @model name="Lead" literal="led"
	 * @generated
	 * @ordered
	 */
	public static final int LEAD_VALUE = 0;

	/**
	 * The '<em><b>Libelee appellee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Libelee appellee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LIBELEE_APPELLEE
	 * @model name="Libelee_appellee" literal="lee"
	 * @generated
	 * @ordered
	 */
	public static final int LIBELEE_APPELLEE_VALUE = 0;

	/**
	 * The '<em><b>Libelee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Libelee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LIBELEE
	 * @model name="Libelee" literal="lel"
	 * @generated
	 * @ordered
	 */
	public static final int LIBELEE_VALUE = 0;

	/**
	 * The '<em><b>Lender</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Lender</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LENDER
	 * @model name="Lender" literal="len"
	 * @generated
	 * @ordered
	 */
	public static final int LENDER_VALUE = 0;

	/**
	 * The '<em><b>Libelee appellant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Libelee appellant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LIBELEE_APPELLANT
	 * @model name="Libelee_appellant" literal="let"
	 * @generated
	 * @ordered
	 */
	public static final int LIBELEE_APPELLANT_VALUE = 0;

	/**
	 * The '<em><b>Lighting designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Lighting designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LIGHTING_DESIGNER
	 * @model name="Lighting_designer" literal="lgd"
	 * @generated
	 * @ordered
	 */
	public static final int LIGHTING_DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Libelant appellee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Libelant appellee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LIBELANT_APPELLEE
	 * @model name="Libelant_appellee" literal="lie"
	 * @generated
	 * @ordered
	 */
	public static final int LIBELANT_APPELLEE_VALUE = 0;

	/**
	 * The '<em><b>Libelant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Libelant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LIBELANT
	 * @model name="Libelant" literal="lil"
	 * @generated
	 * @ordered
	 */
	public static final int LIBELANT_VALUE = 0;

	/**
	 * The '<em><b>Libelant appellant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Libelant appellant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LIBELANT_APPELLANT
	 * @model name="Libelant_appellant" literal="lit"
	 * @generated
	 * @ordered
	 */
	public static final int LIBELANT_APPELLANT_VALUE = 0;

	/**
	 * The '<em><b>Landscape architect</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Landscape architect</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LANDSCAPE_ARCHITECT
	 * @model name="Landscape_architect" literal="lsa"
	 * @generated
	 * @ordered
	 */
	public static final int LANDSCAPE_ARCHITECT_VALUE = 0;

	/**
	 * The '<em><b>Licensee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Licensee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LICENSEE
	 * @model name="Licensee" literal="lse"
	 * @generated
	 * @ordered
	 */
	public static final int LICENSEE_VALUE = 0;

	/**
	 * The '<em><b>Licensor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Licensor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LICENSOR
	 * @model name="Licensor" literal="lso"
	 * @generated
	 * @ordered
	 */
	public static final int LICENSOR_VALUE = 0;

	/**
	 * The '<em><b>Lithographer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Lithographer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LITHOGRAPHER
	 * @model name="Lithographer" literal="ltg"
	 * @generated
	 * @ordered
	 */
	public static final int LITHOGRAPHER_VALUE = 0;

	/**
	 * The '<em><b>Lyricist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Lyricist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LYRICIST
	 * @model name="Lyricist" literal="lyr"
	 * @generated
	 * @ordered
	 */
	public static final int LYRICIST_VALUE = 0;

	/**
	 * The '<em><b>Music copyist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Music copyist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MUSIC_COPYIST
	 * @model name="Music_copyist" literal="mcp"
	 * @generated
	 * @ordered
	 */
	public static final int MUSIC_COPYIST_VALUE = 0;

	/**
	 * The '<em><b>Manufacture place</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Manufacture place</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MANUFACTURE_PLACE
	 * @model name="Manufacture_place" literal="mfp"
	 * @generated
	 * @ordered
	 */
	public static final int MANUFACTURE_PLACE_VALUE = 0;

	/**
	 * The '<em><b>Manufacturer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Manufacturer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MANUFACTURER
	 * @model name="Manufacturer" literal="mfr"
	 * @generated
	 * @ordered
	 */
	public static final int MANUFACTURER_VALUE = 0;

	/**
	 * The '<em><b>Metadata contact</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Metadata contact</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #METADATA_CONTACT
	 * @model name="Metadata_contact" literal="mdc"
	 * @generated
	 * @ordered
	 */
	public static final int METADATA_CONTACT_VALUE = 0;

	/**
	 * The '<em><b>Moderator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Moderator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MODERATOR
	 * @model name="Moderator" literal="mod"
	 * @generated
	 * @ordered
	 */
	public static final int MODERATOR_VALUE = 0;

	/**
	 * The '<em><b>Monitor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Monitor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MONITOR
	 * @model name="Monitor" literal="mon"
	 * @generated
	 * @ordered
	 */
	public static final int MONITOR_VALUE = 0;

	/**
	 * The '<em><b>Marbler</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Marbler</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MARBLER
	 * @model name="Marbler" literal="mrb"
	 * @generated
	 * @ordered
	 */
	public static final int MARBLER_VALUE = 0;

	/**
	 * The '<em><b>Markup editor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Markup editor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MARKUP_EDITOR
	 * @model name="Markup_editor" literal="mrk"
	 * @generated
	 * @ordered
	 */
	public static final int MARKUP_EDITOR_VALUE = 0;

	/**
	 * The '<em><b>Musical director</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Musical director</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MUSICAL_DIRECTOR
	 * @model name="Musical_director" literal="msd"
	 * @generated
	 * @ordered
	 */
	public static final int MUSICAL_DIRECTOR_VALUE = 0;

	/**
	 * The '<em><b>Metal engraver</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Metal engraver</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #METAL_ENGRAVER
	 * @model name="Metal_engraver" literal="mte"
	 * @generated
	 * @ordered
	 */
	public static final int METAL_ENGRAVER_VALUE = 0;

	/**
	 * The '<em><b>Musician</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Musician</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MUSICIAN
	 * @model name="Musician" literal="mus"
	 * @generated
	 * @ordered
	 */
	public static final int MUSICIAN_VALUE = 0;

	/**
	 * The '<em><b>Narrator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Narrator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #NARRATOR
	 * @model name="Narrator" literal="nrt"
	 * @generated
	 * @ordered
	 */
	public static final int NARRATOR_VALUE = 0;

	/**
	 * The '<em><b>Opponent</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Opponent</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #OPPONENT
	 * @model name="Opponent" literal="opn"
	 * @generated
	 * @ordered
	 */
	public static final int OPPONENT_VALUE = 0;

	/**
	 * The '<em><b>Originator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Originator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ORIGINATOR
	 * @model name="Originator" literal="org"
	 * @generated
	 * @ordered
	 */
	public static final int ORIGINATOR_VALUE = 0;

	/**
	 * The '<em><b>Organizer of meeting</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Organizer of meeting</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ORGANIZER_OF_MEETING
	 * @model name="Organizer_of_meeting" literal="orm"
	 * @generated
	 * @ordered
	 */
	public static final int ORGANIZER_OF_MEETING_VALUE = 0;

	/**
	 * The '<em><b>Other</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Other</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #OTHER
	 * @model name="Other" literal="oth"
	 * @generated
	 * @ordered
	 */
	public static final int OTHER_VALUE = 0;

	/**
	 * The '<em><b>Owner</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Owner</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #OWNER
	 * @model name="Owner" literal="own"
	 * @generated
	 * @ordered
	 */
	public static final int OWNER_VALUE = 0;

	/**
	 * The '<em><b>Patron</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Patron</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PATRON
	 * @model name="Patron" literal="pat"
	 * @generated
	 * @ordered
	 */
	public static final int PATRON_VALUE = 0;

	/**
	 * The '<em><b>Publishing director</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Publishing director</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PUBLISHING_DIRECTOR
	 * @model name="Publishing_director" literal="pbd"
	 * @generated
	 * @ordered
	 */
	public static final int PUBLISHING_DIRECTOR_VALUE = 0;

	/**
	 * The '<em><b>Publisher</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Publisher</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PUBLISHER
	 * @model name="Publisher" literal="pbl"
	 * @generated
	 * @ordered
	 */
	public static final int PUBLISHER_VALUE = 0;

	/**
	 * The '<em><b>Project director</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Project director</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PROJECT_DIRECTOR
	 * @model name="Project_director" literal="pdr"
	 * @generated
	 * @ordered
	 */
	public static final int PROJECT_DIRECTOR_VALUE = 0;

	/**
	 * The '<em><b>Proofreader</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Proofreader</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PROOFREADER
	 * @model name="Proofreader" literal="pfr"
	 * @generated
	 * @ordered
	 */
	public static final int PROOFREADER_VALUE = 0;

	/**
	 * The '<em><b>Photographer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Photographer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PHOTOGRAPHER
	 * @model name="Photographer" literal="pht"
	 * @generated
	 * @ordered
	 */
	public static final int PHOTOGRAPHER_VALUE = 0;

	/**
	 * The '<em><b>Platemaker</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Platemaker</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PLATEMAKER
	 * @model name="Platemaker" literal="plt"
	 * @generated
	 * @ordered
	 */
	public static final int PLATEMAKER_VALUE = 0;

	/**
	 * The '<em><b>Permitting agency</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Permitting agency</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PERMITTING_AGENCY
	 * @model name="Permitting_agency" literal="pma"
	 * @generated
	 * @ordered
	 */
	public static final int PERMITTING_AGENCY_VALUE = 0;

	/**
	 * The '<em><b>Production manager</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Production manager</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PRODUCTION_MANAGER
	 * @model name="Production_manager" literal="pmn"
	 * @generated
	 * @ordered
	 */
	public static final int PRODUCTION_MANAGER_VALUE = 0;

	/**
	 * The '<em><b>Printer of plates</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Printer of plates</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PRINTER_OF_PLATES
	 * @model name="Printer_of_plates" literal="pop"
	 * @generated
	 * @ordered
	 */
	public static final int PRINTER_OF_PLATES_VALUE = 0;

	/**
	 * The '<em><b>Papermaker</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Papermaker</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PAPERMAKER
	 * @model name="Papermaker" literal="ppm"
	 * @generated
	 * @ordered
	 */
	public static final int PAPERMAKER_VALUE = 0;

	/**
	 * The '<em><b>Puppeteer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Puppeteer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PUPPETEER
	 * @model name="Puppeteer" literal="ppt"
	 * @generated
	 * @ordered
	 */
	public static final int PUPPETEER_VALUE = 0;

	/**
	 * The '<em><b>Process contact</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Process contact</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PROCESS_CONTACT
	 * @model name="Process_contact" literal="prc"
	 * @generated
	 * @ordered
	 */
	public static final int PROCESS_CONTACT_VALUE = 0;

	/**
	 * The '<em><b>Production personnel</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Production personnel</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PRODUCTION_PERSONNEL
	 * @model name="Production_personnel" literal="prd"
	 * @generated
	 * @ordered
	 */
	public static final int PRODUCTION_PERSONNEL_VALUE = 0;

	/**
	 * The '<em><b>Performer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Performer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PERFORMER
	 * @model name="Performer" literal="prf"
	 * @generated
	 * @ordered
	 */
	public static final int PERFORMER_VALUE = 0;

	/**
	 * The '<em><b>Programmer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Programmer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PROGRAMMER
	 * @model name="Programmer" literal="prg"
	 * @generated
	 * @ordered
	 */
	public static final int PROGRAMMER_VALUE = 0;

	/**
	 * The '<em><b>Printmaker</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Printmaker</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PRINTMAKER
	 * @model name="Printmaker" literal="prm"
	 * @generated
	 * @ordered
	 */
	public static final int PRINTMAKER_VALUE = 0;

	/**
	 * The '<em><b>Producer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Producer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PRODUCER
	 * @model name="Producer" literal="pro"
	 * @generated
	 * @ordered
	 */
	public static final int PRODUCER_VALUE = 0;

	/**
	 * The '<em><b>Production place</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Production place</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PRODUCTION_PLACE
	 * @model name="Production_place" literal="prp"
	 * @generated
	 * @ordered
	 */
	public static final int PRODUCTION_PLACE_VALUE = 0;

	/**
	 * The '<em><b>Printer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Printer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PRINTER
	 * @model name="Printer" literal="prt"
	 * @generated
	 * @ordered
	 */
	public static final int PRINTER_VALUE = 0;

	/**
	 * The '<em><b>Patent applicant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Patent applicant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PATENT_APPLICANT
	 * @model name="Patent_applicant" literal="pta"
	 * @generated
	 * @ordered
	 */
	public static final int PATENT_APPLICANT_VALUE = 0;

	/**
	 * The '<em><b>Plaintiff appellee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Plaintiff appellee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PLAINTIFF_APPELLEE
	 * @model name="Plaintiff_appellee" literal="pte"
	 * @generated
	 * @ordered
	 */
	public static final int PLAINTIFF_APPELLEE_VALUE = 0;

	/**
	 * The '<em><b>Plaintiff</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Plaintiff</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PLAINTIFF
	 * @model name="Plaintiff" literal="ptf"
	 * @generated
	 * @ordered
	 */
	public static final int PLAINTIFF_VALUE = 0;

	/**
	 * The '<em><b>Patent holder</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Patent holder</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PATENT_HOLDER
	 * @model name="Patent_holder" literal="pth"
	 * @generated
	 * @ordered
	 */
	public static final int PATENT_HOLDER_VALUE = 0;

	/**
	 * The '<em><b>Plaintiff appellant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Plaintiff appellant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PLAINTIFF_APPELLANT
	 * @model name="Plaintiff_appellant" literal="ptt"
	 * @generated
	 * @ordered
	 */
	public static final int PLAINTIFF_APPELLANT_VALUE = 0;

	/**
	 * The '<em><b>Publication place</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Publication place</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PUBLICATION_PLACE
	 * @model name="Publication_place" literal="pup"
	 * @generated
	 * @ordered
	 */
	public static final int PUBLICATION_PLACE_VALUE = 0;

	/**
	 * The '<em><b>Rubricator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Rubricator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RUBRICATOR
	 * @model name="Rubricator" literal="rbr"
	 * @generated
	 * @ordered
	 */
	public static final int RUBRICATOR_VALUE = 0;

	/**
	 * The '<em><b>Recording engineer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Recording engineer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RECORDING_ENGINEER
	 * @model name="Recording_engineer" literal="rce"
	 * @generated
	 * @ordered
	 */
	public static final int RECORDING_ENGINEER_VALUE = 0;

	/**
	 * The '<em><b>Recipient</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Recipient</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RECIPIENT
	 * @model name="Recipient" literal="rcp"
	 * @generated
	 * @ordered
	 */
	public static final int RECIPIENT_VALUE = 0;

	/**
	 * The '<em><b>Redactor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Redactor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #REDACTOR
	 * @model name="Redactor" literal="red"
	 * @generated
	 * @ordered
	 */
	public static final int REDACTOR_VALUE = 0;

	/**
	 * The '<em><b>Renderer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Renderer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RENDERER
	 * @model name="Renderer" literal="ren"
	 * @generated
	 * @ordered
	 */
	public static final int RENDERER_VALUE = 0;

	/**
	 * The '<em><b>Researcher</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Researcher</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RESEARCHER
	 * @model name="Researcher" literal="res"
	 * @generated
	 * @ordered
	 */
	public static final int RESEARCHER_VALUE = 0;

	/**
	 * The '<em><b>Reviewer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Reviewer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #REVIEWER
	 * @model name="Reviewer" literal="rev"
	 * @generated
	 * @ordered
	 */
	public static final int REVIEWER_VALUE = 0;

	/**
	 * The '<em><b>Repository</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Repository</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #REPOSITORY
	 * @model name="Repository" literal="rps"
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY_VALUE = 0;

	/**
	 * The '<em><b>Reporter</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Reporter</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #REPORTER
	 * @model name="Reporter" literal="rpt"
	 * @generated
	 * @ordered
	 */
	public static final int REPORTER_VALUE = 0;

	/**
	 * The '<em><b>Responsible party</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Responsible party</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RESPONSIBLE_PARTY
	 * @model name="Responsible_party" literal="rpy"
	 * @generated
	 * @ordered
	 */
	public static final int RESPONSIBLE_PARTY_VALUE = 0;

	/**
	 * The '<em><b>Respondent appellee</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Respondent appellee</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RESPONDENT_APPELLEE
	 * @model name="Respondent_appellee" literal="rse"
	 * @generated
	 * @ordered
	 */
	public static final int RESPONDENT_APPELLEE_VALUE = 0;

	/**
	 * The '<em><b>Restager</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Restager</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RESTAGER
	 * @model name="Restager" literal="rsg"
	 * @generated
	 * @ordered
	 */
	public static final int RESTAGER_VALUE = 0;

	/**
	 * The '<em><b>Respondent</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Respondent</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RESPONDENT
	 * @model name="Respondent" literal="rsp"
	 * @generated
	 * @ordered
	 */
	public static final int RESPONDENT_VALUE = 0;

	/**
	 * The '<em><b>Respondent appellant</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Respondent appellant</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RESPONDENT_APPELLANT
	 * @model name="Respondent_appellant" literal="rst"
	 * @generated
	 * @ordered
	 */
	public static final int RESPONDENT_APPELLANT_VALUE = 0;

	/**
	 * The '<em><b>Research team head</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Research team head</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RESEARCH_TEAM_HEAD
	 * @model name="Research_team_head" literal="rth"
	 * @generated
	 * @ordered
	 */
	public static final int RESEARCH_TEAM_HEAD_VALUE = 0;

	/**
	 * The '<em><b>Research team member</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Research team member</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RESEARCH_TEAM_MEMBER
	 * @model name="Research_team_member" literal="rtm"
	 * @generated
	 * @ordered
	 */
	public static final int RESEARCH_TEAM_MEMBER_VALUE = 0;

	/**
	 * The '<em><b>Scientific advisor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Scientific advisor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SCIENTIFIC_ADVISOR
	 * @model name="Scientific_advisor" literal="sad"
	 * @generated
	 * @ordered
	 */
	public static final int SCIENTIFIC_ADVISOR_VALUE = 0;

	/**
	 * The '<em><b>Scenarist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Scenarist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SCENARIST
	 * @model name="Scenarist" literal="sce"
	 * @generated
	 * @ordered
	 */
	public static final int SCENARIST_VALUE = 0;

	/**
	 * The '<em><b>Sculptor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Sculptor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SCULPTOR
	 * @model name="Sculptor" literal="scl"
	 * @generated
	 * @ordered
	 */
	public static final int SCULPTOR_VALUE = 0;

	/**
	 * The '<em><b>Scribe</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Scribe</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SCRIBE
	 * @model name="Scribe" literal="scr"
	 * @generated
	 * @ordered
	 */
	public static final int SCRIBE_VALUE = 0;

	/**
	 * The '<em><b>Sound designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Sound designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SOUND_DESIGNER
	 * @model name="Sound_designer" literal="sds"
	 * @generated
	 * @ordered
	 */
	public static final int SOUND_DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Secretary</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Secretary</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SECRETARY
	 * @model name="Secretary" literal="sec"
	 * @generated
	 * @ordered
	 */
	public static final int SECRETARY_VALUE = 0;

	/**
	 * The '<em><b>Signer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Signer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SIGNER
	 * @model name="Signer" literal="sgn"
	 * @generated
	 * @ordered
	 */
	public static final int SIGNER_VALUE = 0;

	/**
	 * The '<em><b>Supporting host</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Supporting host</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SUPPORTING_HOST
	 * @model name="Supporting_host" literal="sht"
	 * @generated
	 * @ordered
	 */
	public static final int SUPPORTING_HOST_VALUE = 0;

	/**
	 * The '<em><b>Singer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Singer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SINGER
	 * @model name="Singer" literal="sng"
	 * @generated
	 * @ordered
	 */
	public static final int SINGER_VALUE = 0;

	/**
	 * The '<em><b>Speaker</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Speaker</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SPEAKER
	 * @model name="Speaker" literal="spk"
	 * @generated
	 * @ordered
	 */
	public static final int SPEAKER_VALUE = 0;

	/**
	 * The '<em><b>Sponsor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Sponsor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SPONSOR
	 * @model name="Sponsor" literal="spn"
	 * @generated
	 * @ordered
	 */
	public static final int SPONSOR_VALUE = 0;

	/**
	 * The '<em><b>Second party</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Second party</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SECOND_PARTY
	 * @model name="Second_party" literal="spy"
	 * @generated
	 * @ordered
	 */
	public static final int SECOND_PARTY_VALUE = 0;

	/**
	 * The '<em><b>Surveyor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Surveyor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SURVEYOR
	 * @model name="Surveyor" literal="srv"
	 * @generated
	 * @ordered
	 */
	public static final int SURVEYOR_VALUE = 0;

	/**
	 * The '<em><b>Set designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Set designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SET_DESIGNER
	 * @model name="Set_designer" literal="std"
	 * @generated
	 * @ordered
	 */
	public static final int SET_DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Storyteller</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Storyteller</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #STORYTELLER
	 * @model name="Storyteller" literal="stl"
	 * @generated
	 * @ordered
	 */
	public static final int STORYTELLER_VALUE = 0;

	/**
	 * The '<em><b>Stage manager</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Stage manager</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #STAGE_MANAGER
	 * @model name="Stage_manager" literal="stm"
	 * @generated
	 * @ordered
	 */
	public static final int STAGE_MANAGER_VALUE = 0;

	/**
	 * The '<em><b>Standards body</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Standards body</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #STANDARDS_BODY
	 * @model name="Standards_body" literal="stn"
	 * @generated
	 * @ordered
	 */
	public static final int STANDARDS_BODY_VALUE = 0;

	/**
	 * The '<em><b>Stereotyper</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Stereotyper</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #STEREOTYPER
	 * @model name="Stereotyper" literal="str"
	 * @generated
	 * @ordered
	 */
	public static final int STEREOTYPER_VALUE = 0;

	/**
	 * The '<em><b>Technical director</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Technical director</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TECHNICAL_DIRECTOR
	 * @model name="Technical_director" literal="tcd"
	 * @generated
	 * @ordered
	 */
	public static final int TECHNICAL_DIRECTOR_VALUE = 0;

	/**
	 * The '<em><b>Teacher</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Teacher</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TEACHER
	 * @model name="Teacher" literal="tch"
	 * @generated
	 * @ordered
	 */
	public static final int TEACHER_VALUE = 0;

	/**
	 * The '<em><b>Thesis advisor</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Thesis advisor</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #THESIS_ADVISOR
	 * @model name="Thesis_advisor" literal="ths"
	 * @generated
	 * @ordered
	 */
	public static final int THESIS_ADVISOR_VALUE = 0;

	/**
	 * The '<em><b>Transcriber</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Transcriber</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TRANSCRIBER
	 * @model name="Transcriber" literal="trc"
	 * @generated
	 * @ordered
	 */
	public static final int TRANSCRIBER_VALUE = 0;

	/**
	 * The '<em><b>Translator</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Translator</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TRANSLATOR
	 * @model name="Translator" literal="trl"
	 * @generated
	 * @ordered
	 */
	public static final int TRANSLATOR_VALUE = 0;

	/**
	 * The '<em><b>Type designer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Type designer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TYPE_DESIGNER
	 * @model name="Type_designer" literal="tyd"
	 * @generated
	 * @ordered
	 */
	public static final int TYPE_DESIGNER_VALUE = 0;

	/**
	 * The '<em><b>Typographer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Typographer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TYPOGRAPHER
	 * @model name="Typographer" literal="tyg"
	 * @generated
	 * @ordered
	 */
	public static final int TYPOGRAPHER_VALUE = 0;

	/**
	 * The '<em><b>University place</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>University place</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #UNIVERSITY_PLACE
	 * @model name="University_place" literal="uvp"
	 * @generated
	 * @ordered
	 */
	public static final int UNIVERSITY_PLACE_VALUE = 0;

	/**
	 * The '<em><b>Videographer</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Videographer</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #VIDEOGRAPHER
	 * @model name="Videographer" literal="vdg"
	 * @generated
	 * @ordered
	 */
	public static final int VIDEOGRAPHER_VALUE = 0;

	/**
	 * The '<em><b>Vocalist</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Vocalist</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #VOCALIST
	 * @model name="Vocalist" literal="voc"
	 * @generated
	 * @ordered
	 */
	public static final int VOCALIST_VALUE = 0;

	/**
	 * The '<em><b>Writer of accompanying material</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Writer of accompanying material</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #WRITER_OF_ACCOMPANYING_MATERIAL
	 * @model name="Writer_of_accompanying_material" literal="wam"
	 * @generated
	 * @ordered
	 */
	public static final int WRITER_OF_ACCOMPANYING_MATERIAL_VALUE = 0;

	/**
	 * The '<em><b>Woodcutter</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Woodcutter</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #WOODCUTTER
	 * @model name="Woodcutter" literal="wdc"
	 * @generated
	 * @ordered
	 */
	public static final int WOODCUTTER_VALUE = 0;

	/**
	 * The '<em><b>Wood engraver</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Wood engraver</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #WOOD_ENGRAVER
	 * @model name="Wood_engraver" literal="wde"
	 * @generated
	 * @ordered
	 */
	public static final int WOOD_ENGRAVER_VALUE = 0;

	/**
	 * The '<em><b>Witness</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Witness</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #WITNESS
	 * @model name="Witness" literal="wit"
	 * @generated
	 * @ordered
	 */
	public static final int WITNESS_VALUE = 0;

	/**
	 * An array of all the '<em><b>Role</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final Role[] VALUES_ARRAY =
		new Role[] {
			ART_COPYIST,
			ACTOR,
			ADAPTER,
			AUTHOR_OF_AFTERWORD_COLOPHON_ETC,
			ANALYST,
			ANIMATOR,
			ANNOTATOR,
			BIBLIOGRAPHIC_ANTECEDENT,
			APPLICANT,
			AUTHOR_IN_QUOTATIONS_OR_TEXT_ABSTRACTS,
			ARCHITECT,
			ARTISTIC_DIRECTOR,
			ARRANGER,
			ARTIST,
			ASSIGNEE,
			ASSOCIATED_NAME,
			ATTRIBUTED_NAME,
			AUCTIONEER,
			AUTHOR_OF_DIALOG,
			AUTHOR_OF_INTRODUCTION,
			AUTHOR_OF_SCREENPLAY,
			AUTHOR,
			BINDING_DESIGNER,
			BOOKJACKET_DESIGNER,
			BOOK_DESIGNER,
			BOOK_PRODUCER,
			BLURB_WRITER,
			BINDER,
			BOOKPLATE_DESIGNER,
			BOOKSELLER,
			CONCEPTOR,
			CHOREOGRAPHER,
			COLLABORATOR,
			CLIENT,
			CALLIGRAPHER,
			COLORIST,
			COLLOTYPER,
			COMMENTATOR,
			COMPOSER,
			COMPOSITOR,
			CINEMATOGRAPHER,
			CONDUCTOR,
			CENSOR,
			CONTESTANT_APPELLEE,
			COLLECTOR,
			COMPILER,
			CONSERVATOR,
			CONTESTANT,
			CONTESTANT_APPELLANT,
			COVER_DESIGNER,
			COPYRIGHT_CLAIMANT,
			COMPLAINANT_APPELLEE,
			COPYRIGHT_HOLDER,
			COMPLAINANT,
			COMPLAINANT_APPELLANT,
			CREATOR,
			CORRESPONDENT,
			CORRECTOR,
			CONSULTANT,
			CONSULTANT_TO_APROJECT,
			COSTUME_DESIGNER,
			CONTRIBUTOR,
			CONTESTEE_APPELLEE,
			CARTOGRAPHER,
			CONTRACTOR,
			CONTESTEE,
			CONTESTEE_APPELLANT,
			CURATOR,
			COMMENTATOR_FOR_WRITTEN_TEXT,
			DEFENDANT,
			DEFENDANT_APPELLEE,
			DEFENDANT_APPELLANT,
			DEGREE_GRANTOR,
			DISSERTANT,
			DELINEATOR,
			DANCER,
			DONOR,
			DISTRIBUTION_PLACE,
			DEPICTED,
			DEPOSITOR,
			DRAFTSMAN,
			DIRECTOR,
			DESIGNER,
			DISTRIBUTOR,
			DATA_CONTRIBUTOR,
			DEDICATEE,
			DATA_MANAGER,
			DEDICATOR,
			DUBIOUS_AUTHOR,
			EDITOR,
			ENGRAVER,
			ELECTRICIAN,
			ELECTROTYPER,
			ENGINEER,
			ETCHER,
			EVENT_PLACE,
			EXPERT,
			FACSIMILIST,
			FIELD_DIRECTOR,
			FILM_EDITOR,
			FORMER_OWNER,
			FIRST_PARTY,
			FUNDER,
			FORGER,
			GEOGRAPHIC_INFORMATION_SPECIALIST,
			GRAPHIC_TECHNICIAN,
			HONOREE,
			HOST,
			ILLUSTRATOR,
			ILLUMINATOR,
			INSCRIBER,
			INVENTOR,
			INSTRUMENTALIST,
			INTERVIEWEE,
			INTERVIEWER,
			LABORATORY,
			LIBRETTIST,
			LABORATORY_DIRECTOR,
			LEAD,
			LIBELEE_APPELLEE,
			LIBELEE,
			LENDER,
			LIBELEE_APPELLANT,
			LIGHTING_DESIGNER,
			LIBELANT_APPELLEE,
			LIBELANT,
			LIBELANT_APPELLANT,
			LANDSCAPE_ARCHITECT,
			LICENSEE,
			LICENSOR,
			LITHOGRAPHER,
			LYRICIST,
			MUSIC_COPYIST,
			MANUFACTURE_PLACE,
			MANUFACTURER,
			METADATA_CONTACT,
			MODERATOR,
			MONITOR,
			MARBLER,
			MARKUP_EDITOR,
			MUSICAL_DIRECTOR,
			METAL_ENGRAVER,
			MUSICIAN,
			NARRATOR,
			OPPONENT,
			ORIGINATOR,
			ORGANIZER_OF_MEETING,
			OTHER,
			OWNER,
			PATRON,
			PUBLISHING_DIRECTOR,
			PUBLISHER,
			PROJECT_DIRECTOR,
			PROOFREADER,
			PHOTOGRAPHER,
			PLATEMAKER,
			PERMITTING_AGENCY,
			PRODUCTION_MANAGER,
			PRINTER_OF_PLATES,
			PAPERMAKER,
			PUPPETEER,
			PROCESS_CONTACT,
			PRODUCTION_PERSONNEL,
			PERFORMER,
			PROGRAMMER,
			PRINTMAKER,
			PRODUCER,
			PRODUCTION_PLACE,
			PRINTER,
			PATENT_APPLICANT,
			PLAINTIFF_APPELLEE,
			PLAINTIFF,
			PATENT_HOLDER,
			PLAINTIFF_APPELLANT,
			PUBLICATION_PLACE,
			RUBRICATOR,
			RECORDING_ENGINEER,
			RECIPIENT,
			REDACTOR,
			RENDERER,
			RESEARCHER,
			REVIEWER,
			REPOSITORY,
			REPORTER,
			RESPONSIBLE_PARTY,
			RESPONDENT_APPELLEE,
			RESTAGER,
			RESPONDENT,
			RESPONDENT_APPELLANT,
			RESEARCH_TEAM_HEAD,
			RESEARCH_TEAM_MEMBER,
			SCIENTIFIC_ADVISOR,
			SCENARIST,
			SCULPTOR,
			SCRIBE,
			SOUND_DESIGNER,
			SECRETARY,
			SIGNER,
			SUPPORTING_HOST,
			SINGER,
			SPEAKER,
			SPONSOR,
			SECOND_PARTY,
			SURVEYOR,
			SET_DESIGNER,
			STORYTELLER,
			STAGE_MANAGER,
			STANDARDS_BODY,
			STEREOTYPER,
			TECHNICAL_DIRECTOR,
			TEACHER,
			THESIS_ADVISOR,
			TRANSCRIBER,
			TRANSLATOR,
			TYPE_DESIGNER,
			TYPOGRAPHER,
			UNIVERSITY_PLACE,
			VIDEOGRAPHER,
			VOCALIST,
			WRITER_OF_ACCOMPANYING_MATERIAL,
			WOODCUTTER,
			WOOD_ENGRAVER,
			WITNESS,
		};

	/**
	 * A public read-only list of all the '<em><b>Role</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<Role> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Role</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static Role get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			Role result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Role</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static Role getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			Role result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Role</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static Role get(int value) {
		switch (value) {
			case ART_COPYIST_VALUE: return ART_COPYIST;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private Role(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //Role
