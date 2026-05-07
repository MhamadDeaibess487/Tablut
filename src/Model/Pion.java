package Model;
public class Pion {
    Point p;
    int t; //1 noir, 2 blanc, 3 roi , 4 trone

    public Pion(Point p, int type) {
        this.p = p;
        this.t = type;
    }
}