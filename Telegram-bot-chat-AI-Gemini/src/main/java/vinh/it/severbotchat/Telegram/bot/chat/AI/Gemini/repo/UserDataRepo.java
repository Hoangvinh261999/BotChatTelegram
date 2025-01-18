package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity.dto.UserData;

@Repository
public interface UserDataRepo  extends JpaRepository<UserData,Long> {
}
