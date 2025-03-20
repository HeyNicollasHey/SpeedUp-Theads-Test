package testeSpeedUp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class SerialCalibration {
    public static void main(String[] args) throws IOException {

        long startTime = System.nanoTime();
        long tempoInicial = System.currentTimeMillis();

        Path path = Paths.get(System.getProperty("user.dir"), "src", "new_calibration_text.txt");
        List<String> calibrations = Files.readAllLines(path);

        int somaTotal = 0;
        int countTotal = 0;

        for (String linha : calibrations) {
            somaTotal += Calibracao.valorCalibracao(linha);
            countTotal++;
        }

        System.out.println("A soma total dos valores é: " + somaTotal);
        System.out.println("Total de linhas processadas: " + countTotal);

        long tempoFinal = System.currentTimeMillis();
        long endTime = System.nanoTime();

        System.out.println("Tempo total: " + (tempoFinal - tempoInicial) + " ms");
        System.out.println("Tempo de execução: " + (endTime - startTime) / 1_000_000 + " ms");
    }
}

class Calibracao2 {
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
