/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.cdt.ui.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompletionContext;
import org.eclipse.cdt.core.dom.ast.IASTCompletionNode;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionStyleMacroParameter;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IEnumerator;
import org.eclipse.cdt.core.dom.ast.IField;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.c.ICFunctionScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBlockScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPUsingDeclaration;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.ast.ASTAccessVisibility;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.dom.parser.c.ICInternalBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPImplicitFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPImplicitMethod;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPImplicitTypedef;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPInternalBinding;
import org.eclipse.cdt.internal.ui.text.contentassist.CContentAssistInvocationContext;
import org.eclipse.cdt.internal.ui.text.contentassist.CProposalContextInformation;
import org.eclipse.cdt.internal.ui.text.contentassist.ParsingBasedProposalComputer;
import org.eclipse.cdt.internal.ui.text.contentassist.RelevanceConstants;
import org.eclipse.cdt.internal.ui.viewsupport.CElementImageProvider;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.text.ICPartitions;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.cdt.ui.CDTUIBridgePlugin;
import org.eclipse.swt.graphics.Image;

/**
 * Searches the DOM (both the AST and the index) for completion proposals.
 * 
 * @author Bryan Wilkinson
 */
public class DOMCompletionProposalComputer extends ParsingBasedProposalComputer {

	/**
	 * Default constructor is required (executable extension).
	 */
	public DOMCompletionProposalComputer() {
	}

	@Override
	protected List<ICompletionProposal> computeCompletionProposals(CContentAssistInvocationContext context,
			IASTCompletionNode completionNode, String prefix) {

		List<ICompletionProposal> proposals = new ArrayList<>();

		if (inPreprocessorDirective(context)) {
			if (!inPreprocessorKeyword(context)) {
				// add only macros
				if (prefix.length() == 0) {
					try {
						prefix = context.computeIdentifierPrefix().toString();
					} catch (BadLocationException exc) {
						CUIPlugin.log(exc);
					}
				}
				addMacroProposals(context, prefix, proposals);
			}
		} else {
			boolean handleMacros = false;
			IASTName[] names = completionNode.getNames();

			for (IASTName name : names) {
				if (name.getTranslationUnit() == null) {
					// The node isn't properly hooked up, must have backtracked out of this node
					continue;
				}

				IASTCompletionContext astContext = name.getCompletionContext();
				if (astContext == null) {
					continue;
				} else if (astContext instanceof IASTIdExpression || astContext instanceof IASTNamedTypeSpecifier) {
					// handle macros only if there is a prefix
					handleMacros = prefix.length() > 0;
				}

				IBinding[] bindings = astContext.findBindings(name, !context.isContextInformationStyle());
				if (bindings != null) {
					for (IBinding binding : bindings) {
						handleBinding(binding, context, prefix, astContext, proposals);
					}
				}
			}

			if (handleMacros) {
				addMacroProposals(context, prefix, proposals);
			}
		}

		return proposals;
	}

	/**
	 * Test whether the invocation offset is inside or before the preprocessor directive keyword.
	 * 
	 * @param context
	 *            the invocation context
	 * @return <code>true</code> if the invocation offset is inside or before the directive keyword
	 */
	private boolean inPreprocessorKeyword(CContentAssistInvocationContext context) {
		IDocument doc = context.getDocument();
		int offset = context.getInvocationOffset();

		try {
			final ITypedRegion partition = TextUtilities.getPartition(doc, ICPartitions.C_PARTITIONING, offset, true);
			if (ICPartitions.C_PREPROCESSOR.equals(partition.getType())) {
				String ppPrefix = doc.get(partition.getOffset(), offset - partition.getOffset());
				if (ppPrefix.matches("\\s*#\\s*\\w*")) { //$NON-NLS-1$
					// we are inside the directive keyword
					return true;
				}
			}

		} catch (BadLocationException exc) {
		}
		return false;
	}

	/**
	 * Check if the invocation offset is inside a preprocessor directive.
	 * 
	 * @param context
	 *            the content asist invocation context
	 * @return <code>true</code> if invocation offset is inside a preprocessor directive
	 */
	private boolean inPreprocessorDirective(CContentAssistInvocationContext context) {
		IDocument doc = context.getDocument();
		int offset = context.getInvocationOffset();

		try {
			final ITypedRegion partition = TextUtilities.getPartition(doc, ICPartitions.C_PARTITIONING, offset, true);
			if (ICPartitions.C_PREPROCESSOR.equals(partition.getType())) {
				return true;
			}

		} catch (BadLocationException exc) {
		}
		return false;
	}

	private void addMacroProposals(CContentAssistInvocationContext context, String prefix,
			List<ICompletionProposal> proposals) {
		char[] prefixChars = prefix.toCharArray();
		final boolean matchPrefix = !context.isContextInformationStyle();
		IASTCompletionNode completionNode = context.getCompletionNode();
		IASTPreprocessorMacroDefinition[] macros = completionNode.getTranslationUnit().getMacroDefinitions();
		if (macros != null) {
			for (IASTPreprocessorMacroDefinition element : macros) {
				final char[] macroName = element.getName().toCharArray();
				if (CharArrayUtils.equals(macroName, 0, matchPrefix ? prefixChars.length : macroName.length,
						prefixChars, true)) {
					handleMacro(element, context, prefix, proposals);
				}
			}
		}
		macros = completionNode.getTranslationUnit().getBuiltinMacroDefinitions();
		if (macros != null) {
			for (IASTPreprocessorMacroDefinition element : macros) {
				final char[] macroName = element.getName().toCharArray();
				if (CharArrayUtils.equals(macroName, 0, matchPrefix ? prefixChars.length : macroName.length,
						prefixChars, true)) {
					handleMacro(element, context, prefix, proposals);
				}
			}
		}
	}

	private void handleMacro(IASTPreprocessorMacroDefinition macro, CContentAssistInvocationContext context,
			String prefix, List<ICompletionProposal> proposals) {

//CElementHandleFactory.create;
		final String macroName = macro.getName().toString();
		final int baseRelevance = computeBaseRelevance(prefix, macroName);

		Image image = getImage(CElementImageProvider.getMacroImageDescriptor());

		if (macro instanceof IASTPreprocessorFunctionStyleMacroDefinition functionMacro) {
			StringBuilder repStringBuff = new StringBuilder();
			repStringBuff.append(macroName);
			repStringBuff.append('(');

			StringBuilder args = new StringBuilder();

			IASTFunctionStyleMacroParameter[] params = functionMacro.getParameters();
			if (params != null) {
				for (int i = 0; i < params.length; ++i) {
					if (i > 0) {
						args.append(", "); //$NON-NLS-1$
					}
					args.append(params[i].getParameter());
				}
			}
			String argString = args.toString();

			StringBuilder descStringBuff = new StringBuilder(repStringBuff.toString());
			descStringBuff.append(argString);
			descStringBuff.append(')');

			repStringBuff.append(')');
			String repString = repStringBuff.toString();
			String descString = descStringBuff.toString();

			CCompletionProposal proposal = createProposal(repString, descString, prefix.length(), image,
					baseRelevance + RelevanceConstants.MACRO_TYPE_RELEVANCE, context, getCElement(macro),
					macro.getName().toString());
			if (!context.isContextInformationStyle()) {
				proposal.setCursorPosition(repString.length() - 1);
			}

			if (argString.length() > 0) {
				CProposalContextInformation info = new CProposalContextInformation(image, descString, argString);
				info.setContextInformationPosition(context.getContextInformationOffset());
				proposal.setContextInformation(info);
			}

			proposals.add(proposal);
		} else {
			proposals.add(createProposal(macroName, macroName, prefix.length(), image,
					baseRelevance + RelevanceConstants.MACRO_TYPE_RELEVANCE, context, getCElement(macro),
					macro.getName().toString()));
		}
	}

	@SuppressWarnings("restriction")
	protected void handleBinding(IBinding binding, CContentAssistInvocationContext cContext, String prefix,
			IASTCompletionContext astContext, List<ICompletionProposal> proposals) {

		if ((binding instanceof CPPImplicitFunction || binding instanceof CPPImplicitTypedef)
				&& !(binding instanceof CPPImplicitMethod)) {
			return;
		}

		if (!isAnonymousBinding(binding)) {
			final String name = binding.getName();
			final int baseRelevance = computeBaseRelevance(prefix, name);
			if (binding instanceof ICPPClassType) {
				handleClass((ICPPClassType) binding, astContext, cContext, baseRelevance, proposals);
			} else if (binding instanceof IFunction) {
				handleFunction((IFunction) binding, cContext, baseRelevance, proposals);
			} else if (!cContext.isContextInformationStyle()) {
				if (binding instanceof IVariable) {
					handleVariable((IVariable) binding, cContext, baseRelevance, proposals);
				} else if (binding instanceof ITypedef) {
					proposals.add(createProposal(name, name, getImage(binding),
							baseRelevance + RelevanceConstants.TYPEDEF_TYPE_RELEVANCE, cContext, getCElement(binding),
							binding.getName()));
				} else if (binding instanceof ICPPNamespace) {
					handleNamespace((ICPPNamespace) binding, astContext, cContext, baseRelevance, proposals);
				} else if (binding instanceof IEnumeration) {
					proposals.add(createProposal(name, name, getImage(binding),
							baseRelevance + RelevanceConstants.ENUMERATION_TYPE_RELEVANCE, cContext,
							getCElement(binding), binding.getName()));
				} else if (binding instanceof IEnumerator) {
					proposals.add(createProposal(name, name, getImage(binding),
							baseRelevance + RelevanceConstants.ENUMERATOR_TYPE_RELEVANCE, cContext,
							getCElement(binding), binding.getName()));
				} else {
					proposals.add(createProposal(name, name, getImage(binding),
							baseRelevance + RelevanceConstants.DEFAULT_TYPE_RELEVANCE, cContext, getCElement(binding),
							binding.getName()));
				}
			}
		}
	}

	private boolean isAnonymousBinding(IBinding binding) {
		char[] name = binding.getNameCharArray();
		return name.length == 0 || name[0] == '{';
	}

	private void handleClass(ICPPClassType classType, IASTCompletionContext astContext,
			CContentAssistInvocationContext context, int baseRelevance, List<ICompletionProposal> proposals) {
		if (context.isContextInformationStyle()) {
			try {
				ICPPConstructor[] constructors = classType.getConstructors();
				for (ICPPConstructor constructor : constructors) {
					handleFunction(constructor, context, baseRelevance, proposals);
				}
			} catch (Exception e) {
				handle(e);
			}
		} else {
			int relevance = 0;
			try {
				switch (classType.getKey()) {
					case ICPPClassType.k_class:
						relevance = RelevanceConstants.CLASS_TYPE_RELEVANCE;
						break;
					case ICompositeType.k_struct:
						relevance = RelevanceConstants.STRUCT_TYPE_RELEVANCE;
						break;
					case ICompositeType.k_union:
						relevance = RelevanceConstants.UNION_TYPE_RELEVANCE;
						break;
				}
			} catch (Exception e) {
				handle(e);
			}
			if (astContext instanceof IASTName name && !(astContext instanceof ICPPASTQualifiedName)) {
				if (name.getParent() instanceof IASTDeclarator) {
					proposals.add(createProposal(classType.getName() + "::", classType.getName(), getImage(classType), //$NON-NLS-1$
							baseRelevance + relevance, context, getCElement(classType), classType.getName()));
				}
			}
			proposals.add(createProposal(classType.getName(), classType.getName(), getImage(classType),
					baseRelevance + RelevanceConstants.CLASS_TYPE_RELEVANCE, context, getCElement(classType),
					classType.getName()));
		}
	}

	private void handle(Exception e) {
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
	}

	private void handleFunction(IFunction function, CContentAssistInvocationContext context, int baseRelevance,
			List<ICompletionProposal> proposals) {
		Image image = getImage(function);

		StringBuilder repStringBuff = new StringBuilder();
		repStringBuff.append(function.getName());
		repStringBuff.append('(');

		StringBuilder dispargs = new StringBuilder(); // for the displayString
		StringBuilder idargs = new StringBuilder(); // for the idString
		String returnTypeStr = null;
		try {
			IParameter[] params = function.getParameters();
			if (params != null) {
				for (int i = 0; i < params.length; ++i) {
					IType paramType = params[i].getType();
					if (i > 0) {
						dispargs.append(',');
						idargs.append(',');
					}

					dispargs.append(ASTTypeUtil.getType(paramType, false));
					idargs.append(ASTTypeUtil.getType(paramType, false));
					String paramName = params[i].getName();
					if (paramName != null && paramName.length() > 0) {
						dispargs.append(' ');
						dispargs.append(paramName);
					}
				}

				if (function.takesVarArgs()) {
					if (params.length > 0) {
						dispargs.append(',');
						idargs.append(',');
					}
					dispargs.append("..."); //$NON-NLS-1$
					idargs.append("..."); //$NON-NLS-1$
				} else if (params.length == 0) { // force the void in
					dispargs.append("void"); //$NON-NLS-1$
					idargs.append("void"); //$NON-NLS-1$
				}
			}
			IFunctionType functionType = function.getType();
			if (functionType != null) {
				IType returnType = functionType.getReturnType();
				if (returnType != null) {
					returnTypeStr = ASTTypeUtil.getType(returnType, false);
				}
			}
		} catch (Exception e) {
		}

		String dispargString = dispargs.toString();
		String idargString = idargs.toString();

		StringBuilder dispStringBuff = new StringBuilder(repStringBuff.toString());
		dispStringBuff.append(dispargString);
		dispStringBuff.append(')');
		if (returnTypeStr != null && returnTypeStr.length() > 0) {
			dispStringBuff.append(" : "); //$NON-NLS-1$
			dispStringBuff.append(returnTypeStr);
		}
		String dispString = dispStringBuff.toString();

		StringBuilder idStringBuff = new StringBuilder(repStringBuff.toString());
		idStringBuff.append(idargString);
		idStringBuff.append(')');
		String idString = idStringBuff.toString();

		repStringBuff.append(')');
		String repString = repStringBuff.toString();

		final int relevance = function instanceof ICPPMethod
				? RelevanceConstants.METHOD_TYPE_RELEVANCE
				: RelevanceConstants.FUNCTION_TYPE_RELEVANCE;
		CCompletionProposal proposal = createProposal(repString, dispString, idString,
				context.getCompletionNode().getLength(), image, baseRelevance + relevance, context,
				getCElement(function), function.getName());
		if (!context.isContextInformationStyle()) {
			proposal.setCursorPosition(repString.length() - 1);
		}

		if (dispargString.length() > 0) {
			CProposalContextInformation info = new CProposalContextInformation(image, dispString, dispargString);
			info.setContextInformationPosition(context.getContextInformationOffset());
			proposal.setContextInformation(info);
		}

		proposals.add(proposal);
	}

	private void handleVariable(IVariable variable, CContentAssistInvocationContext context, int baseRelevance,
			List<ICompletionProposal> proposals) {
		StringBuilder repStringBuff = new StringBuilder();
		repStringBuff.append(variable.getName());

		String returnTypeStr = "<unknown>"; //$NON-NLS-1$
		try {
			IType varType = variable.getType();
			if (varType != null) {
				returnTypeStr = ASTTypeUtil.getType(varType, false);
			}
		} catch (Exception e) {
		}

		StringBuilder dispStringBuff = new StringBuilder(repStringBuff.toString());
		if (returnTypeStr != null) {
			dispStringBuff.append(" : "); //$NON-NLS-1$
			dispStringBuff.append(returnTypeStr);
		}
		String dispString = dispStringBuff.toString();

		StringBuilder idStringBuff = new StringBuilder(repStringBuff.toString());
		String idString = idStringBuff.toString();

		String repString = repStringBuff.toString();

		Image image = getImage(variable);
		final int relevance = isLocalVariable(variable)
				? RelevanceConstants.LOCAL_VARIABLE_TYPE_RELEVANCE
				: isField(variable)
						? RelevanceConstants.FIELD_TYPE_RELEVANCE
						: RelevanceConstants.VARIABLE_TYPE_RELEVANCE;
		CCompletionProposal proposal = createProposal(repString, dispString, idString,
				context.getCompletionNode().getLength(), image, baseRelevance + relevance, context,
				getCElement(variable), variable.getName());
		proposals.add(proposal);
	}

	private static boolean isField(IVariable variable) {
		return variable instanceof IField;
	}

	private static boolean isLocalVariable(IVariable variable) {
		try {
			return isLocalScope(variable.getScope());
		} catch (DOMException exc) {
			return false;
		}
	}

	private static boolean isLocalScope(IScope scope) {
		while (scope != null) {
			if (scope instanceof ICPPFunctionScope || scope instanceof ICPPBlockScope
					|| scope instanceof ICFunctionScope) {
				return true;
			}
			try {
				scope = scope.getParent();
			} catch (DOMException e) {
				scope = null;
			}
		}
		return false;
	}

	private void handleNamespace(ICPPNamespace namespace, IASTCompletionContext astContext,
			CContentAssistInvocationContext cContext, int baseRelevance, List<ICompletionProposal> proposals) {

		if (astContext instanceof ICPPASTQualifiedName) {
			IASTCompletionContext parent = ((ICPPASTQualifiedName) astContext).getCompletionContext();
			handleNamespace(namespace, parent, cContext, baseRelevance, proposals);
			return;
		}

		StringBuilder repStringBuff = new StringBuilder();
		repStringBuff.append(namespace.getName());

		if (!(astContext instanceof ICPPASTUsingDeclaration) && !(astContext instanceof ICPPASTUsingDirective)) {
			repStringBuff.append("::"); //$NON-NLS-1$
		}

		String repString = repStringBuff.toString();
		proposals.add(createProposal(repString, namespace.getName(), getImage(namespace),
				baseRelevance + RelevanceConstants.NAMESPACE_TYPE_RELEVANCE, cContext, getCElement(namespace),
				namespace.getName()));
	}

	private ICElement getCElement(ICPPNamespace namespace) {
		// ignore
		return null;
	}

	private ICElement getCElement(IVariable variable) {
		return null;
	}

	private ICElement getCElement(IFunction function) {

		return null;
	}

	private ICElement getCElement(ICPPClassType classType) {
		return null;
	}

	private ICElement getCElement(IASTPreprocessorMacroDefinition macro) {
		if (macro != null && macro.getFileLocation() != null) {
			try {
				return getCElement(macro.getFileLocation().getFileName(), macro.getFileLocation().getNodeOffset());
			} catch (Exception e) {
				StatusHandler.log(
						new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN, "Unable to get CElement for Macro", e)); //$NON-NLS-1$
			}
		}
		return null;
	}

	@SuppressWarnings("restriction")
	private ICElement getCElement(IBinding binding) {
		IASTNode definition = null;
		if (binding instanceof ICInternalBinding) {
			IASTNode[] declarations = ((ICInternalBinding) binding).getDeclarations();
			if (declarations != null && declarations.length > 0) {
				definition = declarations[0];
			}
		} else if (binding instanceof ICPPInternalBinding) {
			IASTNode[] declarations = ((ICPPInternalBinding) binding).getDeclarations();
			if (declarations != null && declarations.length > 0) {
				definition = declarations[0];
			}
		}
		if (definition != null && definition.getFileLocation() != null) {
			try {
				return getCElement(definition.getFileLocation().getFileName(),
						definition.getFileLocation().getNodeOffset());
			} catch (CModelException e) {
				StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN,
						"Unable to get CElement for Binding", e)); //$NON-NLS-1$
			}
		}
		return null;
	}

	private ICElement getCElement(String filename, int offset) throws CModelException {

		IFile res = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(filename));

		ITranslationUnit cu = null;
		if (res instanceof IFile) {
			IFile file = res;
			if (CoreModel.isValidTranslationUnitName(null, file.getName())) {
				cu = CoreModelUtil.findTranslationUnit(file);
			} else {
				return null;
			}
		}
		if (cu != null) {
			ICElement ce = cu.getElementAtOffset(offset);
			return ce;
		} else {
			return null;
		}
	}

	private CCompletionProposal createProposal(String repString, String dispString, Image image, int relevance,
			CContentAssistInvocationContext context, ICElement element, String bindingName) {
		return createProposal(repString, dispString, null, context.getCompletionNode().getLength(), image, relevance,
				context, element, bindingName);
	}

	private CCompletionProposal createProposal(String repString, String dispString, int prefixLength, Image image,
			int relevance, CContentAssistInvocationContext context, ICElement element, String bindingName) {
		return createProposal(repString, dispString, null, prefixLength, image, relevance, context, element,
				bindingName);
	}

	private CCompletionProposal createProposal(String repString, String dispString, String idString, int prefixLength,
			Image image, int relevance, CContentAssistInvocationContext context, ICElement element,
			String bindingName) {
		int parseOffset = context.getParseOffset();
		int invocationOffset = context.getInvocationOffset();
		boolean doReplacement = !context.isContextInformationStyle();

		int repLength = doReplacement ? prefixLength : 0;
		int repOffset = doReplacement ? parseOffset - repLength : invocationOffset;
		repString = doReplacement ? repString : ""; //$NON-NLS-1$

		return new CCompletionProposal(repString, repOffset, repLength, image, dispString, idString, relevance,
				context.getViewer(), element, bindingName);
	}

	private Image getImage(ImageDescriptor desc) {
		return desc != null ? CommonImages.getImage(desc) : null;
	}

	private Image getImage(IBinding binding) {
		ImageDescriptor imageDescriptor = null;

		try {
			if (binding instanceof ITypedef) {
				imageDescriptor = CElementImageProvider.getTypedefImageDescriptor();
			} else if (binding instanceof ICompositeType) {
				if (((ICompositeType) binding).getKey() == ICPPClassType.k_class
						|| binding instanceof ICPPClassTemplate) {
					imageDescriptor = CElementImageProvider.getClassImageDescriptor();
				} else if (((ICompositeType) binding).getKey() == ICompositeType.k_struct) {
					imageDescriptor = CElementImageProvider.getStructImageDescriptor();
				} else if (((ICompositeType) binding).getKey() == ICompositeType.k_union) {
					imageDescriptor = CElementImageProvider.getUnionImageDescriptor();
				}
			} else if (binding instanceof ICPPMethod) {
				imageDescriptor = switch (((ICPPMethod) binding).getVisibility()) {
					case ICPPMember.v_private -> CElementImageProvider
							.getMethodImageDescriptor(ASTAccessVisibility.PRIVATE);
					case ICPPMember.v_protected -> CElementImageProvider
							.getMethodImageDescriptor(ASTAccessVisibility.PROTECTED);
					default -> CElementImageProvider.getMethodImageDescriptor(ASTAccessVisibility.PUBLIC);
				};
			} else if (binding instanceof IFunction) {
				imageDescriptor = CElementImageProvider.getFunctionImageDescriptor();
			} else if (binding instanceof ICPPField) {
				imageDescriptor = switch (((ICPPField) binding).getVisibility()) {
					case ICPPMember.v_private -> CElementImageProvider
							.getFieldImageDescriptor(ASTAccessVisibility.PRIVATE);
					case ICPPMember.v_protected -> CElementImageProvider
							.getFieldImageDescriptor(ASTAccessVisibility.PROTECTED);
					default -> CElementImageProvider.getFieldImageDescriptor(ASTAccessVisibility.PUBLIC);
				};
			} else if (binding instanceof IField) {
				imageDescriptor = CElementImageProvider.getFieldImageDescriptor(ASTAccessVisibility.PUBLIC);
			} else if (binding instanceof IVariable) {
				imageDescriptor = CElementImageProvider.getVariableImageDescriptor();
			} else if (binding instanceof IEnumeration) {
				imageDescriptor = CElementImageProvider.getEnumerationImageDescriptor();
			} else if (binding instanceof IEnumerator) {
				imageDescriptor = CElementImageProvider.getEnumeratorImageDescriptor();
			} else if (binding instanceof ICPPNamespace) {
				imageDescriptor = CElementImageProvider.getNamespaceImageDescriptor();
			} else if (binding instanceof ICPPFunctionTemplate) {
				imageDescriptor = CElementImageProvider.getFunctionImageDescriptor();
			} else if (binding instanceof ICPPUsingDeclaration) {
				IBinding[] delegates = ((ICPPUsingDeclaration) binding).getDelegates();
				if (delegates.length > 0) {
					return getImage(delegates[0]);
				}
			}
		} catch (Exception e) {
			handle(e);
		}

		return imageDescriptor != null ? CommonImages.getImage(imageDescriptor) : null;
	}
}
