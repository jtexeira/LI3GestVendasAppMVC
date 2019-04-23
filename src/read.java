import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;
public class read {
    public static void main(String[] args) {
        Crono.start();
        readLinesWithBR("db/Vendas_3M.txt");
        out.println(Crono.print());
        Crono.start();
        readWithFiles("db/Vendas_3M.txt");
        out.println(Crono.print());

    }

    public static List<String> readLinesWithBR(String fichtxt) {
        List<String> linhas = new ArrayList<>();
        BufferedReader inFile = null;
        String linha = null;
        try {
            inFile = new BufferedReader(new FileReader(fichtxt));
            while((linha = inFile.readLine()) != null)
                linhas.add(linha);
        }
        catch(IOException e) {
            out.println(e);
        }
        return linhas;
    }

    public static List<String> readWithFiles(String fichtxt) {
        List<String> linhas = new ArrayList<>();
        try {
            linhas = Files.readAllLines(Paths.get(fichtxt), StandardCharsets.UTF_8);
        }
        catch(IOException e) {
            out.println(e);
        }
        return linhas;
    }
}
