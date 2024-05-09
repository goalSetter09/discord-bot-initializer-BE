package hongik.discordbots.initializer;

import java.io.*;

public class StringGenerator {

    public static void main(String[] args) {
        generatePythonBot();
    }

    private static final String INDENT = "\t"; // 들여쓰기 자리에 들어갈 문자

    public static void generatePythonBot() {
        StringBuilder pythonCode = new StringBuilder();

        pythonCode.append(generateSampleMethod("ping", "pong!"));

        writeToFile(pythonCode.toString(), "pingpong!.py");
    }

    private static String generateSampleMethod(String ping, String pong) {
        // Sample feature with parameter to be replaced
        String feature = """
                        {indent}@bot.command()
                        {indent}async def {ping}(ctx):
                        {indent}{indent}await ctx.send("{pong}")
                        """;

        // Replace parameter in the feature
        String filledFeature = feature.replace("{indent}", INDENT);
        filledFeature = filledFeature.replace("{ping}", ping);
        filledFeature = filledFeature.replace("{pong}", pong);

        return filledFeature;
    }

    private static void writeToFile(String content, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
            System.out.println("Python bot source code file created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing Python bot source code file.");
            e.printStackTrace();
        }
    }


}
