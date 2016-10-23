package arcatch.ast;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

public class ExceptionRemapScanner extends TreePathScanner<Object, Trees> {

	private String methodName;

	private String fromExeptionType;

	private String toExeptionType;

	private boolean isRemapped = false;

	public ExceptionRemapScanner(String fromExeptionType, String toExeptionType) {
		this.fromExeptionType = fromExeptionType;
		this.toExeptionType = toExeptionType;
	}

	public ExceptionRemapScanner(String methodName, String fromExeptionType, String toExeptionType) {
		this.methodName = methodName;
		this.fromExeptionType = fromExeptionType;
		this.toExeptionType = toExeptionType;
	}

	@Override
	public Object visitMethod(final MethodTree tree, Trees trees) {

		for (ExpressionTree expression : tree.getThrows()) {
			if (Tree.Kind.IDENTIFIER == expression.getKind()) {
				IdentifierTree identifierTree = (IdentifierTree) expression;
				boolean matchMathodName = this.methodName.equals(tree.getName().toString());

				if (matchMathodName && toExeptionType.equals(identifierTree.getName().toString())) {
					this.isRemapped = find(tree.getBody());
				}
			}
		}

		return super.visitMethod(tree, trees);
	}

	public boolean isRemapped() {
		return this.isRemapped;
	}

	private boolean find(StatementTree statementTree) {
		boolean found = false;
		if (Tree.Kind.BLOCK == statementTree.getKind()) {
			BlockTree blockTree = (BlockTree) statementTree;
			for (StatementTree statement : blockTree.getStatements()) {
				found = find(statement);
				if (found) {
					return true;
				}
			}
		} else if (Tree.Kind.TRY == statementTree.getKind()) {
			TryTree tryTree = (TryTree) statementTree;
			found = find(tryTree.getBlock());
			if (!found) {
				for (CatchTree catchTree : tryTree.getCatches()) {
					VariableTree variableTree = catchTree.getParameter();
					String exceptionType = variableTree.getType().toString();
					if (exceptionType.equals(this.fromExeptionType)) {
						found = find(catchTree.getBlock());
					}
					if (found) {
						break;
					}
				}
			} else if (!found) {
				found = find(tryTree.getFinallyBlock());
			}
		} else if (Tree.Kind.THROW == statementTree.getKind()) {
			ThrowTree throwTree = (ThrowTree) statementTree;
			ExpressionTree expressionTree = throwTree.getExpression();
			if (Tree.Kind.NEW_CLASS == expressionTree.getKind()) {
				NewClassTree newClassTree = (NewClassTree) expressionTree;
				if (this.toExeptionType.equals(newClassTree.getIdentifier().toString())) {
					return true;
				}
			}
		} else if (Tree.Kind.IF == statementTree.getKind()) {
			IfTree ifTree = (IfTree) statementTree;
			found = find(ifTree.getThenStatement());
			if (!found) {
				found = find(ifTree.getElseStatement());
			}
		} else if (Tree.Kind.SWITCH == statementTree.getKind()) {
			SwitchTree switchTree = (SwitchTree) statementTree;
			for (CaseTree caseTree : switchTree.getCases()) {
				for (StatementTree statement : caseTree.getStatements()) {
					found = find(statement);
					if (found) {
						break;
					}
				}
				if (found) {
					break;
				}
			}
		} else if (Tree.Kind.FOR_LOOP == statementTree.getKind()) {
			ForLoopTree forLoopTree = (ForLoopTree) statementTree;
			found = find(forLoopTree.getStatement());
		} else if (Tree.Kind.WHILE_LOOP == statementTree.getKind()) {
			DoWhileLoopTree doWhileLoopTree = (DoWhileLoopTree) statementTree;
			found = find(doWhileLoopTree.getStatement());
		}

		return found;

	}
}
