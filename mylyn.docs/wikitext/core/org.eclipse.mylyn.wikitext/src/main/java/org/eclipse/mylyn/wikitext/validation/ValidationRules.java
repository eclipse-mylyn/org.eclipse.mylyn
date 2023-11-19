/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A collection of validation rules, which involves a hierarchy so that language inheritance behaviour can be supported.
 *
 * @author David Green
 * @since 3.0
 */
public class ValidationRules {
	private ValidationRules parent;

	private final List<ValidationRule> rules = new ArrayList<>();

	public void addValidationRule(ValidationRule rule) {
		rules.add(rule);
	}

	/**
	 * get all of the validation rules, including those defined by any {@link #getParent() parent} collections.
	 */
	public List<ValidationRule> getRules() {
		if (parent != null) {
			List<ValidationRule> parentRules = parent.getRules();
			if (rules.isEmpty()) {
				return parentRules;
			} else if (parentRules.isEmpty()) {
				return Collections.unmodifiableList(rules);
			}
			List<ValidationRule> combined = new ArrayList<>(rules.size() + parentRules.size());
			combined.addAll(parentRules);
			combined.addAll(rules);
			return Collections.unmodifiableList(combined);
		}
		return Collections.unmodifiableList(rules);
	}

	public ValidationRules getParent() {
		return parent;
	}

	public void setParent(ValidationRules parent) {
		this.parent = parent;
	}
}
