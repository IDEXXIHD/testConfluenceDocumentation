package com.idexx.labstation.javadoc;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfluenceDoclet {

    private static final String NEW_LINE = "\n";
    private static final String INDENT = "******** ";
    private static final String DOUBLE_INDENT = INDENT + INDENT;
    private static String[] documentationTags = new String[]{
            "date",
            "author",
            "purpose",
            "description",
            "issue",
            "meta",
            "precondition",
            "assert",
    };

    public static boolean start(RootDoc root) {
        Map<ClassDoc, List<MethodDoc>> tests = findTests(root.classes());
        String markup = createMarkup(tests);
        saveMarkup(markup);
        return true;
    }

    private static Map<ClassDoc, List<MethodDoc>> findTests(ClassDoc[] classes) {
        Map<ClassDoc, List<MethodDoc>> tests = new HashMap<ClassDoc, List<MethodDoc>>();

        for (ClassDoc clazz : classes) {
            if (isTestClass(clazz)) {
                List<MethodDoc> methods = new ArrayList<MethodDoc>();

                for (MethodDoc method : clazz.methods()) {
                    if (isTestMethod(method)) {
                        methods.add(method);
                    }
                }

                if (methods.size() > 0) {
                    tests.put(clazz, methods);
                }
            }
        }

        return tests;
    }

    private static boolean isTestClass(ClassDoc clazz) {
        return clazz.toString().endsWith("Test");
    }

    private static boolean isTestMethod(MethodDoc method) {
        AnnotationDesc[] annotations = method.annotations();
        for (AnnotationDesc annotation : annotations) {
            AnnotationTypeDoc type = annotation.annotationType();
            String className = type.asClassDoc().toString();
            if ("org.junit.Test".equals(className)) {
                return true;
            }
        }
        return false;
    }


    private static String createMarkup(Map<ClassDoc, List<MethodDoc>> tests) {
        StringBuilder markup = new StringBuilder();

        for (ClassDoc clazz : tests.keySet()) {
            createClassMarkup(tests, markup, clazz);
        }

        return markup.toString();
    }

    private static void createClassMarkup(Map<ClassDoc, List<MethodDoc>> tests,
                                          StringBuilder markup,
                                          ClassDoc clazz) {
        String className = clazz.toString();
        markup.append(className).append(NEW_LINE).append(NEW_LINE);

        printDocumentationTags(markup, clazz);

        markup.append(NEW_LINE);

        List<MethodDoc> methods = tests.get(clazz);
        for (MethodDoc method : methods) {
            createMethodMarkup(markup, className, method);
        }

        markup.append(NEW_LINE);
    }

    private static void createMethodMarkup(StringBuilder markup, String className, MethodDoc method) {
        String methodName = method.name();
        markup.append(INDENT).append(className).append("#").append(methodName).append(NEW_LINE);

        printDocumentationTags(markup, method);

        markup.append(NEW_LINE);
    }

    private static void printDocumentationTags(StringBuilder markup, Doc doc) {
        for (String documentationTag : documentationTags) {
            Tag[] tags = doc.tags(documentationTag);
            if (tags.length > 0) {
                for (int i = 0; i < tags.length; i++) {
                    String tagValue = tags[i].text();
                    markup.append(DOUBLE_INDENT)
                            .append(documentationTag).append(": ").append(tagValue)
                            .append(NEW_LINE);
                }
            }
        }
    }

    private static void saveMarkup(String markup) {
        try {
            FileWriter writer = new FileWriter("ConfluenceTestDocumentation.txt");
            writer.write(markup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
