/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.core.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.validation.ValidationRule;

/**
 * A collection of validation rules, which involves a hierarchy so that language inheritance behaviour can be supported.
 * 
 * @author David Green
 */
public class ValidationRules {
	private ValidationRules parent;

	private final List<ValidationRule> rules = new ArrayList<ValidationRule>();

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
			List<ValidationRule> combined = new ArrayList<ValidationRule>(rules.size() + parentRules.size());
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
