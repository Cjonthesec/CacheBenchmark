import java.io.FileWriter;
import java.io.PrintWriter;

public class CacheBenchmark {
    static volatile long blackhole = 0;

    public static void main(String[] args) throws Exception {
        // Ð Ð°Ð·Ð¼ÐµÑ€Ñ‹ N Ð¿Ð¾Ð´Ð¾Ð±Ñ€Ð°Ð½Ñ‹ Ð¿Ð¾Ð´ L1d (48KB), L2 (1.25MB), L3 (24MB), RAM
        int[] sizes = {64, 96, 128, 256, 512, 1024, 2048, 4096, 8192};
        int warmupRuns = 3;
        int measuredRuns = 15;

        try (PrintWriter out = new PrintWriter(new FileWriter("results.csv"))) {
            out.println("N,SizeKB,Run,RowMajorNs,ColMajorNs");

            for (int n : sizes) {
                int totalElems = n * n;
                double sizeKB = (totalElems * 4.0) / 1024.0;
                int[] matrix = new int[totalElems];
                for (int i = 0; i < totalElems; i++) matrix[i] = i & 255;

                // Ð”Ð»Ñ Ð¼ÐµÐ»ÐºÐ¸Ñ… Ð¼Ð°Ñ‚Ñ€Ð¸Ñ† Ð¿Ð¾Ð²Ñ‚Ð¾Ñ€ÑÐµÐ¼ Ð¿Ñ€Ð¾Ñ…Ð¾Ð´, Ñ‡Ñ‚Ð¾Ð±Ñ‹ Ð¸ÑÐºÐ»ÑŽÑ‡Ð¸Ñ‚ÑŒ Ð¿Ð¾Ð³Ñ€ÐµÑˆÐ½Ð¾ÑÑ‚ÑŒ Ñ‚Ð°Ð¹Ð¼ÐµÑ€Ð°
                int reps = Math.max(1, (16 * 1024 * 1024) / totalElems);

                // Warm-up (Ð¿Ñ€Ð¾Ð³Ñ€ÐµÐ² JIT Ð¸ ÐºÑÑˆÐ°)
                for (int w = 0; w < warmupRuns; w++) {
                    blackhole += rowMajor(matrix, n, reps);
                    blackhole += colMajor(matrix, n, reps);
                }

                System.out.printf(java.util.Locale.US, "Testing N=%d (%.1f KB, reps=%d)...%n", n, sizeKB, reps);

                for (int run = 1; run <= measuredRuns; run++) {
                    long t1 = System.nanoTime();
                    blackhole += rowMajor(matrix, n, reps);
                    long t2 = System.nanoTime();
                    double rowNs = (double) (t2 - t1) / ((long) totalElems * reps);

                    long t3 = System.nanoTime();
                    blackhole += colMajor(matrix, n, reps);
                    long t4 = System.nanoTime();
                    double colNs = (double) (t4 - t3) / ((long) totalElems * reps);

                    out.printf(java.util.Locale.US, "%d,%.1f,%d,%.4f,%.4f%n", n, sizeKB, run, rowNs, colNs);
                }
            }
        }
        System.out.println("DONE! Saved to results.csv");
    }

    static long rowMajor(int[] m, int n, int reps) {
        long sum = 0;
        for (int rep = 0; rep < reps; rep++) {
            for (int r = 0; r < n; r++) {
                int rowOffset = r * n;
                for (int c = 0; c < n; c++) {
                    sum += m[rowOffset + c];
                }
            }
        }
        return sum;
    }

    static long colMajor(int[] m, int n, int reps) {
        long sum = 0;
        for (int rep = 0; rep < reps; rep++) {
            for (int c = 0; c < n; c++) {
                for (int r = 0; r < n; r++) {
                    sum += m[r * n + c];
                }
            }
        }
        return sum;
    }
}
