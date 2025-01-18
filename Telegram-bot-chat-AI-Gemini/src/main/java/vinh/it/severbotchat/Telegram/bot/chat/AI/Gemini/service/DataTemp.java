package vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity.dto.ModelData;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.entity.dto.UserData;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.repo.ModelDataRepo;
import vinh.it.severbotchat.Telegram.bot.chat.AI.Gemini.repo.UserDataRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataTemp {

    @Autowired
    private UserDataRepo userDataRepo;

    @Autowired
    private ModelDataRepo modelDataRepo;

    // Các biến static
    public static List<String> modelMessage = new ArrayList<>();
    public static List<String> userMessages= new ArrayList<>();

    // Phương thức @PostConstruct để khởi tạo các static variables
    @PostConstruct
    public void init() {
        // Gán giá trị cho các static variables sau khi các phụ thuộc đã được tiêm
        modelMessage = modelDataRepo.findAll()
                .stream()
                .map(ModelData::getText)
                .collect(Collectors.toList());

        userMessages = userDataRepo.findAll()
                .stream()
                .map(UserData::getText)
                .collect(Collectors.toList());
    }
}
