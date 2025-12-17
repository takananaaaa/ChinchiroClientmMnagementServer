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

    // タイムアウト時間（秒）
    private int timer;

    // マッチング成立に必要な人数
    private final int REQUIRED_USERS = 4;

    // タイマー制御用
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> timeoutTask;

    // コンストラクタ
    public MatchingSystem() {
        this.userList = Collections.synchronizedList(new ArrayList<>());
        this.timer = 30; // 30秒タイムアウト
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.matchingInfo = "WAITING";
    }

    // タイマー開始（最初のユーザ参加時に呼ばれる）
    public synchronized void startTimer() {
        // すでにタイマーが動いている場合は何もしない
        if (timeoutTask != null && !timeoutTask.isDone()) {
            return;
        }

        timeoutTask = scheduler.schedule(
                this::processOperationTimeout,
                timer,
                TimeUnit.SECONDS
        );
    }

    // ユーザをマッチング待機リストに追加
    public synchronized void addUser(String id) {
        if (id == null || id.isEmpty()) return;

        if (!userList.contains(id)) {
            userList.add(id);
        }

        updateMatchingInfo();

        // 規定人数が揃ったらゲーム開始
        if (userList.size() >= REQUIRED_USERS) {
            startGame();
        }
    }

    // ユーザを待機リストから削除
    public synchronized void removeUser(String id) {
        userList.remove(id);
        updateMatchingInfo();
    }

    // マッチング成立時の処理
    public synchronized void startGame() {
        cancelTimer();
        matchingInfo = "MATCHED";

        // 本来は ApplicationServerCommunication に通知
        System.out.println("[Matching] Matched users = " + userList);

        // マッチング成立後は待機リストをクリア
        userList.clear();
        updateMatchingInfo();
    }

    // タイムアウト時の処理
    public synchronized void processOperationTimeout() {
        matchingInfo = "TIMEOUT";

        System.out.println("[Matching] Timeout users = " + userList);

        // タイムアウト時は待機リストをクリア
        userList.clear();
        updateMatchingInfo();
    }

    // タイマーを停止
    private void cancelTimer() {
        if (timeoutTask != null && !timeoutTask.isDone()) {
            timeoutTask.cancel(false);
        }
    }

    // マッチング状態文字列更新
    private void updateMatchingInfo() {
        matchingInfo = "WAITING(" + userList.size() + "/" + REQUIRED_USERS + ")";
    }

    // マッチング状態取得
    public String getMatchingInfo() {
        return matchingInfo;
    }

    // サーバ終了時の後始末
    public void shutdown() {
        scheduler.shutdownNow();
    }

}
