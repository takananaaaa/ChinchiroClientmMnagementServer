package Control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class MatchingSystem {

    // マッチング状態情報
    private String matchingInfo;

    // マッチング待機ユーザIDリスト
    private List<String> userList;

    // 制限時間（秒）← 1分
    private final int TIME_LIMIT_SECONDS = 60;

    // ゲーム開始に必要な最小人数
    private final int MIN_USERS_TO_START = 2;

    // タイマー制御
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> timerTask;

    public MatchingSystem() {
        this.userList = Collections.synchronizedList(new ArrayList<>());
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.matchingInfo = "WAITING";
    }

    // ユーザ参加
    public synchronized void addUser(String id) {
        if (id == null || id.isEmpty()) return;

        if (!userList.contains(id)) {
            userList.add(id);
        }

        updateMatchingInfo();

        // 最初の1人が参加したらタイマー開始
        if (userList.size() == 1) {
            startTimer();
        }
    }

    // ユーザ離脱
    public synchronized void removeUser(String id) {
        userList.remove(id);
        updateMatchingInfo();
    }

    // タイマー開始（1分後に判定）
    private synchronized void startTimer() {
        if (timerTask != null && !timerTask.isDone()) {
            return; // すでに計測中
        }

        matchingInfo = "COUNTING";
        System.out.println("[Matching] Timer started (60s)");

        timerTask = scheduler.schedule(
                this::judgeMatchingResult,
                TIME_LIMIT_SECONDS,
                TimeUnit.SECONDS
        );
    }

    // 1分経過後の判定
    private synchronized void judgeMatchingResult() {
        if (userList.size() >= MIN_USERS_TO_START) {
            startGame();
        } else {
            processOperationTimeout();
        }
    }

    // ゲーム開始
    private synchronized void startGame() {
        matchingInfo = "MATCHED";
        System.out.println("[Matching] Game started. users=" + userList);

        // 本来は ApplicationServerCommunication に通知
        // appComm.notifyMatchingStart(userList);

        userList.clear();
        updateMatchingInfo();
        timerTask = null;
    }

    // タイムアウト処理
    private synchronized void processOperationTimeout() {
        matchingInfo = "TIMEOUT";
        System.out.println("[Matching] Timeout. users=" + userList);

        userList.clear();
        updateMatchingInfo();
        timerTask = null;
    }

    // 状態更新
    private void updateMatchingInfo() {
        matchingInfo = "WAITING(" + userList.size() + ")";
    }

    public String getMatchingInfo() {
        return matchingInfo;
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}
