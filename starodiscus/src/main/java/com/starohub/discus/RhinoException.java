/* SH
 *
 * Modified by Staro Hub @ 2020 [ https://github.com/starohub ]
 *
 * Ref: https://github.com/mozilla/rhino/blob/Rhino1_7R3_RELEASE/src/org/mozilla/javascript/RhinoException.java
 *
 * SH
 */

/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1997-1999
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Norris Boyd
 *   Igor Bukanov
 *   Hannes Wallnoefer
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */


package com.starohub.discus;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class of exceptions thrown by the JavaScript engine.
 */
public abstract class RhinoException extends RuntimeException
{

    RhinoException()
    {
        Evaluator e = Context.createInterpreter();
        if (e != null)
            e.captureStackInfo(this);
    }

    RhinoException(String details)
    {
        super(details);
        Evaluator e = Context.createInterpreter();
        if (e != null)
            e.captureStackInfo(this);
    }

    @Override
    public final String getMessage()
    {
        String details = details();
        if (sourceName == null || lineNumber <= 0) {
            return details;
        }
        StringBuffer buf = new StringBuffer(details);
        buf.append(" (");
        if (sourceName != null) {
            buf.append(sourceName);
        }
        if (lineNumber > 0) {
            buf.append('#');
            buf.append(lineNumber);
        }
        buf.append(')');
        return buf.toString();
    }

    public String details()
    {
        return super.getMessage();
    }

    /**
     * Get the uri of the script source containing the error, or null
     * if that information is not available.
     */
    public final String sourceName()
    {
        return sourceName;
    }

    /**
     * Initialize the uri of the script source containing the error.
     *
     * @param sourceName the uri of the script source responsible for the error.
     *                   It should not be <tt>null</tt>.
     *
     * @throws IllegalStateException if the method is called more then once.
     */
    public final void initSourceName(String sourceName)
    {
        if (sourceName == null) throw new IllegalArgumentException();
        if (this.sourceName != null) throw new IllegalStateException();
        this.sourceName = sourceName;
    }

    /**
     * Returns the line number of the statement causing the error,
     * or zero if not available.
     */
    public final int lineNumber()
    {
        return lineNumber;
    }

    /**
     * Initialize the line number of the script statement causing the error.
     *
     * @param lineNumber the line number in the script source.
     *                   It should be positive number.
     *
     * @throws IllegalStateException if the method is called more then once.
     */
    public final void initLineNumber(int lineNumber)
    {
        if (lineNumber <= 0) throw new IllegalArgumentException(String.valueOf(lineNumber));
        if (this.lineNumber > 0) throw new IllegalStateException();
        this.lineNumber = lineNumber;
    }

    /**
     * The column number of the location of the error, or zero if unknown.
     */
    public final int columnNumber()
    {
        return columnNumber;
    }

    /**
     * Initialize the column number of the script statement causing the error.
     *
     * @param columnNumber the column number in the script source.
     *                     It should be positive number.
     *
     * @throws IllegalStateException if the method is called more then once.
     */
    public final void initColumnNumber(int columnNumber)
    {
        if (columnNumber <= 0) throw new IllegalArgumentException(String.valueOf(columnNumber));
        if (this.columnNumber > 0) throw new IllegalStateException();
        this.columnNumber = columnNumber;
    }

    /**
     * The source text of the line causing the error, or null if unknown.
     */
    public final String lineSource()
    {
        return lineSource;
    }

    /**
     * Initialize the text of the source line containing the error.
     *
     * @param lineSource the text of the source line responsible for the error.
     *                   It should not be <tt>null</tt>.
     *
     * @throws IllegalStateException if the method is called more then once.
     */
    public final void initLineSource(String lineSource)
    {
        if (lineSource == null) throw new IllegalArgumentException();
        if (this.lineSource != null) throw new IllegalStateException();
        this.lineSource = lineSource;
    }

    final void recordErrorOrigin(String sourceName, int lineNumber,
                                 String lineSource, int columnNumber)
    {
        // XXX: for compatibility allow for now -1 to mean 0
        if (lineNumber == -1) {
            lineNumber = 0;
        }

        if (sourceName != null) {
            initSourceName(sourceName);
        }
        if (lineNumber != 0) {
            initLineNumber(lineNumber);
        }
        if (lineSource != null) {
            initLineSource(lineSource);
        }
        if (columnNumber != 0) {
            initColumnNumber(columnNumber);
        }
    }

    private String generateStackTrace()
    {
        // Get stable reference to work properly with concurrent access
        CharArrayWriter writer = new CharArrayWriter();
        super.printStackTrace(new PrintWriter(writer));
        String origStackTrace = writer.toString();
        Evaluator e = Context.createInterpreter();
        if (e != null)
            return e.getPatchedStack(this, origStackTrace);
        return null;
    }

    /**
     * Get a string representing the script stack of this exception.
     * If optimization is enabled, this includes java stack elements
     * whose source and method names suggest they have been generated
     * by the Rhino script compiler.
     * @return a script stack dump
     * @since 1.6R6
     */
    public String getScriptStackTrace()
    {
        StringBuilder buffer = new StringBuilder();
        String lineSeparator = SecurityUtilities.getSystemProperty("line.separator");
        ScriptStackElement[] stack = getScriptStack();
        for (ScriptStackElement elem : stack) {
            if (useMozillaStackStyle) {
                elem.renderMozillaStyle(buffer);
            } else {
                elem.renderJavaStyle(buffer);
            }
            buffer.append(lineSeparator);
        }
        return buffer.toString();
    }

    /**
     * Get a string representing the script stack of this exception.
     * @deprecated the filter argument is ignored as we are able to
     * recognize script stack elements by our own. Use
     * #getScriptStackTrace() instead.
     * @param filter ignored
     * @return a script stack dump
     * @since 1.6R6
     */
    public String getScriptStackTrace(FilenameFilter filter)
    {
        return getScriptStackTrace();
    }

    /**
     * Get the script stack of this exception as an array of
     * {@link ScriptStackElement}s.
     * If optimization is enabled, this includes java stack elements
     * whose source and method names suggest they have been generated
     * by the Rhino script compiler.
     * @return the script stack for this exception
     * @since 1.7R3
     */
    public ScriptStackElement[] getScriptStack() {
        List<ScriptStackElement> list = new ArrayList<ScriptStackElement>();
        ScriptStackElement[][] interpreterStack = null;
        if (interpreterStackInfo != null) {
            Evaluator interpreter = Context.createInterpreter();
            if (interpreter instanceof Interpreter)
                interpreterStack = ((Interpreter) interpreter).getScriptStackElements(this);
        }
        int interpreterStackIndex = 0;
        StackTraceElement[] stack = getStackTrace();
        // Pattern to recover function name from java method name -
        // see Codegen.getBodyMethodName()
        // kudos to Marc Guillemot for coming up with this
        Pattern pattern = Pattern.compile("_c_(.*)_\\d+");
        for (StackTraceElement e : stack) {
            String fileName = e.getFileName();
            if (e.getMethodName().startsWith("_c_")
                    && e.getLineNumber() > -1
                    && fileName != null
                    && !fileName.endsWith(".java")) {
                String methodName = e.getMethodName();
                Matcher match = pattern.matcher(methodName);
                // the method representing the main script is always "_c_script_0" -
                // at least we hope so
                methodName = !"_c_script_0".equals(methodName) && match.find() ?
                        match.group(1) : null;
                list.add(new ScriptStackElement(fileName, methodName, e.getLineNumber()));
            } else if ("com.starohub.discus.Interpreter".equals(e.getClassName())
                    && "interpretLoop".equals(e.getMethodName())
                    && interpreterStack != null
                    && interpreterStack.length > interpreterStackIndex) {
                for (ScriptStackElement elem : interpreterStack[interpreterStackIndex++]) {
                    list.add(elem);
                }
            }
        }
        return list.toArray(new ScriptStackElement[list.size()]);
    }


    @Override
    public void printStackTrace(PrintWriter s)
    {
        if (interpreterStackInfo == null) {
            super.printStackTrace(s);
        } else {
            s.print(generateStackTrace());
        }
    }

    @Override
    public void printStackTrace(PrintStream s)
    {
        if (interpreterStackInfo == null) {
            super.printStackTrace(s);
        } else {
            s.print(generateStackTrace());
        }
    }

    /**
     * Returns true if subclasses of <code>RhinoException</code>   
     * use the Mozilla/Firefox style of rendering script stacks
     * (<code>functionName()@fileName:lineNumber</code>)
     * instead of Rhino's own Java-inspired format
     * (<code>    at fileName:lineNumber (functionName)</code>).
     * @return true if stack is rendered in Mozilla/Firefox style
     * @see ScriptStackElement
     * @since 1.7R3
     */
    public static boolean usesMozillaStackStyle() {
        return useMozillaStackStyle;
    }

    /**
     * Tell subclasses of <code>RhinoException</code> whether to
     * use the Mozilla/Firefox style of rendering script stacks
     * (<code>functionName()@fileName:lineNumber</code>)
     * instead of Rhino's own Java-inspired format
     * (<code>    at fileName:lineNumber (functionName)</code>)
     * @param flag whether to render stacks in Mozilla/Firefox style
     * @see ScriptStackElement
     * @since 1.7R3
     */
    public static void useMozillaStackStyle(boolean flag) {
        useMozillaStackStyle = flag;
    }

    private static boolean useMozillaStackStyle = false;

    private String sourceName;
    private int lineNumber;
    private String lineSource;
    private int columnNumber;

    Object interpreterStackInfo;
    int[] interpreterLineData;
}
