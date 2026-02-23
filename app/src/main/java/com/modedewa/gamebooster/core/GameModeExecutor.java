package com.modedewa.gamebooster.core;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.modedewa.gamebooster.model.OptimizationStep;
import com.modedewa.gamebooster.util.SimProtector;

import java.util.ArrayList;
import java.util.List;

/**
 * GameModeExecutor — Main orchestrator that runs all optimization modules
 * in sequence with progress reporting. This is the heart of the app.
 * 
 * Translates the full flow of modeGameON.sh and modeGameOFF.sh into Java.
 */
public class GameModeExecutor {

    private static final String TAG = "GameModeExecutor";

    public interface ProgressListener {
        void onStepStarted(int stepIndex, OptimizationStep step);
        void onStepCompleted(int stepIndex, OptimizationStep step);
        void onAllCompleted(boolean success, String message);
    }

    private final String targetGame;
    private ProgressListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public GameModeExecutor(String targetGamePackage) {
        this.targetGame = targetGamePackage;
    }

    public void setProgressListener(ProgressListener listener) {
        this.listener = listener;
    }

    // ═══════════════════════════════════════════════
    // ACTIVATE GAME MODE (modeGameON.sh equivalent)
    // ═══════════════════════════════════════════════

    public void activateGameMode() {
        new Thread(() -> {
            Log.d(TAG, "═══ ACTIVATING GAME MODE ═══");
            List<OptimizationStep> steps = buildActivateSteps();
            boolean allOk = true;

            for (int i = 0; i < steps.size(); i++) {
                OptimizationStep step = steps.get(i);
                step.status = OptimizationStep.Status.RUNNING;
                notifyStepStarted(i, step);

                try {
                    boolean result = executeActivateStep(i, step);
                    step.status = result ? OptimizationStep.Status.SUCCESS
                            : OptimizationStep.Status.FAILED;
                    if (!result) allOk = false;
                } catch (Exception e) {
                    step.status = OptimizationStep.Status.FAILED;
                    step.message = e.getMessage();
                    allOk = false;
                    Log.e(TAG, "Step failed: " + step.name, e);
                }

                notifyStepCompleted(i, step);

                // Small delay between steps for stability
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            }

            String msg = allOk ? "Mode Dewa aktif! 🔥" : "Beberapa optimasi gagal, tapi mode aktif.";
            notifyAllCompleted(allOk, msg);
            Log.d(TAG, "═══ GAME MODE ACTIVATED ═══");
        }).start();
    }

    // ═══════════════════════════════════════════════
    // DEACTIVATE GAME MODE (modeGameOFF.sh equivalent)
    // ═══════════════════════════════════════════════

    public void deactivateGameMode() {
        new Thread(() -> {
            Log.d(TAG, "═══ DEACTIVATING GAME MODE ═══");
            List<OptimizationStep> steps = buildDeactivateSteps();
            boolean allOk = true;

            for (int i = 0; i < steps.size(); i++) {
                OptimizationStep step = steps.get(i);
                step.status = OptimizationStep.Status.RUNNING;
                notifyStepStarted(i, step);

                try {
                    boolean result = executeDeactivateStep(i, step);
                    step.status = result ? OptimizationStep.Status.SUCCESS
                            : OptimizationStep.Status.FAILED;
                    if (!result) allOk = false;
                } catch (Exception e) {
                    step.status = OptimizationStep.Status.FAILED;
                    step.message = e.getMessage();
                    allOk = false;
                }

                notifyStepCompleted(i, step);
                try { Thread.sleep(150); } catch (InterruptedException ignored) {}
            }

            notifyAllCompleted(allOk, allOk
                    ? "Mode normal dipulihkan ✓" : "Beberapa restore gagal.");
            Log.d(TAG, "═══ GAME MODE DEACTIVATED ═══");
        }).start();
    }

    // ═══════════════════════════════════════════════
    // Build Steps Lists
    // ═══════════════════════════════════════════════

    private List<OptimizationStep> buildActivateSteps() {
        List<OptimizationStep> steps = new ArrayList<>();
        steps.add(new OptimizationStep("🎭", "Matikan Animasi", "Nonaktifkan animasi sistem"));
        steps.add(new OptimizationStep("⚡", "Mode Performa", "Aktifkan developer performance settings"));
        steps.add(new OptimizationStep("📱", "Refresh Rate", "Set refresh rate optimal"));
        steps.add(new OptimizationStep("🔪", "Kill Background", "Matikan app background"));
        steps.add(new OptimizationStep("🧹", "Bersihkan RAM", "Trim cache & memory"));
        steps.add(new OptimizationStep("🎮", "Game Mode API", "Set performa game via API"));
        steps.add(new OptimizationStep("🔓", "FPS Unlock", "Hapus batasan FPS"));
        steps.add(new OptimizationStep("🌐", "Optimasi Jaringan", "Optimasi WiFi & DNS"));
        steps.add(new OptimizationStep("🖥️", "Optimasi Layar", "Brightness & timeout"));
        steps.add(new OptimizationStep("🔕", "Block Notifikasi", "Aktifkan DND & block notif"));
        steps.add(new OptimizationStep("🔇", "Optimasi Audio", "Matikan suara sistem"));
        steps.add(new OptimizationStep("🔄", "Matikan Sync", "Nonaktifkan auto-sync"));
        steps.add(new OptimizationStep("⛔", "Disable Fitur", "Matikan fitur tidak perlu"));
        steps.add(new OptimizationStep("📦", "Disable Apps", "Nonaktifkan app non-gaming"));
        steps.add(new OptimizationStep("📶", "Cek SIM Card", "Safety check SIM & sinyal"));
        return steps;
    }

    private List<OptimizationStep> buildDeactivateSteps() {
        List<OptimizationStep> steps = new ArrayList<>();
        steps.add(new OptimizationStep("🎭", "Pulihkan Animasi", "Aktifkan kembali animasi"));
        steps.add(new OptimizationStep("⚡", "Mode Normal", "Kembalikan performance settings"));
        steps.add(new OptimizationStep("📱", "Refresh Rate", "Reset refresh rate default"));
        steps.add(new OptimizationStep("🎮", "Game Mode Reset", "Kembalikan game mode standard"));
        steps.add(new OptimizationStep("🌐", "Jaringan Normal", "Reset network settings"));
        steps.add(new OptimizationStep("🖥️", "Layar Normal", "Reset display settings"));
        steps.add(new OptimizationStep("🔔", "Pulihkan Notifikasi", "Nonaktifkan DND"));
        steps.add(new OptimizationStep("🔊", "Pulihkan Audio", "Aktifkan suara sistem"));
        steps.add(new OptimizationStep("🔄", "Pulihkan Sync", "Aktifkan auto-sync"));
        steps.add(new OptimizationStep("✅", "Pulihkan Fitur", "Aktifkan fitur kembali"));
        steps.add(new OptimizationStep("📦", "Enable Apps", "Aktifkan kembali semua app"));
        steps.add(new OptimizationStep("📶", "Cek SIM Card", "Verifikasi SIM aman"));
        return steps;
    }

    // ═══════════════════════════════════════════════
    // Execute Individual Steps
    // ═══════════════════════════════════════════════

    private boolean executeActivateStep(int index, OptimizationStep step) {
        switch (index) {
            case 0:  return SettingsTweaker.disableAnimations();
            case 1:  return SettingsTweaker.enablePerformanceMode();
            case 2:  return SettingsTweaker.setRefreshRate(60.0f);
            case 3:
                int killed = AppKiller.killHeavyApps();
                AppKiller.killAllBackground(targetGame);
                step.message = killed + " app dihentikan";
                return true;
            case 4:
                AppKiller.trimCaches();
                AppKiller.trimMemory();
                step.message = "Cache & memory dibersihkan";
                return true;
            case 5:  return GameModeController.setPerformanceMode(targetGame);
            case 6:  return GameModeController.unlockFPS(targetGame);
            case 7:  return NetworkOptimizer.optimizeForGaming();
            case 8:  return DisplayController.optimizeForGaming();
            case 9:
                NotificationController.enableDND();
                int blocked = NotificationController.blockNotifications();
                step.message = blocked + " notifikasi diblockir";
                return true;
            case 10: return AudioController.optimizeForGaming();
            case 11: return SyncController.disableSync();
            case 12: return FeatureController.disableUnnecessary();
            case 13:
                int disabled = AppKiller.disableNonEssentialApps();
                step.message = disabled + " app dinonaktifkan";
                return true;
            case 14:
                SimProtector.HealthReport report = SimProtector.checkHealth();
                step.message = report.message;
                return report.simStatus == SimProtector.SimStatus.READY
                        || report.simStatus == SimProtector.SimStatus.UNKNOWN;
            default: return false;
        }
    }

    private boolean executeDeactivateStep(int index, OptimizationStep step) {
        switch (index) {
            case 0:  return SettingsTweaker.restoreAnimations();
            case 1:  return SettingsTweaker.restorePerformanceMode();
            case 2:  return SettingsTweaker.restoreRefreshRate();
            case 3:  return GameModeController.restoreStandardMode(targetGame);
            case 4:  return NetworkOptimizer.restoreDefaults();
            case 5:  return DisplayController.restoreDefaults();
            case 6:
                NotificationController.disableDND();
                NotificationController.restoreNotifications();
                return true;
            case 7:  return AudioController.restoreDefaults();
            case 8:  return SyncController.restoreSync();
            case 9:  return FeatureController.restoreFeatures();
            case 10:
                int enabled = AppKiller.reEnableApps();
                step.message = enabled + " app diaktifkan kembali";
                return true;
            case 11:
                SimProtector.HealthReport report = SimProtector.checkHealth();
                step.message = report.message;
                return true;
            default: return false;
        }
    }

    // ═══════════════════════════════════════════════
    // Progress Notification Helpers
    // ═══════════════════════════════════════════════

    private void notifyStepStarted(int index, OptimizationStep step) {
        if (listener != null) {
            mainHandler.post(() -> listener.onStepStarted(index, step));
        }
    }

    private void notifyStepCompleted(int index, OptimizationStep step) {
        if (listener != null) {
            mainHandler.post(() -> listener.onStepCompleted(index, step));
        }
    }

    private void notifyAllCompleted(boolean success, String message) {
        if (listener != null) {
            mainHandler.post(() -> listener.onAllCompleted(success, message));
        }
    }
}
