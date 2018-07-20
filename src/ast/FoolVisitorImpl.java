package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.TerminalNode;

import parser.*;
import parser.FOOLParser.BaseExpContext;
import parser.FOOLParser.BoolValContext;
import parser.FOOLParser.DecContext;
import parser.FOOLParser.ExpContext;
import parser.FOOLParser.FactorContext;
import parser.FOOLParser.FunContext;
import parser.FOOLParser.FunExpContext;
import parser.FOOLParser.IfExpContext;
import parser.FOOLParser.IntValContext;
import parser.FOOLParser.LetContext;
import parser.FOOLParser.LetInExpContext;
import parser.FOOLParser.ClassExpContext;
import parser.FOOLParser.ClassdecContext;
import parser.FOOLParser.MethodExpContext;
import parser.FOOLParser.NewExpContext;
import parser.FOOLParser.OperatorContext;
import parser.FOOLParser.SingleExpContext;
import parser.FOOLParser.TermContext;
import parser.FOOLParser.TypeContext;
import parser.FOOLParser.VarExpContext;
import parser.FOOLParser.VarasmContext;
import parser.FOOLParser.VardecContext;
import util.SemanticError;

public class FoolVisitorImpl extends FOOLBaseVisitor<Node> {

	@Override
	public Node visitClassExp(ClassExpContext ctx) {

		ClassExpNode res;
		ArrayList<ClassdecContext> declist = new ArrayList<ClassdecContext>();

		for (ClassdecContext dc : ctx.classdec()) {

			declist.add(dc);

			if (dc.IMPLEMENTS() != null) {
				// System.out.println("\n I am: "+dc.ID(0)+" and my superclass is: "+dc.ID(1));
			}
			// declarations.add( visit(dc) );
		}

		// visit exp context
		ArrayList<Node> let = null;

		int counter = 0;
		if (ctx.let() != null) {
			let = new ArrayList<Node>();
			for (DecContext d : ctx.let().dec()) {
				let.add(visit(ctx.let().dec(counter)));
				counter++;
			}
		}

		Node exp = visit(ctx.exp());
		Node print = null;
		if (ctx.PRINT() != null) {
			print = new PrintNode(exp);
		}
		// build @res accordingly with the result of the visits to its content
		res = new ClassExpNode(declist, exp, let, print);

		return res;
	}

	@Override
	public Node visitLetInExp(LetInExpContext ctx) {

		// resulting node of the right type
		ProgLetInNode res;

		// list of declarations in @res
		ArrayList<Node> declarations = new ArrayList<Node>();

		// visit all nodes corresponding to declarations inside the let context and
		// store them in @declarations
		// notice that the ctx.let().dec() method returns a list, this is because of the
		// use of * or + in the grammar
		// antlr detects this is a group and therefore returns a list
		for (DecContext dc : ctx.let().dec()) {
			declarations.add(visit(dc));
		}

		// visit exp context
		Node exp = visit(ctx.exp());

		Node print = null;
		if (ctx.PRINT() != null) {
			print = new PrintNode(exp);
		}

		// build @res accordingly with the result of the visits to its content
		res = new ProgLetInNode(declarations, exp, print);

		return res;
	}

	@Override
	public Node visitSingleExp(SingleExpContext ctx) {

		// simply return the result of the visit to the inner exp
		return visit(ctx.exp());

	}

	@Override
	public Node visitClassdec(ClassdecContext ctx) {

		// declare the result node
		VarNode result;

		// visit the type
		Node typeNode1 = null;
		ArrayList<Node> typeNode = new ArrayList<Node>();
		for (int i = 0; i < ctx.vardec().size(); i++) {
			typeNode1 = visit(ctx.vardec(i).type());
			typeNode.add(typeNode1);
		}
		ArrayList<String> text = new ArrayList<String>();
		for (int i = 0; i < ctx.vardec().size(); i++)
			text.add(ctx.vardec(i).ID().getText());
		// visit the exp

		// build the varNode
		return new ClassdecNode(text, typeNode);

	}

	@Override
	public Node visitVarasm(VarasmContext ctx) {

		// declare the result node
		VarNode result;

		String idType = ctx.vardec().type().getText();
		// visit the type
		Node typeNode = visit(ctx.vardec().type());
		Node expNode = visit(ctx.exp());
		NewExpNode n = null;
		IdNode n1 = null;
		DotCallNode n2 = null;
		CallNode n3 = null;
		ClassTypeNode t = null;

		if (expNode instanceof NewExpNode)
			n = (NewExpNode) visit(ctx.exp());
		else if (expNode instanceof IdNode)
			n1 = (IdNode) visit(ctx.exp());
		else if (expNode instanceof DotCallNode) {
			n2 = (DotCallNode) visit(ctx.exp());
		} else if (expNode instanceof CallNode) {
			n3 = (CallNode) visit(ctx.exp());
		}

		if ((n == null) && (n1 == null) && (n2 == null) && (n3 == null))
			return new VarNode(ctx.vardec().ID().getText(), typeNode, expNode, idType, null);
		else if (n != null)
			return new VarNode(ctx.vardec().ID().getText(), typeNode, expNode, idType, n.getID());
		else if (n2 != null)
			return new VarNode(ctx.vardec().ID().getText(), typeNode, expNode, idType, null, n2);
		else if (n3 != null)
			return new VarNode(ctx.vardec().ID().getText(), typeNode, expNode, idType, null, n3);
		else
			return new VarNode(ctx.vardec().ID().getText(), typeNode, expNode, idType, null, n1);
	}

	@Override
	public Node visitFun(FunContext ctx) {
		// System.out.println("nome funzione: "+ctx.ID().getText());
		// initialize @res with the visits to the type and its ID

		FunNode res = new FunNode(ctx.ID().getText(), visit(ctx.type()));

		// add argument declarations
		// we are getting a shortcut here by constructing directly the ParNode
		// this could be done differently by visiting instead the VardecContext
		for (VardecContext vc : ctx.vardec()) {
			// System.out.println(vc.ID().getText());
			res.addPar(new ParNode(vc.ID().getText(), visit(vc.type())));

		}
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
		res.addDecBody(innerDec, exp);

		return res;

	}

	@Override
	public Node visitType(TypeContext ctx) {
		if (ctx.getText().equals("int"))
			return new IntTypeNode();
		else if (ctx.getText().equals("bool"))
			return new BoolTypeNode();
		else
			return new ClassTypeNode(ctx.getText());
		// this will never happen thanks to the parser
		// return null;

	}

	@Override
	public Node visitExp(ExpContext ctx) {

		// this could be enhanced
		// check whether this is a simple or binary expression
		// notice here the necessity of having named elements in the grammar
		// if(ctx.left){}
		if (ctx.right == null) {
			// it is a simple expression
			return visit(ctx.left);
		} else if (ctx.PLUS() != null) {
			// it is a binary expression, you should visit left and right
			return new PlusNode(visit(ctx.left), visit(ctx.right));
		} else {
			// it is a binary expression, you should visit left and right
			return new MinusNode(visit(ctx.left), visit(ctx.right));
		}

	}

	@Override
	public NewExpNode visitNewExp(NewExpContext ctx) {
		List<ExpContext> exp = null;
		String id = ctx.ID().getText();
		exp = ctx.exp();
		return new NewExpNode(id, exp);
	}

	@Override
	public Node visitTerm(TermContext ctx) {
		// check whether this is a simple or binary expression
		// notice here the necessity of having named elements in the grammar
		if (ctx.right == null) {
			// it is a simple expression
			return visit(ctx.left);
		} else if (ctx.TIMES() != null) {
			// it is a binary expression, you should visit left and right
			return new MultNode(visit(ctx.left), visit(ctx.right));
		} else {
			return new DivNode(visit(ctx.left), visit(ctx.right));
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
	public Node visitOperator(OperatorContext ctx) {

		// check whether this is a simple or binary expression
		// notice here the necessity of having named elements in the grammar
		if (ctx.right == null) {
			// it is a simple expression
			if (ctx.NOT() != null) {
				System.out.println("token " + ctx.NOT());
				return new NotNode(visit(ctx.left));
			} else {
				return visit(ctx.left);
			}
		} else {
			// it is a binary expression, you should visit left and right
			if (ctx.LT() != null) {
				System.out.println("token " + ctx.LT());
				return new LessThanNode(visit(ctx.left), visit(ctx.right));
			} else if (ctx.GT() != null) {
				System.out.println("token " + ctx.GT());
				return new GreaterThanNode(visit(ctx.left), visit(ctx.right));
			} else if (ctx.AND() != null) {
				System.out.println("token " + ctx.AND());
				return new AndNode(visit(ctx.left), visit(ctx.right));
			} else {
				System.out.println("token " + ctx.OR());
				return new OrNode(visit(ctx.left), visit(ctx.right));
			}

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
	public IdNode visitVarExp(VarExpContext ctx) {
		// this corresponds to a variable access
		return new IdNode(ctx.ID().getText());
	}

	@Override
	public Node visitMethodExp(MethodExpContext ctx) {
		// this corresponds to a variable access
		Node res = null;
		// get the invocation arguments
		ArrayList<Node> args = new ArrayList<Node>();
		for (ExpContext exp : ctx.exp())
			args.add(visit(exp));

		boolean thisExp = false;
		if (ctx.THIS() != null) {
			thisExp = true;
		}
		if (!thisExp)
			res = new CallNode(ctx.ID(1).getText(), args);

		if ((ctx.ID(1) != null) && (!thisExp)) {
			return new DotCallNode(ctx.ID(0).getText(), ctx.ID(1).getText(), args);
		} else if (thisExp) {
			return new DotCallNode("this", ctx.ID(0).getText(), args);
		} else {
			return res;
		}
		// cant arrive here(hope)

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

		else {
			// instantiate the invocation
			// System.out.println(ctx.getText());
			res = new CallNode(ctx.ID().getText(), args);
		}
		return res;
	}

}
