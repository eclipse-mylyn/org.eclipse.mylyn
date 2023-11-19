/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Book Struct</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getBookStruct()
 * @model extendedMetaData="name='bookStruct_._type'"
 * @generated
 */
public enum BookStruct implements Enumerator {
	/**
	 * The '<em><b>PAGENUMBER</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PAGENUMBER_VALUE
	 * @generated
	 * @ordered
	 */
	PAGENUMBER(0, "PAGENUMBER", "PAGE_NUMBER"),

	/**
	 * The '<em><b>NOTE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NOTE_VALUE
	 * @generated
	 * @ordered
	 */
	NOTE(1, "NOTE", "NOTE"),

	/**
	 * The '<em><b>NOTEREFERENCE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NOTEREFERENCE_VALUE
	 * @generated
	 * @ordered
	 */
	NOTEREFERENCE(2, "NOTEREFERENCE", "NOTE_REFERENCE"),

	/**
	 * The '<em><b>ANNOTATION</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ANNOTATION_VALUE
	 * @generated
	 * @ordered
	 */
	ANNOTATION(3, "ANNOTATION", "ANNOTATION"),

	/**
	 * The '<em><b>LINENUMBER</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LINENUMBER_VALUE
	 * @generated
	 * @ordered
	 */
	LINENUMBER(4, "LINENUMBER", "LINE_NUMBER"),

	/**
	 * The '<em><b>OPTIONALSIDEBAR</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OPTIONALSIDEBAR_VALUE
	 * @generated
	 * @ordered
	 */
	OPTIONALSIDEBAR(5, "OPTIONALSIDEBAR", "OPTIONAL_SIDEBAR"),

	/**
	 * The '<em><b>OPTIONALPRODUCERNOTE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OPTIONALPRODUCERNOTE_VALUE
	 * @generated
	 * @ordered
	 */
	OPTIONALPRODUCERNOTE(6, "OPTIONALPRODUCERNOTE", "OPTIONAL_PRODUCER_NOTE");

	/**
	 * The '<em><b>PAGENUMBER</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>PAGENUMBER</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PAGENUMBER
	 * @model literal="PAGE_NUMBER"
	 * @generated
	 * @ordered
	 */
	public static final int PAGENUMBER_VALUE = 0;

	/**
	 * The '<em><b>NOTE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>NOTE</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #NOTE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NOTE_VALUE = 1;

	/**
	 * The '<em><b>NOTEREFERENCE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>NOTEREFERENCE</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #NOTEREFERENCE
	 * @model literal="NOTE_REFERENCE"
	 * @generated
	 * @ordered
	 */
	public static final int NOTEREFERENCE_VALUE = 2;

	/**
	 * The '<em><b>ANNOTATION</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>ANNOTATION</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ANNOTATION
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ANNOTATION_VALUE = 3;

	/**
	 * The '<em><b>LINENUMBER</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>LINENUMBER</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LINENUMBER
	 * @model literal="LINE_NUMBER"
	 * @generated
	 * @ordered
	 */
	public static final int LINENUMBER_VALUE = 4;

	/**
	 * The '<em><b>OPTIONALSIDEBAR</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>OPTIONALSIDEBAR</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #OPTIONALSIDEBAR
	 * @model literal="OPTIONAL_SIDEBAR"
	 * @generated
	 * @ordered
	 */
	public static final int OPTIONALSIDEBAR_VALUE = 5;

	/**
	 * The '<em><b>OPTIONALPRODUCERNOTE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>OPTIONALPRODUCERNOTE</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #OPTIONALPRODUCERNOTE
	 * @model literal="OPTIONAL_PRODUCER_NOTE"
	 * @generated
	 * @ordered
	 */
	public static final int OPTIONALPRODUCERNOTE_VALUE = 6;

	/**
	 * An array of all the '<em><b>Book Struct</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final BookStruct[] VALUES_ARRAY =
		new BookStruct[] {
			PAGENUMBER,
			NOTE,
			NOTEREFERENCE,
			ANNOTATION,
			LINENUMBER,
			OPTIONALSIDEBAR,
			OPTIONALPRODUCERNOTE,
		};

	/**
	 * A public read-only list of all the '<em><b>Book Struct</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<BookStruct> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Book Struct</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static BookStruct get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			BookStruct result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Book Struct</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static BookStruct getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			BookStruct result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Book Struct</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static BookStruct get(int value) {
		switch (value) {
			case PAGENUMBER_VALUE: return PAGENUMBER;
			case NOTE_VALUE: return NOTE;
			case NOTEREFERENCE_VALUE: return NOTEREFERENCE;
			case ANNOTATION_VALUE: return ANNOTATION;
			case LINENUMBER_VALUE: return LINENUMBER;
			case OPTIONALSIDEBAR_VALUE: return OPTIONALSIDEBAR;
			case OPTIONALPRODUCERNOTE_VALUE: return OPTIONALPRODUCERNOTE;
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
	private BookStruct(int value, String name, String literal) {
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
	
} //BookStruct
