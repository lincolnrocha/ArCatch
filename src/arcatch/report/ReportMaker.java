package arcatch.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import arcatch.dsl.rule.DesignRule;

public class ReportMaker {

	private final static String REPORT_PATH = "./report";

	private static String REPORT_NAME = "ArCatchEHCCReport";

	private static boolean isNewReport = true;

	private static String printHeader() {
		StringBuffer report = new StringBuffer();
		report.append("========================================================================================================\n");
		report.append("ArCatch.Checker Exception Handling Conformance Checking Report\n");
		report.append("--------------------------------------------------------------------------------------------------------\n");
		report.append("Label: (V) = Rule Pass | (X) = Rule Fail\n");
		report.append("========================================================================================================\n\n");
		return report.toString();
	}

	private static void reportAppend(String text) {
		File directory = new File(REPORT_PATH);
		if (!directory.exists()) {
			directory.mkdir();
		}

		if (isNewReport) {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			dateFormat.setTimeZone(calendar.getTimeZone());
			StringBuffer reportName = new StringBuffer(REPORT_NAME);
			reportName.append(dateFormat.format(calendar.getTime()));
			reportName.append(".txt");
			REPORT_NAME = reportName.toString();
			isNewReport = false;
		}

		File reportFile = new File(directory, REPORT_NAME);
		try {
			FileWriter fileWriter = new FileWriter(reportFile, true);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.append(text);
			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void report(DesignRule rule) {
		StringBuffer text = new StringBuffer();
		if (isNewReport) {
			text.append(printHeader());
		}
		text.append("--------------------------------------------------------------------------------------------------------\n");
		text.append(rule.getReport());
		text.append("--------------------------------------------------------------------------------------------------------\n\n");
		reportAppend(text.toString());
	}

}
