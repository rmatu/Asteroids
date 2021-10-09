package sample;

public class Rectangle {
    double x;
    double y;
    double width;
    double height;

    public Rectangle(){

    }

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setSize(double w, double h){
        this.width = w;
        this.height = h;
    }

    public boolean overlaps (Rectangle other){
        boolean noOverlap =
                this.x + this.width < other.x ||
                        other.x + other.width < this.x ||
                        this.y + this.height < other.y ||
                        other.y + other.height < this.y;

        return !noOverlap;
    }
}
