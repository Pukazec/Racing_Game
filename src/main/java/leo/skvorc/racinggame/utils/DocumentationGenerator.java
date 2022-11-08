package leo.skvorc.racinggame.utils;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DocumentationGenerator {

    private static final String CLASS_EXTENSION = ".class";
    private static final String HORIZONTAL_LINE = "<hr>";
    private static final String LINE_BREAK = "<br>";

    public static void generateParametersString() {

        File documentationFile = new File("documentation.html");

        try {

            FileWriter writer = new FileWriter(documentationFile);

            writer.write("<!DOCTYPE html>");
            writer.write("<html>");
            writer.write("<head>");
            writer.write("<title>Project documentation</title>");
            writer.write("</head>");
            writer.write("<body>");
            writer.write("<h1>Project documentation</h1>");

            List<Path> paths = Files.walk(Paths.get("."))
                    .filter(path -> path.getFileName().toString().endsWith(CLASS_EXTENSION))
                    .collect(Collectors.toList());

            for (Path path : paths) {
                String[] tokens = path.toString().split(Pattern.quote(System.getProperty("file.separator")));

                Boolean startBuildingPath = false;

                StringBuilder sb = new StringBuilder();

                for (String token : tokens) {
                    if ("classes".equals(token)) {
                        startBuildingPath = true;
                        continue;
                    }

                    if (startBuildingPath) {

                        if (token.endsWith(CLASS_EXTENSION)) {
                            sb.append(token.substring(0, token.indexOf(".")));
                        } else {
                            sb.append(token);
                            sb.append(".");
                        }
                    }
                }

                if ("module-info".equals(sb.toString())) {
                    continue;
                }

                try {
                    Class<?> clazz = Class.forName(sb.toString());



                    writer.write(HORIZONTAL_LINE);
                    writer.write("<h2>CLASS</h2>");
                    writer.write("<h3>" + annotations(clazz.getAnnotations()) + Modifier.toString(clazz.getModifiers()) + " " + clazz.getName() + "</h3>");

                    StringBuilder classFieldString = new StringBuilder();

                    for (Field classField : clazz.getDeclaredFields()) {
                        classFieldString.append(annotations(classField.getAnnotations()));
                        classFieldString.append(Modifier.toString(classField.getModifiers()));
                        classFieldString.append(" ");
                        classFieldString.append(classField.getType().getSimpleName());
                        classFieldString.append(" ");
                        classFieldString.append(classField.getName());
                        classFieldString.append(" ");
                        classFieldString.append(LINE_BREAK);
                    }

                    writer.write("<h3>FIELDS</h3>");

                    writer.write("<h4>" + classFieldString + "</h4>");

                    Constructor[] constructors = clazz.getConstructors();

                    writer.write("<h3>CONSTRUCTORS</h3>");

                    writer.write("<h4>");
                    for (Constructor c : constructors) {
                        StringBuilder constructorStringBuilder = new StringBuilder();

                        constructorStringBuilder.append(annotations(c.getAnnotations()));
                        constructorStringBuilder.append(Modifier.toString(c.getModifiers()));
                        constructorStringBuilder.append(" ");
                        constructorStringBuilder.append(c.getName());
                        constructorStringBuilder.append("(");
                        constructorStringBuilder.append(generateParametersString(c));
                        constructorStringBuilder.append(")");
                        constructorStringBuilder.append(LINE_BREAK);

                        writer.write(constructorStringBuilder.toString());
                    }
                    writer.write("</h4>");

                    Method[] methods = clazz.getMethods();

                    writer.write("<h3>METHODS</h3>");

                    writer.write("<h4>");
                    for (Method m : methods) {

                        StringBuilder exceptionsBuilder = new StringBuilder();

                        for (int i = 0; i < m.getExceptionTypes().length; i++) {
                            if (exceptionsBuilder.isEmpty()) {
                                exceptionsBuilder.append(" throws ");
                            }

                            Class exceptionClass = m.getExceptionTypes()[i];
                            exceptionsBuilder.append(exceptionClass.getSimpleName());

                            if (i < m.getExceptionTypes().length - 1) {
                                exceptionsBuilder.append(", ");
                            }
                        }

                        StringBuilder methodStringBuilder = new StringBuilder();

                        methodStringBuilder.append(annotations(m.getAnnotations()));
                        methodStringBuilder.append(Modifier.toString(m.getModifiers()));
                        methodStringBuilder.append(" ");
                        methodStringBuilder.append(m.getReturnType().getSimpleName());
                        methodStringBuilder.append(" ");
                        methodStringBuilder.append(m.getName());
                        methodStringBuilder.append("(");
                        methodStringBuilder.append(generateParametersString(m));
                        methodStringBuilder.append(")");
                        methodStringBuilder.append(exceptionsBuilder);
                        methodStringBuilder.append(LINE_BREAK);

                        writer.write(methodStringBuilder.toString());
                    }
                    writer.write("</h4>");

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            writer.write("</body>");
            writer.write("</html>");
            writer.close();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error while generating documentation!");
            alert.setHeaderText("Cannot find the files");
            alert.setContentText("The class files cannot be accessed.");

            alert.showAndWait();
        }
    }

    private static String annotations(Annotation[] annotations) {
        StringBuilder sb = new StringBuilder();
        if (annotations.length != 0) {
            for (Annotation a : annotations) {
                sb.append(a.toString());
                sb.append(LINE_BREAK);
            }
        }
        return sb.toString();
    }

    private static <T extends Executable> String generateParametersString(T executable) {
        Parameter[] params = executable.getParameters();

        StringBuilder methodsParams = new StringBuilder();

        for (int i = 0; i < params.length; i++) {
            String modifierString = Modifier.toString(params[i].getModifiers());

            if (!modifierString.isEmpty()) {
                methodsParams.append(modifierString);
                methodsParams.append(" ");
            }
            methodsParams.append(params[i].getType().getSimpleName());
            methodsParams.append(" ");
            methodsParams.append(params[i].getName());

            if (i < (params.length - 1)) {
                methodsParams.append(", ");
            }
        }

        return methodsParams.toString();
    }
}
