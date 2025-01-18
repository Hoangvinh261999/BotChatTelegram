package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity;

//@Entity
//@Table(name = "Conversation")
//@Getter
//@Setter
public class Conversation {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String role;
    private MessagePart parts ;
    public Conversation(String role, MessagePart parts) {
        this.role = role;
        this.parts = parts;
    }
    public Conversation() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setParts(MessagePart parts) {
        this.parts = parts;
    }

    public String getRole() {
        return role;
    }

    public MessagePart getParts() {
        return parts;
    }

    @Override
    public String toString() {
        return "Role: " + role + "\n" +
                "Text: " + parts.getText();
    }

}
