/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core;

/**
 * Programming languages
 */
public enum Language {

	/** ACTIONSCRIPT */
	ACTIONSCRIPT("ActionScript"),
	/** ADA */
	ADA("Ada"),
	/** APPLESCRIPT */
	APPLESCRIPT("AppleScript"),
	/** ARC */
	ARC("Arc"),
	/** ASP */
	ASP("ASP"),
	/** ASSEMBLY */
	ASSEMBLY("Assembly"),
	/** BATCHFILE */
	BATCHFILE("Batchfile"),
	/** BEFUNGE */
	BEFUNGE("Befunge"),
	/** BLITZMAX */
	BLITZMAX("BlitzMax"),
	/** BOO */
	BOO("Boo"),
	/** BRAINFUCK */
	BRAINFUCK("Brainfuck"),
	/** C */
	C("C"),
	/** CSHARP */
	CSHARP("C#"),
	/** CPLUSPLUS */
	CPLUSPLUS("C++"),
	/** C_OBJDUMP */
	C_OBJDUMP("C-ObjDump"),
	/** CHUCK */
	CHUCK("Chuck"),
	/** CLOJURE */
	CLOJURE("Clojure"),
	/** COFFEESCRIPT */
	COFFEESCRIPT("CoffeeScript"),
	/** COLDFUSION */
	COLDFUSION("ColdFusion"),
	/** COMMON_LISP */
	COMMON_LISP("Common Lisp"),
	/** CPP_OBJDUMP */
	CPP_OBJDUMP("Cpp-ObjDump"),
	/** CSS */
	CSS("CSS"),
	/** CUCUMBER */
	CUCUMBER("Cucumber"),
	/** CYTHON */
	CYTHON("Cython"),
	/** D */
	D("D"),
	/** D_OBJDUMP */
	D_OBJDUMP("D-ObjDump"),
	/** DARCS_PATCH */
	DARCS_PATCH("Darcs Patch"),
	/** DELPHI */
	DELPHI("Delphi"),
	/** DIFF */
	DIFF("Diff"),
	/** DYLAN */
	DYLAN("Dylan"),
	/** EIFFEL */
	EIFFEL("Eiffel"),
	/** EMACS_LISP */
	EMACS_LISP("Emacs Lisp"),
	/** ERLANG */
	ERLANG("Erlang"),
	/** FSHARP */
	FSHARP("F#"),
	/** FACTOR */
	FACTOR("Factor"),
	/** FANCY */
	FANCY("Fancy"),
	/** FORTRAN */
	FORTRAN("FORTRAN"),
	/** GAS */
	GAS("GAS"),
	/** GENSHI */
	GENSHI("Genshi"),
	/** GENTOO_EBUILD */
	GENTOO_EBUILD("Gentoo Ebuild"),
	/** GENTOO_ECLASS */
	GENTOO_ECLASS("Gentoo Eclass"),
	/** GO */
	GO("Go"),
	/** GROFF */
	GROFF("Groff"),
	/** GROOVY */
	GROOVY("Groovy"),
	/** HAML */
	HAML("Haml"),
	/** HASKELL */
	HASKELL("Haskell"),
	/** HAXE */
	HAXE("HaXe"),
	/** HTML */
	HTML("HTML"),
	/** HTML_DJANGO */
	HTML_DJANGO("HTML+Django"),
	/** HTML_ERB */
	HTML_ERB("HTML+ERB"),
	/** HTML_PHP */
	HTML_PHP("HTML+PHP"),
	/** INI */
	INI("INI"),
	/** IO */
	IO("Io"),
	/** IRC_LOG */
	IRC_LOG("IRC log"),
	/** JAVA */
	JAVA("Java"),
	/** JAVA_SERVER_PAGE */
	JAVA_SERVER_PAGE("Java Server Pages"),
	/** JAVASCRIPT */
	JAVASCRIPT("JavaScript"),
	/** LILYPOND */
	LILYPOND("LilyPond"),
	/** LITERATE_HASKELL */
	LITERATE_HASKELL("Literate Haskell"),
	/** LLVM */
	LLVM("LLVM"),
	/** LUA */
	LUA("Lua"),
	/** MAKEFILE */
	MAKEFILE("Makefile"),
	/** MAKO */
	MAKO("Mako"),
	/** MARKDOWN */
	MARKDOWN("Markdown"),
	/** MATLAB */
	MATLAB("Matlab"),
	/** MAX_MSP */
	MAX_MSP("Max/MSP"),
	/** MIRAH */
	MIRAH("Mirah"),
	/** MOOCODE */
	MOOCODE("Moocode"),
	/** MUPAD */
	MUPAD("mupad"),
	/** MYGHTY */
	MYGHTY("Myghty"),
	/** NIMROD */
	NIMROD("Nimrod"),
	/** NU */
	NU("Nu"),
	/** NUMPY */
	NUMPY("NumPy"),
	/** OBJDUMP */
	OBJDUMP("ObjDump"),
	/** OBJECTIVE_C */
	OBJECTIVE_C("Objective-C"),
	/** OBJECTIVE_J */
	OBJECTIVE_J("Objective-J"),
	/** OCAML */
	OCAML("OCaml"),
	/** OOC */
	OOC("ooc"),
	/** OPENCL */
	OPENCL("OpenCL"),
	/** PARROT_INTERNAL_REPRESENTATION */
	PARROT_INTERNAL_REPRESENTATION("Parrot Internal Representation"),
	/** PERL */
	PERL("Perl"),
	/** PROLOG */
	PROLOG("Prolog"),
	/** PHP */
	PHP("PHP"),
	/** PURE_DATA */
	PURE_DATA("Pure Data"),
	/** PYTHON */
	PYTHON("Python"),
	/** R */
	R("R"),
	/** RACKET */
	RACKET("Racket"),
	/** RAW_TOKEN_DATA */
	RAW_TOKEN_DATA("Raw token data"),
	/** REBOL */
	REBOL("Rebol"),
	/** REDCODE */
	REDCODE("Redcode"),
	/** RESTRUCTUREDTEXT */
	RESTRUCTUREDTEXT("reStructuredText"),
	/** RHTML */
	RHTML("RHTML"),
	/** RUBY */
	RUBY("Ruby"),
	/** SASS */
	SASS("Sass"),
	/** SCALA */
	SCALA("Scala"),
	/** SCHEME */
	SCHEME("Scheme"),
	/** SELF */
	SELF("Self"),
	/** SHELL */
	SHELL("Shell"),
	/** SMALLTALK */
	SMALLTALK("Smalltalk"),
	/** SMARTY */
	SMARTY("Smarty"),
	/** STANDARD_ML */
	STANDARD_ML("Standard ML"),
	/** SUPERCOLLIDER */
	SUPERCOLLIDER("SuperCollider"),
	/** TCL */
	TCL("Tcl"),
	/** TCSH */
	TCSH("Tcsh"),
	/** TEX */
	TEX("TeX"),
	/** TEXT */
	TEXT("Text"),
	/** TEXTILE */
	TEXTILE("Textile"),
	/** VALA */
	VALA("Vala"),
	/** VERILOG */
	VERILOG("Verilog"),
	/** VHDL */
	VHDL("VHDL"),
	/** VIML */
	VIML("VimL"),
	/** VISUAL_BASIC */
	VISUAL_BASIC("Visual Basic"),
	/** XML */
	XML("XML"),
	/** XQUERY */
	XQUERY("XQuery"),
	/** XS */
	XS("XS"),
	/** YAML */
	YAML("YAML");

	private final String value;

	Language(String value) {
		this.value = value;
	}

	/**
	 * Get value
	 * 
	 * @return value
	 */
	public String getValue() {
		return this.value;
	}
}
