package arcatch.util;

public class TargetSystemInfo {

	private static String sourcePath;

	private static String binaryPath;

	private static TargetSystemInfo INSTANCE;

	private TargetSystemInfo(String sourcePath, String binaryPath) {
		super();
		TargetSystemInfo.sourcePath = sourcePath;
		TargetSystemInfo.binaryPath = binaryPath;
	}

	public static boolean isConfigured() {
		return INSTANCE != null;
	}

	public static void config(String sourcePath, String binaryPath) {
		if (INSTANCE == null) {
			INSTANCE = new TargetSystemInfo(sourcePath, binaryPath);
		}
	}

	public static TargetSystemInfo getInstance() {
		if (!isConfigured()) {
			throw new RuntimeException("You must perform TargetSystemInfo.config(String, String) call first.");
		}
		return INSTANCE;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public String getBinaryPath() {
		return binaryPath;
	}
}
