# ArCatch 

ArCatch is a tool for static-architecture conformance checking of exception handling design. ArCatch aims at enforcing exception handling design decisions in Java projects, providing: 
* A declarative domain-specific language (ArCatch.Rules) for expressing design constraints regarding exception handling; and 
* A design rule checker (ArCatch.Checker) to automatically verify the exception handling conformance. 

Furthermore, ArCatch provides support for several kinds of dependence relation concerning the exception handling design, such as raising, re-raising, remapping, signaling, handling, and flow.

ArCatch in 5 steps
------------------

1. Configuration

    ```java
    ArCatch.config("source code path", "binary path");
    ```
2. Module Declaration
    ```java
    ModuleElement m = ArCatch.element().module("M").matching("regex").build();

    ModuleElement n = ArCatch.element().module("N").matching("regex").build();

    ModuleElement p = ArCatch.element().module("P").matching("regex").build();
    ```
3. Exception Declaration
    ```java
    ExceptionElement e = ArCatch.element().exception("E").matching("regex").build();

    ExceptionElement f = ArCatch.element().exception("F").matching("regex").build();
    ```
4. Design Rule Especification
    ```java
    DesignRule r1 = ArCatch.rule().module(m).cannotSignal(e).build();

    DesignRule r2 = ArCatch.rule().only(m).canRaise(e).build();

    DesignRule r3 = ArCatch.rule().module(m).mustHandle(e).build();

    DesignRule r4 = ArCatch.rule().module(n).canOnlySignal(e).build();

    DesignRule r5 = ArCatch.rule().exception(e).cannotFlow(m, n, p).build();

    DesignRule r6 = ArCatch.rule().only(n).canRemap(e).to(f).build();

    DesignRule r7 = ArCatch.rule().module(p).mustReraise(e).build();
    ```
5. Checking Rules
    ```java
    ArCatch.checker().addRule(r1);
    ...
    ArCatch.checker().addRule(r7);

    ArCatch.checker().checkAll();
    ```
    OR
    ```java
    boolean resultR1 = ArCatch.checker().check(r1);
    ...
    boolean resultR7 = ArCatch.checker().check(r7);
    ```

Conformance Report
------------------
ArCatch.Checker provides a report containing useful information on which design rules have been violated and where such violations take place in the software source code. If ```ArCatch.checker().checkAll()``` is used a text-based conformance chacking report is generated at ```./report``` folder. However, if ```ArCatch.checker().check(rule)``` is employed, you can access information abou rule violation performing ```rule.getReport()```. In the following we show an example of ArCatch.Checker conformance checking report:

```
========================================================================================================
ArCatch.Checker Exception Handling Conformance Checking Report
--------------------------------------------------------------------------------------------------------
Label: (V) = Rule Pass | (X) = Rule Fail
========================================================================================================
...
--------------------------------------------------------------------------------------------------------
(X) R8: only (BuL) can raise (BuLEx) 8 ms

 -BuL module implementation classes:
  -healthwatcher.business.complaint.ComplaintRecord
  -healthwatcher.business.healthguide.HealthUnitRecord
  -healthwatcher.business.healthguide.MedicalSpecialityRecord
  -healthwatcher.business.factories.FacadeFactory
  -healthwatcher.business.factories.RMIFacadeFactory
  -healthwatcher.business.complaint.DiseaseRecord
  -healthwatcher.business.complaint.SymptomRecord
  -healthwatcher.business.employee.EmployeeRecord
  -healthwatcher.business.factories.AbstractFacadeFactory

 -BuLEx exception implementation classes:
  -lib.exceptions.ObjectAlreadyInsertedException

 -Rule Violations
	-The method [healthwatcher.data.rdb.AddressRepositoryRDB.insert(healthwatcher.model.address.Address)] is raising the exception [lib.exceptions.ObjectAlreadyInsertedException]
--------------------------------------------------------------------------------------------------------
...
```

External Dependencies
---------------------
In ArCatch.Checker, all source code information relevant for the checking process is extracted using:
* [Design Wizard](https://github.com/joaoarthurbm/designwizard)
* [Java Compiler Tree API](https://docs.oracle.com/javase/7/docs/jdk/api/javac/tree/)


