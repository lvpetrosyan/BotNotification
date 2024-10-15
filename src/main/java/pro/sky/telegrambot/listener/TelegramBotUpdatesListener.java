package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.POJO.ChatLog;
import pro.sky.telegrambot.POJO.ClientLog;
import pro.sky.telegrambot.repositories.ChatLogRepository;
import pro.sky.telegrambot.repositories.ClientLogRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private ChatLogRepository chatLogRepository;

    @Autowired
    private ClientLogRepository clientLogRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Проверяем, есть ли сообщение в обновлении
            if (update.message() != null) {
                // Получаем текст сообщения и прописываем какую команду будем искать
                String messageText = update.message().text();
                String textComand="/start";

                // Проверяем, является ли сообщение командой /start
                if (textComand.equals(messageText)) {

                    // Получаем идентификатор чата
                    Long chatId = update.message().chat().id();

                    // Отправляем ответное сообщение
                    SendMessage response = new SendMessage(chatId, "Тук-тук :) Напиши Дату и время в формате 15.10.2022 22:00 и текст уведомления, который нужно тебе прислать в это время");
                    telegramBot.execute(response);}

               else {
                        // Извлекаем дату из текста сообщения
                        LocalDateTime dateMessage = extractDateFromMessage(messageText);
                        if (dateMessage != null) {
                            // Сохраняем сообщение и дату в базу данных
                            ChatLog chatLog = new ChatLog(
                                    0, // id будет сгенерирован автоматически
                                    update.message().chat().id().intValue(),
                                    messageText,
                                    dateMessage
                            );
                            chatLogRepository.save(chatLog);
                            // Сохраняем данные о клиенте в базу данных
                            ClientLog clientLog = new ClientLog(
                                    0, // id будет сгенерирован автоматически
                                    update.message().from().id().intValue(),
                                    update.message().chat().id().intValue()
                            );
                            clientLogRepository.save(clientLog);
                        }
                        //Проверяем, корректность введенной даты
                    if (dateMessage.compareTo(LocalDateTime.now()) < 0) {
                        // Отправляем уведомление о неверной дате и времени
                        Long chatId = update.message().chat().id();
                        SendMessage response = new SendMessage(chatId, "Дата и время выбрано неверно! Укажи другую!");
                        telegramBot.execute(response);
                    }
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
//Метод чтобы вычленить дату и текст сообщения в логах
    private LocalDateTime extractDateFromMessage(String messageText) {
        // Регулярное выражение для поиска даты в формате "dd.MM.yyyy HH:mm"
        Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2})");
        Matcher matcher = pattern.matcher(messageText);

        if (matcher.find()) {
            String dateString = matcher.group(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return LocalDateTime.parse(dateString, formatter);
        }

        return null;
    }
    // Метод шедулера, который выполняется каждую минуту
    @Scheduled(cron = "0 * * * * *") // Выполнять каждую минуту
    public void sendNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMinute = now.truncatedTo(ChronoUnit.MINUTES);//Убираем до минут, т.к точное время впоть до милисек трудно найти

        List<ChatLog> chatLogs = chatLogRepository.findByDateMessageEquals(startOfMinute);

        for (ChatLog chatLog : chatLogs) {
            SendMessage message = new SendMessage(chatLog.getChatId(), "Уведомление: " + chatLog.getTextMessage());
            telegramBot.execute(message);
        }
    }
}