package tobyspring.learningtest.archunit

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import io.kotest.core.spec.style.FunSpec

//@AnalyzeClasses(packages = ["tobyspring.learningtest.archunit"])
class ArchUnitLearningTest : FunSpec ({



    val importedClasses = ClassFileImporter().importPackages("tobyspring.learningtest.archunit")

    test("application에 의존하는 클래스는 application, adapter 패키지에만 존재해야 한다.") {
        classes().that().resideInAPackage("..application..")
            .should().onlyHaveDependentClassesThat().resideInAnyPackage("..application..", "..adapter..")
            .check(importedClasses)
    }

    test("application 클래스는 adapter 패키지에 의존하면 안된다.") {
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..adapter..")
            .check(importedClasses)
    }
})