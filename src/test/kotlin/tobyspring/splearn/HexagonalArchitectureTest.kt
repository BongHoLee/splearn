package tobyspring.splearn

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures
import io.kotest.core.spec.style.FunSpec

class HexagonalArchitectureTest : FunSpec({

    val importedClasses = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())  // 테스트 코드 제외
        .importPackages("tobyspring.splearn")

    test("헥사고날 아키텍처 테스트") {
        Architectures.layeredArchitecture()
            .consideringAllDependencies()
            .layer("domain").definedBy("tobyspring.splearn.domain..")
            .layer("application").definedBy("tobyspring.splearn.application..")
            .layer("adapter").definedBy("tobyspring.splearn.adapter..")
            .whereLayer("domain").mayOnlyBeAccessedByLayers("application", "adapter")
            .whereLayer("application").mayOnlyBeAccessedByLayers("adapter")
            .whereLayer("adapter").mayNotBeAccessedByAnyLayer()
            .check(importedClasses)
    }
})
