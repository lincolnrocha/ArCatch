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
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

public class ExceptionReraiseScanner extends TreePathScanner<Object, Trees> {

	private String methodName;

	private String exeptionType;

	private boolean isReraised = false;

	public ExceptionReraiseScanner(String exeptionType) {
		this.exeptionType = exeptionType;
	}

	public ExceptionReraiseScanner(String methodName, String exeptionType) {
		this.methodName = methodName;
		this.exeptionType = exeptionType;
	}

	@Override
	public Object visitMethod(final MethodTree tree, Trees trees) {

		for (ExpressionTree expression : tree.getThrows()) {
			if (Tree.Kind.IDENTIFIER == expression.getKind()) {
				IdentifierTree identifierTree = (IdentifierTree) expression;
				boolean matchMathodName = true;
				if (methodName != null) {
					matchMathodName = methodName.equals(tree.getName().toString());
				}

				if (matchMathodName && exeptionType.equals(identifierTree.getName().toString())) {
					this.isReraised = find(tree.getBody(), null);
				}
			}
		}

		return super.visitMethod(tree, trees);
	}

	public boolean isReraised() {
		return this.isReraised;
	}

	private boolean find(StatementTree statementTree, String exceptionVariable) {
		boolean found = false;
		if (Tree.Kind.BLOCK == statementTree.getKind()) {
			BlockTree blockTree = (BlockTree) statementTree;
			for (StatementTree statement : blockTree.getStatements()) {
				found = find(statement, exceptionVariable);
				if (found) {
					return true;
				}
			}
		} else if (Tree.Kind.TRY == statementTree.getKind()) {
			TryTree tryTree = (TryTree) statementTree;
			found = find(tryTree.getBlock(), exceptionVariable);
			if (!found) {
				for (CatchTree catchTree : tryTree.getCatches()) {
					VariableTree variableTree = catchTree.getParameter();
					String exceptionType = variableTree.getType().toString();
					String exceptionName = variableTree.getName().toString();
					if (exceptionType.equals(this.exeptionType)) {
						found = find(catchTree.getBlock(), exceptionName);
					}
					if (found) {
						break;
					}
				}
			} else if (!found) {
				found = find(tryTree.getFinallyBlock(), exceptionVariable);
			}
		} else if (Tree.Kind.THROW == statementTree.getKind()) {
			ThrowTree throwTree = (ThrowTree) statementTree;
			ExpressionTree expressionTree = throwTree.getExpression();
			if (Tree.Kind.IDENTIFIER == expressionTree.getKind()) {
				IdentifierTree identifierTree = (IdentifierTree) expressionTree;
				if (exceptionVariable.equals(identifierTree.getName())) {
					return true;
				}
			}
		} else if (Tree.Kind.IF == statementTree.getKind()) {
			IfTree ifTree = (IfTree) statementTree;
			found = find(ifTree.getThenStatement(), exceptionVariable);
			if (!found) {
				found = find(ifTree.getElseStatement(), exceptionVariable);
			}
		} else if (Tree.Kind.SWITCH == statementTree.getKind()) {
			SwitchTree switchTree = (SwitchTree) statementTree;
			for (CaseTree caseTree : switchTree.getCases()) {
				for (StatementTree statement : caseTree.getStatements()) {
					found = find(statement, exceptionVariable);
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
			found = find(forLoopTree.getStatement(), exceptionVariable);
		} else if (Tree.Kind.WHILE_LOOP == statementTree.getKind()) {
			DoWhileLoopTree doWhileLoopTree = (DoWhileLoopTree) statementTree;
			found = find(doWhileLoopTree.getStatement(), exceptionVariable);
		}

		return found;

	}
}
