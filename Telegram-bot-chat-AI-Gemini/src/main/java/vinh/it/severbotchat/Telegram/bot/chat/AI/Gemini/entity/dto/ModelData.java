package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity.dto;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "ModelData")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ModelData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Lob
    @Column(columnDefinition = "TEXT")
    public String text;
}
