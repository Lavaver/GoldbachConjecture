import java.io.*;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        int startEven = 4; // 初始偶数

        // 尝试从文件中读取上次的进度
        File progressFile = new File("GoldbachProgress.txt");
        if (progressFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(progressFile))) {
                String lastChecked = reader.readLine();
                startEven = Integer.parseInt(lastChecked);
            } catch (IOException | NumberFormatException e) {
                System.out.println("读取进度失败，从 " + startEven + " 开始");
            }
        }

        // 迭代每个偶数，检查它是否可以表示为两个素数之和
        for (int even = startEven; ; even += 2) {
            boolean found = false;
            for (int i = 2; i <= even / 2; i++) {
                if (isPrime(i) && isPrime(even - i)) {
                    System.out.printf("%d 可以表示为两个素数之和：%d + %d%n", even, i, even - i);
                    found = true;
                    break; // 找到一对素数就足够了，不需要找出所有可能的组合
                }
            }

            if (!found) {
                System.out.println("反例找到: " + even + " 不能表示为两个素数之和！");
                break;
            }

            // 保存当前进度到文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(progressFile))) {
                writer.write(Integer.toString(even));
            } catch (IOException e) {
                System.out.println("保存进度失败，在偶数 " + even + " 处停止");
                break;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("运行时间: " + (endTime - startTime) + " 毫秒");
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