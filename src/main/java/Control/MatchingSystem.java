package Control;

import Communication.ApplicationServerCommunication;
import Communication.ClientCommunication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MatchingSystem {

    // マッチング待機ユーザIDリスト (順序を保持するリスト)
    private final List<String> userList;

    // ユーザIDと通信クラスの紐付け (通知を送るために必要)
    private final Map<String, ClientCommunication> userConnections;

    private String matchingInfo;
    private final int TIME_LIMIT_SECONDS = 60;
    private final int REQUIRED_USERS = 4;
    private final int MIN_USERS_TO_START = 2;

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> timerTask;

    private final ApplicationServerCommunication appComm;

    public MatchingSystem() {
        this.userList = Collections.synchronizedList(new ArrayList<>());
        this.userConnections = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.matchingInfo = "WAITING";
        this.appComm = new ApplicationServerCommunication();
    }

    // ★修正: ClientCommunication も受け取るように変更
    public synchronized void addUser(String id, ClientCommunication client) {
        if (id == null || id.isEmpty()) return;

        if (!userList.contains(id)) {
            userList.add(id);
            userConnections.put(id, client);

            // 登録時点では「待機中」を通知
            client.sendMatchResponse("WAITING");
        }

        updateMatchingInfo();

        if (userList.size() == 1) {
            startTimer();
        }

        if (userList.size() >= REQUIRED_USERS) {
            startGame();
        }
    }

    public synchronized void removeUser(String id) {
        userList.remove(id);
        userConnections.remove(id);
        updateMatchingInfo();
    }

    private synchronized void startTimer() {
        if (timerTask != null && !timerTask.isDone()) return;

        matchingInfo = "COUNTING";
        System.out.println("[Matching] Timer started (60s)");

        timerTask = scheduler.schedule(
                this::judgeMatchingResult,
                TIME_LIMIT_SECONDS,
                TimeUnit.SECONDS
        );
    }

    private synchronized void judgeMatchingResult() {
        if (userList.size() >= MIN_USERS_TO_START) {
            startGame();
        } else {
            processOperationTimeout();
        }
    }

    private synchronized void startGame() {
        // ★修正: 先頭から最大4人を取得
        int count = Math.min(userList.size(), REQUIRED_USERS);
        List<String> matchMembers = new ArrayList<>(userList.subList(0, count));

        System.out.println("[Matching] Game started. members=" + matchMembers);

        // 1. APサーバに通知 (HTTP POST)
        boolean success = appComm.notifyMatchingRequest(matchMembers);

        if (success) {
            // 2. 成功したら、該当する各クライアントに「マッチング成立」を通知
            for (String memberId : matchMembers) {
                ClientCommunication conn = userConnections.get(memberId);
                if (conn != null) {
                    // ここで初めてクライアントは画面遷移する
                    // "MATCHED" などの合言葉を送る (home.htmlの修正が必要な場合あり)
                    // home.htmlが {result: true} で遷移するなら sendMatchResponse("MATCHED") でOK
                    conn.sendMatchResponse("MATCHED");
                }

                // リストから削除
                userList.remove(memberId);
                userConnections.remove(memberId);
            }
        } else {
            System.err.println("APサーバへの登録に失敗しました。");
            // エラー処理（全員に解散通知など）が必要ならここに記述
        }

        updateMatchingInfo();

        // まだ待機者が残っている場合、タイマーは止めない or 再起動の判断が必要
        if (userList.isEmpty()) {
            timerTask = null;
        }
    }

    private synchronized void processOperationTimeout() {
        matchingInfo = "TIMEOUT";
        System.out.println("[Matching] Timeout. users=" + userList);

        // 全員に解散通知
        for (String id : userList) {
            ClientCommunication conn = userConnections.get(id);
            if (conn != null) conn.sendMatchResponse("TIMEOUT_DISMISSED");
        }

        userList.clear();
        userConnections.clear();
        updateMatchingInfo();
        timerTask = null;
    }

    private void updateMatchingInfo() {
        matchingInfo = "WAITING(" + userList.size() + ")";
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}