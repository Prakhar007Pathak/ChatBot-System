package chatbott;

import java.io.File;
import org.alicebot.ab.*;
import org.alicebot.ab.utils.*;

public class chat {

    private static final boolean TRACE_MODE = false;
    private Bot bot;
    private Chat chatSession;

    // Constructor to initialize bot
    public chat() {
        try {
            String resourcePath = getPath();
            MagicBooleans.trace_mode = TRACE_MODE;
            bot = new Bot("super", resourcePath);
            chatSession = new Chat(bot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to get bot response for a user input
    public String getResponse(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            userInput = MagicStrings.null_input;
        }
        return chatSession.multisentenceRespond(userInput);
    }

    // Existing main method (optional, still works for console)
    public static void main(String args[]) {
        chat chatbot = new chat();  // initialize bot
        try {
            while (true) {
                System.out.print("YOU : ");
                String textLine = IOUtils.readInputTextLine();
                if (textLine == null || textLine.length() < 1) {
                    textLine = MagicStrings.null_input;
                } else if (textLine.equals("q")) {
                    System.exit(0);
                } else if (textLine.equals("wq")) {
                    chatbot.bot.writeQuit();
                    System.exit(0);
                } else {
                    String response = chatbot.getResponse(textLine);
                    System.out.println("BOT :" + response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper to get resources path
    private static String getPath() {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String resourcePath = path + File.separator + "src" + File.separator + "main" + File.separator + "resources";
        return resourcePath;
    }
}
