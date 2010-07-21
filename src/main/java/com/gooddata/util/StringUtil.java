package com.gooddata.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.ibm.icu.text.Transliterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * GoodData
 *
 * @author zd <zd@gooddata.com>
 * @version 1.0
 */
public class StringUtil {

    private static String[] DISCARD_CHARS = {"\"", " ", "!", "?", "%", "&", "#", "*", "+", "-", "=", "/", ",", ".", ">", "<",
            "$", "%", ",", "(", ")", "�", "�", "�","@", "{" ,"}",
            "[", "]","\\",":"};

    private static String[] INVALID_CSV_HEADER_CHARS = {"\"", "'", "!", "?", "%", "&", "#", "*", "+", "-", "=", "/", ",", ".", ">", "<",
            "$", "%", ",", "(", ")", "�", "�", "�","@", "{" ,"}",
            "[", "]","\\",":"};

    private static String[] WHITESPACE = {"\n","\t"};

    private static String[][] DATE_FORMAT_CONVERSION = {{"MM","%m"},{"yyyy","%Y"},{"yy","%y"},{"dd","%d"}};
    
    /**
     * Formats a string as identifier
     * Currently only converts to the lowercase and replace spaces
     * @param s the string to convert to identifier
     * @return converted string
     */
    public static String formatShortName(String s) {
        return convertToIdentifier(s);
    }

    /**
     * Formats a string as title
     * Currently does nothing TBD
     * @param s the string to convert to a title
     * @return converted string
     */
    public static String formatLongName(String s) {
        return s.trim();
    }

     private static String convertToIdentifier(String s) {
        Transliterator t = Transliterator.getInstance("Any-Latin; NFD; [:Nonspacing Mark:] Remove; NFC");
        s = t.transliterate(s);
        s = s.replaceAll("[^a-zA-Z0-9_]", "");
        s = s.replaceAll("^[0-9_]*", "");
        //s = s.replaceAll("[_]*$", "");
        //s = s.replaceAll("[_]+", "_");
        return s.toLowerCase().trim();
    }

    /**
     * Formats a CSV header
     * @param s the string to convert to identifier
     * @return converted string
     */
    public static String csvHeaderToIdentifier(String s) {
        return convertToIdentifier(s);
    }

    /**
     * Formats a CSV header
     * @param s the string to convert to identifier
     * @return converted string
     */
    public static String csvHeaderToTitle(String s) {
        Transliterator t = Transliterator.getInstance("Any-Latin; NFD; [:Nonspacing Mark:] Remove; NFC");
        s = t.transliterate(s);
        for ( String r : INVALID_CSV_HEADER_CHARS ) {
            s = s.replace(r,"");
        }
        return s.trim();
    }

    /**
     * Converts the Java date format string to the MySQL format
     * @param dateFormat Java date format
     * @return MySQL date format
     */
    public static String convertJavaDateFormatToMySql(String dateFormat) {
        for(int i=0; i < DATE_FORMAT_CONVERSION.length; i++)
            dateFormat = dateFormat.replace(DATE_FORMAT_CONVERSION[i][0],
                            DATE_FORMAT_CONVERSION[i][1]);
        return dateFormat;
    }
    
    /**
     * Converts a {@link Collection} to a <tt>separator<tt> separated string
     * 
     * @param separator
     * @param list
     * @return <tt>separator<tt> separated string version of the given list
     */
    public static String join(String separator, Collection<String> list) {
    	return join(separator, list, null);
    }

    /**
     * Converts a {@link Collection} to a <tt>separator<tt> separated string.
     * If the <tt>replacement</tt> parameter is not null, it is used to populate
     * the result string instead of list elements.
     * 
     * @param separator
     * @param list
     * @param replacement
     * @return <tt>separator<tt> separated string version of the given list
     */
    public static String join(String separator, Collection<String> list, String replacement) {
    	StringBuffer sb = new StringBuffer();
    	boolean first = true;
    	for (final String s : list) {
    		if (first)
    			first = false;
    		else
    			sb.append(separator);
			sb.append(replacement == null ? s : replacement);
		}
    	return sb.toString();
    }

    /**
     * Parse CSV line
     * @param elements CSV line
     * @return alements as String[]
     */
    public static List<String> parseLine(String elements) {
        if (elements == null) {
            return new ArrayList<String>();
        }
        // TODO proper CSV parsing
        String[] result = elements.trim().split("\\s*,\\s*");
        return Arrays.asList(result);
    }

    public static void normalize(File in, File out, int skipRows) throws IOException {
    	CSVReader csvIn  = FileUtil.createUtf8CsvReader(in);
    	CSVWriter csvOut = FileUtil.createUtf8CsvWriter(out);
    	normalize(csvIn, csvOut, skipRows);
    	csvOut.close();
    }

    public static void normalize(CSVReader in, CSVWriter out, int skipRows) throws IOException {
    	String[] nextLine;
    	int i = 0;
    	while ((nextLine = in.readNext()) != null) {
    		if (i >= skipRows) {
    			out.writeNext(nextLine);
    		}
    		i++;
	    }
    }
}