package penjadwalan;

public class Data {
    int iMesin;
    int durasi;
    int startTime;
    int endTime;

    public Data(int iMesin, int durasi) {
        this.iMesin = iMesin;
        this.durasi = durasi;
    }
    
    public String toString(){
        return "iMac: "+iMesin+"; durasi: "+durasi+"; start: "+startTime+"; end: "+endTime;
    }
}
