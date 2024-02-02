/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

/**
 * @author Steffen Pingel
 */
public class TestResultLabelProvider extends LabelProvider implements IStyledLabelProvider {

	final Styler NO_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
		}
	};

	public TestResultLabelProvider() {
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof ITestSuite) {
			return CommonImages.getImage(getImageDescriptor((ITestSuite) element));
		} else if (element instanceof ITestCase) {
			return CommonImages.getImage(getImageDescriptor((ITestCase) element));
		}
		return null;
	}

	private ImageDescriptor getImageDescriptor(ITestCase testCase) {
		switch (testCase.getStatus()) {
			case PASSED:
			case FIXED:
				return BuildImages.TEST_PASSED;
			case REGRESSION:
			case FAILED:
				if (testCase.getMessage() != null) {
					return BuildImages.TEST_FAILED;
				} else {
					return BuildImages.TEST_ERROR;
				}
			case SKIPPED:
				return BuildImages.TEST_IGNORED;
		}
		return BuildImages.TEST;
	}

	private ImageDescriptor getImageDescriptor(ITestSuite suite) {
		int passedCount = 0;
		int failedCount = 0;
		int errorCount = 0;
		int ignoredCount = 0;
		for (ITestCase testCase : suite.getCases()) {
			switch (testCase.getStatus()) {
				case PASSED:
				case FIXED:
					passedCount++;
					break;
				case REGRESSION:
				case FAILED:
					if (testCase.getMessage() != null) {
						failedCount++;
					} else {
						errorCount++;
					}
				case SKIPPED:
					ignoredCount++;
			}
		}
		if (errorCount > 0) {
			return BuildImages.TEST_SUITE_ERROR;
		}
		if (failedCount > 0) {
			return BuildImages.TEST_SUITE_FAILED;
		}
		if (ignoredCount > 0) {
			return BuildImages.TEST_SUITE_IGNORED;
		}
		if (passedCount > 0) {
			return BuildImages.TEST_SUITE_PASSED;
		}
		return BuildImages.TEST_SUITE;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ITestSuite) {
			return ((ITestSuite) element).getLabel();
		}
		if (element instanceof ITestCase) {
			return ((ITestCase) element).getLabel();
		}
		return super.getText(element);
	}

	@Override
	public StyledString getStyledText(Object element) {
		String text = getText(element);
		if (text != null) {
			StyledString styledString = new StyledString(text);
			if (element instanceof ITestCase) {
				String duration = " " + DateUtil.getFormattedDurationShort(((ITestCase) element).getDuration(), true);
				styledString.append(duration, StyledString.DECORATIONS_STYLER);
			}
			return styledString;
		}
		return new StyledString();
	}

}
