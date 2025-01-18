package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity.dto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UserData")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Lob
    @Column(columnDefinition = "TEXT")
    public String text;
}
