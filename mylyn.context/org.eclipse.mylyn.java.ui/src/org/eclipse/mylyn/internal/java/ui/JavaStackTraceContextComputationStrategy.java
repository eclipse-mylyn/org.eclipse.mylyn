/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.java.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.mylyn.context.core.IInteractionContext;

/**
 * A strategy that computes Java items based on a Java stack trace in the task description.
 * 
 * @author David Green
 */
public class JavaStackTraceContextComputationStrategy extends AbstractJavaContextComputationStrategy {

	private static final String PACKAGE_PART = "([a-z][a-z0-9]*)"; //$NON-NLS-1$

	private static final String CLASS_PART = "[A-Za-z][a-zA-Z0-9_$]*"; //$NON-NLS-1$

	private static final String FQN_PART = "((" + PACKAGE_PART + "\\.)+" + CLASS_PART + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final Pattern STACK_TRACE_PATTERN = Pattern.compile("\\s*((" + "((Caused by:\\s+)|(at\\s+))?" //$NON-NLS-1$//$NON-NLS-2$
			+ FQN_PART + "((:\\s+\\w.*)|(\\.((\\<(?:cl)?init\\>)|([a-zA-Z0-9_$]+))\\(.*?\\)))?" //$NON-NLS-1$
			+ ")|(\\.\\.\\.\\s\\d+\\smore))"); //$NON-NLS-1$ 

	private final SortedSet<String> filteredPrefixes = new TreeSet<String>(Arrays.asList(new String[] { "java", //$NON-NLS-1$
			"javax", "junit.framework", "sun.reflect" })); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * public for testing only
	 */
	public static class Element {
		String fqn;

		String methodName;

		public Element(String fqn, String methodName) {
			Assert.isNotNull(fqn);
			this.fqn = fqn;
			this.methodName = methodName;
		}

		public Element() {
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Element [fqn="); //$NON-NLS-1$
			builder.append(fqn);
			builder.append(", methodName="); //$NON-NLS-1$
			builder.append(methodName);
			builder.append("]"); //$NON-NLS-1$
			return builder.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fqn == null) ? 0 : fqn.hashCode());
			result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Element other = (Element) obj;
			if (fqn == null) {
				if (other.fqn != null) {
					return false;
				}
			} else if (!fqn.equals(other.fqn)) {
				return false;
			}
			if (methodName == null) {
				if (other.methodName != null) {
					return false;
				}
			} else if (!methodName.equals(other.methodName)) {
				return false;
			}
			return true;
		}

	}

	private final int maxElements = 10;

	@Override
	public List<Object> computeContext(IInteractionContext context, IAdaptable input, IProgressMonitor monitor) {
		String text = getText(input);
		if (text != null) {
			return computeContext(text, monitor);
		}
		return Collections.emptyList();
	}

	private String getText(IAdaptable input) {
		String text = (String) input.getAdapter(String.class);
		if (text != null) {
			return text;
		}
		return null;
	}

	public List<Object> computeContext(String description, IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor);
		try {
			List<Element> elements = computeElements(description);
			if (!elements.isEmpty()) {

				progress.beginTask(
						Messages.JavaStackTraceContextComputationStrategy_Finding_Java_Context_Element_Progress_Label,
						elements.size());

				final List<Object> javaElements = new ArrayList<Object>();
				try {
					for (Element element : elements) {
						if (progress.isCanceled()) {
							break;
						}

						SortedSet<String> prefix = filteredPrefixes.headSet(element.fqn);
						if (prefix.isEmpty() || !element.fqn.startsWith(prefix.last())) {
							try {
								IType type = findTypeInWorkspace(element.fqn);
								if (type != null) {
									javaElements.add(type);
									if (element.methodName != null) {
										IMethod[] methods = type.getMethods();
										for (IMethod method : methods) {
											if (method.getElementName().equals(element.methodName)) {
												javaElements.add(method);
											}
										}
									}
								}
							} catch (CoreException e) {
								JavaUiBridgePlugin.getDefault().getLog().log(e.getStatus());
							}
						}
						progress.worked(1);
					}
				} finally {
					progress.done();
				}
				return javaElements;
			}
		} catch (IOException e) {
			// ignore
		}
		return Collections.emptyList();
	}

	/**
	 * Public for test purposes only
	 */
	public List<Element> computeElements(String description) throws IOException {
		List<Element> elements = new ArrayList<Element>();

		BufferedReader reader = new BufferedReader(new StringReader(description));
		for (String line = reader.readLine(); line != null && elements.size() < maxElements; line = reader.readLine()) {
			Matcher matcher = STACK_TRACE_PATTERN.matcher(line);
			if (matcher.matches()) {
				String fqn = matcher.group(6);
				if (fqn != null) {
					Element element = new Element(fqn, matcher.group(12));
					elements.add(element);
				}
			}
		}
		return elements;
	}
}
