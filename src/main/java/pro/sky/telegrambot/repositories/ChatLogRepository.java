package pro.sky.telegrambot.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.POJO.ChatLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
    public interface ChatLogRepository extends JpaRepository<ChatLog, Integer> {

    List<ChatLog> findByDateMessageEquals(LocalDateTime dateMessage);

    }

