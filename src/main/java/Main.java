import gsonObject.Input;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.Deserializer;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.pattern.PatternBuilder;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();

        // Create the options
        Options options = new Options();
        options.addOption(Option.builder("i")
                .desc("read json file ")
                .hasArg()
                .argName("FILE/DIR")
                .build());

        options.addOption("h", "help", false,
                "print this message");

        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("genJavaClass", options, true);
            }

            if (line.hasOption('i')) {
                List<Input> inputs = Deserializer.deserialize(line.getOptionValue('i'));

                writeJavaFiles(inputs);
            }

        } catch (ParseException exp) {
            logger.error("Unexpected parse exception: " + exp.getMessage());
        }
    }

    private static void writeJavaFiles(List<Input> inputs) {
        SpoonAPI launcher = new Launcher();
        launcher.setSourceOutputDirectory("Liu/");
        Factory factory = launcher.getFactory();


        // using regex
        String pattern = "(.*)\\(([^)]*)\\)";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        for (Input i : inputs) {

            CtClass newClass = factory.createClass("generated.java");
            newClass.setSimpleName(i.getClass_name());

            List<String> methodSignatures = i.getMethod_signatures();
            for (String ms : methodSignatures) {

                Matcher matcher = r.matcher(ms);
                if(matcher.find() && matcher.groupCount() >= 2) {
                    // function name
                    String before = matcher.group(1);
                    CtMethod m = factory.createMethod();
                    m.setBody(factory.createBlock());
                    m.setSimpleName(before);

                    // function parameters
                    String after = matcher.group(2);
                    if(null != after) {
                        String[] pList = after.split(",");
                        for(int ind = 0; ind < pList.length; ++ind) {
                            CtParameter p = factory.createParameter();
                            p.setSimpleName(pList[ind]);
                            m.addParameter(p);
                        }
                    }

                    newClass.addMethod(m);
                } else {
                    logger.error("Method Signature Error in Class " + i.getClass_name() +
                            " , bad signature is " + ms);
                }
            }
        }
        launcher.prettyprint();
    }
}
