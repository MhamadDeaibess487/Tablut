package Model;

public class TableauBit implements Comparable<TableauBit>{
    public long noir1, noir2;
    public long blanc1, blanc2;
    public long roi1, roi2;
    
    public TableauBit(){}
    
    public TableauBit(long n1,long n2,long b1,long b2,long r1,long r2){
        noir1 = n1;
        noir2 = n2;
        blanc1 = b1;
        blanc2 = b2;
        roi1 = r1;
        roi2 = r2;
    }
    
    /*On veut juste savoir si deux plateaux sont egaux*/
    @Override
    public int compareTo(TableauBit o) {
        return (int) (noir1 - o.noir1 + noir2 - o.noir2 + 
                blanc1 - o.blanc1 + blanc2 - o.blanc2 
                + roi1 - o.roi1 + roi2 - o.roi2);
    }
}