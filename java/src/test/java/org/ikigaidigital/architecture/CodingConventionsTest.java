package org.ikigaidigital.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class CodingConventionsTest {

  private static final JavaClasses CLASSES =
      new ClassFileImporter()
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages("org.ikigaidigital");

  @Test
  void localDateTimeShouldNeverBeCreatedFromTheJvmDefaultTimeZone() {
    noClasses()
        .should()
        .callMethod(LocalDateTime.class, "now")
        .because(
            "use LocalDateTime.now(ZoneOffset.UTC) so the value does not depend on the JVM zone")
        .check(CLASSES);
  }
}
