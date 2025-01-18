package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity;

public class RenderedMessage {
    private String text;
    private String renderedContent;

    // Constructor
    public RenderedMessage(String text, String renderedContent) {
        this.text = text;
        this.renderedContent = renderedContent;
    }

    // Getters và setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRenderedContent() {
        return renderedContent;
    }

    public void setRenderedContent(String renderedContent) {
        this.renderedContent = renderedContent;
    }

    @Override
    public String toString() {
        return "RenderedMessage{" +
                "text='" + text + '\'' +
                ", renderedContent='" + renderedContent + '\'' +
                '}';
    }
}

