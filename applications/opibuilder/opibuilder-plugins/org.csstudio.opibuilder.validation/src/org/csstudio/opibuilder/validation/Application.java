/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.csstudio.opibuilder.validation.core.SchemaVerifier;
import org.csstudio.opibuilder.validation.core.ValidationFailure;
import org.csstudio.opibuilder.validation.core.Validator;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 *
 * <code>Application</code> verifies the opi files provided by the parameters against the provided
 * schema and validation rules. The results of validation are either printed to the console or into
 * a file specified by one of the parameters.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Application implements IApplication {

    private static final char SEPARATOR = ';';
    private static final String HEADER = "PATH;WIDGET_NAME;WIDGET_TYPE;LINE_NUMBER;PROPERTY;VALUE;EXPECTED_VALUE";
//    private static final Integer EXIT_ERROR = -1;

    private static final String VALIDATION_RULES = "-rules";
    private static final String SCHEMA = "-schema";
    private static final String OPI_LOCATION = "-opilocation";
    private static final String RESULTS = "-results";
    private static final String HELP = "-help";
    private static final String VERSION = "-version";
    private static final String PRINT_RESULTS = "-print";

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    @Override
    public Object start(IApplicationContext context) throws Exception {
        String rules = null;
        String location = null;
        String schema = null;
        String results = null;
        boolean printResults = false;
        final String args[] = (String[]) context.getArguments().get("application.args");
        for (int i = 0; i < args.length; i++) {
            if (HELP.equals(args[i])) {
                printHelp();
                return EXIT_OK;
            } else if (VERSION.equals(args[i])) {
                String version = (String) context.getBrandingBundle().getHeaders().get("Bundle-Version");
                System.out.println(version);
                return EXIT_OK;
            } else if (PRINT_RESULTS.equals(args[i])) {
                printResults = true;
            } else if (VALIDATION_RULES.equals(args[i])) {
                rules = args[++i];
            } else if (OPI_LOCATION.equals(args[i])) {
                location = args[++i];
            } else if (SCHEMA.equals(args[i])) {
                schema = args[++i];
            } else if (RESULTS.equals(args[i])) {
                results = args[++i];
            }
        }

        if (rules == null) {
            System.err.println("Validation Rules file is not defined!");
            printHelp();
            return EXIT_OK;
        } else if (schema == null) {
            System.err.println("OPI Schema is not defined!");
            printHelp();
            return EXIT_OK;
        } else if (location == null) {
            location = new File(".").getAbsoluteFile().getParentFile().getAbsolutePath();
        }

        Path schemaPath = new Path(new File(schema).getAbsolutePath());
        Path rulesPath = new Path(new File(rules).getAbsolutePath());
        SchemaVerifier verifier = Validator.createVerifier(schemaPath, rulesPath, null);
        File file = new File(location);
        if (!file.exists()) {
            System.err.println("The path '" + location + "' does not exist.");
            return EXIT_OK;
        }
        check(verifier, file);
        ValidationFailure[] failures = verifier.getValidationFailures();
        StringBuilder sb = new StringBuilder();
        sb.append("Validated files: ").append(verifier.getNumberOfAnalyzedFiles()).append('\n');
        sb.append("Files with failures: ").append(verifier.getNumberOfFilesFailures()).append('\n');
        sb.append("Validated widgets: ").append(verifier.getNumberOfAnalyzedWidgets()).append('\n');
        sb.append("Widgets with failures: ").append(verifier.getNumberOfWidgetsFailures()).append('\n');
        sb.append("Validated RO properties: ").append(verifier.getNumberOfROProperties()).append('\n');
        sb.append("Critical RO failures: ").append(verifier.getNumberOfCriticalROFailures()).append('\n');
        sb.append("Major RO failures: ").append(verifier.getNumberOfMajorROFailures()).append('\n');
        sb.append("Validated WRITE properties: ").append(verifier.getNumberOfWRITEProperties()).append('\n');
        sb.append("WRITE failures: ").append(verifier.getNumberOfWRITEFailures()).append('\n');
        sb.append("Validated RW properties: ").append(verifier.getNumberOfRWProperties()).append('\n');
        sb.append("RW failures: ").append(verifier.getNumberOfRWFailures()).append('\n');
        sb.append("Deprecated properties used: ").append(verifier.getNumberOfDeprecatedFailures());

        if (printResults) {
            System.out.println(HEADER);
            for (ValidationFailure f : failures) {
                System.out.println(toMessage(f));
            }
            System.out.println(sb.toString());
        }

        if (results != null) {
            System.out.println("Results printed to file '" + new File(results).getAbsolutePath() + "'.");
            PrintWriter pw = new PrintWriter(new File(results));
            pw.println(HEADER);
            for (ValidationFailure f : failures) {
                pw.println(toMessage(f));
            }
            pw.close();

            int idx = results.lastIndexOf('.');
            String summaryFile = null;
            if (idx < 1) {
                summaryFile = results + "_summary";
            } else {
                summaryFile = results.substring(0,idx) + "_summary" + results.substring(idx);
            }
            System.out.println("Results summary printed to file '" + new File(summaryFile).getAbsolutePath() + "'.");
            pw = new PrintWriter(new File(summaryFile));
            pw.println(sb.toString());
            pw.close();
        }

        return EXIT_OK;
    }

    /**
     * Convert the validation failure into a csv format: file path; widget name; line number; property; message
     *
     * @param f the validation failure
     * @return the full message describing the failure
     */
    private String toMessage(ValidationFailure f) {
        StringBuilder sb = new StringBuilder(300);
        sb.append(f.getPath()).append(SEPARATOR).append(f.getWidgetName()).append(SEPARATOR)
            .append(f.getWidgetType()).append(SEPARATOR).append(f.getLineNumber()).append(SEPARATOR)
            .append(f.getProperty()).append(SEPARATOR).append('"').append(f.getActual()).append('"').append(SEPARATOR)
            .append('"').append(f.getExpected()).append('"');
        return sb.toString();
    }

    /**
     * Checks the given file. If the file is a directory all its children are checked. A file is only checked
     * if it is an opi file.
     *
     * @param verifier the verifier to use for checking
     * @param file the file to check
     * @throws IllegalStateException
     * @throws IOException
     */
    private void check(SchemaVerifier verifier, File file)
            throws IllegalStateException, IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    check(verifier, f);
                }
            }
        } else if (file.getAbsolutePath().toLowerCase().endsWith(".opi")) {
            System.out.println("Validating file: " + file.getAbsolutePath());
            verifier.validate(new Path(file.getAbsolutePath()));
        }
    }

    private void printHelp() {
        System.out.println("Options:");
        System.out.println(String.format("  %-30s: %s", HELP, "Print this help."));
        System.out.println(String.format("  %-30s: %s", VERSION, "Print the version number of this tool."));
        System.out.println(String.format("  %-30s: %s", VALIDATION_RULES + " <FILE>",
                "Path to the file with validation rules."));
        System.out.println(String.format("  %-30s: %s", SCHEMA + " <FILE>", "Path to the OPI schema file."));
        System.out.println(String.format("  %-30s: %s", OPI_LOCATION + " <PATH>",
                "Path to the OPI file or folder to validate. If null current directory is used."));
        System.out.println(String.format("  %-30s: %s", RESULTS + " <FILE> ",
                "Path to the file into which the results will be printed"));
        System.out.println(String.format("  %-30s: %s", PRINT_RESULTS,"Print validation results to console."));

        try {
            //if the application terminates too quickly, the framework is terminated before it was fully started
            //and some annoying stack traces are printed
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
    public void stop() {
    }

}
