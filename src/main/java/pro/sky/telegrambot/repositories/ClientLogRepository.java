package pro.sky.telegrambot.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.POJO.ClientLog;

import java.util.List;

@Repository
public interface ClientLogRepository extends JpaRepository<ClientLog, Integer> {


}
