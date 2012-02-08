/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.html.HTML.Tag;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.commons.net.HtmlTag;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;

public class BugzillaUserMatchResponse {
	private final List<String> newCCProposals = new LinkedList<String>();

	private final List<String> assignedToProposals = new LinkedList<String>();

	private final List<String> qaContactProposals = new LinkedList<String>();

	private String newCCMsg;

	private String assignedToMsg;

	private String qaContactMsg;

	public List<String> getNewCCProposals() {
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

	public void parseResultConfirmMatch(HtmlStreamTokenizer tokenizer, String repositoryURL, String body,
			boolean oldStyle) throws IOException, CoreException {
		if (oldStyle) {
			parseConfirmMatchOld(tokenizer, repositoryURL, body);
		} else {
			parseConfirmMatchNew(tokenizer, repositoryURL, body);
		}
		throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
				BugzillaStatus.ERROR_CONFIRM_MATCH, repositoryURL, "Confirm Match", body, this)); //$NON-NLS-1$
	}

	private void parseConfirmMatchNew(HtmlStreamTokenizer tokenizer, String repositoryURL, String body)
			throws IOException, CoreException {
		String name = ""; //$NON-NLS-1$
		String value = ""; //$NON-NLS-1$
		String divClass = null;
		try {
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.DIV
						&& !((HtmlTag) (token.getValue())).isEndTag()) {
					divClass = ((HtmlTag) (token.getValue())).getAttribute("class"); //$NON-NLS-1$
				}
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.DIV
						&& ((HtmlTag) (token.getValue())).isEndTag()) {
					divClass = null;
				}

				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.INPUT
						&& !((HtmlTag) (token.getValue())).isEndTag() && divClass != null
						&& divClass.equals("user_match")) { //$NON-NLS-1$
					value = ((HtmlTag) (token.getValue())).getAttribute("value"); //$NON-NLS-1$
					name = ((HtmlTag) (token.getValue())).getAttribute("name"); //$NON-NLS-1$
					value = value.replace("&#64;", "@"); //$NON-NLS-1$ //$NON-NLS-2$
					if (name.equals("newcc")) { //$NON-NLS-1$
						newCCProposals.add(value);
					} else if (name.equals("assigned_to")) { //$NON-NLS-1$
						assignedToProposals.add(value);
					} else if (name.equals("qa_contact")) { //$NON-NLS-1$
						qaContactProposals.add(value);
					}
				}
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.SELECT
						&& !((HtmlTag) (token.getValue())).isEndTag()) {
					name = ((HtmlTag) (token.getValue())).getAttribute("id"); //$NON-NLS-1$
				}
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.OPTION
						&& !((HtmlTag) (token.getValue())).isEndTag() && divClass != null
						&& divClass.equals("user_match")) { //$NON-NLS-1$
					value = ((HtmlTag) (token.getValue())).getAttribute("value"); //$NON-NLS-1$
					value = value.replace("&#64;", "@"); //$NON-NLS-1$ //$NON-NLS-2$
					if (name.equals("newcc")) { //$NON-NLS-1$
						newCCProposals.add(value);
					} else if (name.equals("assigned_to")) { //$NON-NLS-1$
						assignedToProposals.add(value);
					} else if (name.equals("qa_contact")) { //$NON-NLS-1$
						qaContactProposals.add(value);
					}
				}
			}
		} catch (ParseException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryURL + ".")); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	private void parseConfirmMatchOld(HtmlStreamTokenizer tokenizer, String repositoryURL, String body)
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

						int start = divString.indexOf("</b>"); //$NON-NLS-1$
						int optionValue = divString.indexOf("<option value=\"", start + 4); //$NON-NLS-1$
						String value = ""; //$NON-NLS-1$
						if (optionValue == -1) {
							int startText = divString.indexOf("&lt;", start + 4) + 1; //$NON-NLS-1$
							int endText = divString.indexOf("&gt;", startText + 1); //$NON-NLS-1$
							String temp = divString.substring(startText + 3, endText);
							value = temp.replace("&#64;", "@"); //$NON-NLS-1$ //$NON-NLS-2$
							if (lastDTValue.equals("newcc")) { //$NON-NLS-1$
								newCCProposals.add(value);
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
									newCCProposals.add(value);
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

						int start = divString.indexOf("</b>"); //$NON-NLS-1$
						int optionValue = divString.indexOf("<option value=\"", start + 4); //$NON-NLS-1$
						String value = ""; //$NON-NLS-1$
						if (optionValue == -1) {
							int startText = divString.indexOf(">", start + 4) + 1; //$NON-NLS-1$
							int endText = divString.indexOf("<", startText + 1); //$NON-NLS-1$
							String temp = divString.substring(startText, endText);
							value = divString.substring(5, start) + temp;
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
									newCCProposals.add(value);
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
					BugzillaStatus.ERROR_MATCH_FAILED, repositoryURL, "Match Failed", body, this));
		} catch (ParseException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryURL + ".")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
