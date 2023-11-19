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
 * A representation of the literals of the enumeration '<em><b>Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getType()
 * @model
 * @generated
 */
public enum Type implements Enumerator {
	/**
	 * The '<em><b>Cover</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COVER_VALUE
	 * @generated
	 * @ordered
	 */
	COVER(1, "Cover", "cover"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Title</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TITLE_VALUE
	 * @generated
	 * @ordered
	 */
	TITLE(2, "Title", "title-page"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>TOC</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TOC_VALUE
	 * @generated
	 * @ordered
	 */
	TOC(3, "TOC", "toc"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Index</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #INDEX_VALUE
	 * @generated
	 * @ordered
	 */
	INDEX(4, "Index", "index"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Glossary</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #GLOSSARY_VALUE
	 * @generated
	 * @ordered
	 */
	GLOSSARY(5, "Glossary", "glossary"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Acknowledgements</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ACKNOWLEDGEMENTS_VALUE
	 * @generated
	 * @ordered
	 */
	ACKNOWLEDGEMENTS(6, "Acknowledgements", "acknowledgements"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Bibliography</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BIBLIOGRAPHY_VALUE
	 * @generated
	 * @ordered
	 */
	BIBLIOGRAPHY(7, "Bibliography", "bibliography"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Colophon</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COLOPHON_VALUE
	 * @generated
	 * @ordered
	 */
	COLOPHON(8, "Colophon", "colophon"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Copyright</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #COPYRIGHT_VALUE
	 * @generated
	 * @ordered
	 */
	COPYRIGHT(9, "Copyright", "copyright-page"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Dedication</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEDICATION_VALUE
	 * @generated
	 * @ordered
	 */
	DEDICATION(10, "Dedication", "dedication"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Epigraph</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #EPIGRAPH_VALUE
	 * @generated
	 * @ordered
	 */
	EPIGRAPH(11, "Epigraph", "epigraph"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Foreword</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FOREWORD_VALUE
	 * @generated
	 * @ordered
	 */
	FOREWORD(12, "Foreword", "foreword"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Illustrations</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ILLUSTRATIONS_VALUE
	 * @generated
	 * @ordered
	 */
	ILLUSTRATIONS(13, "Illustrations", "loi"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Tables</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TABLES_VALUE
	 * @generated
	 * @ordered
	 */
	TABLES(14, "Tables", "lot"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Notes</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NOTES_VALUE
	 * @generated
	 * @ordered
	 */
	NOTES(15, "Notes", "notes"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Preface</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PREFACE_VALUE
	 * @generated
	 * @ordered
	 */
	PREFACE(16, "Preface", "preface"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Text</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TEXT_VALUE
	 * @generated
	 * @ordered
	 */
	TEXT(17, "Text", "text"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Cover</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Cover</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COVER
	 * @model name="Cover" literal="cover"
	 * @generated
	 * @ordered
	 */
	public static final int COVER_VALUE = 1;

	/**
	 * The '<em><b>Title</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Title</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TITLE
	 * @model name="Title" literal="title-page"
	 * @generated
	 * @ordered
	 */
	public static final int TITLE_VALUE = 2;

	/**
	 * The '<em><b>TOC</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>TOC</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TOC
	 * @model literal="toc"
	 * @generated
	 * @ordered
	 */
	public static final int TOC_VALUE = 3;

	/**
	 * The '<em><b>Index</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Index</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #INDEX
	 * @model name="Index" literal="index"
	 * @generated
	 * @ordered
	 */
	public static final int INDEX_VALUE = 4;

	/**
	 * The '<em><b>Glossary</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Glossary</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #GLOSSARY
	 * @model name="Glossary" literal="glossary"
	 * @generated
	 * @ordered
	 */
	public static final int GLOSSARY_VALUE = 5;

	/**
	 * The '<em><b>Acknowledgements</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Acknowledgements</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ACKNOWLEDGEMENTS
	 * @model name="Acknowledgements" literal="acknowledgements"
	 * @generated
	 * @ordered
	 */
	public static final int ACKNOWLEDGEMENTS_VALUE = 6;

	/**
	 * The '<em><b>Bibliography</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Bibliography</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BIBLIOGRAPHY
	 * @model name="Bibliography" literal="bibliography"
	 * @generated
	 * @ordered
	 */
	public static final int BIBLIOGRAPHY_VALUE = 7;

	/**
	 * The '<em><b>Colophon</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Colophon</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COLOPHON
	 * @model name="Colophon" literal="colophon"
	 * @generated
	 * @ordered
	 */
	public static final int COLOPHON_VALUE = 8;

	/**
	 * The '<em><b>Copyright</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Copyright</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #COPYRIGHT
	 * @model name="Copyright" literal="copyright-page"
	 * @generated
	 * @ordered
	 */
	public static final int COPYRIGHT_VALUE = 9;

	/**
	 * The '<em><b>Dedication</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Dedication</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEDICATION
	 * @model name="Dedication" literal="dedication"
	 * @generated
	 * @ordered
	 */
	public static final int DEDICATION_VALUE = 10;

	/**
	 * The '<em><b>Epigraph</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Epigraph</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #EPIGRAPH
	 * @model name="Epigraph" literal="epigraph"
	 * @generated
	 * @ordered
	 */
	public static final int EPIGRAPH_VALUE = 11;

	/**
	 * The '<em><b>Foreword</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Foreword</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FOREWORD
	 * @model name="Foreword" literal="foreword"
	 * @generated
	 * @ordered
	 */
	public static final int FOREWORD_VALUE = 12;

	/**
	 * The '<em><b>Illustrations</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Illustrations</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ILLUSTRATIONS
	 * @model name="Illustrations" literal="loi"
	 * @generated
	 * @ordered
	 */
	public static final int ILLUSTRATIONS_VALUE = 13;

	/**
	 * The '<em><b>Tables</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Tables</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TABLES
	 * @model name="Tables" literal="lot"
	 * @generated
	 * @ordered
	 */
	public static final int TABLES_VALUE = 14;

	/**
	 * The '<em><b>Notes</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Notes</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #NOTES
	 * @model name="Notes" literal="notes"
	 * @generated
	 * @ordered
	 */
	public static final int NOTES_VALUE = 15;

	/**
	 * The '<em><b>Preface</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Preface</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PREFACE
	 * @model name="Preface" literal="preface"
	 * @generated
	 * @ordered
	 */
	public static final int PREFACE_VALUE = 16;

	/**
	 * The '<em><b>Text</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Text</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TEXT
	 * @model name="Text" literal="text"
	 * @generated
	 * @ordered
	 */
	public static final int TEXT_VALUE = 17;

	/**
	 * An array of all the '<em><b>Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final Type[] VALUES_ARRAY =
		new Type[] {
			COVER,
			TITLE,
			TOC,
			INDEX,
			GLOSSARY,
			ACKNOWLEDGEMENTS,
			BIBLIOGRAPHY,
			COLOPHON,
			COPYRIGHT,
			DEDICATION,
			EPIGRAPH,
			FOREWORD,
			ILLUSTRATIONS,
			TABLES,
			NOTES,
			PREFACE,
			TEXT,
		};

	/**
	 * A public read-only list of all the '<em><b>Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<Type> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static Type get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			Type result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static Type getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			Type result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static Type get(int value) {
		switch (value) {
			case COVER_VALUE: return COVER;
			case TITLE_VALUE: return TITLE;
			case TOC_VALUE: return TOC;
			case INDEX_VALUE: return INDEX;
			case GLOSSARY_VALUE: return GLOSSARY;
			case ACKNOWLEDGEMENTS_VALUE: return ACKNOWLEDGEMENTS;
			case BIBLIOGRAPHY_VALUE: return BIBLIOGRAPHY;
			case COLOPHON_VALUE: return COLOPHON;
			case COPYRIGHT_VALUE: return COPYRIGHT;
			case DEDICATION_VALUE: return DEDICATION;
			case EPIGRAPH_VALUE: return EPIGRAPH;
			case FOREWORD_VALUE: return FOREWORD;
			case ILLUSTRATIONS_VALUE: return ILLUSTRATIONS;
			case TABLES_VALUE: return TABLES;
			case NOTES_VALUE: return NOTES;
			case PREFACE_VALUE: return PREFACE;
			case TEXT_VALUE: return TEXT;
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
	private Type(int value, String name, String literal) {
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
	
} //Type
