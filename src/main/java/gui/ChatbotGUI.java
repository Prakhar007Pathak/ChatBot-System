package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// Import your chatbot package
import chatbott.chat;

public class ChatbotGUI extends Application {

    private chat chatbot;
    private final ObservableList<Message> messageList = FXCollections.observableArrayList();
    private ListView<Message> chatView;
    private TextField userInput;

    // --- 1. Message Data Model ---
    private static class Message {
        String text;
        boolean isUser;

        public Message(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }
    }

    // --- 2. Custom ListCell Renderer ---
    private class MessageCell extends ListCell<Message> {
        private final HBox container = new HBox();
        private final Label label = new Label();

        public MessageCell() {
            // Apply the base CSS class for styling the bubble
            label.getStyleClass().add("chat-bubble");
            label.setWrapText(true);

            // The Label is wrapped in an HBox to control horizontal alignment
            container.getChildren().add(label);
            // This ensures the HBox stretches, allowing alignment
            container.setMaxWidth(Double.MAX_VALUE);
        }

        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                label.setText(item.text);

                // Clear previous styles and set new ones based on the sender
                label.getStyleClass().removeAll("bubble-user", "bubble-bot");

                if (item.isUser) {
                    container.setAlignment(Pos.CENTER_RIGHT);
                    label.getStyleClass().add("bubble-user");
                } else {
                    container.setAlignment(Pos.CENTER_LEFT);
                    label.getStyleClass().add("bubble-bot");
                }

                // Set the custom graphic for the cell
                setGraphic(container);
            }
        }
    }

    // --- 3. Main Application Setup ---
    @Override
    public void start(Stage stage) {
        // Initialize the chatbot
        chatbot = new chat();

        // 1. CHAT HEADER (Top Bar)
        HBox header = new HBox(10);
        header.setId("header-pane");
        // Placeholder elements for the back arrow and menu icon
        Label backArrow = new Label("<-");
        Label menuIcon = new Label("≡");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label titleLabel = new Label("CHATBOT");
        titleLabel.setId("header-label");

        header.getChildren().addAll(backArrow, titleLabel, spacer, menuIcon);
        header.setAlignment(Pos.CENTER_LEFT);

        // 2. MESSAGE AREA (ListView)
        chatView = new ListView<>(messageList);
        chatView.setCellFactory(param -> new MessageCell()); // Apply the custom renderer
        chatView.setFocusTraversable(false);
        chatView.setId("chat-view");

        // Auto-scroll logic to show the latest message
        messageList.addListener((javafx.collections.ListChangeListener.Change<? extends Message> c) -> {
            // Scroll to the end of the list when a message is added
            chatView.scrollTo(messageList.size() - 1);
        });

        // 3. INPUT AREA (Bottom)
        userInput = new TextField();
        userInput.setId("user-input");
        userInput.setPromptText("Type a message...");

        Button sendButton = new Button("➤");
        sendButton.setId("send-button");
        sendButton.setPrefSize(40, 40);

        // HBox to hold the text field and send button
        HBox inputBar = new HBox(10, userInput, sendButton);
        HBox.setHgrow(userInput, Priority.ALWAYS); // Text field fills space
        inputBar.setId("input-pane");

        // 4. ROOT LAYOUT (VBox)
        VBox root = new VBox();
        root.getChildren().addAll(header, chatView, inputBar);
        VBox.setVgrow(chatView, Priority.ALWAYS);

        // 5. Action Handlers
        sendButton.setOnAction(e -> sendMessage());
        userInput.setOnAction(e -> sendMessage());

        // 6. Scene and stage setup
        Scene scene = new Scene(root, 400, 650);

        // Load the CSS stylesheet
        String cssPath = getClass().getResource("/chatbot_style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setScene(scene);
        stage.setTitle("AI Chatbot");
        stage.show();
    }

    // --- 4. Message Sending Logic ---
    private void sendMessage() {
        String input = userInput.getText().trim();
        if (!input.isEmpty()) {
            // Add user message to the list (triggers view update)
            messageList.add(new Message(input, true));

            // Get response from chatbot
            String reply = chatbot.getResponse(input);

            // Add bot response (triggers view update)
            messageList.add(new Message(reply, false));

            userInput.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}