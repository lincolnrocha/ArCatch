package arcatch.dsl.rule.cannot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CannotFlow extends DesignRule {

	public CannotFlow() {
	}

	public CannotFlow(ExceptionElement exception, ModuleElement... modules) {
		super(exception, modules);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getException().getLabel());
		template.append(") cannot flow (");
		Iterator<ModuleElement> iterator = getModuleList().iterator();
		while (iterator.hasNext()) {
			ModuleElement module = iterator.next();
			template.append(module.getLabel());
			if (iterator.hasNext())
				template.append(", ");
		}
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>E cannot flow M1,...,Mn</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean cannotFlow = true;

		List<Set<ClassNode>> listOfAllClassesOfModuleList = new ArrayList<Set<ClassNode>>();

		for (ModuleElement module : this.getModuleList()) {
			listOfAllClassesOfModuleList.add(CheckerUtil.toClassNodes(module.getImplElements()));
		}

		final Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;
		List<Set<MethodNode>> chain = CheckerUtil.callChain(listOfAllClassesOfModuleList);

		for (ClassNode exceptionClass : exceptionClasses) {
			if (CheckerUtil.flows(chain, exceptionClass)) {
				violation = new Violation();
				violation.setExceptionName(exceptionClass.getClassName());
				this.addViolation(violation);
				cannotFlow = false;
			}
		}
		return cannotFlow;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Exception [");
			text.append(violation.getExceptionName());
			text.append("] is flowing through [");
			Iterator<ModuleElement> iterator = this.getModuleList().iterator();
			while (iterator.hasNext()) {
				ModuleElement module = iterator.next();
				text.append(module.getLabel());
				if (iterator.hasNext())
					text.append(", ");
			}
			text.append("]\n");
		}
		return text.toString();
	}
}
