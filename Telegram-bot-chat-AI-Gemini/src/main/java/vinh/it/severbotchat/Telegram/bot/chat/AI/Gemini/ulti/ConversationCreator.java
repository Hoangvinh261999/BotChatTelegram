package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.ulti;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity.Conversation;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity.MessagePart;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.repo.ModelDataRepo;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.repo.UserDataRepo;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.service.DataTemp;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationCreator {
    @Autowired
    UserDataRepo userDataRepo;
    @Autowired
    ModelDataRepo modelDataRepo;
    public List<Conversation> generateModelReplyData(){
        List<Conversation> list= new ArrayList<>();
        DataTemp.modelMessage.forEach(s -> {
            list.add(new Conversation("model",new MessagePart(s)));
        });
        return list;
    }

    public List<Conversation> generateUserAskData(){
        List<Conversation> listModel= new ArrayList<>();
        DataTemp.userMessages.forEach(s -> {
            listModel.add(new Conversation("user",new MessagePart(s)));
        });
        return listModel;
    }



}
