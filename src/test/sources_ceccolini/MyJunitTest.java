//package junitPackage;
//
//import static org.junit.Assert.*;
//
//import org.junit.Test; // for @Test
//import org.junit.Before; // for @Before
//
//import java.util.List;
//import java.io.FileInputStream;
//import java.util.ArrayList;
//
//import org.antlr.v4.runtime.ANTLRInputStream;
//import org.antlr.v4.runtime.CommonTokenStream;
//import org.antlr.v4.runtime.ParserRuleContext;
//
//import parser.ExecuteVM;
//import parser.FOOLLexer;
//import parser.FOOLParser;
//import parser.MyLexerErrorListener;
//import parser.MyParserErrorListener;
//import parser.SVMLexer;
//import parser.SVMParser;
//import util.Environment;
//import util.MyTypeError;
//import util.SemanticError;
//import util.TypeErrorException;
//import ast.FoolVisitorImpl;
//import ast.Node;
//
//public class MyJunitTest {
//
//	private List<Tris<String, String, Boolean>> semanticTest = new ArrayList<Tris<String, String, Boolean>>();
//	private List<Tris<String, String, Boolean>> typecheckTest = new ArrayList<Tris<String, String, Boolean>>();
//	private List<Tris<String, String, CodeGenTestClass>> codegenTest = new ArrayList<Tris<String, String, CodeGenTestClass>>();
//
//	@Before
//	public void initializeSemantic() {
//		addToSemanticTest("e01.fool", "CORRETTO: ProgExp, IntNode", true);
//		addToSemanticTest("e02.fool",
//				"CORRETTO: ProgLetIn, assegnamento, IdNode", true);
//		addToSemanticTest("e03.fool",
//				"CORRETTO: ProgLetIn, assegamento doppio, IdNode", true);
//		addToSemanticTest("e04.fool",
//				"CORRETTO: ProgLetIn, assegamento doppio, PlusNode", true);
//		addToSemanticTest("e05.fool",
//				"CORRETTO: ProgLetIn, dic. funzione, CallNode", true);
//
//		addToSemanticTest("e06.fool", "CORRETTO: ProgClassNode, IntNode", true);
//		addToSemanticTest("e07.fool",
//				"CORRETTO: ProgClassNode, NewNode, CallMethodNode", true);
//		addToSemanticTest("e08.fool",
//				"CORRETTO: ProgClassNode, implements, object field", true);
//		addToSemanticTest("e09.fool",
//				"ERRORE: la classe implementa una classe inesistente", false);
//		addToSemanticTest("e10.fool", "ERRORE: la classe implementa se stessa",
//				false);
//
//		addToSemanticTest("e11.fool",
//				"CORRETTO: ProgClassNode, FunNode, IfNode, IntNode", true);
//		addToSemanticTest("e12.fool",
//				"ERRORE: variabile con nome a caso non dichiarata", false);
//		addToSemanticTest("e13.fool",
//				"CORRETTO: ProgClassNode, FunNode con nome della classe", true);
//		addToSemanticTest("e14.fool",
//				"ERRORE: chiamata su un oggetto no dichiarato", false);
//		addToSemanticTest("e15.fool",
//				"ERRORE: chiamata metodo su un non oggetto", false);
//
//		addToSemanticTest("e16.fool",
//				"ERRORE: A = new B, chiamata ad un metodo della classe figlia",
//				false);
//		// aggiungere funzione che dichiara funzione
//	}
//
//	@Before
//	public void initializeTypeCheck() {
//		addToTypecheckTest("et01.fool", "CORRETTO: ProgExp, IntNode", true);
//		addToTypecheckTest("et02.fool",
//				"CORRETTO: ProgLetIn, assegamento doppio, IdNode", true);
//		addToTypecheckTest("et03.fool", "ERRORE: bool+int", false);
//		addToTypecheckTest("et04.fool", "ERRORE: bool = int", false);
//		addToTypecheckTest("et05.fool", "CORRETTO: ProgClassNode, IntNode",
//				true);
//
//		addToTypecheckTest("et06.fool",
//				"ERRORE: tipo di ritorno di un metodo errato", false);
//		addToTypecheckTest("et07.fool",
//				"CORRETTO: ProgClassNode, implements, CallMethodNode", true);
//		addToTypecheckTest(
//				"et08.fool",
//				"ERRORE: tipo passato al creator errato, non sottotipo (td1 non implementa t1)",
//				false);
//		addToTypecheckTest("et09.fool",
//				"CORRETTO: ProgClassNode, NewNode, IntNode", true);
//		
//		addToTypecheckTest("et10.fool",
//				"ERRORE: tipo di ritorno di un metodo sovrascritto errato",
//				false);
//
//		addToTypecheckTest("et11.fool",
//				"CORRETTO: ProgClassNode, implements, object field, IntNode",
//				true);
//		addToTypecheckTest(
//				"et12.fool",
//				"CORRETTO: ProgClassNode, implements, object field, SubTyping, IntNode",
//				true);
//		addToTypecheckTest("et13.fool",
//				"ERRORE: override campo di una classe con un non sottotipo",
//				false);
//		addToTypecheckTest("et14.fool", "CORRETTO: PlusNode, IntNode, IfNode",
//				true);
//		addToTypecheckTest("et15.fool",
//				"ERRORE: if con tipi errati nei branch, tipi semplici", false);
//
//		addToTypecheckTest("et16.fool",
//				"ERRORE: funzione chiamata di funzione su una variabile", false);
//		addToTypecheckTest("et17.fool",
//				"CORRETTO: istanziazione oggetto senza la new", true);
//		addToTypecheckTest("et18.fool",
//				"ERRORE: classi identiche ma nome diverso", false);
//
//		addToTypecheckTest("et20.fool",
//				"ERRORE: numero parametri funzione errato", false);
//
//		addToTypecheckTest("et21.fool", "ERRORE: A = new B, parametro errato",
//				false);
//	}
//
//	@Before
//	public void initializeCodegen() {
//		// sp (non controllato), hp, fp, rv, finalHead
//		addToCodegenTest("ec01.fool", "commento", new CodeGenTestClass(9998, 0,
//				10000, 0, 1));
//		addToCodegenTest("codegen01.fool", "commento", new CodeGenTestClass(
//				9996, 0, 10000, 0, 320));
//		addToCodegenTest("codegen03.fool", "chiamata metodo",
//				new CodeGenTestClass(0, 3, 10000, 33, 33));
//		addToCodegenTest("codegen02.fool",
//				"dichiarazione funzione dentro metodo", new CodeGenTestClass(0,
//						4, 10000, 40, 40));
//
//		addToCodegenTest("codegen04.fool",
//				"chiamata metodo che chiama metodo che chiama metodo che ...",
//				new CodeGenTestClass(0, 6, 10000, 20, 20));
//		addToCodegenTest("codegen05.fool",
//				"metodo che richiama metodo di campo della classe",
//				new CodeGenTestClass(0, 4, 10000, 25, 25));
//
//		addToCodegenTest("codegen06.fool",
//				"metodo che prende oggetto e richiama metodo",
//				new CodeGenTestClass(0, 4, 10000, 25, 25));
//		
//		addToCodegenTest("codegen07.fool",
//				"esempio complesso Claudio",
//				new CodeGenTestClass(0, 8, 10000, 6, 6));
//		
//		addToCodegenTest("codegen09.fool",
//				"metodi che chiamano metodi",
//				new CodeGenTestClass(0, 3, 10000, 3, 3));
//		
//		addToCodegenTest("codegen10.fool",
//				"metodi che chiamano metodi, + codice inutile ma presente",
//				new CodeGenTestClass(0, 16, 10000, 3, 3));
//		
//		addToCodegenTest("codegen11.fool",
//				"caso classi senza attributi",
//				new CodeGenTestClass(0, 6, 10000, 3, 3));
//		
//		addToCodegenTest("codegen12.fool",
//				"metodo che torna oggetto, chiamata sull'oggetto",
//				new CodeGenTestClass(0, 9, 10000, 5, 5));
//		
//		addToCodegenTest("codegen13.fool",
//				"ricorsione, fattoriale di 5 * 2",
//				new CodeGenTestClass(0, 0, 10000, 120, 240));
//		
//	}
//
//	@Test
//	public void SemanticAssert() {
//		for (Tris<String, String, Boolean> t : this.semanticTest) {
//			try {
//				assertEquals(
//						t.getT2(),
//						t.getT3(),
//						this.initSemantic("test/sources/" + t.getT1()).size() == 0);
//			} catch (AssertionError e) {
//				System.err.println(t.getT1() + " - failed");
//				throw e;
//			}
//		}
//	}
//
//	@Test
//	public void TypecheckAssert() {
//		for (Tris<String, String, Boolean> t : this.typecheckTest) {
//			this.initTypeCheck("test/sources/" + t.getT1());
//			try {
//				assertEquals(t.getT2(), t.getT3(), MyTypeError.getInstance()
//						.getErrorsNumber() == 0);
//			} catch (AssertionError e) {
//				System.err.println(t.getT1() + " - failed");
//				throw e;
//			}
//			MyTypeError.getInstance().resetForTest();
//		}
//	}
//
//	@Test
//	public void CodeGenAssert() {
//		for (Tris<String, String, CodeGenTestClass> t : this.codegenTest) {
//			CodeGenTestClass c = this.initCodegen("test/sources/" + t.getT1());
//			try {
//				assertEquals(t.getT2(), true, c.equals(t.getT3()));
//				// System.out.println(t.getT2() + " - passed");
//			} catch (AssertionError e) {
//				System.err.println(t.getT1() + " - failed");
//				throw e;
//			}
//		}
//	}
//
//	// @Test
//	// public void CodegenAssert() {
//	// for(Tris<String,String,CodeGenTestClass> t : this.codegenTest){
//	// //assertEquals(t.getT2(),t.getT3(),this.initSemantic("test/sources/"+t.getT1()).size()
//	// == 0);
//	// }
//	// }
//
//	private void addToSemanticTest(String file, String info, Boolean res) {
//		this.semanticTest
//				.add(new Tris<String, String, Boolean>(file, info, res));
//	}
//
//	private void addToTypecheckTest(String file, String info, Boolean res) {
//		this.typecheckTest.add(new Tris<String, String, Boolean>(file, info,
//				res));
//	}
//
//	private void addToCodegenTest(String file, String info, CodeGenTestClass res) {
//		this.codegenTest.add(new Tris<String, String, CodeGenTestClass>(file,
//				info, res));
//	}
//
//	@Test
//	public void ordineNonConta() throws TypeErrorException {
//		// -----
//		assertEquals("", true, this.initSemantic("test/sources/ordine01.fool")
//				.size() == 0);
//		this.initTypeCheck("test/sources/ordine01.fool");
//		assertEquals("", true, MyTypeError.getInstance().getErrorsNumber() == 0);
//		MyTypeError.getInstance().resetForTest();
//
//		// -----
//		assertEquals("", true, this.initSemantic("test/sources/ordine02.fool")
//				.size() == 0);
//		this.initTypeCheck("test/sources/ordine02.fool");
//		assertEquals("", true, MyTypeError.getInstance().getErrorsNumber() == 0);
//		MyTypeError.getInstance().resetForTest();
//
//		// ----- con new ma senza usare poi l'oggetto
//		assertEquals("", true, this.initSemantic("test/sources/ordine03.fool")
//				.size() == 0);
//		this.initTypeCheck("test/sources/ordine03.fool");
//		assertEquals("", true, MyTypeError.getInstance().getErrorsNumber() == 0);
//		MyTypeError.getInstance().resetForTest();
//
//		// ----- con new utilizzando l'oggetto
//		assertEquals("", true, this.initSemantic("test/sources/ordine04.fool")
//				.size() == 0);
//		this.initTypeCheck("test/sources/ordine04.fool");
//		assertEquals("", true, MyTypeError.getInstance().getErrorsNumber() == 0);
//		MyTypeError.getInstance().resetForTest();
//
//		// //----- con new utilizzando l'oggetto
//		// assertEquals("", true,
//		// this.initSemantic("test/sources/ordine06.fool").size() == 0);
//		// this.initTypeCheck("test/sources/ordine06.fool");
//		// assertEquals("", true, MyTypeError.getInstance().getErrorsNumber() ==
//		// 0);
//		// MyTypeError.getInstance().resetForTest();
//
//	}
//
//	private ArrayList<SemanticError> initSemantic(String code) {
//		System.out.println("ESECUZIONE SEMANTICA:   " + code);
//		try {
//			FileInputStream is = new FileInputStream(code);
//			ANTLRInputStream input = new ANTLRInputStream(is);
//			FOOLLexer lexer = new FOOLLexer(input);
//			lexer.removeErrorListeners();
//			lexer.addErrorListener(MyLexerErrorListener.INSTANCE);
//
//			CommonTokenStream tokens = new CommonTokenStream(lexer);
//
//			FOOLParser parser = new FOOLParser(tokens);
//			parser.removeErrorListeners();
//			parser.addErrorListener(MyParserErrorListener.INSTANCE);
//
//			ParserRuleContext tree = parser.prog();
//			int lexicalErrors = MyLexerErrorListener.INSTANCE.getErrorsNumber();
//			int syntaxErrors = MyParserErrorListener.INSTANCE.getErrorsNumber();
//
//			if (lexicalErrors != 0 || syntaxErrors != 0) {
//				// MyLexerErrorListener.INSTANCE.printErrors();
//				// MyParserErrorListener.INSTANCE.printErrors();
//			} else {
//
//				FoolVisitorImpl visitor = new FoolVisitorImpl();
//				Node ast = visitor.visit(tree); // generazione ASTe
//
//				Environment env = new Environment();
//				ArrayList<SemanticError> err = ast.checkSemantics(env);
//				return err;
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private void initTypeCheck(String code) {
//		System.out.println("ESECUZIONE TYPECHECK:   " + code);
//		try {
//			FileInputStream is = new FileInputStream(code);
//			ANTLRInputStream input = new ANTLRInputStream(is);
//			FOOLLexer lexer = new FOOLLexer(input);
//			lexer.removeErrorListeners();
//			lexer.addErrorListener(MyLexerErrorListener.INSTANCE);
//
//			CommonTokenStream tokens = new CommonTokenStream(lexer);
//
//			FOOLParser parser = new FOOLParser(tokens);
//			parser.removeErrorListeners();
//			parser.addErrorListener(MyParserErrorListener.INSTANCE);
//
//			ParserRuleContext tree = parser.prog();
//			int lexicalErrors = MyLexerErrorListener.INSTANCE.getErrorsNumber();
//			int syntaxErrors = MyParserErrorListener.INSTANCE.getErrorsNumber();
//
//			if (lexicalErrors != 0 || syntaxErrors != 0) {
//				// MyLexerErrorListener.INSTANCE.printErrors();
//				// MyParserErrorListener.INSTANCE.printErrors();
//			} else {
//
//				FoolVisitorImpl visitor = new FoolVisitorImpl();
//				Node ast = visitor.visit(tree); // generazione ASTe
//
//				Environment env = new Environment();
//				@SuppressWarnings("unused")
//				ArrayList<SemanticError> err = ast.checkSemantics(env);
//				try {
//					@SuppressWarnings("unused")
//					Node type = ast.typeCheck(); // type-checking bottom-up
//
//				} catch (TypeErrorException e) {
//					// System.out.println(e.toString());
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private CodeGenTestClass initCodegen(String code) {
//		System.out.println("ESECUZIONE CODEGEN:   " + code);
//		try {
//			FileInputStream is = new FileInputStream(code);
//			ANTLRInputStream input = new ANTLRInputStream(is);
//			FOOLLexer lexer = new FOOLLexer(input);
//			lexer.removeErrorListeners();
//			lexer.addErrorListener(MyLexerErrorListener.INSTANCE);
//
//			CommonTokenStream tokens = new CommonTokenStream(lexer);
//
//			FOOLParser parser = new FOOLParser(tokens);
//			parser.removeErrorListeners();
//			parser.addErrorListener(MyParserErrorListener.INSTANCE);
//
//			ParserRuleContext tree = parser.prog();
//			int lexicalErrors = MyLexerErrorListener.INSTANCE.getErrorsNumber();
//			int syntaxErrors = MyParserErrorListener.INSTANCE.getErrorsNumber();
//
//			if (lexicalErrors != 0 || syntaxErrors != 0) {
//				// MyLexerErrorListener.INSTANCE.printErrors();
//				// MyParserErrorListener.INSTANCE.printErrors();
//			} else {
//
//				FoolVisitorImpl visitor = new FoolVisitorImpl();
//				Node ast = visitor.visit(tree); // generazione ASTe
//
//				Environment env = new Environment();
//				@SuppressWarnings("unused")
//				ArrayList<SemanticError> err = ast.checkSemantics(env);
//				try {
//					@SuppressWarnings("unused")
//					Node type = ast.typeCheck(); // type-checking bottom-up
//					if (MyTypeError.getInstance().getErrorsNumber() > 0) {
//						// MyTypeError.getInstance().printErrors();
//						System.exit(1);
//					} else {
//						// System.out
//						// .println(type
//						// .toPrint("Type checking ok! Type of the program is: "));
//						// CODE GENERATION prova.fool.asm
//						String generatedCode = ast.codeGeneration();
//						// BufferedWriter out = new BufferedWriter(new
//						// FileWriter(
//						// code + ".asm"));
//						// out.write(generatedCode);
//						// out.close();
//						// System.out
//						// .println("Code generated! Assembling and running generated code.");
//						//
//
//						// FileInputStream isASM = new FileInputStream(code
//						// + ".asm");
//
//						ANTLRInputStream inputASM = new ANTLRInputStream(
//								generatedCode);
//						SVMLexer lexerASM = new SVMLexer(inputASM);
//						CommonTokenStream tokensASM = new CommonTokenStream(
//								lexerASM);
//						SVMParser parserASM = new SVMParser(tokensASM);
//
//						parserASM.assembly();
//
//						// System.out.println("You had: " +
//						// lexerASM.lexicalErrors
//						// + " lexical errors and "
//						// + parserASM.getNumberOfSyntaxErrors()
//						// + " syntax errors.");
//						if (lexerASM.lexicalErrors > 0
//								|| parserASM.getNumberOfSyntaxErrors() > 0)
//							System.exit(1);
//
//						// System.out.println("Starting Virtual Machine...");
//						ExecuteVM vm = new ExecuteVM(parserASM.code);
//						vm.setDebug(false);
//						vm.cpu();
//
//						return new CodeGenTestClass(vm.getSp(), vm.getHp(),
//								vm.getFp(), vm.getRv(), vm.getMemory());
//					}
//
//				} catch (TypeErrorException e) {
//					// System.out.println(e.toString());
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return null;
//	}
//
//}
