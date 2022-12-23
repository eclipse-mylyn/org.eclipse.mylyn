/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTML.Tag;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.commons.core.HtmlTag;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;

public class BugzillaUserMatchResponse {
	private final Map<String, List<String>> newCCProposals = new HashMap<String, List<String>>();

	private final List<String> assignedToProposals = new LinkedList<String>();

	private final List<String> qaContactProposals = new LinkedList<String>();

	private String newCCMsg;

	private String assignedToMsg;

	private String qaContactMsg;

	public Map<String, List<String>> getNewCCProposals() {
		return newCCProposals;
	}

	public List<String> getAssignedToProposals() {
		return assignedToProposals;
	}

	public List<String> getQaContactProposals() {
		return qaContactProposals;
	}

	public String getNewCCMsg() {
		return newCCMsg;
	}

	public String getAssignedToMsg() {
		return assignedToMsg;
	}

	public String getQaContactMsg() {
		return qaContactMsg;
	}

	public void parseResultConfirmMatch(HtmlStreamTokenizer tokenizer, String repositoryURL, String body)
			throws IOException, CoreException {
		parseConfirmMatchInternal(tokenizer, repositoryURL, body);
		throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
				BugzillaStatus.ERROR_CONFIRM_MATCH, repositoryURL, "Confirm Match", body, this)); //$NON-NLS-1$
	}

	private void parseConfirmMatchInternal(HtmlStreamTokenizer tokenizer, String repositoryURL, String body)
			throws IOException, CoreException {
		boolean isDT = false;
		String dtString = ""; //$NON-NLS-1$
		String lastDTValue = ""; //$NON-NLS-1$
		boolean isDiv = false;
		String divString = ""; //$NON-NLS-1$
		try {
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.TD
						&& ((HtmlTag) (token.getValue())).isEndTag()) {
					isDT = false;
					if (!dtString.equals("")) { //$NON-NLS-1$
						lastDTValue = dtString;
					}
					dtString = ""; //$NON-NLS-1$
					continue;
				}
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.DIV
						&& ((HtmlTag) (token.getValue())).isEndTag()) {
					isDiv = false;
					if (divString.length() > 4) {
						if (lastDTValue.equals("CC:")) { //$NON-NLS-1$
							lastDTValue = "newcc"; //$NON-NLS-1$
						}
						if (lastDTValue.equals("Assignee:")) { //$NON-NLS-1$
							lastDTValue = "assigned_to"; //$NON-NLS-1$
						}
						if (lastDTValue.equals("QAContact:")) { //$NON-NLS-1$
							lastDTValue = "qa_contact"; //$NON-NLS-1$
						}

						int startBold = divString.indexOf("<b>"); //$NON-NLS-1$
						int endBold = divString.indexOf("</b>"); //$NON-NLS-1$
						String name = divString.substring(startBold + 3, endBold).trim();
						int optionValue = divString.indexOf("<option value=\"", endBold + 4); //$NON-NLS-1$
						String value = ""; //$NON-NLS-1$
						if (optionValue == -1) {
							int startText = divString.indexOf("&lt;", endBold + 4) + 1; //$NON-NLS-1$
							int endText = divString.indexOf("&gt;", startText + 1); //$NON-NLS-1$
							String temp = null;
							if (startText == 0) {
								startText = divString.indexOf("(", endBold + 4) + 1; //$NON-NLS-1$
								endText = divString.indexOf(")", startText + 1); //$NON-NLS-1$
								temp = divString.substring(startText, endText);
							} else {
								temp = divString.substring(startText + 3, endText);
							}
							value = temp.replace("&#64;", "@"); //$NON-NLS-1$ //$NON-NLS-2$
							if (lastDTValue.equals("newcc")) { //$NON-NLS-1$
								List<String> proposalList = newCCProposals.get(name);
								if (proposalList == null) {
									proposalList = new ArrayList<String>();
									newCCProposals.put(name, proposalList);
								}
								proposalList.add(value);
							} else if (lastDTValue.equals("assigned_to")) { //$NON-NLS-1$
								assignedToProposals.add(value);
							} else if (lastDTValue.equals("qa_contact")) { //$NON-NLS-1$
								qaContactProposals.add(value);
							}
						} else {
							while (optionValue != -1) {
								int endText = divString.indexOf("\">", optionValue + 1); //$NON-NLS-1$
								value = divString.substring(optionValue + 15, endText);
								value = value.replace("&#64;", "@"); //$NON-NLS-1$ //$NON-NLS-2$
								if (lastDTValue.equals("newcc")) { //$NON-NLS-1$
									List<String> proposalList = newCCProposals.get(name);
									if (proposalList == null) {
										proposalList = new ArrayList<String>();
										newCCProposals.put(name, proposalList);
									}
									proposalList.add(value);
								} else if (lastDTValue.equals("assigned_to")) { //$NON-NLS-1$
									assignedToProposals.add(value);
								} else if (lastDTValue.equals("qa_contact")) { //$NON-NLS-1$
									qaContactProposals.add(value);
								}
								optionValue = divString.indexOf("<option value=\"", endText + 1); //$NON-NLS-1$
							}
						}
					}
					dtString = ""; //$NON-NLS-1$
					divString = ""; //$NON-NLS-1$
					continue;
				}
				if (isDiv) {
					divString += (" " + token.getValue()); //$NON-NLS-1$
				}
				if (isDT) {
					if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.DIV
							&& !((HtmlTag) (token.getValue())).isEndTag()) {
						isDiv = true;
						divString = ""; //$NON-NLS-1$
					} else {
						dtString += token.getValue();
					}
				}
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.TD
						&& !((HtmlTag) (token.getValue())).isEndTag()) {
					isDT = true;
					continue;
				}
			}
		} catch (ParseException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryURL + ".")); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	public void parseResultMatchFailed(HtmlStreamTokenizer tokenizer, String repositoryURL, String body)
			throws IOException, CoreException {
		boolean isDT = false;
		String dtString = ""; //$NON-NLS-1$
		String lastDTValue = ""; //$NON-NLS-1$
		boolean isDiv = false;
		String divString = ""; //$NON-NLS-1$
		try {
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.TD
						&& ((HtmlTag) (token.getValue())).isEndTag()) {
					isDT = false;
					if (!dtString.equals("")) { //$NON-NLS-1$
						lastDTValue = dtString;
					}
					dtString = ""; //$NON-NLS-1$
					continue;
				}
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.DIV
						&& ((HtmlTag) (token.getValue())).isEndTag()) {
					isDiv = false;
					if (divString.length() > 4) {
						if (lastDTValue.equals("CC:")) { //$NON-NLS-1$
							lastDTValue = "newcc"; //$NON-NLS-1$
						}
						if (lastDTValue.equals("Assignee:")) { //$NON-NLS-1$
							lastDTValue = "assigned_to"; //$NON-NLS-1$
						}
						if (lastDTValue.equals("QAContact:")) { //$NON-NLS-1$
							lastDTValue = "qa_contact"; //$NON-NLS-1$
						}

						int startBold = divString.indexOf("<b>"); //$NON-NLS-1$
						int endBold = divString.indexOf("</b>"); //$NON-NLS-1$
						String name = divString.substring(startBold + 3, endBold).trim();
						int optionValue = divString.indexOf("<option value=\"", endBold + 4); //$NON-NLS-1$
						String value = ""; //$NON-NLS-1$
						if (optionValue == -1) {
							int startText = divString.indexOf(">", endBold + 4) + 1; //$NON-NLS-1$
							int endText = divString.indexOf("<", startText + 1); //$NON-NLS-1$
							String temp = divString.substring(startText, endText);
							value = divString.substring(5, endBold) + temp;
							value = value.replace("&#64;", "@"); //$NON-NLS-1$ //$NON-NLS-2$
							if (lastDTValue.equals("newcc")) { //$NON-NLS-1$
								newCCMsg = value;
							} else if (lastDTValue.equals("assigned_to")) { //$NON-NLS-1$
								assignedToMsg = value;
							} else if (lastDTValue.equals("qa_contact")) { //$NON-NLS-1$
								qaContactMsg = value;
							}
						} else {
							while (optionValue != -1) {
								int endText = divString.indexOf("\">", optionValue + 1); //$NON-NLS-1$
								value = divString.substring(optionValue + 15, endText);
								value = value.replace("&#64;", "@"); //$NON-NLS-1$ //$NON-NLS-2$
								if (lastDTValue.equals("newcc")) { //$NON-NLS-1$
									List<String> proposalList = newCCProposals.get(name);
									if (proposalList == null) {
										proposalList = new ArrayList<String>();
										newCCProposals.put(name, proposalList);
									}
									proposalList.add(value);
								} else if (lastDTValue.equals("assigned_to")) { //$NON-NLS-1$
									assignedToProposals.add(value);
								} else if (lastDTValue.equals("qa_contact")) { //$NON-NLS-1$
									qaContactProposals.add(value);
								}
								optionValue = divString.indexOf("<option value=\"", endText + 1); //$NON-NLS-1$
							}
						}
					}
					dtString = ""; //$NON-NLS-1$
					divString = ""; //$NON-NLS-1$
					continue;
				}
				if (isDiv) {
					divString += (" " + token.getValue()); //$NON-NLS-1$
				}
				if (isDT) {
					if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.DIV
							&& !((HtmlTag) (token.getValue())).isEndTag()) {
						isDiv = true;
						divString = ""; //$NON-NLS-1$
					} else {
						dtString += token.getValue();
					}
				}
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.TD
						&& !((HtmlTag) (token.getValue())).isEndTag()) {
					isDT = true;
					continue;
				}
			}
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					BugzillaStatus.ERROR_MATCH_FAILED, repositoryURL, "Match Failed", body, this)); //$NON-NLS-1$
		} catch (ParseException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryURL + ".")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
