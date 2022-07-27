package com.rating.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.LogLog;

public class RatingAndReviewFileAppender extends RollingFileAppender {
	private static Properties appConfig = null;

	public RatingAndReviewFileAppender() {
		super();


		// older way(read file): if still null
		if (appConfig == null) {
			appConfig = new Properties();
			InputStream inputStream = null;
			try {

				inputStream = new FileInputStream(System.getenv("APP_PROPERTIES"));
				// inputStream = new FileInputStream(System.getenv("APP_PROPERTIES_OPTIMIZE"));
				if (inputStream != null) {
					appConfig = new Properties();
					appConfig.load(inputStream);
				}

			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void rollOver() {
		this.rollFile();
		super.rollOver();
	}

	@Override
	public void activateOptions() {
		super.fileAppend = false;

		if (appConfig.getProperty("log." + super.getName() + ".file") != null) {
			super.setFile(appConfig.getProperty("log." + super.getName() + ".file"));
		} else {
			System.out.println("Configuration not found for " + "log." + super.getName() + ".file");
			System.exit(0);
		}

		LogManager.getRootLogger().setLevel(Level.toLevel(appConfig.getProperty("log." + super.getName() + ".level", "DEBUG")));
		super.setMaximumFileSize(Long.parseLong(appConfig.getProperty("log." + super.getName() + ".file.size", "5120000").toString()));
		super.setMaxBackupIndex(Integer.parseInt(appConfig.getProperty("log." + super.getName() + ".MaxBackupIndex", "30").toString()));
		this.rollFile();
		super.activateOptions();
	}

	private void rollFile() {
		File target;
		File file;
		boolean renameSucceeded = true;
		String extension = fileName.substring(fileName.indexOf("."));
		String myFileName = fileName.substring(0, fileName.indexOf("."));

		// Rename fileName
		target = new File(myFileName + "." + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + extension);
		this.closeFile();
		file = new File(fileName);

		if (file.exists()) {
			renameSucceeded = file.renameTo(target);

			//
			// if file rename failed, reopen file with append = true
			//
			if (!renameSucceeded) {
				try {
					this.setFile(fileName, true, bufferedIO, bufferSize);
				} catch (IOException e) {
					if (e instanceof InterruptedIOException) {
						Thread.currentThread().interrupt();
					}
					LogLog.error("setFile(" + fileName + ", true) call failed.", e);
				}
			}
		}
		// delete older log file
		deleteOlderFiles(file);

	}

	/**
	 * deletes older files from the configured log folder, based of property set
	 * MaxBackupIndex
	 * 
	 * @param current log file (ex. RatingAndReview.log or etc.)
	 */
	private void deleteOlderFiles(File file) {
		try {
			// get folder of the log file
			File folder = file.getParentFile();

//		prepare regex to filter files of this folder
//		regex should be like : "RatingAndReview\\..*\\.log", explain: starting with RatingAndReview, then 1 dot, then any chars with zero or multiple length then .log 
			String logfile = file.getName().substring(0, file.getName().indexOf("."));
			String ext = file.getName().substring(file.getName().indexOf("."));

			String fileRegx = (logfile + "\\..*\\" + ext);

			File[] files = folder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(final File dir, final String name) {
					return name.matches(fileRegx);
				}
			});
			// dictionary listing order could be random, sort by last modified
			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

			// difference -1 because there will be one current file.
			int filesToDelete = (files.length - getMaxBackupIndex());

			System.out.println("deleting older log file copies of " + file.getName());
			System.out.println("total backups found " + files.length + " ,keeping " + getMaxBackupIndex() + " files, "
					+ (filesToDelete <= 0 ? "nothing" : filesToDelete) + " to delete");

			// delete first n files from array and keep {MaxBackupSize} files only
			for (int i = 0; i < filesToDelete; i++) {
				System.out.println("deleting: " + files[i].getName());
				files[i].delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}