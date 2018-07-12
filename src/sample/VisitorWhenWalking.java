package sample;

import javafx.application.Platform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.max;
import static java.nio.file.FileVisitResult.*;


public class VisitorWhenWalking extends SimpleFileVisitor<Path> {

    private String extension;
    private SecondViewController controller;
    private byte[] bytesToFind;
    private byte[] bytesToPut;

    private static final int maxSize = 2147483647;

    public VisitorWhenWalking(String extension, SecondViewController controller, byte[] bytesToFind, byte[] bytesToPut){
        this.extension = extension;
        this.controller = controller;
        this.bytesToFind = bytesToFind;
        this.bytesToPut = bytesToPut;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        Platform.runLater(() -> {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*."+extension);

            String fileName = String.format("%s",file.getFileName());
            String fullPath = file.toString();
            Integer size = (int)attr.size();

            if(attr.size() <= maxSize) {
                if (matcher.matches(file.getFileName())) {

                    byte[] bFile = readBytesFromFile(fullPath); //there is full path necessary
                    List<Integer> positionsOfPattern = findPattern(bytesToFind, bFile, (byte)-128,(byte)127);

                    if(positionsOfPattern.isEmpty()) {
                        controller.addToListView(fileName + " NOT CHANGED");
                        controller.addToTableView(fullPath,IsFileChanged.NO,size);
                    } else {
                        changeBytes(fullPath, positionsOfPattern, bytesToPut);
                        controller.addToListView(fileName + " CHANGED");
                        controller.addToTableView(fullPath,IsFileChanged.YES,size);
                    }
                    controller.setTextField2(fileName);
                } else {
                    controller.setTextField2(fileName);
                }
            } else {
                String s = String.format("%s -> size to big",fileName);
                controller.addToListView(s);
                controller.setTextField2(s);
                controller.addToTableView(fullPath,IsFileChanged.TOO_BIG,size);
            }
        });
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exc) {
        String s = "Error in path name. Please click Once Again button to try again.";
        controller.addToListView(s);
        controller.setTextField2("Error!!!");
        controller.showList();
        controller.hideLabel();
        controller.setFlag(1);
        return CONTINUE;
    }

    //tutaj zapodaj wszystkie algorytmy do szukania i zamieniania ciągów znaków
    private static byte[] readBytesFromFile(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int)file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }

     private List<Integer> findPattern(byte[] p, byte[] s, byte zP, byte zK) {
         int i;                 //położenie okna wzorca p w przeszukiwanym łańcuchu s
         int j;                 //położenie porównywanego znaku we wzorcu p
         int m;                 //długość wzorca p - wyznaczane w trakcie wyliczania tablic last i bmNext
         int n;                 //długość łańcucha s – wyznaczane w trakcie wyliczania tablic last i bmNext
         int[] last = null;     //tablica ostatnich wystąpień znaków alfabetu we wzorcu p. Indeksy od 0 do zK - zP
         int[] bmNext = null;   //tablica przesunięć okna wzorca dla pasujących sufiksów. Indeksy od 0 do m
         int[] pi = null;       //pomocnicza tablica m + 1 elementowa położeń prefikso-sufiksów sufiksów wzorca
         int pp;                //znalezione pozycje wzorca p w łańcuchu s
         int b;                 //polozenie prefikso-sufiksu aktualnego sufiksu
         List<Integer> positionsOfPattern = new ArrayList<>();

         last = new int[zK-zP+1];
         m = p.length;  //wylicamy dlugosc wzorca
         n = s.length;
         bmNext = new int[m+1];
         pi = new int[m+1];

         //dla wzorca obliczamy tablice last[]
         for(i = 0; i <= zK - zP; i++) last[i] = -1;
         for(i = 0; i < m; i++) last[p[i] - zP] = i;

         //etap I obliczania tablicy bmNext
         for(i = 0; i <= m; i++) bmNext[i] = 0;
         i = m;
         b = m + 1;
         pi[i] = b;
         while(i > 0) {
             while( (b <= m) && (p[i-1] != p[b-1])) {
                 if(bmNext[b] == 0)
                     bmNext[b] = b - i;
                 b = pi[b];
             }
             pi[--i] = --b;
         }

         //etap II obliczania tablicy bmNext[]
         b = pi[0];
         for(i = 0; i <= m; i++) {
             if(bmNext[i] == 0)
                 bmNext[i] = b;
             if(i == b)
                 b = pi[b];
         }

         //szukamy pozycji w lancuchu
         pp = 0;
         i = 0;
         while(i <= n-m) {
             j = m - 1;
             while((j > -1) && (p[j] == s[i+j]))
                 j--;
             if(j == -1) {
                 while(pp < i) pp++;
                 if(positionsOfPattern.isEmpty()) {
                     positionsOfPattern.add(i); //tu bylo pp
                 }  else {
                     if ((positionsOfPattern.get(positionsOfPattern.size() - 1) + p.length) <= pp) { //nie naklada sie
                         positionsOfPattern.add(i); //tu bylo pp
                     }
                 }
                 pp++;
                 i += bmNext[0];
             }
             else {
                 i += max(bmNext[j+1],j-last[s[i+j]-zP]);
             }
         }
         return positionsOfPattern;
     }

     private void changeBytes(String fileName, List<Integer> positions, byte[] newBytes) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(fileName, "rw");

            for (Integer i: positions) {
                raf.seek(i);
                raf.write(newBytes);
            }

        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
     }

}