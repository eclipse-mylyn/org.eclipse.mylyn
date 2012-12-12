/**
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Line Location</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ILineLocation#getRanges <em>Ranges</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ILineLocation#getRangeMin <em>Range Min</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ILineLocation#getRangeMax <em>Range Max</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface ILineLocation extends ILocation {
	/**
	 * Returns the value of the '<em><b>Ranges</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.ILineRange}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ranges</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Ranges</em>' containment reference list.
	 * @generated
	 */
	List<ILineRange> getRanges();

	/**
	 * Returns the value of the '<em><b>Range Min</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Range Min</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Range Min</em>' attribute.
	 * @generated
	 */
	int getRangeMin();

	/**
	 * Returns the value of the '<em><b>Range Max</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Range Max</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Range Max</em>' attribute.
	 * @generated
	 */
	int getRangeMax();

} // ILineLocation
