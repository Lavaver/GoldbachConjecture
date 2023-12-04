import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final String PROGRESS_FILE = "GoldbachProgress.txt";
    private static AtomicInteger startEven = new AtomicInteger(4); // 使用原子变量以确保线程安全

    public static void main(String[] args) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);
        System.out.println("使用 " + availableProcessors + " 个处理器核心");

        // 读取进度
        readProgress();

        // 提交任务到线程池
        for (int i = 0; i < availableProcessors; i++) {
            executor.submit(() -> {
                int even;
                while (true) {
                    even = startEven.getAndAdd(2); // 原子地获取并增加

                    boolean found = false;
                    for (int j = 2; j <= even / 2; j++) {
                        if (isPrime(j) && isPrime(even - j)) {
                            System.out.printf("线程 %s: %d 可以表示为两个素数之和：%d + %d%n",
                                              Thread.currentThread().getName(), even, j, even - j);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        System.out.println("反例找到: " + even + " 不能表示为两个素数之和！");
                        System.exit(0); // 找到反例，关闭所有线程
                    }

                    // 每隔一段时间保存进度
                    if (even % 100 == 0) {
                        saveProgress(even);
                    }
                }
            });
        }

        executor.shutdown(); // 关闭线程池
    }

    private static void readProgress() {
        File progressFile = new File(PROGRESS_FILE);
        if (progressFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(progressFile))) {
                String lastChecked = reader.readLine();
                startEven.set(Integer.parseInt(lastChecked));
            } catch (IOException | NumberFormatException e) {
                System.out.println("读取进度失败，从 " + startEven + " 开始");
            }
        }
    }

    private static synchronized void saveProgress(int even) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROGRESS_FILE))) {
            writer.write(Integer.toString(even));
        } catch (IOException e) {
            System.out.println("保存进度失败，在偶数 " + even + " 处停止");
        }
    }

    private static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i * i <= number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}