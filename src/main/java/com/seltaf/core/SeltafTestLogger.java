package com.seltaf.core;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.testng.Reporter;

import com.google.gdata.util.common.html.HtmlToText;
import com.seltaf.core.ScreenShot;

public class SeltafTestLogger {
	
	public static Logger getLogger(final Class<?> cls) {
        boolean rootIsConfigured = Logger.getRootLogger().getAllAppenders().hasMoreElements();
        if (!rootIsConfigured) {
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.INFO);

            Appender appender = (Appender) Logger.getRootLogger().getAllAppenders().nextElement();
            appender.setLayout(new PatternLayout(" %-5p %d [%t] %C{1}: %m%n"));
        }

        return Logger.getLogger(cls);
    }
	
	/**
     * error Logger.
     *
     * @param  message
     */
    public static void errorLogger(String message) {
        message = "<li><b><font color='#6600CC'>" + message + "</font></b></li>";
        log(message, false, false);
    }
    
    /**
     * Log info.
     *
     * @param  message
     */
    public static void logInfo(String message) {
        message = "<li><font color='#00cd00'>" + message + "</font></li>";
        log(message, false, false);
    }
    
    /**
     * Log method.
     *
     * @param  message
     */
    public static void warning(String message) {
        message = "<li><font color='#FFFF00'>" + message + "</font></li>";
        log(message, false, false);
    }
    public static void log(final String message) {
        log(message, false, false);
    }

    /**
     * Log.
     *
     * @param  message
     * @param  logToStandardOutput
     */
    public static void log(final String message, final boolean logToStandardOutput) {
        log(message, false, logToStandardOutput);
    }
    
    public static void log(String message, final boolean failed, final boolean logToStandardOutput) {

        if (message == null) {
            message = "";
        }

        message = message.replaceAll("\\n", "<br/>");

        if (failed) {
            message = "<span style=\"font-weight:bold;color:#cc0052;\">" + message + "</span>";
        }

        Reporter.log(message, logToStandardOutput);
    }
    
    public static String escape(final String message) {
        return message.replaceAll("\\n", "<br/>").replaceAll("<", "@@lt@@").replaceAll(">", "^^greaterThan^^");
    }

    public static String unEscape(String message) {
        message = message.replaceAll("<br/>", "\\n").replaceAll("@@lt@@", "<").replaceAll("\\^\\^gt\\^\\^", ">");

        message = HtmlToText.htmlToPlainText(message);
        return message;
    }
    public static void logWebOutput(final String url, final String message, final boolean failed) {
        log("Output: " + message + "<br/>", failed, false);
    }

    public static void logWebStep(final String url, final String message, final boolean failed) {
        log("<li>" + (failed ? "<b>FailedStep</b>: " : " ") + message + "</li>", failed, false);
    }

    public static String buildScreenshotLog(final ScreenShot screenShot) {
        StringBuffer sbMessage = new StringBuffer("");
        if (screenShot.getLocation() != null) {
            sbMessage.append("<a href='" + screenShot.getLocation() + "' target=url>Application URL</a> | ");
        }

        if (screenShot.getHtmlSourcePath() != null) {
            sbMessage.append(" <a href='" + screenShot.getHtmlSourcePath()
                    + "' target='_blank'>Application HTML Source</a> | ");
        }

        if (screenShot.getImagePath() != null) {
            sbMessage.append(" <a href='" + screenShot.getImagePath()
                    + "'data-featherlight='image'>Application Snapshot</a>");
        }

        return sbMessage.toString();
    }
}
