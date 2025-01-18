package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//@Entity
//@Table(name = "MessagePart")
@Getter
@Setter
public class MessagePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    // Getter và Setter
    private String text;
    public MessagePart(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessagePart() {
    }

}

