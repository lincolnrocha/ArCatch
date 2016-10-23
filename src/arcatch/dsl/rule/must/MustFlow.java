package arcatch.dsl.rule.must;

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

public class MustFlow extends DesignRule {

	public MustFlow() {
	}

	public MustFlow(ExceptionElement exception, ModuleElement... modules) {
		super(exception, modules);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getException().getLabel());
		template.append(") must flow (");
		for (ModuleElement module : getModuleList()) {
			template.append(module.getLabel());
			template.append(", ");
		}
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>E must flow M1,...,Mn</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean mustFlow = false;

		List<Set<ClassNode>> listOfAllClassesOfModuleList = new ArrayList<Set<ClassNode>>();

		for (ModuleElement module : this.getModuleList()) {
			listOfAllClassesOfModuleList.add(CheckerUtil.toClassNodes(module.getImplElements()));
		}

		final Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		List<Set<MethodNode>> chain = CheckerUtil.callChain(listOfAllClassesOfModuleList);

		for (ClassNode exceptionClass : exceptionClasses) {
			if (CheckerUtil.flows(chain, exceptionClass)) {
				mustFlow = true;
				break;
			}
		}

		if (!mustFlow) {
			Violation violation = new Violation();
			this.addViolation(violation);
		}
		return mustFlow;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		if (this.hasViolation()) {
			text.append("\t-No exception in [");
			text.append(this.getException().getLabel());
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
