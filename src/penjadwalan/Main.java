package penjadwalan;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        int nJob = 0;// banyaknya Job
        int nProc = 0;// banyaknya Proses
        int nMac = 0;// banyaknya Mesin

        Data[][] data = null;

        // BACA ISI FILE DATA SET
        //nama filenya: dataset_001.txt
        File f = new File("dataset/dataset_001.txt");
        try {
            FileInputStream fis = new FileInputStream(f);
            Scanner sc = new Scanner(fis, "UTF-8");

            //ada 4 baris yang tidak kita gunakan di baris pertama file dataset_001.txt
            sc.nextLine();//baca baris pertama tapi hasilnya tidak perlu disimpan
            sc.nextLine();//baca baris kedua tapi hasilnya tidak perlu disimpan
            sc.nextLine();//baca baris ketiga tapi hasilnya tidak perlu disimpan
            sc.nextLine();//baca baris keempat tapi hasilnya tidak perlu disimpan

            String baris = sc.nextLine();//baca baris kelima dan simpat hasil baca ke variabel "line" bertipe  String
            String[] segmen = baris.split("\\s+");//melakukan split terhadap string line menggunakan pemisah simbol "spasi"

            //s0 dan s1 bertipe String
            String s0 = segmen[0];
            String s1 = segmen[1];

            nJob = Integer.parseInt(s0);// banyaknya Job di dataset
            nProc = Integer.parseInt(s1);// banyaknya proses di dataset
            nMac = nProc;// banyaknya msein di dataset

            //di dataset_001 ini ada 10 Job dan ada 5 mesin, jumlah mesin dan proses sama = 5
            //coba cetak jumlah Job, proses dan esin
            System.out.println("------------------------------------------------");
            System.out.println("DATASET");
            System.out.println("Banyaknya Job   : " + nJob);
            System.out.println("Banyaknya Proses: " + nProc);
            System.out.println("Banyaknya Mesin : " + nMac);
            System.out.println("");

            //selanjutnya kita akan membaca dataset dan melakukan pemisahan/split kolom data
            //index kolom di java dimulai dari index-0
            //kolom genap akan disimpan sebagai data mesin
            //sedangkan kolom ganjil akan disimpan sebagai data waktu
            //baris dan kolom tabel beturut-turut menyatakan Job dan Proses
            //jadi jika kita menyatakan data pada baris ke-2 kolom ke-3 berarti data yang dimaksud adalah data untuk Job-2 dan Proses-3
            //kita siapkan tabelnya (tabel mesin dan tabel waktu)
            data = new Data[nJob][nProc];

            //baca data mesin dan waktu baris demi baris
            System.out.println("ISI DATASET");
            for (int i = 0; i < nJob; i++) {
                baris = sc.nextLine();//baca baris demi baris
                System.out.println(baris);//coba cetak barisnya
                //kita akan melakukan pemisaahan data mesin dan data waktu
                //untuk dimasukkan ke dalam tabel mesin dan tabel waktu

                // kita split dulu String baris ke dalam array segmen yang masih bertipe string juga
                segmen = baris.split("\\s+");//split string baris ke dalam array segmen. simbol \\s+ berarti bahwa simbol pemisah yang kita gunakan untuk melakukan split adalah simbol spasi
                for (int k = 0; k < nProc; k++) {
                    String sMac = segmen[2 * k];
                    String sDur = segmen[2 * k + 1];
                    int iMesin = Integer.parseInt(sMac);
                    int durasi = Integer.parseInt(sDur);
                    data[i][k] = new Data(iMesin, durasi);
                }

            }//sampai disini dataset telah didistribusikan ke tabel mesin dan tabel waktu

            System.out.println("");
            System.out.println("TABEL MESIN");
            for (int i = 0; i < nJob; i++) {
                for (int j = 0; j < nProc; j++) {
                    System.out.print(data[i][j].iMesin + " ");
                }
                System.out.println("");
            }

            System.out.println("");
            System.out.println("TABEL WAKTU untuk TIAP MESIN pada JOB-PROSES");
            for (int i = 0; i < nJob; i++) {
                for (int j = 0; j < nProc; j++) {
                    System.out.print(data[i][j].durasi + " ");
                }
                System.out.println("");
            }
            System.out.println("------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }

        //setelah pembacaan isi dataset usai
        //dan kita telah melakukan split dataset ke dalam tabel mesin dan tabel waktu
        //selanjutnya kita akan menggenerate solusi
        //GENERATE INDIVIDU-----------------------------------------------------
        if (nJob > 0 && nProc > 0 && data != null) {

            int[] individu = generateIndividu(data);

            //cetak individu
            System.out.print("INDIVIDU: " + Arrays.toString(individu));

            System.out.println("");
            System.out.println("VALIDASI");
            //VALIDASI INDIVIDU untuk menghitung nilai fitness
            int MAX_DURATION = hitungDurasiTotal(individu, data);

            //penelusuran gen untuk memvalidasi individu menggunakan gantt diagram
            System.out.println("");
            System.out.println("DURASI TOTAL: " + MAX_DURATION);

            //hitung fitness
            double epsilon = Double.MIN_VALUE;
            double fitness = 1.0 / (MAX_DURATION + epsilon);
            System.out.println("FITNESS: " + fitness);

        }//end of if (nJob > 0 && nProc > 0 && mesin!=null && waktu!=null)

    }//end of main()

    private static int[] generateIndividu(Data[][] data) {
        int[] individu = null;
        if (data != null) {
            int nJob = data.length;
            int nProc = data[0].length;
            int panjangKromosom = nJob * nProc;
            int[] maxProc = new int[nJob];
            individu = new int[panjangKromosom];
            int k = 0;
            while (k < panjangKromosom) {
                int value = new Random().nextInt(nJob);
                //validasi ada berapa proses yang telah dimiliki oleh setiap Job
                if (maxProc[value] < nProc) {
                    individu[k] = value;
                    maxProc[value]++;
                    k++;
                }
            }
        }
        return individu;
    }//end of generateIndividu(Data[][] data)

    private static int hitungDurasiTotal(int[] individu, Data[][] data) {
        int MAX_DURATION = 0;
        if (individu != null && data != null) {
            int nJob = data.length;
            int nProc = data[0].length;
            int nMac = nProc;
            int[] endTimeMac = new int[nMac];
            int[] endTimeJob = new int[nJob];
            int[] iProc = new int[nJob];//untuk menandai index proses yang sedang dikerjakan di setiap Job

            for (int i = 0; i < individu.length; i++) {
                int job = individu[i];
                int proc = iProc[job];
                int iMesin = data[job][proc].iMesin;//mesin
                int durasi = data[job][proc].durasi;//durasi mesin
                //masukkan mesin dan durasi ke gantt diagram
                int etMac = endTimeMac[iMesin];//end time untuk mesin
                int etJob = endTimeJob[job];//end time untuk job
                int start = Math.max(etMac, etJob);
                int finish = start + durasi;

                data[job][proc].startTime = start;
                data[job][proc].endTime = finish;

                endTimeMac[iMesin] = finish;
                endTimeJob[job] = finish;

                if (finish > MAX_DURATION) {
                    MAX_DURATION = finish;
                }
                iProc[job]++;//lakukan inkremen untuk index proses pada job yang baru dikerjakan

                //cetak progres gantt diagram
                System.out.println("Job: " + job + "; Proc: " + proc + "; " + data[job][proc]);
            }
        }
        return MAX_DURATION;
    }

}//end of class
