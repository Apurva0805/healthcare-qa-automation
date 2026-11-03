package utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    private static final String FILE_PATH =
            "test-cases/Healthcare_QA_Test_Cases.xlsx";
    private static final String SHEET_NAME = "Sheet1";
    private static final int COLUMN_COUNT = 8;
    private static final int FIRST_TEST_ROW = 1;

    public static Object[][] readExcelData() {
        Path file = Paths.get(FILE_PATH);

        if (!Files.exists(file)) {
            throw new IllegalStateException(
                    "Excel file was not found: " + file.toAbsolutePath()
            );
        }

        List<Object[]> testCases = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (InputStream input = Files.newInputStream(file);
             Workbook workbook = WorkbookFactory.create(input)) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) {
                throw new IllegalStateException(
                        "Excel sheet was not found: " + SHEET_NAME
                );
            }

            for (int rowNumber = FIRST_TEST_ROW;
                 rowNumber <= sheet.getLastRowNum();
                 rowNumber++) {

                Row row = sheet.getRow(rowNumber);
                if (row == null) {
                    continue;
                }

                String[] values = readRow(row, formatter);
                if (!isEmptyRow(values)) {
                    testCases.add(values);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not read the Excel test cases",
                    e
            );
        }

        if (testCases.isEmpty()) {
            throw new IllegalStateException(
                    "The Excel sheet does not contain any test cases"
            );
        }

        return testCases.toArray(new Object[0][]);
    }

    private static String[] readRow(Row row, DataFormatter formatter) {
        String[] values = new String[COLUMN_COUNT];

        for (int column = 0; column < COLUMN_COUNT; column++) {
            values[column] = formatter.formatCellValue(
                    row.getCell(column)
            ).trim();
        }

        return values;
    }

    private static boolean isEmptyRow(String[] values) {
        for (String value : values) {
            if (!value.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @DataProvider(name = "excelTestCases")
    public Object[][] getExcelTestCases() {
        return readExcelData();
    }

    public static Object[] getTestCaseById(String testCaseId) {
        for (Object[] testCase : readExcelData()) {
            String currentId = String.valueOf(testCase[0]).trim();

            if (testCaseId.equalsIgnoreCase(currentId)) {
                return testCase;
            }
        }

        throw new IllegalArgumentException(
                "Test case was not found in Excel: " + testCaseId
        );
    }
}
