package sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class BaseSheet {
	public static void main(String[] args) {
		new BaseSheet().loadFile(args[0]);
	}

	public void loadFile(String path) {
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(new File(path));
			workbook.createFont().setFontName("MS 明朝");
			FormulaEvaluator evaluator = workbook.getCreationHelper()
					.createFormulaEvaluator();
			for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
				Sheet sheet = workbook.getSheetAt(i);
				PrintWriter pw = new PrintWriter(new BufferedWriter(
						new FileWriter(new File(sheet.getSheetName()
								+ ".csv"))));
				for (int indexRow = 0; indexRow < sheet.getLastRowNum(); ++indexRow) {
					Row row = sheet.getRow(indexRow);
					if (null == row) {
						continue;
					}
					for (int indexCol = 0; indexCol < row.getLastCellNum(); ++indexCol) {
						Cell cell = row.getCell(indexCol);
						if (null == cell) {
							continue;
						}
						if (Cell.CELL_TYPE_FORMULA == cell.getCellType()) {
							try {
								CellValue value = evaluator.evaluate(cell);
								cell.setCellValue(value.getStringValue());
							} catch (NotImplementedException e) {
								cell.setCellValue("Error evaluating cell "
										+ cell.getCellFormula());
							}
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}
						if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
							cell.setCellValue(new BigDecimal(cell
									.getNumericCellValue()).toPlainString());
							;
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}
						System.out.print(cell.getStringCellValue() + ",");
						pw.print(cell.getStringCellValue() + ",");
					}
					System.out.println("");
					pw.println("");
				}
				pw.close();
			}
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
