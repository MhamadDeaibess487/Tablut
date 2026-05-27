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
        if (noir1 != o.noir1) {
            return Long.compare(noir1, o.noir1);
        }
        if (noir2 != o.noir2) {
            return Long.compare(noir2, o.noir2);
        }
        if (blanc1 != o.blanc1) {
            return Long.compare(blanc1, o.blanc1);
        }
        if (blanc2 != o.blanc2) {
            return Long.compare(blanc2, o.blanc2);
        }
        if (roi1 != o.roi1) {
            return Long.compare(roi1, o.roi1);
        }
        return Long.compare(roi2, o.roi2);
    }
}
