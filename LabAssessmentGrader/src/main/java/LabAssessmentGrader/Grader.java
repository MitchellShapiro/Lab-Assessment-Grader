package LabAssessmentGrader;

// author - Mitchell Shapiro - March 2020

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import net.lingala.zip4j.ZipFile;

public class Grader {

	private static Scanner sc;

	public Grader(String topDirectory, String harnessDirectory, String mainClass) {

		boolean noHarness = harnessDirectory.length() <= 0;

		File dir = new File(topDirectory);
		File harness = null;
		if (!noHarness) {
			harness = new File(harnessDirectory);
		}
		String baseResultDir = "temp_grader_files";
		File resultDir = new File(baseResultDir);
		// Get all folders in top directory
		File[] directoryListing = dir.listFiles();

		// Avoid interfering with existing files
		int num = 1;
		while (resultDir.exists()) {
			resultDir = new File(baseResultDir + num);
			num++;
		}
		System.out.println("Temporary folder location: " + resultDir.getAbsolutePath());

		try {
			int index = 0;
			if (directoryListing != null) {
				for (File child : directoryListing) {
					System.out.println("\nOpening: " + child.getName());
					try {

						boolean wasZip = false;
						if (!child.isDirectory() && getExtension(child.getAbsolutePath()).equals("zip")) {
							wasZip = true;
							new ZipFile(child).extractAll(resultDir.getAbsolutePath());
						} else {
							if (!child.isDirectory()) {
								System.out.println("A user file is not a .zip nor directory, skipping...");
								continue;
							}
						}

						if (!wasZip) {
							// Copy the user files into the temp directory
							FileUtils.copyDirectory(child, resultDir);
						}

						if (!noHarness && harness != null) {
							// Copy the harness files into the temp directory
							File[] harnessFiles = harness.listFiles();

							if (harnessFiles == null) {
								System.out.println("Error reading harness files, check path");
								return;
							}

							for (File harnessFile : harnessFiles) {
								FileUtils.copyFileToDirectory(harnessFile, resultDir);
							}
						}

						runJava(resultDir, mainClass);

						// Don't ask to continue if no more items
						if (index < directoryListing.length - 1) {
							// This was originally optional but this made it too easy to miss programs that
							// crashed
							System.out.println("\nEnter any key to continue to next person...");
							sc.nextLine();
						}

						// Delete the temp directory
						FileUtils.deleteDirectory(resultDir);
						index++;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Reached end of directory");
			} else {
				System.out.println("Error reading user files, check path");
				System.exit(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Delete the temp directory even when program crashes
			try {
				FileUtils.deleteDirectory(resultDir);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		sc.close();
	}

	public static String getExtension(String file) {
		String[] split = file.split("\\.");
		if (split.length <= 0)
			return "";
		return split[split.length - 1];
	}

	// https://stackoverflow.com/questions/15464111/run-cmd-commands-through-java
	private void runJava(File directory, String mainClass) throws IOException {
		System.out.println("Directory: " + directory);
		System.out.println("Compiling and running java code...");
		System.out.println("\nBegin Console Output:\n");
		// Locks thread until java program completes
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "javac * && java " + mainClass);
		builder.directory(directory);
		builder.redirectErrorStream(true);
		Process p = builder.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while (true) {
			line = r.readLine();
			if (line == null) {
				break;
			}
			System.out.println(line);
		}
		System.out.println("\nEnd Console Output\n");
	}

	public static boolean checkArrayForString(String[] array, String checking) {
		for (String s : array) {
			if (s.equals(checking))
				return true;
		}
		return false;
	}

	public static void main(String[] args) {

		sc = new Scanner(System.in);

		System.out.println(
				"Input Format: [\"Path to user files\"] [\"Path to harness files\"] [\"Name of main class (no file extension)\"]"
						+ "Paths can be relative to jar location or absolute."
						+ "These options can be provided as Command Line Arguments or during runtime\n");

		String userDir;
		String harnessDir;
		String mainClass;

		if (args.length < 3) {
			System.out.println("No Command Line Arguments were provided, asking at runtime...");

			System.out.println("Enter path to user files: ");
			userDir = sc.nextLine();
			System.out.println("Enter path to harness files, enter nothing if no harness files: ");
			harnessDir = sc.nextLine();
			System.out.println("Enter name of main class (no file extension): ");
			mainClass = sc.nextLine();
		} else {
			userDir = args[0];
			harnessDir = args[1];
			mainClass = args[2];
		}

		System.out.println("Path to user files: " + userDir);
		System.out.println("Path to harness files: " + harnessDir);
		System.out.println("Name of main class: " + mainClass);

		new Grader(userDir, harnessDir, mainClass);
	}

}
