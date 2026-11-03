package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ExcelUtils;

@Test(dataProvider = "excelTestCases",
        dataProviderClass = ExcelUtils.class)
public class ExcelDataTest {

    public void verifyExcelTestCaseData(
            String testCaseId,
            String module,
            String testType,
            String scenario,
            String testSteps,
            String expectedResult,
            String priority,
            String status) {
        Assert.assertFalse(
                testCaseId.isBlank(),
                "Test Case ID must not be empty"
        );

        Assert.assertFalse(
                module.isBlank(),
                "Module must not be empty"
        );
        Assert.assertFalse(
                testType.isBlank(),
                "Test type must not be empty"
        );
        Assert.assertFalse(
                scenario.isBlank(),
                "Scenario must not be empty"
        );
        Assert.assertFalse(
                testSteps.isBlank(),
                "Test steps must not be empty"
        );
        Assert.assertFalse(
                expectedResult.isBlank(),
                "Expected result must not be empty"
        );
        Assert.assertFalse(priority.isBlank(), "Priority must not be empty");
        Assert.assertFalse(status.isBlank(), "Status must not be empty");
    }
}
