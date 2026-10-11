package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TimeDepositCalculatorTest {

  private static final double BALANCE = 1000.00;

  private final TimeDepositCalculator calculator = new TimeDepositCalculator();

  @ParameterizedTest(name = "{0} plan, {1} days -> {2}")
  @CsvSource({
    "basic,   30, 1000.00",
    "student, 30, 1000.00",
    "premium, 30, 1000.00",
    "basic,    0, 1000.00",
  })
  void shouldNotApplyInterestDuringFirst30Days(String planType, int days, double expected) {
    assertThat(updatedBalance(planType, days, BALANCE)).isEqualTo(expected);
  }

  @ParameterizedTest(name = "{0} days -> {1}")
  @CsvSource({"31, 1000.83", "366, 1000.83"})
  void shouldApplyOnePercentYearlyInterestMonthlyToBasicPlanAfter30Days(int days, double expected) {
    assertThat(updatedBalance("basic", days, BALANCE)).isEqualTo(expected);
  }

  @ParameterizedTest(name = "{0} days -> {1}")
  @CsvSource({"31, 1002.50", "365, 1002.50"})
  void shouldApplyThreePercentYearlyInterestMonthlyToStudentPlanFrom31To365Days(
      int days, double expected) {
    assertThat(updatedBalance("student", days, BALANCE)).isEqualTo(expected);
  }

  @ParameterizedTest(name = "{0} days -> {1}")
  @CsvSource({"366, 1000.00", "1000, 1000.00"})
  void shouldNotApplyInterestToStudentPlanAfterOneYear(int days, double expected) {
    assertThat(updatedBalance("student", days, BALANCE)).isEqualTo(expected);
  }

  @ParameterizedTest(name = "{0} days -> {1}")
  @CsvSource({"31, 1000.00", "45, 1000.00"})
  void shouldNotApplyInterestToPremiumPlanUpTo45Days(int days, double expected) {
    assertThat(updatedBalance("premium", days, BALANCE)).isEqualTo(expected);
  }

  @ParameterizedTest(name = "{0} days -> {1}")
  @CsvSource({"46, 1004.17", "400, 1004.17"})
  void shouldApplyFivePercentYearlyInterestMonthlyToPremiumPlanAfter45Days(
      int days, double expected) {
    assertThat(updatedBalance("premium", days, BALANCE)).isEqualTo(expected);
  }

  @ParameterizedTest(name = "\"{0}\" -> unchanged")
  @CsvSource({"unknown", "Basic"})
  void shouldNotApplyInterestToUnknownOrDifferentlyCasedPlanTypes(String planType) {
    assertThat(updatedBalance(planType, 100, BALANCE)).isEqualTo(BALANCE);
  }

  @Test
  void shouldRoundInterestHalfUpToTwoDecimals() {
    assertThat(updatedBalance("basic", 31, 150.00)).isEqualTo(150.13);
  }

  @Test
  void shouldAddRoundedInterestToUnroundedBalanceUsingDoubleArithmetic() {
    assertThat(updatedBalance("premium", 46, 123.45)).isEqualTo(123.96000000000001);
  }

  @Test
  void shouldUpdateEveryDepositInTheList() {
    var basic = new TimeDeposit(1, "basic", BALANCE, 31);
    var student = new TimeDeposit(2, "student", BALANCE, 31);
    var premium = new TimeDeposit(3, "premium", BALANCE, 46);
    var tooNew = new TimeDeposit(4, "premium", BALANCE, 10);

    calculator.updateBalance(List.of(basic, student, premium, tooNew));

    assertThat(basic.getBalance()).isEqualTo(1000.83);
    assertThat(student.getBalance()).isEqualTo(1002.50);
    assertThat(premium.getBalance()).isEqualTo(1004.17);
    assertThat(tooNew.getBalance()).isEqualTo(BALANCE);
  }

  @Test
  void shouldLeaveOtherFieldsUnchanged() {
    var deposit = new TimeDeposit(7, "premium", BALANCE, 46);

    calculator.updateBalance(List.of(deposit));

    assertThat(deposit.getId()).isEqualTo(7);
    assertThat(deposit.getPlanType()).isEqualTo("premium");
    assertThat(deposit.getDays()).isEqualTo(46);
  }

  @Test
  void shouldKeepApplyingPremiumInterestEveryMonthOnceItHasStarted() {
    var deposit = new TimeDeposit(1, "premium", BALANCE, 46);

    calculator.updateBalance(List.of(deposit));
    assertThat(deposit.getBalance()).isEqualTo(1004.17);
    calculator.updateBalance(List.of(deposit));
    assertThat(deposit.getBalance()).isEqualTo(1008.3499999999999);
    calculator.updateBalance(List.of(deposit));
    assertThat(deposit.getBalance()).isEqualTo(1012.55);
  }

  @Test
  void shouldKeepApplyingStudentInterestEveryMonthWhileWithinTheFirstYear() {
    var deposit = new TimeDeposit(1, "student", BALANCE, 365);

    calculator.updateBalance(List.of(deposit));
    assertThat(deposit.getBalance()).isEqualTo(1002.5);
    calculator.updateBalance(List.of(deposit));
    assertThat(deposit.getBalance()).isEqualTo(1005.01);
    calculator.updateBalance(List.of(deposit));
    assertThat(deposit.getBalance()).isEqualTo(1007.52);
  }

  private double updatedBalance(String planType, int days, double balance) {
    var deposit = new TimeDeposit(1, planType, balance, days);
    calculator.updateBalance(List.of(deposit));
    return deposit.getBalance();
  }
}
