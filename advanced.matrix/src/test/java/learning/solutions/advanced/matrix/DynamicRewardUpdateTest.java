package learning.solutions.advanced.matrix;

import learning.solutions.advanced.matrix.domain.RCell;
import learning.solutions.advanced.matrix.utils.ReinforcementLearnerUtil;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by ktalabattula on 7/20/17.
 */
public class DynamicRewardUpdateTest {
    private static RCell rCell;
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        Point point = new Point(10, 15);
        rCell = new RCell(point, 1, 10);
        rCell.setReward(1);

        final Runnable addCustomerCalls = new Runnable() {
            public void run() {
                rCell.addCustomerCall();
            }
        };

        final Runnable addTrafficDelays = new Runnable() {
            public void run() {
                rCell.addTrafficDelay();
            }
        };

        final Runnable rewardTester = new Runnable() {
            public void run() {
                System.out.println("Current reward is : " + rCell.recalculateRewardValue());
            }
        };

        final ScheduledFuture<?> custCallHandle = scheduler.scheduleAtFixedRate(addCustomerCalls, 0, 3, TimeUnit.SECONDS);
        final ScheduledFuture<?> trafficDelayHandle = scheduler.scheduleAtFixedRate(addTrafficDelays, 2, 3, TimeUnit.SECONDS);
        final ScheduledFuture<?> testRewardHandle = scheduler.scheduleAtFixedRate(rewardTester, 0, 1, TimeUnit.SECONDS);

        scheduler.schedule(
                new Runnable() {
                    public void run() {
                        custCallHandle.cancel(true);
                    }
                }, 15, TimeUnit.SECONDS);

        scheduler.schedule(
                new Runnable() {
                    public void run() {
                        trafficDelayHandle.cancel(true);
                    }
                }, 17, TimeUnit.SECONDS);

        scheduler.schedule(
                new Runnable() {
                    public void run() {
                        testRewardHandle.cancel(true);
                    }
                }, 40, TimeUnit.SECONDS);
    }
}
