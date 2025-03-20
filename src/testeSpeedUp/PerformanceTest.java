package testeSpeedUp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class PerformanceTest {
    private static final String FILE_PATH = "src/new_calibration_text.txt";
    private static final int THRESHOLD = 10_000;
    private static int NUM_THREADS;

    public static void main(String[] args) throws IOException {
        NUM_THREADS = 2;

        // Lê os números do arquivo
        List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
        long[] numbers = new long[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            numbers[i] = Calibracao.valorCalibracao(lines.get(i));
        }

        // 🔹 Execução com ForkJoinPool
        long start = System.nanoTime();
        ForkJoinPool pool = new ForkJoinPool(NUM_THREADS);
        long forkJoinSum = pool.invoke(new ForkJoinTask(numbers, 0, numbers.length));
        long end = System.nanoTime();
        pool.shutdown();
        System.out.println("Soma com ForkJoin: " + forkJoinSum + " | Tempo: " + (end - start) / 1_000_000 + " ms");
    }

    // 🔴 Soma utilizando ForkJoinPool
    static class ForkJoinTask extends RecursiveTask<Long> {
        private long[] numbers;
        private int start, end;

        public ForkJoinTask(long[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if ((end - start) <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += numbers[i];
                }
                return sum;
            } else {
                int mid = (start + end) / 2;
                ForkJoinTask leftTask = new ForkJoinTask(numbers, start, mid);
                ForkJoinTask rightTask = new ForkJoinTask(numbers, mid, end);

                leftTask.fork(); // Inicia o cálculo de forma assíncrona
                long rightResult = rightTask.fork().join(); // Calcula o lado direito
                long leftResult = leftTask.join(); // Espera pelo cálculo do lado esquerdo

                return leftResult + rightResult;
            }
        }
    }
}

// 🔹 Classe para extração do valor de calibração
class Calibracao {
    public static int valorCalibracao(String linha) {
        Character primeiroCaractere = null;
        Character ultimoCaractere = null;

        for (char c : linha.toCharArray()) {
            if (Character.isDigit(c)) {
                if (primeiroCaractere == null) {
                    primeiroCaractere = c;
                }
                ultimoCaractere = c;
            }
        }

        return (primeiroCaractere != null && ultimoCaractere != null)
                ? Integer.parseInt("" + primeiroCaractere + ultimoCaractere)
                : 0;
    }
}
