package org.ikigaidigital.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {

  private static final String ROOT = "org.ikigaidigital";
  private static final String SHARED_DOMAIN = ROOT;
  private static final String DOMAIN = ROOT + ".domain..";
  private static final String APPLICATION = ROOT + ".application..";
  private static final String WEB_ADAPTER = ROOT + ".adapter.in.web..";
  private static final String PERSISTENCE_ADAPTER = ROOT + ".adapter.out.persistence..";
  private static final String CONFIG = ROOT + ".config..";
  private static final String BOOTSTRAP = ROOT + ".bootstrap..";

  private static final JavaClasses CLASSES =
      new ClassFileImporter()
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages(ROOT);

  @Test
  void shouldFollowPortsAndAdaptersLayering() {
    onionArchitecture()
        .domainModels(SHARED_DOMAIN, DOMAIN)
        .applicationServices(APPLICATION)
        .adapter("web", WEB_ADAPTER)
        .adapter("persistence", PERSISTENCE_ADAPTER)
        .withOptionalLayers(true)
        .check(CLASSES);
  }

  @Test
  void domainShouldNotDependOnFrameworks() {
    noClasses()
        .that()
        .resideInAnyPackage(SHARED_DOMAIN, DOMAIN)
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            "org.springframework..",
            "jakarta..",
            "org.hibernate..",
            "com.fasterxml.jackson..",
            "tools.jackson..",
            "lombok..")
        .check(CLASSES);
  }

  @Test
  void onlyBootstrapShouldDependOnConfiguration() {
    noClasses()
        .that()
        .resideOutsideOfPackages(CONFIG, BOOTSTRAP)
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(CONFIG, BOOTSTRAP)
        .check(CLASSES);
  }
}
