package team.nine.booknutsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.nine.booknutsbackend.domain.Debate.DebateRoom;

import java.util.List;

public interface DebateRoomRepository extends JpaRepository<DebateRoom, Long> {
    List<DebateRoom> findByTypeAndStatus(int type, int status);
    List<DebateRoom> findByStatus(int status);
}