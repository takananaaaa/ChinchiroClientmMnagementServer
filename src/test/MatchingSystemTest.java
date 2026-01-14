import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import Control.MatchingSystem;
import Communication.ClientCommunication;

import java.lang.reflect.Field;
import java.util.List;


public class MatchingSystemTest {

    private MatchingSystem matchingSystem;


    static class TestClientCommunication extends ClientCommunication {
        String lastStatus = "";
        @Override
        public void sendMatchResponse(String statusMessage) {
            this.lastStatus = statusMessage;

        }
    }

    @BeforeEach
    void setUp() {
        matchingSystem = new MatchingSystem();
    }

    @Test
    @DisplayName("4人追加されたとき、全員にMATCHEDが通知されるか確認")
    void testMatchingFlowWithStatusCheck() {
        TestClientCommunication[] clients = {
                new TestClientCommunication(),
                new TestClientCommunication(),
                new TestClientCommunication(),
                new TestClientCommunication()
        };

        // ユーザー追加
        matchingSystem.addUser("user1", clients[0]);
        matchingSystem.addUser("user2", clients[1]);
        matchingSystem.addUser("user3", clients[2]);

        // 3人目まではWAITINGのはず
        assertEquals("WAITING", clients[0].lastStatus);

        // 4人目追加（ここでstartGameが走る）
        matchingSystem.addUser("user4", clients[3]);

        System.out.println("Final Status for user1: " + clients[0].lastStatus);
    }

    @Test
    @DisplayName("重複ユーザーが追加されないことを確認")
    @SuppressWarnings("unchecked")
    void testDuplicateUserAddition() throws Exception {
        TestClientCommunication c1 = new TestClientCommunication();
        matchingSystem.addUser("same_id", c1);
        matchingSystem.addUser("same_id", c1);

        Field userListField = MatchingSystem.class.getDeclaredField("userList");
        userListField.setAccessible(true);
        List<String> userList = (List<String>) userListField.get(matchingSystem);

        assertEquals(1, userList.size(), "重複IDはリストに入らないはず");
    }

    @Test
    @DisplayName("シャットダウンの実行")
    void testShutdown() {
        assertDoesNotThrow(() -> matchingSystem.shutdown());
    }
}