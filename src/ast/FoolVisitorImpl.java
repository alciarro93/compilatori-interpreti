package ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import parser.*;
import parser.FOOLParser.BaseExpContext;
import parser.FOOLParser.BoolValContext;
import parser.FOOLParser.ClassExpContext;
import parser.FOOLParser.ClassdecContext;
import parser.FOOLParser.DecContext;
import parser.FOOLParser.ExpContext;
import parser.FOOLParser.FactorContext;
import parser.FOOLParser.FunContext;
import parser.FOOLParser.FunDeclarationContext;
import parser.FOOLParser.FunExpContext;
import parser.FOOLParser.IfExpContext;
import parser.FOOLParser.IntValContext;
import parser.FOOLParser.LetContext;
import parser.FOOLParser.LetInExpContext;
import parser.FOOLParser.MethodExpContext;
import parser.FOOLParser.NewExpContext;
import parser.FOOLParser.SingleExpContext;
import parser.FOOLParser.TermContext;
import parser.FOOLParser.ThisExpContext;
import parser.FOOLParser.TypeContext;
import parser.FOOLParser.VarAssignmentContext;
import parser.FOOLParser.VarExpContext;
import parser.FOOLParser.VarasmContext;
import parser.FOOLParser.VardecContext;
import util.SemanticError;
import util.Util;

public class FoolVisitorImpl extends FOOLBaseVisitor<Node> {

	@Override
	public Node visitSingleExp(SingleExpContext ctx) {

		// simply return the result of the visit to the inner exp
		return visit(ctx.exp());

	}

	@Override
	public Node visitLetInExp(LetInExpContext ctx) {

		// resulting node of the right type
		ProgLetInNode res;

		// list of declarations in @res
		ArrayList<Node> declarations = new ArrayList<Node>();

		// visit all nodes corresponding to declarations inside the *let context* and
		// store them in @declarations
		// notice that the ctx.let().dec() method returns a list, this is because of the
		// use of * or + in the grammar
		// antlr detects this is a group and therefore returns a list
		for (DecContext dc : ctx.let().dec()) {
			declarations.add(visit(dc));
		}

		// visit exp context
		Node exp = visit(ctx.exp());

		// build @res accordingly with the result of the visits to its content
		res = new ProgLetInNode(declarations, exp);

		return res;
	}

	/**
	 * @author antonio TODO first entry of rule. This method will start the
	 *         implementation of class in fool
	 */
	@Override
	public Node visitClassExp(ClassExpContext ctx) {

		ArrayList<Node> classes = new ArrayList<Node>();

		// visit all node of (classdec)+ inside grammar and save the results inside an
		// ArrayList of Node
		for (ClassdecContext cd : ctx.classdec()) {
			// add visit single class
			classes.add(visit(cd));
		}

		ClassExpNode node = new ClassExpNode(classes, visit(ctx.exp()));
		// Isn't exist @visitLet in this class, because maybe is more comfortable
		// handler the let node in this way, without create a specific node but
		// in every method that require let. BUT keep in mind that every let has @DEC+.
		// This follow that it must handler with arrayList

		if (ctx.let() != null) { // ? --> implements 0 or 1
			// if there are visit each dec and add it to the @innerDec list

			for (DecContext dc : ctx.let().dec())
				node.addLetInnerDec(visit(dc));

		}

		// return all (classdec)+ and the rest of body
		return node;
	}

	/**
	 * @author antonio TODO implementation classdec node
	 */
	@Override
	public Node visitClassdec(ClassdecContext ctx) {

		// ID CLASS
		String id = ctx.ID().get(0).getText();
		ClassDecNode node = new ClassDecNode(id);
		// IMPLEMENTS ?
		if (ctx.ID().size() > 1) {
			String impl = ctx.ID().get(1).getText(); // implements ? --> 0 or 1
			node.setImpl(impl);
		}

		// The different between this and @varasm is that there are only one visit
		// to @vardec node. In this case, instead there are multiple possibilities. For
		// this:

		for (VardecContext dc : ctx.vardec()) {
			node.addFieldToClass(visit(dc));
		}

		for (FunContext fun : ctx.fun()) {
			node.addMethodToClass(visit(fun));
		}

		return node;
	}

	/**
	 * @author antonio TODO COMMENTATA, FORSE NON SERVE
	 */
	@Override
	public Node visitVardec(VardecContext ctx) {

		Node tipo = visit(ctx.type());
		return new VarDecNode(ctx.ID().getText(), tipo);
	}

	@Override
	public Node visitVarasm(VarasmContext ctx) {

		// visit the type
		//Node typeNode = visit(ctx.vardec().type());

		// visit the exp
		Node expNode = visit(ctx.exp());

		// build the varNode
		return new VarAsmNode(visit(ctx.vardec()), expNode);
	}

	@Override
	public Node visitFun(FunContext ctx) {

		// initialize @res with the visits to the type and its ID
		FunNode res = new FunNode(ctx.ID().getText(), visit(ctx.type()));

		// add argument declarations
		// we are getting a shortcut here by constructing directly the ParNode
		// this could be done differently by visiting instead the VardecContext
		for (VardecContext vc : ctx.vardec())
			res.addPar(new VarDecNode(vc.ID().getText(), visit(vc.type())));

		// add body
		// create a list for the nested declarations
		ArrayList<Node> innerDec = new ArrayList<Node>();

		// check whether there are actually nested decs
		if (ctx.let() != null) {
			// if there are visit each dec and add it to the @innerDec list
			for (DecContext dc : ctx.let().dec())
				innerDec.add(visit(dc));
		}

		// get the exp body
		Node exp = visit(ctx.exp());

		// add the body and the inner declarations to the function
		res.addLetInExp(innerDec, exp);

		return res;

	}

	/**
	 * @author antonio TODO implements è il primo da implementare
	 */
	// @Override
	// public Node visitVarAssignment(VarAssignmentContext ctx) {
	// return null;
	// }

	
	@Override
	public Node visitType(TypeContext ctx) {
		
		if (ctx.getText().equals("int"))
			return new IntTypeNode();
		else if (ctx.getText().equals("bool"))
			return new BoolTypeNode();
		else return new ClassTypeNode(ctx.getText());

		// this will never happen thanks to the parser
		//return null;

	}

	@Override
	public Node visitExp(ExpContext ctx) {

		// this could be enhanced
		// check whether this is a simple or binary expression
		// notice here the necessity of having named elements in the grammar
		if (ctx.right == null) {
			// it is a simple expression
			return visit(ctx.left);
		} else {

			if (ctx.children.get(1).equals(ctx.MINUS())) {
				return new MinusNode(visit(ctx.left), visit(ctx.right));

			} else {
				// it is a binary expression, you should visit left and right
				return new PlusNode(visit(ctx.left), visit(ctx.right));
			}

		}

	}

	@Override
	public Node visitTerm(TermContext ctx) {
		// check whether this is a simple or binary expression
		// notice here the necessity of having named elements in the grammar
		if (ctx.right == null) {
			// it is a simple expression
			return visit(ctx.left);
		} else {

			if (ctx.children.get(1).equals(ctx.DIV())) {
				return new DivNode(visit(ctx.left), visit(ctx.right));
			} else {
				// it is a binary expression, you should visit left and right
				return new MultNode(visit(ctx.left), visit(ctx.right));
			}

		}
	}

	@Override
	public Node visitFactor(FactorContext ctx) {
		// check whether this is a simple or binary expression
		// notice here the necessity of having named elements in the grammar
		if (ctx.right == null) {
			// it is a simple expression
			return visit(ctx.left);
		} else {
			// it is a binary expression, you should visit left and right
			return new EqualNode(visit(ctx.left), visit(ctx.right));
		}
	}

	@Override
	public Node visitIntVal(IntValContext ctx) {
		// notice that this method is not actually a rule but a named production #intVal

		// there is no need to perform a check here, the lexer ensures this text is an
		// int
		return new IntNode(Integer.parseInt(ctx.INTEGER().getText()));
	}

	@Override
	public Node visitBoolVal(BoolValContext ctx) {

		// there is no need to perform a check here, the lexer ensures this text is a
		// boolean
		return new BoolNode(Boolean.parseBoolean(ctx.getText()));
	}

	@Override
	public Node visitBaseExp(BaseExpContext ctx) {

		// this is actually nothing in the sense that for the ast the parenthesis are
		// not relevant
		// the thing is that the structure of the ast will ensure the operational order
		// by giving
		// a larger depth (closer to the leafs) to those expressions with higher
		// importance

		// this is actually the default implementation for this method in the
		// FOOLBaseVisitor class
		// therefore it can be safely removed here

		return visit(ctx.exp());

	}

	@Override
	public Node visitIfExp(IfExpContext ctx) {

		// create the resulting node
		IfNode res;

		// visit the conditional, then the then branch, and then the else branch
		// notice once again the need of named terminals in the rule, this is because
		// we need to point to the right expression among the 3 possible ones in the
		// rule

		Node condExp = visit(ctx.cond);

		Node thenExp = visit(ctx.thenBranch);

		Node elseExp = visit(ctx.elseBranch);

		// build the @res properly and return it
		res = new IfNode(condExp, thenExp, elseExp);

		return res;
	}

	@Override
	public Node visitVarExp(VarExpContext ctx) {
		// this corresponds to a variable access
		return new IdNode(ctx.ID().getText());

	}

	@Override
	public Node visitFunExp(FunExpContext ctx) {
		// this corresponds to a function invocation

		// declare the result
		Node res;

		// get the invocation arguments
		ArrayList<Node> args = new ArrayList<Node>();

		for (ExpContext exp : ctx.exp())
			args.add(visit(exp));

		// especial check for stdlib func
		// this is WRONG, THIS SHOULD BE DONE IN A DIFFERENT WAY
		// JUST IMAGINE THERE ARE 800 stdlib functions...
		if (ctx.ID().getText().equals("print"))
			res = new PrintNode(args.get(0));

		else
			// instantiate the invocation
			res = new FunExpNode(ctx.ID().getText(), args);

		return res;
	}

	/**
	 * @author antonio 
	 */
	 @Override
	 public Node visitMethodExp(MethodExpContext ctx) {
		 
		 MethodExpNode node = new MethodExpNode();
		 
		 // there are 2 ID and no THIS
		 if(ctx.ID().size()>1) {
			 String firstID = ctx.ID(0).getText();
			 String secondID = ctx.ID(1).getText();
			 node.setFirstID(firstID);
			 node.setMethodClass(secondID);
		 }else {
			 
			 String id = ctx.ID(0).getText();
			 node.setMethodClass(id);
		 }
		 
		 
		// Add @exp to arrayList and set this inside arrayList of node
			for (ExpContext e : ctx.exp()) {
				node.addExpList(visit(e));
			}
		
	 return node;
	 }

	/**
	 * @author antonio TODO implements
	 */
	@Override
	public Node visitNewExp(NewExpContext ctx) {

		// ID
		String id = ctx.ID().getText();
		NewExpNode node = new NewExpNode(id);

		// Add @exp to arrayList and set this inside arrayList of node
		for (ExpContext e : ctx.exp()) {
			node.addExpList(visit(e));
		}
		return node;
	}

}
