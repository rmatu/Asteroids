package sample;

public class Vector {
    public double x;
    public double y;

    public Vector(){
        this.set(x,y);
    }

    public void set(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void add (double dx, double dy){
        this.x += dx;
        this.y += dy;
    }

    public void multiply (double m) {
        this.x *= m;
        this.y *= m;
    }

    public double getLength(){
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public void setLength (double L) {
        double currentLength = this.getLength();
        if (currentLength == 0){
            this.set(L, 0);
        } else {
            this.multiply(1 /currentLength);
            this.multiply(L);
        }

    }

    public void setAngle(double angleDegrees){
        double L = this.getLength();
        double angleRadians = Math.toRadians(angleDegrees);
        this.x = L * Math.cos(angleRadians);
        this.y = L * Math.sin(angleRadians);
    }
}
