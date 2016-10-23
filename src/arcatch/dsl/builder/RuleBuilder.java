package arcatch.dsl.builder;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.grammar.rule.Begin;
import arcatch.dsl.grammar.rule.Common;
import arcatch.dsl.grammar.rule.CommonFlow;
import arcatch.dsl.grammar.rule.End;
import arcatch.dsl.grammar.rule.OnlyCan;
import arcatch.dsl.grammar.rule.OnlyCanFlow;
import arcatch.dsl.grammar.rule.To;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.canOnly.CanHandleOnly;
import arcatch.dsl.rule.canOnly.CanRaiseOnly;
import arcatch.dsl.rule.canOnly.CanRemapOnly;
import arcatch.dsl.rule.canOnly.CanReraiseOnly;
import arcatch.dsl.rule.canOnly.CanSignalOnly;
import arcatch.dsl.rule.cannot.CannotFlow;
import arcatch.dsl.rule.cannot.CannotHandle;
import arcatch.dsl.rule.cannot.CannotRaise;
import arcatch.dsl.rule.cannot.CannotRemap;
import arcatch.dsl.rule.cannot.CannotReraise;
import arcatch.dsl.rule.cannot.CannotSignal;
import arcatch.dsl.rule.must.MustFlow;
import arcatch.dsl.rule.must.MustHandle;
import arcatch.dsl.rule.must.MustRaise;
import arcatch.dsl.rule.must.MustRemap;
import arcatch.dsl.rule.must.MustReraise;
import arcatch.dsl.rule.must.MustSignal;
import arcatch.dsl.rule.onlyCan.OnlyCanHandle;
import arcatch.dsl.rule.onlyCan.OnlyCanRaise;
import arcatch.dsl.rule.onlyCan.OnlyCanRemap;
import arcatch.dsl.rule.onlyCan.OnlyCanReraise;
import arcatch.dsl.rule.onlyCan.OnlyCanSignal;

public class RuleBuilder implements Begin, OnlyCan, OnlyCanFlow, Common, CommonFlow, To, End {

	private static RuleBuilder INSTANCE;

	private String ruleLabel = "";

	private ModuleElement module;

	private ExceptionElement exception;

	private DesignRule designRule;

	public static RuleBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RuleBuilder();
		}
		return INSTANCE;
	}

	@Override
	public DesignRule build() {
		return this.designRule;
	}

	@Override
	public End canRaiseOnly(ExceptionElement exception) {
		this.designRule = new CanRaiseOnly(this.module, exception);
		return this;
	}

	@Override
	public End canSignalOnly(ExceptionElement exception) {
		this.designRule = new CanSignalOnly(this.module, exception);
		return this;
	}

	@Override
	public End canHandleOnly(ExceptionElement exception) {
		this.designRule = new CanHandleOnly(this.module, exception);
		return this;
	}

	@Override
	public End canReraiseOnly(ExceptionElement exception) {
		this.designRule = new CanReraiseOnly(module, exception);
		return this;
	}

	@Override
	public To canRemapOnly(ExceptionElement exception) {
		this.designRule = new CanRemapOnly(module, exception, null);
		return this;
	}

	@Override
	public End canFlowOnly(ModuleElement... modules) {
		this.designRule.setModuleList(modules);
		return this;
	}

	@Override
	public End cannotRaise(ExceptionElement exception) {
		this.designRule = new CannotRaise(this.module, exception);
		return this;
	}

	@Override
	public End cannotSignal(ExceptionElement exception) {
		this.designRule = new CannotSignal(this.module, exception);
		return this;
	}

	@Override
	public End cannotReraise(ExceptionElement exception) {
		this.designRule = new CannotReraise(this.module, exception);
		return this;
	}

	@Override
	public To cannotRemap(ExceptionElement exception) {
		this.designRule = new CannotRemap(this.module, exception, null);
		return this;
	}

	@Override
	public End cannotHandle(ExceptionElement exception) {
		this.designRule = new CannotHandle(this.module, exception);
		return this;
	}

	@Override
	public End cannotFlow(ModuleElement... modules) {
		this.designRule = new CannotFlow(this.exception, modules);
		return this;
	}

	@Override
	public End canRaise(ExceptionElement exception) {
		this.designRule = new OnlyCanRaise(this.module, exception);
		return this;
	}

	@Override
	public End canSignal(ExceptionElement exception) {
		this.designRule = new OnlyCanSignal(this.module, exception);
		return this;
	}

	@Override
	public End canHandle(ExceptionElement exception) {
		this.designRule = new OnlyCanHandle(this.module, exception);
		return this;
	}

	@Override
	public End canReraise(ExceptionElement exception) {
		this.designRule = new OnlyCanReraise(module, exception);
		return this;
	}

	@Override
	public To canRemap(ExceptionElement exception) {
		this.designRule = new OnlyCanRemap(this.module, exception, null);
		return this;
	}

	@Override
	public End canFlow(ModuleElement... modules) {
		this.designRule = new CannotFlow(this.exception, modules);
		return null;
	}

	@Override
	public End mustRaise(ExceptionElement exception) {
		this.designRule = new MustRaise(this.ruleLabel, this.module, exception);
		return this;
	}

	@Override
	public End mustSignal(ExceptionElement exception) {
		this.designRule = new MustSignal(this.module, exception);
		return this;
	}

	@Override
	public End mustHandle(ExceptionElement exception) {
		this.designRule = new MustHandle(this.module, exception);
		return this;
	}

	@Override
	public End mustReraise(ExceptionElement exception) {
		this.designRule = new MustReraise(this.module, exception);
		return this;
	}

	@Override
	public To mustRemap(ExceptionElement exception) {
		this.designRule = new MustRemap(this.module, exception, null);
		return this;
	}

	@Override
	public End mustFlow(ModuleElement... modules) {
		this.designRule = new MustFlow(this.exception, modules);
		return null;
	}

	@Override
	public OnlyCan only(ModuleElement module) {
		this.module = module;
		return this;
	}

	@Override
	public Common module(ModuleElement module) {
		this.module = module;
		return this;
	}

	@Override
	public End to(ExceptionElement exception) {
		this.designRule.setToException(exception);
		return this;
	}

	@Override
	public OnlyCanFlow only(ExceptionElement exception) {
		this.exception = exception;
		return this;
	}

	@Override
	public CommonFlow exception(ExceptionElement exception) {
		this.exception = exception;
		return this;
	}
}
